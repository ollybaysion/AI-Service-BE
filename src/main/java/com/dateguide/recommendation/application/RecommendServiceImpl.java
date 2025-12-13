package com.dateguide.recommendation.application;

import com.dateguide.recommendation.dto.PlaceDto;
import com.dateguide.recommendation.dto.RecommendRequest;
import com.dateguide.recommendation.dto.RecommendResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Override
    public RecommendResponse recommend(RecommendRequest recommendRequest) {
        List<PlaceDto> placeList = buildSimpleRoute(recommendRequest);

        String concept = String.join(" , ",
                placeList.stream().map(PlaceDto::category).toList());

        return new RecommendResponse(
                UUID.randomUUID().toString(),
                recommendRequest.userId(),
                recommendRequest.area(),
                recommendRequest.date(),
                recommendRequest.startTime(),
                new RecommendResponse.Summary(
                        placeList.size(),
                        routeHint(recommendRequest),
                        concept
                ),
                placeList
        );

    }

    private List<PlaceDto> buildSimpleRoute(RecommendRequest recommendRequest) {
        List<String> categories = List.of("cafe", "exhibition", "food");

        List<PlaceDto> result = new ArrayList<>();
        int order = 1;

        for (String category : categories) {
            if (result.size() >= 4) break;
            result.add(new PlaceDto(
                    order++,
                    sampleName(recommendRequest.area(), category),
                    category,
                    sampleAddress(recommendRequest.area()),
                    "요청 조건 기반으로 동선/무드에 맞춘 후보",
                    estimateCost(recommendRequest.budgetRange())
            ));

        }

        return result;
    }

    private String routeHint(RecommendRequest recommendRequest) {
        return switch (recommendRequest.transportation()) {
            case WALK -> "도보 동선 위주";
            case PUBLIC -> "대중교통 환승 최소";
            case CAR -> "주차 고려";
        };
    }

    private Integer estimateCost(RecommendRequest.BudgetRange budgetRange) {
        return switch (budgetRange) {
            case LOW -> 15000;
            case MID -> 30000;
            case HIGH -> 60000;
        };
    }

    private String sampleName(String area, String category) {
        return switch (category) {
            case "cafe" -> area + " 감성 카페";
            case "food" -> area + " 인기 맛집";
            case "exhibition" -> area + " 전시/체험";
            default -> area + " 추천 스팟";
        };
    }

    private String sampleAddress(String area) {
        return "서울 " + area + " 인근";
    }
}
