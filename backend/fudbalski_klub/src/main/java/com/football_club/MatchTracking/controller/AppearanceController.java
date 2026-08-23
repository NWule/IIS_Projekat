package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.AppearanceDTO;
import com.football_club.MatchTracking.dto.GameDTO;
import com.football_club.MatchTracking.dto.GameLineupResponseDTO;
import com.football_club.MatchTracking.service.IAppearanceService;
import com.football_club.MatchTracking.service.IGameService;
import com.football_club.Auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/appearances")
@RequiredArgsConstructor
public class AppearanceController {

    private final IAppearanceService appearanceService;
    private final IGameService gameService;

    @PostMapping
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'HEAD_COACH', 'ADMIN')")
    public ResponseEntity<?> createAppearance(
            @RequestBody AppearanceDTO appearanceDTO,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            GameDTO game = gameService.getGameById(appearanceDTO.getGameId());
            if (user.getClubId() == null || (game.getHomeClubId() != user.getClubId() && game.getAwayClubId() != user.getClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        AppearanceDTO createdAppearance = appearanceService.createAppearance(appearanceDTO);
        return new ResponseEntity<>(createdAppearance, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<AppearanceDTO> getAppearanceById(@PathVariable Long id) {
        AppearanceDTO appearanceDTO = appearanceService.getAppearanceById(id);
        return ResponseEntity.ok(appearanceDTO);
    }

    @GetMapping("/game/{gameId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<AppearanceDTO>> getAppearancesByGame(@PathVariable Long gameId) {
        List<AppearanceDTO> appearances = appearanceService.getAppearancesByGame(gameId);
        return ResponseEntity.ok(appearances);
    }

    @GetMapping("/player/{playsForId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<AppearanceDTO>> getAppearancesByPlayer(@PathVariable Long playsForId) {
        List<AppearanceDTO> appearances = appearanceService.getAppearancesByPlayer(playsForId);
        return ResponseEntity.ok(appearances);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'HEAD_COACH', 'ADMIN')")
    public ResponseEntity<?> updateAppearance(
            @PathVariable Long id,
            @RequestBody AppearanceDTO appearanceDTO,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            AppearanceDTO appearance = appearanceService.getAppearanceById(id);
            GameDTO game = gameService.getGameById(appearance.getGameId());
            if (user.getClubId() == null || (game.getHomeClubId() != user.getClubId() && game.getAwayClubId() != user.getClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        AppearanceDTO updatedAppearance = appearanceService.updateAppearance(id, appearanceDTO);
        return ResponseEntity.ok(updatedAppearance);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'ADMIN')")
    public ResponseEntity<?> deleteAppearance(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            AppearanceDTO appearance = appearanceService.getAppearanceById(id);
            GameDTO game = gameService.getGameById(appearance.getGameId());
            if (user.getClubId() == null || (game.getHomeClubId() != user.getClubId() && game.getAwayClubId() != user.getClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        appearanceService.deleteAppearance(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/game/{gameId}/lineup")
    @PreAuthorize("hasAnyRole('STATISTICIAN', 'HEAD_COACH', 'ADMIN')")
    public ResponseEntity<?> saveLineup(@PathVariable Long gameId, @RequestParam Long clubId,
            @RequestBody List<AppearanceDTO> lineupDTOs, @AuthenticationPrincipal User user) {
        GameDTO game = gameService.getGameById(gameId);

        if (clubId.intValue() != game.getHomeClubId() && clubId.intValue() != game.getAwayClubId()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Greška: Izabrani klub ne učestvuje u ovoj utakmici!");
        }

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            if (user.getClubId() == null || (user.getClubId() != game.getHomeClubId() && user.getClubId() != game.getAwayClubId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        GameLineupResponseDTO savedLineup = appearanceService.saveLineup(gameId, clubId, lineupDTOs);
        return ResponseEntity.ok(savedLineup);
    }

    @PostMapping("/upload-pdf/{clubId}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'ADMIN')")
    public ResponseEntity<List<AppearanceDTO>> uploadLineupPdf(
            @RequestParam("file") MultipartFile file,
            @PathVariable Integer clubId) {
        try {
            List<AppearanceDTO> parsedPlayers = appearanceService.parseLineupFromPdf(file, clubId);
            return ResponseEntity.ok(parsedPlayers);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}