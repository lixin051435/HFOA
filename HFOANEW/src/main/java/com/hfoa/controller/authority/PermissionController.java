package com.hfoa.controller.authority;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.permission.BPermissionService;
/**
 * 
 * @author wzx
 * 权限管理
 */
@Controller
@RequestMapping("permission")
public class PermissionController {
	@Autowired
	private BPermissionService bPermissionService;
	@Autowired
	private DictManage dictManage;
	//权限信息
	//Web-显示权限相关信息
	@RequestMapping("/showAllPermission")
	@ResponseBody
	public Object showAllPermission(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BPermissionEntity> list = new ArrayList<BPermissionEntity>();
		list = bPermissionService.permissionDisplayByPage(start, number);
		for (BPermissionEntity bPermissionEntity : list) {
			if("".equals(bPermissionEntity.getAvailable()+"")||bPermissionEntity.getAvailable()==null){
				bPermissionEntity.setShowAvailable("");
			}else if(bPermissionEntity.getAvailable()==0){
				bPermissionEntity.setShowAvailable("否");
			}else if(bPermissionEntity.getAvailable()==1){
				bPermissionEntity.setShowAvailable("是");
			}
			String info=bPermissionEntity.getUrlclass();
			String parentId=Constants.PERMISSION_URL;
			String text=dictManage.getByParentAndInfo(info,parentId);
			bPermissionEntity.setShowUrlClass(text);
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bPermissionService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//添加权限信息
	@RequestMapping("/insertPermission")
	@ResponseBody
	public Object insertPermission(BPermissionEntity permission){
		bPermissionService.insert(permission);
		return permission.getId();
	}
	//修改权限信息
	@RequestMapping("/updatePermission")
	@ResponseBody
	public Object updatePermission(BPermissionEntity permission){
		String text=permission.getShowUrlClass();
		String parentId=Constants.PERMISSION_URL;
		String info=dictManage.getByTextAndParentId(text,parentId);
		permission.setUrlclass(info);
		return bPermissionService.update(permission);
	}
	//删除权限
	@RequestMapping("/deletePermission")
	@ResponseBody
	public Object deletePermission(int id){
		List<BPermissionEntity> child=bPermissionService.getByNodeType(id+"");
		BPermissionEntity byId = bPermissionService.getById(id);
		String lastFile;
		String p=System.getProperty("user.dir");
		String path="";
		if(byId.getImageurl()!=null&&!byId.getImageurl().equals("null")&&!"".equals(byId.getImageurl())){
			try {
				String projectPath=URLDecoder.decode(p, "UTF-8").replace("\\", "/");
				lastFile="images/WeiXin/"+byId.getImageurl().split("/")[byId.getImageurl().split("/").length-1];
				path=projectPath+"/src/main/webapp/"+lastFile;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			File file = new File(path);
			if(file.exists()){//删除文件
				file.delete();
			}
		}
		int deleteById = bPermissionService.deleteById(id);
		if(deleteById!=0){
			bPermissionService.deleteMiddleRole(id);
		}
		if(child!=null&&deleteById!=0){
			for (BPermissionEntity bPermissionEntity : child) {
				bPermissionService.deleteMiddleRole(bPermissionEntity.getId());
			}
		}
		return deleteById;
	}
	//权限的模糊查询
	@RequestMapping("/searchPermission")
	@ResponseBody
	public Object searchPermission(HttpServletRequest request,String title){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BPermissionEntity> list = new ArrayList<BPermissionEntity>();
		list = bPermissionService.permissionVagueByPage(start, number,title);
		for (BPermissionEntity bPermissionEntity : list) {
			if("".equals(bPermissionEntity.getAvailable()+"")||bPermissionEntity.getAvailable()==null){
				bPermissionEntity.setShowAvailable("");
			}else if(bPermissionEntity.getAvailable()==0){
				bPermissionEntity.setShowAvailable("否");
			}else if(bPermissionEntity.getAvailable()==1){
				bPermissionEntity.setShowAvailable("是");
			}
			String info=bPermissionEntity.getUrlclass();
			String parentId=Constants.PERMISSION_URL;
			String text=dictManage.getByParentAndInfo(info,parentId);
			bPermissionEntity.setShowUrlClass(text);
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bPermissionService.getVagueCount(title);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	
	//根据根权限获取所有子节点
	  @RequestMapping(value = "/getByNodeType")
	  @ResponseBody
	  public Object getByNodeType(String id) {
	    String parentId;
	    if ("".equals(id) || id == null) {
	    	//根节点
	      parentId = "1";
	    } else {
	      parentId = id;
	    }
	    return bPermissionService.getByNodeType(parentId);
	  }
	//根据角色id获取权限
	  @RequestMapping(value = "/getPermissionByRole")
	  @ResponseBody
	  public List<Integer> getPermissionByRole(int roleId) {
	    return bPermissionService.getPermissionByRole(roleId);
	  }
	  //上传图片
	  @RequestMapping(value = "/uploadFile")
	  @ResponseBody 
		public Object uploadFile(HttpServletRequest request, MultipartFile icon,int pId) {  
			String originalName = icon.getOriginalFilename(); 
			String originalFileName = originalName.substring(0,originalName.lastIndexOf("."));
			String newFileName = originalFileName+
					originalName.substring(originalName.lastIndexOf("."),originalName.length());
			String path = request.getSession().getServletContext().getRealPath("/images");
			String newFile="WeiXin";
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
				icon.transferTo(targetFile);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
//			String newPath= "http://192.168.4.122:9988/HFOANEW/images/WeiXin/"+newFileName;
			String newPath= "https://gongche.hfga.com.cn/HFOANEW/images/WeiXin/"+newFileName;
			BPermissionEntity byId = bPermissionService.getById(pId);
			String lastFile;
			String p=System.getProperty("user.dir");
			if(byId.getImageurl()!=null&&!byId.getImageurl().equals("null")&&!"".equals(byId.getImageurl())){
				try {
					String projectPath=URLDecoder.decode(p, "UTF-8").replace("\\", "/");
					lastFile="images/WeiXin/"+byId.getImageurl().split("/")[byId.getImageurl().split("/").length-1];
					path=projectPath+"/src/main/webapp/"+lastFile;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				File file = new File(path);
				if(file.exists()){//删除文件
					file.delete();
				}
			}
			byId.setImageurl(newPath);
			int update = bPermissionService.update(byId);
			return update;
		}
	//在前台显示权限信息（转换）
	@RequestMapping("/showPermissionInfo")
	@ResponseBody
	public ModelAndView showPermissionInfo(){
		ModelAndView result=new ModelAndView("authority/permission");
		return result;
	}
}
