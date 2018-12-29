package com.hfoa.dao.common;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.common.BNotice;

/**
 * 
 * @author wzx
 * 微信公告dao接口
 */
public interface BNoticeMapper {

	//分页查询微信公告展示
	@Select({"select * from b_notice order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BNotice> noticeDisplayByPage(@Param("start")int start, @Param("number")int number);

	//查询公告总数
	@Select({"select count(*) from b_notice"})
	int getAllCount();

	//添加公告
	Integer insert(BNotice bNotice);

	//修改公告信息
	Integer update(BNotice bNotice);

	//根据id获取公告信息
	BNotice selectById(int id);

	//删除公告信息
	Integer deleteById(int id);

	//分页模糊查询公告信息
	@Select({"select * from b_notice where content like #{title,jdbcType=VARCHAR} order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BNotice> noticeVagueByPage(@Param("start")int start, @Param("number")int number, @Param("title")String title);

	//模糊查询公告数量
	@Select({"select count(*) from b_notice where content like #{title,jdbcType=VARCHAR}"})
	int getVagueCount(@Param("title")String title);

	//获取在前台显示的图片
	@Select({"select * from b_notice where linkUrl='是' order by id desc"})
	List<BNotice> getAvailable();

	//获取所有可用公告图片的数量
	@Select({"select count(*) from b_notice where linkUrl='是'"})
	int countAvailable();

}