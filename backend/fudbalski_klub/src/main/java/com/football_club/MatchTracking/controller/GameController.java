package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.GameDTO;
import com.football_club.MatchTracking.service.IGameService;
import com.football_club.Auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final IGameService gameService;

    @PostMapping
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> createGame(@RequestBody GameDTO gameDTO, @AuthenticationPrincipal User userDetails) {


        GameDTO createdGame = gameService.createGame(gameDTO);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH','ASSISTANT_COACH', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> updateGame(@PathVariable Long id, @RequestBody GameDTO gameDTO, @AuthenticationPrincipal User userDetails) {


        GameDTO updatedGame = gameService.updateGame(id, gameDTO);
        return ResponseEntity.ok(updatedGame);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> deleteGame(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {

        GameDTO game = gameService.getGameById(id);



        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-club")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR')")
    public ResponseEntity<?> getMyClubGames(@AuthenticationPrincipal User userDetails) {
        if (userDetails.getClubId() == null) {
            return ResponseEntity.badRequest().body("Korisnik nije povezan ni sa jednim klubom (Admin treba da koristi opšte rute).");
        }

        List<GameDTO> games = gameService.getGamesByClub(userDetails.getClubId());
        return ResponseEntity.ok(games);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<GameDTO> getGameById(@PathVariable Long id) {
        GameDTO gameDTO = gameService.getGameById(id);
        return ResponseEntity.ok(gameDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getAllGames() {
        List<GameDTO> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/period")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getGamesInPeriod(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<GameDTO> games = gameService.getGamesInPeriod(startDate, endDate);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/club/{clubId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getGamesByClub(@PathVariable int clubId) {
        List<GameDTO> games = gameService.getGamesByClub(clubId);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/head-to-head")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getHeadToHeadMatches(
            @RequestParam("club1") int club1Id,
            @RequestParam("club2") int club2Id) {
        List<GameDTO> matches = gameService.getHeadToHeadMatches(club1Id, club2Id);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getUpcomingGames() {
        List<GameDTO> games = gameService.getUpcomingGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/live")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getLiveGames() {
        List<GameDTO> games = gameService.getLiveGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/played")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<GameDTO>> getPlayedGames() {
        List<GameDTO> games = gameService.getPlayedGames();
        return ResponseEntity.ok(games);
    }
}