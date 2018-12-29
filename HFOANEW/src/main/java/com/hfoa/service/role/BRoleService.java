package com.hfoa.service.role;

import java.util.List;
import java.util.Set;

import com.hfoa.entity.role.BRoleClass;
import com.hfoa.entity.role.BRoleEntity;

/**
 * 
 * @author wzx
 * 角色service接口
 */
public interface BRoleService {

	//根据角色名称查找角色id
	int getRoleIdByRoleName(String roleName);

	//根据模块类型查询该类型下所有角色id
	List<Integer> getRoleClassList(String modulenum);

	//根据功能模块编号获取所有具有该功能模块的角色id
	Set<Integer> getRoleIdFromClass(String moduleNum);

	//获取用户在公共类型里边级别最高的角色编号
	Integer getGradeFromClass(Integer roleid);

	//根据用户在公共类型里边的级别查询用户角色id
	int getLeaderRole(Integer grade);

	//查询公司领导级别
	int getLeaderGrade();

	//在公共类别里边根据roleid查询类别
	String getModuleNumByRole(int minRoleId);

	//根据角色id获取角色信息
	BRoleEntity getRoleByRoleId(Integer roleid);

	//获取公共角色
	List<Integer> getShareRoleId();

	//获取到某个类型的某个级别及以下的角色id
	List<Integer> getBeforeRoleId(int maxGrade, String string);

	//根据模块类别以及角色id获取角色级别
	Integer getGradeByModuleNum(Integer roleId, String moduleNum);

	//查找公共类型中大于某个级别的所有角色
	List<Integer> getAfterRole(int maxAppGrade);

	//申请人在该模块中的级别
	Integer getAppRoleByModule(int maxAppRole, String moduleNum);

	//获取某个功能模块某个级别之后的角色id
	List<Integer> getAfterRoleByModule(int appGrade, String moduleNum);

	//根据角色以及模块id获取所有等级
	List<Integer> getAllGradeByClass(String moduleNum, List<Integer> gradeList);

	//查找一个功能模块中某个级别的所有角色id
	List<Integer> getRoleByGradeAndMod(String moduleNum, int minGrade);

	//根据模块获取所有级别
	Set<Integer> getGradeByModule(String moduleNum);

	//分页获取角色信息
	List<BRoleEntity> roleDisplayByPage(int start, int number);

	//获取角色总数
	int getAllCount();

	//添加角色信息
	int insert(BRoleEntity bRoleEntity);

	//修改角色信息
	int update(BRoleEntity bRoleEntity);

	//删除角色
	int deleteById(int id);
	
	BRoleClass getmoney(int roleid);

	//角色的模糊分页查询
	List<BRoleEntity> roleVagueByPage(int start, int number, String title);

	//获取角色模糊查询的数量
	int getVagueCount(String title);

	//查询所有角色
	List<BRoleEntity> findAll();

	//插入用户角色中间表
	void inserMiddleUser(Integer id, int roleid);

	//删除用户角色中间表中相同用户id的数据
	void deleteMiddleUser(Integer id);

	//删除用户角色中间表中相同角色id的数据
	void deleteMiddleRole(int id);

	//添加角色权限中间表，角色id相同
	void insertMiddlePermission(int insert, int parseInt);

	//删除角色权限中间表，角色id相同信息
	void deleteMiddlePermission(int update);

	//删除角色部门中间表部门id相同信息
	void deleteMiddleDepartment(int id);

	//根据角色id集合获取角色信息
	List<BRoleEntity> getRoleByRoleIds(List<Integer> roleIds);

	//插入角色流程中间表角色id相同
	void insertMiddleProcess(int roleid, int processid);

	//根据流程节点获取相应节点的角色
	List<Integer> getRoleByProcessId(int processId);

}
