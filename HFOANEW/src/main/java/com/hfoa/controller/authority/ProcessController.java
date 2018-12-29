package com.hfoa.controller.authority;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.permission.BProcess;
import com.hfoa.service.permission.BProcessService;

/**
 * 
 * @author wzx
 * 流程管理
 */
@Controller
@RequestMapping("process")
public class ProcessController {

	@Autowired
	private BProcessService bProcessService;
	//流程信息
	//Web-显示流程相关信息
	@RequestMapping("/showAllProcess")
	@ResponseBody
	public Object showAllProcess(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BProcess> list = new ArrayList<BProcess>();
		list = bProcessService.processDisplayByPage(start, number);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bProcessService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//获取流程模块
	@RequestMapping("/processModel")
	@ResponseBody
	public List<BProcess> processModel(HttpServletRequest request){
		List<BProcess> list =bProcessService.getByParentId(0);
		return list;
	}
	//查询流程信息
	@RequestMapping("searchProcess")
	@ResponseBody
	public Object searchProcess(HttpServletRequest request,Integer parentid,String processname) throws ParseException{
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		String result="select * from b_process where 1=1";//sql语句
		String countResult="select count(*) from b_process where 1=1";
		if(parentid!=null){
			result+=" and parentid ="+parentid+" or id="+parentid;
			countResult+=" and parentid ="+parentid+" or id="+parentid;
		}
		if(processname!=null && !"".equals(processname)){
			result+=" and processname ='"+processname+"'";
			countResult+=" and processname ='"+processname+"'";
		}
		result+=" limit "+start+","+number;
		List<BProcess> processinfoList=bProcessService.getBySql(result);
		int count=bProcessService.getCountBysql(countResult);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		jsonMap.put("total", count);// total存放总记录数
		jsonMap.put("rows", processinfoList);// rows键，存放每页记录list
		return jsonMap;
	}
	//导出excel
	@RequestMapping("/exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, HttpServletResponse response, String number) {
		String[] nlist = number.split(","); // 获得传递过来的number列表
		// 获取导出文件夹
		String path = request.getSession().getServletContext().getRealPath("/");
		// 生成导出的文件名
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String date = matter.format(dt);
		String fileName = "流程信息" + date + ".xlsx";
		String filePath = path + "/" + fileName;
		int flag = bProcessService.export(nlist, filePath);
		if (flag != 1) {
			return;
		}
		try {
			// 根据不同的浏览器处理下载文件名乱码问题
			String userAgent = request.getHeader("User-Agent");
			// 针对IE或者是以ie为内核的浏览器
			if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
				// 非IE浏览器的处理：
				fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			// 获取一个流
			InputStream in = new FileInputStream(new File(filePath));
			// 设置下载的响应头
			response.setHeader("content-disposition", "attachment;fileName=" + fileName);
			response.setCharacterEncoding("UTF-8");
			// 获取response字节流
			OutputStream out = response.getOutputStream();
			byte[] b = new byte[1024];
			int len = -1;
			while ((len = in.read(b)) != -1) {
				out.write(b, 0, len);
			}
			// 关闭
			out.close();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	//根据根权限获取所有子节点
	  @RequestMapping(value = "/getByNodeType")
	  @ResponseBody
	  public Object getByNodeType(String id) {
	    String parentId;
	    if ("".equals(id) || id == null) {
	    	//根节点
	      parentId = "0";
	    } else {
	      parentId = id;
	    }
	    return bProcessService.getByNodeType(parentId);
	  }
	//根据角色id获取流程
	  @RequestMapping(value = "/getProcessByRole")
	  @ResponseBody
	  public List<Integer> getProcessByRole(int roleId) {
	    return bProcessService.getProcessByRole(roleId);
	  }
	  
	//根据角色id获取流程id集合
	  @RequestMapping(value = "/getProcessByRoleId")
	  @ResponseBody
	  public List<Integer> getProcessByRoleId(int roleId) {
	    return bProcessService.getProcessByRoleId(roleId);
	  }
	//在前台显示权限信息（转换）
	@RequestMapping("/getAllProcessInfo")
	@ResponseBody
	public ModelAndView showPermissionInfo(){
		ModelAndView result=new ModelAndView("authority/process");
		return result;
	}
}
