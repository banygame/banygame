package com.bany.game.service.impl;

import com.bany.game.mapper.UserMapper;
import com.bany.game.model.User;
import com.bany.game.service.DBUserDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DBUserDetailServiceImpl implements DBUserDetailService {

    @Autowired
    private UserMapper userMapper ;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", s).eq("is_del",0));

        user.setRoles("ROLE_ADMIN");
        System.out.println(user.toString());
        return user;
    }

    @Override
    public int regist(String username, String password) {

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setBenable(true);
        int insert = userMapper.insert(user);
        return insert;
    }
}
