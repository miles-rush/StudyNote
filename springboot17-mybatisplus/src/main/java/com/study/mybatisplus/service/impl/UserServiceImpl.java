package com.study.mybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.mybatisplus.entity.User;
import com.study.mybatisplus.mapper.UserMapper;
import com.study.mybatisplus.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: KingRainGrey
 * Date: 2020/9/2
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
