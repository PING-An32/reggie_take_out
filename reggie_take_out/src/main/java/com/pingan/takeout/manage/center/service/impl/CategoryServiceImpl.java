package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.common.CustomException;
import com.pingan.takeout.manage.center.entity.Category;
import com.pingan.takeout.manage.center.entity.Dish;
import com.pingan.takeout.manage.center.entity.Setmeal;
import com.pingan.takeout.manage.center.mapper.CategoryMapper;
import com.pingan.takeout.manage.center.service.CategoryService;
import com.pingan.takeout.manage.center.service.DishService;
import com.pingan.takeout.manage.center.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    /**
     * 通过autowired注入两个service
     */
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<Dish>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if(count1 > 0){
            throw new CustomException("当前分类项关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2 > 0){
            throw new CustomException("当前分类项关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
