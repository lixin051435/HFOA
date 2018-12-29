package com.hfoa.dao.car;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hfoa.entity.car.BCarbaseinfo;
/**
 * 
 * @author wzx
 * 车辆基本信息dao接口
 */
public interface BCarbaseinfoMapper {

	//获取当天可以出库的车
	@Select({"select * from b_carbaseinfo where CarState = #{text,jdbcType=VARCHAR} and SuspendDay!=#{week,jdbcType=VARCHAR}"})
	List<BCarbaseinfo> getAvailableCar(@Param("week")String week, @Param("text")String text);

	//通过车牌号获取车辆基本信息
	@Select({"select * from b_carbaseinfo where CarNum =#{carNum,jdbcType=VARCHAR}"})
	BCarbaseinfo getCarInfoByNum(@Param("carNum")String carNum);

	//修改车辆状态
	@Update({"update b_carbaseinfo set CarState = #{status,jdbcType=VARCHAR}",
	" where CarNum = #{carCode,jdbcType=VARCHAR}"})
	void updatestatus(@Param("carCode")String carCode, @Param("status")String status);

	//修改车辆基本信息
	int update(BCarbaseinfo carInfo);

	//获取所有车辆信息
	@Select({"select * from b_carbaseinfo"})
	List<BCarbaseinfo> getAllCar();

	//通过车辆id获取到车辆的具体信息
	@Select({"select * from b_carbaseinfo where ID =#{id,jdbcType=INTEGER}"})
	BCarbaseinfo getCarInfoById(Integer id);

	//获取所有非报废车
	@Select({"select * from b_carbaseinfo where CarState!=#{text,jdbcType=VARCHAR}"})
	List<BCarbaseinfo> getAllNormalCar(String text);
	
	@Select({"select * from b_carbaseinfo order by ID desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BCarbaseinfo> carDisplayByPage(@Param("start")int start, @Param("number")int number);

	//获取公车总数量
	@Select({"select count(*) from b_carbaseinfo"})
	int getAllCount();

	//添加公车信息
	int insert(BCarbaseinfo car);
}