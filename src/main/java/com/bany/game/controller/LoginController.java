package com.bany.game.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


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
    public String registUser(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {

        return "hello";
    }



}
