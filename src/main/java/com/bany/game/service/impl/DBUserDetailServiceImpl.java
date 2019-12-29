package com.bany.game.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.bany.game.mapper.UserMapper;
import com.bany.game.model.User;
import com.bany.game.service.DBUserDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBUserDetailServiceImpl implements DBUserDetailService {

    @Autowired
    private UserMapper userMapper ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", s).eq("is_del",0));

        user.setRoles("ROLE_ADMIN");
        System.out.println(user.toString());
        return user;
    }

    @Override
    public int regist(String username, String password) {


        List<User> username1 = userMapper.selectList(new QueryWrapper<User>().eq("username", username));

        if(ObjectUtil.isNotNull(username1)){
            return -1;
        }
        User user = new User();
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));
        user.setBenable(true);
        int insert = userMapper.insert(user);
        return insert;
    }
}
