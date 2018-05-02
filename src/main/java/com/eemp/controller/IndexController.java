package com.eemp.controller;

import com.eemp.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by A03742 on 2018-05-02.
 */
@Controller
public class IndexController {

    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "/")
    public String index(HttpServletRequest request, Model model) {
        return "index";
    }


    @RequestMapping(value = "/test")
    public String test(HttpServletRequest request, Model model) {

        redisUtil.setStringValue("template","template",1000);
        return "test";
    }


}
