package com.packhub.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
