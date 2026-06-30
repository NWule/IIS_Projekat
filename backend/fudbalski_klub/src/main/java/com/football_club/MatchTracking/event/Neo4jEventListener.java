package com.football_club.MatchTracking.event;

import com.football_club.MatchTracking.model.Appearance;
import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.model.TeamStatistic;
import com.football_club.MatchTracking.model.graph.*;
import com.football_club.MatchTracking.repository.graph.*;
import com.football_club.MatchTracking.repository.jpa.AppearanceRepository;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.MatchTracking.repository.jpa.TeamStatisticRepository;
import com.football_club.MatchTracking.service.ITacticalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;


import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class Neo4jEventListener {

    private final PlayerGraphRepository playerGraphRepository;
    private final ClubGraphRepository clubGraphRepository;
    private final AppearanceGraphRepository appearanceGraphRepository;
    private final GameGraphRepository gameGraphRepository;
    private final TeamStatisticGraphRepository teamStatisticGraphRepository;

    private final GameRepository gameRepository;
    private final TeamStatisticRepository teamStatisticRepository;
    private final AppearanceRepository appearanceRepository;
    private final ITacticalAnalysisService tacticalAnalysisService;

    @Autowired
    @Qualifier("neo4jTransactionManager")
    private final PlatformTransactionManager transactionManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePlayerCreated(PlayerCreatedEvent event) {
        System.out.println("EVENT: Postgres uspešno komitovao. Upisujem igrača u Neo4j, ID: " + event.getId());
        try {
            PlayerGraph graphPlayer = new PlayerGraph();
            graphPlayer.setPlayerId(event.getId());
            graphPlayer.setName(event.getName());
            graphPlayer.setSurname(event.getSurname());
            graphPlayer.setPosition(event.getPosition());

            playerGraphRepository.save(graphPlayer);
            System.out.println("EVENT: Igrač uspešno upisan u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešan upis igrača u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleContractCreated(ContractCreatedEvent event) {
        System.out.println("EVENT: Postgres uspešno komitovao ugovor. Povezujem u Neo4j.");
        try {
            PlayerGraph graphPlayer = playerGraphRepository.findById(event.getPlayerId())
                    .orElseThrow(() -> new RuntimeException("PlayerGraph node not found"));

            ClubGraph graphClub = clubGraphRepository.findById(event.getClubId())
                    .orElseThrow(() -> new RuntimeException("ClubGraph node not found"));

            graphPlayer.setClubGraph(graphClub);
            playerGraphRepository.save(graphPlayer);
            System.out.println("EVENT: Uspešno kreirana veza između igrača i kluba u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno povezivanje u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePlayerUpdated(PlayerUpdatedEvent event) {
        System.out.println("EVENT: Postgres ažurirao igrača. Sinhronizujem Neo4j za ID: " + event.getId());
        try {
            PlayerGraph graphPlayer = playerGraphRepository.findById(event.getId())
                    .orElse(new PlayerGraph());

            graphPlayer.setPlayerId(event.getId());
            graphPlayer.setName(event.getName());
            graphPlayer.setSurname(event.getSurname());
            graphPlayer.setPosition(event.getPosition());

            playerGraphRepository.save(graphPlayer);
            System.out.println("EVENT: Uspešno ažuriran igrač u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno ažuriranje igrača u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePlayerDeleted(PlayerDeletedEvent event) {
        System.out.println("EVENT: Postgres obrisao igrača. Brišem iz Neo4j, ID: " + event.getId());
        try {
            playerGraphRepository.deleteById(event.getId());
            System.out.println("EVENT: Uspešno obrisan igrač iz Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno brisanje igrača iz Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClubCreated(ClubCreatedEvent event) {
        System.out.println("EVENT: Postgres uspešno kreirao klub. Upisujem čvor u Neo4j, ID: " + event.getId());
        try {
            ClubGraph graphClub = new ClubGraph();
            graphClub.setId(event.getId());
            graphClub.setName(event.getName());

            clubGraphRepository.save(graphClub);
            System.out.println("EVENT: Klub uspešno upisan u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešan upis kluba u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClubUpdated(ClubUpdatedEvent event) {
        System.out.println("EVENT: Postgres ažurirao klub. Sinhronizujem Neo4j čvor za ID: " + event.getId());
        try {
            ClubGraph graphClub = clubGraphRepository.findById(event.getId())
                    .orElse(new ClubGraph());

            graphClub.setId(event.getId());
            graphClub.setName(event.getName());

            clubGraphRepository.save(graphClub);
            System.out.println("EVENT: Klub uspešno ažuriran u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno ažuriranje kluba u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleClubDeleted(ClubDeletedEvent event) {
        System.out.println("EVENT: Postgres obrisao klub. Brišem čvor iz Neo4j, ID: " + event.getId());
        try {
            clubGraphRepository.deleteById(event.getId());
            System.out.println("EVENT: Klub uspešno obrisan iz Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno brisanje kluba iz Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAppearanceCreated(AppearanceCreatedEvent event) {
        System.out.println("EVENT: Kreiram nastup u Neo4j, ID: " + event.getId());
        try {
            AppearanceGraph ag = new AppearanceGraph();
            ag.setId(event.getId());
            syncGraphStats(event, ag);

            playerGraphRepository.findById(event.getPlayerId()).ifPresent(ag::setPlayerGraph);
            gameGraphRepository.findById(event.getGameId()).ifPresent(ag::setGameGraph);

            appearanceGraphRepository.save(ag);
            System.out.println("EVENT: Nastup uspešno kreiran u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešan upis nastupa u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAppearanceUpdated(AppearanceUpdatedEvent event) {
        System.out.println("EVENT: Ažuriram nastup u Neo4j, ID: " + event.getId());
        try {
            AppearanceGraph ag = appearanceGraphRepository.findById(event.getId())
                    .orElse(new AppearanceGraph());

            ag.setId(event.getId());
            syncGraphStats(event, ag);

            if (ag.getPlayerGraph() == null) {
                playerGraphRepository.findById(event.getPlayerId()).ifPresent(ag::setPlayerGraph);
            }
            if (ag.getGameGraph() == null) {
                gameGraphRepository.findById(event.getGameId()).ifPresent(ag::setGameGraph);
            }

            appearanceGraphRepository.save(ag);
            System.out.println("EVENT: Nastup uspešno ažuriran u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno ažuriranje nastupa u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAppearanceDeleted(AppearanceDeletedEvent event) {
        System.out.println("EVENT: Brišem nastup iz Neo4j, ID: " + event.getId());
        try {
            appearanceGraphRepository.deleteById(event.getId());
            System.out.println("EVENT: Nastup uspešno uklonjen iz Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno brisanje nastupa iz Neo4j: " + e.getMessage());
        }
    }

    private void syncGraphStats(AppearanceCreatedEvent source, AppearanceGraph target) {
        target.setMatchRole(source.getMatchRole() != null ? source.getMatchRole().name() : null);
        target.setMinutesPlayed(source.getMinutesPlayed());
        target.setGoals(source.getGoals());
        target.setAssists(source.getAssists());
        target.setFouls(source.getFouls());
        target.setYellowCards(source.getYellowCards());
        target.setRedCard(source.isRedCard());
        target.setRating(source.getRating());
        target.setPassingAccuracy(source.getPassingAccuracy());
    }

    private void syncGraphStats(AppearanceUpdatedEvent source, AppearanceGraph target) {
        target.setMatchRole(source.getMatchRole() != null ? source.getMatchRole().name() : null);
        target.setMinutesPlayed(source.getMinutesPlayed());
        target.setGoals(source.getGoals());
        target.setAssists(source.getAssists());
        target.setFouls(source.getFouls());
        target.setYellowCards(source.getYellowCards());
        target.setRedCard(source.isRedCard());
        target.setRating(source.getRating());
        target.setPassingAccuracy(source.getPassingAccuracy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "!#event.isUpdate")
    public void handleTeamStatisticCreated(TeamStatisticSaveEvent event) {
        System.out.println("EVENT: Kreiram timsku statistiku u Neo4j, ID: " + event.getId());
        try {
            TeamStatisticGraph statGraph = new TeamStatisticGraph();
            statGraph.setId(event.getId());
            mapEventToGraph(event, statGraph);

            GameGraph gameGraph = gameGraphRepository.findById(event.getGameId())
                    .orElseThrow(() -> new RuntimeException("GameGraph node not found with id: " + event.getGameId()));
            statGraph.setGameGraph(gameGraph);

            teamStatisticGraphRepository.save(statGraph);
            System.out.println("EVENT: Timska statistika uspešno kreirana u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešan upis statistike u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "#event.isUpdate")
    public void handleTeamStatisticUpdated(TeamStatisticSaveEvent event) {
        System.out.println("EVENT: Ažuriram timsku statistiku u Neo4j, ID: " + event.getId());
        try {
            TeamStatisticGraph statGraph = teamStatisticGraphRepository.findById(event.getId())
                    .orElse(new TeamStatisticGraph());

            statGraph.setId(event.getId());
            mapEventToGraph(event, statGraph);

            if (statGraph.getGameGraph() == null) {
                gameGraphRepository.findById(event.getGameId()).ifPresent(statGraph::setGameGraph);
            }

            teamStatisticGraphRepository.save(statGraph);
            System.out.println("EVENT: Timska statistika uspešno ažurirana u Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno ažuriranje statistike u Neo4j: " + e.getMessage());
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTeamStatisticDeleted(TeamStatisticDeletedEvent event) {
        System.out.println("EVENT: Brišem timsku statistiku iz Neo4j, ID: " + event.getId());
        try {
            teamStatisticGraphRepository.deleteById(event.getId());
            System.out.println("EVENT: Timska statistika uspešno obrisana iz Neo4j!");
        } catch (Exception e) {
            System.err.println("EVENT GREŠKA: Neuspešno brisanje statistike iz Neo4j: " + e.getMessage());
        }
    }

    private void mapEventToGraph(TeamStatisticSaveEvent source, TeamStatisticGraph target) {
        target.setHomeGoals(source.getHomeGoals());
        target.setAwayGoals(source.getAwayGoals());
        target.setHomeShots(source.getHomeShots());
        target.setAwayShots(source.getAwayShots());
        target.setHomeShotsOnTarget(source.getHomeShotsOnTarget());
        target.setAwayShotsOnTarget(source.getAwayShotsOnTarget());
        target.setHomeFouls(source.getHomeFouls());
        target.setAwayFouls(source.getAwayFouls());
        target.setHomeCorners(source.getHomeCorners());
        target.setAwayCorners(source.getAwayCorners());
        target.setHomeOffsides(source.getHomeOffsides());
        target.setAwayOffsides(source.getAwayOffsides());
        target.setHomePassSuccessRate(source.getHomePassSuccessRate());
        target.setAwayPassSuccessRate(source.getAwayPassSuccessRate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGraphSync(LiveMatchSyncEvent event) {
        CompletableFuture.runAsync(() -> {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                try {
                    Game jpaGame = gameRepository.findById(event.getGameId()).orElse(null);
                    TeamStatistic relStat = teamStatisticRepository.findById(event.getTeamStatisticId()).orElse(null);

                    Appearance relApp = null;
                    if (event.getAppearanceId() != null) {
                        relApp = appearanceRepository.findAppearancesWithPlayerInfoByGameId(event.getGameId())
                                .stream()
                                .filter(a -> a.getId().equals(event.getAppearanceId()))
                                .findFirst()
                                .orElse(null);
                    }

                    if (relStat == null || jpaGame == null) return null;

                    Long statId = relStat.getId();
                    Long gameId = jpaGame.getId();

                    GameGraph gameGraph = gameGraphRepository.findById(gameId).orElse(new GameGraph());
                    gameGraph.setId(gameId);
                    if (jpaGame.getStatus() != null) {
                        gameGraph.setStatus(jpaGame.getStatus().name());
                    }
                    gameGraph = gameGraphRepository.save(gameGraph);

                    TeamStatisticGraph statGraph = teamStatisticGraphRepository.findById(statId).orElse(new TeamStatisticGraph());
                    statGraph.setId(statId);
                    statGraph.setHomeGoals(relStat.getHomeGoals());
                    statGraph.setAwayGoals(relStat.getAwayGoals());
                    statGraph.setHomeShots(relStat.getHomeShots());
                    statGraph.setAwayShots(relStat.getAwayShots());
                    statGraph.setHomeShotsOnTarget(relStat.getHomeShotsOnTarget());
                    statGraph.setAwayShotsOnTarget(relStat.getAwayShotsOnTarget());
                    statGraph.setHomeFouls(relStat.getHomeFouls());
                    statGraph.setAwayFouls(relStat.getAwayFouls());
                    statGraph.setHomeCorners(relStat.getHomeCorners());
                    statGraph.setAwayCorners(relStat.getAwayCorners());
                    statGraph.setHomeOffsides(relStat.getHomeOffsides());
                    statGraph.setAwayOffsides(relStat.getAwayOffsides());
                    statGraph.setHomePassSuccessRate(relStat.getHomePassSuccessRate());
                    statGraph.setAwayPassSuccessRate(relStat.getAwayPassSuccessRate());
                    statGraph.setGameGraph(gameGraph);
                    teamStatisticGraphRepository.save(statGraph);

                    if (relApp != null && relApp.getId() != null) {
                        Long appId = relApp.getId();
                        AppearanceGraph appGraph = appearanceGraphRepository.findById(appId).orElse(new AppearanceGraph());

                        appGraph.setId(appId);
                        appGraph.setMatchRole(relApp.getMatchRole() != null ? relApp.getMatchRole().name() : null);
                        appGraph.setMinutesPlayed(relApp.getMinutesPlayed());
                        appGraph.setGoals(relApp.getGoals());
                        appGraph.setAssists(relApp.getAssists());
                        appGraph.setFouls(relApp.getFouls());
                        appGraph.setYellowCards(relApp.getYellowCards());
                        appGraph.setRedCard(relApp.isRedCard());
                        appGraph.setRating(relApp.getRating());
                        appGraph.setPassingAccuracy(relApp.getPassingAccuracy());
                        appGraph.setGameGraph(gameGraph);

                        if (relApp.getPlaysFor() != null && relApp.getPlaysFor().getPlayer() != null) {
                            Long playerId = relApp.getPlaysFor().getPlayer().getId();
                            if (playerId != null) {
                                PlayerGraph playerGraph = playerGraphRepository.findById(playerId).orElse(new PlayerGraph());
                                playerGraph.setPlayerId(playerId);
                                playerGraphRepository.save(playerGraph);

                                appGraph.setPlayerGraph(playerGraph);
                            }
                        }
                        appearanceGraphRepository.save(appGraph);
                    }
                    if (jpaGame.getHomeClub() != null && jpaGame.getAwayClub() != null) {
                        ClubGraph hc = new ClubGraph();
                        hc.setId((long) jpaGame.getHomeClub().getId());
                        gameGraph.setHomeClub(hc);

                        ClubGraph ac = new ClubGraph();
                        ac.setId((long) jpaGame.getAwayClub().getId());
                        gameGraph.setAwayClub(ac);

                        tacticalAnalysisService.runAnalysis((long) jpaGame.getHomeClub().getId(), statGraph, gameGraph);
                        tacticalAnalysisService.runAnalysis((long) jpaGame.getAwayClub().getId(), statGraph, gameGraph);
                    }
                } catch (Exception e) {
                    System.err.println("Neo4j Background Sync Error: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            });
        });

    }
}