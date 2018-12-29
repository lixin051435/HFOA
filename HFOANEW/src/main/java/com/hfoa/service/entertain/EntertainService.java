package com.hfoa.service.entertain;

import java.util.List;
import java.util.Map;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.EntertainAnnualInfoDTO;
import com.hfoa.entity.entertain.Entertainannualbudget;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainhospitality;
import com.hfoa.entity.entertain.EntertainhospitalitySubsidiary;
import com.hfoa.entity.entertain.Entertaininvoiceunit;
import com.hfoa.entity.entertain.Entertainobjecttype;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 部门service接口
 */
public interface EntertainService {

	//事前申请
	public int insertEntertain(Entertainapplyinfo entertainapplyinfo);
	
	//修改
	public int updateEntertain(Entertainapplyinfo entertainapplyinfo,Entertainregisterinfo entertainregisterinfo);
	
	//删除
	public int deleteEntertain(int id,String taskId);
	
	
	//员工事后登记查看
	public List<Entertainapplyinfo> openIdEntertain(String openId);
	
	public List<Entertainapplyinfo> openIdbusinessapply(String openId);
	
	//领导人审批查看
	public List<Entertainapplyinfo> listApproveEntertainAppliyInfo(String openId);
	
	//领导人审批
	public int approveEntertainApplyinfo(int id,String taskId,String result,String comment);

	//查看状态
	public List<Entertainapplyinfo> statusEntertainApplyInfo(String openId);
	
	//事后登记
	public int postregistration(Entertainregisterinfo entertainregisterinfo,String taskId);
	
	//财务审批人查看
	public List<Entertainapplyinfo> finaceEntertainApplyinfo(String openId);
	
	//财务审批
	public int finaceEntertainApprove(Entertainregisterinfo entertainregisterinfo,int id,String taskId,String result,String comment);
	
	public Map<String,Object> searchEntertainApprove(Entertainapplyinfo entertainapplyinfo,String openId,Integer nowPage,Integer pageSize);
	
	public Object getLastSum(String department,String openId);
	
	public List<Entertainobjecttype> getType();
	
	public List<Entertaininvoiceunit> getInvoiceUnitType();
	
	public Object getYear();
	
	public List<Entertainannualbudget> wGetNowAnnual();
	
	public List<Entertainannualbudget> wGetNowAnnual1(String year);
	
	public List<Entertainhospitality> wGetHospitality();
	
	
	public List<EntertainAnnualInfoDTO> wGetAnnualBudget();
	
	public Object wGetSelectedUsed(String year);
	
	public List<Entertainhospitality> wGetAnnualBudgethospitality();
	
	public int wSetAdjust(String param);
	
	//web端查看
	public List<Entertainapplyinfo> wRGetAllEntertain(Integer start,Integer number);
	
	
	public List<Entertainapplyinfo> wGetAllApproved();
	
	public List<Entertainregisterinfo> registerDetail(String number,String taskId);
	
	public List<Entertainapplyinfo> wRGetRegisterEntertain();
	
	public int approveX(String number,String taskId);
	
	public int notThrough(String number,String taskId);
	
	public Entertainapplyinfo getapplyDetail(String number);
	
	public int insertAllVoucherNum(String number,String paidTime,String voucherNum);
	
	public List<Entertainregisterinfo> getOneRegister(String number);
	
	
	public List<Entertainapplyinfo> wgtServApply(Entertainapplyinfo entertainapplyinfo);
	
    public int updateEntertaion(Entertainregisterinfo entertainregisterinfo);
    
    public int export(List<Entertainapplyinfo> nlist,String path);
    
    public List<Map<String,Object>> countEntertain(String openId);

    //根据年份查询每一年的招待预算
	public List<Entertainhospitality> wGetSearchAnnual(String year);

	//根据对应基本id获取相应的调整详情
	public List<EntertainhospitalitySubsidiary> getAnnualBudgetDetail(String hospitalityId);

	//调整金额
	public Object wSetAdjust(String id, String money, String datenew, String date);
    
}
