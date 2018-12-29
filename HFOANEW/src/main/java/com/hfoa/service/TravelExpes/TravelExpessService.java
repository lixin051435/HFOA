package com.hfoa.service.TravelExpes;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfoa.controller.weixin.YJJYController;
import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.entity.weixin.TextMessage;
import com.hfoa.service.user.USerService;
import com.hfoa.util.MessageUtil;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 */

public interface TravelExpessService {
	
	int insertTravelExpenss(ApplyExpensesEntity applyExpensesEntity);
	
	List<Map<String,Object>> countApplyExpense(String openId);
	
	int updateTravelExpenss(ApplyExpensesEntity applyExpensesEntity);
	
	int deleteTravelExpess(String taskId,String id);
	
	List<ApplyExpensesEntity> listApplyExpense(String openId);
	
	List<ApplyExpensesEntity> statusApplyExpense(String openId);
	
	List<ApplyExpensesEntity> selectApplyExpense(String openId);
	
	//财务部查看
	List<ApplyExpensesEntity> finaceApplyExpense(ApplyExpensesEntity applyExpensesEntity);
	
	List<ApplyExpensesEntity> searApplyExpense(String openId);
	
	int approveApplyExpense(String id,String taskId,String result,String comment);
	
	int confirmApplyExpense(String id,String taskId,String confirm);
	
	int registerApplyExpense(String taskId,String result,ApproveExpensesEntity approveExpensesEntity);
	
	Map<String,Object> searchApplyExpense(ApplyExpensesEntity applyExpensesEntity,Integer nowPage,Integer pageSize);
	
	List<ApplyExpensesEntity> searApplyExpenseApprove(ApplyExpensesEntity applyExpensesEntity);
	
	Map<String,Object> listcCListApplyExpense(String cCListOpenId,Integer nowPage,Integer pageSize);
	
	Map<String,Object> getcClistApplyExpenseState(String cCListOpenId,Integer nowPage,Integer pageSize);
	
	Map<String,Object> getcClistApplyExpense(String cCListOpenId,Integer nowPage,Integer pageSize,String beginTime,String endTime,String applyMan,String state);
	
	int ifState(String id);
	
	int financeReviewFail(String taskId);
	
	int exportApplyExpenses(ApplyExpensesEntity applyExpensesEntity,String filePath);
	
}
