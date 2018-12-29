package com.hfoa.controller.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import cn.com.hfga.entity.common.TravelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.entity.common.DictEntity;
import com.hfoa.entity.common.TravelEntity;
import com.hfoa.entity.common.WineEntity;
import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.printing.BGzkind;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.service.car.BCarbaseinfoService;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.permission.BPermissionService;
import com.hfoa.service.printing.BGzkindService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.TimeUtil;

import oracle.net.aso.d;

//import cn.com.hfga.entity.common.WineEntity;
//import cn.com.hfga.util.Constants;

/**
 * @author wzx
 * 数据字典
 */
@Controller
@RequestMapping("/dictionary")
public class DictController {

  @Autowired
  private DictManage dictManage;
  @Autowired
  private BCarbaseinfoService bCarbaseinfoService;
  @Autowired
  private BPermissionService bPermissionService;
  
  @Autowired
  private DynamicGetRoleUtil dynamicGetRoleUtil;
  
  @Autowired
  private USerService uSerService;
  @Autowired
  private BGzkindService bGzkindService;
  
  //保存字典
  @RequestMapping(value = "/saveDict")
  @ResponseBody
  public int saveDict(String text,String info,String parentId) {
	  DictEntity dict=new DictEntity();
	  String id = new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date() );
	  dict.setText(text);
	  dict.setId(id);
	  dict.setInfo(info);
	  dict.setParentId(parentId);
	  BGzkind gz=new BGzkind();
	  if("20170718172505854".equals(parentId)){
		  gz.setGrade(Integer.parseInt(info));
		  gz.setGzkind(text);
		  gz.setLable(id);
		  bGzkindService.save(gz);
	  }
    return dictManage.insert(dict);
  }

  //根据父节点获取所有子节点
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
    return dictManage.getByNodeType(parentId);
  }

  //根据节点获取节点信息
  @RequestMapping(value = "/getNodeInfo")
  @ResponseBody
  public DictEntity getNodeInfo(String id) {
    return dictManage.getNodeInfo(id);
  }
  //根据节点获取菜单信息(暂停)
  @RequestMapping(value = "/getMenuByNodeType")
  @ResponseBody
  public Object getMenuByNodeType(String id, String userId) {
    String parentId;
    if ("".equals(id) || id == null) {
      parentId = "100";
    } else {
      parentId = id;
    }
    return dictManage.getMenuByNodeType(parentId, userId);
  }

  //删除字典
  @RequestMapping(value = "/deleteDict")
  @ResponseBody
  public Object deleteDict(String id) {
    List<DictEntity> list = dictManage.getByNodeType(id);
//    if (list.size() > 0) {
//    	//父级菜单
//      return dictManage.deleteAllModule(id);
//    }else{
//    	//子级菜单
//    	return dictManage.delete(id);
//    }
    if (list.size() > 0) {
        return 0;
      }
    List<BGzkind> allGzKind = bGzkindService.getAllGzKind();
	  for (BGzkind bGzkind : allGzKind) {
		  if(bGzkind.getLable()!=null&&id.equals(bGzkind.getLable())){
			  bGzkindService.deleteById(bGzkind.getId());
		  }
	  }
     return dictManage.delete(id);
  }

  //修改字典
  @RequestMapping(value = "/updateDict")
  @ResponseBody
  public int updateDict(DictEntity dict) {
	  List<BGzkind> allGzKind = bGzkindService.getAllGzKind();
	  for (BGzkind bGzkind : allGzKind) {
		if(dict.getId().equals(bGzkind.getLable())){
			bGzkind.setGrade(Integer.parseInt(dict.getInfo()));
			bGzkind.setGzkind(dict.getText());
			bGzkind.setLable(dict.getId());
			bGzkindService.update(bGzkind);
		}
	}
    return dictManage.update(dict);
  }

  //修改字典--顺带修改相关表内容
  @RequestMapping(value = "/updateDictAndTable")
  @ResponseBody
  public int updateDictAndTable(DictEntity dict) {
	  //根据id获取之前的字典数据
	  DictEntity nodeInfo = getNodeInfo(dict.getId());
	  if(nodeInfo.getParentId().equals(Constants.COMMON_CAR)){//公车信息
		  List<BCarbaseinfo> carList=bCarbaseinfoService.getAllCar();
		  for (BCarbaseinfo bCarbaseinfo : carList) {
			if(bCarbaseinfo.getCarLable()==Integer.parseInt(nodeInfo.getInfo())){
				bCarbaseinfo.setCarstate(dict.getText());
				bCarbaseinfo.setCarLable(Integer.parseInt(dict.getInfo()));
				bCarbaseinfoService.update(bCarbaseinfo);
			}
		}
	  }else if(nodeInfo.getParentId().equals(Constants.PERMISSION_URL)){
		  List<BPermissionEntity> perList=bPermissionService.getAllPermission();
		  for (BPermissionEntity bPermissionEntity : perList) {
			if(bPermissionEntity.getUrlclass().equals(nodeInfo.getInfo())){
				bPermissionEntity.setUrlclass(nodeInfo.getInfo());
				bPermissionService.update(bPermissionEntity);
			}
		}
	  }
    return dictManage.update(dict);
  }
  //根据业务类型查找字典
  @RequestMapping(value = "/selectDict")
  @ResponseBody
  public Object getList(String type) {

    if ("local".equals(type)) {

      type = Constants.COMMON_LOCATIONS;//常用地点

    } else if ("gz".equals(type)) {

      type = Constants.GZ_TYPE;//公章类型

    } else if ("bt".equals(type)) {

      type = Constants.HDBTJG;//核定补贴价格

    } else if ("wine".equals(type)) {

      type = Constants.WINE_TYPE;//酒水类型

    }else if ("zd".equals(type)) {

      type = Constants.ZD_TYPE;//招待类型

    }else if ("kp".equals(type)) {

      type = Constants.KP_TYPE;//开票类型

    }else if ("travel".equals(type)) {

     type = Constants.TRAVEL_TYPE;//出行方式

    }else if ("carinfo".equals(type)) {

     type = Constants.COMMON_CAR;//公车信息

    }else if ("url".equals(type)) {

     type = Constants.PERMISSION_URL;//权限链接

    }

    return dictManage.getByNodeType(type);

  }

  //获取公车信息
	@RequestMapping(value = "/selectCommonCar")
	@ResponseBody
	public Object getCommonCar() {
	  List<DictEntity> dict = dictManage.getByNodeType(Constants.COMMON_CAR);
	  List<BCarbaseinfo> ls = new ArrayList<BCarbaseinfo>();
	  for (DictEntity d : dict) {
		  BCarbaseinfo carinfo = new BCarbaseinfo();
		  carinfo.setCarLable(Integer.parseInt(d.getInfo()));
		  carinfo.setCarstate(d.getText());
	      ls.add(carinfo);
	  }
	  return ls;
	}
  //查找酒水
  @RequestMapping(value = "/selectWine")
  @ResponseBody
  public Object getWine() {
    List<DictEntity> dict = dictManage.getByNodeType(Constants.WINE_TYPE);
    List<WineEntity> ls = new ArrayList<WineEntity>();
    for (DictEntity d : dict) {
      WineEntity wine = new WineEntity();
      wine.setDict(d);
      wine.setListDict(dictManage.getByNodeType(d.getId()));
      ls.add(wine);
    }
    
    
    
    
    return ls;
 }

  /*public Object getzdType(){
	  List<DictEntity> dict = dictManage.getByNodeType(Constants.)
  }
  */
  
  //出行方式
  @RequestMapping(value = "/selectTravel")
  @ResponseBody
  public Object getTravel(String openId) {
    List<DictEntity> dict = dictManage.getByNodeType(Constants.TRAVEL_TYPE);
    List<TravelEntity> ls = new ArrayList<TravelEntity>();
    List<DictEntity> dictEntities = new ArrayList<DictEntity>();
    for(DictEntity d : dict) {
      TravelEntity travel = new TravelEntity();
      travel.setDict(d);
      if(dictManage.getByNodeType(d.getId()).size()>0){
    	  List<DictEntity> list = dictManage.getByNodeType(d.getId());
    	  if(openId!=null&&!openId.equals("")){
    	    	UserEntity userEntity = uSerService.loginOpenId(openId);
    	        String getroleApplyense = dynamicGetRoleUtil.getroleApplyense(userEntity.getId());
    	        if(getroleApplyense.equals("0")){
    	        	for(DictEntity travelEntity:list){
    	        		System.out.println(travelEntity.getId());
    	        		if(travelEntity.getId().equals("20180522112056140")){
    	        			list.remove(travelEntity);
    	        		}
    	        	}
    	        }
    	    }
    	  
    	  travel.setListDict(list);
      }
      
      else
        travel.setListDict(dictEntities);
      ls.add(travel);
    }
    Map<String, Object> jsonMap = new HashMap<String, Object>();
    
    jsonMap.put("data", ls);
    return jsonMap;
  }
  	//在前台显示字典信息（转换）
	@RequestMapping("/showDictionaryInfo")
	@ResponseBody
	public ModelAndView showDepartmentInfo(){
		ModelAndView result=new ModelAndView("common/dictManage");
		return result;
	} 
}
