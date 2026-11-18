package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.test1.entity.Registration;
import org.apache.ibatis.annotations.Param;

/**
 * 挂号记录持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件分页查询。
 * SQL 见 resources/mapper/RegistrationMapper.xml。
 */
public interface RegistrationMapper extends BaseMapper<Registration> {

    /**
     * 多条件组合分页查询（由分页插件自动追加 COUNT 与 LIMIT）。
     */
    IPage<Registration> selectPageByCondition(Page<Registration> page,
                                              @Param("registrationNo") String registrationNo,
                                              @Param("patientId") Long patientId,
                                              @Param("doctorId") Long doctorId,
                                              @Param("visitDate") String visitDate,
                                              @Param("timeSlot") String timeSlot,
                                              @Param("status") String status);
}
