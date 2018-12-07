package com.johnny.controller;

import annotation.*;
import com.johnny.inters.HbaseService;
import pojo.Person;

@Controller
@RequestMapping("/test2")
public class Controller2 {
    @AutoWired("zdHbaseService") private HbaseService hbaseService;

    @RequestMapping("/hbase-test1")
    public String getHbaseConnection(@ParameterMapping("name") String name) {
        return hbaseService.getConnection() + " " + name;
    }

    @RequestMapping("/hbase-test2")
    public String hbaseSayHello(@ParameterMapping("name") String name) {
        return hbaseService.sayHello() + " " + name;
    }

    @RequestMapping("/hbase-add")
    public Integer hbaseAdd(@ParameterMapping("a") Integer a, @ParameterMapping("b") Integer b) {
        return hbaseService.add(a,b);
    }

    @RequestMapping("/showInfo")
    public String showInfo(@ModelAttribute Person person,String name) {
        return  name + " --> " + person.toString();
    }

    @RequestMapping("/showInfo2")
    public String showInfo() {
        return  "这是一个无参调用";
    }
}
