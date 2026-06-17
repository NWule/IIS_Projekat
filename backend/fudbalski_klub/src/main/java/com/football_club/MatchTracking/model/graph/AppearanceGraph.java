package com.football_club.MatchTracking.model.graph;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppearanceGraph {
    @Id
    private Long id;

    private String matchRole;
    private int minutesPlayed;
    private int goals;
    private int assists;
    private int fouls;
    private int yellowCards;
    private boolean redCard;
    private double rating;
    private double passingAccuracy;

    @Relationship(type = "OF_PLAYER", direction = Relationship.Direction.OUTGOING)
    private PlayerGraph playerGraph;

    @Relationship(type = "IN_GAME", direction = Relationship.Direction.OUTGOING)
    private GameGraph gameGraph;
}
