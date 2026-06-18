package com.football_club.Scouting.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchTemplateSaveDTO {
    private String templateName;
    private List<TemplatePartSaveDTO> parts;
}