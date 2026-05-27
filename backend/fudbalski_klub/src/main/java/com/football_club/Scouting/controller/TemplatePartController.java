package com.football_club.Scouting.controller;

import com.football_club.Scouting.dto.TemplatePartDTO;
import com.football_club.Scouting.dto.TemplatePartSaveDTO;
import com.football_club.Scouting.service.ITemplatePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/template-parts")
@RequiredArgsConstructor
public class TemplatePartController {

    private final ITemplatePartService templatePartService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<TemplatePartDTO> createTemplatePart(@RequestBody TemplatePartSaveDTO dto) {
        TemplatePartDTO created = templatePartService.createTemplatePart(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<TemplatePartDTO> getTemplatePartById(@PathVariable Long id) {
        return ResponseEntity.ok(templatePartService.getTemplatePartById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<TemplatePartDTO>> getAllTemplateParts() {
        return ResponseEntity.ok(templatePartService.getAllTemplateParts());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<TemplatePartDTO> updateTemplatePart(@PathVariable Long id, @RequestBody TemplatePartSaveDTO dto) {
        return ResponseEntity.ok(templatePartService.updateTemplatePart(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteTemplatePart(@PathVariable Long id) {
        templatePartService.deleteTemplatePart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/template/{templateId}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<TemplatePartDTO>> getPartsByTemplate(@PathVariable Long templateId) {
        return ResponseEntity.ok(templatePartService.getPartsByTemplate(templateId));
    }
}