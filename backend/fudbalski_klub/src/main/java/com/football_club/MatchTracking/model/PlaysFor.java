package com.football_club.MatchTracking.model;

import jakarta.persistence.*; // Ili javax.persistence.* ako koristiš stariji Spring Boot
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "plays_for")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaysFor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "jersey_number")
    private int jerseyNumber;

    @Column(name = "contract_start")
    private LocalDate contractStart;

    @Column(name = "contract_end")
    private LocalDate contractEnd;
}