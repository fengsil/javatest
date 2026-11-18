package com.example.test1.controller;

import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Prescription;
import com.example.test1.service.IPrescriptionService;
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
 * 电子处方管理接口：多条件组合分页查询（处方号、挂号单、诊断）、详情、增删改。
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final IPrescriptionService prescriptionService;

    public PrescriptionController(IPrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public R<PageResult<Prescription>> page(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String prescriptionNo,
                                            @RequestParam(required = false) Long registrationId,
                                            @RequestParam(required = false) String diagnosis) {
        PageParam p = PageParam.of(page, size);
        return R.ok(prescriptionService.pageByCondition(p.page, p.size,
                trim(prescriptionNo), registrationId, trim(diagnosis)));
    }

    @GetMapping("/{id}")
    public R<Prescription> get(@PathVariable Long id) {
        return R.ok(prescriptionService.getRequired(id));
    }

    @PostMapping
    public R<Prescription> add(@RequestBody Prescription body) {
        return R.ok(prescriptionService.create(body));
    }

    @PutMapping("/{id}")
    public R<Prescription> update(@PathVariable Long id, @RequestBody Prescription body) {
        return R.ok(prescriptionService.modify(id, body));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        prescriptionService.delete(id);
        return R.ok(null);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
