package com.website.service;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;



@MapperScan("com.website.service.mapper")
@SpringBootTest
class ServiceApplicationTests {


    @Test
    void contextLoads() {
        System.out.println("Hello World!");
    }

}
