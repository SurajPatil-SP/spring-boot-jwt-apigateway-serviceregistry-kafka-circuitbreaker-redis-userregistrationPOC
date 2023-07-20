package com.coforge.main.service;

import com.coforge.main.dto.DepartmentDto;

public interface DepartmentService {

	public DepartmentDto saveDepartment(DepartmentDto departmentDto);

	public DepartmentDto getDepartmentByCode(String departmentCode);
}
