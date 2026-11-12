package com.example.test1.controller;

import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Patient;
import com.example.test1.mapper.PatientMapper;
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

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final IPatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(IPatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @GetMapping
    public R<PageResult<Patient>> page(@RequestParam(defaultValue = "1") long page,
                                       @RequestParam(defaultValue = "10") long size,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String phone,
                                       @RequestParam(required = false) String idCard,
                                       @RequestParam(required = false) String gender) {
        PageParam p = PageParam.of(page, size);
        long total = patientMapper.countByCondition(trim(name), trim(phone), trim(idCard), trim(gender));
        List<Patient> records = patientMapper.selectByCondition(trim(name), trim(phone),
                trim(idCard), trim(gender), p.offset, p.size);
        return R.ok(new PageResult<>(records, total, p.page, p.size));
    }

    @GetMapping("/{id}")
    public R<Patient> get(@PathVariable Long id) {
        Patient entity = patientService.getById(id);
        if (entity == null) {
            throw new BizException("患者不存在");
        }
        return R.ok(entity);
    }

    @PostMapping
    public R<Patient> add(@RequestBody Patient body) {
        validate(body);
        body.setPatientId(null);
        patientService.save(body);
        return R.ok(body);
    }

    @PutMapping("/{id}")
    public R<Patient> update(@PathVariable Long id, @RequestBody Patient body) {
        if (patientService.getById(id) == null) {
            throw new BizException("患者不存在");
        }
        validate(body);
        body.setPatientId(id);
        patientService.updateById(body);
        return R.ok(body);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!patientService.removeById(id)) {
            throw new BizException("患者不存在或删除失败");
        }
        return R.ok(null);
    }

    private void validate(Patient body) {
        FieldValidator.notBlank(body.getName(), "患者姓名");
        FieldValidator.phone(body.getPhone(), "患者手机号");
        FieldValidator.idCard(body.getIdCard());
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
