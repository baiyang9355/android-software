package com.johnny.controller;

import annotation.AutoWired;
import annotation.ParameterMapping;
import annotation.RequestMapping;
import com.johnny.inters.RedisService;

@annotation.Controller
@RequestMapping("/test")
public class Controller1 {
    @AutoWired private RedisService redisService;

    @RequestMapping("/getConnection")
    public String getConnection(@ParameterMapping("name") String name) {
        return redisService.getConnection() + " " + name;
    }

    @RequestMapping("/sayHello")
    public String sayHello(@ParameterMapping("name") String name) {
        return redisService.sayHello() + " " + name;
    }

    @RequestMapping("/add")
    public Integer add(@ParameterMapping("a") Integer a, @ParameterMapping("b") Integer b) {
        System.out.println("a is :" + a + ", b is :" + b);
        return redisService.add(a,b);
    }
}
