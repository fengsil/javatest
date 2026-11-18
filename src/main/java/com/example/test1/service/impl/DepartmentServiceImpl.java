package com.example.test1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Department;
import com.example.test1.entity.Doctor;
import com.example.test1.mapper.DepartmentMapper;
import com.example.test1.mapper.DoctorMapper;
import com.example.test1.service.IDepartmentService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

    private final DoctorMapper doctorMapper;

    public DepartmentServiceImpl(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    @Override
    public PageResult<Department> pageByCondition(long page, long size, String deptName,
                                                  String location, String tel) {
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
        validate(body);
        checkUnique(body.getDeptCode(), body.getDeptName(), null);
        body.setDeptId(null);
        save(body);
        return getById(body.getDeptId());
    }

    @Override
    public Department modify(Long deptId, Department body) {
        if (getById(deptId) == null) {
            throw new BizException("科室不存在：" + deptId);
        }
        validate(body);
        checkUnique(body.getDeptCode(), body.getDeptName(), deptId);
        body.setDeptId(deptId);
        updateById(body);
        return getById(deptId);
    }

    @Override
    public void delete(Long deptId) {
        if (getById(deptId) == null) {
            throw new BizException("科室不存在：" + deptId);
        }
        Long doctorCount = doctorMapper.selectCount(
                new LambdaQueryWrapper<Doctor>().eq(Doctor::getDeptId, deptId));
        if (doctorCount != null && doctorCount > 0) {
            throw new BizException("该科室下仍有 " + doctorCount + " 名医生，无法删除");
        }
        removeById(deptId);
    }

    private void validate(Department body) {
        FieldValidator.notBlank(body.getDeptCode(), "科室编码");
        FieldValidator.notBlank(body.getDeptName(), "科室名称");
        FieldValidator.notBlank(body.getLocation(), "科室位置");
        FieldValidator.officePhone(body.getTel(), "科室联系电话");
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
