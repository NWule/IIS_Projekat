package com.football_club.Scouting.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchTemplateDTO {
    private Long id;
    private String templateName;
    private Long creatorId;
    private String creatorName;
    private List<TemplatePartDTO> parts;
}