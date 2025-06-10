package com.packhub.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
