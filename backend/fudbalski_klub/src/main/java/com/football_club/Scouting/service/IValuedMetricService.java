package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.ValuedMetricDTO;
import com.football_club.Scouting.dto.ValuedMetricSaveDTO;

import java.util.List;

public interface IValuedMetricService {
    ValuedMetricDTO createValuedMetric(ValuedMetricSaveDTO dto);
    ValuedMetricDTO getValuedMetricById(Long id);
    List<ValuedMetricDTO> getAllValuedMetrics();
    ValuedMetricDTO updateValuedMetric(Long id, ValuedMetricSaveDTO dto);
    void deleteValuedMetric(Long id);
    List<ValuedMetricDTO> getValuedMetricsByReportId(Long reportId);
    ValuedMetricDTO updateValueByReportAndMetric(Long reportId, Long metricId, double newValue);
}
