package com.example.test1.controller;

import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Patient;
import com.example.test1.service.IPatientService;
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
 * 患者管理接口：多条件组合分页查询（姓名/手机号/身份证/性别）、详情、增删改。
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final IPatientService patientService;

    public PatientController(IPatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public R<PageResult<Patient>> page(@RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String phone,
                                       @RequestParam(required = false) String idCard,
                                       @RequestParam(required = false) String gender) {
        PageParam p = PageParam.of(page, size);
        return R.ok(patientService.pageByCondition(p.page, p.size, trim(name), trim(phone),
                trim(idCard), trim(gender)));
    }

    @GetMapping("/{id}")
    public R<Patient> get(@PathVariable Long id) {
        return R.ok(patientService.getRequired(id));
    }

    @PostMapping
    public R<Patient> add(@RequestBody Patient body) {
        return R.ok(patientService.create(body));
    }

    @PutMapping("/{id}")
    public R<Patient> update(@PathVariable Long id, @RequestBody Patient body) {
        return R.ok(patientService.modify(id, body));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return R.ok(null);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
