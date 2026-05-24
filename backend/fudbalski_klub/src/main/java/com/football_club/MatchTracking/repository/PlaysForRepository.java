package com.football_club.MatchTracking.repository;

import com.football_club.MatchTracking.model.PlaysFor;
import com.football_club.MatchTracking.model.Player;
import com.football_club.MatchTracking.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlaysForRepository extends JpaRepository<PlaysFor, Long> {
    List<PlaysFor> findByPlayerId(Long playerId);

    List<PlaysFor> findByClubId(int clubId);

    List<PlaysFor> findByPlayerAndClub(Player player, Club club);

    @Query("SELECT p FROM PlaysFor p WHERE p.player.id = :playerId " +
            "AND p.contractStart <= :currentDate " +
            "AND (p.contractEnd IS NULL OR p.contractEnd >= :currentDate)")
    Optional<PlaysFor> findCurrentContract(@Param("playerId") Long playerId,
                                           @Param("currentDate") LocalDate currentDate);


    @Query("SELECT p FROM PlaysFor p WHERE p.club.id = :clubId " +
            "AND p.contractStart <= :currentDate " +
            "AND (p.contractEnd IS NULL OR p.contractEnd >= :currentDate)")
    List<PlaysFor> findCurrentRoster(@Param("clubId") int clubId,
                                     @Param("currentDate") LocalDate currentDate);
}