package com.dateguide.recommendation.api;

import com.dateguide.recommendation.application.SseStreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/recommendations")
public class SseStreamController {

    private final SseStreamService streamService;

    public SseStreamController(SseStreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(
            value = "/{jobId}/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter stream(
            @PathVariable String jobId,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
        if (jobId == null || jobId.isBlank()) {
            throw new IllegalArgumentException("jobId must not be blanked");
        }

        return streamService.open(jobId, lastEventId);
    }
}
