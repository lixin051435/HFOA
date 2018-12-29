package com.hfoa.dao.permission;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.permission.BProcess;
/**
 * 
 * @author wzx
 * 流程dao接口
 */
public interface BProcessMapper {

    int deleteById(Integer id);

    int insert(BProcess process);

    BProcess selectById(Integer id);

    int update(BProcess process);

    //分页查询流程信息
    @Select({"select * from b_process order by id asc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BProcess> processDisplayByPage(@Param("start")int start, @Param("number")int number);

    //获取流程信息总数量
    @Select({"select count(*) from b_process"})
	int getAllCount();

    //获取流程模块
    @Select({"select * from b_process where parentid=#{id,jdbcType=INTEGER}"})
	List<BProcess> getByParentId(@Param("id")int id);

    //根据sql语句查询流程信息
	List<BProcess> getBySql(@Param("result")String result);

	//根据sql语句查询流程信息数量
	int getCountBysql(@Param("countResult")String countResult);

	//查询所有流程信息
	 @Select({"select * from b_process"})
	List<BProcess> findAllInfo();

	//根据父级权限获取子权限
	@Select({"select * from b_process where parentid= #{parentId,jdbcType=INTEGER}"})
	List<BProcess> getByNodeType(@Param("parentId")int parentId);

	//根据角色id获取流程id
	@Select({"select processid from b_role_process where roleid= #{roleId,jdbcType=INTEGER}"})
	List<Integer> getProcessByRole(@Param("roleId")int roleId);

	//删除角色流程中间表角色id相同信息
	@Delete({"delete from b_role_process where roleid= #{id,jdbcType=INTEGER}"})
	void deleteMiddleRole(@Param("id")int id);

	//根据角色id获取流程id集合
	@Select({"select processid from b_role_process where roleid= #{roleId,jdbcType=INTEGER}"})
	List<Integer> getProcessByRoleId(@Param("roleId")int roleId);

	//根据节点名称获取流程信息
	@Select({"select * from b_process where processname= #{name,jdbcType=VARCHAR}"})
	BProcess getprocessByName(@Param("name")String name);

	//根据业务节点名称获取所有节点内容
	@Select({"select * from b_process where processname= #{processName,jdbcType=VARCHAR}"})
	List<BProcess> getByName(@Param("processName")String processName);

	//根据流程id获取流程内容
	@Select({"select * from b_process where id= #{id,jdbcType=INTEGER}"})
	BProcess getById(@Param("id")Integer id);
}