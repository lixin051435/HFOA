package com.hfoa.dao.entertain;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainannualbudget;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.service.user.BUserDepartment;

/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertainAnnualBudgetMapper {
	
	Entertainannualbudget getEntity(String department, String year);
	
	List<String> year(Integer year);
	
	List<Entertainannualbudget> wGetAnnualBudget(String year);
	
	String getTimeById(String param,String year);
	
	int updateAnnualBudget(Entertainannualbudget entertainannualbudget);
}