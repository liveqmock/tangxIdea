package com.topaiebiz.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Description Nebula软件快速研发平台SSM基础框架的演示项目的启动器。 
 * 
 * Author Aaron.Xue 
 *    
 * Date 2017年9月26日 下午2:35:57 
 * 
 * Copyright Cognieon technology group co.LTD. All rights reserved.DF
 * 
 * Notice 本内容仅限于授权后使用，禁止非授权传阅以及私自用于其他商CorsFilter业目的。
 *
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.nebulapaas,com.topaiebiz")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class OnlineApplicationStarter {
	/**
	 * Description 应用的启动入口
	 *
	 * Author Hedda
	 *
	 * @param args 自定义变量参数
	 */
	public static void main(String[] args) {
		SpringApplication.run(OnlineApplicationStarter.class, args);
	}
}
