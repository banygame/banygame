package com.bany.game.service.impl;

import com.bany.game.mapper.UserMapper;
import com.bany.game.model.User;
import com.bany.game.service.DBUserDetailService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
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


        List<User> usernames = userMapper.selectList(new QueryWrapper<User>().eq("username", username));

        if(!usernames.isEmpty()){
            return -1;
        }
        User user = new User();
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));
        user.setBenable(true);
        user.setCreateTime(new Date());
        int insert = userMapper.insert(user);
        return insert;
    }
}
