package com.football_club.Scouting.dto;

import com.football_club.Scouting.model.enums.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoutRequestDTO {
    private Long id;
    private Long directorId;
    private String directorName;
    private Long scoutId;
    private String scoutName;
    private Long playerId;
    private String playerName;
    private String playerSurname;
    private LocalDateTime requestDate;
    private String instructions;
    private LocalDateTime deadline;
    private RequestStatus status;
}
