package com.hfoa.entity.car;

import java.io.Serializable;

public class Cargasinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String carId;
	
	private String cardBalance;
	
	private String ExecuteTime;
	
	private String changeValue;
	
	private String changeType;
	
	private String remark;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getCardBalance() {
		return cardBalance;
	}

	public void setCardBalance(String cardBalance) {
		this.cardBalance = cardBalance;
	}

	public String getExecuteTime() {
		return ExecuteTime;
	}

	public void setExecuteTime(String executeTime) {
		ExecuteTime = executeTime;
	}

	public String getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(String changeValue) {
		this.changeValue = changeValue;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Cargasinfo [id=" + id + ", carId=" + carId + ", cardBalance=" + cardBalance + ", ExecuteTime="
				+ ExecuteTime + ", changeValue=" + changeValue + ", changeType=" + changeType + ", remark=" + remark
				+ "]";
	}
	
	
	
	
	
	 
		
}
