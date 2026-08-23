package com.football_club.MatchTracking.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameGraph {
    @Id
    private Long id;
    private String status;
    private LocalDateTime matchDate;

    private Double expectedHomeGoals;
    private Double expectedHomeShots;
    private Double expectedHomeShotsOnTarget;
    private Double expectedHomeFouls;
    private Double expectedHomeCorners;
    private Double expectedHomeOffsides;
    private Double expectedHomePassSuccessRate;

    private Double expectedAwayGoals;
    private Double expectedAwayShots;
    private Double expectedAwayShotsOnTarget;
    private Double expectedAwayFouls;
    private Double expectedAwayCorners;
    private Double expectedAwayOffsides;
    private Double expectedAwayPassSuccessRate;

    @Relationship(type = "HOME_CLUB", direction = Relationship.Direction.OUTGOING)
    private ClubGraph homeClub;

    @Relationship(type = "AWAY_CLUB", direction = Relationship.Direction.OUTGOING)
    private ClubGraph awayClub;
}
