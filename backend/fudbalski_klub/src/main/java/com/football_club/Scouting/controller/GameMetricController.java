package com.football_club.Scouting.controller;

import com.football_club.Scouting.dto.GameMetricDTO;
import com.football_club.Scouting.dto.GameMetricSaveDTO;
import com.football_club.Scouting.service.IGameMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-metrics")
@RequiredArgsConstructor
public class GameMetricController {

    private final IGameMetricService gameMetricService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<GameMetricDTO> createGameMetric(@RequestBody GameMetricSaveDTO dto) {
        GameMetricDTO created = gameMetricService.createGameMetric(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> createGameMetrics(@RequestBody List<GameMetricSaveDTO> dtos) {
        List<GameMetricDTO> createdList = gameMetricService.createGameMetrics(dtos);
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<GameMetricDTO> getGameMetricById(@PathVariable Long id) {
        return ResponseEntity.ok(gameMetricService.getGameMetricById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> getAllGameMetrics() {
        return ResponseEntity.ok(gameMetricService.getAllGameMetrics());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<GameMetricDTO> updateGameMetric(@PathVariable Long id, @RequestBody GameMetricSaveDTO dto) {
        return ResponseEntity.ok(gameMetricService.updateGameMetric(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<Void> deleteGameMetric(@PathVariable Long id) {
        gameMetricService.deleteGameMetric(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/game/{gameId}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> getMetricsByGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameMetricService.getMetricsByGame(gameId));
    }

    @GetMapping("/player/{playerId}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> getMetricsByPlayer(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameMetricService.getMetricsByPlayer(playerId));
    }

    @GetMapping("/game/{gameId}/player/{playerId}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> getMetricsByGameAndPlayer(
            @PathVariable Long gameId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(gameMetricService.getMetricsByGameAndPlayer(gameId, playerId));
    }

    @GetMapping("/player/{playerId}/recent")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameMetricDTO>> getLastFiveGamesMetrics(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameMetricService.getLastFiveGamesMetrics(playerId));
    }
}