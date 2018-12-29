package com.hfoa.dao.entertain;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.entertain.Entertainhospitality;
import com.hfoa.entity.entertain.EntertainhospitalitySubsidiary;
import com.hfoa.entity.entertain.Entertaininvoiceunit;


/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertainHospitalityMapper {
	
	int insertEntertainhospitality(Entertainhospitality entertainhospitality);
	
	
	List<Entertainhospitality> getEntertainhospitality(String type,String year);


	//根据基本编号获取部门名称
	@Select({"select Department from b_entertainhospitality where HospitalityId= #{hospitalityId,jdbcType=VARCHAR}"})
	String getdepartmentByhospitalId(@Param("hospitalityId")String hospitalityId);

	//根据基本id获取相应的调整详情
//	@Select({"select * from b_entertainhospitalitysubsidiary where HospitalityId=#{hospitalityId,jdbcType=VARCHAR}"})
//	List<EntertainhospitalitySubsidiary> getAnnualBudgetDetail(@Param("hospitalityId")String hospitalityId);
	
}