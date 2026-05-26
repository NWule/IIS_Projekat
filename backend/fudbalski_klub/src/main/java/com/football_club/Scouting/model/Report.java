package com.football_club.Scouting.model;

import com.football_club.Auth.model.User;
import com.football_club.MatchTracking.model.Club;
import com.football_club.MatchTracking.model.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scout_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scout_id", nullable = false)
    private User scout;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "overall_commentary", nullable = true)
    private String overallCommentary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_at_time_id")
    private Club clubAtTime;

    @Column(name = "league_multiplier_at_time", nullable = false)
    private double leagueMultiplierAtTime;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    private List<ValuedMetric> valuedMetrics;

}
