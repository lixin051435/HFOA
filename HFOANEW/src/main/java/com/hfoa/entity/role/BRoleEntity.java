package com.hfoa.entity.role;
/**
 * 
 * @author wzx
 * 角色实体类
 */
public class BRoleEntity {
    private Integer id;//id

    private String rolename;//角色名称

    private Integer available;//是否可用
    
    //新加属性，非数据库
    private String showAvailable;//前台显示是否可用
    private Integer forEditPermission;
    private Integer forEditProcess;
    
	public Integer getForEditProcess() {
		return forEditProcess;
	}

	public void setForEditProcess(Integer forEditProcess) {
		this.forEditProcess = forEditProcess;
	}

	public Integer getForEditPermission() {
		return forEditPermission;
	}

	public void setForEditPermission(Integer forEditPermission) {
		this.forEditPermission = forEditPermission;
	}

	public String getShowAvailable() {
		return showAvailable;
	}

	public void setShowAvailable(String showAvailable) {
		this.showAvailable = showAvailable;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}