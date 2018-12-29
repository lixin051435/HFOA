package com.hfoa.entity.entertain;

public class Entertainhospitality {
	
	private String hospitalityId;
	
	private String departmentId;
	
	private String department;
	
	private String hospitalitybudget;
	
	private String createTime;
	
	private String year;
	
	private String type;

	public String getHospitalityId() {
		return hospitalityId;
	}

	public void setHospitalityId(String hospitalityId) {
		this.hospitalityId = hospitalityId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getHospitalitybudget() {
		return hospitalitybudget;
	}

	public void setHospitalitybudget(String hospitalitybudget) {
		this.hospitalitybudget = hospitalitybudget;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Entertainhospitality [hospitalityId=" + hospitalityId + ", departmentId=" + departmentId
				+ ", department=" + department + ", hospitalitybudget=" + hospitalitybudget + ", createTime="
				+ createTime + ", year=" + year + ", type=" + type + "]";
	}
	
	

}
