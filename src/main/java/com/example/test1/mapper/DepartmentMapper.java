package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Department;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("<script>" +
            "SELECT COUNT(*) FROM department " +
            "<where>" +
            "<if test='deptName != null and deptName != \"\"'> AND dept_name LIKE CONCAT('%', #{deptName}, '%')</if>" +
            "<if test='deptLocation != null and deptLocation != \"\"'> AND dept_location LIKE CONCAT('%', #{deptLocation}, '%')</if>" +
            "<if test='deptPhone != null and deptPhone != \"\"'> AND dept_phone = #{deptPhone}</if>" +
            "</where>" +
            "</script>")
    long countByCondition(@Param("deptName") String deptName,
                          @Param("deptLocation") String deptLocation,
                          @Param("deptPhone") String deptPhone);

    @Select("<script>" +
            "SELECT * FROM department " +
            "<where>" +
            "<if test='deptName != null and deptName != \"\"'> AND dept_name LIKE CONCAT('%', #{deptName}, '%')</if>" +
            "<if test='deptLocation != null and deptLocation != \"\"'> AND dept_location LIKE CONCAT('%', #{deptLocation}, '%')</if>" +
            "<if test='deptPhone != null and deptPhone != \"\"'> AND dept_phone = #{deptPhone}</if>" +
            "</where>" +
            " ORDER BY dept_id ASC LIMIT #{offset}, #{size}" +
            "</script>")
    List<Department> selectByCondition(@Param("deptName") String deptName,
                                       @Param("deptLocation") String deptLocation,
                                       @Param("deptPhone") String deptPhone,
                                       @Param("offset") long offset,
                                       @Param("size") long size);
}
