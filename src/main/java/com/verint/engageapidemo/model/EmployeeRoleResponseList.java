package com.verint.engageapidemo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeRoleResponseList {
    private List<EmployeeRolesResponse> responses = new ArrayList<>();
}
