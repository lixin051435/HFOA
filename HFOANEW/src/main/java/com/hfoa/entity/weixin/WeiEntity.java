package com.hfoa.entity.weixin;

import java.util.List;

public class WeiEntity {

	private String applyTime;
	
	private String approveMan;
	
	private String applyMan;
	
	private String departMent;
	
	private String leaveType;
	
	private String status;
	
	private String days;
	
	private String frequency;
	
	private String begingTime;
	
	private String endTime;
	
	private String tripContent;//行程内容
	
	private String travelPurpose;//差旅目的
	
	private String cause;
	
	private String masterBudget;//总预算
	
	private String type;
	
	private String title;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getApproveMan() {
		return approveMan;
	}

	public void setApproveMan(String approveMan) {
		this.approveMan = approveMan;
	}

	public String getApplyMan() {
		return applyMan;
	}

	public void setApplyMan(String applyMan) {
		this.applyMan = applyMan;
	}

	public String getDepartMent() {
		return departMent;
	}

	public void setDepartMent(String departMent) {
		this.departMent = departMent;
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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getBegingTime() {
		return begingTime;
	}

	public void setBegingTime(String begingTime) {
		this.begingTime = begingTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTripContent() {
		return tripContent;
	}

	public void setTripContent(String tripContent) {
		this.tripContent = tripContent;
	}

	public String getTravelPurpose() {
		return travelPurpose;
	}

	public void setTravelPurpose(String travelPurpose) {
		this.travelPurpose = travelPurpose;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getMasterBudget() {
		return masterBudget;
	}

	public void setMasterBudget(String masterBudget) {
		this.masterBudget = masterBudget;
	}

	@Override
	public String toString() {
		return "WeiEntity [applyTime=" + applyTime + ", approveMan=" + approveMan + ", applyMan=" + applyMan
				+ ", departMent=" + departMent + ", leaveType=" + leaveType + ", status=" + status + ", days=" + days
				+ ", frequency=" + frequency + ", begingTime=" + begingTime + ", endTime=" + endTime + ", tripContent="
				+ tripContent + ", travelPurpose=" + travelPurpose + ", cause=" + cause + ", masterBudget="
				+ masterBudget + ", type=" + type + ", title=" + title + "]";
	}
	
}
