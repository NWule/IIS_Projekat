package com.football_club.Scouting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoutRequestSaveDTO {
    private Long playerId;
    private String instructions;
    private LocalDateTime deadline;
}
