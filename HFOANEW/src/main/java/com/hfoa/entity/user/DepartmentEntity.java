package com.hfoa.entity.user;

import java.io.Serializable;

public class DepartmentEntity implements Serializable{

	private int ID;
	
	private String DepartmentName;
	
	private String DepartId;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getDepartmentName() {
		return DepartmentName;
	}

	public void setDepartmentName(String departmentName) {
		DepartmentName = departmentName;
	}

	public String getDepartId() {
		return DepartId;
	}

	public void setDepartId(String departId) {
		DepartId = departId;
	}

	@Override
	public String toString() {
		return "DepartmentEntity [ID=" + ID + ", DepartmentName=" + DepartmentName + ", DepartId=" + DepartId + "]";
	}

	
	
	
		
}
