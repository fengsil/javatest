package com.example.test1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.entity.JobTitle;
import com.example.test1.mapper.JobTitleMapper;
import com.example.test1.service.IJobTitleService;
import org.springframework.stereotype.Service;

@Service
public class JobTitleServiceImpl extends ServiceImpl<JobTitleMapper, JobTitle> implements IJobTitleService {
}
