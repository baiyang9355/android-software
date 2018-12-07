package com.johnny.inters;

public interface RedisService {
    String getConnection();
    String sayHello();
    Integer add(Integer a, Integer b);
}
