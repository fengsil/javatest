package com.example.test1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.SqlLikeUtil;
import com.example.test1.entity.Department;
import com.example.test1.entity.Doctor;
import com.example.test1.mapper.DepartmentMapper;
import com.example.test1.mapper.DoctorMapper;
import com.example.test1.service.IDepartmentService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 科室业务实现：分页条件转义、字段规范化、唯一性预检、引用检查后删除。
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    private final DoctorMapper doctorMapper;

    public DepartmentServiceImpl(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    @Override
    public Department getRequired(Long deptId) {
        Department entity = getById(deptId);
        if (entity == null) {
            throw new BizException("科室不存在：" + deptId);
        }
        return entity;
    }

    @Override
    public PageResult<Department> pageByCondition(long page, long size, String deptName,
                                                  String location, String tel) {
        // 模糊条件转义 LIKE 通配符，防止用户输入 % _ 造成宽泛匹配
        deptName = SqlLikeUtil.escape(deptName);
        location = SqlLikeUtil.escape(location);
        long total = baseMapper.countByCondition(deptName, location, tel);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0, page, size);
        }
        long start = (page - 1) * size;
        if (start >= total) {
            // 页码越界时不查库，直接返回空列表
            return new PageResult<>(Collections.emptyList(), total, page, size);
        }
        List<Department> records = baseMapper.selectByCondition(deptName, location, tel, start, size);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public Department create(Department body) {
        normalize(body);
        validate(body);
        checkUnique(body.getDeptCode(), body.getDeptName(), null);
        body.setDeptId(null);
        save(body);
        return getById(body.getDeptId());
    }

    @Override
    public Department modify(Long deptId, Department body) {
        getRequired(deptId);
        normalize(body);
        validate(body);
        checkUnique(body.getDeptCode(), body.getDeptName(), deptId);
        body.setDeptId(deptId);
        // description 允许为空；用 UpdateWrapper 显式 set 才能把它真正清空
        update(new UpdateWrapper<Department>()
                .eq("dept_id", deptId)
                .set("dept_code", body.getDeptCode())
                .set("dept_name", body.getDeptName())
                .set("location", body.getLocation())
                .set("tel", body.getTel())
                .set("description", body.getDescription()));
        return getById(deptId);
    }

    @Override
    public void delete(Long deptId) {
        getRequired(deptId);
        Long doctorCount = doctorMapper.selectCount(
                new LambdaQueryWrapper<Doctor>().eq(Doctor::getDeptId, deptId));
        if (doctorCount != null && doctorCount > 0) {
            throw new BizException("该科室下仍有 " + doctorCount + " 名医生，无法删除");
        }
        removeById(deptId);
    }

    /** 字段规范化：去首尾空格，空串转为 null，科室编码统一大写。 */
    private void normalize(Department body) {
        body.setDeptCode(trimToNull(body.getDeptCode()));
        if (body.getDeptCode() != null) {
            body.setDeptCode(body.getDeptCode().toUpperCase());
        }
        body.setDeptName(trimToNull(body.getDeptName()));
        body.setLocation(trimToNull(body.getLocation()));
        body.setTel(trimToNull(body.getTel()));
        body.setDescription(trimToNull(body.getDescription()));
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validate(Department body) {
        FieldValidator.notBlank(body.getDeptCode(), "科室编码");
        FieldValidator.notBlank(body.getDeptName(), "科室名称");
        FieldValidator.notBlank(body.getLocation(), "科室位置");
        FieldValidator.officePhone(body.getTel(), "科室联系电话");
        FieldValidator.maxLength(body.getDeptCode(), 20, "科室编码");
        FieldValidator.maxLength(body.getDeptName(), 64, "科室名称");
        FieldValidator.maxLength(body.getLocation(), 128, "科室位置");
        FieldValidator.maxLength(body.getDescription(), 255, "科室简介");
    }

    private void checkUnique(String deptCode, String deptName, Long selfId) {
        Long byCode = baseMapper.selectCount(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeptCode, deptCode)
                .ne(selfId != null, Department::getDeptId, selfId));
        if (byCode != null && byCode > 0) {
            throw new BizException("科室编码已存在：" + deptCode);
        }
        Long byName = baseMapper.selectCount(new LambdaQueryWrapper<Department>()
                .eq(Department::getDeptName, deptName)
                .ne(selfId != null, Department::getDeptId, selfId));
        if (byName != null && byName > 0) {
            throw new BizException("科室名称已存在：" + deptName);
        }
    }
}
