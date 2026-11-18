package com.example.test1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.entity.Drug;
import com.example.test1.mapper.DrugMapper;
import com.example.test1.service.IDrugService;
import org.springframework.stereotype.Service;

@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements IDrugService {
}
