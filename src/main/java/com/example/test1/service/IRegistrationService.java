package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Registration;

/**
 * 挂号记录业务接口：多条件分页查询、详情、新增、修改、删除。
 */
public interface IRegistrationService extends IService<Registration> {

    /** 按主键查询，不存在时抛出业务异常。 */
    Registration getRequired(Long registrationId);

    /** 多条件分页查询（患者、医生、就诊日期、时段、状态、单号）。 */
    PageResult<Registration> pageByCondition(long page, long size, String registrationNo, Long patientId,
                                             Long doctorId, String visitDate, String timeSlot, String status);

    /** 新增挂号记录（唯一性与外键校验）。 */
    Registration create(Registration body);

    /** 修改挂号记录。 */
    Registration modify(Long registrationId, Registration body);

    /** 删除挂号记录（已开处方时拒绝删除）。 */
    void delete(Long registrationId);
}
