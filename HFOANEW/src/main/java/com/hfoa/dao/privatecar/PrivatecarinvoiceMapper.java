package com.hfoa.dao.privatecar;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;
import com.hfoa.entity.vacation.LearYear;


public interface PrivatecarinvoiceMapper {
	
	int insertPrivateCarinvoice(PrivatecarinvoiceEntity privatecarinvoiceEntity);
	
	int updatePrivateCarinvoice(PrivatecarinvoiceEntity privatecarinvoiceEntity);
	
	int deletePrivateCarinvoice(String applyId);
	
	List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity();
	
	List<PrivatecarinvoiceEntity> unSignInvoiceDisplay();
	
	List<PrivatecarinvoiceEntity> invoiceDisplay();
	
	List<PrivatecarinvoiceEntity> invoiceDisplayApplyTime(String beingTime,String endTime);
	
	PrivatecarinvoiceEntity getApplyIdsPrivatecarinvoiceEntity(String applyIds);
	
	PrivatecarinvoiceEntity getApplyIdPrivatecarinvoiceEntity(String applyId);
	
	List<PrivatecarinvoiceEntity> applymanPrivatecarinvoice(String applyMan,String applyTime);

	//根据报告单号删除报告单
	@Delete({"delete from b_privatecarinvoice where ApplyId = #{applyId,jdbcType=VARCHAR}"})
	void deleteByApplyid(@Param("applyId")String applyId);

}