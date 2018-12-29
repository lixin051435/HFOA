package com.hfoa.controller.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.common.BMessagenotice;
import com.hfoa.entity.common.BNotice;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.service.common.BMessagenoticeService;
import com.hfoa.service.common.BNoticeService;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.TimeUtil;

/**
 * @author wzx
 * 微信公告
 */
@Controller
@RequestMapping("notice")
public class NoticeController {

	@Autowired
	private BNoticeService bNoticeService;
	@Autowired
	private BMessagenoticeService bMessagenoticeService;
	//微信公告信息
	//Web-显示微信公告相关信息
	@RequestMapping("/getAllNoticeByPage")
	@ResponseBody
	public Object getAllNoticeByPage(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BNotice> list = new ArrayList<BNotice>();
		list = bNoticeService.noticeDisplayByPage(start, number);
	
		for (BNotice bNotice : list) {
			bNotice.setForUpload(bNotice.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bNoticeService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	} 
	//添加公告
	@RequestMapping("/insertNotice")
	@ResponseBody
	public Object insertNotice(BNotice bNotice){
		return bNoticeService.insert(bNotice);
	}
	//修改公告信息
	@RequestMapping("/updateNotice")
	@ResponseBody
	public Object updateNotice(BNotice bNotice){
		return bNoticeService.update(bNotice);
	}
	@RequestMapping(value = "/uploadFile")
	@ResponseBody 
	public Object uploadFile(HttpServletRequest request, MultipartFile upload,int noticeid) {  
		String originalName = upload.getOriginalFilename(); 
		String originalFileName = originalName.substring(0,originalName.lastIndexOf("."));
		String newFileName = originalFileName+
				originalName.substring(originalName.lastIndexOf("."),originalName.length());
		String path = request.getSession().getServletContext().getRealPath("/upload");
		System.out.println("path是"+path);
		
		String newFile="微信公告";
//				new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		path+="/"+newFile+"/";
		File dir=new File(path);
		if(!dir.exists()&&!dir.isDirectory()){//如果文件不存在则创建文件
			dir.mkdirs();
		}
		File targetFile = new File(path,newFileName);
		if(!targetFile.exists()){
			targetFile.mkdirs();
		}
		//保存
		try {
			upload.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
//		String newPath= "http://192.168.4.122:9988/HFOANEW/upload/微信公告/"+newFileName;
		String newPath= "https://gongche.hfga.com.cn/HFOANEW/upload/微信公告/"+newFileName;
		BNotice notice=bNoticeService.selectById(noticeid);
//		if(StringUtils.isNotBlank(notice.getImgurl())){
//			File file = new File(notice.getImgurl());
//			if(file.exists()){//删除文件
//				file.delete();
//			}
//		}
		String lastFile;
		String p=System.getProperty("user.dir");
		if(notice.getImgurl()!=null&&!notice.getImgurl().equals("null")&&!"".equals(notice.getImgurl())){
			try {
				String projectPath=URLDecoder.decode(p, "UTF-8").replace("\\", "/");
				lastFile="upload/微信公告/"+notice.getImgurl().split("/")[notice.getImgurl().split("/").length-1];
				path=projectPath+"/src/main/webapp/"+lastFile;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			File file = new File(path);
			if(file.exists()){//删除文件
				file.delete();
			}
		}
		notice.setImgurl(newPath);
		Integer update = bNoticeService.update(notice);
		return update;
	}
	//删除公告信息
	@RequestMapping("/deleteNotice")
	@ResponseBody
	public Object deleteNotice(int id){
		BNotice selectById = bNoticeService.selectById(id);
		Integer result=null;
		if(StringUtils.isNotBlank(selectById.getImgurl())){
			String realPath=System.getProperty("user.dir")+"/src/main/webapp/"+selectById.getImgurl();
			File file = new File(realPath);
			if(file.exists()){//删除文件
				file.delete();
			}
			result=bNoticeService.deleteById(id);
		}
		return result;
	}
	//公告的模糊查询
	@RequestMapping("/searchNotice")
	@ResponseBody
	public Object searchNotice(HttpServletRequest request,String title){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BNotice> list = new ArrayList<BNotice>();
		list = bNoticeService.noticeVagueByPage(start, number,title);
		for (BNotice notice : list) {
			notice.setForUpload(notice.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bNoticeService.getVagueCount(title);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//修改部门角色中间表
//	@RequestMapping("/updateMiddleDepartment")
//	@ResponseBody
//	public Object updateMiddleDepartment(String roleids,int departmentid){
//		String[] split = roleids.split(",");
//		bRoleService.deleteMiddleDepartment(departmentid);
//		int result=0;
//		for(int i=0;i<split.length;i++){
//			result=bDepartmentService.insertMiddleRole(departmentid,Integer.parseInt(split[i]));
//		}
//		return result;
//	}
	//微信公告前台显示
	@RequestMapping("/showImage")
	@ResponseBody
	public Object showImage(){
		List<BNotice> list = new ArrayList<BNotice>();
		list = bNoticeService.getAvailable();
		return list;
	} 
	//在前台显示公告信息（转换）
	@RequestMapping("/showNoticeInfo")
	@ResponseBody
	public ModelAndView showNoticeInfo(){
		ModelAndView result=new ModelAndView("common/notice");
		return result;
	} 
	//微信消息公告信息
	//Web-显示微信消息公告相关信息
	@RequestMapping("/getAllMessageNoticeByPage")
	@ResponseBody
	public Object getAllMessageNoticeByPage(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BMessagenotice> list = new ArrayList<BMessagenotice>();
		list = bMessagenoticeService.messageNoticeDisplayByPage(start, number);
	
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bMessagenoticeService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	} 
	//在前台显示消息公告信息（转换）
	@RequestMapping("/showMessageNoticeInfo")
	@ResponseBody
	public ModelAndView showMessageNoticeInfo(){
		ModelAndView result=new ModelAndView("common/messageNotice");
		return result;
	} 
	//添加消息公告
	@RequestMapping("/insertMessagenotice")
	@ResponseBody
	public Object insertMessagenotice(BMessagenotice messageNotice){
		List<BMessagenotice> messageList=bMessagenoticeService.getAll();
		for (BMessagenotice bMessagenotice : messageList) {
			if(!messageNotice.getMaintitle().equals(bMessagenotice.getMaintitle())){
				bMessagenotice.setMaintitle(messageNotice.getMaintitle());
				bMessagenoticeService.update(bMessagenotice);
			}
		}
		Date now=new Date();
		String time=TimeUtil.fromDateDateToString(now);
		messageNotice.setUsertime(time);
		return bMessagenoticeService.insert(messageNotice);
	}
	//修改消息公告信息
	@RequestMapping("/updateMessagenotice")
	@ResponseBody
	public Object updateMessagenotice(BMessagenotice messageNotice){
		BMessagenotice message=bMessagenoticeService.getById(messageNotice.getId());
		message.setId(messageNotice.getId());
		message.setContent(messageNotice.getContent());
		message.setContenttitle(messageNotice.getContenttitle());
		message.setMaintitle(messageNotice.getMaintitle());
		message.setUsertime(messageNotice.getUsertime());
		List<BMessagenotice> messageList=bMessagenoticeService.getAll();
		for (BMessagenotice bMessagenotice : messageList) {
			if(!messageNotice.getMaintitle().equals(bMessagenotice.getMaintitle())){
				bMessagenotice.setMaintitle(messageNotice.getMaintitle());
				bMessagenoticeService.update(bMessagenotice);
			}
		}
		return bMessagenoticeService.update(message);
	}
	//删除消息公告信息
	@RequestMapping("/deleteMessagenotice")
	@ResponseBody
	public Object deleteMessagenotice(int id){
		return bMessagenoticeService.deleteById(id);
	}
	//传给前台公告
	@RequestMapping("/showMessage")
	@ResponseBody
	public Object showMessage(){
		List<BMessagenotice> messageList=bMessagenoticeService.getAll();
		String message=messageList.get(0).getMaintitle();
		return message;
	}
	//获取所有可用公告的数量
	@RequestMapping("/countAvailable")
	@ResponseBody
	public Object countAvailable(){
		int countAvailable=bNoticeService.countAvailable();
		return countAvailable;
	}
	//获取已经添加的消息公告的主标题
	@RequestMapping("/getMainTitle")
	@ResponseBody
	public Object getMainTitle(){
		String title=null;
		List<BMessagenotice> messageList=bMessagenoticeService.getAll();
		if(messageList.size()>0){
			title=messageList.get(0).getMaintitle();
		}
		return title;
	}
	
	//查看公告详情
	@RequestMapping("/showMessageDetail")
	@ResponseBody
	public Object showMessageDetail(String mainTitle){
		List<BMessagenotice> messageList=bMessagenoticeService.getByMainTitle(mainTitle);
		return messageList;
	}
}
