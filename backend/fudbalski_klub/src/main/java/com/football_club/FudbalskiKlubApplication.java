package com.football_club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FudbalskiKlubApplication {

	public static void main(String[] args) {
		SpringApplication.run(FudbalskiKlubApplication.class, args);
	}

}
