package com.verint.engageapidemo.controller;

import com.verint.engageapidemo.model.EmployeeRoleResponseList;
import com.verint.engageapidemo.model.EmployeeRolesResponse;
import com.verint.engageapidemo.model.generated.getallemployees.AllEmployeesResponse;
import com.verint.engageapidemo.model.generated.getallemployees.Datum;
import com.verint.platform.security.VwtRequest;
import com.verint.platform.security.VwtSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DataExtractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractController.class);

    @GetMapping("/extract/employeeRoles")
    public ResponseEntity<EmployeeRoleResponseList> employeeRoles(@RequestParam String apiKeyId, @RequestParam String apiKeyValue, @RequestParam String roleName) {
        String baseUrl = "https://blue77.verint.training";
        String getAllEmployeesEndpoint = "/wfo/user-mgmt-api/v1/employees";

        String url = baseUrl + getAllEmployeesEndpoint;
        VwtRequest req = VwtRequest.builder()
                .url(getAllEmployeesEndpoint)
                .method("GET")
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
        ResponseEntity<AllEmployeesResponse> authResponse = restTemplate.exchange(url, HttpMethod.GET, request, AllEmployeesResponse.class);
        EmployeeRoleResponseList responseList = new EmployeeRoleResponseList();
        for (Datum anEmployee : authResponse.getBody().getData()) {
            EmployeeRolesResponse anEmployeeRole = new EmployeeRolesResponse();
            anEmployeeRole.setEmployeeId(anEmployee.getId());
            anEmployeeRole.setRoleName("DUMMY");
            responseList.getResponses().add(anEmployeeRole);
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);

    }


}
