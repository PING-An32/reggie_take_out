package com.pingan.takeout.manage.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.takeout.manage.center.entity.Orders;

public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
}
