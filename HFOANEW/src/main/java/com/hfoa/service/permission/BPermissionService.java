package com.hfoa.service.permission;

import java.util.List;

import com.hfoa.entity.permission.BPermissionEntity;

/**
 * 
 * @author wzx
 * 权限service接口
 */
public interface BPermissionService {

	//获取所有权限信息
	List<BPermissionEntity> getAllPermission();

	//修改权限信息
	int update(BPermissionEntity bPermissionEntity);

	//分页查询权限信息
	List<BPermissionEntity> permissionDisplayByPage(int start, int number);

	//查询权限数量
	int getAllCount();

	//添加权限信息
	int insert(BPermissionEntity permission);

	//删除权限信息
	int deleteById(int id);

	//权限的模糊分页查询
	List<BPermissionEntity> permissionVagueByPage(int start, int number, String title);

	//获取模糊查询的权限数量
	int getVagueCount(String title);

	//根据根权限获取所有子节点
	List<BPermissionEntity> getByNodeType(String parentId);

	//删除相同权限id的角色权限中间表
	void deleteMiddleRole(int id);

	//删除相同角色id的角色权限中间表
	void deleteMiddlePermission(int id);

	//根据角色id获取权限
	List<Integer> getPermissionByRole(int roleId);

	//根据id获取权限信息
	BPermissionEntity getById(Integer integer);

}
