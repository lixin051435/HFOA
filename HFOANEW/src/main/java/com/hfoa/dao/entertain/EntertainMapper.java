package com.hfoa.dao.entertain;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertainMapper {
	
	int insertEntertain(Entertainapplyinfo entertainapplyinfo);

	int updateEntertain(Entertainapplyinfo entertainapplyinfo);
	
	int updateOpenId(Entertainapplyinfo entertainapplyinfo);
	
	int updateApproverOpenid(Entertainapplyinfo entertainapplyinfo);
	
	int deleteEntertain(int id);
	
	Integer getSqlLast();
	
	String getSqlLastNumber();
	
	Integer getApplyObject();
	
	List<Map<String,Object>> countEntertain(String openId,String applyTime);
	
	
	Entertainapplyinfo getAproveEntertain(int id);
	
	Entertainapplyinfo getNumberEntertainApplyInfo(String number);
	
	List<Entertainapplyinfo> listEntertainApplyInfo();
	
	List<Entertainapplyinfo> statusApproveEntertain(String openId);
	
	List<String> getOneDepartmentEntertainSum(String department, String year);
	
	List<Map<String,Object>> getEntertainSum(String year);
	
	List<Entertainapplyinfo> wgtServApply(Entertainapplyinfo entertainapplyinfo);
	
	List<Entertainapplyinfo> searchEntertainApprove(Entertainapplyinfo entertainapplyinfo);
}