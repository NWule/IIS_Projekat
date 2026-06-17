package com.football_club.MatchTracking.model.graph;

import com.football_club.MatchTracking.model.enums.PlayerPosition;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGraph {
    @Id
    private Long playerId;
    private String name;
    private String surname;
    private PlayerPosition position;

    @Relationship(type = "PLAYS_FOR", direction = Relationship.Direction.OUTGOING)
    private ClubGraph clubGraph;
}