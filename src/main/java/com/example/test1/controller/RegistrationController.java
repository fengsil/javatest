package com.example.test1.controller;

import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Registration;
import com.example.test1.service.IRegistrationService;
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
 * 挂号记录管理接口：多条件组合分页查询（单号、患者、医生、就诊日期、时段、状态）、详情、增删改。
 */
@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final IRegistrationService registrationService;

    public RegistrationController(IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public R<PageResult<Registration>> page(@RequestParam(defaultValue = "1") long page,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String registrationNo,
                                            @RequestParam(required = false) Long patientId,
                                            @RequestParam(required = false) Long doctorId,
                                            @RequestParam(required = false) String visitDate,
                                            @RequestParam(required = false) String timeSlot,
                                            @RequestParam(required = false) String status) {
        PageParam p = PageParam.of(page, size);
        return R.ok(registrationService.pageByCondition(p.page, p.size,
                trim(registrationNo), patientId, doctorId, trim(visitDate), trim(timeSlot), trim(status)));
    }

    @GetMapping("/{id}")
    public R<Registration> get(@PathVariable Long id) {
        return R.ok(registrationService.getRequired(id));
    }

    @PostMapping
    public R<Registration> add(@RequestBody Registration body) {
        return R.ok(registrationService.create(body));
    }

    @PutMapping("/{id}")
    public R<Registration> update(@PathVariable Long id, @RequestBody Registration body) {
        return R.ok(registrationService.modify(id, body));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        registrationService.delete(id);
        return R.ok(null);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
