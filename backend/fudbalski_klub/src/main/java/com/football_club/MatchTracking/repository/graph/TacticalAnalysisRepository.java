package com.football_club.MatchTracking.repository.graph;

import com.football_club.MatchTracking.model.graph.TacticalAnalysisGraph;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacticalAnalysisRepository extends Neo4jRepository<TacticalAnalysisGraph, Long> {

    List<TacticalAnalysisGraph> findByGameGraphId(Long gameId);

}