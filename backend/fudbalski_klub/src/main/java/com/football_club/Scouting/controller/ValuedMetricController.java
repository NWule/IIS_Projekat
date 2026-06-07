package com.football_club.Scouting.controller;

import com.football_club.Scouting.dto.ValuedMetricDTO;
import com.football_club.Scouting.dto.ValuedMetricSaveDTO;
import com.football_club.Scouting.dto.ValuedMetricUpdateDTO;
import com.football_club.Scouting.service.IValuedMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valued-metrics")
@RequiredArgsConstructor
public class ValuedMetricController {

    private final IValuedMetricService valuedMetricService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ValuedMetricDTO> createValuedMetric(@RequestBody ValuedMetricSaveDTO dto) {
        ValuedMetricDTO created = valuedMetricService.createValuedMetric(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<List<ValuedMetricDTO>> createValuedMetrics(@RequestBody List<ValuedMetricSaveDTO> dtos) {
        List<ValuedMetricDTO> created = valuedMetricService.createValuedMetrics(dtos);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HEAD_COACH', 'ASSISTANT_COACH', 'SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<ValuedMetricDTO> getValuedMetricById(@PathVariable Long id) {
        ValuedMetricDTO metric = valuedMetricService.getValuedMetricById(id);
        return ResponseEntity.ok(metric);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<ValuedMetricDTO>> getAllValuedMetrics() {
        List<ValuedMetricDTO> metrics = valuedMetricService.getAllValuedMetrics();
        return ResponseEntity.ok(metrics);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ValuedMetricDTO> updateValuedMetric(@PathVariable Long id, @RequestBody ValuedMetricSaveDTO dto) {
        ValuedMetricDTO updated = valuedMetricService.updateValuedMetric(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/bulk")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<List<ValuedMetricDTO>> updateValuedMetrics(@RequestBody List<ValuedMetricUpdateDTO> dtos) {
        List<ValuedMetricDTO> updated = valuedMetricService.updateValuedMetrics(dtos);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<Void> deleteValuedMetric(@PathVariable Long id) {
        valuedMetricService.deleteValuedMetric(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasAnyRole('SCOUT', 'SPORTS_DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<ValuedMetricDTO>> getValuedMetricsByReportId(@PathVariable Long reportId) {
        List<ValuedMetricDTO> metrics = valuedMetricService.getValuedMetricsByReportId(reportId);
        return ResponseEntity.ok(metrics);
    }

    @PatchMapping("/report/{reportId}/metric/{metricId}")
    @PreAuthorize("hasAnyRole('SCOUT', 'ADMIN')")
    public ResponseEntity<ValuedMetricDTO> updateValueByReportAndMetric(
            @PathVariable Long reportId,
            @PathVariable Long metricId,
            @RequestParam("value") double newValue) {
        ValuedMetricDTO updated = valuedMetricService.updateValueByReportAndMetric(reportId, metricId, newValue);
        return ResponseEntity.ok(updated);
    }
}
