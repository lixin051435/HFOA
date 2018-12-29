
package com.hfoa.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;  
import java.net.ConnectException;  
import java.net.HttpURLConnection;  
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLSocketFactory;  
import javax.net.ssl.TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.hfoa.entity.weixin.MyX509TrustManager;
import com.hfoa.entity.weixin.Sender;
import com.hfoa.entity.weixin.Template;
import com.hfoa.entity.weixin.TemplateParam;
import com.hfoa.entity.weixin.WeiEntity;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.TemplateSender;
import com.soecode.wxtools.bean.result.TemplateSenderResult;
import com.soecode.wxtools.exception.WxErrorException;

import net.sf.json.JSONObject;  
  
public class CommonUtil {  
	
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {   
          
        JSONObject jsonObject = null;  
        StringBuffer buffer = new StringBuffer();    
        try {    
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化    
            TrustManager[] tm = { new MyX509TrustManager() };    
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");    
            sslContext.init(null, tm, new java.security.SecureRandom());    
            // 从上述SSLContext对象中得到SSLSocketFactory对象    
            SSLSocketFactory ssf = sslContext.getSocketFactory();    
    
            URL url = new URL(requestUrl);    
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();    
            httpUrlConn.setSSLSocketFactory(ssf);    
    
            httpUrlConn.setDoOutput(true);    
            httpUrlConn.setDoInput(true);    
            httpUrlConn.setUseCaches(false);    
            // 设置请求方式（GET/POST）    
            httpUrlConn.setRequestMethod(requestMethod);    
    
            if ("GET".equalsIgnoreCase(requestMethod)) {  
                 httpUrlConn.connect();    
            }   
                 
    
            // 当有数据需要提交时    
            if (null != outputStr) {    
                OutputStream outputStream = httpUrlConn.getOutputStream();    
                // 注意编码格式，防止中文乱码    
                outputStream.write(outputStr.getBytes("UTF-8"));    
                outputStream.close();    
            }    
    
            // 将返回的输入流转换成字符串    
            InputStream inputStream = httpUrlConn.getInputStream();    
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");    
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);    
    
            String str = null;    
            while ((str = bufferedReader.readLine()) != null) {    
                buffer.append(str);    
            }    
            bufferedReader.close();    
            inputStreamReader.close();    
            // 释放资源    
            inputStream.close();    
            inputStream = null;    
            httpUrlConn.disconnect();    
            jsonObject = JSONObject.fromObject(buffer.toString());    
        } catch (ConnectException ce) {    
            ce.printStackTrace();  
        } catch (Exception e) {    
            e.printStackTrace();  
        }    
        return jsonObject;    
    }  
      
    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {   
          
          
        StringBuffer buffer = new StringBuffer();    
        try {    
            
    
            URL url = new URL(requestUrl);    
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();    
             
    
            httpUrlConn.setDoOutput(true);    
            httpUrlConn.setDoInput(true);    
            httpUrlConn.setUseCaches(false);    
            // 设置请求方式（GET/POST）    
            httpUrlConn.setRequestMethod(requestMethod);    
    
            if ("GET".equalsIgnoreCase(requestMethod)) {  
                 httpUrlConn.connect();    
            }   
                 
    
            // 当有数据需要提交时    
            if (null != outputStr) {    
                OutputStream outputStream = httpUrlConn.getOutputStream();    
                // 注意编码格式，防止中文乱码    
                outputStream.write(outputStr.getBytes("UTF-8"));    
                outputStream.close();    
            }    
    
            // 将返回的输入流转换成字符串    
            InputStream inputStream = httpUrlConn.getInputStream();    
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");    
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);    
    
            String str = null;    
            while ((str = bufferedReader.readLine()) != null) {    
                buffer.append(str);    
            }    
            bufferedReader.close();    
            inputStreamReader.close();    
            // 释放资源    
            inputStream.close();    
            inputStream = null;    
            httpUrlConn.disconnect();    
            //jsonObject = JSONObject.fromObject(buffer.toString());    
        } catch (ConnectException ce) {    
            ce.printStackTrace();  
        } catch (Exception e) {    
            e.printStackTrace();  
        }    
        return buffer.toString();    
    }  
    public static String urlEncodeUTF8(String source){  
        String result = source;  
        try {  
            result = java.net.URLEncoder.encode(source,"utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  
      
    public static String httpsRequestForStr(String requestUrl, String requestMethod, String outputStr) {   
          
        String result="";  
        StringBuffer buffer = new StringBuffer();    
        try {    
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化    
            TrustManager[] tm = { new MyX509TrustManager() };    
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");    
            sslContext.init(null, tm, new java.security.SecureRandom());    
            // 从上述SSLContext对象中得到SSLSocketFactory对象    
            SSLSocketFactory ssf = sslContext.getSocketFactory();    
    
            URL url = new URL(requestUrl);    
            HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();    
            httpUrlConn.setSSLSocketFactory(ssf);    
    
            httpUrlConn.setDoOutput(true);    
            httpUrlConn.setDoInput(true);    
            httpUrlConn.setUseCaches(false);    
            // 设置请求方式（GET/POST）    
            httpUrlConn.setRequestMethod(requestMethod);    
    
            if ("GET".equalsIgnoreCase(requestMethod)) {  
                 httpUrlConn.connect();    
            }   
                 
    
            // 当有数据需要提交时    
            if (null != outputStr) {    
                OutputStream outputStream = httpUrlConn.getOutputStream();    
                // 注意编码格式，防止中文乱码    
                outputStream.write(outputStr.getBytes("UTF-8"));    
                outputStream.close();    
            }    
    
            // 将返回的输入流转换成字符串    
            InputStream inputStream = httpUrlConn.getInputStream();    
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");    
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);    
    
            String str = null;    
            while ((str = bufferedReader.readLine()) != null) {    
                buffer.append(str);    
            }    
            bufferedReader.close();    
            inputStreamReader.close();    
            // 释放资源    
            inputStream.close();    
            inputStream = null;    
            httpUrlConn.disconnect();    
            result=buffer.toString();    
        } catch (ConnectException ce) {    
            ce.printStackTrace();  
        } catch (Exception e) {    
            e.printStackTrace();  
        }    
        return result;    
    }  
    
    
    //微信公众号提交申请推送消息
    public static  boolean sendMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getApply_id());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApplyTime());
		time.put("color","#173177");
		content.put("value",weiEntity.getApplyMan());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getLeaveType());
		leaveType.put("color","#173177");
		substance.put("value","请假天数"+weiEntity.getDays()+",休假次数是"+weiEntity.getFrequency()+"次");
		substance.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		data.put("keyword4",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    //微信公众号审批状态推送
    public static boolean sendApply(String openid,WeiEntity weiEntity,String status,String view) throws JsonGenerationException, JsonMappingException, IOException{
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getSeuccess_id());
		//发送给辛源才的ID
		sender2.setTouser(openid);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject opention = new JSONObject();
		JSONObject remark=new JSONObject();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		first.put("value", "您好，您的申请已经处理"); 
		first.put("color", "#173177");
		data.put("first", first);
		time.put("value", weiEntity.getLeaveType());
		time.put("color","#173177");
		content.put("value", weiEntity.getApproveMan());
		content.put("color", "#173177");
		opention.put("value", view);
		opention.put("color", "#173177");
		leaveType.put("value", status);
		leaveType.put("color", "#173177");
		data.put("keyword1", time);
		data.put("keyword2", content);
		data.put("keyword3", opention);
		data.put("keyword4", leaveType);
		remark.put("value", "查看详情");
		remark.put("color", "#173177");
		data.put("remark", remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
    	return result;
    }
    
    
    
    //微信待执行
    public static boolean sendSubmit(String openid,WeiEntity weiEntity) throws JsonGenerationException, JsonMappingException, IOException{
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getSubmit_id());
		//发送给辛源才的ID
		sender2.setTouser(openid);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject success = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value", "您好，您的年假待处理"); 
		first.put("color", "#173177");
		data.put("first", first);
		time.put("value", weiEntity.getLeaveType());
		time.put("color","#173177");
		content.put("value", weiEntity.getBegingTime());
		content.put("color", "#173177");
		leaveType.put("value", weiEntity.getEndTime());
		leaveType.put("color", "#173177");
		success.put("value", "待执行");
		success.put("color", "#173177");
		data.put("keyword1", time);
		data.put("keyword2", content);
		data.put("keyword3", leaveType);
		data.put("keyword4", success);
		remark.put("value", "详情请在小程序查看");
		remark.put("color", "#173177");
		data.put("remark", remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
    	return result;
    }
    
    
    
  //微信公众号异常推送消息
    public static  boolean sendAbnormalMessage(String openId,WeiEntity weiEntity) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getAppID());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject success = new JSONObject();
		JSONObject remark=new JSONObject();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		first.put("value","您好，您有新的异常年假待审批"); 
		first.put("color","#173177");
		data.put("first", first);
		time.put("value", weiEntity.getLeaveType());
		time.put("color","#173177");
		content.put("value", weiEntity.getBegingTime());
		content.put("color", "#173177");
		leaveType.put("value", weiEntity.getEndTime());
		leaveType.put("color", "#173177");
		success.put("value", "异常待审批");
		success.put("color", "#173177");
		data.put("keyword1", time);
		data.put("keyword2", content);
		data.put("keyword3", leaveType);
		data.put("keyword4", success);
		remark.put("value", "详情请在小程序查看");
		remark.put("color", "#173177");
		data.put("remark", remark);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
  
    
    public static boolean sendTemplateMsg(Sender template) throws JsonGenerationException, JsonMappingException, IOException{  
		   String token = getToken( template);
		   System.out.println("token是"+token);
	       boolean flag=false;   
	        String requestUrl="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";  
	        requestUrl=requestUrl.replace("ACCESS_TOKEN", token);  
	        String jsonString = template.toJson();
	        System.out.println("格式化的数据为"+jsonString);
	        JSONObject jsonResult=CommonUtil.httpsRequest(requestUrl, "POST", jsonString);  
	        if(jsonResult!=null){  
	        	System.out.println("发送");
	            int errorCode=jsonResult.getInt("errcode");  
	            String errorMessage=jsonResult.getString("errmsg");  
	            if(errorCode==0){  
	            	System.out.println("发送成功");
	                flag=true;  
	            }else{  
	                System.out.println("模板消息发送失败:"+errorCode+","+errorMessage);  
	                flag=false;  
	            }  
	        }
	        return flag;          
	    }  
    
    public static String getToken(Sender template) throws JsonGenerationException, JsonMappingException, IOException{  
        String requestUrl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+Configure.getAppliIDGZH()+"&secret="+Configure.getGZHsecret();  
        JSONObject jsonResult=CommonUtil.httpsRequest(requestUrl, "POST", template.toJson());  
        if(jsonResult!=null){  
            String access_token=jsonResult.getString("access_token");  
            return access_token;
        }else{  
        	  return "";     
        }    
    }  
    //格式化status
    public static String setStatus(String status){
    	String s = "";
    	if(status.equals("1")){
    		s = "待部门审批";
    	}else if(status.equals("2")){
    		s = "驳回待修改";
    	}else if(status.equals("3")){
    		s = "待员工确认";
    	}else if(status.equals("5")){
    		s = "完结";
    	}else if(status.equals("6")){
    		s = "放弃";
    	}else if(status.equals("7")){
    		s = "转接到第二年";
    	}else if(status.equals("8")){
    		s = "现金补偿";
    	}else if(status.equals("9")){
    		s = "异常";
    	}
    	return s;
    }
    
    
    //微信公众号提交申请推送消息
    public static  boolean sendTravelMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getTravel_id());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApplyMan());
		time.put("color","#173177");
		content.put("value",weiEntity.getStatus());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getDepartMent());
		leaveType.put("color","#173177");
		substance.put("value",weiEntity.getTripContent());
		substance.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		data.put("keyword4",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    //微信公众号审批推送消息
    public static  boolean sendTravelApproveMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getTravel_submitId());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApproveMan());
		time.put("color","#173177");
		content.put("value",weiEntity.getApplyMan());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getStatus());
		leaveType.put("color","#173177");
		substance.put("value",weiEntity.getTripContent());
		substance.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		data.put("keyword4",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    
  //微信公众号审批推送消息
    public static  boolean sendTravelApplyMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getTravel_submitId());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApproveMan());
		time.put("color","#173177");
		content.put("value",weiEntity.getApplyMan());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getStatus());
		leaveType.put("color","#173177");
		substance.put("value",weiEntity.getTripContent());
		substance.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		data.put("keyword4",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    
    
    
    
    /**
     * 业务招待推送
     * @param openId
     * @param weiEntity
     * @param title
     * @param mark
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static  boolean sendEntertainApproveMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getEntertain_submitId());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getType());
		time.put("color","#173177");
		content.put("value",weiEntity.getApplyTime());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getMasterBudget());
		leaveType.put("color","#173177");
		substance.put("value",weiEntity.getCause());
		substance.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		data.put("keyword4",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    
    /**
     * 业务招待审批推送
     * @param openId
     * @param weiEntity
     * @param title
     * @param mark
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static  boolean sendPrivateApproveMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getPrivatecar_id());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject substance = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getTitle());
		time.put("color","#173177");
		content.put("value",weiEntity.getApplyMan());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getStatus());
		leaveType.put("color","#173177");
		substance.put("color", "#173177");
		substance.put("value", weiEntity.getApplyTime());
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",substance);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    
    
    public static  boolean sendEntertainApplyMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getEntertain_id());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApproveMan());
		time.put("color","#173177");
		content.put("value",weiEntity.getStatus());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getApplyTime());
		leaveType.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    public static  boolean sendPrivateCarApplyMessage(String openId,WeiEntity weiEntity,String title,String mark) throws JsonGenerationException, JsonMappingException, IOException{
		//插入申请信息
    	Sender sender2 = new  Sender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id(Configure.getEntertain_id());
		//发送给辛源才的ID
		sender2.setTouser(openId);
		JSONObject miniprogram = new JSONObject();
		miniprogram.put("appid", Configure.getAppID());
		miniprogram.put("pagepath", "pages/mainPages/mine/mine");
		sender2.setMiniprogram(miniprogram);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject leaveType = new JSONObject();
		JSONObject remark=new JSONObject();
		first.put("value",title); 
		first.put("color","#173177");
		data.put("first",first);
		time.put("value",weiEntity.getApplyMan());
		time.put("color","#173177");
		content.put("value",weiEntity.getStatus());
		content.put("color","#173177");
		leaveType.put("value",weiEntity.getApplyTime());
		leaveType.put("color","#173177");
		data.put("keyword1",time);
		data.put("keyword2",content);
		data.put("keyword3",leaveType);
		remark.put("value",mark);
		remark.put("color","#173177");
		data.put("remark",remark);
		sender2.setData(data);
		boolean result=sendTemplateMsg(sender2); 
		return result;
	}
    
    
    
    
    //获取当前的时间点
  	public static  String getTime(){
  		SimpleDateFormat simple=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		return simple.format(new Date());
  	}
    
    
}  
