package com.football_club.MatchTracking.repository.graph;

import com.football_club.MatchTracking.model.graph.AppearanceGraph;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppearanceGraphRepository extends Neo4jRepository<AppearanceGraph, Long> {
}
