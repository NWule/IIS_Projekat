package com.football_club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FudbalskiKlubApplication {

	public static void main(String[] args) {
		SpringApplication.run(FudbalskiKlubApplication.class, args);
	}

	@Configuration
	@EnableJpaRepositories(basePackages = {
	    "com.football_club.Auth.repository",
	    "com.football_club.MatchTracking.repository.jpa",
	    "com.football_club.Scouting.repository",
	    "com.football_club.TicketSales.repository"
	},
			transactionManagerRef = "transactionManager"
	)
	@EnableNeo4jRepositories(basePackages = {
	    "com.football_club.MatchTracking.repository.graph"
	},
			transactionManagerRef = "neo4jTransactionManager")
	public class DatabaseConfig {
	}

}
