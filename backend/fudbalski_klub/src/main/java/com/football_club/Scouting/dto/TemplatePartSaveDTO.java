package com.football_club.Scouting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplatePartSaveDTO {
    private Long searchTemplateId;
    private Long metricId;
    private double weight;
}