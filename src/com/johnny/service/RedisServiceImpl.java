package com.johnny.service;

import annotation.Service;
import com.johnny.inters.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

    @Override
    public String getConnection() {
        return "this is the redis connection.";
    }

    @Override
    public String sayHello() {
        return "say hello";
    }

    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

}
