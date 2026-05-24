package com.football_club.MatchTracking.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
}