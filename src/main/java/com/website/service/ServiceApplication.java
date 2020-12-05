package com.website.service;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author 修世超
 * @date 2020-8-7 12:20:29
 */
@MapperScan({"com.website.service.mapper"})
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class ServiceApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
		LOGGER.info("项目启动");
	}

}
