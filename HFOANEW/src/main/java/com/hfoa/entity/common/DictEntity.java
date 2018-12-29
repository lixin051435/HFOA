package com.hfoa.entity.common;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 
 * @author wzx
 * 字典实体类
 */
public class  DictEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String id; //id
	
	private String text;//菜单名称
	
	private String parentId;//父级菜单（具体含义）
	
	private String state;
	
	private String info;//标记

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "DictEntity [id=" + id + ", text=" + text + ", parentId=" + parentId + ", state=" + state + ", info="
				+ info + "]";
	}

	
	
}
