package com.football_club.MatchTracking.repository.jpa;

import com.football_club.MatchTracking.model.MatchEvent;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchEventRepository {
    private final InfluxDBClient influxDBClient;
    private final String bucket;
    private final String org;

    public MatchEventRepository(InfluxDBClient influxDBClient, @Value("${influxdb.bucket}") String bucket, @Value("${influxdb.org}") String org){
        this.influxDBClient = influxDBClient;
        this.bucket = bucket;
        this.org = org;
    }

    public void save(MatchEvent event){
        WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, event);
    }

    public List<FluxTable> getStatsForGame(Long gameId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: 0) " +
                        "|> filter(fn: (r) => r._measurement == \"match_events\" and r.gameId == \"%s\") " +
                        "|> group(columns: [\"clubId\", \"eventType\"]) " +
                        "|> count(column: \"_value\")", bucket, gameId
        );

        return influxDBClient.getQueryApi().query(fluxQuery, org);
    }

    public List<FluxTable> getStatsForPlayer(Long gameId, Long playsForId) {
        String fluxQuery = String.format(
                "from(bucket: \"%s\") " +
                        "|> range(start: 0) " +
                        "|> filter(fn: (r) => r._measurement == \"match_events\" and r.gameId == \"%s\" and r.playsForId == \"%s\") " +
                        "|> group(columns: [\"eventType\"]) " +
                        "|> count(column: \"_value\")", bucket, gameId, playsForId
        );

        return influxDBClient.getQueryApi().query(fluxQuery, org);
    }
}
