package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.MetricDTO;
import com.football_club.Scouting.dto.MetricSaveDTO;

import java.util.List;

public interface IMetricService {
    MetricDTO createMetric(MetricSaveDTO dto);
    MetricDTO getMetricById(Long id);
    List<MetricDTO> getAllMetrics();
    MetricDTO updateMetric(Long id, MetricSaveDTO dto);
    void deleteMetric(Long id);
}
