package com.football_club.MatchTracking.model.graph;

import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.football_club.MatchTracking.model.enums.PlayerPosition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Transient 
    private boolean isNewInstance = true;

    @Relationship(type = "PLAYS_FOR", direction = Relationship.Direction.OUTGOING)
    private ClubGraph clubGraph;
}