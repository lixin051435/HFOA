package com.hfoa.service.impl.printingimp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.printing.BGzapplyinfoMapper;
import com.hfoa.dao.printing.BGzkindMapper;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.entity.printing.BGzkind;
import com.hfoa.service.printing.BGzapplyinfoService;
import com.hfoa.service.printing.BGzkindService;

/**
 * 
 * @author wzx
 * 公章种类service实现类
 */
@Service
public class BGzkindServiceImpl implements BGzkindService{

	@Autowired
	private BGzkindMapper bGzkindMapper;

	//修改公章信息
	@Override
	public int update(BGzkind bGzkind) {
		return bGzkindMapper.update(bGzkind);
	}

	//添加公章信息
	@Override
	public int insert(BGzkind bGzkind) {
		return bGzkindMapper.insert(bGzkind);
	}

	//删除公章信息
	@Override
	public int deleteById(int id) {
		return bGzkindMapper.deleteById(id);
	}

	//获取所有公章类型
	@Override
	public List<BGzkind> getAllGzKind() {
		return bGzkindMapper.getAllGzKind();
	}

	//获取公章中最小级别
	@Override
	public int getMinGrade() {
		return bGzkindMapper.getMinGrade();
	}

	//根据id获取公章种类
	@Override
	public BGzkind getById(int id) {
		return bGzkindMapper.getById(id);
	}

	//添加公章类型
	@Override
	public void save(BGzkind gz) {
		bGzkindMapper.insert(gz);
	}

}
