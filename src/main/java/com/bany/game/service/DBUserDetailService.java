package com.bany.game.service;

import com.bany.game.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface DBUserDetailService extends UserDetailsService{

     int regist (String username,String password);


}
