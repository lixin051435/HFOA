package com.hfoa.entity.privatecar;

import java.io.Serializable;
import java.util.List;

import com.hfoa.entity.fatherEntity.publicEntity;


public class PrivateCarEntity extends publicEntity implements Serializable{

	private String applyId;//申请编号
	
	private String department;//部门名称
	
	private String userCarTime;//用车时间
	
	private String reason;//申请事由
	
	private String beginAddress;//起始地
	
	private String passAddress;//途径地[{"addressName":"诺德中心","addressValue":"2"}]
	
	private String destination;//目的地
	
	private String status;//状态
	
	private double singleLength;//单程里程
	
	private double sureLength;//核定价格
	
	private double countLength;//计价里程
	
	private String applyMan;//申请人
	
	private String approveMan;//审批人
	
	private String applyTime;//申请时间
	
	private String sureTime;//
	
	private String approveTime;//审批时间
	
	private String ifPerform;//执行状态̬
	
	private String wayModel;//路线规划
	
	private String wayDetail;//规划路线编号
	
	private String doubleLength;//是否往返
	
	private String endLength;//行驶里程
	
	private String ifPass;//̬报销状态（已报销，未报销，财务驳回）
	
	private String ifSub;
	
	private String ifBefore;//是否补录
	
	private String beforeDate;//补录日期
	
	private String openId;
	
	private String approveOpenId;
	
	private String confirm;//�ж�״̬ 0�쵼���� 1���񲵻�
	
	private String taskId;
	
	private String title;

	private String url;
	
	private String canConfirm;
	
	private String canUpdate;//待处理 是0，进行中1 
	
	private String canReimburse;
	
	private String beingTime;
	
	private String endTime;
	//非数据库
	private String submitTime;//提交时间
	private String sum;//报销金额
	private String paidTime;//登记时间
	private String paidMan;//登记人
	private String voucherNum;//凭单号
	private int start;
	private int number;
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}

	public String getPaidMan() {
		return paidMan;
	}

	public void setPaidMan(String paidMan) {
		this.paidMan = paidMan;
	}

	public String getVoucherNum() {
		return voucherNum;
	}

	public void setVoucherNum(String voucherNum) {
		this.voucherNum = voucherNum;
	}

	public String getBeingTime() {
		return beingTime;
	}

	public void setBeingTime(String beingTime) {
		this.beingTime = beingTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCanReimburse() {
		return canReimburse;
	}

	public void setCanReimburse(String canReimburse) {
		this.canReimburse = canReimburse;
	}

	public String getCanConfirm() {
		return canConfirm;
	}

	public void setCanConfirm(String canConfirm) {
		this.canConfirm = canConfirm;
	}

	public String getCanUpdate() {
		return canUpdate;
	}

	public void setCanUpdate(String canUpdate) {
		this.canUpdate = canUpdate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getUserCarTime() {
		return userCarTime;
	}

	public void setUserCarTime(String userCarTime) {
		this.userCarTime = userCarTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getBeginAddress() {
		return beginAddress;
	}

	public void setBeginAddress(String beginAddress) {
		this.beginAddress = beginAddress;
	}

	public String getPassAddress() {
		return passAddress;
	}

	public void setPassAddress(String passAddress) {
		this.passAddress = passAddress;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	
	
	public double getSingleLength() {
		return singleLength;
	}

	public void setSingleLength(double singleLength) {
		this.singleLength = singleLength;
	}

	public double getSureLength() {
		return sureLength;
	}

	public void setSureLength(double sureLength) {
		this.sureLength = sureLength;
	}

	public double getCountLength() {
		return countLength;
	}

	public void setCountLength(double countLength) {
		this.countLength = countLength;
	}

	public String getApplyMan() {
		return applyMan;
	}

	public void setApplyMan(String applyMan) {
		this.applyMan = applyMan;
	}

	public String getApproveMan() {
		return approveMan;
	}

	public void setApproveMan(String approveMan) {
		this.approveMan = approveMan;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getSureTime() {
		return sureTime;
	}

	public void setSureTime(String sureTime) {
		this.sureTime = sureTime;
	}

	public String getApproveTime() {
		return approveTime;
	}

	public void setApproveTime(String approveTime) {
		this.approveTime = approveTime;
	}

	public String getIfPerform() {
		return ifPerform;
	}

	public void setIfPerform(String ifPerform) {
		this.ifPerform = ifPerform;
	}

	public String getWayModel() {
		return wayModel;
	}

	public void setWayModel(String wayModel) {
		this.wayModel = wayModel;
	}

	public String getWayDetail() {
		return wayDetail;
	}

	public void setWayDetail(String wayDetail) {
		this.wayDetail = wayDetail;
	}

	public String getDoubleLength() {
		return doubleLength;
	}

	public void setDoubleLength(String doubleLength) {
		this.doubleLength = doubleLength;
	}

	public String getEndLength() {
		return endLength;
	}

	public void setEndLength(String endLength) {
		this.endLength = endLength;
	}

	public String getIfPass() {
		return ifPass;
	}

	public void setIfPass(String ifPass) {
		this.ifPass = ifPass;
	}

	public String getIfSub() {
		return ifSub;
	}

	public void setIfSub(String ifSub) {
		this.ifSub = ifSub;
	}

	public String getIfBefore() {
		return ifBefore;
	}

	public void setIfBefore(String ifBefore) {
		this.ifBefore = ifBefore;
	}

	public String getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(String beforeDate) {
		this.beforeDate = beforeDate;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getApproveOpenId() {
		return approveOpenId;
	}

	public void setApproveOpenId(String approveOpenId) {
		this.approveOpenId = approveOpenId;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "PrivateCarEntity [applyId=" + applyId + ", department=" + department + ", userCarTime=" + userCarTime
				+ ", reason=" + reason + ", beginAddress=" + beginAddress + ", passAddress=" + passAddress
				+ ", destination=" + destination + ", status=" + status + ", singleLength=" + singleLength
				+ ", sureLength=" + sureLength + ", countLength=" + countLength + ", applyMan=" + applyMan
				+ ", approveMan=" + approveMan + ", applyTime=" + applyTime + ", sureTime=" + sureTime
				+ ", approveTime=" + approveTime + ", ifPerform=" + ifPerform + ", wayModel=" + wayModel
				+ ", wayDetail=" + wayDetail + ", doubleLength=" + doubleLength + ", endLength=" + endLength
				+ ", ifPass=" + ifPass + ", ifSub=" + ifSub + ", ifBefore=" + ifBefore + ", beforeDate=" + beforeDate
				+ ", openId=" + openId + ", approveOpenId=" + approveOpenId + ", confirm=" + confirm + ", taskId="
				+ taskId + ", title=" + title + ", url=" + url + ", canConfirm=" + canConfirm + ", canUpdate="
				+ canUpdate + ", canReimburse=" + canReimburse + ", beingTime=" + beingTime + ", endTime=" + endTime
				+ "]";
	}






	

	
	
	
		
}
