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

/**
 * 患者业务实现：多条件分页、唯一性预检、字段规范化、引用检查后删除。
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements IPatientService {

    private final RegistrationMapper registrationMapper;

    public PatientServiceImpl(RegistrationMapper registrationMapper) {
        this.registrationMapper = registrationMapper;
    }

    @Override
    public Patient getRequired(Long patientId) {
        Patient entity = getById(patientId);
        if (entity == null) {
            throw new BizException("患者不存在：" + patientId);
        }
        return entity;
    }

    @Override
    public PageResult<Patient> pageByCondition(long page, long size, String name, String phone,
                                               String idCard, String gender) {
        // 姓名是模糊查询，转义 LIKE 通配符；手机号/身份证是精确匹配，保持原值
        name = SqlLikeUtil.escape(name);
        // 使用 MyBatis-Plus 分页插件：插件自动生成 COUNT 语句与 LIMIT 语句
        Page<Patient> pageParam = new Page<>(page, size);
        IPage<Patient> result = baseMapper.selectPageByCondition(pageParam, name, phone, idCard, gender);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    @Override
    public Patient create(Patient body) {
        normalize(body);
        validate(body);
        checkUnique(body.getPhone(), body.getIdCard(), null);
        body.setPatientId(null);
        if (body.getPatientNo() == null) {
            body.setPatientNo(generatePatientNo());
        }
        save(body);
        return getById(body.getPatientId());
    }

    @Override
    public Patient modify(Long patientId, Patient body) {
        getRequired(patientId);
        normalize(body);
        validate(body);
        checkUnique(body.getPhone(), body.getIdCard(), patientId);
        body.setPatientId(patientId);
        // updateById 默认忽略 null 字段，会导致“把地址清空”这类操作无效；
        // 这里用 UpdateWrapper 显式 set，保证可空字段能被真正清空，且不覆盖 created_at。
        update(new UpdateWrapper<Patient>()
                .eq("patient_id", patientId)
                .set("name", body.getName())
                .set("gender", body.getGender())
                .set("phone", body.getPhone())
                .set("id_card", body.getIdCard().toUpperCase())
                .set("address", body.getAddress()));
        return getById(patientId);
    }

    @Override
    public void delete(Long patientId) {
        getRequired(patientId);
        Long regCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getPatientId, patientId));
        if (regCount != null && regCount > 0) {
            throw new BizException("该患者存在 " + regCount + " 条挂号记录，无法删除");
        }
        removeById(patientId);
    }

    /**
     * 字段规范化：去首尾空格、身份证校验位字母统一大写、性别大写。
     */
    private void normalize(Patient body) {
        body.setName(trimToNull(body.getName()));
        body.setGender(trimToNull(body.getGender()));
        if (body.getGender() != null) {
            body.setGender(body.getGender().toUpperCase());
        }
        body.setPhone(trimToNull(body.getPhone()));
        body.setPatientNo(trimToNull(body.getPatientNo()));
        body.setAddress(trimToNull(body.getAddress()));
        if (body.getIdCard() != null) {
            body.setIdCard(body.getIdCard().trim().toUpperCase());
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validate(Patient body) {
        FieldValidator.notBlank(body.getName(), "患者姓名");
        FieldValidator.gender(body.getGender());
        FieldValidator.phone(body.getPhone(), "患者手机号");
        FieldValidator.idCard(body.getIdCard());
        FieldValidator.maxLength(body.getName(), 32, "患者姓名");
        FieldValidator.maxLength(body.getPatientNo(), 24, "就诊卡号");
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
