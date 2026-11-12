package com.example.test1.service.impl;

import com.example.test1.entity.Department;
import com.example.test1.mapper.DepartmentMapper;
import com.example.test1.service.IDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author student
 * @since 
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements IDepartmentService {

}
