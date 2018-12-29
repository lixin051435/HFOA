package com.hfoa.controller.weixin;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.jstl.test.beans.PublicInterface2;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hfoa.service.leaver.WechatService;
import com.hfoa.util.SignUtil;
import com.itextpdf.text.log.LoggerFactory;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConfig;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.api.WxMessageRouter;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.TemplateSender;
import com.soecode.wxtools.bean.WxJsapiConfig;
import com.soecode.wxtools.bean.WxMenu;
import com.soecode.wxtools.bean.WxMenu.WxMenuButton;
import com.soecode.wxtools.bean.result.TemplateSenderResult;
import com.soecode.wxtools.bean.result.WxCurMenuInfoResult;
import com.soecode.wxtools.bean.result.WxMenuResult;
import com.soecode.wxtools.bean.WxXmlMessage;
import com.soecode.wxtools.bean.WxXmlOutMessage;
import com.soecode.wxtools.exception.WxErrorException;
import com.soecode.wxtools.handler.DemoHandler;
import com.soecode.wxtools.interceptor.DemoInterceptor;
import com.soecode.wxtools.matcher.DemoMatcher;
import com.soecode.wxtools.util.xml.XStreamTransformer;

import weixin.popular.bean.xmlmessage.XMLMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.support.ExpireKey;
import weixin.popular.support.expirekey.DefaultExpireKey;
import weixin.popular.util.SignatureUtil;


//根路径测试
//映射需要加上wx上  wx-tools里面有了
@Controller
@RequestMapping("/properties/")
public class YJJYController {
	private static final com.itextpdf.text.log.Logger LOGGER = LoggerFactory.getLogger(YJJYController.class);
	//微信服务器接口  全在这里面
	private IService iService = new WxService();
	
	@Autowired
	private  WechatService wechatService;
	
	//重复
	private static ExpireKey expireKey = new DefaultExpireKey();
	
