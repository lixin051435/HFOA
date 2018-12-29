package com.hfoa.entity.entertain;

public class EntertainAnnualInfoDTO {
	private int ID;

	private String Department; // 部门
	
	private String BudgetSum; // 预算金额
	
	private String CopileTime; // 编制时间

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getBudgetSum() {
		return BudgetSum;
	}

	public void setBudgetSum(String budgetSum) {
		BudgetSum = budgetSum;
	}

	public String getCopileTime() {
		return CopileTime;
	}

	public void setCopileTime(String copileTime) {
		CopileTime = copileTime;
	}

	@Override
	public String toString() {
		return "EntertainAnnualnfoDTO [ID=" + ID + ", Department=" + Department + ", BudgetSum=" + BudgetSum
				+ ", CopileTime=" + CopileTime + "]";
	}
	
	
	
	

}
