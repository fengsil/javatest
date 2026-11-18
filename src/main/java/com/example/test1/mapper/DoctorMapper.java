package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Doctor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医生持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件组合分页查询。SQL 见 resources/mapper/DoctorMapper.xml。
 */
public interface DoctorMapper extends BaseMapper<Doctor> {

    long countByCondition(@Param("name") String name,
                          @Param("deptId") Long deptId,
                          @Param("titleId") Long titleId,
                          @Param("phone") String phone,
                          @Param("idCard") String idCard,
                          @Param("gender") String gender,
                          @Param("status") Integer status);

    List<Doctor> selectByCondition(@Param("name") String name,
                                   @Param("deptId") Long deptId,
                                   @Param("titleId") Long titleId,
                                   @Param("phone") String phone,
                                   @Param("idCard") String idCard,
                                   @Param("gender") String gender,
                                   @Param("status") Integer status,
                                   @Param("offset") long offset,
                                   @Param("size") long size);
}
