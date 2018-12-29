package com.hfoa.entity.common;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class  WineEntity {
	
	private DictEntity dict;
	
	private List<DictEntity> listDict;

	public DictEntity getDict() {
		return dict;
	}

	public void setDict(DictEntity dict) {
		this.dict = dict;
	}

	public List<DictEntity> getListDict() {
		return listDict;
	}

	public void setListDict(List<DictEntity> listDict) {
		this.listDict = listDict;
	}
	
	
	
}
