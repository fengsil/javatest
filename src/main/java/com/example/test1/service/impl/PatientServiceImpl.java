package com.example.test1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Patient;
import com.example.test1.entity.Registration;
import com.example.test1.mapper.PatientMapper;
import com.example.test1.mapper.RegistrationMapper;
import com.example.test1.service.IPatientService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements IPatientService {

    private final RegistrationMapper registrationMapper;

    public PatientServiceImpl(RegistrationMapper registrationMapper) {
        this.registrationMapper = registrationMapper;
    }

    @Override
    public PageResult<Patient> pageByCondition(long page, long size, String name, String phone,
                                               String idCard, String gender) {
        long total = baseMapper.countByCondition(name, phone, idCard, gender);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0, page, size);
        }
        long start = (page - 1) * size;
        if (start >= total) {
            return new PageResult<>(Collections.emptyList(), total, page, size);
        }
        List<Patient> records = baseMapper.selectByCondition(name, phone, idCard, gender, start, size);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Patient create(Patient body) {
        validate(body);
        checkUnique(body.getPhone(), body.getIdCard(), null);
        body.setPatientId(null);
        if (body.getPatientNo() == null || body.getPatientNo().trim().isEmpty()) {
            body.setPatientNo(generatePatientNo());
        }
        save(body);
        return getById(body.getPatientId());
    }

    @Override
    public Patient modify(Long patientId, Patient body) {
        if (getById(patientId) == null) {
            throw new BizException("患者不存在：" + patientId);
        }
        validate(body);
        checkUnique(body.getPhone(), body.getIdCard(), patientId);
        body.setPatientId(patientId);
        updateById(body);
        return getById(patientId);
    }

    @Override
    public void delete(Long patientId) {
        if (getById(patientId) == null) {
            throw new BizException("患者不存在：" + patientId);
        }
        Long regCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getPatientId, patientId));
        if (regCount != null && regCount > 0) {
            throw new BizException("该患者存在 " + regCount + " 条挂号记录，无法删除");
        }
        removeById(patientId);
    }

    private void validate(Patient body) {
        FieldValidator.notBlank(body.getName(), "患者姓名");
        FieldValidator.gender(body.getGender());
        FieldValidator.phone(body.getPhone(), "患者手机号");
        FieldValidator.idCard(body.getIdCard());
        FieldValidator.maxLength(body.getAddress(), 200, "联系地址");
    }

    private void checkUnique(String phone, String idCard, Long selfId) {
        if (countOther(baseMapper.selectCount(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getPhone, phone)
                .ne(selfId != null, Patient::getPatientId, selfId))) > 0) {
            throw new BizException("患者手机号已存在：" + phone);
        }
        if (countOther(baseMapper.selectCount(new LambdaQueryWrapper<Patient>()
                .eq(Patient::getIdCard, idCard)
                .ne(selfId != null, Patient::getPatientId, selfId))) > 0) {
            throw new BizException("患者身份证号已存在：" + idCard);
        }
    }

    /** 就诊卡号规则：P + 年份 + 8 位随机数，生成后仍受数据库唯一约束兜底。 */
    private String generatePatientNo() {
        return "P" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
                + String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
    }

    private long countOther(Long count) {
        return count == null ? 0L : count;
    }
}
