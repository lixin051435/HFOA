package com.hfoa.entity.permission;
/**
 * 
 * @author wzx
 * 业务类型实体类
 */
public class BClass {
    private Integer id;//id

    private String classname;//分类名称
    
    private String moduleNum;//功能编号

    public String getModuleNum() {
		return moduleNum;
	}

	public void setModuleNum(String moduleNum) {
		this.moduleNum = moduleNum;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }
}