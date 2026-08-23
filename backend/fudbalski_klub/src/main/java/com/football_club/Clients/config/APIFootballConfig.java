package com.football_club.Clients.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class APIFootballConfig {
    @Value("${api.football.key}")
    private String apiKey;

    @Value("${api.football.base-url}")
    private String baseUrl;

    @Bean
    public RestClient apiFootballRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-apisports-key", apiKey)
                .build();
    }
}
