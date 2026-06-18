package com.football_club.Scouting.service.impl;

import com.football_club.Scouting.repository.MetricContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricContextService {
    private final MetricContextRepository metricContextRepository;

    @Scheduled(cron = "0 18 0 * * *")
    private int refreshContextTable() {
        return metricContextRepository.refreshMetricContextTable();
    }
}
