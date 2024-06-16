package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.entity.Employee;
import com.pingan.takeout.manage.center.mapper.EmployeeMapper;
import com.pingan.takeout.manage.center.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
