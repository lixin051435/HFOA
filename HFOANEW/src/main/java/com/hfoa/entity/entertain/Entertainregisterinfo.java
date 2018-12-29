package com.hfoa.entity.entertain;

import java.io.Serializable;

public class Entertainregisterinfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private int id;
	
	private String number; // ��������
	
	private String invoiceDate;// ��Ʊ����
	
	private String invoiceContent;// ��Ʊ����
	
	private String invoiceSum;// ��Ʊ���
	
	private String invoiceNum;// ��Ʊ��������
	
	private String paidTime;// ����ʱ��
	
	private String voucherNum;// ƾ֤��
	
	private String invoiceUnit;// ��Ʊ��λ
	
	private String status;// ״̬
	
	private String registerMan;// �Ǽ���
	
	private String remark;// ��ע
	
	private String invoiceNumber;//��Ʊ��
	
	private String wineSum;//��ˮ���
	
	private String enterSum;//�д��ܽ��
	
	private String personSum;//�˾����
	
	private String taskId;

	private int registerId;
	

	
	
	

	public int getRegisterId() {
		return registerId;
	}

	public void setRegisterId(int registerId) {
		this.registerId = registerId;
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


	public String getInvoiceSum() {
		return invoiceSum;
	}

	public void setInvoiceSum(String invoiceSum) {
		this.invoiceSum = invoiceSum;
	}

	public String getInvoiceNum() {
		return invoiceNum;
	}

	public void setInvoiceNum(String invoiceNum) {
		this.invoiceNum = invoiceNum;
	}

	public String getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}

	public String getVoucherNum() {
		return voucherNum;
	}

	public void setVoucherNum(String voucherNum) {
		this.voucherNum = voucherNum;
	}

	public String getInvoiceUnit() {
		return invoiceUnit;
	}

	public void setInvoiceUnit(String invoiceUnit) {
		this.invoiceUnit = invoiceUnit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRegisterMan() {
		return registerMan;
	}

	public void setRegisterMan(String registerMan) {
		this.registerMan = registerMan;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getWineSum() {
		return wineSum;
	}

	public void setWineSum(String wineSum) {
		this.wineSum = wineSum;
	}

	public String getEnterSum() {
		return enterSum;
	}

	public void setEnterSum(String enterSum) {
		this.enterSum = enterSum;
	}

	public String getPersonSum() {
		return personSum;
	}

	public void setPersonSum(String personSum) {
		this.personSum = personSum;
	}


	public String getInvoiceContent() {
		return invoiceContent;
	}

	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Override
	public String toString() {
		return "Entertainregisterinfo [id=" + id + ", number=" + number + ", invoiceDate=" + invoiceDate
				+ ", invoiceContent=" + invoiceContent + ", invoiceSum=" + invoiceSum + ", invoiceNum=" + invoiceNum
				+ ", paidTime=" + paidTime + ", voucherNum=" + voucherNum + ", invoiceUnit=" + invoiceUnit + ", status="
				+ status + ", registerMan=" + registerMan + ", remark=" + remark + ", invoiceNumber=" + invoiceNumber
				+ ", wineSum=" + wineSum + ", enterSum=" + enterSum + ", personSum=" + personSum + ", taskId=" + taskId
				+ "]";
	}


		
}
