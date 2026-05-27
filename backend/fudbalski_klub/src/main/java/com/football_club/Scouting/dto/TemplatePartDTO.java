package com.football_club.Scouting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplatePartDTO {
    private Long id;
    private Long searchTemplateId;
    private Long metricId;
    private String metricName;
    private double weight;
}