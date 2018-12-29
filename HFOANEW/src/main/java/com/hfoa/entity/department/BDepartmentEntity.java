package com.hfoa.entity.department;
/**
 * 
 * @author wzx
 * 部门实体类
 */
public class BDepartmentEntity {
    private Integer id;//id

    private String departmentname;//部门名称

    private Integer available;//是否可用

    //新加属性，非数据库
    private String showAvailable;//前台显示是否可用
    private Integer forShowRole;
    private Integer foreditRole;
    private Integer forCopyDepart;
    
    public Integer getForCopyDepart() {
		return forCopyDepart;
	}

	public void setForCopyDepart(Integer forCopyDepart) {
		this.forCopyDepart = forCopyDepart;
	}

	public Integer getForShowRole() {
		return forShowRole;
	}

	public void setForShowRole(Integer forShowRole) {
		this.forShowRole = forShowRole;
	}

	public Integer getForeditRole() {
		return foreditRole;
	}

	public void setForeditRole(Integer foreditRole) {
		this.foreditRole = foreditRole;
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

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname == null ? null : departmentname.trim();
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}