package com.verint.engageapidemo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRolesResponse {
    private String employeeId;
    private String roleName;
}
