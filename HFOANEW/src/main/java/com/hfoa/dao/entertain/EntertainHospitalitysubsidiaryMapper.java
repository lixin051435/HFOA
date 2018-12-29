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
public interface EntertainHospitalitysubsidiaryMapper {
	
	int insertEntertainhospitalitysubsidiary(EntertainhospitalitySubsidiary entertainhospitality);
	
	
	List<Entertainhospitality> listEntertainhospitality(String type,String year);


	//根据基本id获取相应的调整详情
	List<EntertainhospitalitySubsidiary> getAnnualBudgetDetail(String hospitalityId);

	
}