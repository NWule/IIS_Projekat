package com.football_club.Clients;

import com.football_club.client.ApiException;
import com.football_club.client.api.DefaultApi;
import com.football_club.client.api.SystemApi;
import com.football_club.client.model.DirectPromptRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LLMClient {
    private final DefaultApi defaultApi;
    private final SystemApi systemApi;

    /**
     * Sends a prompt query to the Gemini AI scouting microservice.
     */
    public Object askAi(String promptText) {
        try {
            DirectPromptRequest request = new DirectPromptRequest();
            request.setPrompt(promptText);

            return defaultApi.generateResponseApiV1ChatQueryPost(request);
        } catch (ApiException e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            throw new RuntimeException("Scouting AI Service error [Code " + e.getCode() + "]: " + e.getMessage(), e);
        }
    }

    public TacticalAnalysisResponse generateTacticalReport(TacticalAnalysisRequest request) {
        try {
            return defaultApi.generateTacticalAnalysisApiV1TacticalGeneratePost(request);
        } catch (ApiException e) {
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            throw new RuntimeException("Tactical AI Service error [Code " + e.getCode() + "]: " + e.getMessage(), e);
        }
    }
}
