package com.hfoa.entity.car;

import java.io.Serializable;

import com.hfoa.entity.fatherEntity.publicEntity;
/**
 * 
 * @author wzx
 * 公车申请信息实体类
 */
public class CarApplyInfo2 extends publicEntity implements Serializable{

	private int ID;//id
	
	private String ApplyId;//审批编号
	
	private String DepartmentId;//部门id
	
	private String Department;//部门名称
	
	private String ApplyUserName;//申请人账号
	
	private String ApplyMan;//申请人真实姓名
	
	private String ApproveMan;//审批人账号
	
	private int CarId;//公车id
	
	private String CarType;//车辆类型
	
	private String CarCode;//车牌号
	
	private String Driver;//驾驶人
	
	private int CompareManNum;//陪同人数
	
	private float LengthBegin;//开始里程
	
	private float LengthEnd;//结束里程
	
	private double AccountLength;//总路程
	
	private String BeginTimePlan;//计划结束时间
	
	private String EndTimePlan;//计划开始时间
	
	private String BeginTime;//开始时间
	
	private String EndTime;//结束时间
	
	private String UseCarReason;//申请事由
	
	private String BeginPlace;//起始地
	
	private String EndPlace;//目的地
	
//	private int CarBeginExamin;//������ʼ����
	
	private String ApplyTime;//申请时间
	
	private String Remarks;//备注
		
	private Double AccountPlanTime;//计划消耗时间
	
	private Double AccountRealTime;//实际消耗时间
	
	private int Status;//̬业务状态

	private String RoleId;//存放流程接下来参与的角色id
	
	private String RoleName;//存放流程接下来参与的角色名称
	
	private String taskId;//任务Id 
	
	private String checkMan;//抄送人id
	
	private int ifApproval;//是否可审批
	
	private int ApprovalUserId;//审批人id
	
	private String state;//车辆状态
	
	private String midAddress1;//途径地方1
	
	private String midAddress2;//途径地方2
	
	private String midAddress3;//途径地方3
	
	private String RealApproveMan;//审批人真实姓名
	
	private String OutTreasuryMan;//出库审批人
	
	private String InTreasuryMan;//入库审批人
	//非数据库字段
	private String carTypeNum;//车辆类型车牌号
	
	private String carUrl;//车辆图片
	
	private String title;
	
	private String url;
	
	private String param;
	
	private String canUpdate;//是否可修改
	
	private String canRevoke;//是否可撤回
	
	private String comment;//驳回原因
	
	private int carryNum;//载客量
	
	public int getCarryNum() {
		return carryNum;
	}

