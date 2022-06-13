package com.verint.engageapidemo.controller;

import com.verint.engageapidemo.model.ThrowItBackModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BasicWebServiceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicWebServiceController.class);
    @GetMapping("/isAlive")
    public ResponseEntity<String> isAlive(@RequestParam String name) {
        LOGGER.info("In isAlive()");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).body("{\"message\":\"I am alive, " + name + "\"}");
    }

    @PostMapping("/throwItBack")
    public ResponseEntity<ThrowItBackModel> throwItBack(@RequestBody String body) {
        ThrowItBackModel response = new ThrowItBackModel(body);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
