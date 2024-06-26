package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.common.BaseContext;
import com.pingan.takeout.manage.center.common.CustomException;
import com.pingan.takeout.manage.center.dto.SetmealDto;
import com.pingan.takeout.manage.center.entity.Setmeal;
import com.pingan.takeout.manage.center.entity.SetmealDish;
import com.pingan.takeout.manage.center.mapper.SetmealMapper;
import com.pingan.takeout.manage.center.service.SetmealDishService;
import com.pingan.takeout.manage.center.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes =  setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count>0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal id in (x,x,x)
        LambdaQueryWrapper<SetmealDish> queryWrapperDish = new LambdaQueryWrapper<>();
        queryWrapperDish.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据setmeal_dish
        setmealDishService.remove(queryWrapperDish);
    }

    /**
     * 修改操作
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //首先修改套餐上的信息
        this.updateById(setmealDto);
        //修改内部菜品操作（同样先删除再添加）
        //删除操作
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);

        setmealDishService.remove(queryWrapper);
        //新添操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Async("asyncServiceExecutor")
    public void updateSetmealStatusById(CountDownLatch countDownLatch, int status, Long id, Long userId) {
        try{
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,id);
            List<Setmeal> list = this.list(queryWrapper);
            //这里是因为线程不同了，原来登录过滤器设置的用户id拿不到，得通过传参方式给进来
            //然后用封装的BaseContext放到ThreadLocal里，才能给MyMetaObjectHandler来做公共字段自动填充
            BaseContext.setCurrentId(userId);
            for(Setmeal setmeal:list){
                if(setmeal != null){
                    setmeal.setStatus(status);
                    this.updateById(setmeal);
                }
            }
        }finally {
            countDownLatch.countDown();
        }
    }
}
