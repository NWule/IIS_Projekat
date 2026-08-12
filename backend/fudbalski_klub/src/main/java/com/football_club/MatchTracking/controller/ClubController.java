package com.football_club.MatchTracking.controller;

import com.football_club.MatchTracking.dto.ClubDTO;
import com.football_club.MatchTracking.service.ICLubService;
import com.football_club.Auth.model.User; // Prilagodi paket
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ICLubService clubService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT_COACH')")
    public ResponseEntity<ClubDTO> createClub(@RequestBody ClubDTO clubDTO) {
        ClubDTO createdClub = clubService.createClub(clubDTO);
        return new ResponseEntity<>(createdClub, HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR')")
    public ResponseEntity<?> getMyClub(@AuthenticationPrincipal User userDetails) {
        if (userDetails.getClubId() == null) {
            return ResponseEntity.badRequest().body("Korisnik nije povezan sa klubom.");
        }
        ClubDTO clubDTO = clubService.getClubById(userDetails.getClubId());
        return ResponseEntity.ok(clubDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'STATISTICIAN', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<ClubDTO> getClubById(@PathVariable int id) {
        ClubDTO clubDTO = clubService.getClubById(id);
        return ResponseEntity.ok(clubDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClubDTO>> getAllClubs() {
        List<ClubDTO> clubs = clubService.getAllClubs();
        return ResponseEntity.ok(clubs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR','ASSISTANT_COACH', 'ADMIN')")
    public ResponseEntity<?> updateClub(
            @PathVariable int id,
            @RequestBody ClubDTO clubDTO,
            @AuthenticationPrincipal User userDetails) {



        ClubDTO updatedClub = clubService.updateClub(id, clubDTO);
        return ResponseEntity.ok(updatedClub);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT_COACH')")
    public ResponseEntity<Void> deleteClub(@PathVariable int id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/league-table")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN', 'BUYER')")
    public ResponseEntity<List<ClubDTO>> getLeagueTable() {
        List<ClubDTO> table = clubService.getLeagueTable();
        return ResponseEntity.ok(table);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<ClubDTO>> searchClubsByName(@RequestParam("name") String name) {
        List<ClubDTO> clubs = clubService.searchClubsByName(name);
        return ResponseEntity.ok(clubs);
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTANT_COACH', 'SPORTS_DIRECTOR')")
    public ResponseEntity<ClubDTO> uploadClubImage(
            @PathVariable int id,
            @RequestParam("file") MultipartFile file) {

        ClubDTO updatedClub = clubService.uploadClubImage(id, file);
        return ResponseEntity.ok(updatedClub);
    }
}