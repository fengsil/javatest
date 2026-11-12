package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Patient;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PatientMapper extends BaseMapper<Patient> {

    @Select("<script>" +
            "SELECT COUNT(*) FROM patient " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='idCard != null and idCard != \"\"'> AND id_card = #{idCard}</if>" +
            "<if test='gender != null and gender != \"\"'> AND gender = #{gender}</if>" +
            "</where>" +
            "</script>")
    long countByCondition(@Param("name") String name,
                          @Param("phone") String phone,
                          @Param("idCard") String idCard,
                          @Param("gender") String gender);

    @Select("<script>" +
            "SELECT * FROM patient " +
            "<where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='idCard != null and idCard != \"\"'> AND id_card = #{idCard}</if>" +
            "<if test='gender != null and gender != \"\"'> AND gender = #{gender}</if>" +
            "</where>" +
            " ORDER BY patient_id ASC LIMIT #{offset}, #{size}" +
            "</script>")
    List<Patient> selectByCondition(@Param("name") String name,
                                    @Param("phone") String phone,
                                    @Param("idCard") String idCard,
                                    @Param("gender") String gender,
                                    @Param("offset") long offset,
                                    @Param("size") long size);
}
