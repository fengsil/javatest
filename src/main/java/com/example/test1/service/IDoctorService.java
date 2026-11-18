package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Doctor;

public interface IDoctorService extends IService<Doctor> {

    PageResult<Doctor> pageByCondition(long page, long size, String name, Long deptId, Long titleId,
                                       String phone, String idCard, String gender, Integer status);

    Doctor create(Doctor body);

    Doctor modify(Long doctorId, Doctor body);

    void delete(Long doctorId);
}
