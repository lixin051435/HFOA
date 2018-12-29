package com.hfoa.entity.user;

import java.io.Serializable;

public class DutyEntity implements Serializable{

	private int ID;
	
	private String DutyName;
	
	private String DutyId;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getDutyName() {
		return DutyName;
	}

	public void setDutyName(String dutyName) {
		DutyName = dutyName;
	}

	public String getDutyId() {
		return DutyId;
	}

	public void setDutyId(String dutyId) {
		DutyId = dutyId;
	}

	@Override
	public String toString() {
		return "DutyEntity [ID=" + ID + ", DutyName=" + DutyName + ", DutyId=" + DutyId + "]";
	}
	
		
}
