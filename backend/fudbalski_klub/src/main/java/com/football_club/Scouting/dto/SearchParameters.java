package com.football_club.Scouting.dto;

import com.football_club.Scouting.model.enums.SearchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParameters {
    private String searchTerm;
    private List<SearchMetric> metrics;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchMetric {
        private Long metricId;
        private double value;
        private SearchType searchType;
    }
}
