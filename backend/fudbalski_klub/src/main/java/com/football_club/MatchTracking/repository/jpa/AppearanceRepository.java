package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.Appearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppearanceRepository extends JpaRepository<Appearance, Long> {

    List<Appearance> findByGameId(Long gameId);

    List<Appearance> findByPlaysForId(Long playsForId);

    Optional<Appearance> findByPlaysForIdAndGameId(Long playsForId, Long gameId);

    @Query("SELECT a FROM Appearance a JOIN FETCH a.playsFor pf JOIN FETCH pf.player WHERE a.game.id = :gameId")
    List<Appearance> findAppearancesWithPlayerInfoByGameId(@Param("gameId") Long gameId);


}