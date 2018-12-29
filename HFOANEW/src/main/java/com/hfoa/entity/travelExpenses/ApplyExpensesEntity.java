package com.hfoa.entity.travelExpenses;

import java.io.Serializable;
import java.util.List;

import com.hfoa.entity.fatherEntity.publicEntity;


public class ApplyExpensesEntity extends publicEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;//����
	
	private String department;//����
	
	private String travelers;//������
	
	private String cause;//ԭ��
	
	private String tripDetails;//[{�����ء�Ŀ�ĵء����з�ʽ}]
	
	private String beginTime;//��������
	
	private String endTime;//��������
	
	private int travelDays;//��������
	
	private float totalBudget;//��Ԥ��
	
	private String isTest;//�Ƿ���������
	
	private String applyTime;//����ʱ��
	
	private String applyMan;//������
	
	private String approveMan;//������
	
	private String remarks;//��ע
	
	private String approveState;//״̬
	
	private String tripMode;//��̺����� ������1�����2
	
	private String startAddress;//����������
	
	private String middAddress;//�������ݿ�
	
	private String endAddress;//�������ݿ�
	
	private String openId;//�û�OpenId
	
	private String approveManOpenId;//������OpeniD
	
	private String comment;//����
	
	private String cCListOpenId;//������Id
	
	private String confirm;//�ж��ǲ��Ų���1�����ǲ�������2,Ա��ȷ��3
	
	private String cCListOpenIdName;//����������
	
	private List<TravelAddressDTO> tripDetails_list;//�������ݿ�
	
	private String taskId;//�������ݿ�
	
	private String title;
	
	private String url;
	
	private String canConfirm;
	
	private String canUpdate;
	
	
	private String state;
	
	
	
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private float vehicleCost;
	private float foodAllowance;//��ʳ�Ѳ���
	private float hotelExpense;//ס�޷�
	private float parValueAllowance;//Ʊ�油��
    private float urbanTraffic;//���ڽ�ͨ
	private float otherCost;//��������
	private float repayCost;//������
	private float supplementalCost;//������
	private float inputTax;//����˰��
	private float totalExpenses;//�����ܶ�
	private String paidTime;//����ʱ��
	private String fundSource;//������Դ
	private String voucherNum;//ƾ֤��
	private String isTestCost;//�Ƿ�Ϊ�����
	private String testSite;//�����ֳ�
	private String illustration;//˵��  
	
	private String searchBeginTime;//��ѯʱ��ʼʱ���
	private String searchEndTime;//��ѯʱ����ʱ���

	
	
	
	
	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<TravelAddressDTO> getTripDetails_list() {
		return tripDetails_list;
	}

	public void setTripDetails_list(List<TravelAddressDTO> tripDetails_list) {
		this.tripDetails_list = tripDetails_list;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getTravelers() {
		return travelers;
	}

	public void setTravelers(String travelers) {
		this.travelers = travelers;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public String getTripDetails() {
		return tripDetails;
	}

	public void setTripDetails(String tripDetails) {
		this.tripDetails = tripDetails;
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

	public int getTravelDays() {
		return travelDays;
	}

	public void setTravelDays(int travelDays) {
		this.travelDays = travelDays;
	}

	public float getTotalBudget() {
		return totalBudget;
	}

	public void setTotalBudget(float totalBudget) {
		this.totalBudget = totalBudget;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getApproveState() {
		return approveState;
	}

	public void setApproveState(String approveState) {
		this.approveState = approveState;
	}


	public String getTripMode() {
		return tripMode;
	}

	public void setTripMode(String tripMode) {
		this.tripMode = tripMode;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public String getMiddAddress() {
		return middAddress;
	}

	public void setMiddAddress(String middAddress) {
		this.middAddress = middAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getApproveManOpenId() {
		return approveManOpenId;
	}

	public void setApproveManOpenId(String approveManOpenId) {
		this.approveManOpenId = approveManOpenId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getcCListOpenId() {
		return cCListOpenId;
	}

	public void setcCListOpenId(String cCListOpenId) {
		this.cCListOpenId = cCListOpenId;
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

	
	
	
	
	public String getcCListOpenIdName() {
		return cCListOpenIdName;
	}

	public void setcCListOpenIdName(String cCListOpenIdName) {
		this.cCListOpenIdName = cCListOpenIdName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "ApplyExpensesEntity [id=" + id + ", department=" + department + ", travelers=" + travelers + ", cause="
				+ cause + ", tripDetails=" + tripDetails + ", beginTime=" + beginTime + ", endTime=" + endTime
				+ ", travelDays=" + travelDays + ", totalBudget=" + totalBudget + ", isTest=" + isTest + ", applyTime="
				+ applyTime + ", applyMan=" + applyMan + ", approveMan=" + approveMan + ", remarks=" + remarks
				+ ", approveState=" + approveState + ", tripMode=" + tripMode + ", startAddress=" + startAddress
				+ ", middAddress=" + middAddress + ", endAddress=" + endAddress + ", openId=" + openId
				+ ", approveManOpenId=" + approveManOpenId + ", comment=" + comment + ", cCListOpenId=" + cCListOpenId
				+ ", confirm=" + confirm + ", cCListOpenIdName=" + cCListOpenIdName + ", tripDetails_list="
				+ tripDetails_list + ", taskId=" + taskId + ", title=" + title + ", url=" + url + ", canConfirm="
				+ canConfirm + ", canUpdate=" + canUpdate + ", state=" + state + ", vehicleCost=" + vehicleCost
				+ ", foodAllowance=" + foodAllowance + ", hotelExpense=" + hotelExpense + ", parValueAllowance="
				+ parValueAllowance + ", urbanTraffic=" + urbanTraffic + ", otherCost=" + otherCost + ", repayCost="
				+ repayCost + ", supplementalCost=" + supplementalCost + ", inputTax=" + inputTax + ", totalExpenses="
				+ totalExpenses + ", paidTime=" + paidTime + ", fundSource=" + fundSource + ", voucherNum=" + voucherNum
				+ ", isTestCost=" + isTestCost + ", testSite=" + testSite + ", illustration=" + illustration
				+ ", searchBeginTime=" + searchBeginTime + ", searchEndTime=" + searchEndTime + "]";
	}


	






	
	
	
		
}
