package com.example.test1.service.impl;

import com.example.test1.entity.Doctor;
import com.example.test1.mapper.DoctorMapper;
import com.example.test1.service.IDoctorService;
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
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements IDoctorService {

}
