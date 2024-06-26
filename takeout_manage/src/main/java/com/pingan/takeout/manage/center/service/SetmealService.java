package com.pingan.takeout.manage.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.takeout.manage.center.dto.SetmealDto;
import com.pingan.takeout.manage.center.entity.Setmeal;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);

    /**
     * 修改操作
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);
    public void updateSetmealStatusById(CountDownLatch countDownLatch,int status,Long id,Long userId);
}
