package com.example.test1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.SqlLikeUtil;
import com.example.test1.entity.Doctor;
import com.example.test1.entity.Registration;
import com.example.test1.mapper.DoctorMapper;
import com.example.test1.mapper.RegistrationMapper;
import com.example.test1.service.IDepartmentService;
import com.example.test1.service.IDoctorService;
import com.example.test1.service.IJobTitleService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 医生业务实现：多条件分页、外键存在性与唯一性预检、字段规范化、引用检查后删除。
 */
@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements IDoctorService {

    private final IDepartmentService departmentService;
    private final IJobTitleService jobTitleService;
    private final RegistrationMapper registrationMapper;

    public DoctorServiceImpl(IDepartmentService departmentService,
                             IJobTitleService jobTitleService,
                             RegistrationMapper registrationMapper) {
        this.departmentService = departmentService;
        this.jobTitleService = jobTitleService;
        this.registrationMapper = registrationMapper;
    }

    @Override
    public Doctor getRequired(Long doctorId) {
        Doctor entity = getById(doctorId);
        if (entity == null) {
            throw new BizException("医生不存在：" + doctorId);
        }
        return entity;
    }

    @Override
    public PageResult<Doctor> pageByCondition(long page, long size, String name, Long deptId, Long titleId,
                                              String phone, String idCard, String gender, Integer status) {
        // 姓名是模糊查询，转义 LIKE 通配符；手机号/身份证是精确匹配，保持原值
        name = SqlLikeUtil.escape(name);
        long total = baseMapper.countByCondition(name, deptId, titleId, phone, idCard, gender, status);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0, page, size);
        }
        long start = (page - 1) * size;
        if (start >= total) {
            return new PageResult<>(Collections.emptyList(), total, page, size);
        }
        List<Doctor> records = baseMapper.selectByCondition(name, deptId, titleId, phone, idCard,
                gender, status, start, size);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Doctor create(Doctor body) {
        normalize(body);
        validate(body);
        checkForeignKey(body.getDeptId(), body.getTitleId());
        checkUnique(body.getDoctorNo(), body.getPhone(), body.getIdCard(), null);
        body.setDoctorId(null);
        if (body.getStatus() == null) {
            body.setStatus((byte) 1);
        }
        save(body);
        return getById(body.getDoctorId());
    }

    @Override
    public Doctor modify(Long doctorId, Doctor body) {
        getRequired(doctorId);
        normalize(body);
        validate(body);
        checkForeignKey(body.getDeptId(), body.getTitleId());
        checkUnique(body.getDoctorNo(), body.getPhone(), body.getIdCard(), doctorId);
        body.setDoctorId(doctorId);
        updateById(body);
        return getById(doctorId);
    }

    @Override
    public void delete(Long doctorId) {
        getRequired(doctorId);
        Long regCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>().eq(Registration::getDoctorId, doctorId));
        if (regCount != null && regCount > 0) {
            throw new BizException("该医生存在 " + regCount + " 条挂号记录，无法删除");
        }
        removeById(doctorId);
    }

    /**
     * 字段规范化：去首尾空格、身份证校验位字母统一大写。
     * 身份证末位 x/X 若不统一，数据库唯一索引会当成两个不同值，导致重复建档。
     */
    private void normalize(Doctor body) {
        body.setDoctorNo(trimToNull(body.getDoctorNo()));
        body.setName(trimToNull(body.getName()));
        body.setGender(trimToNull(body.getGender()));
        body.setPhone(trimToNull(body.getPhone()));
        if (body.getIdCard() != null) {
            body.setIdCard(body.getIdCard().trim().toUpperCase());
        }
        if (body.getStatus() == null) {
            body.setStatus((byte) 1);
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validate(Doctor body) {
        FieldValidator.notBlank(body.getDoctorNo(), "医生工号");
        FieldValidator.notBlank(body.getName(), "医生姓名");
        FieldValidator.gender(body.getGender());
        FieldValidator.phone(body.getPhone(), "医生手机号");
        FieldValidator.idCard(body.getIdCard());
        FieldValidator.maxLength(body.getDoctorNo(), 24, "医生工号");
        FieldValidator.maxLength(body.getName(), 32, "医生姓名");
        if (body.getStatus() != null && body.getStatus() != 0 && body.getStatus() != 1) {
            throw new BizException("医生状态只能为 1（在诊）或 0（停诊）");
        }
    }

    private void checkForeignKey(Long deptId, Long titleId) {
        if (deptId == null) {
            throw new BizException("所属科室不能为空");
        }
        if (departmentService.getById(deptId) == null) {
            throw new BizException("所属科室不存在：" + deptId);
        }
        if (titleId == null) {
            throw new BizException("职称不能为空");
        }
        if (jobTitleService.getById(titleId) == null) {
            throw new BizException("职称不存在：" + titleId);
        }
    }

    private void checkUnique(String doctorNo, String phone, String idCard, Long selfId) {
        if (countOther(baseMapper.selectCount(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getDoctorNo, doctorNo)
                .ne(selfId != null, Doctor::getDoctorId, selfId))) > 0) {
            throw new BizException("医生工号已存在：" + doctorNo);
        }
        if (countOther(baseMapper.selectCount(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getPhone, phone)
                .ne(selfId != null, Doctor::getDoctorId, selfId))) > 0) {
            throw new BizException("医生手机号已存在：" + phone);
        }
        if (countOther(baseMapper.selectCount(new LambdaQueryWrapper<Doctor>()
                .eq(Doctor::getIdCard, idCard)
                .ne(selfId != null, Doctor::getDoctorId, selfId))) > 0) {
            throw new BizException("医生身份证号已存在：" + idCard);
        }
    }

    private long countOther(Long count) {
        return count == null ? 0L : count;
    }
}
