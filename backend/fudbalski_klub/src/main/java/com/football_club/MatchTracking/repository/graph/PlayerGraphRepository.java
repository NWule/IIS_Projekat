package com.football_club.MatchTracking.repository.graph;

import com.football_club.MatchTracking.model.graph.PlayerGraph;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlayerGraphRepository extends Neo4jRepository<PlayerGraph, Long> {

}
