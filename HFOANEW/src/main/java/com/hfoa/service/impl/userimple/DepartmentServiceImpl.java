package com.hfoa.service.impl.userimple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.lear.YearLearMapper;
import com.hfoa.dao.user.DepartmentMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.service.leaver.YearLearService;
import com.hfoa.service.user.DepartmentService;
import com.hfoa.service.user.USerService;


/**
 *年假                                                                                                                                                                                                                                                     
 */
@Service
public class DepartmentServiceImpl implements DepartmentService{

	
	@Autowired
	private DepartmentMapper department;
	
	
	@Override
	public List<DepartmentEntity> selectDepartment() {
		return department.selectDuty();
	}
	
	


	

	

}
