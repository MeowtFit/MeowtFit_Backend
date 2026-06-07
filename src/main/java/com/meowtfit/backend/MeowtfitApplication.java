package com.meowtfit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MeowtfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeowtfitApplication.class, args);
	}

}