	public void setCarryNum(int carryNum) {
		this.carryNum = carryNum;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCanUpdate() {
		return canUpdate;
	}

	public void setCanUpdate(String canUpdate) {
		this.canUpdate = canUpdate;
	}

	public String getCanRevoke() {
		return canRevoke;
	}

	public void setCanRevoke(String canRevoke) {
		this.canRevoke = canRevoke;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getOutTreasuryMan() {
		return OutTreasuryMan;
	}

	public void setOutTreasuryMan(String outTreasuryMan) {
		OutTreasuryMan = outTreasuryMan;
	}

	public String getInTreasuryMan() {
		return InTreasuryMan;
	}

	public void setInTreasuryMan(String inTreasuryMan) {
		InTreasuryMan = inTreasuryMan;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCarTypeNum() {
		return carTypeNum;
	}

	public void setCarTypeNum(String carTypeNum) {
		this.carTypeNum = carTypeNum;
	}

	public String getCarUrl() {
		return carUrl;
	}

	public void setCarUrl(String carUrl) {
		this.carUrl = carUrl;
	}

	public String getRealApproveMan() {
		return RealApproveMan;
	}

	public void setRealApproveMan(String realApproveMan) {
		RealApproveMan = realApproveMan;
	}

	public int getApprovalUserId() {
		return ApprovalUserId;
	}

	public void setApprovalUserId(int approvalUserId) {
		ApprovalUserId = approvalUserId;
	}

	public String getCheckMan() {
		return checkMan;
	}

	public void setCheckMan(String checkMan) {
		this.checkMan = checkMan;
	}

	public int getIfApproval() {
		return ifApproval;
	}

	public void setIfApproval(int ifApproval) {
		this.ifApproval = ifApproval;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getRoleId() {
		return RoleId;
	}

	public void setRoleId(String roleId) {
		RoleId = roleId;
	}

	public String getRoleName() {
		return RoleName;
	}

	public void setRoleName(String roleName) {
		RoleName = roleName;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getApplyId() {
		return ApplyId;
	}

	public void setApplyId(String applyId) {
		ApplyId = applyId;
	}

	public String getDepartmentId() {
		return DepartmentId;
	}

	public void setDepartmentId(String departmentId) {
		DepartmentId = departmentId;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getApplyUserName() {
		return ApplyUserName;
	}

	public void setApplyUserName(String applyUserName) {
		ApplyUserName = applyUserName;
	}

	public String getApplyMan() {
		return ApplyMan;
	}

	public void setApplyMan(String applyMan) {
		ApplyMan = applyMan;
	}

	public String getApproveMan() {
		return ApproveMan;
	}

	public void setApproveMan(String approveMan) {
		ApproveMan = approveMan;
	}

	public int getCarId() {
		return CarId;
	}

	public void setCarId(int carId) {
		CarId = carId;
	}

	public String getCarType() {
		return CarType;
	}

	public void setCarType(String carType) {
		CarType = carType;
	}

	public String getCarCode() {
		return CarCode;
	}

	public void setCarCode(String carCode) {
		CarCode = carCode;
	}

	public String getDriver() {
		return Driver;
	}

	public void setDriver(String driver) {
		Driver = driver;
	}

	public int getCompareManNum() {
		return CompareManNum;
	}

	public void setCompareManNum(int compareManNum) {
		CompareManNum = compareManNum;
	}

	public float getLengthBegin() {
		return LengthBegin;
	}

	public void setLengthBegin(float lengthBegin) {
		LengthBegin = lengthBegin;
	}

	public float getLengthEnd() {
		return LengthEnd;
	}

	public void setLengthEnd(float lengthEnd) {
		LengthEnd = lengthEnd;
	}

	public double getAccountLength() {
		return AccountLength;
	}

	public void setAccountLength(double accountLength) {
		AccountLength = accountLength;
	}

	public String getBeginTimePlan() {
		return BeginTimePlan;
	}

	public void setBeginTimePlan(String beginTimePlan) {
		BeginTimePlan = beginTimePlan;
	}

	public String getEndTimePlan() {
		return EndTimePlan;
	}

	public void setEndTimePlan(String endTimePlan) {
		EndTimePlan = endTimePlan;
	}

	public String getBeginTime() {
		return BeginTime;
	}

	public void setBeginTime(String beginTime) {
		BeginTime = beginTime;
	}

	public String getEndTime() {
		return EndTime;
	}

	public void setEndTime(String endTime) {
		EndTime = endTime;
	}

	public String getUseCarReason() {
		return UseCarReason;
	}

	public void setUseCarReason(String useCarReason) {
		UseCarReason = useCarReason;
	}

	public String getBeginPlace() {
		return BeginPlace;
	}

	public void setBeginPlace(String beginPlace) {
		BeginPlace = beginPlace;
	}

	public String getEndPlace() {
		return EndPlace;
	}

	public void setEndPlace(String endPlace) {
		EndPlace = endPlace;
	}

	public String getApplyTime() {
		return ApplyTime;
	}

	public void setApplyTime(String applyTime) {
		ApplyTime = applyTime;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public Double getAccountPlanTime() {
		return AccountPlanTime;
	}

	public void setAccountPlanTime(Double accountPlanTime) {
		AccountPlanTime = accountPlanTime;
	}

	public Double getAccountRealTime() {
		return AccountRealTime;
	}

	public void setAccountRealTime(Double accountRealTime) {
		AccountRealTime = accountRealTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMidAddress1() {
		return midAddress1;
	}

	public void setMidAddress1(String midAddress1) {
		this.midAddress1 = midAddress1;
	}

	public String getMidAddress2() {
		return midAddress2;
	}

	public void setMidAddress2(String midAddress2) {
		this.midAddress2 = midAddress2;
	}

	public String getMidAddress3() {
		return midAddress3;
	}

	public void setMidAddress3(String midAddress3) {
		this.midAddress3 = midAddress3;
	}

	public CarApplyInfo2() {
		super();
	}

}
