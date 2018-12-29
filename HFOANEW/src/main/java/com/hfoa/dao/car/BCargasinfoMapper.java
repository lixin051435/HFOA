package com.hfoa.dao.car;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.car.BCargasinfo;

/**
 * 
 * @author wzx
 * 车辆加油记录信息dao接口
 */
public interface BCargasinfoMapper {

	//根据车辆id获取车辆加油信息
	@Select({"select * from b_cargasinfo where CarID = #{id,jdbcType=INTEGER}"})
	List<BCargasinfo> getByCarId(Integer id);
	
}