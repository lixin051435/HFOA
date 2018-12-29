package com.hfoa.service.impl.commonimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.common.BMessagenoticeMapper;
import com.hfoa.entity.common.BMessagenotice;
import com.hfoa.service.common.BMessagenoticeService;
/**
 * 
 * @author wzx
 * 微信消息公告service实现类
 */
@Service
public class BMessagenoticeServiceImpl implements BMessagenoticeService{

	@Autowired
	private BMessagenoticeMapper bMessagenoticeMapper;

	//分页显示消息通知
	@Override
	public List<BMessagenotice> messageNoticeDisplayByPage(int start, int number) {
		return bMessagenoticeMapper.messageNoticeDisplayByPage(start,number);
	}

	//获取消息通知总数量
	@Override
	public int getAllCount() {
		return bMessagenoticeMapper.getAllCount();
	}

	//添加消息公告
	@Override
	public int insert(BMessagenotice messageNotice) {
		return bMessagenoticeMapper.insert(messageNotice);
	}

	//根据id获取消息公告
	@Override
	public BMessagenotice getById(Integer id) {
		return bMessagenoticeMapper.getById(id);
	}

	//修改消息公告信息
	@Override
	public int update(BMessagenotice message) {
		return bMessagenoticeMapper.update(message);
	}

	//删除消息公告信息
	@Override
	public int deleteById(int id) {
		return bMessagenoticeMapper.deleteById(id);
	}

	//获取所有消息公告
	@Override
	public List<BMessagenotice> getAll() {
		return bMessagenoticeMapper.getAll();
	}

	//根据消息公告获取消息公告详情
	@Override
	public List<BMessagenotice> getByMainTitle(String mainTitle) {
		return bMessagenoticeMapper.getByMainTitle(mainTitle);
	}
}
