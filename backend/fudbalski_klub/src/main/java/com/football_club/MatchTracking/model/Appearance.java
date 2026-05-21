package com.football_club.MatchTracking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appearances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plays_for_id", nullable = false)
    private PlaysFor playsFor;

    @Column(name = "minutes_played", nullable = false)
    private int minutesPlayed = 0;

    @Column(name = "goals", nullable = false)
    private int goals = 0;

    @Column(name = "assists", nullable = false)
    private int assists = 0;

    @Column(name = "fouls", nullable = false)
    private int fouls = 0;

    @Column(name = "yellow_cards", nullable = false)
    private int yellowCards = 0;

    @Column(name = "red_card", nullable = false)
    private boolean redCard = false;

    @Column(name = "rating")
    private double rating = 0.0;

    @Column(name = "passing_accuracy")
    private double passingAccuracy = 0.0;
}