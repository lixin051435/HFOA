package com.hfoa.dao.user;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hfoa.entity.user.UserEntity;

public interface UserMapper {
	UserEntity selectDays(String id);
	
	List<UserEntity> findUserByNameAndDept(UserEntity user);//查询用户
	
//	List<UserEntity> findLeader(String department,String roleid);//普通员工查询审批人
//	
//	List<UserEntity> findLeader1(String departmentname,
//			String roleid,String username);//部门副经理查询审批人
//	
//	List<UserEntity> findleader2(String workgroupid);//部门经理查询审批人
//	
//	List<UserEntity> findUserByNameAndId(String id,String username);//公司领导查询审批人
	
	UserEntity loginUser(String username,String password);
	
	
	UserEntity loginUserName(String userName);
	
	UserEntity getopenId(String modifyUserId);
	
	int updateUserDays(UserEntity userEntity);
	
	//更新openId
	int updateOpenId(UserEntity userEntity);

	//更新公众号OpenId
	int updateGzhOpenId(UserEntity userEntity);
	//根据用户账号查找用户信息
	@Select({"select * from u_user where Code=#{userCode,jdbcType=VARCHAR}"})
	UserEntity getUserByUserCode(@Param("userCode")String userCode);

	//根据角色id获取所有用户id
	@Select({"select userid from b_user_role where roleid=#{roleid,jdbcType=INTEGER}"})
	List<Integer> getallUserId(Integer roleid);

	//根据用户id获取用户信息
	@Select({"select * from u_user where id=#{userid,jdbcType=INTEGER}"})
	UserEntity getUserById(@Param("userid")Integer userid);

	//根据部门id获取该部门下所有用户信息
	@Select({"select * from u_user where Departmentid=#{departmentId,jdbcType=INTEGER}"})
	List<UserEntity> getAllUserByDepartId(Integer departmentId);

	//根据用户账号的关键字查询所有用户信息
	@Select({"select * from u_user where Code like#{checkman,jdbcType=VARCHAR}"})
	List<UserEntity> getUserByLike(String checkman);

	//通过用户id获取所有角色id
	@Select({"select roleid from b_user_role where userid=#{userId,jdbcType=INTEGER}"})
	List<Integer> getRoleIdByUserId(int userId);

	//查询所有用户
	@Select({"select * from u_user"})
	List<UserEntity> getAll();

	//根据openID获取用户id
	@Select({"select Id from u_user where OpenId=#{openId,jdbcType=VARCHAR}"})
	Integer getUserIdByOpenId(String openId);
	
	//查看所有角色
	@Select({"select roleid from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR}"})
	List<Integer> getRoleIdByModuleNum(String moduleNum);
	
	//查看角色名称
	@Select({"select rolename from b_role where id=#{id,jdbcType=INTEGER}"})
	String getRoleName(int id);

	//分页查询用户信息
	@Select({"select * from u_user order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<UserEntity> userDisplayByPage(@Param("start")int start,@Param("number")int number);

	//获取用户数量
	@Select({"select count(*) from u_user"})
	int getAllCount();

	//添加用户信息
	Integer insert(UserEntity user);

	//修改用户信息
	Integer update(UserEntity user);

	//根据用户id删除用户信息
	@Delete({"delete from u_user where Id=#{id,jdbcType=INTEGER}"})
	Integer deleteById(@Param("id")int id);

	//分页模糊查询用户的基本信息
	@Select({"select * from u_user where RealName like #{title,jdbcType=VARCHAR} order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<UserEntity> userVagueByPage(@Param("start")int start, @Param("number")int number, @Param("title")String title);

	//模糊查询用户数量
	@Select({"select count(*) from u_user where RealName like #{title,jdbcType=VARCHAR}"})
	int getVagueCount(@Param("title")String title);

	//删除用户部门中间表相同部门id信息
	@Delete({"delete from b_user_department where departmentid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleDepartment(@Param("id")int id);

	//根据角色id获取用户id
	@Select({"select userid from b_user_role where roleid=#{roleId,jdbcType=INTEGER}"})
	List<Integer> getUserIdByRoleId(@Param("roleId")int roleId);

	//修改表中以此人为领导人的领导人账号
	@Update({"update u_user set Leadername=#{code,jdbcType=VARCHAR} where WorkgroupId = #{id,jdbcType=VARCHAR}"})
	void updateApprovalName(@Param("code")String code,@Param("id") String id);

	//根据账号获取数量
	@Select({"select count(*) from u_user where Code =#{code,jdbcType=VARCHAR}"})
	int getCountByCode(@Param("code")String code);

	//根据用户名获取openid
	@Select({"select OpenId from u_user where Code = #{applyMan,jdbcType=VARCHAR}"})
	String getOpenIdByCode(@Param("applyMan")String applyMan);
	

}