package com.hfoa.service.impl.carimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.car.BCarbaseinfoMapper;
import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.service.car.BCarbaseinfoService;
/**
 * 
 * @author wzx
 * 车辆基本信息service实现类
 */
@Service
public class BCarbaseinfoServiceImpl implements BCarbaseinfoService{
	@Autowired
	private BCarbaseinfoMapper bCarbaseinfoMapper;

	//获取可以当天可出库的车
	@Override
	public List<BCarbaseinfo> getAvailableCar(String week,String text) {
		return bCarbaseinfoMapper.getAvailableCar(week,text);
	}

	//根据车牌号查找车辆信息
	@Override
	public BCarbaseinfo getCarInfoByNum(String carNum) {
		return bCarbaseinfoMapper.getCarInfoByNum(carNum);
	}

	//根据车牌号修改车辆状态
	@Override
	public void updatestatus(String carCode, String status) {
		bCarbaseinfoMapper.updatestatus(carCode,status);
	}

	//修改车辆基本信息
	@Override
	public int update(BCarbaseinfo carInfo) {
		return bCarbaseinfoMapper.update(carInfo);
	}

	//获取所有车辆信息
	@Override
	public List<BCarbaseinfo> getAllCar() {
		return bCarbaseinfoMapper.getAllCar();
	}

	//通过车辆id获取到车辆的具体信息
	@Override
	public BCarbaseinfo getCarInfoById(Integer id) {
		return bCarbaseinfoMapper.getCarInfoById(id);
	}

	//获取所有非报废车
	@Override
	public List<BCarbaseinfo> getAllNormalCar(String text) {
		return bCarbaseinfoMapper.getAllNormalCar(text);
	}

	//分页显示公车基本信息
	@Override
	public List<BCarbaseinfo> carDisplayByPage(int start, int number) {
		return bCarbaseinfoMapper.carDisplayByPage(start,number);
	}

	//获取公车总数量
	@Override
	public int getAllCount() {
		return bCarbaseinfoMapper.getAllCount();
	}

	//添加公车信息
	@Override
	public int insert(BCarbaseinfo car) {
		return bCarbaseinfoMapper.insert(car);
	}
}
