package com.example.test1.service.impl;

import com.example.test1.entity.Patient;
import com.example.test1.mapper.PatientMapper;
import com.example.test1.service.IPatientService;
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
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements IPatientService {

}
