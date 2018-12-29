package com.hfoa.service.car;

import java.util.List;

import com.hfoa.entity.car.BCargasinfo;

/**
 * 
 * @author wzx
 * 车辆加油信息service接口
 */
public interface BCargasinfoService {

	//根据车辆id获取车辆加油信息
	List<BCargasinfo> getByCarId(Integer id);

}
