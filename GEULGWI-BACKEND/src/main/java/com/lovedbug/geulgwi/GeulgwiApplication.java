package com.lovedbug.geulgwi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.TimeZone;

@EnableFeignClients
@EnableJpaAuditing
@SpringBootApplication
public class GeulgwiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeulgwiApplication.class, args);
	}
}
