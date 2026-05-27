package com.football_club.Scouting.service.impl;

import com.football_club.Scouting.dto.TemplatePartDTO;
import com.football_club.Scouting.dto.TemplatePartSaveDTO;
import com.football_club.Scouting.model.Metric;
import com.football_club.Scouting.model.SearchTemplate;
import com.football_club.Scouting.model.TemplatePart;
import com.football_club.Scouting.repository.MetricRepository;
import com.football_club.Scouting.repository.SearchTemplateRepository;
import com.football_club.Scouting.repository.TemplatePartRepository;
import com.football_club.Scouting.service.ITemplatePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplatePartService implements ITemplatePartService {

    private final TemplatePartRepository templatePartRepository;
    private final SearchTemplateRepository searchTemplateRepository;
    private final MetricRepository metricRepository;

    @Override
    @Transactional
    public TemplatePartDTO createTemplatePart(TemplatePartSaveDTO dto) {
        if (templatePartRepository.existsBySearchTemplateIdAndMetricId(dto.getSearchTemplateId(), dto.getMetricId())) {
            throw new IllegalArgumentException("Metrika je već dodata u ovaj šablon pretrage!");
        }

        SearchTemplate template = searchTemplateRepository.findById(dto.getSearchTemplateId())
                .orElseThrow(() -> new NoSuchElementException("Šablon nije pronađen sa ID-em: " + dto.getSearchTemplateId()));

        Metric metric = metricRepository.findById(dto.getMetricId())
                .orElseThrow(() -> new NoSuchElementException("Metrika nije pronađena sa ID-em: " + dto.getMetricId()));

        TemplatePart part = new TemplatePart();
        part.setSearchTemplate(template);
        part.setMetric(metric);
        part.setWeight(dto.getWeight());

        TemplatePart saved = templatePartRepository.save(part);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TemplatePartDTO getTemplatePartById(Long id) {
        TemplatePart part = templatePartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stavka šablona nije pronađena sa ID-em: " + id));
        return mapToDTO(part);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplatePartDTO> getAllTemplateParts() {
        return templatePartRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TemplatePartDTO updateTemplatePart(Long id, TemplatePartSaveDTO dto) {
        TemplatePart part = templatePartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stavka šablona nije pronađena sa ID-em: " + id));

        SearchTemplate template = searchTemplateRepository.findById(dto.getSearchTemplateId())
                .orElseThrow(() -> new NoSuchElementException("Šablon nije pronađen."));

        Metric metric = metricRepository.findById(dto.getMetricId())
                .orElseThrow(() -> new NoSuchElementException("Metrika nije pronađena."));

        part.setSearchTemplate(template);
        part.setMetric(metric);
        part.setWeight(dto.getWeight());

        TemplatePart updated = templatePartRepository.save(part);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteTemplatePart(Long id) {
        if (!templatePartRepository.existsById(id)) {
            throw new NoSuchElementException("Stavka šablona sa ID-em " + id + " ne postoji.");
        }
        templatePartRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplatePartDTO> getPartsByTemplate(Long templateId) {
        return templatePartRepository.findBySearchTemplateId(templateId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TemplatePartDTO mapToDTO(TemplatePart part) {
        return TemplatePartDTO.builder()
                .id(part.getId())
                .searchTemplateId(part.getSearchTemplate().getId())
                .metricId(part.getMetric().getId())
                .metricName(part.getMetric().getName())
                .weight(part.getWeight())
                .build();
    }
}