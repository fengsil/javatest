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
import com.example.test1.entity.PrescriptionItem;
import com.example.test1.mapper.PrescriptionItemMapper;
import com.example.test1.mapper.PrescriptionMapper;
import com.example.test1.service.IPrescriptionService;
import com.example.test1.service.IRegistrationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 电子处方业务实现：多条件分页、挂号单存在性校验、一挂号一处方约束、引用检查后删除。
 */
@Service
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription>
        implements IPrescriptionService {

    private final IRegistrationService registrationService;
    private final PrescriptionItemMapper prescriptionItemMapper;

    public PrescriptionServiceImpl(IRegistrationService registrationService,
                                   PrescriptionItemMapper prescriptionItemMapper) {
        this.registrationService = registrationService;
        this.prescriptionItemMapper = prescriptionItemMapper;
    }

    @Override
    public Prescription getRequired(Long prescriptionId) {
        Prescription entity = getById(prescriptionId);
        if (entity == null) {
            throw new BizException("处方不存在：" + prescriptionId);
        }
        return entity;
    }

    @Override
    public PageResult<Prescription> pageByCondition(long page, long size, String prescriptionNo,
                                                    Long registrationId, String diagnosis) {
        prescriptionNo = SqlLikeUtil.escape(prescriptionNo);
        diagnosis = SqlLikeUtil.escape(diagnosis);
        Page<Prescription> pageParam = new Page<>(page, size);
        IPage<Prescription> result = baseMapper.selectPageByCondition(
                pageParam, prescriptionNo, registrationId, diagnosis);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public Prescription create(Prescription body) {
        normalize(body);
        validate(body);
        checkUniqueRegistration(body.getRegistrationId(), null);
        body.setPrescriptionId(null);
        if (body.getPrescriptionNo() == null) {
            body.setPrescriptionNo(generatePrescriptionNo());
        }
        save(body);
        return getById(body.getPrescriptionId());
    }

    @Override
    public Prescription modify(Long prescriptionId, Prescription body) {
        getRequired(prescriptionId);
        normalize(body);
        validate(body);
        checkUniqueRegistration(body.getRegistrationId(), prescriptionId);
        // advice 允许为空，用 UpdateWrapper 显式 set 才能清空
        update(new UpdateWrapper<Prescription>()
                .eq("prescription_id", prescriptionId)
                .set("registration_id", body.getRegistrationId())
                .set("diagnosis", body.getDiagnosis())
                .set("advice", body.getAdvice()));
        return getById(prescriptionId);
    }

    @Override
    public void delete(Long prescriptionId) {
        getRequired(prescriptionId);
        Long itemCount = prescriptionItemMapper.selectCount(
                new LambdaQueryWrapper<PrescriptionItem>()
                        .eq(PrescriptionItem::getPrescriptionId, prescriptionId));
        if (itemCount != null && itemCount > 0) {
            throw new BizException("该处方下有 " + itemCount + " 条药品明细，请先删除明细");
        }
        removeById(prescriptionId);
    }

    private void normalize(Prescription body) {
        body.setPrescriptionNo(trimToNull(body.getPrescriptionNo()));
        body.setDiagnosis(trimToNull(body.getDiagnosis()));
        body.setAdvice(trimToNull(body.getAdvice()));
    }

    private void validate(Prescription body) {
        FieldValidator.notBlank(body.getDiagnosis(), "临床诊断");
        FieldValidator.maxLength(body.getDiagnosis(), 255, "临床诊断");
        FieldValidator.maxLength(body.getAdvice(), 500, "医嘱");
        FieldValidator.maxLength(body.getPrescriptionNo(), 32, "处方编号");
        if (body.getRegistrationId() == null) {
            throw new BizException("挂号记录不能为空");
        }
        // 挂号单必须存在，否则会触发数据库外键约束错误
        registrationService.getRequired(body.getRegistrationId());
    }

    /** 一次挂号最多开一张处方，与数据库 prescription.registration_id 唯一约束一致。 */
    private void checkUniqueRegistration(Long registrationId, Long selfId) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<Prescription>()
                .eq(Prescription::getRegistrationId, registrationId)
                .ne(selfId != null, Prescription::getPrescriptionId, selfId));
        if (count != null && count > 0) {
            throw new BizException("该挂号记录已开具处方，不可重复开方");
        }
    }

    /** 处方编号规则：P + 日期 + 随机序号，受数据库唯一约束兜底。 */
    private String generatePrescriptionNo() {
        return "P" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
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
