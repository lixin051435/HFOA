package com.hfoa.service.common;

import java.util.List;

import com.hfoa.entity.common.BNotice;

/**
 * 
 * @author wzx
 * 微信公告的service接口
 */
public interface BNoticeService {

	//分页查询微信公告展示
	List<BNotice> noticeDisplayByPage(int start, int number);

	//查询公告总数
	int getAllCount();

	//添加公告
	Integer insert(BNotice bNotice);

	//修改公告信息
	Integer update(BNotice bNotice);

	//根据id获取公告信息
	BNotice selectById(int noticeid);

	//删除公告信息
	Integer deleteById(int id);

	//分页模糊查询公告信息
	List<BNotice> noticeVagueByPage(int start, int number, String title);

	//模糊查询公告数量
	int getVagueCount(String title);

	//获取在前台显示的图片
	List<BNotice> getAvailable();

	//获取所有可用公告图片的数量
	int countAvailable();

}
