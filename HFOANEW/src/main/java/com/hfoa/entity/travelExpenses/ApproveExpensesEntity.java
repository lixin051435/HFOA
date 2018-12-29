package com.hfoa.entity.travelExpenses;

import java.io.Serializable;
import java.util.List;


public class ApproveExpensesEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String travelExpenseId;//差率费申请单id
	private float vehicleCost;//交通工具金额
	private float foodAllowance;//伙食费补助
	private float hotelExpense;//住宿费
	private float parValueAllowance;//票面补助
	private float urbanTraffic;//市内交通
	private float otherCost;//其他费用
	private float repayCost;//还款金额
	private float supplementalCost;//补领金额
	private float inputTax;//进项税额
	private float totalExpenses;//报销总额
	private String paidTime;//报销时间
	private String fundSource;//经费来源
	private String voucherNum;//凭证号
	private String isTestCost;//是否为试验费
	private String testSite;//试验现场
	private String illustration;//说明

	  //不在数据库字段
	private String searchBeginTime;//查询时开始时间段
	private String searchEndTime;//查询时结束时间段
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTravelExpenseId() {
		return travelExpenseId;
	}
	public void setTravelExpenseId(String travelExpenseId) {
		this.travelExpenseId = travelExpenseId;
	}
	public float getVehicleCost() {
		return vehicleCost;
	}
	public void setVehicleCost(float vehicleCost) {
		this.vehicleCost = vehicleCost;
	}
	public float getFoodAllowance() {
		return foodAllowance;
	}
	public void setFoodAllowance(float foodAllowance) {
		this.foodAllowance = foodAllowance;
	}
	public float getHotelExpense() {
		return hotelExpense;
	}
	public void setHotelExpense(float hotelExpense) {
		this.hotelExpense = hotelExpense;
	}
	public float getParValueAllowance() {
		return parValueAllowance;
	}
	public void setParValueAllowance(float parValueAllowance) {
		this.parValueAllowance = parValueAllowance;
	}
	public float getUrbanTraffic() {
		return urbanTraffic;
	}
	public void setUrbanTraffic(float urbanTraffic) {
		this.urbanTraffic = urbanTraffic;
	}
	public float getOtherCost() {
		return otherCost;
	}
	public void setOtherCost(float otherCost) {
		this.otherCost = otherCost;
	}
	public float getRepayCost() {
		return repayCost;
	}
	public void setRepayCost(float repayCost) {
		this.repayCost = repayCost;
	}
	public float getSupplementalCost() {
		return supplementalCost;
	}
	public void setSupplementalCost(float supplementalCost) {
		this.supplementalCost = supplementalCost;
	}
	public float getInputTax() {
		return inputTax;
	}
	public void setInputTax(float inputTax) {
		this.inputTax = inputTax;
	}
	public float getTotalExpenses() {
		return totalExpenses;
	}
	public void setTotalExpenses(float totalExpenses) {
		this.totalExpenses = totalExpenses;
	}
	public String getPaidTime() {
		return paidTime;
	}
	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}
	public String getFundSource() {
		return fundSource;
	}
	public void setFundSource(String fundSource) {
		this.fundSource = fundSource;
	}
	public String getVoucherNum() {
		return voucherNum;
	}
	public void setVoucherNum(String voucherNum) {
		this.voucherNum = voucherNum;
	}
	public String getIsTestCost() {
		return isTestCost;
	}
	public void setIsTestCost(String isTestCost) {
		this.isTestCost = isTestCost;
	}
	public String getTestSite() {
		return testSite;
	}
	public void setTestSite(String testSite) {
		this.testSite = testSite;
	}
	public String getIllustration() {
		return illustration;
	}
	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}
	public String getSearchBeginTime() {
		return searchBeginTime;
	}
	public void setSearchBeginTime(String searchBeginTime) {
		this.searchBeginTime = searchBeginTime;
	}
	public String getSearchEndTime() {
		return searchEndTime;
	}
	public void setSearchEndTime(String searchEndTime) {
		this.searchEndTime = searchEndTime;
	}
	@Override
	public String toString() {
		return "ApproveExpensesEntity [id=" + id + ", travelExpenseId=" + travelExpenseId + ", vehicleCost="
				+ vehicleCost + ", foodAllowance=" + foodAllowance + ", hotelExpense=" + hotelExpense
				+ ", parValueAllowance=" + parValueAllowance + ", urbanTraffic=" + urbanTraffic + ", otherCost="
				+ otherCost + ", repayCost=" + repayCost + ", supplementalCost=" + supplementalCost + ", inputTax="
				+ inputTax + ", totalExpenses=" + totalExpenses + ", paidTime=" + paidTime + ", fundSource="
				+ fundSource + ", voucherNum=" + voucherNum + ", isTestCost=" + isTestCost + ", testSite=" + testSite
				+ ", illustration=" + illustration + ", searchBeginTime=" + searchBeginTime + ", searchEndTime="
				+ searchEndTime + "]";
	}
	
	

	


	
	
	
		
}
