package com.hfoa.service.department;

import java.util.List;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 部门service接口
 */
public interface BDepartmentService {

	//根据部门id获取所有该部门的角色id
	List<Integer> getRoleIds(int departmentid);

	//根据部门id获取用户id
	List<Integer> getUserIdByDId(Integer departmentId);

	//根据用户id获取部门id
	List<Integer> getDepartmentIdByUserId(int userId);

	//根据id获取部门信息
	BDepartmentEntity getDepartmentIdById(Integer integer);

	//根据部门集合查找这些部门的用户
	List<BUserDepartment> getUserIdByDIds(List<Integer> dlist);

	//获取所有部门信息
	List<BDepartmentEntity> getAllDepartment();

	//获取自己所有部门信息
	List<BDepartmentEntity> getOwnDepartment(List<Integer> departmentIdByUserId);

	//添加用户部门中间表
	void insertMiddleUser(Integer id, Integer departmentid);

	//根据部门名称获取部门id
	int getDepartmentIdByName(String departmentname);

	//修改用户部门中间表
	void updateMiddleUser(Integer id, int departmentId);

	//删除用户部门中间表中相同用户ID的信息
	void deleteMiddleUser(int id);

	//删除角色部门中间表中相同角色ID的信息
	void deleteMiddleRole(int id);

	//分页获取部门信息
	List<BDepartmentEntity> departmentDisplayByPage(int start, int number);

	//获取部门总数量
	int getAllCount();

	//添加部门信息
	int insert(BDepartmentEntity department);

	//修改部门信息
	int update(BDepartmentEntity department);

	//根据id删除部门信息
	Integer deleteById(int id);

	//分页模糊查询部门信息
	List<BDepartmentEntity> departmentVagueByPage(int start, int number, String title);

	//获取模糊查询的部门数量
	int getVagueCount(String title);

	//添加部门角色中间表部门id相同数据
	int insertMiddleRole(int departmentid, int roleId);

	//查询所有可用的部门
	List<BDepartmentEntity> getAllAvalibleDepart();

	//根据部门id获取所有抄送的部门id
	List<Integer> getAlldepartByDId(int departmentid);

	//根据部门id删除抄送部门信息
	void deleteCopyDepartmentByDid(int departmentid);

	//插入抄送部门信息
	int insertCopyDepartment(int departmentid, int copyDepartmentId);

	//根据部门id获取抄送部门Id
	List<Integer> getcopyDepartId(int realDepartmentid);

}
