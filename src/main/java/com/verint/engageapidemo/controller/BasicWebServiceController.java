package com.verint.engageapidemo.controller;

import com.verint.engageapidemo.model.ThrowItBackModel;
import com.verint.platform.security.VwtRequest;
import com.verint.platform.security.VwtSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @PostMapping("isVerintAlive")
    public ResponseEntity<String> isVerintAlive(@RequestParam String apiKeyId, @RequestParam String apiKeyValue) {
        String baseUrl = "https://blue77.verint.training";
        String endpoint = "/wfo/user-mgmt-api/v1/employees";
        String url = baseUrl + endpoint;

        VwtRequest req = VwtRequest.builder()
                .url(endpoint)
                .method("HEAD")
                .build();

        String token = VwtSecurity.tokenBuilder()
                .forRequest(req)
                .withKey(apiKeyId, apiKeyValue)
                .createToken();
        LOGGER.info("Here is the token: {}", token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> authResponse = restTemplate.exchange(url, HttpMethod.HEAD, request, String.class);
        return ResponseEntity.status(authResponse.getStatusCode()).body(authResponse.getBody());
    }
}
