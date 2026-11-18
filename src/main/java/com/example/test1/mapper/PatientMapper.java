package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.test1.entity.Patient;
import org.apache.ibatis.annotations.Param;

/**
 * 患者持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件组合分页查询。
 * SQL 见 resources/mapper/PatientMapper.xml。
 */
public interface PatientMapper extends BaseMapper<Patient> {

    /**
     * 多条件组合分页查询（由分页插件自动追加 COUNT 与 LIMIT）。
     */
    IPage<Patient> selectPageByCondition(Page<Patient> page,
                                         @Param("name") String name,
                                         @Param("phone") String phone,
                                         @Param("idCard") String idCard,
                                         @Param("gender") String gender);
}
