package com.hfoa.entity.entertain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;

import com.hfoa.entity.fatherEntity.publicEntity;

public class Entertainapplyinfo extends publicEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private int id;
	
	private String number;//���
	
	private String department;//����
	
	private String applyTime;//����ʱ��
	
	private String entertainObject;//�д���λ
	
	private String entertainReason;//ԭ��
	
	private String entertainNum;//�д�����
	
	private String accompanyNum;//��ͬ����
	
	private String perBudget;//�˾�Ԥ��
	
	private String masterBudget;//��Ԥ��
	
	private String remainingBudget;//ʣ��Ԥ��
	
	private String entertainCategory;//�д����
	
	private String manager;//�Ӵ���
	
	private String approver;//������
	
	private String approverOpenid;//������openId
	
	
	private String status;//״̬
	
	private String remark;//��ע
	
	private int registerNum;// �������º������
	
	private String paidTime;
	
	private String approveTime;//����ʱ��
	
	private String ifWine;//�Ƿ���Ҫ��ˮ
	
	private String wineType;// ��ˮ���ͣ���ϸ��[{"name":"�׾�","name1":"�ž���","value":"1"},{"name":"���","name1":"���","value":"2"}]����
	
	private String wineNum;// ��ˮ����
	
	private String wineOther;// ��ˮ����
	
	private String wineSum;
	
	private String ifBefore;// �Ƿ���ύ
	
	private String beforeDate;// ԭ��������
	
	private String openId;//С����openId
	
	private String taskId;//����id
	
	private String confirm;//����
	
	
	
	private String invoiceNumber;
	private String invoiceSum;
	
	private String paidTime1;
	
	private String startTime;
	
	private String beginTime;
	
	private String endTime;
	
	private String url;
	
	private String canConfirm;
	
	private String canUpdate;//0左边  1右边
	
	private String canReimburse;
	
	private String showButton;
	
	private String imgUrl;
	
	private List<Entertainregisterinfo> entertainregisterinfo;
	
	
	
	
	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<Entertainregisterinfo> getEntertainregisterinfo() {
		return entertainregisterinfo;
	}

	public void setEntertainregisterinfo(List<Entertainregisterinfo> entertainregisterinfo) {
		this.entertainregisterinfo = entertainregisterinfo;
	}

	public String getShowButton() {
		return showButton;
	}

	public void setShowButton(String showButton) {
		this.showButton = showButton;
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

	public String getPaidTime1() {
		return paidTime1;
	}

	public void setPaidTime1(String paidTime1) {
		this.paidTime1 = paidTime1;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceSum() {
		return invoiceSum;
	}

	public void setInvoiceSum(String invoiceSum) {
		this.invoiceSum = invoiceSum;
	}

	public String getEeterSum() {
		return eeterSum;
	}

	public void setEeterSum(String eeterSum) {
		this.eeterSum = eeterSum;
	}

	public String getPersonSum() {
		return personSum;
	}

	public void setPersonSum(String personSum) {
		this.personSum = personSum;
	}

	private String eeterSum;
	private String personSum;
	
	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	private String title;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getEntertainObject() {
		return entertainObject;
	}

	public void setEntertainObject(String entertainObject) {
		this.entertainObject = entertainObject;
	}

	public String getEntertainReason() {
		return entertainReason;
	}

	public void setEntertainReason(String entertainReason) {
		this.entertainReason = entertainReason;
	}

	public String getEntertainNum() {
		return entertainNum;
	}

	public void setEntertainNum(String entertainNum) {
		this.entertainNum = entertainNum;
	}

	public String getAccompanyNum() {
		return accompanyNum;
	}

	public void setAccompanyNum(String accompanyNum) {
		this.accompanyNum = accompanyNum;
	}

	public String getPerBudget() {
		return perBudget;
	}

	public void setPerBudget(String perBudget) {
		this.perBudget = perBudget;
	}

	public String getMasterBudget() {
		return masterBudget;
	}

	public void setMasterBudget(String masterBudget) {
		this.masterBudget = masterBudget;
	}

	public String getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(String remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	public String getEntertainCategory() {
		return entertainCategory;
	}

	public void setEntertainCategory(String entertainCategory) {
		this.entertainCategory = entertainCategory;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getRegisterNum() {
		return registerNum;
	}

	public void setRegisterNum(int registerNum) {
		this.registerNum = registerNum;
	}

	public String getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}

	public String getApproveTime() {
		return approveTime;
	}

	public void setApproveTime(String approveTime) {
		this.approveTime = approveTime;
	}

	public String getIfWine() {
		return ifWine;
	}

	public void setIfWine(String ifWine) {
		this.ifWine = ifWine;
	}

	public String getWineType() {
		return wineType;
	}

	public void setWineType(String wineType) {
		this.wineType = wineType;
	}

	public String getWineNum() {
		return wineNum;
	}

	public void setWineNum(String wineNum) {
		this.wineNum = wineNum;
	}

	public String getWineOther() {
		return wineOther;
	}

	public void setWineOther(String wineOther) {
		this.wineOther = wineOther;
	}

	public String getWineSum() {
		return wineSum;
	}

	public void setWineSum(String wineSum) {
		this.wineSum = wineSum;
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

	public String getApproverOpenid() {
		return approverOpenid;
	}

	public void setApproverOpenid(String approverOpenid) {
		this.approverOpenid = approverOpenid;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "Entertainapplyinfo [id=" + id + ", number=" + number + ", department=" + department + ", applyTime="
				+ applyTime + ", entertainObject=" + entertainObject + ", entertainReason=" + entertainReason
				+ ", entertainNum=" + entertainNum + ", accompanyNum=" + accompanyNum + ", perBudget=" + perBudget
				+ ", masterBudget=" + masterBudget + ", remainingBudget=" + remainingBudget + ", entertainCategory="
				+ entertainCategory + ", manager=" + manager + ", approver=" + approver + ", approverOpenid="
				+ approverOpenid + ", status=" + status + ", remark=" + remark + ", registerNum=" + registerNum
				+ ", paidTime=" + paidTime + ", approveTime=" + approveTime + ", ifWine=" + ifWine + ", wineType="
				+ wineType + ", wineNum=" + wineNum + ", wineOther=" + wineOther + ", wineSum=" + wineSum
				+ ", ifBefore=" + ifBefore + ", beforeDate=" + beforeDate + ", openId=" + openId + ", taskId=" + taskId
				+ ", confirm=" + confirm + ", invoiceNumber=" + invoiceNumber + ", invoiceSum=" + invoiceSum
				+ ", paidTime1=" + paidTime1 + ", startTime=" + startTime + ", beginTime=" + beginTime + ", endTime="
				+ endTime + ", url=" + url + ", canConfirm=" + canConfirm + ", canUpdate=" + canUpdate
				+ ", canReimburse=" + canReimburse + ", showButton=" + showButton + ", imgUrl=" + imgUrl
				+ ", entertainregisterinfo=" + entertainregisterinfo + ", eeterSum=" + eeterSum + ", personSum="
				+ personSum + ", title=" + title + "]";
	}










	
		
}
