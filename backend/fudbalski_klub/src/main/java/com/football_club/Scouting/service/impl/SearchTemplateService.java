package com.football_club.Scouting.service.impl;

import com.football_club.Auth.model.User;
import com.football_club.Auth.repository.UserRepository;
import com.football_club.Scouting.dto.SearchTemplateDTO;
import com.football_club.Scouting.dto.SearchTemplateSaveDTO;
import com.football_club.Scouting.dto.TemplatePartDTO;
import com.football_club.Scouting.model.SearchTemplate;
import com.football_club.Scouting.repository.SearchTemplateRepository;
import com.football_club.Scouting.service.ISearchTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchTemplateService implements ISearchTemplateService {

    private final SearchTemplateRepository searchTemplateRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SearchTemplateDTO createTemplate(SearchTemplateSaveDTO dto, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new NoSuchElementException("Korisnik kreator nije pronađen sa ID-em: " + creatorId));

        SearchTemplate template = new SearchTemplate();
        template.setTemplateName(dto.getTemplateName());
        template.setCreator(creator);

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
        SearchTemplate template = searchTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Šablon pretrage sa ID-em " + id + " ne postoji."));

        template.setTemplateName(dto.getTemplateName());

        SearchTemplate updated = searchTemplateRepository.save(template);
        return mapToDTO(updated);
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