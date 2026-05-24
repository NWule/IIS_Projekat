package com.football_club.MatchTracking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "home_goals", nullable = false)
    private int homeGoals = 0;

    @Column(name = "away_goals", nullable = false)
    private int awayGoals = 0;

    @Column(name = "home_shots", nullable = false)
    private int homeShots = 0;

    @Column(name = "away_shots", nullable = false)
    private int awayShots = 0;

    @Column(name = "home_possession", nullable = false)
    private double homePossession = 0.0;

    @Column(name = "away_possession", nullable = false)
    private double awayPossession = 0.0;

    @Column(name = "home_shots_on_target", nullable = false)
    private int homeShotsOnTarget = 0;

    @Column(name = "away_shots_on_target", nullable = false)
    private int awayShotsOnTarget = 0;

    @Column(name = "home_fouls", nullable = false)
    private int homeFouls = 0;

    @Column(name = "away_fouls", nullable = false)
    private int awayFouls = 0;

    @Column(name = "home_corners", nullable = false)
    private int homeCorners = 0;

    @Column(name = "away_corners", nullable = false)
    private int awayCorners = 0;

    @Column(name = "home_offsides", nullable = false)
    private int homeOffsides = 0;

    @Column(name = "away_offsides", nullable = false)
    private int awayOffsides = 0;

    @Column(name = "home_pass_success_rate")
    private double homePassSuccessRate = 0.0;

    @Column(name = "away_pass_success_rate")
    private double awayPassSuccessRate = 0.0;
}