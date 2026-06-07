package com.football_club.Scouting.dto;

import com.football_club.MatchTracking.dto.PlayerDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
    private Long id;
    private String name;
    private Long directorId;
    private List<PlayerDTO> players;
}
