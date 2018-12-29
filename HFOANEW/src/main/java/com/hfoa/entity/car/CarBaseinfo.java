package com.hfoa.entity.car;

import java.io.Serializable;

public class CarBaseinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;//主键
	
	private String carCode;//车名
	
	private String carType;//类型
	
	private String carNum;//车牌号
	
	private String carUnit;//公司
	
	private String carBuyTime;//购买时间
	
	private String carDetailInfo;//
	
	private String carInsuranceInfo1;
	
	private String carInsuranceInfo;
	
	private String cardVale;
	
	private String carDistance;
	
	private String carInsuranceInfoDetal;
	
	private String others;
	
	private String peasonNum;
	
	private String carState;
	
	private String suspendDay;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public String getCarNum() {
		return carNum;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public String getCarUnit() {
		return carUnit;
	}

	public void setCarUnit(String carUnit) {
		this.carUnit = carUnit;
	}

	public String getCarBuyTime() {
		return carBuyTime;
	}

	public void setCarBuyTime(String carBuyTime) {
		this.carBuyTime = carBuyTime;
	}

	public String getCarDetailInfo() {
		return carDetailInfo;
	}

	public void setCarDetailInfo(String carDetailInfo) {
		this.carDetailInfo = carDetailInfo;
	}

	public String getCarInsuranceInfo1() {
		return carInsuranceInfo1;
	}

	public void setCarInsuranceInfo1(String carInsuranceInfo1) {
		this.carInsuranceInfo1 = carInsuranceInfo1;
	}

	public String getCarInsuranceInfo() {
		return carInsuranceInfo;
	}

	public void setCarInsuranceInfo(String carInsuranceInfo) {
		this.carInsuranceInfo = carInsuranceInfo;
	}

	public String getCardVale() {
		return cardVale;
	}

	public void setCardVale(String cardVale) {
		this.cardVale = cardVale;
	}

	public String getCarDistance() {
		return carDistance;
	}

	public void setCarDistance(String carDistance) {
		this.carDistance = carDistance;
	}

	public String getCarInsuranceInfoDetal() {
		return carInsuranceInfoDetal;
	}

	public void setCarInsuranceInfoDetal(String carInsuranceInfoDetal) {
		this.carInsuranceInfoDetal = carInsuranceInfoDetal;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getPeasonNum() {
		return peasonNum;
	}

	public void setPeasonNum(String peasonNum) {
		this.peasonNum = peasonNum;
	}

	public String getCarState() {
		return carState;
	}

	public void setCarState(String carState) {
		this.carState = carState;
	}

	public String getSuspendDay() {
		return suspendDay;
	}

	public void setSuspendDay(String suspendDay) {
		this.suspendDay = suspendDay;
	}

	@Override
	public String toString() {
		return "CarBaseinfo [id=" + id + ", carCode=" + carCode + ", carType=" + carType + ", carNum=" + carNum
				+ ", carUnit=" + carUnit + ", carBuyTime=" + carBuyTime + ", carDetailInfo=" + carDetailInfo
				+ ", carInsuranceInfo1=" + carInsuranceInfo1 + ", carInsuranceInfo=" + carInsuranceInfo + ", cardVale="
				+ cardVale + ", carDistance=" + carDistance + ", carInsuranceInfoDetal=" + carInsuranceInfoDetal
				+ ", others=" + others + ", peasonNum=" + peasonNum + ", carState=" + carState + ", suspendDay="
				+ suspendDay + "]";
	}
	
	
	
	 
		
}
