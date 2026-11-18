package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.test1.entity.Prescription;
import org.apache.ibatis.annotations.Param;

/**
 * 电子处方持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件分页查询。
 * SQL 见 resources/mapper/PrescriptionMapper.xml。
 */
public interface PrescriptionMapper extends BaseMapper<Prescription> {

    /**
     * 多条件组合分页查询（由分页插件自动追加 COUNT 与 LIMIT）。
     */
    IPage<Prescription> selectPageByCondition(Page<Prescription> page,
                                              @Param("prescriptionNo") String prescriptionNo,
                                              @Param("registrationId") Long registrationId,
                                              @Param("diagnosis") String diagnosis);
}
