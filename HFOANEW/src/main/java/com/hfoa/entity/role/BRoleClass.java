package com.hfoa.entity.role;
/**
 * 
 * @author wzx
 * 角色-分类实体类
 */
public class BRoleClass {
    private Integer id;//id

    private Integer roleid;//角色id

    private Integer grade;//在不同功能模块中的等级
    
    private String moduleNum;//对应的功能模块编号
    
    private String money;//钱数

    
    
    
    
    
    public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getModuleNum() {
		return moduleNum;
	}

	@Override
	public String toString() {
		return "BRoleClass [id=" + id + ", roleid=" + roleid + ", grade=" + grade + ", moduleNum=" + moduleNum
				+ ", money=" + money + "]";
	}

	public void setModuleNum(String moduleNum) {
		this.moduleNum = moduleNum;
	}
    
}