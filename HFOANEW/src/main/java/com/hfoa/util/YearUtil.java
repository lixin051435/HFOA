
package com.hfoa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.hfoa.dao.lear.YearLearMapper;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.TemplateSender;
import com.soecode.wxtools.bean.result.TemplateSenderResult;
import com.soecode.wxtools.exception.WxErrorException;
public class YearUtil {

	
	private IService iService = new WxService();
	
	@Autowired
	private YearLearMapper yearLearMapper;
	
	/**
	 * 查询所有待执行年假的员工进行推送提示
	 * @throws InterruptedException
	 * @throws ParseException 
	 */
//	@Scheduled(cron="0 0 1 * * ? ")   //每天凌晨一点执行一次
	@Scheduled(cron="0/5 * *  * * ? ")   
    public void aTask() throws InterruptedException, ParseException{      
         System.out.println("5秒执行一次");
		List<Map<String,Object>> list = yearLearMapper.listExecuted();//获取所有年假
		Date nowDate = new Date();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
/*		for(Map<String,Object> map:list){
			String begingTime = map.get("beingTime").toString();
			System.out.println("开始时间是"+begingTime);
			Date date = smf.parse(begingTime);
			System.out.println("当前时间"+smf.format(nowDate));
			int days = (int) ((date.getTime() - nowDate.getTime()) / (1000*3600*24));
			System.out.println("天数是"+days);
			if(days<=3&&days>0){//
				//距离休假还有三天所以开始推送
				System.out.println("推送");
				String openId = map.get("openId").toString();//获取待执行的OpenId进行推送
				//需要将模板消息推送给一个人
				TemplateSender sender = new  TemplateSender();
				sender.setTemplate_id(Configure.getApply_id());//模板ID
				sender.setTouser(openId);//微信用户唯一openId
				//url不需要指定
				JSONObject data=new JSONObject();
				JSONObject first=new JSONObject();
				first.put("value", "haha");
				first.put("color", "173177");
				data.put("first", first);
				JSONObject keyword1=new JSONObject();
				JSONObject keyword2=new JSONObject();
				JSONObject keyword3=new JSONObject();
				JSONObject keyword4=new JSONObject();
				JSONObject keyword5=new JSONObject();
				JSONObject remark=new JSONObject();
				keyword1.put("value", "2018-12-12");
				keyword1.put("color", "173177");
				data.put("keyword1", keyword1);
				keyword2.put("value", "急救");
				keyword2.put("color", "173177");
				data.put("keyword2", keyword2);
				keyword3.put("value", "88.00");
				keyword3.put("color", "173177");
				data.put("keyword3", keyword3);
				keyword4.put("value", "haha");
				keyword4.put("color", "173177");
				data.put("keyword4", keyword4);
				keyword5.put("value", "haha");
				keyword5.put("color", "173177");
				data.put("keyword5", keyword5);
				remark.put("value", "haha");
				remark.put("color", "173177");
				data.put("remark", remark);
				sender.setData(data);
				TemplateSenderResult result;
				try {
					result = iService.templateSend(sender);
					System.out.println(result.toString());
				} catch (WxErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}else if(days<0){
				System.out.println("异常");
				//生成异常
				yearLearMapper.updateAbnormal(map.get("id").toString());
				
			}*/
			
		}
		
		
		
  
	
	public  int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        System.out.println("天数是"+days);
        return days;
    }
	
	public String sendMessage(HttpServletRequest request,HttpServletResponse response,Map<String,Object>map){
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("flag", true);
		TemplateSender sender = new  TemplateSender();
		//模板ID
		sender.setTemplate_id("OlYzsracple0EZHaeoSuhD3iYRrcm02di2fB1RZRMqA");
		//发送OpenID
		sender.setTouser(map.get("openId").toString());
		//url不用指定
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		first.put("value", "待执行");
		first.put("color", "173177");
		data.put("first", first);
		JSONObject keyword1=new JSONObject();
		JSONObject keyword2=new JSONObject();
		JSONObject keyword3=new JSONObject();
		JSONObject keyword4=new JSONObject();
		JSONObject keyword5=new JSONObject();
		JSONObject remark=new JSONObject();
		keyword1.put("value", "2018-12-12");
		keyword1.put("color", "173177");
		data.put("keyword1", keyword1);
		keyword2.put("value", "急救");
		keyword2.put("color", "173177");
		data.put("keyword2", keyword2);
		keyword3.put("value", "88.00");
		keyword3.put("color", "173177");
		data.put("keyword3", keyword3);
		keyword4.put("value", "haha");
		keyword4.put("color", "173177");
		data.put("keyword4", keyword4);
		keyword5.put("value", "haha");
		keyword5.put("color", "173177");
		data.put("keyword5", keyword5);
		remark.put("value", "haha");
		remark.put("color", "173177");
		data.put("remark", remark);
		sender.setData(data);
		TemplateSenderResult result = null;
		
		
		
		try {
			result = iService.templateSend(null);
			System.out.println(result.toString());
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

}
	

