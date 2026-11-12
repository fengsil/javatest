package com.example.test1.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusMapper {

    @Select("SELECT 1")
    Integer checkConnection();

    @Select("SELECT DATABASE()")
    String databaseName();
}
