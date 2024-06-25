package com.pingan.takeout.manage.center.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.takeout.manage.center.common.R;
import com.pingan.takeout.manage.center.entity.Orders;
import com.pingan.takeout.manage.center.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }
//    @GetMapping("/userPage")
//    public R<Page> page(int page, int pageSize, String name){
//        Page<>
//    }
}
