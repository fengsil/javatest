package com.example.test1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.test1.entity.Department;
import org.apache.ibatis.annotations.Param;

/**
 * 科室持久层：继承 BaseMapper 获得通用 CRUD，并补充多条件分页查询。
 * SQL 见 resources/mapper/DepartmentMapper.xml。
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 多条件分页查询。
     *
     * <p>第一个参数为 {@link Page} 时必须放在首位，分页插件 {@code PaginationInnerInterceptor}
     * 会自动拦截该语句并追加 COUNT 查询与 LIMIT 子句，SQL 中无需手写分页语句。</p>
     */
    IPage<Department> selectPageByCondition(Page<Department> page,
                                            @Param("deptName") String deptName,
                                            @Param("location") String location,
                                            @Param("tel") String tel);
}
