package com.hfoa.dao.travelExpenses;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;
import com.hfoa.entity.user.UserEntity;

public interface ApproveExpensesMapper {
	
	List<ApproveExpensesEntity> listApproveExpense(String id);
	
	List<ApproveExpensesEntity> getApproveExpensesByTId(String travelExpenseId);
	
	int insertApproveExpense(ApproveExpensesEntity approveExpensesEntity);
	
	int updateApproveExpense(ApproveExpensesEntity approveExpensesEntity);

	int deleteapproveExpenses(String Id);
}