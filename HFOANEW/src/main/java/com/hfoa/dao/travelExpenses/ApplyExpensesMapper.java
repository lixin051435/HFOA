package com.hfoa.dao.travelExpenses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.user.UserEntity;

public interface ApplyExpensesMapper {
	
	
	int insertApplyExpensesEntity(ApplyExpensesEntity applyExpensesEntity);
	
	int updateApplyExpens(ApplyExpensesEntity applyExpensesEntity);
	
	int updateOpenId(ApplyExpensesEntity applyExpensesEntity);
	
	int updateApproveOpenId(ApplyExpensesEntity applyExpensesEntity);
	
	int deleteApplyExpese(String id);
	
	ApplyExpensesEntity getApplyExpens(String id);
	
	List<ApplyExpensesEntity> getCCListOpenIdsearch(ApplyExpensesEntity applyExpensesEntity);
	
	List<ApplyExpensesEntity> searchApplyExpense(ApplyExpensesEntity applyExpensesEntity);
	
	List<ApplyExpensesEntity> getCCListOpenIdsearchState(ApplyExpensesEntity applyExpensesEntity);
	
	List<ApplyExpensesEntity> statusApplyExpense(String openId);
	
	List<ApplyExpensesEntity> getCCListOpenId(String cCListOpenId);
	
	List<ApplyExpensesEntity> getCCListOpenIdState(String cCListOpenId);
	
	List<ApplyExpensesEntity> searApplyExpenseApprove();
	
	List<Map<String,Object>> countApplyExpense(String openId,String applyTime);

}