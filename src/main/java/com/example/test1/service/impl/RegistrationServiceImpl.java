package com.example.test1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.entity.Registration;
import com.example.test1.mapper.RegistrationMapper;
import com.example.test1.service.IRegistrationService;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements IRegistrationService {
}
