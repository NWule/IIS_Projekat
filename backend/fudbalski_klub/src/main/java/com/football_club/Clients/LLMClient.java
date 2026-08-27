package com.football_club.Clients;

import com.football_club.client.ApiClient;
import com.football_club.client.ApiException;
import com.football_club.client.api.DefaultApi;
import com.football_club.client.api.TacticalAnalysisApi;
import com.football_club.client.model.DirectPromptRequest;
import com.football_club.client.model.TacticalAnalysisRequest;
import com.football_club.client.model.TacticalAnalysisResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;

@Service
public class LLMClient {
    private DefaultApi defaultApi;
    private TacticalAnalysisApi tacticalAnalysisApi;

    @Value("${FOOTBALL_AI_SERVICE_BASE_URL:http://localhost:8000}")
    private String aiServiceUrl;

    @PostConstruct
    public void init() {
        ApiClient apiClient = new ApiClient();
        apiClient.updateBaseUri(aiServiceUrl);

        apiClient.setHttpClientBuilder(
                HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
        );

        apiClient.updateBaseUri(aiServiceUrl);

        this.defaultApi = new DefaultApi(apiClient);
        this.tacticalAnalysisApi = new TacticalAnalysisApi(apiClient);
    }

    public Object askAi(String promptText) {
        try {
            DirectPromptRequest request = new DirectPromptRequest();
            request.setPrompt(promptText);
            return defaultApi.generateResponseApiV1ChatQueryPost(request);
        } catch (ApiException e) {
            throw new RuntimeException("Scouting AI error: " + e.getMessage(), e);
        }
    }

    public TacticalAnalysisResponse generateTacticalReport(TacticalAnalysisRequest request) {
        try {
            return tacticalAnalysisApi.generateTacticalAnalysisApiV1TacticalGeneratePost(request);
        } catch (ApiException e) {
            throw new RuntimeException("Tactical AI error: " + e.getMessage(), e);
        }
    }
}