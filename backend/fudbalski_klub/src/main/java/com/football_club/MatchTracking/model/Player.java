package com.football_club.MatchTracking.model;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import com.football_club.Scouting.model.Report;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "players", indexes = {
        @Index(name = "idx_player_position", columnList = "position")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private PlayerPosition position;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<PlaysFor> memberships;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Report> reports;
}