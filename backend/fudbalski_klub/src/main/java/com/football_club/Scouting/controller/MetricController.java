package com.football_club.Scouting.controller;

import com.football_club.Scouting.dto.MetricDTO;
import com.football_club.Scouting.dto.MetricSaveDTO;
import com.football_club.Scouting.service.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final IMetricService metricService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<MetricDTO> createMetric(@RequestBody MetricSaveDTO metricSaveDTO) {
        MetricDTO createdMetric = metricService.createMetric(metricSaveDTO);
        return new ResponseEntity<>(createdMetric, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<MetricDTO> getMetricById(@PathVariable Long id) {
        MetricDTO metricDTO = metricService.getMetricById(id);
        return ResponseEntity.ok(metricDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<MetricDTO>> getAllMetrics() {
        List<MetricDTO> metrics = metricService.getAllMetrics();
        return ResponseEntity.ok(metrics);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<MetricDTO> updateMetric(@PathVariable Long id, @RequestBody MetricSaveDTO metricSaveDTO) {
        MetricDTO updatedMetric = metricService.updateMetric(id, metricSaveDTO);
        return ResponseEntity.ok(updatedMetric);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteMetric(@PathVariable Long id) {
        metricService.deleteMetric(id);
        return ResponseEntity.noContent().build();
    }
}
