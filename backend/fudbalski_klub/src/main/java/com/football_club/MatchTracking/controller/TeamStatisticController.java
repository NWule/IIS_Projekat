package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.GameDTO;
import com.football_club.MatchTracking.dto.TeamChartDTO;
import com.football_club.MatchTracking.dto.TeamStatisticDTO;
import com.football_club.MatchTracking.service.IGameService;
import com.football_club.MatchTracking.service.ITeamStatisticService;
import com.football_club.Auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class TeamStatisticController {

    private final ITeamStatisticService teamStatisticService;
    private final IGameService gameService;

    @PostMapping("/final")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'HEAD_COACH', 'ADMIN')")
    public ResponseEntity<?> saveFinalStatistic(@RequestBody TeamStatisticDTO statisticDTO, @AuthenticationPrincipal User user) {
        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            GameDTO game = gameService.getGameById(statisticDTO.getGameId());
            if (user.getClubId() == null || (game.getHomeClubId() != user.getClubId() && game.getAwayClubId() != user.getClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        TeamStatisticDTO savedStatistic = teamStatisticService.saveFinalStatistic(statisticDTO);
        return new ResponseEntity<>(savedStatistic, HttpStatus.OK);
    }

    @GetMapping("/game/{gameId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<TeamStatisticDTO> getStatisticByGameId(@PathVariable Long gameId) {
        TeamStatisticDTO statisticDTO = teamStatisticService.getStatisticByGameId(gameId);
        return ResponseEntity.ok(statisticDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<?> deleteStatistic(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            TeamStatisticDTO statistic = teamStatisticService.getStatisticByGameId(id);
            GameDTO game = gameService.getGameById(statistic.getGameId());
            if (user.getClubId() == null || (game.getHomeClubId() != user.getClubId() && game.getAwayClubId() != user.getClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        teamStatisticService.deleteStatistic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/club/{clubId}/chart")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'STATISTICIAN', 'ADMIN', 'SPORTS_DIRECTOR')")
    public ResponseEntity<List<TeamChartDTO>> getClubChartData(@PathVariable Long clubId) {
        List<TeamChartDTO> chartData = teamStatisticService.getClubChartStatistics(clubId);
        return ResponseEntity.ok(chartData);
    }
}