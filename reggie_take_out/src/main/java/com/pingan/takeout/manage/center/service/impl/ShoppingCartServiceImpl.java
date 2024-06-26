package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.common.BaseContext;
import com.pingan.takeout.manage.center.common.R;
import com.pingan.takeout.manage.center.entity.ShoppingCart;
import com.pingan.takeout.manage.center.mapper.ShoppingCartMapper;
import com.pingan.takeout.manage.center.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {
    @Override
    @Transactional
    public R<String> clean() {
        //进行用户比对
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //删除即可
        this.remove(queryWrapper);
        return R.success("清空成功");
    }
}
