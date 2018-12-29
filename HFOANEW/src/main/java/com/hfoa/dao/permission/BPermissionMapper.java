package com.hfoa.dao.permission;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.common.PageDTO;
import com.hfoa.entity.permission.BPermissionEntity;

/**
 * 
 * @author wzx
 * 权限dao接口
 */
public interface BPermissionMapper {

	//获取所有权限信息
	@Select({"select * from b_permission"})
	List<BPermissionEntity> getAllPermission();

	//修改权限信息
	int update(BPermissionEntity bPermissionEntity);

	//分页查询权限信息 
	@Select({"select * from b_permission order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BPermissionEntity> permissionDisplayByPage(@Param("start")int start, @Param("number")int number);

	//查询权限数量
	@Select({"select count(*) from b_permission"})
	int getAllCount();

	//添加权限信息
	int insert(BPermissionEntity permission);

	//删除权限信息
	int deleteById(int id);

	//权限的模糊分页查询
	@Select({"select * from b_permission where permissionname like #{title,jdbcType=VARCHAR} order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BPermissionEntity> permissionVagueByPage(@Param("start")int start,@Param("number")int number,@Param("title")String title);

	//获取模糊查询的权限数量
	@Select({"select count(*) from b_permission where permissionname like #{title,jdbcType=VARCHAR}"})
	int getVagueCount(@Param("title")String title);

	//根据父级权限获取子权限
	@Select({"select * from b_permission where parentid= #{parentId,jdbcType=INTEGER}"})
	List<BPermissionEntity> getByNodeType(@Param("parentId")int parentId);

	//删除角色权限中间表
	@Delete({"delete from b_role_permission where permissionid= #{id,jdbcType=INTEGER}"})
	void deleteMiddleRole(@Param("id")int id);

	//删除相同角色id的角色权限中间表
	@Delete({"delete from b_role_permission where roleid= #{id,jdbcType=INTEGER}"})
	void deleteMiddlePermission(@Param("id")int id);

	//根据角色id获取所有权限id
	@Select({"select permissionid from b_role_permission where roleid= #{roleId,jdbcType=INTEGER}"})
	List<Integer> getPermissionIdByRole(@Param("roleId")int roleId);

	//根据id获取权限信息
	@Select({"select * from b_permission where id= #{id,jdbcType=INTEGER}"})
	BPermissionEntity getById(@Param("id")Integer id);

}