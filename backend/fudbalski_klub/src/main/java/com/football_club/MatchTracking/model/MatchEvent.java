package com.football_club.MatchTracking.model;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.*;

import java.time.Instant;

@Measurement(name = "match_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {
    @Column(tag = true)
    private String gameId;

    @Column(tag = true)
    private String clubId;

    @Column(tag = true)
    private String playsForId;

    @Column(tag = true)
    private String eventType;

    @Column
    private Integer matchMinute;

    @Column(timestamp = true)
    private Instant timestamp;
}
