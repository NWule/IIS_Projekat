package com.football_club.Scouting.service.impl;

import com.football_club.Scouting.dto.MetricDTO;
import com.football_club.Scouting.dto.MetricSaveDTO;
import com.football_club.Scouting.model.Metric;
import com.football_club.Scouting.repository.MetricRepository;
import com.football_club.Scouting.service.IMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MetricService implements IMetricService {

    private final MetricRepository metricRepository;

    @Override
    public MetricDTO createMetric(MetricSaveDTO dto) {
        Metric metric = new Metric();
        metric.setName(dto.getName());
        metric.setCategory(dto.getCategory());
        metric.setType(dto.getType());

        Metric savedMetric = metricRepository.save(metric);
        return convertToDTO(savedMetric);
    }

    @Override
    @Transactional(readOnly = true)
    public MetricDTO getMetricById(Long id) {
        Metric metric = metricRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Metric sa ID-em " + id + " nije pronađen."));
        return convertToDTO(metric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetricDTO> getAllMetrics() {
        return metricRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MetricDTO updateMetric(Long id, MetricSaveDTO dto) {
        Metric metric = metricRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Metric sa ID-em " + id + " nije pronađen."));

        metric.setName(dto.getName());
        metric.setCategory(dto.getCategory());
        metric.setType(dto.getType());

        Metric updatedMetric = metricRepository.save(metric);
        return convertToDTO(updatedMetric);
    }

    @Override
    public void deleteMetric(Long id) {
        if (!metricRepository.existsById(id)) {
            throw new NoSuchElementException("Metric sa ID-em " + id + " ne postoji.");
        }
        metricRepository.deleteById(id);
    }

    private MetricDTO convertToDTO(Metric metric) {
        return new MetricDTO(metric.getId(), metric.getName(), metric.getCategory(), metric.getType());
    }
}
