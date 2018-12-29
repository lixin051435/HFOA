package com.hfoa.dao.car;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.car.BInsurance;

public interface BInsuranceMapper {

	//根据车牌号获取车辆保险信息
	@Select({"select * from b_insurance where CarNum = #{carnum,jdbcType=VARCHAR}"})
	List<BInsurance> getByCarNum(String carnum);
	
}