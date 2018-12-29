package com.hfoa.entity.vacation;

import java.io.Serializable;

public class LearTime implements Serializable{

	/**
	 * 年假附属字表
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;//主键
	
	private String leave_id;//年假ID
	
	private String beingTime;//开始时间
	
	private String endTime;//结束时间
	
	private String days;//天数
	
	private String state;//数据无效转态
	
	private String sfyc;//是否异常
	
	private String status;
	
	
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLeave_id() {
		return leave_id;
	}

	public void setLeave_id(String leave_id) {
		this.leave_id = leave_id;
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

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSfyc() {
		return sfyc;
	}

	public void setSfyc(String sfyc) {
		this.sfyc = sfyc;
	}

	
	
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "LearTime [id=" + id + ", leave_id=" + leave_id + ", beingTime=" + beingTime + ", endTime=" + endTime
				+ ", days=" + days + ", state=" + state + ", sfyc=" + sfyc + ", status=" + status + "]";
	}



	
	

}
