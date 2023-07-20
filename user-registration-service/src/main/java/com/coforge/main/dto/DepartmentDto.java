package com.coforge.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {

//    private Long departmentId;
    private String departmentName;
    private String departmentDescription;
    private String departmentCode;
}
