package com.hfoa.service.car;

import java.util.List;

import com.hfoa.entity.car.BInsurance;

/**
 * 
 * @author wzx
 * 车辆保险信息service接口
 */
public interface BInsuranceService {

	//根据车牌号获取车辆的保险信息
	List<BInsurance> getByCarNum(String carnum);

}
