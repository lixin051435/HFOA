package com.hfoa.service.common;

import java.util.List;

import com.hfoa.entity.common.BMessagenotice;

/**
 * 
 * @author wzx
 * 微信消息公告的service接口
 */
public interface BMessagenoticeService {

	//分页显示消息通知
	List<BMessagenotice> messageNoticeDisplayByPage(int start, int number);

	//获取消息通知总数量
	int getAllCount();

	//添加消息公告
	int insert(BMessagenotice messageNotice);

	//根据id获取消息公告
	BMessagenotice getById(Integer id);

	//修改消息公告信息
	int update(BMessagenotice message);

	//删除消息公告信息
	int deleteById(int id);

	//获取所有消息公告
	List<BMessagenotice> getAll();

	//根据消息公告获取消息公告详情
	List<BMessagenotice> getByMainTitle(String mainTitle);

}
