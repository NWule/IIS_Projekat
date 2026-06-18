package com.football_club.Scouting.dto;

import com.football_club.Scouting.model.enums.MetricCategory;
import com.football_club.Scouting.model.enums.MetricType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MetricDTO {
    private Long id;
    private String name;
    private MetricCategory category;
    private MetricType type;
}
