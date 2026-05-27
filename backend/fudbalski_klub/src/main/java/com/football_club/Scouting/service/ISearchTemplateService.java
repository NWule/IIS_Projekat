package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.SearchTemplateDTO;
import com.football_club.Scouting.dto.SearchTemplateSaveDTO;

import java.util.List;

public interface ISearchTemplateService {
    SearchTemplateDTO createTemplate(SearchTemplateSaveDTO dto, Long creatorId);
    SearchTemplateDTO getTemplateById(Long id);
    List<SearchTemplateDTO> getAllTemplates();
    SearchTemplateDTO updateTemplate(Long id, SearchTemplateSaveDTO dto);
    void deleteTemplate(Long id);
    List<SearchTemplateDTO> getTemplatesByCreator(Long creatorId);
}