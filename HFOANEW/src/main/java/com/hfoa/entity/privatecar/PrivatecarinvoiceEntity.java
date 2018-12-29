package com.hfoa.entity.privatecar;

import java.io.Serializable;
import java.util.List;

import com.hfoa.entity.fatherEntity.publicEntity;


public class PrivatecarinvoiceEntity extends publicEntity implements Serializable{

	
	private String applyId;//主键
	
	private String applyMan;//发气人
	
	private String approveMan;//审批人
	
	private String applyTime;//时间
	
	private String sum;//总金额
	
	private String sureLength;//
	
	private String voucherNum;//凭证号
	
	private String status;//状态
	
	private String paidTime;//时间
	
	private String applyIds;//你选中的那先私车的主键使用逗号拼接给我 ；例如 2018001,2018002
	
	private String title;
	
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
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

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getSureLength() {
		return sureLength;
	}

	public void setSureLength(String sureLength) {
		this.sureLength = sureLength;
	}

	public String getVoucherNum() {
		return voucherNum;
	}

	public void setVoucherNum(String voucherNum) {
		this.voucherNum = voucherNum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaidTime() {
		return paidTime;
	}

	public void setPaidTime(String paidTime) {
		this.paidTime = paidTime;
	}

	public String getApplyIds() {
		return applyIds;
	}

	public void setApplyIds(String applyIds) {
		this.applyIds = applyIds;
	}

	@Override
	public String toString() {
		return "PrivatecarinvoiceEntity [applyId=" + applyId + ", applyMan=" + applyMan + ", approveMan=" + approveMan
				+ ", applyTime=" + applyTime + ", sum=" + sum + ", sureLength=" + sureLength + ", voucherNum="
				+ voucherNum + ", status=" + status + ", paidTime=" + paidTime + ", applyIds=" + applyIds + ", title="
				+ title + "]";
	}
	
	
	
	
	
		
}
