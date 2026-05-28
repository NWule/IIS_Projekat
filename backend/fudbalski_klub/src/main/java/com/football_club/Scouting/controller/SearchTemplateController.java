package com.football_club.Scouting.controller;

import com.football_club.Auth.model.User;
import com.football_club.Scouting.dto.SearchTemplateDTO;
import com.football_club.Scouting.dto.SearchTemplateSaveDTO;
import com.football_club.Scouting.service.ISearchTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-templates")
@RequiredArgsConstructor
public class SearchTemplateController {

    private final ISearchTemplateService searchTemplateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<SearchTemplateDTO> createTemplate(
            @RequestBody SearchTemplateSaveDTO dto,
            @AuthenticationPrincipal User userDetails) {
        SearchTemplateDTO created = searchTemplateService.createTemplate(dto, userDetails.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<SearchTemplateDTO> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(searchTemplateService.getTemplateById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<SearchTemplateDTO>> getAllTemplates() {
        return ResponseEntity.ok(searchTemplateService.getAllTemplates());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> updateTemplate(
            @PathVariable Long id,
            @RequestBody SearchTemplateSaveDTO dto,
            @AuthenticationPrincipal User userDetails) {

        if (!userDetails.getRole().name().equals("ROLE_ADMIN")) {
            SearchTemplateDTO existing = searchTemplateService.getTemplateById(id);
            if (!existing.getCreatorId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Nemate dozvolu da menjate tuđe šablone!");
            }
        }

        return ResponseEntity.ok(searchTemplateService.updateTemplate(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<?> deleteTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal User userDetails) {

        if (!userDetails.getRole().name().equals("ROLE_ADMIN")) {
            SearchTemplateDTO existing = searchTemplateService.getTemplateById(id);
            if (!existing.getCreatorId().equals(userDetails.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Nemate dozvolu da obrišete tuđi šablon!");
            }
        }

        searchTemplateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<SearchTemplateDTO>> getMyTemplates(@AuthenticationPrincipal User userDetails) {
        return ResponseEntity.ok(searchTemplateService.getTemplatesByCreator(userDetails.getId()));
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<SearchTemplateDTO>> getTemplatesByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(searchTemplateService.getTemplatesByCreator(creatorId));
    }
}