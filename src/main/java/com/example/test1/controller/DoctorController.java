package com.example.test1.controller;

import com.example.test1.common.BizException;
import com.example.test1.common.FieldValidator;
import com.example.test1.common.PageResult;
import com.example.test1.common.R;
import com.example.test1.entity.Doctor;
import com.example.test1.mapper.DoctorMapper;
import com.example.test1.service.IDepartmentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final IDoctorService doctorService;
    private final IDepartmentService departmentService;
    private final DoctorMapper doctorMapper;

    public DoctorController(IDoctorService doctorService, IDepartmentService departmentService,
                            DoctorMapper doctorMapper) {
        this.doctorService = doctorService;
        this.departmentService = departmentService;
        this.doctorMapper = doctorMapper;
    }

    @GetMapping
    public R<PageResult<Doctor>> page(@RequestParam(defaultValue = "1") long page,
                                      @RequestParam(defaultValue = "10") long size,
                                      @RequestParam(required = false) String name,
                                      @RequestParam(required = false) Long deptId,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) String phone,
                                      @RequestParam(required = false) String idCard,
                                      @RequestParam(required = false) String gender) {
        PageParam p = PageParam.of(page, size);
        long total = doctorMapper.countByCondition(trim(name), deptId, trim(title),
                trim(phone), trim(idCard), trim(gender));
        List<Doctor> records = doctorMapper.selectByCondition(trim(name), deptId, trim(title),
                trim(phone), trim(idCard), trim(gender), p.offset, p.size);
        return R.ok(new PageResult<>(records, total, p.page, p.size));
    }

    @GetMapping("/{id}")
    public R<Doctor> get(@PathVariable Long id) {
        Doctor entity = doctorService.getById(id);
        if (entity == null) {
            throw new BizException("医生不存在");
        }
        return R.ok(entity);
    }

    @PostMapping
    public R<Doctor> add(@RequestBody Doctor body) {
        validate(body);
        body.setDoctorId(null);
        doctorService.save(body);
        return R.ok(body);
    }

    @PutMapping("/{id}")
    public R<Doctor> update(@PathVariable Long id, @RequestBody Doctor body) {
        if (doctorService.getById(id) == null) {
            throw new BizException("医生不存在");
        }
        validate(body);
        body.setDoctorId(id);
        doctorService.updateById(body);
        return R.ok(body);
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (!doctorService.removeById(id)) {
            throw new BizException("医生不存在或删除失败");
        }
        return R.ok(null);
    }

    private void validate(Doctor body) {
        FieldValidator.notBlank(body.getName(), "医生姓名");
        FieldValidator.phone(body.getPhone(), "医生手机号");
        FieldValidator.idCard(body.getIdCard());
        if (body.getDeptId() == null) {
            throw new BizException("所属科室不能为空");
        }
        if (departmentService.getById(body.getDeptId()) == null) {
            throw new BizException("所属科室不存在");
        }
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
