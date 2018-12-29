package com.hfoa.service.impl.carimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.car.BInsuranceMapper;
import com.hfoa.entity.car.BInsurance;
import com.hfoa.service.car.BInsuranceService;
/**
 * 
 * @author wzx
 * 车辆保险信息service实现类
 */
@Service
public class BInsuranceServiceimpl implements BInsuranceService{
	@Autowired
	private BInsuranceMapper bInsuranceMapper;

	//根据车牌号获取车辆保险信息
	@Override
	public List<BInsurance> getByCarNum(String carnum) {
		return bInsuranceMapper.getByCarNum(carnum);
	}
}
