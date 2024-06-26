package com.pingan.takeout.manage.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pingan.takeout.manage.center.entity.AddressBook;
import com.pingan.takeout.manage.center.mapper.AddressBookMapper;
import com.pingan.takeout.manage.center.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>implements AddressBookService {
}
