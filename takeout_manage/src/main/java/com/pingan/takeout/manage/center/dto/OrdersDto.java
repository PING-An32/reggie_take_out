package com.pingan.takeout.manage.center.dto;

import com.pingan.takeout.manage.center.entity.OrderDetail;
import com.pingan.takeout.manage.center.entity.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto extends Orders {
    private List<Orders> orders;
    private String userName;
    private int sumNum;
    private List<OrderDetail> orderDetails;
}
