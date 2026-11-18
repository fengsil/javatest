package com.example.test1.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置：注册分页插件 {@link PaginationInnerInterceptor}。
 *
 * <p>分页插件会自动把 {@code selectPage} 的 SQL 改写为「COUNT 查询 + LIMIT 查询」两条语句，
 * 业务层只需传入 {@code Page<T>} 对象，无需手写 {@code LIMIT #{offset}, #{size}}。</p>
 *
 * <p>关键参数说明：</p>
 * <ul>
 *   <li>{@code maxLimit}：单页最大条数，防止前端传 {@code size=huge} 一次性拉全表；</li>
 *   <li>{@code overflow=false}：页码超出总页数时返回空结果，而不是回到第一页；</li>
 *   <li>{@code setOptimizeJoin}：开启 join 查询优化，COUNT 时去掉不必要的 join。</li>
 * </ul>
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页上限与 PageParam.MAX_SIZE 保持一致，双重保险
        pagination.setMaxLimit(100L);
        // 页码越界时返回空列表（而不是跳回第一页），与前端分页组件行为一致
        pagination.setOverflow(false);

        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
