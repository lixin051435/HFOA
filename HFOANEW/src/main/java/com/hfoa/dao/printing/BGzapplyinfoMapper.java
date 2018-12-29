package com.hfoa.dao.printing;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.printing.BGzapplyinfo;
/**
 * 
 * @author wzx
 * 用印dao接口
 */
public interface BGzapplyinfoMapper {

	//修改申请信息
	int update(BGzapplyinfo bGzapplyinfo);

	//添加申请信息
	int insert(BGzapplyinfo bGzapplyinfo);

	//删除申请信息
	int deleteById(int id);

	//根据id获取用印申请信息
	BGzapplyinfo getById(int id);

	//根据业务状态获取业务信息
	List<BGzapplyinfo> findTasksByStatus(@Param("list")List<String> list, @Param("status")String status);

	//查看当天相同部门办理业务数量
	@Select({"select count(*) from b_gzapplyinfo where ApprovalId like #{approvalId,jdbcType=VARCHAR}"})
	int getApprovalCount(@Param("approvalId")String approvalId);

	//根据申请人账号查找申请人的申请信息
	List<BGzapplyinfo> findTasksByUserCode(@Param("list")List<String> list, @Param("code")String code);

	//根据sql语句获取公章申请信息
	List<BGzapplyinfo> getBySql(@Param("result")String result);

	//根据申请人账号获取所有业务信息
	@Select({"select * from b_gzapplyinfo where ApplyUserName =#{code,jdbcType=VARCHAR}"})
	List<BGzapplyinfo> getByAppUser(@Param("code")String code);

	//分页查询用印申请信息
	@Select({"select * from b_gzapplyinfo where Status='已完成' order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BGzapplyinfo> getAll(@Param("start")int start, @Param("number")int number);

	//获取用印申请总数量
	@Select({"select count(*) from b_gzapplyinfo where Status='已完成'"})
	int getAllCount();

	//根据sql语句查询用印申请数量
	int getCountBySQL(@Param("countResult")String countResult);

	//获取所有完成申请的用印信息
	@Select({"select * from b_gzapplyinfo where Status='已完成' order by ApplyTime desc"})
	List<BGzapplyinfo> findAllPass();

	//查询待审批或者已经通过的业务信息
	List<BGzapplyinfo> findTasksByStatuses(@Param("list")List<String> list, @Param("status1")String status1, @Param("status2")String status2);

	//根据流程审批人以及状态获取业务信息
	@Select({"select * from b_gzapplyinfo where ApproveMan=#{code,jdbcType=VARCHAR} and approvalLable=#{i,jdbcType=INTEGER}"})
	List<BGzapplyinfo> findApprovalAndLable(@Param("code")String code,@Param("i") int i);

	//根据流程业务主管审批人以及状态获取业务信息
	@Select({"select * from b_gzapplyinfo where BusinessManager=#{code,jdbcType=VARCHAR} and bussinessLable=#{i,jdbcType=INTEGER}"})
	List<BGzapplyinfo> findBussinessAndLable(@Param("code")String code,@Param("i") int i);

	//根据流程确认人审批人以及状态获取业务信息
	@Select({"select * from b_gzapplyinfo where ConfirmMan=#{code,jdbcType=VARCHAR} and confirmLable=#{i,jdbcType=INTEGER}"})
	List<BGzapplyinfo> findConfirmAndLable(@Param("code")String code,@Param("i") int i);

	//获取所有业务信息
	@Select({"select * from b_gzapplyinfo"})
	List<BGzapplyinfo> getAllInfo();

	List<BGzapplyinfo> findTasksLikeStatuses(@Param("list")List<String> list,@Param("status") String status);

	//根据审批人账号获取审批信息
	@Select({"select count(*) from b_gzapplyinfo where ApproveMan = #{code,jdbcType=VARCHAR} and Status='已申请'"})
	Object selectNum(@Param("code")String code);

	//获取所有发往单位
	@Select({"select SendTo from b_gzapplyinfo where SendTo like #{companyName,jdbcType=VARCHAR} and Status='已完成'"})
	List<String> getAllSendUnit(@Param("companyName")String companyName);

	//获取所有发往单位
	@Select({"select SendTo from b_gzapplyinfo"})
	Set<String> getAllSendTo();

	//条件查询用印申请信息
	List<BGzapplyinfo> findTasksByCase(BGzapplyinfo gzapplyinfo);

	//根据申请人获取完成的信息
	@Select({"select * from b_gzapplyinfo where ApplyUserName =#{code,jdbcType=VARCHAR} and Status='已完成'"})
	List<BGzapplyinfo> getFinishByAppUser(@Param("code")String code);

	//导数据
	@Select({"select * from b_gzapplyinfo where Status='通过'"})
	List<BGzapplyinfo> getAllPass();

	//分页获取所有进行中的业务申请信息
	@Select({"select * from b_gzapplyinfo where Status!='已完成' order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BGzapplyinfo> getAllRunTime(@Param("start")int start, @Param("number")int number);

	//获取进行中的业务数量
	@Select({"select count(*) from b_gzapplyinfo where Status!='已完成'"})
	int getAllRunTimeCount();

	//获取所有正在进行中未完成的业务申请信息
	@Select({"select * from b_gzapplyinfo where Status!='已完成' order by id desc"})
	List<BGzapplyinfo> getAllRunTimeInfo();
}