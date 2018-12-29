package com.hfoa.dao.common;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.common.BMessagenotice;

public interface BMessagenoticeMapper {

	//删除消息公告信息
    int deleteById(Integer id);

    //添加消息公告
    int insert(BMessagenotice record);

    int update(BMessagenotice record);

    //分页显示消息通知
    @Select({"select * from b_messageNotice order by id desc limit #{start,jdbcType=INTEGER},#{number,jdbcType=INTEGER}"})
	List<BMessagenotice> messageNoticeDisplayByPage(@Param("start")int start, @Param("number")int number);

    //获取消息通知总数量
    @Select({"select count(*) from b_messageNotice"})
	int getAllCount();

    //根据id获取消息公告
    @Select({"select * from b_messageNotice where id = #{id,jdbcType=INTEGER}"})
	BMessagenotice getById(@Param("id")Integer id);

    //获取所有消息公告
    @Select({"select * from b_messageNotice"})
	List<BMessagenotice> getAll();

    //根据消息公告获取消息公告详情
    @Select({"select * from b_messageNotice where mainTitle = #{mainTitle,jdbcType=VARCHAR} order by userTime desc"})
	List<BMessagenotice> getByMainTitle(@Param("mainTitle")String mainTitle);
}