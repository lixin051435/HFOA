package com.hfoa.entity.user;

/**
 * 发现领导dto
 * @author Administrator
 *
 */
public class FindLeaderDTO {

	private String roleid;
	private String department;
	private String workgroupid;
	private String username;
	
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getWorkgroupid() {
		return workgroupid;
	}
	public void setWorkgroupid(String workgroupid) {
		this.workgroupid = workgroupid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "FindLeaderDTO [roleid=" + roleid + ", department=" + department + ", workgroupid=" + workgroupid
				+ ", username=" + username + "]";
	}
	
}
