package com.hfoa.dao.department;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 部门dao接口
 */
public interface BDepartmentMapper {

	//根据部门id获取所有该部门的角色id
	@Select({"select roleid from b_department_role where departmentid=#{departmentid,jdbcType=INTEGER}"})
	List<Integer> getRoleIds(int departmentid);

	
	//根据部门id获取用户id
	@Select({"select userid from b_user_department where departmentid=#{departmentId,jdbcType=INTEGER}"})
	List<Integer> getUserIdByDId(Integer departmentId);

	//根据用户id获取部门id
	@Select({"select departmentid from b_user_department where userid=#{userId,jdbcType=INTEGER}"})
	List<Integer> getDepartmentIdByUserId(int userId);

	//根据id获取部门信息
	@Select({"select * from b_department where id=#{integer,jdbcType=INTEGER}"})
	BDepartmentEntity getDepartmentIdById(Integer integer);

	//根据部门集合查找这些部门的用户
	List<BUserDepartment> getUserIdByDIds(@Param("dlist")List<Integer> dlist);

	//获取所有部门信息
	@Select({"select * from b_department"})
	List<BDepartmentEntity> getAllDepartment();

	//获取所有自己的部门信息
	List<BDepartmentEntity> getOwnDepartment(@Param("departmentIdByUserId")List<Integer> departmentIdByUserId);

	//添加用户部门中间表
	@Insert({"insert into b_user_department (userid,departmentid) values(#{id,jdbcType=INTEGER},#{departmentid,jdbcType=INTEGER})"})
	void insertMiddleUser(@Param("id")Integer id, @Param("departmentid")Integer departmentid);

	//根据部门名称获取部门id
	@Select({"select id from b_department where departmentname=#{departmentname,jdbcType=VARCHAR}"})
	int getDepartmentIdByName(@Param("departmentname")String departmentname);

	//修改用户部门中间表
	@Update({"update b_user_department set departmentid=#{departmentId,jdbcType=INTEGER} where userid=#{id,jdbcType=INTEGER}"})
	void updateMiddleUser(@Param("id")Integer id, @Param("departmentId")int departmentId);

	//删除用户部门中间表中相同用户ID的信息
	@Delete({"delete from b_user_department where userid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleUser(@Param("id")int id);

	//删除角色部门中间表中相同角色ID的信息
	@Delete({"delete from b_department_role where roleid=#{id,jdbcType=INTEGER}"})
	void deleteMiddleRole(@Param("id")int id);

	//分页获取部门信息
	@Select({"select * from b_department order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BDepartmentEntity> departmentDisplayByPage(@Param("start")int start, @Param("number")int number);

	//获取部门总数量
	@Select({"select count(*) from b_department"})
	int getAllCount();

	//添加部门信息
	int insert(BDepartmentEntity department);

	//修改部门信息
	int update(BDepartmentEntity department);

	//根据id删除部门信息
	Integer deleteById(@Param("id")int id);

	//分页模糊查询部门信息
	@Select({"select * from b_department where departmentname like #{title,jdbcType=VARCHAR} order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BDepartmentEntity> departmentVagueByPage(@Param("start")int start, @Param("number")int number, @Param("title")String title);

	//获取模糊查询的部门数量
	@Select({"select count(*) from b_department where departmentname like #{title,jdbcType=VARCHAR}"})
	int getVagueCount(String title);

	//添加部门角色中间表部门id相同数据
	@Insert({"insert into b_department_role (roleid,departmentid) values(#{roleId,jdbcType=INTEGER},#{departmentid,jdbcType=INTEGER})"})
	int insertMiddleRole(@Param("departmentid")int departmentid, @Param("roleId")int roleId);

	//获取所有可用的部门
	@Select({"select * from b_department where available=1"})
	List<BDepartmentEntity> getAllAvalibleDepart();

	//根据部门id获取所有抄送的部门id
	@Select({"select copyDepartmentId from b_department_copyDepartment where departmentId =#{departmentid,jdbcType=INTEGER}"})
	List<Integer> getAlldepartByDId(@Param("departmentid")int departmentid);

	//根据部门id删除抄送部门信息
	@Delete({"delete from b_department_copydepartment where departmentId=#{departmentid,jdbcType=INTEGER}"})
	void deleteCopyDepartmentByDid(@Param("departmentid")int departmentid);

	//插入抄送部门信息
	@Insert({"insert into b_department_copydepartment (departmentId,copyDepartmentId) values(#{departmentid,jdbcType=INTEGER},#{copyDepartmentId,jdbcType=INTEGER})"})
	int insertCopyDepartment(@Param("departmentid")int departmentid, @Param("copyDepartmentId")int copyDepartmentId);

	//根据部门id获取抄送部门Id
	@Select({"select copyDepartmentId from b_department_copyDepartment where departmentId =#{realDepartmentid,jdbcType=INTEGER}"})
	List<Integer> getcopyDepartId(@Param("realDepartmentid")int realDepartmentid);
}