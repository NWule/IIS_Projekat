package com.football_club.MatchTracking.repository.graph;

import com.football_club.MatchTracking.model.graph.GameGraph;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameGraphRepository extends Neo4jRepository<GameGraph, Long> {

    interface ClubFormAverages {
        Double getAvgGoals();
        Double getAvgShots();
        Double getAvgShotsOnTarget();
        Double getAvgFouls();
        Double getAvgCorners();
        Double getAvgOffsides();
        Double getAvgPassSuccessRate();
    }

    @Query("MATCH (c:ClubGraph {id: $clubId})<-[r:HOME_CLUB|AWAY_CLUB]-(g:GameGraph)-[:STATS_FOR_GAME]->(ts:TeamStatisticGraph) " +
            "WHERE g.status = 'PLAYED' " +
            "WITH g, ts, type(r) AS role " +
            "ORDER BY g.matchDate DESC LIMIT 5 " +
            "RETURN " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeGoals ELSE ts.awayGoals END) AS avgGoals, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeShots ELSE ts.awayShots END) AS avgShots, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeShotsOnTarget ELSE ts.awayShotsOnTarget END) AS avgShotsOnTarget, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeFouls ELSE ts.awayFouls END) AS avgFouls, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeCorners ELSE ts.awayCorners END) AS avgCorners, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homeOffsides ELSE ts.awayOffsides END) AS avgOffsides, " +
            "avg(CASE WHEN role = 'HOME_CLUB' THEN ts.homePassSuccessRate ELSE ts.awayPassSuccessRate END) AS avgPassSuccessRate")
    ClubFormAverages calculateClubFormForLast5Games(@Param("clubId") Long clubId);
}
