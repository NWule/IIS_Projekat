package com.football_club.MatchTracking.dto;

import com.football_club.Scouting.dto.ReportDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerWithReportDTO {
    private PlayerDTO player;
    private ReportDTO latestReport;
}
