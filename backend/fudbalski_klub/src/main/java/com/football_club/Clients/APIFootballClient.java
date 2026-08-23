package com.football_club.Clients;

import com.football_club.dto.apifootball.TeamSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class APIFootballClient {
    private final RestClient restClient;

    // Search for a team by name
    public TeamSearch searchTeams(String teamName) {
        return restClient.get()
                .uri("/teams?search={name}", teamName)
                .retrieve()
                .body(TeamSearch.class);
    }
}
