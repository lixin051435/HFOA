package com.hfoa.dao.common;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.hfoa.entity.common.DictEntity;

//import cn.com.hfga.entity.menu.MenuEntity1;

@Repository("dictDAO")
public interface DictDAO{

//	 extends SpringDataDAO<DictEntity>
	//添加字典
	int insert(DictEntity dict);
	
	//根据父节点获取所有子节点
	@Select({"select * from b_dictionary where parent_id=#{parent_id,jdbcType=VARCHAR}"})
	public List<DictEntity> getByNodeType(@Param(value="parent_id")String parent_id);
	
	//根据节点获取菜单信息
	@Select({"select * from b_menu where parentId=#{parent_id,jdbcType=VARCHAR}"})
	public List<Object> getMenuByNodeType(@Param(value="parent_id")String parent_id);
//	
//	@Query(nativeQuery=true,value="select * from b_menu where parentId=:parent_id")
//	public List<MenuEntity1> getMenuByNodeTypeReturnEn(@Param(value="parent_id")String parent_id);
	
	//删除字典表
//	@Modifying
//	@Query(nativeQuery = true, value="delete from b_dictionary where id =?1")
	public int delete(String id);

	//删除父节点及其子节点
	@Delete({"delete from b_dictionary where parent_id=#{id,jdbcType=VARCHAR} or id=#{id,jdbcType=VARCHAR}"})
	int deleteAllModule(String id);
	
	//修改字典
	public int update(DictEntity dict);

	//根据节点获取节点信息
	@Select({"select * from b_dictionary where id=#{id,jdbcType=VARCHAR}"})
	@ResultMap("BaseResultMap")
	DictEntity getNodeInfo(String id);

	//根据父节点跟标识获取描述
	@Select({"select text from b_dictionary where parent_id=#{parentId,jdbcType=VARCHAR} and info=#{info,jdbcType=VARCHAR}"})
	String getByParentAndInfo(@Param("info")String info, @Param("parentId")String parentId);

	//根据父节点跟描述获取节点标识
	@Select({"select info from b_dictionary where parent_id=#{parentId,jdbcType=VARCHAR} and text=#{text,jdbcType=VARCHAR}"})
	String getByTextAndParentId(@Param("text")String text, @Param("parentId")String parentId);
	
}
