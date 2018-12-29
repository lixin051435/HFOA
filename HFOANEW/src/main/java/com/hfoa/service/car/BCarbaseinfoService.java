package com.hfoa.service.car;

import java.util.List;

import com.hfoa.entity.car.BCarbaseinfo;
/**
 * 
 * @author wzx
 * 车辆基本信息service接口
 */
public interface BCarbaseinfoService {

	//获取可以当天可出库的车
	List<BCarbaseinfo> getAvailableCar(String week, String text);

	//根据车牌号查找车辆基本信息
	BCarbaseinfo getCarInfoByNum(String carNum);

	//根据车牌号修改车辆状态
	void updatestatus(String carCode, String status);

	//修改车辆基本信息
	int update(BCarbaseinfo carInfo);

	//获取所有车辆信息
	List<BCarbaseinfo> getAllCar();

	//通过车辆id获取到车的具体信息
	BCarbaseinfo getCarInfoById(Integer id);

	//获取所有非报废车
	List<BCarbaseinfo> getAllNormalCar(String text);

	//分页显示公车基本信息
	List<BCarbaseinfo> carDisplayByPage(int start, int number);

	//获取公车总数量
	int getAllCount();

	//添加公车信息
	int insert(BCarbaseinfo car);

}
