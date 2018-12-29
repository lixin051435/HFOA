package com.hfoa.dao.privatecar;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;
import com.hfoa.entity.vacation.LearYear;


public interface PrivateMapper {
	
	int insertPrivateCar(PrivateCarEntity privateCarEntity);
	
	int updatePrivateCar(PrivateCarEntity privateCarEntity);
	
	int updateOpenId(PrivateCarEntity privateCarEntity);
	
	int updateApproveOpenId(PrivateCarEntity privateCarEntity);
	
	
	int deletePrivateCar(String applyId);
	
	PrivateCarEntity getPrivateCar(String applyId);

	List<Object> getMaxId(String department,String applyid);
	
	List<PrivateCarEntity> searPrivateCar(PrivateCarEntity privateCarEntity);
	
	List<Map<String,Object>> countPrivate(String applyman,String applyTime);

	//获取所有私车申请信息
	@Select({"select * from b_privatecar order by ApplyTime desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<PrivateCarEntity> allApplyDisplay(@Param("start")int start, @Param("number")int number);

	//获取所有申请信息的总数
	@Select({"select count(*) from b_privatecar"})
	int getAllCount();

	//根据申请id获取相应的执行信息
	@Select({"select * from b_privatecarinvoice where ApplyIds like #{applyId,jdbcType=VARCHAR}"})
	PrivatecarinvoiceEntity getbyLikeApplyId(@Param("applyId")String applyId);

	//条件查询私车申请信息
	List<PrivateCarEntity> allApplySearchDisplay(PrivateCarEntity applyinfo);

	//条件查询申请信息数量
	int getAllSearchCount(PrivateCarEntity applyinfo);

	//根据申请编号删除私车申请信息
	@Delete({"delete from b_privatecar where ApplyId = #{applyId,jdbcType=VARCHAR}"})
	Integer deleteByApplyId(@Param("applyId")String applyId);

	//根据业务id获取进行中的业务信息
	List<PrivateCarEntity> findTasksByApplyId(@Param("list")List<String> list);

}