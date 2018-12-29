package com.hfoa.service.privatecarservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;

/**
 * 
 * @author dzh
 * 私车service接口
 */
public interface PrivateCarService {

	int insertPrivateCar(PrivateCarEntity privateCarEntity);
	
	List<PrivateCarEntity> privateApplytion(String openId);
	
	int updatePrivateCar(PrivateCarEntity privateCarEntity);
	
	int deletePrivateCar(String applyId,String taskId);
	
	PrivateCarEntity getPrivateCar(String applyId);
	
	List<PrivateCarEntity> listOpendIdPrivateCar(String openId);
	
	int approvalPrivateCar(String applyId,String taskId,String result,String comment);
	
	List<PrivateCarEntity> staffPrivateCar(String openId);
	
	int staffPrivateCarApprove(String applyId,String taskId,String staffresult);
	
	List<PrivateCarEntity> reimbursementPrivateCar(String openId);
	
	int reimbursementPrivateCarApprove(PrivatecarinvoiceEntity privatecarinvoiceEntity,String taskId);
	
	List<PrivatecarinvoiceEntity> financePrivateCar(String openId);
	
	List<PrivateCarEntity> financePrivatreCartaskId(String openId,String applyIds,String canUpdate);
	
	int financePrivateCarApprove(String taskId,String applyId,String finaceresult,String applyIdinvoice,String voucherNum,String paidTime,String comment);
	
	int privateRegistervoucher(String taskId,String applyId,String voucherNum,String paidTime);
	
	List<PrivatecarinvoiceEntity> uninvoiceDisplay();
	
	List<PrivatecarinvoiceEntity> unSignInvoiceDisplay();
	
	List<PrivatecarinvoiceEntity> invoiceDisplay();
	
	List<PrivateCarEntity> selectByApplyIds(String applyId);
	
	List<PrivateCarEntity> selectByApplyIds(String applyId,String openId);
	
	Map<String,Object> searPrivateCar(PrivateCarEntity privateCarEntity,Integer nowPage,Integer pageSize);
	
	int passPrivateCars(String applyid,String applyId,String taskId);
	
	int backPrivateCars(String applyid,String taskId);
	
	int privateCarInvoiceexportExcel(String number,String filePath);
	
	int updateInvoice(PrivatecarinvoiceEntity privatecarinvoiceEntity);
	
	PrivatecarinvoiceEntity getOneRegister(String applyid);
	
	List<Map<String,Object>> countPrivate(String openId);

	//获取所有私车申请信息
	List<PrivateCarEntity> allApplyDisplay(int start, int number);

	//获取所有申请信息的总数
	int getAllCount();

	//根据申请id获取相应的执行信息
	PrivatecarinvoiceEntity getbyLikeApplyId(String applyId);

	//条件查询私车申请信息
	List<PrivateCarEntity> allApplySearchDisplay(PrivateCarEntity applyinfo, int start, int number);

	//条件查询申请信息数量
	int getAllSearchCount(PrivateCarEntity applyinfo);

	//财务添加申请信息
	int SaveNew(PrivateCarEntity privateCarEntity);

	//根据报告单号删除报告单
	void deleteByApplyid(String applyId);

	//根据申请编号删除私车申请信息
	Integer deleteByApplyId(String applyId);

	//采取修改私车申请信息
	Object updateNew(PrivateCarEntity privateCarEntity);

	//根据业务id获取进行中的私车申请信息
	List<PrivateCarEntity> findTasksByApplyId(Set<String> keySet);

	 //批量事后登记
	Object perform(String applyIds, String subtime, String sum,String applyman);

	 //批量报销
	Object reimbursement(String applyIds, String registtime, String sum, String registman, String vouchernum,String subtime,String applyman);
	
}
