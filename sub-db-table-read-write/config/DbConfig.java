package com.oujiong.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbConfig {
	public static String driverClass;
	public static String url;
	public static String user;
	public static String password;
	
	@Value("${spring.datasource.url}")
	public void setUrl(String classFeedbackImageUrl) {
		DbConfig.url = classFeedbackImageUrl;
	}


@Value("${spring.datasource.driver-class-name}")
public void setDriverClass(String driverClass) {
	DbConfig.driverClass = driverClass;
}	


@Value("${spring.datasource.username}")
public void setuser(String user) {
	DbConfig.user = user;
}


@Value("${spring.datasource.password}")
public void setpassword(String password) {
	DbConfig.password = password;
}
}
