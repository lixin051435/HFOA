package com.hfoa.service.impl.commonimpl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.common.BNoticeMapper;
import com.hfoa.entity.common.BNotice;
import com.hfoa.service.common.BNoticeService;

/**
 * 
 * @author wzx
 * 微信公告service实现类
 */
@Service
public class BNoticeServiceImpl implements BNoticeService{

	@Autowired
	private BNoticeMapper bNoticeMapper;

	//分页查询微信公告展示
	@Override
	public List<BNotice> noticeDisplayByPage(int start, int number) {
		return bNoticeMapper.noticeDisplayByPage(start,number);
	}

	//查询公告总数
	@Override
	public int getAllCount() {
		return bNoticeMapper.getAllCount();
	}

	//添加公告
	@Override
	public Integer insert(BNotice bNotice) {
		return bNoticeMapper.insert(bNotice);
	}

	//修改公告信息
	@Override
	public Integer update(BNotice bNotice) {
		return bNoticeMapper.update(bNotice);
	}

	//根据id获取公告信息
	@Override
	public BNotice selectById(int noticeid) {
		return bNoticeMapper.selectById(noticeid);
	}

	//删除公告信息
	@Override
	public Integer deleteById(int id) {
		return bNoticeMapper.deleteById(id);
	}

	//分页模糊查询公告信息
	@Override
	public List<BNotice> noticeVagueByPage(int start, int number, String title) {
		title="%"+title+"%";
		return bNoticeMapper.noticeVagueByPage(start,number,title);
	}

	//模糊查询公告数量
	@Override
	public int getVagueCount(String title) {
		title="%"+title+"%";
		return bNoticeMapper.getVagueCount(title);
	}

	//获取在前台显示的图片
	@Override
	public List<BNotice> getAvailable() {
		return bNoticeMapper.getAvailable();
	}

	//获取所有可用公告图片的数量
	@Override
	public int countAvailable() {
		return bNoticeMapper.countAvailable();
	}

}
