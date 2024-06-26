package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.entity.User;
import com.pingan.takeout.manage.center.mapper.UserMapper;
import com.pingan.takeout.manage.center.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
