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
public class GameGraph {
    @Id
    private Long id;
    private String status;

    @Relationship(type = "HOME_CLUB", direction = Relationship.Direction.OUTGOING)
    private ClubGraph homeClub;

    @Relationship(type = "AWAY_CLUB", direction = Relationship.Direction.OUTGOING)
    private ClubGraph awayClub;
}
