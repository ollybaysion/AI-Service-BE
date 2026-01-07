package com.dateguide.scoring.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScoreInternalControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void score_returns_sorted_topk_with_breakdown() throws Exception {
        String body = """
                {
                    "userContext": {
                        "userId": "u1",
                        "area": "Seongsu",
                        "date": "2026-01-05",
                        "startTime": "19:00",
                        "budgetRange": "MID",
                        "transportation": "SUBWAY",
                        "mood": ["quiet", "romantic"]
                    },
                    "timeContext": {
                        "now": "2026-01-05T18:40:00"
                    },
                    "places": [
                        {"placeId": 101},
                        {"placeId": 202},
                        {"placeId": 303}
                    ],
                    "options": {
                        "window": "H24",
                        "returnBreakdown": true,
                        "topK": 2
                    }
                }
                """;

        mockMvc.perform(post("/internal/score")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoredPlaces").isArray())
                .andExpect(jsonPath("$.scoredPlaces.length()").value(2))
                .andExpect(jsonPath("$.scoredPlaces[0].placeId").exists())
                .andExpect(jsonPath("$.scoredPlaces[0].totalScore").exists())
                .andExpect(jsonPath("$.scoredPlaces[0].breakdown.trendScore").exists())
                .andExpect(jsonPath("$.meta.scoringVersion").exists())
                .andExpect(jsonPath("$.meta.window").value("H24"));
    }

    @Test
    void score_returns_no_breakdown_when_disabled() throws Exception {
        String body = """
                {
                    "userContext": {
                        "userId": "u1",
                        "area": "Seongsu",
                        "date": "2026-01-05",
                        "startTime": "19:00",
                        "budgetRange": "MID",
                        "transportation": "SUBWAY",
                        "mood": ["quiet"]
                    },
                    "timeContext": {
                        "now": "2026-01-05T18:40:00"
                    },
                    "places": [
                        {"placeId": 101},
                        {"placeId": 202}
                    ],
                    "options": {
                        "window": "H24",
                        "returnBreakdown": false,
                        "topK": 10
                    }
                }
                """;

        mockMvc.perform(post("/internal/score")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoredPlaces[0].breakdown").doesNotExist());
    }
}