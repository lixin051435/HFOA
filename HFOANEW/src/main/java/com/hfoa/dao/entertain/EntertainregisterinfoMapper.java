package com.hfoa.dao.entertain;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertainregisterinfoMapper {
	
	int insertEntertainregisterinfo(Entertainregisterinfo entertainregisterinfo);
	
	int updateEntertainregisterinfo(Entertainregisterinfo entertainregisterinfo);
	
	int deleteEntertainregisterinfo(int id);
	
	int deleteEntertainregisterinfonumber(String number);
	
	Integer getSqlLast();
	
	List<Entertainregisterinfo> getNumber(String number);
	
	
	
	Entertainregisterinfo getEntertainregisterinfo(String id);
	
	
}