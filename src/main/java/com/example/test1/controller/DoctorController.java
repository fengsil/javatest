package com.example.test1.controller;

import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Doctor;
import com.example.test1.service.IDoctorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生管理接口：多条件组合分页查询（姓名/科室/职称/手机号/身份证/性别/状态）、详情、增删改。
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final IDoctorService doctorService;

    public DoctorController(IDoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public R<PageResult<Doctor>> page(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) Long deptId,
                                      @RequestParam(required = false) Long titleId,
                                      @RequestParam(required = false) String phone,
                                      @RequestParam(required = false) String idCard,
                                      @RequestParam(required = false) String gender,
                                      @RequestParam(required = false) Integer status) {
        PageParam p = PageParam.of(page, size);
        return R.ok(doctorService.pageByCondition(p.page, p.size, trim(name), deptId, titleId,
                trim(phone), trim(idCard), trim(gender), status));
    }

    @GetMapping("/{id}")
    public R<Doctor> get(@PathVariable Long id) {
        return R.ok(doctorService.getRequired(id));
    }

    @PostMapping
    public R<Doctor> add(@RequestBody Doctor body) {
        return R.ok(doctorService.create(body));
    }

    @PutMapping("/{id}")
    public R<Doctor> update(@PathVariable Long id, @RequestBody Doctor body) {
        return R.ok(doctorService.modify(id, body));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return R.ok(null);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
