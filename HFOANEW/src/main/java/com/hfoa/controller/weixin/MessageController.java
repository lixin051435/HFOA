package com.hfoa.controller.weixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hfoa.entity.weixin.Template;
import com.hfoa.entity.weixin.TemplateParam;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.Configure;

@Controller
public class MessageController {
	
	/**
	 * 获取openId
	 * @param request
	 * @param model
	 * @throws IOException 
	 */
	@RequestMapping("/getOpenId")  
    public void getOpenId(String code, HttpServletRequest request,Model model,HttpServletResponse response) throws IOException{ 
		    String requestUrl="https://api.weixin.qq.com/sns/jscode2session?appid="+Configure.getAppID()+"&secret="+Configure.getSecret()+"&js_code="+code+"&grant_type=authorization_code";  
	        System.out.println("查看URL是"+requestUrl);
		    JSONObject jsonResult=CommonUtil.httpsRequest(requestUrl, "POST", null);  
	        String openId ="";
	        if(jsonResult!=null){  
	           openId=jsonResult.getString("openid");  
	        }   
	        System.out.println("获取oppenid是"+openId);
	        response.getWriter().append(openId);
    }  
    
	/**
	 * 提交完成发送微信消息
	 * @param request
	 * @param model
	 */
	/* @RequestMapping("/sendMessage")  */
	    public static void sendMessage(String openId,String formId,HttpServletRequest request,Model model,WeiEntity weiEntity){  
		 	System.out.println("openid是"+openId);
		 	System.out.println("formID是"+formId);
		 	
		    Template tem=new Template();  
			tem.setTemplateId(Configure.getSubmit_id());  
			tem.setFormId(formId);
			tem.setTopColor("#00DD00");  
			tem.setToUser(openId);
			tem.setPages("applylist");
			tem.setUrl("");  
			
			List<TemplateParam> paras=new ArrayList<TemplateParam>();  
			paras.add(new TemplateParam("keyword1",weiEntity.getApplyMan(),"#0044BB"));//申请人
			paras.add(new TemplateParam("keyword2",weiEntity.getDepartMent(),"#0044BB"));//所属部门
			paras.add(new TemplateParam("keyword3",weiEntity.getLeaveType(),"#AAAAAA"));//申请类型
			paras.add(new TemplateParam("keyword4",weiEntity.getApplyTime(),"#0044BB"));//申请时间
			paras.add(new TemplateParam("keyword5",weiEntity.getApproveMan(),"#0044BB"));//审批人
			          
			tem.setTemplateParamList(paras);        
			boolean result=sendTemplateMsg(tem); 
			System.out.println(result);
	    }  
	    
	    //审批通过流程
	    public static void updatePassSendMessage(String openId,String formId,HttpServletRequest request,Model model,WeiEntity weiEntity){  
		 	System.out.println("openid是"+openId);
		 	System.out.println("formID是"+formId);
		 	
		    Template tem=new Template();  
			tem.setTemplateId(Configure.getSubmit_id());  
			tem.setFormId(formId);
			tem.setTopColor("#00DD00");  
			tem.setToUser(openId);
			tem.setPages("applyexcute");
			tem.setUrl("");  
			
			List<TemplateParam> paras=new ArrayList<TemplateParam>();  
			paras.add(new TemplateParam("keyword1",weiEntity.getApplyMan(),"#0044BB"));//申请人
			paras.add(new TemplateParam("keyword2",weiEntity.getDepartMent(),"#0044BB"));//所属部门
			paras.add(new TemplateParam("keyword3",weiEntity.getLeaveType(),"#AAAAAA"));//申请类型
			paras.add(new TemplateParam("keyword4",weiEntity.getApplyTime(),"#0044BB"));//申请时间
			paras.add(new TemplateParam("keyword5",weiEntity.getApproveMan(),"#0044BB"));//审批人
			          
			tem.setTemplateParamList(paras);        
			boolean result=sendTemplateMsg(tem); 
			System.out.println(result);
	    }  
	 
	 
	 public static boolean sendTemplateMsg(Template template){  
		   String token = getToken(template);
		   System.out.println("token是"+token);
	       boolean flag=false;   
	        String requestUrl="https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=ACCESS_TOKEN";  
	        requestUrl=requestUrl.replace("ACCESS_TOKEN", token);  
	        String jsonString = template.toJSON();
	        System.out.println("格式化的数据为"+jsonString);
	        JSONObject jsonResult=CommonUtil.httpsRequest(requestUrl, "POST", jsonString);  
	        if(jsonResult!=null){  
	            int errorCode=jsonResult.getInt("errcode");  
	            String errorMessage=jsonResult.getString("errmsg");  
	            if(errorCode==0){  
	                flag=true;  
	            }else{  
	                System.out.println("模板消息发送失败:"+errorCode+","+errorMessage);  
	                flag=false;  
	            }  
	        }
	        return flag;          
	    }  
		
		
		/**
		 *获取token
		 * @param template
		 * @return
		 */
	    public static String getToken(Template template){  
	        String requestUrl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+Configure.getAppID()+"&secret="+Configure.getSecret();  
	        JSONObject jsonResult=CommonUtil.httpsRequest(requestUrl, "POST", template.toJSON());  
	        if(jsonResult!=null){  
	            String access_token=jsonResult.getString("access_token");  
	            return access_token;
	        }else{  
	        	  return "";     
	        }    
	    }  
}
