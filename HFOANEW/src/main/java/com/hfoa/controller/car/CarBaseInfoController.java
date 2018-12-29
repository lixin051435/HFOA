package com.hfoa.controller.car;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.entity.car.BCarbaseinfoDTO;
import com.hfoa.entity.common.BNotice;
import com.hfoa.service.car.BCarbaseinfoService;
import com.hfoa.service.car.BCargasinfoService;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.util.TimeUtil;
/**
 * 
 * @author wzx
 *公共车基本信息
 */
@Controller
@RequestMapping("CommonCar")
public class CarBaseInfoController {
	@Autowired
	private BCarbaseinfoService bCarbaseinfoService;
	@Autowired
    private BCargasinfoService bCargasinfoService;
	@Autowired
    private DictManage dictManage;
	//获取车辆信息
	@RequestMapping("getAllCar")
	@ResponseBody
	public Object getAllCar(){
		List<BCarbaseinfoDTO> carInfoList=new ArrayList<BCarbaseinfoDTO>();
		List<BCarbaseinfo> carList=bCarbaseinfoService.getAllCar();
		for (BCarbaseinfo bCarbaseinfo : carList) {
			BCarbaseinfoDTO car=new BCarbaseinfoDTO();
				car.setCarbuytime(bCarbaseinfo.getCarbuytime());
				car.setCarcode(bCarbaseinfo.getCarcode());
				car.setCardetailinfo(bCarbaseinfo.getCardetailinfo());
				car.setCardistance(bCarbaseinfo.getCardistance());
				car.setCardvale(bCarbaseinfo.getCardvale());
				car.setCarinsuranceinfo(bCarbaseinfo.getCarinsuranceinfo());
				car.setCarinsuranceinfo1(bCarbaseinfo.getCarinsuranceinfo1());
				car.setCarinsuranceinfodetal(bCarbaseinfo.getCarinsuranceinfodetal());
				car.setCarLable(bCarbaseinfo.getCarLable());
				car.setCarstate(bCarbaseinfo.getCarstate());
				car.setCarunit(bCarbaseinfo.getCarunit());
				car.setId(bCarbaseinfo.getId());
				car.setOthers(bCarbaseinfo.getOthers());
				car.setCarNum(bCarbaseinfo.getCarnum());
				car.setPeasonnum(bCarbaseinfo.getPeasonnum());
				car.setSuspendday(bCarbaseinfo.getSuspendday());
				car.setTypenum(bCarbaseinfo.getCarnum()+"   "+bCarbaseinfo.getCartype());
				carInfoList.add(car);
		}
		return carInfoList;
	}
	//公车基本信息
	//Web-显示公车相关信息
	@RequestMapping("/showAllCarBaseInfo")
	@ResponseBody
	public Object carDisplay(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BCarbaseinfo> list = new ArrayList<BCarbaseinfo>();
		list = bCarbaseinfoService.carDisplayByPage(start, number);
		for (BCarbaseinfo bCarbaseinfo : list) {
			bCarbaseinfo.setForUpload(bCarbaseinfo.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bCarbaseinfoService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//添加公车信息
	@RequestMapping("/insertCommonCar")
	@ResponseBody
	public Object insertCommonCar(BCarbaseinfo car){
		return bCarbaseinfoService.insert(car);
	}
	//修改公车信息
	@RequestMapping("/updateCommonCar")
	@ResponseBody
	public Object updateCommonCar(BCarbaseinfo car){
		String text=car.getCarstate();
		String parentId=Constants.COMMON_CAR;
		String info=dictManage.getByTextAndParentId(text,parentId);
		car.setCarLable(Integer.parseInt(info));
		return bCarbaseinfoService.update(car);
	}
	//上传图片
	@RequestMapping(value = "/uploadFile")
	@ResponseBody 
	public Object uploadFile(HttpServletRequest request, MultipartFile upload,int carId) {  
		String originalName = upload.getOriginalFilename(); 
		String originalFileName = originalName.substring(0,originalName.lastIndexOf("."));
		String newFileName = originalFileName+
				originalName.substring(originalName.lastIndexOf("."),originalName.length());
		String path = request.getSession().getServletContext().getRealPath("/images");
		String newFile="CommonCar";
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
//		String newPath= "http://192.168.4.122:9988/HFOANEW/images/CommonCar/"+newFileName;
		String newPath= "https://gongche.hfga.com.cn/HFOANEW/images/CommonCar/"+newFileName;
		BCarbaseinfo car=bCarbaseinfoService.getCarInfoById(carId);
//		if(StringUtils.isNotBlank(notice.getImgurl())){
//			File file = new File(notice.getImgurl());
//			if(file.exists()){//删除文件
//				file.delete();
//			}
//		}
		String lastFile;
		String p=System.getProperty("user.dir");
		if(car.getCarUrl()!=null&&!car.getCarUrl().equals("null")&&!"".equals(car.getCarUrl())){
			try {
				String projectPath=URLDecoder.decode(p, "UTF-8").replace("\\", "/");
				lastFile="images/CommonCar/"+car.getCarUrl().split("/")[car.getCarUrl().split("/").length-1];
				path=projectPath+"/src/main/webapp/"+lastFile;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			File file = new File(path);
			if(file.exists()){//删除文件
				file.delete();
			}
		}
		car.setCarUrl(newPath);
		Integer update = bCarbaseinfoService.update(car);
		return update;
	}
	//在前台显示车辆基本信息（转换）
	@RequestMapping("/showCarBaseInfo")
	@ResponseBody
	public ModelAndView showCarBaseInfo(BCarbaseinfo car){
		ModelAndView result=new ModelAndView("commonCar/carBaseInfo");
		return result;
	}
}
