package com.bany.game.controller;

import com.bany.game.service.DBUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private DBUserDetailService dbUserDetailService;

    @GetMapping("/hello")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "hello";
    }

    @GetMapping("/login")
    public String login(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "login";
    }

    @GetMapping("/regist")
    public String regist(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        return "regist";
    }
    @PostMapping("/regist")
    public String registUser(String username,String password,Model model) {
        int regist = dbUserDetailService.regist(username, password);
        if(regist<=0){
            model.addAttribute("error","注册失败，用户名可能已存在");
            return "regist";
        }
        return "index";
    }



}
