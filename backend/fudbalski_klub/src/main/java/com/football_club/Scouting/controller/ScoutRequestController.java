package com.football_club.Scouting.controller;

import com.football_club.Auth.model.User;
import com.football_club.Scouting.dto.ScoutRequestDTO;
import com.football_club.Scouting.dto.ScoutRequestSaveDTO;
import com.football_club.Scouting.service.IScoutRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scout-requests")
@RequiredArgsConstructor
public class ScoutRequestController {

    private final IScoutRequestService scoutRequestService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> createRequest(
            @RequestBody ScoutRequestSaveDTO dto,
            @AuthenticationPrincipal User userDetails) {
        ScoutRequestDTO created = scoutRequestService.createRequest(dto, userDetails.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(scoutRequestService.getRequestById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<ScoutRequestDTO>> getAllRequests() {
        return ResponseEntity.ok(scoutRequestService.getAllRequests());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> updateRequest(
            @PathVariable Long id,
            @RequestBody ScoutRequestSaveDTO dto,
            @AuthenticationPrincipal User userDetails) {

        if (!userDetails.getRole().name().equals("ROLE_ADMIN")) {
            ScoutRequestDTO existing = scoutRequestService.getRequestById(id);
            if (!existing.getDirectorId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Nemate dozvolu da menjate tuđe zahteve!");
            }
        }

        return ResponseEntity.ok(scoutRequestService.updateRequest(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> deleteRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {

        if (!userDetails.getRole().name().equals("ROLE_ADMIN")) {
            ScoutRequestDTO existing = scoutRequestService.getRequestById(id);
            if (!existing.getDirectorId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Nemate dozvolu da obrišete tuđe zahteve!");
            }
        }

        scoutRequestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unclaimed")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<List<ScoutRequestDTO>> getUnclaimedRequests() {
        return ResponseEntity.ok(scoutRequestService.getUnclaimedRequests());
    }

    @PostMapping("/{id}/claim")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> claimRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {
        ScoutRequestDTO claimed = scoutRequestService.claimRequest(id, userDetails.getId());
        return ResponseEntity.ok(claimed);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> cancelRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {
        ScoutRequestDTO canceled = scoutRequestService.cancelRequest(id, userDetails.getId());
        return ResponseEntity.ok(canceled);
    }

    @PostMapping("/{id}/cancel-by-director")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> cancelRequestByDirector(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {
        ScoutRequestDTO canceled = scoutRequestService.directorCancelRequest(id, userDetails.getId());
        return ResponseEntity.ok(canceled);
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ScoutRequestDTO> completeRequest(@PathVariable Long id) {
        ScoutRequestDTO completed = scoutRequestService.completeRequest(id);
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/scout")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<List<ScoutRequestDTO>> getRequestsByScout(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(scoutRequestService.getRequestsByScout(userDetails.getId()));
    }

    @GetMapping("/director")
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<ScoutRequestDTO>> getRequestsByDirector(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(scoutRequestService.getRequestsByDirector(userDetails.getId()));
    }
}
