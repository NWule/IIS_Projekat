package com.football_club.Scouting.service;

import com.football_club.Scouting.dto.TemplatePartDTO;
import com.football_club.Scouting.dto.TemplatePartSaveDTO;

import java.util.List;

public interface ITemplatePartService {
    TemplatePartDTO createTemplatePart(TemplatePartSaveDTO dto);
    TemplatePartDTO getTemplatePartById(Long id);
    List<TemplatePartDTO> getAllTemplateParts();
    TemplatePartDTO updateTemplatePart(Long id, TemplatePartSaveDTO dto);
    void deleteTemplatePart(Long id);
    List<TemplatePartDTO> getPartsByTemplate(Long templateId);
}