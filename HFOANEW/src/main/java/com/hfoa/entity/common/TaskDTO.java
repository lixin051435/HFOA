package com.hfoa.entity.common;

public class TaskDTO {

	private String bussinessId;
	
	private String processInstanceId;
	
	private String key;
	
	private String name;
	
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBussinessId() {
		return bussinessId;
	}

	public void setBussinessId(String bussinessId) {
		this.bussinessId = bussinessId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "TaskDTO [bussinessId=" + bussinessId + ", processInstanceId=" + processInstanceId + ", key=" + key
				+ "]";
	}

	
}
