package com.hu.system.controller;

import com.hu.framework.annotation.Controller;
import com.hu.framework.annotation.RequestMapping;

/**
 * @ClassName NewsController
 * @Description
 * @Author us
 * @Date 2020/8/12 14:23
 * @Version 1.0
 **/
@Controller
@RequestMapping("/sys/news")
public class NewsController {
    /**
     *  跳转到新闻列表
     */
    @RequestMapping
    public void list(){

    }

    /**
     *  跳转到新增新闻
     */
    @RequestMapping("/add")
    public void add(){

    }

    /**
     *  新增新闻
     */
    @RequestMapping("/doAdd")
    public void doAdd(){

    }
}
