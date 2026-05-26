package com.football_club.Scouting.model;

import com.football_club.MatchTracking.model.Club;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "difficulty_multiplier", nullable = false)
    private double difficultyMultiplier;

    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL)
    private List<Club> clubs;
}
