package com.football_club.MatchTracking.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatisticGraph {
    @Id
    private Long id;

    private int homeGoals;
    private int awayGoals;
    private int homeShots;
    private int awayShots;
    //private double homePossession;
    //private double awayPossession;
    private int homeShotsOnTarget;
    private int awayShotsOnTarget;
    private int homeFouls;
    private int awayFouls;
    private int homeCorners;
    private int awayCorners;
    private int homeOffsides;
    private int awayOffsides;
    private double homePassSuccessRate;
    private double awayPassSuccessRate;

    @JsonIgnore
    @Relationship(type = "STATS_FOR_GAME", direction = Relationship.Direction.OUTGOING)
    private GameGraph gameGraph;
}
