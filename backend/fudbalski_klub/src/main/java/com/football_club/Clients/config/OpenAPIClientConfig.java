package com.football_club.Clients.config;

import com.football_club.client.ApiClient;
import com.football_club.client.JSON;
import com.football_club.client.api.DefaultApi;
import com.football_club.client.api.SystemApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class OpenAPIClientConfig {
    // Configuring generated classes for using in Spring

    @Value("${football.ai-service.base-url:http://ai-rest-api:8000}")
    private String baseUrl;

    @Bean
    public ApiClient apiClient() {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1);

        return new ApiClient(builder, new JSON().getMapper(), baseUrl);
    }

    @Bean
    public DefaultApi defaultApi(ApiClient apiClient) {
        return new DefaultApi(apiClient);
    }

    @Bean
    public SystemApi systemApi(ApiClient apiClient) {
        return new SystemApi(apiClient);
    }
}