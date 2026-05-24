package com.football_club.MatchTracking.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDTO {
    private Long id;
    private LocalDateTime matchDate;
    private int homeClubId;
    private String homeClubName;
    private int awayClubId;
    private String awayClubName;
}