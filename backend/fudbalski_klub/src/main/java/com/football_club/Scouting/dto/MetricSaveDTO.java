package com.football_club.Scouting.dto;

import com.football_club.Scouting.model.enums.MetricCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MetricSaveDTO {
    private String name;
    private MetricCategory category;
}
