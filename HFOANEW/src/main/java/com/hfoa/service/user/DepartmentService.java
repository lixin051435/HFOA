package com.hfoa.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearYear;

/**
 */
public interface DepartmentService {
	
	List<DepartmentEntity> selectDepartment();
	
}