	@RequestMapping(value="/token",method = {RequestMethod.GET, RequestMethod.POST})
	public void tokenGet(HttpServletRequest request, HttpServletResponse response)throws Exception{
		System.out.println("消息测试");
		request.setCharacterEncoding("UTF-8");  //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        response.setCharacterEncoding("UTF-8"); //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；boolean isGet = request.getMethod().toLowerCase().equals("get"); 
        boolean isGet = request.getMethod().toLowerCase().equals("get"); 
        System.out.println("isGet是"+isGet);
        PrintWriter out = response.getWriter();
        if (isGet) {
        	String signature = request.getParameter("signature");// 微信加密签名  
            String timestamp = request.getParameter("timestamp");// 时间戳  
            String nonce = request.getParameter("nonce");// 随机数  
            String echostr = request.getParameter("echostr");//随机字符串  
            if (SignUtil.checkSignature("hfgahfga", signature, timestamp, nonce)) {  
            	LOGGER.info("Connect the weixin server is successful.");
                response.getWriter().write(echostr);  
            }else {
            	LOGGER.error("Failed to verify the signature!"); 
            }
                
                
        }else{
        	 String respMessage = "异常消息！";
             
             try {
                 respMessage = wechatService.weixinPost(request);
                 System.out.println("消息是"+respMessage);
                 out.write(respMessage);
                 LOGGER.info("The request completed successfully");
                 LOGGER.info("to weixin server "+respMessage);
             } catch (Exception e) {
                 LOGGER.error("Failed to convert the message from weixin!"); 
             }
             
        }
        

        
	}
	
	
	/*@RequestMapping(value="/",method=RequestMethod.POST)
	@ResponseBody
	public void tokenPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 返回消息给微信服务器
		PrintWriter out = response.getWriter();
		// 获取encrypt_type 消息加解密方式标识
		String encrypt_type = request.getParameter("encrypt_type");
		// 创建一个路由器
		WxMessageRouter router = new WxMessageRouter(iService);
		try {
			// 判断消息加解密方式，如果是加密模式。encrypt_type==aes
			if (encrypt_type != null && "aes".equals(encrypt_type)) {
//				String signature = request.getParameter("signature");
				String timestamp = request.getParameter("timestamp");
				String nonce = request.getParameter("nonce");
				String msg_signature = request.getParameter("msg_signature");

				// 微信服务器推送过来的加密消息是XML格式。使用WxXmlMessage中的decryptMsg()解密得到明文。
				WxXmlMessage wx = WxXmlMessage.decryptMsg(request.getInputStream(), WxConfig.getInstance(), timestamp,
						nonce, msg_signature);
				System.out.println("消息：\n " + wx.toString());
				// 添加规则；这里的规则所有消息都交给DemoMatcher处理，交给DemoInterceptor处理，交给DemoHandler处理
				// 注意！！每一个规则，必须由end()或者next()结束。不然不会生效。
				// end()是指消息进入该规则后不再进入其他规则。 而next()是指消息进入了一个规则后，如果满足其他规则也能进入，处理。
				router.rule().matcher(new DemoMatcher()).interceptor(new DemoInterceptor()).handler(new DemoHandler()).end();
				// 把消息传递给路由器进行处理，得到最后一个handler处理的结果
				WxXmlOutMessage xmlOutMsg = router.route(wx);
				if (xmlOutMsg != null) {
					// 将要返回的消息加密，返回
					out.print(WxXmlOutMessage.encryptMsg(WxConfig.getInstance(), xmlOutMsg.toXml(), timestamp, nonce));// 返回给用户。
				}
			//如果是明文模式，执行以下语句
			} else {
				// 微信服务器推送过来的是XML格式。
				WxXmlMessage wx = XStreamTransformer.fromXml(WxXmlMessage.class, request.getInputStream());
				System.out.println("消息：\n " + wx.toString());
				iService.getAccessToken();
				router.rule().matcher(new DemoMatcher()).interceptor(new DemoInterceptor()).handler(new DemoHandler()).end();
				// 把消息传递给路由器进行处理
				WxXmlOutMessage xmlOutMsg = router.route(wx);
				if (xmlOutMsg != null)
					out.print(xmlOutMsg.toXml());// 因为是明文，所以不用加密，直接返回给用户。
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}	*/

