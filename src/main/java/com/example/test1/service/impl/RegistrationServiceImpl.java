package com.example.test1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.SqlLikeUtil;
import com.example.test1.entity.Prescription;
import com.example.test1.entity.Registration;
import com.example.test1.mapper.PrescriptionMapper;
import com.example.test1.mapper.RegistrationMapper;
import com.example.test1.service.IDoctorService;
import com.example.test1.service.IPatientService;
import com.example.test1.service.IRegistrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 挂号记录业务实现：多条件分页、外键存在性校验、(医生,日期,时段) 唯一性预检、引用检查后删除。
 */
@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration>
        implements IRegistrationService {

    /** 挂号状态枚举，与数据库 CHECK 约束保持一致 */
    private static final Set<String> STATUSES = Set.of("BOOKED", "COMPLETED", "CANCELLED");
    /** 时段枚举 */
    private static final Set<String> TIME_SLOTS = Set.of("AM", "PM");

    private final IPatientService patientService;
    private final IDoctorService doctorService;
    private final PrescriptionMapper prescriptionMapper;

    public RegistrationServiceImpl(IPatientService patientService,
                                   IDoctorService doctorService,
                                   PrescriptionMapper prescriptionMapper) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.prescriptionMapper = prescriptionMapper;
    }

    @Override
    public Registration getRequired(Long registrationId) {
        Registration entity = getById(registrationId);
        if (entity == null) {
            throw new BizException("挂号记录不存在：" + registrationId);
        }
        return entity;
    }

    @Override
    public PageResult<Registration> pageByCondition(long page, long size, String registrationNo, Long patientId,
                                                    Long doctorId, String visitDate, String timeSlot, String status) {
        registrationNo = SqlLikeUtil.escape(registrationNo);
        Page<Registration> pageParam = new Page<>(page, size);
        IPage<Registration> result = baseMapper.selectPageByCondition(
                pageParam, registrationNo, patientId, doctorId, visitDate, timeSlot, status);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public Registration create(Registration body) {
        normalize(body);
        validate(body);
        checkUniqueSlot(body.getDoctorId(), body.getVisitDate(), body.getTimeSlot(), null);
        body.setRegistrationId(null);
        if (body.getRegistrationNo() == null) {
            body.setRegistrationNo(generateRegistrationNo());
        }
        if (body.getStatus() == null) {
            body.setStatus("BOOKED");
        }
        save(body);
        return getById(body.getRegistrationId());
    }

    @Override
    public Registration modify(Long registrationId, Registration body) {
        getRequired(registrationId);
        normalize(body);
        validate(body);
        checkUniqueSlot(body.getDoctorId(), body.getVisitDate(), body.getTimeSlot(), registrationId);
        // symptom 允许为空，用 UpdateWrapper 显式 set 才能清空
        update(new UpdateWrapper<Registration>()
                .eq("registration_id", registrationId)
                .set("patient_id", body.getPatientId())
                .set("doctor_id", body.getDoctorId())
                .set("visit_date", body.getVisitDate())
                .set("time_slot", body.getTimeSlot())
                .set("status", body.getStatus() == null ? "BOOKED" : body.getStatus())
                .set("symptom", body.getSymptom()));
        return getById(registrationId);
    }

    @Override
    public void delete(Long registrationId) {
        getRequired(registrationId);
        Long presCount = prescriptionMapper.selectCount(
                new LambdaQueryWrapper<Prescription>().eq(Prescription::getRegistrationId, registrationId));
        if (presCount != null && presCount > 0) {
            throw new BizException("该挂号记录已开具 " + presCount + " 张处方，无法删除");
        }
        removeById(registrationId);
    }

    /** 字段规范化：去空格，状态与时段转大写，空串转 null。 */
    private void normalize(Registration body) {
        body.setRegistrationNo(trimToNull(body.getRegistrationNo()));
        body.setSymptom(trimToNull(body.getSymptom()));
        if (body.getTimeSlot() != null) {
            body.setTimeSlot(body.getTimeSlot().trim().toUpperCase());
        }
        if (body.getStatus() != null) {
            body.setStatus(body.getStatus().trim().toUpperCase());
        }
    }

    private void validate(Registration body) {
        if (body.getPatientId() == null) {
            throw new BizException("患者不能为空");
        }
        if (patientService.getById(body.getPatientId()) == null) {
            throw new BizException("患者不存在：" + body.getPatientId());
        }
        if (body.getDoctorId() == null) {
            throw new BizException("接诊医生不能为空");
        }
        if (doctorService.getById(body.getDoctorId()) == null) {
            throw new BizException("医生不存在：" + body.getDoctorId());
        }
        if (body.getVisitDate() == null) {
            throw new BizException("就诊日期不能为空");
        }
        if (body.getTimeSlot() == null || !TIME_SLOTS.contains(body.getTimeSlot())) {
            throw new BizException("时段只能为 AM 或 PM");
        }
        if (body.getStatus() != null && !STATUSES.contains(body.getStatus())) {
            throw new BizException("挂号状态只能为 BOOKED、COMPLETED 或 CANCELLED");
        }
        FieldValidator.maxLength(body.getSymptom(), 255, "主诉");
        FieldValidator.maxLength(body.getRegistrationNo(), 32, "挂号单号");
    }

    /** 同一医生同一日期同一时段只能有一条挂号记录，与数据库唯一索引一致。 */
    private void checkUniqueSlot(Long doctorId, LocalDate visitDate, String timeSlot, Long selfId) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<Registration>()
                .eq(Registration::getDoctorId, doctorId)
                .eq(Registration::getVisitDate, visitDate)
                .eq(Registration::getTimeSlot, timeSlot)
                .ne(selfId != null, Registration::getRegistrationId, selfId));
        if (count != null && count > 0) {
            throw new BizException("该医生在 " + visitDate + " " + timeSlot + " 时段已有挂号记录，不可重复放号");
        }
    }

    /** 挂号单号规则：R + 日期 + 随机序号，生成后仍受数据库唯一约束兜底。 */
    private String generateRegistrationNo() {
        return "R" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10_000));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
