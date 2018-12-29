package com.hfoa.dao.role;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.role.BRoleClass;
import com.hfoa.entity.role.BRoleEntity;

/**
 * 
 * @author wzx
 * 角色dao接口
 */
public interface BRoleMapper {

	//根据角色名称查找角色id
	@Select({"select id from b_role where rolename=#{roleName,jdbcType=VARCHAR} and available=1"})
	int getRoleIdByRoleName(String roleName);

	//根据功能模块编号获取所有具有该功能模块的角色id
	@Select({"select roleId from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR}"})
	Set<Integer> getRoleIdFromClass(String moduleNum);

	//获取用户在公共类型里边级别最高的角色编号
	@Select({"select grade from b_role_class where roleId=#{roleid,jdbcType=INTEGER} and moduleNum like '%*%'"})
	Integer getGradeFromClass(int roleid);

	//获取钱
	@Select({"select * from b_role_class where roleId=#{roleid,jdbcType=INTEGER} and moduleNum like '%*%'"})
	BRoleClass getMoney(int roleid);
	
	//根据用户在公共类型里边的级别查询用户角色id
	@Select({"select roleId from b_role_class where moduleNum like '%*%' and grade=#{grade,jdbcType=INTEGER}"})
	int getLeaderRole(Integer grade);

	//查询公司领导级别
	@Select({"select max(grade) from b_role_class where moduleNum like '%*%'"})
	int getLeaderGrade();

	//在公共类别里边根据roleid查询类别
	@Select({"select moduleNum from b_role_class where moduleNum like '%*%' and roleId=#{minRoleId,jdbcType=INTEGER}"})
	String getModuleNumByRole(int minRoleId);

	//根据模块类型查询该类型下所有角色id
	@Select({"select roleId from b_role_class where moduleNum =#{modulenum,jdbcType=VARCHAR}"})
	List<Integer> getRoleClassList(String modulenum);

	//根据角色id获取角色信息
	@Select({"select * from b_role where id=#{roleid,jdbcType=INTEGER} and available=1"})
	BRoleEntity getRoleByRoleId(Integer roleid);

	//获取公共角色
	@Select({"select roleId from b_role_class where moduleNum like '%*%'"})
	List<Integer> getShareRoleId();

	//获取到某个类型的某个级别及以下的角色id
	@Select({"select roleId from b_role_class where moduleNum =#{moduleNum,jdbcType=VARCHAR} and grade<=#{maxGrade,jdbcType=INTEGER}"})
	List<Integer> getBeforeRoleId(@Param("maxGrade")int maxGrade,@Param("moduleNum")String moduleNum);

	//根据模块类别以及角色id获取角色级别
	@Select({"select grade from b_role_class where moduleNum =#{moduleNum,jdbcType=VARCHAR} and roleId=#{roleId,jdbcType=INTEGER}"})
	Integer getGradeByModuleNum(@Param("roleId")Integer roleId, @Param("moduleNum")String moduleNum);

	//查找公共类型中大于某个级别的所有角色
	@Select({"select roleId from b_role_class where moduleNum like '%*%' and grade>#{maxAppGrade,jdbcType=INTEGER}"})
	List<Integer> getAfterRole(int maxAppGrade);

	//申请人在该模块中的级别
	@Select({"select grade from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR} and roleId=#{maxAppRole,jdbcType=INTEGER}"})
	Integer getAppRoleByModule(@Param("maxAppRole")int maxAppRole,@Param("moduleNum") String moduleNum);

	//获取某个功能模块某个级别之后的角色id
	@Select({"select roleId from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR} and grade>#{appGrade,jdbcType=INTEGER}"})
	List<Integer> getAfterRoleByModule(@Param("appGrade")int appGrade, @Param("moduleNum")String moduleNum);

	//根据角色以及模块id获取所有等级
	List<Integer> getAllGradeByClass(@Param("moduleNum")String moduleNum,@Param("list") List<Integer> list);

	//查找一个功能模块中某个级别的所有角色id
	@Select({"select roleId from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR} and grade=#{minGrade,jdbcType=INTEGER}"})
	List<Integer> getRoleByGradeAndMod(@Param("moduleNum")String moduleNum, @Param("minGrade")int minGrade);

	//根据模块获取所有级别
	@Select({"select grade from b_role_class where moduleNum=#{moduleNum,jdbcType=VARCHAR}"})
	Set<Integer> getGradeByModule(@Param("moduleNum")String moduleNum);

	//分页获取角色信息
	@Select({"select * from b_role order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BRoleEntity> roleDisplayByPage(@Param("start")int start,@Param("number")int number);
//	, @Param("shareRoleId")List<Integer> shareRoleId

	//获取角色总数
	@Select({"select count(*) from b_role"})
	int getAllCount();

	//添加角色信息
	int insert(BRoleEntity bRoleEntity);

	//修改角色信息
	int update(BRoleEntity bRoleEntity);

	//删除角色
	int deleteById(int id);

	//角色的模糊分页查询
	@Select({"select * from b_role where rolename like #{title,jdbcType=VARCHAR} order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BRoleEntity> roleVagueByPage(@Param("start")int start,@Param("number")int number,@Param("title")String title);

	//获取角色模糊查询的数量
	@Select({"select count(*) from b_role  where rolename like #{title,jdbcType=VARCHAR}"})
	int getVagueCount(@Param("title")String title);

	//查询所有角色
	@Select({"select * from b_role"})
	List<BRoleEntity> findAll();

	//插入用户角色中间表
	@Insert({"insert into b_user_role (userid,roleid) values(#{id,jdbcType=INTEGER},#{roleId,jdbcType=INTEGER})"})
	void inserMiddleUser(@Param("id")Integer id, @Param("roleId")int roleId);

	//删除用户角色中间表中相同用户id的数据
	@Delete({"delete from b_user_role where userid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleUser(@Param("id")Integer id);

	//删除用户角色中间表中相同角色id的数据
	@Delete({"delete from b_user_role where roleid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleRole(@Param("id")int id);

	//添加角色权限中间表，角色id相同
	@Insert({"insert into b_role_permission (roleid,permissionid) values(#{roleid,jdbcType=INTEGER},#{permissionid,jdbcType=INTEGER})"})
	void insertMiddlePermission(@Param("roleid")int roleid, @Param("permissionid")int permissionid);

	//删除角色权限中间表，角色id相同信息
	@Delete({"delete from b_role_permission where roleid=#{roleid,jdbcType=INTEGER}"})
	void deleteMiddlePermission(@Param("roleid")int roleid);

	//删除角色部门中间表部门id相同信息
	@Delete({"delete from b_department_role where departmentid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleDepartment(@Param("id")int id);

	//根据角色id集合获取角色信息
	List<BRoleEntity> getRoleByRoleIds(@Param("roleIds")List<Integer> roleIds);

	//插入角色流程中间表角色id相同
	@Insert({"insert into b_role_process (roleid,processid) values(#{roleid,jdbcType=INTEGER},#{processid,jdbcType=INTEGER})"})
	void insertMiddleProcess(@Param("roleid")int roleid, @Param("processid")int processid);

	//根据流程节点获取相应节点的角色
	@Select({"select roleid from b_role_process  where processid = #{processId,jdbcType=INTEGER}"})
	List<Integer> getRoleByProcessId(@Param("processId")int processId);
}