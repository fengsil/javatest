package com.example.test1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.test1.entity.PrescriptionItem;
import com.example.test1.mapper.PrescriptionItemMapper;
import com.example.test1.service.IPrescriptionItemService;
import org.springframework.stereotype.Service;

/**
 * 处方明细业务实现。
 */
@Service
public class PrescriptionItemServiceImpl extends ServiceImpl<PrescriptionItemMapper, PrescriptionItem>
        implements IPrescriptionItemService {
}
