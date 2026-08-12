package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.PlayerDTO;
import com.football_club.MatchTracking.dto.PlayerWithReportDTO;
import com.football_club.MatchTracking.service.IPlayerService;
import com.football_club.Auth.model.User;
import com.football_club.Scouting.dto.SearchParameters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final IPlayerService playerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ASSISTANT_COACH', 'ADMIN')")
    public ResponseEntity<PlayerDTO> createPlayer(@RequestBody PlayerDTO playerDTO) {
        System.out.println("DEBUG: Kontroler primio zahtev: " + playerDTO);
        PlayerDTO createdPlayer = playerService.createPlayer(playerDTO);
        return new ResponseEntity<>(createdPlayer, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        PlayerDTO playerDTO = playerService.getPlayerById(id);
        return ResponseEntity.ok(playerDTO);
    }

    @GetMapping("/find")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlayerDTO>> getPlayersByIds(
            @RequestParam("ids") List<Long> ids
    ) {
        List<PlayerDTO> players = playerService.getPlayersByIds(ids);
        return ResponseEntity.ok(players);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT_COACH', 'ADMIN')")
    public ResponseEntity<PlayerDTO> updatePlayer(@PathVariable Long id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO updatedPlayer = playerService.updatePlayer(id, playerDTO);
        return ResponseEntity.ok(updatedPlayer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ASSISTANT_COACH', 'ADMIN')")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlayerDTO>> searchPlayers(@RequestParam("keyword") String keyword) {
        List<PlayerDTO> players = playerService.searchPlayers(keyword);
        return ResponseEntity.ok(players);
    }

    @PostMapping("/advanced-search")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlayerDTO>> advancedSearch(@RequestBody() SearchParameters searchParameters) {
        return ResponseEntity.ok(playerService.advancedSearch(searchParameters));
    }

    @GetMapping("/compare")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlayerWithReportDTO>> getPlayersForComparison(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(playerService.getPlayersForComparison(ids));
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ASSISTANT_COACH', 'ADMIN')")
    public ResponseEntity<PlayerDTO> uploadPlayerImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        PlayerDTO updatedPlayer = playerService.uploadPlayerImage(id, file);
        return ResponseEntity.ok(updatedPlayer);
    }
}