	//客户提交一键救援  公众号进行推送
	@RequestMapping(value="/createMenu")
	@ResponseBody
	public JSONObject  createMenu(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, WxErrorException{
		//显示菜单
		WxMenu menu = new WxMenu();
		//创建菜单button列表
		List<WxMenuButton> list=new ArrayList<WxMenuButton>();
		//设置VIEW类型的按钮2
		WxMenuButton btn2 = new WxMenuButton();
		btn2.setType(WxConsts.BUTTON_VIEW);
		//链接到飞机救援页面
		btn2.setUrl("http://weixin.tonghangyun.com.cn/locationTest.do");
		btn2.setName("一键救援");
		list.add(btn2);
		menu.setButton(list);
		//参数1--menu ，参数2--是否是个性化定制。如果是个性化菜单栏，需要设置MenuRule
		iService.createMenu(menu, false);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("flag", true);
		return jsonObject;
	}
	
	//删除菜单信息
	@RequestMapping(value="/delMenu")
	@ResponseBody
	public JSONObject  delMenu(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, WxErrorException{
		JSONObject jsonObject=new JSONObject();
		try {
			iService.deleteMenu();
			jsonObject.put("flag", true);
		} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  jsonObject;
	}
	
	//获取菜单信息
	@RequestMapping(value="/getMenu")
	@ResponseBody
	public WxMenuResult  getMenu(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, WxErrorException{
		WxMenuResult result=null;
		try {
			result= iService.getMenu();
			System.out.println(result.toString());
		} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//获取现有菜单配置栏信息
	@RequestMapping(value="/getMenuCurInfo")
	@ResponseBody
	public WxCurMenuInfoResult  getMenuCurInfo(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, WxErrorException{
		WxCurMenuInfoResult result=null;
		try {
			result = iService.getMenuCurInfo();
			System.out.println(result.toString());
			} catch (WxErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	//客户提交一键救援  公众号进行推送
	@RequestMapping(value="/sendMessage",method=RequestMethod.GET)
	@ResponseBody
	public JSONObject sendMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		JSONObject jsonObject=new JSONObject();
		//保存到数据库中
		int i=0;
		if(i==1){
			jsonObject.put("flag", true);
			//需要将模板消息推送给一个人
			TemplateSender sender = new  TemplateSender();
			//模板ID
			sender.setTemplate_id("OlYzsracple0EZHaeoSuhD3iYRrcm02di2fB1RZRMqA");
			//发送给这个人openid
			sender.setTouser("or2wPwm9EAvpV0osk4GU52XLQwMI");
			//url不用指定
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
		}
		else{
			jsonObject.put("flag", false);
		}
		return jsonObject;
	}
	
	//测试发送消息
	//客户提交一键救援  公众号进行推送
	@RequestMapping(value="/testSendMessage",method=RequestMethod.GET)
	@ResponseBody
	public String testSendMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		//需要将模板消息推送给一个人
		TemplateSender sender = new  TemplateSender();
		//模板ID
		sender.setTemplate_id("OlYzsracple0EZHaeoSuhD3iYRrcm02di2fB1RZRMqA");
		//发送给这个人openid
		sender.setTouser("or2wPwm9EAvpV0osk4GU52XLQwMI");
		//url不用指定
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
		TemplateSenderResult result=null;
		try {
			result = iService.templateSend(sender);
			System.out.println(result.toString());
		} catch (WxErrorException e) {
			// TODO Auto-generated catch block+
			e.printStackTrace();
		}
		return result.toString();
	}

	
	//测试急救信息插入
	@RequestMapping("/insertApplyInfo")
	@ResponseBody
	public JSONObject testInsert(){
		/*applyInfo.setContactman("辛源才");
		applyInfo.setContactphone("18810946108");
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		applyInfo.setCreatetime(simpleDateFormat.format(new Date()));
		applyInfo.setDescipbe("病人生病了");
		applyInfo.setName("哈哈");
		applyInfo.setPlace("星火路4号");
		applyInfo.setTitude("fasd");
		int i=applyInfoService.insert(applyInfo);*/
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("result", 0);
		return jsonObject;
	}
	
	//跳转都一键救援的页面
	@RequestMapping("/planeHelp")
	public ModelAndView planeHelp(HttpServletRequest request, HttpServletResponse response){
		return new ModelAndView("planeHelp");
	}
	
	//返回locationTest 测试
	@RequestMapping("/locationTest")
	public ModelAndView locationTest(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("locationTest");
	}
		
	//配置JSSDK接口
	@RequestMapping("/createJsapiConfig")
	@ResponseBody
	public WxJsapiConfig createJsapiConfig(){
	   List<String> jsApiList = new ArrayList<String>();
	    //需要用到哪些JS SDK API 就设置哪些
	    jsApiList.add("chooseImage");//拍照或从手机相册中选图接口
	    jsApiList.add("onMenuShareQZone");//获取“分享到QQ空间”按钮点击状态及自定义分享内容接口
	    jsApiList.add("openLocation");//打开微信内置地图的接口
	    jsApiList.add("getLocation");//获取地理位置的接口
	    WxJsapiConfig config=null;
	    try {
	        //把config返回到前端进行js调用即可。
	    	config = iService.createJsapiConfig("http://weixin.tonghangyun.com.cn/locationTest.do", jsApiList);
	        System.out.println(config.toString());
	    } catch (WxErrorException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return config;
	}
	
	//配置JSSDK接口
	@RequestMapping("/createlocationConfig")
	@ResponseBody
	public WxJsapiConfig createlocationConfig(String url){
	   List<String> jsApiList = new ArrayList<String>();
	    //需要用到哪些JS SDK API 就设置哪些
	    jsApiList.add("chooseImage");//拍照或从手机相册中选图接口
	    jsApiList.add("onMenuShareQZone");//获取“分享到QQ空间”按钮点击状态及自定义分享内容接口
	    jsApiList.add("openLocation");//打开微信内置地图的接口
	    jsApiList.add("getLocation");//获取地理位置的接口
	    WxJsapiConfig config=null;
	    try {
	        //把config返回到前端进行js调用即可。
	    	System.err.println("打开微信内置地图"+url);
	    	config = iService.createJsapiConfig(url, jsApiList);
	        System.out.println(config.toString());
	    } catch (WxErrorException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return config;
	}
	
	
	//提交申请并进行推送
	@RequestMapping("/sendTrailMessage")
	@ResponseBody
	public  TemplateSenderResult sendMessage(){
		//插入申请信息
		int i=0;
		//需要将模板消息推送给一个人
		TemplateSender sender = new  TemplateSender();
		//模板ID
		//宋晗的ID or2wPwhVwmvFynYWYgYtRdjHfkUk
		/*sender.setTemplate_id("77v3XSuzCOURv68YhSxBPCcwMdupp4In-wzqPmCanQw");
		//发送给这个人openid
		sender.setTouser("or2wPwhVwmvFynYWYgYtRdjHfkUk");*/
		//模板点击事件的跳转URL
		//经纬度信息
		String  titude="";
		String url="local";
		sender.setUrl(url);
		//模板消息体
		//需要将模板消息推送给一个人
		TemplateSender sender2 = new  TemplateSender();
		//模板ID
		//辛源才的ID
		sender2.setTemplate_id("77v3XSuzCOURv68YhSxBPCcwMdupp4In-wzqPmCanQw");
		//发送给辛源才的ID
		sender2.setTouser("or2wPwm9EAvpV0osk4GU52XLQwMI");
		//模板点击事件的跳转URL并且把地理位置
		sender2.setUrl(url);
		JSONObject data=new JSONObject();
		JSONObject first=new JSONObject();
		JSONObject time=new  JSONObject();
		JSONObject content=new JSONObject();
		JSONObject remark=new JSONObject();
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		first.put("value", "一键救援"); 
		first.put("color", "#173177");
		data.put("first", first);
		time.put("value", simpleDateFormat.format(new Date()));
		time.put("color","#173177");
		data.put("keyword1", time);
		data.put("keyword2", content);
		remark.put("value", "请尽快联系！");
		remark.put("color", "#173177");
		data.put("remark", remark);
		content.put("value", "联系人：,联系方式:,具体位置:,具体地点:");
		content.put("color", "#173177");
		sender.setData(data);
		sender2.setData(data);
		TemplateSenderResult result=null;
		TemplateSenderResult result2=null;
		try {
			result = iService.templateSend(sender);
			result2= iService.templateSend(sender2);
			System.out.println(result.toString());
			System.out.println(result2.toString());
		} catch (WxErrorException e) {
			
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//打开微信的内置地图并将地址等参数传递过去
	@RequestMapping(value="/openLocation",method=RequestMethod.GET)
	public ModelAndView openLocation(String latitude,String longitude,String place){
		ModelAndView modelAndView= new ModelAndView("openLocation");
		modelAndView.addObject("latitude",latitude);
		modelAndView.addObject("longitude",longitude);
		modelAndView.addObject("place",place);
		return modelAndView;
	}
	
	//测试字符串信息
	public static void main(String[] args) {
		String titude="116°17′29.1804″,39°50′24.9288″";
		System.out.println(titude.split(",")[0]);
	}
	
	
    
	
}

