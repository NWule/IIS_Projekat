package com.football_club.Scouting.model;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.Player;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "game_metric_values", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "player_id", "metric_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_id", nullable = false)
    private Metric metric;

    @Column(name = "recorded_value", nullable = false)
    private double recordedValue;
}
