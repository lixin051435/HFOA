package com.hfoa.dao.printing;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.printing.BGzkind;
/**
 * 
 * @author wzx
 * 公章种类dao接口
 */
public interface BGzkindMapper {

	//修改公章信息
	int update(BGzkind bGzkind);

	//添加公章信息
	int insert(BGzkind bGzkind);

	//删除公章信息
	int deleteById(int id);

	//获取所有公章类型
	@Select({"select * from b_gzkind"})
	List<BGzkind> getAllGzKind();

	//获取公章中最小级别
	@Select({"select min(grade) from b_gzkind"})
	int getMinGrade();

	//根据id获取公章种类
	BGzkind getById(int id);
}