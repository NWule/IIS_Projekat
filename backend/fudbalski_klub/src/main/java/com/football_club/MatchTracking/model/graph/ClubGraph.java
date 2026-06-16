package com.football_club.MatchTracking.model.graph;


import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClubGraph {
    @Id
    private Long id;

    private String name;
}
