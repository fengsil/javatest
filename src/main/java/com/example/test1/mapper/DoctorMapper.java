package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Doctor;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DoctorMapper extends BaseMapper<Doctor> {

    @Select("<script>" +
            "SELECT COUNT(*) FROM doctor " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='deptId != null'> AND dept_id = #{deptId}</if>" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='idCard != null and idCard != \"\"'> AND id_card = #{idCard}</if>" +
            "<if test='gender != null and gender != \"\"'> AND gender = #{gender}</if>" +
            "</where>" +
            "</script>")
    long countByCondition(@Param("name") String name,
                          @Param("deptId") Long deptId,
                          @Param("title") String title,
                          @Param("phone") String phone,
                          @Param("idCard") String idCard,
                          @Param("gender") String gender);

    @Select("<script>" +
            "SELECT * FROM doctor " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='deptId != null'> AND dept_id = #{deptId}</if>" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='idCard != null and idCard != \"\"'> AND id_card = #{idCard}</if>" +
            "<if test='gender != null and gender != \"\"'> AND gender = #{gender}</if>" +
            "</where>" +
            " ORDER BY doctor_id ASC LIMIT #{offset}, #{size}" +
            "</script>")
    List<Doctor> selectByCondition(@Param("name") String name,
                                   @Param("deptId") Long deptId,
                                   @Param("title") String title,
                                   @Param("phone") String phone,
                                   @Param("idCard") String idCard,
                                   @Param("gender") String gender,
                                   @Param("offset") long offset,
                                   @Param("size") long size);
}
