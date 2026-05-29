package com.meowfit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MeowfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeowfitApplication.class, args);
	}

}
