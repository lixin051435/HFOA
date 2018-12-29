package com.hfoa.entity.printing;
/**
 * 
 * @author wzx
 * 公章种类实体类
 */
public class BGzkind {
    private Integer id;//id

    private String gzkind;//公章类型
    
    private int grade;//公章级别

    private String lable;//公章数据字典编号
    
    public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGzkind() {
        return gzkind;
    }

    public void setGzkind(String gzkind) {
        this.gzkind = gzkind == null ? null : gzkind.trim();
    }
}