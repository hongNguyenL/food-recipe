package com.nguyen.foodrecipe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodrecipeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodrecipeApplication.class, args);
	}
}
