package com.football_club.Scouting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValuedMetricUpdateDTO {
    private Long id;
    private Long reportId;
    private Long metricId;
    private double value;
}
