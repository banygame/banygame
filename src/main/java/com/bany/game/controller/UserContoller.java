package com.bany.game.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserContoller {

    @GetMapping("/list")
    public String list() {


        return "hello";
    }


}
