package com.football_club.MatchTracking.model.graph;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@Setter
public class TacticalRecommendationGraph {
    @Id
    @GeneratedValue
    private Long id;
    private String recommendationText;
}
