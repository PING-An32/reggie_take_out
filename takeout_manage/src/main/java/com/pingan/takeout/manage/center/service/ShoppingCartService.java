package com.pingan.takeout.manage.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.takeout.manage.center.common.R;
import com.pingan.takeout.manage.center.entity.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public R<String> clean();
}
