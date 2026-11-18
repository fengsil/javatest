package com.example.test1.mapper;

import org.springframework.stereotype.Repository;

/**
 * 健康检查 Mapper：验证 MyBatis 与数据库连接是否正常（供 /api/status 使用）。
 * SQL 统一维护在 resources/mapper/StatusMapper.xml。
 */
@Repository
public interface StatusMapper {

    Integer checkConnection();

    String databaseName();
}
