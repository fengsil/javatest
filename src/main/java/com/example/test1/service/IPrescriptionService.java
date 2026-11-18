package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Prescription;

/**
 * 电子处方业务接口：多条件分页查询、详情、新增、修改、删除。
 */
public interface IPrescriptionService extends IService<Prescription> {

    /** 按主键查询，不存在时抛出业务异常。 */
    Prescription getRequired(Long prescriptionId);

    /** 多条件分页查询（处方号、挂号单、诊断）。 */
    PageResult<Prescription> pageByCondition(long page, long size, String prescriptionNo,
                                             Long registrationId, String diagnosis);

    /** 新增处方（一次挂号只能开一张处方）。 */
    Prescription create(Prescription body);

    /** 修改处方。 */
    Prescription modify(Long prescriptionId, Prescription body);

    /** 删除处方（存在处方明细时拒绝删除）。 */
    void delete(Long prescriptionId);
}
