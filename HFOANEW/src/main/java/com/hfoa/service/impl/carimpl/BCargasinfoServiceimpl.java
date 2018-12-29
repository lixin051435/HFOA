package com.hfoa.service.impl.carimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.car.BCargasinfoMapper;
import com.hfoa.entity.car.BCargasinfo;
import com.hfoa.service.car.BCargasinfoService;
/**
 * 
 * @author wzx
 * 车辆加油信息service实现类
 */
@Service
public class BCargasinfoServiceimpl implements BCargasinfoService{
	@Autowired
	private BCargasinfoMapper bCargasinfoMapper;

	@Override
	public List<BCargasinfo> getByCarId(Integer id) {
		return bCargasinfoMapper.getByCarId(id);
	}
}
