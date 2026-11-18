package com.example.test1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.test1.common.PageResult;
import com.example.test1.entity.Department;

/**
 * 科室业务接口：分页查询、详情、新增、修改、删除。
 */
public interface IDepartmentService extends IService<Department> {

    /** 按主键查询，不存在时抛出业务异常（避免每个 Controller 重复判空）。 */
    Department getRequired(Long deptId);

    /** 多条件组合分页查询。 */
    PageResult<Department> pageByCondition(long page, long size, String deptName, String location, String tel);

    /** 新增科室（含格式校验与唯一性预检）。 */
    Department create(Department body);

    /** 修改科室。 */
    Department modify(Long deptId, Department body);

    /** 删除科室（存在医生引用时拒绝删除）。 */
    void delete(Long deptId);
}
