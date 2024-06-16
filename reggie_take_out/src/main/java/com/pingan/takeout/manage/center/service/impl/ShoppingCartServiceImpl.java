package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.entity.ShoppingCart;
import com.pingan.takeout.manage.center.mapper.ShoppingCartMapper;
import com.pingan.takeout.manage.center.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {
}
