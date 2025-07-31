package com.DreamOfDuck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableJpaAuditing
@EnableAsync
public class DreamOfDuckApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamOfDuckApplication.class, args);
	}

}
