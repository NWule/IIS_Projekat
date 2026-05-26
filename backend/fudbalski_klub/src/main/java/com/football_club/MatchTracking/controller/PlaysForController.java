package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.PlaysForDTO;
import com.football_club.MatchTracking.service.IPlaysForService;
import com.football_club.Auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class PlaysForController {

    private final IPlaysForService playsForService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> createContract(
            @RequestBody PlaysForDTO playsForDTO,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            if (user.getClubId() == null || user.getClubId() != playsForDTO.getClubId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        PlaysForDTO createdContract = playsForService.createContract(playsForDTO);
        return new ResponseEntity<>(createdContract, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<PlaysForDTO> getContractById(@PathVariable Long id) {
        PlaysForDTO contractDTO = playsForService.getContractById(id);
        return ResponseEntity.ok(contractDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> updateContract(
            @PathVariable Long id,
            @RequestBody PlaysForDTO playsForDTO,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            if (user.getClubId() == null || user.getClubId() != playsForDTO.getClubId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        PlaysForDTO updatedContract = playsForService.updateContract(id, playsForDTO);
        return ResponseEntity.ok(updatedContract);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        playsForService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/player/{playerId}/history")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<PlaysForDTO>> getPlayerHistory(@PathVariable Long playerId) {
        List<PlaysForDTO> history = playsForService.getPlayerHistory(playerId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/club/{clubId}/history")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> getClubHistory(
            @PathVariable int clubId,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            if (user.getClubId() == null || user.getClubId() != clubId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        List<PlaysForDTO> history = playsForService.getClubHistory(clubId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/player/{playerId}/current")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<PlaysForDTO> getCurrentContract(@PathVariable Long playerId) {
        PlaysForDTO currentContract = playsForService.getCurrentContract(playerId);
        return ResponseEntity.ok(currentContract);
    }

    @GetMapping("/club/{clubId}/roster")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> getCurrentRoster(
            @PathVariable int clubId,
            @AuthenticationPrincipal User user) {

        if (!user.getRole().name().equals("ROLE_ADMIN")) {
            if (user.getClubId() == null || user.getClubId() != clubId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        List<PlaysForDTO> roster = playsForService.getCurrentRoster(clubId);
        return ResponseEntity.ok(roster);
    }
}