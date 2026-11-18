package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Patient;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PatientMapper extends BaseMapper<Patient> {

    long countByCondition(@Param("name") String name,
                          @Param("phone") String phone,
                          @Param("idCard") String idCard,
                          @Param("gender") String gender);

    List<Patient> selectByCondition(@Param("name") String name,
                                    @Param("phone") String phone,
                                    @Param("idCard") String idCard,
                                    @Param("gender") String gender,
                                    @Param("offset") long offset,
                                    @Param("size") long size);
}
