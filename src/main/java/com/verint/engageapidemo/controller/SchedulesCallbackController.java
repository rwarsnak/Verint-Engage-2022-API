package com.verint.engageapidemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.verint.engageapidemo.model.generated.schedulecallback.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RestController
public class SchedulesCallbackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulesCallbackController.class);
    private static Map<String, String> REQUEST_STATUS = new HashMap<>();

    @PostMapping("/schedule-callback")
    void handleCallback(@RequestBody String details) {
        String validTransactionId = null;
        for (String aTrackingId : REQUEST_STATUS.keySet()) {
            if (details.contains(aTrackingId)) {
                validTransactionId = aTrackingId;
                break;
            }
        }
        if (validTransactionId == null) {
            LOGGER.error("INVALID CALLBACK: {}", details);
        } else {
            LOGGER.info("Schedule request completed with tracking ID: {}", validTransactionId);
            REQUEST_STATUS.remove(validTransactionId);
        }


    }

    @PostMapping("/wfo/fis-api/v1/schedules")
    public ResponseEntity<PostScheduleResponse> updateSchedule(@RequestBody String requestBody, @RequestHeader(name="Authorization") String authHeader) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        String url = "https://blue77.verint.training/wfo/fis-api/v1/schedules";
        ResponseEntity<PostScheduleResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, PostScheduleResponse.class);
        if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            String trackingId = response.getBody().getData().getAttributes().getTrackingID();
            LOGGER.info("Schedule update accepted.  Tracking ID: {}", trackingId);
            REQUEST_STATUS.put(trackingId, "QUEUED");
        }
        return response;
    }
}
