package com.bany.game.controller;

import com.bany.game.model.User;
import com.bany.game.service.PunchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PunchController {


    @Autowired
    private PunchService punchService;
    @GetMapping(value = {"/punch"})
    @ResponseBody
    public String punch (Authentication authentication,String punchTime){
        String userName = ((User) authentication.getPrincipal()).getUsername();
        String result = punchService.punch(userName, punchTime);

        return result;
    }

}
