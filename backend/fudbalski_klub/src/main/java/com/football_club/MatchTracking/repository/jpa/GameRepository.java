package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByMatchDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Game> findByHomeClubIdOrAwayClubId(int homeClubId, int awayClubId);

    @Query("SELECT g FROM Game g WHERE " +
            "(g.homeClub.id = :club1Id AND g.awayClub.id = :club2Id) OR " +
            "(g.homeClub.id = :club2Id AND g.awayClub.id = :club1Id)")
    List<Game> findHeadToHeadMatches(@Param("club1Id") int club1Id, @Param("club2Id") int club2Id);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub WHERE g.matchDate BETWEEN :startDate AND :endDate")
    List<Game> findGamesWithClubsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub " +
            "WHERE g.matchDate >= :now ORDER BY g.matchDate ASC")
    List<Game> findUpcomingGames(@Param("now") LocalDateTime now);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub " +
            "WHERE g.status = com.football_club.MatchTracking.model.enums.GameStatus.UPCOMING " +
            "ORDER BY g.matchDate ASC")
    List<Game> findUpcomingGames();

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub " +
            "WHERE g.status = com.football_club.MatchTracking.model.enums.GameStatus.LIVE")
    List<Game> findLiveGames();

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub " +
            "WHERE g.status = com.football_club.MatchTracking.model.enums.GameStatus.PLAYED " +
            "ORDER BY g.matchDate DESC")
    List<Game> findPlayedGames();

    @Query("SELECT g FROM Game g JOIN FETCH g.homeClub JOIN FETCH g.awayClub " +
            "WHERE (g.homeClub.id = :clubId OR g.awayClub.id = :clubId) " +
            "AND g.status = :status")
    List<Game> findGamesByClubAndStatus(@Param("clubId") Long clubId, @Param("status") GameStatus status);
}