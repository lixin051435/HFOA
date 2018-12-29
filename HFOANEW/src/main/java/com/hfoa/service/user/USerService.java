package com.hfoa.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearYear;

/**
 */
public interface USerService {
	
	UserEntity tologin (String name,String password);
	
	UserEntity loginOpenId(String openId);
	
	UserEntity loginUserName(String userName);
	
	UserEntity select(String id);
	
	List<BDepartmentEntity> getdepartmentOpenId(String openId);
	
	List<UserEntity> findUserByNameAndDept(UserEntity user);//查询用户
	
//	List<UserEntity> findLeader(String department,String roleid);//普通员工查询审批人
//	
//	List<UserEntity> findLeader1(UserEntity user);//部门副经理查询审批人
//	
//	List<UserEntity> findleader2(String workgroupid);//部门经理查询审批人
//	
//	List<UserEntity> findUserByNameAndId(String id,String username);//公司领导查询审批人
	
	int updateUserDays(UserEntity userEntity);
	
	int updateGzhOpenId(UserEntity userEntity);
	//根据用户账号查找用户信息
	UserEntity getUserByUserCode(String userCode);

	//根据角色id获取所有用户id
	List<Integer> getallUserId(Integer roleid);

	//根据用户id获取用户信息
	UserEntity getUserById(Integer userid);

	//根据部门id获取该部门下所有用户信息
	List<UserEntity> getAllUserByDepartId(Integer departmentId);

	//根据用户账号的关键字查询所有用户信息
	List<UserEntity> getUserByLike(String string);

	//通过用户id获取所有角色id
	List<Integer> getRoleIdByUserId(int userId);

	//查询所有用户
	List<UserEntity> getAll();

	//根据openId获取用户id
	Integer getUserIdByOpenId(String openId);
	
	//获取部门审批人
	List<UserEntity> getFinder(String openId,String moduleNum);
	
	//获取部门抄送人
	List<UserEntity> getCcList(String moduleNum,String openId);
	
	List<UserEntity> defaultgetCcList(String openId);
	
	boolean getmoney(String openId,String entertainCategory,String perBudget);
	
	
	
	int updateOpenId(UserEntity userEntity);

	//分页查询用户信息
	List<UserEntity> userDisplayByPage(int start, int number);

	//获取用户数量
	int getAllCount();

	//添加用户信息
	Integer insert(UserEntity user);

	//修改用户信息
	Integer update(UserEntity user);

	//根据用户id删除用户信息
	Integer deleteById(int id);

	//分页模糊查询用户的基本信息
	List<UserEntity> userVagueByPage(int start, int number, String title);

	//模糊查询用户数量
	int getVagueCount(String title);

	//删除用户部门中间表相同部门id信息
	void deleteMiddleDepartment(int id);

	//根据角色id获取用户id
	List<Integer> getUserIdByRoleId(int roleId);

	//修改表中以此人为领导人的领导人账号
	void updateApprovalName(String code, Integer id);

	//根据账号获取数量
	int getCountByCode(String code);
}
