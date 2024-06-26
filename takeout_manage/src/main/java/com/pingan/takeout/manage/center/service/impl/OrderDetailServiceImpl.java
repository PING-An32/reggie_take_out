package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.entity.OrderDetail;
import com.pingan.takeout.manage.center.mapper.OrderDetailMapper;
import com.pingan.takeout.manage.center.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
