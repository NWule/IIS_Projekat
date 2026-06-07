package com.football_club.Scouting.service.impl;

import com.football_club.Scouting.dto.ValuedMetricDTO;
import com.football_club.Scouting.dto.ValuedMetricSaveDTO;
import com.football_club.Scouting.dto.ValuedMetricUpdateDTO;
import com.football_club.Scouting.model.Metric;
import com.football_club.Scouting.model.Report;
import com.football_club.Scouting.model.ValuedMetric;
import com.football_club.Scouting.repository.MetricRepository;
import com.football_club.Scouting.repository.ReportRepository;
import com.football_club.Scouting.repository.ValuedMetricRepository;
import com.football_club.Scouting.service.IValuedMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValuedMetricService implements IValuedMetricService {

    private final ValuedMetricRepository valuedMetricRepository;
    private final ReportRepository reportRepository;
    private final MetricRepository metricRepository;

    @Override
    @Transactional
    public ValuedMetricDTO createValuedMetric(ValuedMetricSaveDTO dto) {
        if (valuedMetricRepository.existsByReportIdAndMetricId(dto.getReportId(), dto.getMetricId())) {
            throw new IllegalArgumentException("Ocenjena metrika već postoji za ovaj izveštaj!");
        }

        Report report = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new NoSuchElementException("Izveštaj nije pronađen sa ID-em: " + dto.getReportId()));

        Metric metric = metricRepository.findById(dto.getMetricId())
                .orElseThrow(() -> new NoSuchElementException("Metrika nije pronađena sa ID-em: " + dto.getMetricId()));

        ValuedMetric valuedMetric = new ValuedMetric();
        valuedMetric.setReport(report);
        valuedMetric.setMetric(metric);
        valuedMetric.setValue(dto.getValue());

        ValuedMetric saved = valuedMetricRepository.save(valuedMetric);
        return mapToDTO(saved);
    }

    public List<ValuedMetricDTO> createValuedMetrics(List<ValuedMetricSaveDTO> dtos) {
        return dtos.stream()
                .map(this::createValuedMetric)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ValuedMetricDTO getValuedMetricById(Long id) {
        ValuedMetric valuedMetric = valuedMetricRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ocenjena metrika nije pronađena sa ID-em: " + id));
        return mapToDTO(valuedMetric);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValuedMetricDTO> getAllValuedMetrics() {
        return valuedMetricRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ValuedMetricDTO updateValuedMetric(Long id, ValuedMetricSaveDTO dto) {
        ValuedMetric valuedMetric = valuedMetricRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ocenjena metrika nije pronađena sa ID-em: " + id));

        Report report = reportRepository.findById(dto.getReportId())
                .orElseThrow(() -> new NoSuchElementException("Izveštaj nije pronađen sa ID-em: " + dto.getReportId()));

        Metric metric = metricRepository.findById(dto.getMetricId())
                .orElseThrow(() -> new NoSuchElementException("Metrika nije pronađena sa ID-em: " + dto.getMetricId()));

        valuedMetric.setReport(report);
        valuedMetric.setMetric(metric);
        valuedMetric.setValue(dto.getValue());

        ValuedMetric updated = valuedMetricRepository.save(valuedMetric);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public List<ValuedMetricDTO> updateValuedMetrics(List<ValuedMetricUpdateDTO> dtos) {
        return dtos.stream()
                .map(dto -> updateValuedMetric(dto.getId(), new ValuedMetricSaveDTO(dto.getReportId(), dto.getMetricId(), dto.getValue())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteValuedMetric(Long id) {
        if (!valuedMetricRepository.existsById(id)) {
            throw new NoSuchElementException("Ocenjena metrika sa ID-em " + id + " ne postoji.");
        }
        valuedMetricRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValuedMetricDTO> getValuedMetricsByReportId(Long reportId) {
        return valuedMetricRepository.findByReportId(reportId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ValuedMetricDTO updateValueByReportAndMetric(Long reportId, Long metricId, double newValue) {
        ValuedMetric valuedMetric = valuedMetricRepository.findByReportIdAndMetricId(reportId, metricId)
                .orElseThrow(() -> new NoSuchElementException("Nije pronađena ocenjena metrika za dati izveštaj i metriku."));

        valuedMetric.setValue(newValue);
        ValuedMetric updated = valuedMetricRepository.save(valuedMetric);
        return mapToDTO(updated);
    }

    private ValuedMetricDTO mapToDTO(ValuedMetric valuedMetric) {
        return ValuedMetricDTO.builder()
                .id(valuedMetric.getId())
                .reportId(valuedMetric.getReport().getId())
                .metricId(valuedMetric.getMetric().getId())
                .metricName(valuedMetric.getMetric().getName())
                .value(valuedMetric.getValue())
                .build();
    }
}
