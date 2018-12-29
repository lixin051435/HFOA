package com.hfoa.service.TravelExpes;

import java.util.List;

import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;


/**
 */
public interface ApproveExpensesService {
	
	List<ApproveExpensesEntity> listApproveExpense(String id);
	
	List<ApplyExpensesEntity> searchApplyExpense(ApplyExpensesEntity applyExpense,String starTime,String searendTime,String voucherNum);
	
	//财务审核--根据TravelExpenseId查看
	List<ApproveExpensesEntity> getApproveExpensesByTId(String travelExpenseId);
	
	int insertApproveExpensesService(ApproveExpensesEntity approveExpensesEntity);
	
	int deleteapproveExpenses(String Id);
	
	int modifyFinanceReview(ApproveExpensesEntity approveExpensesEntity);
	
	int exportapproveExpenses(ApplyExpensesEntity applyExpensesDTO,String filePath,String starTime,String searendTime,String voucherNum);

}
