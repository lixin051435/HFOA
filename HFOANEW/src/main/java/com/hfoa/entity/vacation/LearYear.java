package com.hfoa.entity.vacation;

import java.io.Serializable;
import java.util.List;

import com.hfoa.entity.fatherEntity.publicEntity;

public class LearYear extends publicEntity implements Serializable {

	/**
	 * 年假实体类
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;//主键
	
	private String user_id;//用户ID
	
	private String department;//部门
	
	private String applyMan;//申请人
	
	private String vacation;//休假
	
	private String applyTime;//申请时间
	
	private String Confirm;//综合部确认一次 第一次插入为0确认完成后改成1
	
	private String leaveType;//休假类型
	
	
	private String frequency;//休假次数
	
	private String status;//状态
	
	private String title;
	/**
	 * 状态 1:待部门审批
	 * 	  2:驳回待修改
	 * 	  
	 * 	  3:待员工确认
	 * 	  5:完结
	 * 	  6:放弃
	 * 	  7:转接到第二年
	 * 	  8:现金补偿
	 * 	  9:异常
	 */
	
	private String remarks;//备注
	
	private String approveMan;//审批人
	
	private String state;//数据无效转态
	
	private String openId;//微信唯一标识

	
	private String approveManOpenId;//审批人标识
	
	private String sfyc;
	
	private String taskId;
	
	
	private String url;
	
	private String conConfirm;
	
	private String conUpdate;
	
	private String canRevoke;
	
	
	


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

	public String getConConfirm() {
		return conConfirm;
	}

	public void setConConfirm(String conConfirm) {
		this.conConfirm = conConfirm;
	}

	public String getConUpdate() {
		return conUpdate;
	}

	public void setConUpdate(String conUpdate) {
		this.conUpdate = conUpdate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getApproveManOpenId() {
		return approveManOpenId;
	}

	public void setApproveManOpenId(String approveManOpenId) {
		this.approveManOpenId = approveManOpenId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public List<LearTime> getLeaver() {
		return leaver;
	}

	public void setLeaver(List<LearTime> leaver) {
		this.leaver = leaver;
	}

	private List<LearTime> leaver;
	
	private LearTime time;
	
	//不在数据库的数据
	
	public LearTime getTime() {
		return time;
	}

	public void setTime(LearTime time) {
		this.time = time;
	}

	private String beginTime;
	
	private String endTime;
	
	private int days;
	
	private int pc;
	
	private String ids;
	
	


	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getState() {
		return state;
	}

	public String getConfirm() {
		return Confirm;
	}

	public void setConfirm(String confirm) {
		Confirm = confirm;
	}
	
	

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getApplyMan() {
		return applyMan;
	}

	public void setApplyMan(String applyMan) {
		this.applyMan = applyMan;
	}

	public String getVacation() {
		return vacation;
	}

	public void setVacation(String vacation) {
		this.vacation = vacation;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getApproveMan() {
		return approveMan;
	}

	public void setApproveMan(String approveMan) {
		this.approveMan = approveMan;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSfyc() {
		return sfyc;
	}

	public void setSfyc(String sfyc) {
		this.sfyc = sfyc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "LearYear [id=" + id + ", user_id=" + user_id + ", department=" + department + ", applyMan=" + applyMan
				+ ", vacation=" + vacation + ", applyTime=" + applyTime + ", Confirm=" + Confirm + ", leaveType="
				+ leaveType + ", frequency=" + frequency + ", status=" + status + ", title=" + title + ", remarks="
				+ remarks + ", approveMan=" + approveMan + ", state=" + state + ", openId=" + openId
				+ ", approveManOpenId=" + approveManOpenId + ", sfyc=" + sfyc + ", taskId=" + taskId + ", url=" + url
				+ ", conConfirm=" + conConfirm + ", conUpdate=" + conUpdate + ", canRevoke=" + canRevoke + ", leaver="
				+ leaver + ", time=" + time + ", beginTime=" + beginTime + ", endTime=" + endTime + ", days=" + days
				+ ", pc=" + pc + ", ids=" + ids + "]";
	}








	

	


	
	

}
