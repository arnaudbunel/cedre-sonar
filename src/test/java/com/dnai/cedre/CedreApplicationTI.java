package com.dnai.cedre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:persistence-generic-entity.properties")
public class CedreApplicationTI {

	public static void main(String[] args) {
		SpringApplication.run(CedreApplicationTI.class, args);
	}
}
