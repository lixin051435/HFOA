package com.hfoa.service.printing;

import java.util.List;

import com.hfoa.entity.printing.BGzkind;

/**
 * 
 * @author wzx
 * 公章种类service接口
 */
public interface BGzkindService {

	//修改公章信息
	int update(BGzkind bGzkind);

	//添加公章信息
	int insert(BGzkind bGzkind);

	//删除公章信息
	int deleteById(int id);

	//获取所有公章类型
	List<BGzkind> getAllGzKind();

	//获取公章中最小的级别
	int getMinGrade();

	//根据id获取公章种类
	BGzkind getById(int parseInt);

	//添加公章类型
	void save(BGzkind gz);

}
