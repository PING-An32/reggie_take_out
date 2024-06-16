package com.pingan.takeout.manage.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pingan.takeout.manage.center.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
