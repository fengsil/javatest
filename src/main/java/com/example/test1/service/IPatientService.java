package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Patient;

/**
 * 患者业务接口：多条件分页查询、详情、新增、修改、删除。
 */
public interface IPatientService extends IService<Patient> {

    /** 按主键查询，不存在时抛出业务异常（避免每个 Controller 重复判空）。 */
    Patient getRequired(Long patientId);
    PageResult<Patient> pageByCondition(long page, long size, String name, String phone,
                                        String idCard, String gender);

    Patient create(Patient body);

    Patient modify(Long patientId, Patient body);

    void delete(Long patientId);
}
