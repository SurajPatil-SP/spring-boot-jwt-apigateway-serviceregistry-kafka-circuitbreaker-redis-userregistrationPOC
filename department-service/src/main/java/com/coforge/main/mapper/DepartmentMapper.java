package com.coforge.main.mapper;

import com.coforge.main.dto.DepartmentDto;
import com.coforge.main.model.Department;

public class DepartmentMapper {

    public static DepartmentDto mapToDepartmentDto(Department department){
        DepartmentDto departmentDto = new DepartmentDto(
                department.getDepartmentName(),
                department.getDepartmentDescription(),
                department.getDepartmentCode()
        );
        return departmentDto;
    }

//    public static Department mapToDepartment(DepartmentDto departmentDto){
//        Department department = new Department(
//                departmentDto.getDepartmentId(),
//                departmentDto.getDepartmentName(),
//                departmentDto.getDepartmentDescription(),
//                departmentDto.getDepartmentCode()
//        );
//        return department;
//    }
}
