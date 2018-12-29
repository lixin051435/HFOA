package com.hfoa.entity.car;

import java.util.List;

public class CarApplyDTO {

	private String department;//部门名称
	
	private String carNum;//车牌号
	
	private Double cardistance;//行驶里程

	private String starttime;//开始时间
	
	private  String endtime;//结束时间
	
	private String applyman;//申请人
	
	private String carinfo;//车辆信息
	
	private int status;//状态
	
	private List<String> list;//业务id集合
	
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getApplyman() {
		return applyman;
	}

	public void setApplyman(String applyman) {
		this.applyman = applyman;
	}

	public String getCarinfo() {
		return carinfo;
	}

	public void setCarinfo(String carinfo) {
		this.carinfo = carinfo;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public Double getCardistance() {
		return cardistance;
	}

	public void setCardistance(Double cardistance) {
		this.cardistance = cardistance;
	}
	
}
