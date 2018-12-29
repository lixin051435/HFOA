package com.hfoa.dao.user;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;

public interface DepartmentMapper {
	
	List<DepartmentEntity> selectDuty();//查询部门
	
	List<DepartmentEntity> getByName(String departName);

}