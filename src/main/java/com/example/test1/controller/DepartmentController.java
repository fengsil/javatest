package com.example.test1.controller;

import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Department;
import com.example.test1.entity.JobTitle;
import com.example.test1.service.IDepartmentService;
import com.example.test1.service.IJobTitleService;
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

/**
 * 科室管理接口：多条件分页查询、详情、新增、修改、删除。
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final IDepartmentService departmentService;
    private final IJobTitleService jobTitleService;

    public DepartmentController(IDepartmentService departmentService, IJobTitleService jobTitleService) {
        this.departmentService = departmentService;
        this.jobTitleService = jobTitleService;
    }

    @GetMapping
    public R<PageResult<Department>> page(@RequestParam(defaultValue = "1") long page,
                                          @RequestParam(defaultValue = "10") long size,
                                          @RequestParam(required = false) String deptName,
                                          @RequestParam(required = false) String location,
                                          @RequestParam(required = false) String tel) {
        PageParam p = PageParam.of(page, size);
        return R.ok(departmentService.pageByCondition(p.page, p.size,
                trim(deptName), trim(location), trim(tel)));
    }

    @GetMapping("/{id}")
    public R<Department> get(@PathVariable Long id) {
        return R.ok(departmentService.getRequired(id));
    }

    @PostMapping
    public R<Department> add(@RequestBody Department body) {
        return R.ok(departmentService.create(body));
    }

    @PutMapping("/{id}")
    public R<Department> update(@PathVariable Long id, @RequestBody Department body) {
        return R.ok(departmentService.modify(id, body));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return R.ok(null);
    }

    /** 职称字典下拉数据（挂号费与职称绑定，供医生表单选择）。 */
    @GetMapping("/titles")
    public R<List<JobTitle>> titles() {
        return R.ok(jobTitleService.list());
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
