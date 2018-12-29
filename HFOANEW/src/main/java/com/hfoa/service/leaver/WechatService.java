package com.hfoa.service.leaver;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

//import org.apache.jasper.tagplugins.jstl.core.Catch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.controller.weixin.YJJYController;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.entity.weixin.TextMessage;
import com.hfoa.service.user.USerService;
import com.hfoa.util.MessageUtil;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;

/**
 */
@Service
public class WechatService {
	private static final Logger LOGGER = LoggerFactory.getLogger(YJJYController.class);
	
	@Autowired
	private USerService usSerService;
	
	
	String respMessage = null;
	public String weixinPost(HttpServletRequest request) {
		System.out.println("消息services");
        String respMessage = null;
        try {

            // xml请求解析
            Map<String, String> requestMap = MessageUtil.xmlToMap(request);
            System.out.println("map是"+requestMap);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            System.out.println("fromUserName是"+fromUserName);
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            System.out.println("toUserName是"+toUserName);
            // 消息类型
            String msgType = requestMap.get("MsgType");
            System.out.println("msgType是"+msgType);
            // 消息内容
            String content = requestMap.get("Content");
            System.out.println("消息内容是"+content);
            LOGGER.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);

            // 文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //这里根据关键字执行相应的逻辑，只有你想不到的，没有做不到的
                UserEntity userEntity = usSerService.loginUserName(content);
                TextMessage text = new TextMessage();
                if(userEntity==null){
                	
                    text.setContent("对不起用户查询失败！请联系管理员");
                    text.setToUserName(fromUserName);
                    text.setFormUserName(toUserName);
                    text.setCreateTime(new Date().getTime()+"");
                    text.setMsgType(msgType);
                }else{
                	System.out.println("不等于null");
                	//自动回复
                	
                	userEntity.setModifiedby(fromUserName);
                	System.out.println("用户"+userEntity);
                	int i =0;
                	  try
                	    {
                		  i = usSerService.updateGzhOpenId(userEntity);
                	    }
                	   catch (Exception ex)

                	    {
                		   System.out.println("错误");
                		   System.out.println(ex.getMessage());
                		   
                	         throw ex;

                	    }
                	System.out.println("错误"+i);
                	if(i==1){
                		System.out.println("绑定"+i);
                        text.setContent("您好，您的微信账户已经与海丰通航信息化小程序绑定成功，此公众号用于接收小程序的推送消息，点击推送消息可以直接进入小程序相应界面");
                        text.setToUserName(fromUserName);
                        text.setFormUserName(toUserName);
                        text.setCreateTime(new Date().getTime()+"");
                        text.setMsgType(msgType);
                	}else{
                		text.setContent("您好，您的微信账户已经与海丰通航信息化小程序绑定失败。");
                        text.setToUserName(fromUserName);
                        text.setFormUserName(toUserName);
                        text.setCreateTime(new Date().getTime()+"");
                        text.setMsgType(msgType);
                	}
                	
                }
                
                respMessage = MessageUtil.textMessageToXml(text);
            }
            /*
            //开启微信声音识别测试 2015-3-30
            else if(msgType.equals("voice"))
            {
                String recvMessage = requestMap.get("Recognition");
                //respContent = "收到的语音解析结果："+recvMessage;
                if(recvMessage!=null){
                    respContent = TulingApiProcess.getTulingResult(recvMessage);
                }else{
                    respContent = "您说的太模糊了，能不能重新说下呢？";
                }
                return MessageResponse.getTextMessage(fromUserName , toUserName , respContent); 
            }
            //拍照功能
            else if(msgType.equals("pic_sysphoto"))
            {
                
            }
            else
            {
                return MessageResponse.getTextMessage(fromUserName , toUserName , "返回为空"); 
            }*/
            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                String eventType = requestMap.get("Event");// 事件类型
                // 订阅
                if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    
                    TextMessage text = new TextMessage();
                    text.setContent("欢迎关注");
                    text.setToUserName(fromUserName);
                    text.setFormUserName(toUserName);
                    text.setCreateTime(new Date().getTime() + "");
                    text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                    
                    respMessage = MessageUtil.textMessageToXml(text);
                } 
                // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {// 取消订阅
                    
                    
                } 
                // 自定义菜单点击事件
                else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    String eventKey = requestMap.get("EventKey");// 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    if (eventKey.equals("customer_telephone")) {
                        TextMessage text = new TextMessage();
                        text.setContent("0755-86671980");
                        text.setToUserName(fromUserName);
                        text.setFormUserName(toUserName);
                        text.setCreateTime(new Date().getTime() + "");
                        text.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                        
                        respMessage = MessageUtil.textMessageToXml(text);
                    }
                }
            }
        }
        catch (Exception e) {
        	LOGGER.error("error......");
        }
        return respMessage;
    }
	
}
