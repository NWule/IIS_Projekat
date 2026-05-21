package com.football_club.MatchTracking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location")
    private String Location;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "draws", nullable = false)
    private int draws = 0;

    @Column(name = "goals_scored", nullable = false)
    private int goalsScored = 0;

    @Column(name = "goals_conceded", nullable = false)
    private int goalsConceded = 0;
}
