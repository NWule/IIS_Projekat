package com.football_club.MatchTracking.model.graph;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node
@Getter @Setter
public class TacticalAnalysisGraph {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private String severity;
    private int matchMinute;

    @Relationship(type = "SOLVED_BY", direction = Relationship.Direction.OUTGOING)
    private TacticalRecommendationGraph recommendation;

    @Relationship(type = "ANALYSIS_OF_GAME", direction = Relationship.Direction.INCOMING)
    private GameGraph gameGraph;
}
