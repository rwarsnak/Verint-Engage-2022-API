package com.verint.engageapidemo.controller;

import com.verint.engageapidemo.model.EmployeeRoleResponseList;
import com.verint.engageapidemo.model.EmployeeRoleResponse;
import com.verint.engageapidemo.model.generated.getallemployees.AllEmployeesResponse;
import com.verint.engageapidemo.model.generated.getallemployees.Datum;
import com.verint.engageapidemo.model.generated.getroles.EmployeeRolesResponse;
import com.verint.engageapidemo.model.generated.getroles.RoleDatum;
import com.verint.platform.security.VwtRequest;
import com.verint.platform.security.VwtSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
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
        ResponseEntity<AllEmployeesResponse> getAllEmployeesResponse = restTemplate.exchange(url, HttpMethod.GET, request, AllEmployeesResponse.class);
        LOGGER.info("Got the employees");
        EmployeeRoleResponseList responseList = new EmployeeRoleResponseList();
        for (Datum anEmployee : getAllEmployeesResponse.getBody().getData()) {
            EmployeeRoleResponse anEmployeeRole = new EmployeeRoleResponse();
            anEmployeeRole.setEmployeeId(anEmployee.getId());
            String getEmployeeRolesEndpoint = "/wfo/user-mgmt-api/v1/employees/" + anEmployee.getId() + "/roles";
            String getEmployeeRolesUrl = baseUrl + getEmployeeRolesEndpoint;
            LOGGER.info("Roles endpoint: {}", getEmployeeRolesEndpoint);
            VwtRequest roleReq = VwtRequest.builder()
                    .url(getEmployeeRolesEndpoint)
                    .method("GET")
                    .build();
            String roleToken = VwtSecurity.tokenBuilder()
                    .forRequest(roleReq)
                    .withKey(apiKeyId, apiKeyValue)
                    .createToken();
            HttpHeaders roleHeaders = new HttpHeaders();
            roleHeaders.add("Authorization", roleToken);
            HttpEntity<String> roleRequest = new HttpEntity<>(roleHeaders);
            try {
                ResponseEntity<EmployeeRolesResponse> roleResponse = restTemplate.exchange(getEmployeeRolesUrl, HttpMethod.GET, roleRequest, EmployeeRolesResponse.class);
                for (RoleDatum aRole : roleResponse.getBody().getData()) {
                    String employeesRole = aRole.getAttributes().getName();
                    if (employeesRole.equals(roleName)) {
                        anEmployeeRole.setRoleName(roleName);
                        responseList.getResponses().add(anEmployeeRole);

                    }
                }

            } catch (HttpClientErrorException ex) {
                LOGGER.warn("Something bad with roles request", ex);
            }


        }

        return ResponseEntity.status(HttpStatus.OK).body(responseList);

    }


}
