package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Patient;

public interface IPatientService extends IService<Patient> {

    PageResult<Patient> pageByCondition(long page, long size, String name, String phone,
                                        String idCard, String gender);

    Patient create(Patient body);

    Patient modify(Long patientId, Patient body);

    void delete(Long patientId);
}
