package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.test1.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    long countByCondition(@Param("deptName") String deptName,
                          @Param("location") String location,
                          @Param("tel") String tel);

    List<Department> selectByCondition(@Param("deptName") String deptName,
                                       @Param("location") String location,
                                       @Param("tel") String tel,
                                       @Param("offset") long offset,
                                       @Param("size") long size);
}
