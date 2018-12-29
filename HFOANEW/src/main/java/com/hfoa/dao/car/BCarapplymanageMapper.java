package com.hfoa.dao.car;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hfoa.entity.car.CarApplyDTO;
import com.hfoa.entity.car.CarApplyInfo2;

/**
 * 
 * @author wzx
 *公车申请的dao接口
 */
public interface BCarapplymanageMapper {

	//插入公车申请的业务信息
	int insertCarApply(CarApplyInfo2 carapply);

	//根据业务id修改业务状态
	@Update({"update b_carapplymanage set Status = #{status,jdbcType=INTEGER}",
	" where id = #{id,jdbcType=INTEGER}"})
	int updateStatus(@Param("status")int status,@Param("id")int id);

	//只能查询本部门的业务
	List<CarApplyInfo2> findByIds(@Param("list")List<String> list, @Param("departmentId")int departmentId);

	//查询申请中最新一个审批单号
	@Select({"select ApplyId from b_carapplymanage where ID = (select max(ID) from b_carapplymanage)"})
	String getlastApply();

	//根据业务id查询业务信息
	CarApplyInfo2 getById(int id);

	//修改业务信息
	void updateCarApply(CarApplyInfo2 carApply);

	//获取这个状态下的业务信息
	List<CarApplyInfo2> findTasksByStatus(@Param("list")List<String> list, @Param("status")int status);

	//根据当天时间查询在业务申请信息中的数量
	@Select({"select count(*) from b_carapplymanage where ApplyId like #{date,jdbcType=VARCHAR}"})
	int getAppNumByLike(String date);

	//根据业务id，部门id查询业务
	List<CarApplyInfo2> findBusinessByTaskses(@Param("list")List<String> list, @Param("departList")List<Integer> departList);

	//根据sql语句查找公车申请信息
	List<CarApplyInfo2> getBySql(@Param(value="result")String result);

	//获取已申请数量
	@Select({"select count(*) from b_carapplymanage where CarCode = #{carcode,jdbcType=VARCHAR} and state='已申请' and ApplyTime>=#{mon,jdbcType=VARCHAR}"})
	int getAppCount(@Param("carcode")String carcode, @Param("mon")String mon);

	//获取已预约数量
	@Select({"select count(*) from b_carapplymanage where CarCode = #{carcode,jdbcType=VARCHAR} and state='已预约' and ApplyTime>=#{mon,jdbcType=VARCHAR}"})
	int getAppointmentCount(@Param("carcode")String carcode, @Param("mon")String mon);

	//根据车牌号获取到车辆借用信息
	@Select({"select * from b_carapplymanage where CarCode = #{carNum,jdbcType=VARCHAR} and (state='已申请' or state='已预约' or state='使用中') order by BeginTimePlan desc"})
	List<CarApplyInfo2> getByCarNum(@Param("carNum")String carNum);

	//根据申请人的账号查找申请人借车的信息
	@Select({"select * from b_carapplymanage where ApplyUserName = #{code,jdbcType=VARCHAR} and Status<5"})
	List<CarApplyInfo2> getByAppUserName(String code);

	//根据业务id删除业务信息
	void deleteByPrimaryKey(Integer id);

	//根据车牌号查询该车辆正在申请的数量
	@Select({"select count(*) from b_carapplymanage where CarCode = #{carCode,jdbcType=VARCHAR} and Status<5"})
	int getCountByCarNum(String carCode);

	//查询用户正在办理的业务
	List<CarApplyInfo2> findTasksByUserCode(@Param("list")List<String> list, @Param("code")String code);

	//根据状态获取业务信息
	@Select({"select * from b_carapplymanage where Status=#{i,jdbcType=INTEGER}"})
	List<CarApplyInfo2> findByStatus(int i);

	//根据审批人的账号获取待审批的业务信息
	@Select({"select count(*) from b_carapplymanage where ApproveMan = #{code,jdbcType=VARCHAR} and Status=2"})
	Object selectNum(@Param("code")String code);

	//获取所有申请信息
	@Select({"select * from b_carapplymanage limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<CarApplyInfo2> getAll(@Param("start")int start, @Param("number")int number);

	//获取所有通过的申请信息
	@Select({"select * from b_carapplymanage where Status>2"})
	List<CarApplyInfo2> findAllPass();

	//获取模糊查询总数
	int getCountBySQL(@Param("countResult")String countResult);

	//获取所有业务信息总数
	@Select({"select count(*) from b_carapplymanage"})
	int getAllCount();

	//根据出库或者入库审批人获取业务信息
//	(OutTreasuryMan=#{code,jdbcType=VARCHAR} or InTreasuryMan=#{code,jdbcType=VARCHAR} or 
	@Select({"select * from b_carapplymanage where ApplyId is not null and ApproveMan=#{code,jdbcType=VARCHAR}"})
	List<CarApplyInfo2> findByOutOrInMan(@Param("code")String code);

	//获取所有“我”发起的并且完成的业务信息
	@Select({"select count(*) from b_carapplymanage where Status=5"})
	List<CarApplyInfo2> getAllComplate();

	//根据申请车辆车牌号获取该车辆还未进行完的申请信息
	@Select({"select * from b_carapplymanage where CarCode =#{carCode,jdbcType=VARCHAR} and Status>=3 and Status<5 and ID !=#{id,jdbcType=INTEGER}"})
	List<CarApplyInfo2> getNoFinishedInfo(@Param("carCode")String carCode, @Param("id")int id);

	//根据条件查询公车申请信息
	List<CarApplyInfo2> findTasksByCase(CarApplyDTO carUseDetailDTO);

	//根据申请人获取完成的申请信息
	@Select({"select * from b_carapplymanage where ApplyUserName = #{code,jdbcType=VARCHAR} and Status=5 and state='已归还'"})
	List<CarApplyInfo2> getFinishByAppUserName(@Param("code")String code);

	//导数据
	@Select({"select * from b_carapplymanage where Status=6 and state='已预约'"})
	List<CarApplyInfo2> getAllYuyue();

	//获取所有公车申请进行中业务信息
	@Select({"select * from b_carapplymanage where Status>=2 and Status<5 limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<CarApplyInfo2> getAllRunTimeInfo(@Param("start")int start, @Param("number")int number);

	//获取所有公车申请进行中业务信息数量
	@Select({"select count(*) from b_carapplymanage where Status>=2 and Status<5"})
	int getAllRunTimeCount();
}