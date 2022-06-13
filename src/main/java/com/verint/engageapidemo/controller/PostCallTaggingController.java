package com.verint.engageapidemo.controller;

import com.verint.engageapidemo.model.CallTaggingRequestModel;
import com.verint.platform.security.VwtRequest;
import com.verint.platform.security.VwtSecurity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class PostCallTaggingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostCallTaggingController.class);

    @PostMapping("tag-calls")
    public ResponseEntity<String> tagCalls(@RequestBody CallTaggingRequestModel requestBody) throws IOException {
        ResponseEntity<String> response = null;
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                requestBody.getAccessKey(),
                requestBody.getSecretKey());
        S3Client s3 = S3Client.builder()
                .region(Region.of(requestBody.getBucketRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(requestBody.getBucketName())
                .key(requestBody.getFilename())
                .build();
        ResponseInputStream<GetObjectResponse> s3ObjResponse = s3.getObject(getObjectRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(s3ObjResponse));
        CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').parse(reader);
        String hostname = "https://blue77.verint.training";
        String baseUrl = "/api/recording/capture/v1/interactions/update/";
        int totalAttempts = 0;
        int successfulAttempts = 0;
        for (CSVRecord aRecord : parser) {
            LOGGER.info("Reading Record: {}", aRecord);
            totalAttempts++;
            String transactionId = aRecord.get(0);
            String newValue = aRecord.get(1);
            String endpoint = baseUrl + transactionId;
            VwtRequest request = VwtRequest.POST().url(endpoint).build();
            String token = VwtSecurity.tokenBuilder().forRequest(request).withKey(requestBody.getApiKeyId(), requestBody.getApiKeyValue()).createToken();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", token);
            headers.add("Content-Type", "application/json");
            String updateRequestBody = "{\"custom_data\": {\"5\": \"" + newValue + "\"}}";
            HttpEntity<String> updateRequestEntity = new HttpEntity<>(updateRequestBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> updateResponse = restTemplate.exchange(hostname + endpoint, HttpMethod.POST, updateRequestEntity, String.class);
            if (updateResponse.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Update success for {}", transactionId);
                successfulAttempts++;
            } else {
                LOGGER.info("Update failed for {}", transactionId);
            }

            if (successfulAttempts == 0) {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No successful update attempts.  See log.");
            } else if (successfulAttempts < totalAttempts) {
                response = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("Some update failures.  See log.");
            } else {
                response = ResponseEntity.status(HttpStatus.OK).body(null);
            }
        }
        return response;
    }
}
