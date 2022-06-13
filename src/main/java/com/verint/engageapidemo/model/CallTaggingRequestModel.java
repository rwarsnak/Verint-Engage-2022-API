package com.verint.engageapidemo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallTaggingRequestModel {
    private String accessKey;
    private String secretKey;
    private String bucketRegion;
    private String bucketName;
    private String filename;
    private String apiKeyId;
    private String apiKeyValue;
}