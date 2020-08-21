package com.hu.system.controller;

import com.hu.framework.annotation.Controller;
import com.hu.framework.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description
 * @Author us
 * @Date 2020/8/12 14:20
 * @Version 1.0
 **/
@Controller
@RequestMapping("/sys/user")
public class UserController {

    /**
     *  跳转到用户列表
     */
    @RequestMapping
    public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "老张");
        return map;
    }

    /**
     *  跳转到新增用户
     */
    @RequestMapping("/add")
    public String add(HttpServletRequest request, HttpServletResponse response, String name){
        return "user_add";
        // return "redirect:/index.html";
    }

    /**
     *  新增用户
     */
    @RequestMapping("/doAdd")
    public void doAdd(){

    }
}
