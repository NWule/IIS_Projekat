package com.football_club.Scouting.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.Auth.repository.UserRepository;
import com.football_club.Scouting.dto.SearchTemplateDTO;
import com.football_club.Scouting.dto.SearchTemplateSaveDTO;
import com.football_club.Scouting.dto.TemplatePartDTO;
import com.football_club.Scouting.dto.TemplatePartSaveDTO;
import com.football_club.Scouting.model.Metric;
import com.football_club.Scouting.model.SearchTemplate;
import com.football_club.Scouting.model.TemplatePart;
import com.football_club.Scouting.repository.MetricRepository;
import com.football_club.Scouting.repository.SearchTemplateRepository;
import com.football_club.Scouting.repository.TemplatePartRepository;
import com.football_club.Scouting.service.ISearchTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchTemplateService implements ISearchTemplateService {

    private final SearchTemplateRepository searchTemplateRepository;
    private final MetricRepository metricRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SearchTemplateDTO createTemplate(SearchTemplateSaveDTO dto, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException("Korisnik kreator nije pronađen sa ID-em: " + creatorId));

        SearchTemplate template = new SearchTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setCreator(creator);

        List<TemplatePart> templateParts = new ArrayList<>();
        if (dto.getParts() != null) {
            templateParts = createTemplateParts(dto.getParts(), template);
        }

        template.setTemplateParts(templateParts);

        SearchTemplate saved = searchTemplateRepository.save(template);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchTemplateDTO getTemplateById(Long id) {
        SearchTemplate template = searchTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Šablon pretrage sa ID-em " + id + " ne postoji."));
        return mapToDTO(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchTemplateDTO> getAllTemplates() {
        return searchTemplateRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SearchTemplateDTO updateTemplate(Long id, SearchTemplateSaveDTO dto) {
        SearchTemplate template = searchTemplateRepository.findWithPartsById(id)
                .orElseThrow(() -> new NoSuchElementException("Šablon pretrage sa ID-em " + id + " ne postoji."));

        template.setTemplateName(dto.getTemplateName());

        template.getTemplateParts().clear();
        searchTemplateRepository.saveAndFlush(template);

        List<TemplatePart> templateParts = new ArrayList<>();
        if (dto.getParts() != null) {
            templateParts = createTemplateParts(dto.getParts(), template);
        }

        template.getTemplateParts().addAll(templateParts);

        SearchTemplate saved = searchTemplateRepository.save(template);
        return mapToDTO(saved);
    }

    private List<TemplatePart> createTemplateParts(List<TemplatePartSaveDTO> parts, SearchTemplate template) {
        List<TemplatePart> templateParts = new ArrayList<>();
        for (TemplatePartSaveDTO part : parts) {
            Metric metric = metricRepository.findById(part.getMetricId())
                    .orElseThrow(() -> new NoSuchElementException("Metrika nije pronađena sa ID-em: " + part.getMetricId()));

            TemplatePart newPart = new TemplatePart();
            newPart.setMetric(metric);
            newPart.setWeight(part.getWeight());

            newPart.setSearchTemplate(template);

            templateParts.add(newPart);
        }
        return templateParts;
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        if (!searchTemplateRepository.existsById(id)) {
            throw new NoSuchElementException("Šablon pretrage sa ID-em " + id + " ne postoji.");
        }
        searchTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchTemplateDTO> getTemplatesByCreator(Long creatorId) {
        return searchTemplateRepository.findByCreatorId(creatorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private SearchTemplateDTO mapToDTO(SearchTemplate template) {
        List<TemplatePartDTO> parts = template.getTemplateParts() != null ?
                template.getTemplateParts().stream()
                        .map(part -> TemplatePartDTO.builder()
                                .id(part.getId())
                                .searchTemplateId(template.getId())
                                .metricId(part.getMetric().getId())
                                .metricName(part.getMetric().getName())
                                .weight(part.getWeight())
                                .build())
                        .collect(Collectors.toList()) : Collections.emptyList();

        return SearchTemplateDTO.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .creatorId(template.getCreator().getId())
                .creatorName(template.getCreator().getUsername())
                .parts(parts)
                .build();
    }
}