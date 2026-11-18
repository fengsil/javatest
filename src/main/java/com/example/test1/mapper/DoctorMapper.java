package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.test1.entity.Doctor;
import org.apache.ibatis.annotations.Param;

/**
 * 医生持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件组合分页查询。
 * SQL 见 resources/mapper/DoctorMapper.xml。
 */
public interface DoctorMapper extends BaseMapper<Doctor> {

    /**
     * 多条件组合分页查询（由分页插件自动追加 COUNT 与 LIMIT）。
     */
    IPage<Doctor> selectPageByCondition(Page<Doctor> page,
                                        @Param("name") String name,
                                        @Param("deptId") Long deptId,
                                        @Param("titleId") Long titleId,
                                        @Param("phone") String phone,
                                        @Param("idCard") String idCard,
                                        @Param("gender") String gender,
                                        @Param("status") Integer status);
}
