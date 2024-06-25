package com.pingan.takeout.manage.center.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pingan.takeout.manage.center.common.BaseContext;
import com.pingan.takeout.manage.center.common.R;
import com.pingan.takeout.manage.center.dto.OrdersDto;
import com.pingan.takeout.manage.center.entity.OrderDetail;
import com.pingan.takeout.manage.center.entity.Orders;
import com.pingan.takeout.manage.center.entity.User;
import com.pingan.takeout.manage.center.service.OrderService;
import com.pingan.takeout.manage.center.service.OrderDetailService;
import com.pingan.takeout.manage.center.service.ShoppingCartService;
import com.pingan.takeout.manage.center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 后台回显
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> pagePC(int page, int pageSize, Long number, Date beginTime, Date endTime) {
        //定制基本Page
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //定制带有名字的特殊Orders
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //书写限制条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null,Orders::getId,number);
        if(beginTime != null && endTime != null){
            queryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        orderService.page(pageInfo,queryWrapper);

        //普通赋值
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");
        //订单赋值
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item)->{
            //新创内部元素
            OrdersDto ordersDto = new OrdersDto();
            //普通值赋值
            BeanUtils.copyProperties(item,ordersDto);
            //特殊值赋值（由于user的name属性为空，因此直接赋id）
//            Long userId = item.getUserId();
//            User user = userService.getById(userId);
//            ordersDto.setUserName(user.getName());
            Long userId = item.getUserId();
            ordersDto.setUserName(userId.toString());
            return ordersDto;
        }).collect(Collectors.toList());
        //完成dishDtoPage的results的内容封装
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }

    /**
     * 修改订单状态（派送中，完成等）
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> beginSend(@RequestBody Orders orders) {
        orderService.updateById(orders);
        return R.success("修改成功");
    }

    /**
     * 查看手机版订单历史记录
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        //新创返回类型Page
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        //用户ID
        Long currentId = BaseContext.getCurrentId();

        //原条件写入
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,currentId);
        queryWrapper.orderByDesc(Orders::getOrderTime);

        orderService.page(pageInfo,queryWrapper);

        //普通赋值
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        //订单赋值
        List<Orders> records = pageInfo.getRecords();

        List<OrdersDto> ordersDtoList = records.stream().map((item)->{
            //新创内部元素
            OrdersDto ordersDto = new OrdersDto();
            //普通值赋值
            BeanUtils.copyProperties(item,ordersDto);
            //菜单详情赋值
            Long itemId = item.getId();

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,itemId);

            int count = orderDetailService.count(orderDetailLambdaQueryWrapper);

            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);

            ordersDto.setSumNum(count);//在ordersDto加上sumNum属性，lombok会自动生成setSumNum方法
            ordersDto.setOrderDetails(orderDetailList);//同上
            return ordersDto;
        }).collect(Collectors.toList());
        //完成dishDtoPage的results的内容封装
        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }
}
