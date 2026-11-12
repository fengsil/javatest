package com.example.test1.controller;

import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Department;
import com.example.test1.mapper.DepartmentMapper;
import com.example.test1.service.IDepartmentService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final IDepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    public DepartmentController(IDepartmentService departmentService, DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @GetMapping
    public R<PageResult<Department>> page(@RequestParam(defaultValue = "1") long page,
                                          @RequestParam(defaultValue = "10") long size,
                                          @RequestParam(required = false) String deptName,
                                          @RequestParam(required = false) String deptLocation,
                                          @RequestParam(required = false) String deptPhone) {
        PageParam p = PageParam.of(page, size);
        long total = departmentMapper.countByCondition(trim(deptName), trim(deptLocation), trim(deptPhone));
        List<Department> records = departmentMapper.selectByCondition(
                trim(deptName), trim(deptLocation), trim(deptPhone), p.offset, p.size);
        return R.ok(new PageResult<>(records, total, p.page, p.size));
    }

    @GetMapping("/{id}")
    public R<Department> get(@PathVariable Long id) {
        Department entity = departmentService.getById(id);
        if (entity == null) {
            throw new BizException("科室不存在");
        }
        return R.ok(entity);
    }

    @PostMapping
    public R<Department> add(@RequestBody Department body) {
        validate(body);
        body.setDeptId(null);
        departmentService.save(body);
        return R.ok(body);
    }

    @PutMapping("/{id}")
    public R<Department> update(@PathVariable Long id, @RequestBody Department body) {
        if (departmentService.getById(id) == null) {
            throw new BizException("科室不存在");
        }
        validate(body);
        body.setDeptId(id);
        departmentService.updateById(body);
        return R.ok(body);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!departmentService.removeById(id)) {
            throw new BizException("科室不存在或删除失败");
        }
        return R.ok(null);
    }

    private void validate(Department body) {
        FieldValidator.notBlank(body.getDeptName(), "科室名称");
        FieldValidator.optionalOfficePhone(body.getDeptPhone(), "科室电话");
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
