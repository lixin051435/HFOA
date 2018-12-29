package com.hfoa.controller.car;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.common.AnccResult;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.entity.car.BCarbaseinfoDTO;
import com.hfoa.entity.car.BCargasinfo;
import com.hfoa.entity.car.BInsurance;
import com.hfoa.entity.car.CarApplyDTO;
import com.hfoa.entity.car.CarApplyInfo2;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.entity.role.BRoleEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.car.BCarapplymanageService;
import com.hfoa.service.car.BCarbaseinfoService;
import com.hfoa.service.car.BCargasinfoService;
import com.hfoa.service.car.BInsuranceService;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.TimeUtil;
import com.hfoa.util.WorkflowUtil;

/**
 * 
 * @author wzx
 *公共车申请
 */
@Controller
@RequestMapping("applyCar")
public class CarApplyController {
	@Autowired
	private WorkflowUtil workflowUtil;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private BCarapplymanageService bCarapplymanageService;
	@Autowired
	private USerService bUserService;
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
	private BCarbaseinfoService bCarbaseinfoService;
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	@Autowired
    private DictManage dictManage;
	@Autowired
    private BInsuranceService bInsuranceService;
	@Autowired
    private BCargasinfoService bCargasinfoService;
	@Autowired
    private BProcessService bProcessService;
	@Autowired
	private IdentityService identityService;
//	*******************************************导数据***************************************
	//申请
	@RequestMapping("apply")
	@ResponseBody
	public void apply(){
		//获取所有已预约车辆
		List<CarApplyInfo2> carList=bCarapplymanageService.getAllYuyue();
		for (CarApplyInfo2 carApplyInfo2 : carList) {
			UserEntity buser = bUserService.getUserByUserCode(carApplyInfo2.getApplyUserName());
			List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(buser.getId());
			String departId="";
			String department="";
			for (Integer integer : departmentIdByUserId) {
				departId+=integer+",";
				BDepartmentEntity depart=bDepartmentService.getDepartmentIdById(integer);
				department+=depart.getDepartmentname()+",";
			}
			carApplyInfo2.setDepartment(department);//部门名称
			carApplyInfo2.setDepartmentId(departId);//部门id
			UserEntity user = bUserService.getUserByUserCode(carApplyInfo2.getApproveMan());
			carApplyInfo2.setApprovalUserId(user.getId());//审批人id
			//获取审批人的角色
			List<Integer> roleId=bUserService.getRoleIdByUserId(user.getId());
//			审批者的最大级别
			int realGrade=0;
//			审批者的最大级别角色
			int realRoleId=0;
			for (Integer integer : roleId) {
				Integer grade=bRoleService.getGradeFromClass(integer);
				if(grade!=null&&grade>realGrade){
					realGrade=grade;
					realRoleId=integer;
				}
			}
			BRoleEntity roleByRoleId = bRoleService.getRoleByRoleId(realRoleId);
			carApplyInfo2.setRoleName(roleByRoleId.getRolename());//审批人角色名称
			carApplyInfo2.setRoleId(realRoleId+"");//审批人角色id
			bCarapplymanageService.updateCarApply(carApplyInfo2);
			int carapplyId=carApplyInfo2.getID();
			//获取部门id
			String departmentId=carApplyInfo2.getDepartmentId();
			//开启流程实例
			Map<String,Object> activitiMap = new HashMap<>();
			activitiMap.put("user", departmentId);
//			activitiMap.put("user", user.getOpenid());
			String objId="applyCar:"+carapplyId;
			identityService.setAuthenticatedUserId(buser.getCode());
			runtimeService.startProcessInstanceByKey("applyCarKey",objId,activitiMap);
			//根据流程定义id和节点id查询任务Id
			List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", "sqjc");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			int i=0;
			for(;i<bids.size();i++){
				int id=Integer.parseInt(bids.get(i));
				if(id==carapplyId)
					break;
			}
			String taskId=tasks.get(i).getId();
			System.out.println("***流程开启***");
			//完成任务
			completeApplyTask(taskId,"", "true", 2);
		}
		
	}
	@RequestMapping("passApprove")
	@ResponseBody
	public void passApprove(){
		String pointId="sp";//节点名称
		List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findBusinessByTasks(busAndTaskId.keySet(),departmentId);
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId.keySet(),2);
		for (CarApplyInfo2 carApplyInfo2 : carApplyList) {
			//taskId
			String str = carApplyInfo2.getID()+"";
			String tid=busAndTaskId.get(str);
			int status=3;
			carApplyInfo2.setStatus(status);
			bCarapplymanageService.updateCarApply(carApplyInfo2);
			if(status==3){
				//如果通过，修改审批车辆状态为“已预约”
				String info="2";
				String parentId=Constants.COMMON_CAR;
				String text=dictManage.getByParentAndInfo(info,parentId);
				BCarbaseinfo carinfo=bCarbaseinfoService.getCarInfoByNum(carApplyInfo2.getCarCode());
				carinfo.setCarstate(text);
				carinfo.setCarLable(2);
				bCarbaseinfoService.update(carinfo);
			}
			//办理业务
			Map<String,Object> map = new HashMap<>();
//			map.put("user", user.getOpenid());
			map.put("result", true); 
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(tid);
			workflowUtil.completeTask(workflowBean, map);
		}
	}
	//出入库
	@RequestMapping("outin")
	@ResponseBody
	public Object outin(){
	//用印处理节点
	String pointId="jcck";
	//根据节点的名称获和用户名称取任务
	List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", pointId);
	//根据任务获取业务
	Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
	List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId.keySet(),3);
	
		return carApplyList;
	}
//	*******************************************借用状态***************************************
	//借用状态一级
	@RequestMapping("carInfo")
	@ResponseBody
	public List<BCarbaseinfo> carInfo(){
		//当前时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        //一个月之前时间
        String mon = format.format(m);
		List<BCarbaseinfo> sendInfo=new ArrayList<>();
		String info="4";
		String parentId=Constants.COMMON_CAR;
		String text=dictManage.getByParentAndInfo(info,parentId);
		List<BCarbaseinfo> carSendInfo=bCarbaseinfoService.getAllNormalCar(text);
		for (BCarbaseinfo bCarbaseinfo : carSendInfo) {
			int appCount=bCarapplymanageService.getAppCount(bCarbaseinfo.getCarnum(),mon);
			int appointmentCount=bCarapplymanageService.getAppointmentCount(bCarbaseinfo.getCarnum(),mon);
			bCarbaseinfo.setAppCount(appCount);
			bCarbaseinfo.setAppointmentCount(appointmentCount);
			sendInfo.add(bCarbaseinfo);
		}
		return sendInfo;
	}
	//借用状态二级------------------------carNum：车牌号
	@RequestMapping("carSendDetail")
	@ResponseBody
	public HashMap<String, Object> carSendDetail(String carNum){
		//当前时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        //一个月之前时间
        String mon = format.format(m);
		HashMap<String, Object> hashMap = new HashMap<>();
		List<CarApplyInfo2> carApply=new ArrayList<>();
		List<CarApplyInfo2> carApplydetaile=bCarapplymanageService.getByCarNum(carNum);
		for (CarApplyInfo2 carApplyInfo2 : carApplydetaile) {
			if(carApplyInfo2.getApplyTime().compareTo(mon)>=0){
				carApply.add(carApplyInfo2);
			}
		}
		BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carNum);
		BCarbaseinfoDTO car=new BCarbaseinfoDTO();
		car.setCarbuytime(carInfoByNum.getCarbuytime());
		car.setCarcode(carInfoByNum.getCarcode());
		car.setCardetailinfo(carInfoByNum.getCardetailinfo());
		car.setCardistance(carInfoByNum.getCardistance());
		car.setCardvale(carInfoByNum.getCardvale());
		car.setCarinsuranceinfo(carInfoByNum.getCarinsuranceinfo());
		car.setCarinsuranceinfo1(carInfoByNum.getCarinsuranceinfo1());
		car.setCarinsuranceinfodetal(carInfoByNum.getCarinsuranceinfodetal());
		car.setCarLable(carInfoByNum.getCarLable());
		car.setCarstate(carInfoByNum.getCarstate());
		car.setCarunit(carInfoByNum.getCarunit());
		car.setId(carInfoByNum.getId());
		car.setOthers(carInfoByNum.getOthers());
		car.setPeasonnum(carInfoByNum.getPeasonnum());
		car.setSuspendday(carInfoByNum.getSuspendday());
		car.setTypenum(carInfoByNum.getCartype()+","+carInfoByNum.getCarnum());
		car.setCarUrl(carInfoByNum.getCarUrl());
		car.setCarNum(carInfoByNum.getCarnum());
		car.setCarType(carInfoByNum.getCartype());
		hashMap.put("carInfo", car);
		hashMap.put("applyInfo", carApply);
//		for (CarApplyInfo2 carApplyInfo2 : carApply) {
//			carApplyInfo2.setCarTypeNum(carInfoByNum.getCartype()+","+carInfoByNum.getCarnum());
//			carApplyInfo2.setCarUrl(carInfoByNum.getCarUrl());
//		}
		return hashMap;
	}
	//新建------------------------getApproval.action openId:申请人的微信编号，moduleNum：模块的编号
	//借用状态三级------------------------appId：申请id
	@RequestMapping("appForm")
	@ResponseBody
	public CarApplyInfo2 appForm(Integer appId){
		CarApplyInfo2 byId = bCarapplymanageService.getById(appId);
		byId.setCarTypeNum(byId.getCarType()+","+byId.getCarCode());
		return byId;
	}
//	*******************************************借车申请***************************************
	//部署流程实例
	@RequestMapping(value="/deployment",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String deployment(){
		workflowUtil.deployment("activiti/applyCar", "申请借车");
		return "部署成功";
	}
	//list去重
	 public List<UserEntity> removeDuplicate(List<UserEntity> list) {
	        for (int i = 0; i < list.size() - 1; i++) {
	            for (int j = list.size() - 1; j > i; j--) {
	                if (list.get(j).equals(list.get(i))) {
	                    list.remove(j);
	                }
	            }
	        }
			return list;
	    }
	//根据登录申请人获取相应的审批人----------openId:申请人的微信编号
	@RequestMapping("getApproval")
	@ResponseBody
	public Object getApproval(HttpServletRequest request,String openId){
		Map<String,Object> map=new HashMap<>();
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity u=null;
		List<UserEntity> approvalMan =null;
		if(userId!=null){
			u=bUserService.getUserById(userId);
			approvalMan = dynamicGetRoleUtil.getApprovalMan(userId);
		}
		approvalMan=removeDuplicate(approvalMan);
		List<BCarbaseinfoDTO> availableCar = (List<BCarbaseinfoDTO>) getAvailableCar();
		//是否可以提交申请
		BProcess process=bProcessService.getprocessByName("借车申请（不可操作）");
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		roleIdByUserId.retainAll(roleIds);
		if(roleIdByUserId.size()>0){
			map.put("ifOperation", false);
		}else{
			map.put("ifOperation", true);
		}
		map.put("user", u);
		map.put("approvalMan", approvalMan);
		map.put("car", availableCar);
		return map;
}
	//获取可以出库的车
	@RequestMapping("getAvailableCar")
	@ResponseBody
	public Object getAvailableCar(){
		TimeUtil tu=new TimeUtil();
		String week=tu.getWeekOfDate(new Date());
		String info1="1";
		String parentId=Constants.COMMON_CAR;
		String text1=dictManage.getByParentAndInfo(info1,parentId);
		String info2="2";
		String text2=dictManage.getByParentAndInfo(info2,parentId);
		//获取当天出库的车
		List<BCarbaseinfoDTO> carOutList=new ArrayList<BCarbaseinfoDTO>();
		List<BCarbaseinfo> carList=bCarbaseinfoService.getAvailableCar(week,text1);
		List<BCarbaseinfo> carList1=bCarbaseinfoService.getAvailableCar(week,text2);
		carList.addAll(carList1);
		//获取所有正常车辆
		String info="4";
		String parentId1=Constants.COMMON_CAR;
		String text=dictManage.getByParentAndInfo(info,parentId1);
		List<BCarbaseinfo> normalcar=bCarbaseinfoService.getAllNormalCar(text);
		for (BCarbaseinfo bCarbaseinfo : normalcar) {
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
				car.setPeasonnum(bCarbaseinfo.getPeasonnum());
				car.setSuspendday(bCarbaseinfo.getSuspendday());
				car.setTypenum(bCarbaseinfo.getCarnum()+","+bCarbaseinfo.getCartype());
				car.setCarNum(bCarbaseinfo.getCarnum());
				car.setCarType(bCarbaseinfo.getCartype());
				carOutList.add(car);
		}
		return carOutList;
	}

	//获取抄送人
	@RequestMapping("getCheckMan")
	@ResponseBody
	public List<UserEntity> getCheckMan(Integer userId,Integer departmentId,int approvalUserId,String approvalId){
		List<UserEntity> userList=new ArrayList<>();
		List<UserEntity> uList=bUserService.getAllUserByDepartId(departmentId);
		for (UserEntity bUserEntity : uList) {
			if(bUserEntity.getId()!=userId&&approvalUserId!=bUserEntity.getId()){
				userList.add(bUserEntity);
			}
		}
		return userList;
	}
	//提交前判断申请时间
	@RequestMapping("canApply")
	@ResponseBody
	public String canApply(CarApplyInfo2 carapply) throws ParseException{
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String flag="ok";
		String now=TimeUtil.fromDateDateToString(new Date());
		String week=TimeUtil.getWeekOfDate(inputFormat.parse(carapply.getBeginTimePlan()));
		String maxReturnTime="0000-00-00 00:00:00";
		//根据申请车辆车牌号获取该车辆还未进行完的申请信息
		List<CarApplyInfo2> list=bCarapplymanageService.getNoFinishedInfo(carapply.getCarCode(),carapply.getID());
//		for (int i=0;i<list.size();i++) {
//			if(carapply.getID()==list.get(0).getID()){
//				list.remove(i);
//			}
//		}
//		for (CarApplyInfo2 carApplyInfo2 : list) {
//			if(carApplyInfo2.getEndTimePlan().compareTo(maxReturnTime)>0){
//				maxReturnTime=carApplyInfo2.getEndTimePlan();
//			}
//		}
		//获取车辆限行日
		BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
			if(week.equals(carInfoByNum.getSuspendday())){
				flag="出库时间不能为该车的限行日!";
			}else{
				for (CarApplyInfo2 carApplyInfo2 : list) {
				if(TimeUtil.isOverlap(carapply.getBeginTimePlan(), carapply.getEndTimePlan(), carApplyInfo2.getBeginTimePlan(), carApplyInfo2.getEndTimePlan())){
					flag="该时间段内该车不可借出!";
				}					
				}
//				if(carapply.getBeginTimePlan().compareTo(maxReturnTime)<=0){
//					flag="该时间段内该车不可借出!";
//				}
			}
		return flag;
	}
	//提交申请，开启借车流程-------------carapply：业务信息
	@RequestMapping("startApply")
	@ResponseBody
	public String startApply(CarApplyInfo2 carapply,HttpSession session,HttpServletRequest request){
		//测试数据
//		carapply.setApplyUserName("张瑞超");//申请人账号
//		carapply.setApplyMan("张瑞超");//申请人账号
//		carapply.setCarType("奔驰");//选择的车辆牌子
//		carapply.setCarId(5);//车辆id
//		carapply.setCarCode("京Q1L932");//车牌号
//		carapply.setApproveMan("刘斌");//审核人账号
//		carapply.setDriver("辛源才");//驾驶人
//		carapply.setCompareManNum(4);//出差人数
//		carapply.setBeginPlace("海丰");//起始地
//		carapply.setEndPlace("永丰");//目的地
//		carapply.setBeginTimePlan("2018-10-21 09:58:53");//计划开始时间
//		carapply.setEndTimePlan("2018-10-21 17:00:00");//计划结束时间
//		carapply.setUseCarReason("调试项目");//借车事由
//		carapply.setRealApproveMan("刘斌");
		
		//真实获取
		UserEntity buser = bUserService.getUserByUserCode(carapply.getApplyUserName());
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(buser.getId());
		String departId="";
		String department="";
		for (Integer integer : departmentIdByUserId) {
			departId+=integer+",";
			BDepartmentEntity depart=bDepartmentService.getDepartmentIdById(integer);
			department+=depart.getDepartmentname()+",";
		}
		carapply.setDepartment(department);//部门名称
		carapply.setDepartmentId(departId);//部门id
		UserEntity user = bUserService.getUserByUserCode(carapply.getApproveMan());
		carapply.setApprovalUserId(user.getId());//审批人id
		//获取审批人的角色
		List<Integer> roleId=bUserService.getRoleIdByUserId(user.getId());
//		审批者的最大级别
		int realGrade=0;
//		审批者的最大级别角色
		int realRoleId=0;
		for (Integer integer : roleId) {
			Integer grade=bRoleService.getGradeFromClass(integer);
			if(grade!=null&&grade>realGrade){
				realGrade=grade;
				realRoleId=integer;
			}
		}
		BRoleEntity roleByRoleId = bRoleService.getRoleByRoleId(realRoleId);
		carapply.setRoleName(roleByRoleId.getRolename());//审批人角色名称
		carapply.setRoleId(realRoleId+"");//审批人角色id
		carapply.setState("已申请");
		//测试方法
//		String gradeId=dynamicGetRoleUtil.getNextGrade(bUserService.getUserByUserCode(carapply.getApplyUserName()).getId(), null, moduleNum);
//		carapply.setRoleId(gradeId);//审批人级别
		Date now=new Date();
		String applyTime=TimeUtil.fromDateDateToString(now);
		carapply.setApplyTime(applyTime);//申请时间
		//获取计划时间差（小时）
		carapply.setAccountPlanTime(TimeUtil.getAccurateHour(carapply.getBeginTimePlan(), carapply.getEndTimePlan()));
		
		//获取申请id
		bCarapplymanageService.insertCarApply(carapply);
		
		int carapplyId=carapply.getID();
		//获取申请人账号
		String userName=carapply.getApplyUserName();
		//获取部门id
		String departmentId=carapply.getDepartmentId();
		//开启流程实例
		Map<String,Object> activitiMap = new HashMap<>();
		activitiMap.put("user", departmentId);
//		activitiMap.put("user", user.getOpenid());
		String objId="applyCar:"+carapplyId;
		identityService.setAuthenticatedUserId(buser.getCode());
		runtimeService.startProcessInstanceByKey("applyCarKey",objId,activitiMap);
		//根据流程定义id和节点id查询任务Id
		List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", "sqjc");
		List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
		int i=0;
		for(;i<bids.size();i++){
			int id=Integer.parseInt(bids.get(i));
			if(id==carapplyId)
				break;
		}
		String taskId=tasks.get(i).getId();
		System.out.println("***流程开启***");
		//完成任务
		completeApplyTask(taskId,"", "true", 2);
		
		//公众号消息推送
		String GzhOpenId = "";
		UserEntity userEntity = bUserService.getUserByUserCode(carapply.getApproveMan());
		if(userEntity!=null){
			GzhOpenId = userEntity.getModifiedby();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String title = "您好,您有一个新的公车申请需要审批,请及时处理。";
		String mark = "待审批";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("公车申请");
		weiEntity.setApplyMan(userName);
		weiEntity.setApplyTime(sdf.format(new Date()));
		try {
			CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userName;
	}
	//业务流程，完成申请（没有角色）
	@RequestMapping("completeApplyTask")
	@ResponseBody
	public void completeApplyTask(String taskId,String comment,String result,Integer status){
		int id = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//修改业务状态
		if(status>0){
			bCarapplymanageService.updateStatus(status,id);
		}
		//通过 result=true
		//完成任务
		Map<String,Object> map = new HashMap<>();
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
	}
//	*******************************************借车审批***************************************
	//判断是否有权限审批------------openId:微信认证号
//	@RequestMapping("ifApproval")
//	@ResponseBody
//	public HashMap<String, Object> ifApproval(String openId){
//		//获取用户id
//		Integer userId=bUserService.getUserIdByOpenId(openId);
//		//获取登录用户的所有角色id
//		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
//		HashMap<String, Object> hashMap = new HashMap<>();
//		//获取经理级别跟领导级别角色
//		List<Integer> leaderRole=new ArrayList<>();
//		Set<Integer> roleIdFromClass1 = bRoleService.getRoleIdFromClass("2*");
//		Set<Integer> roleIdFromClass2 = bRoleService.getRoleIdFromClass("3*");
//		leaderRole.addAll(roleIdFromClass1);
//		leaderRole.addAll(roleIdFromClass2);
//		roleIdByUserId.retainAll(leaderRole);
//		if(roleIdByUserId.size()>0){
//			//可操作
//			hashMap.put("permission", true);
//			getPointTask(openId);
//		}else{
//			//不可操作
//			hashMap.put("permission", false);
//		}
//		return hashMap;
//	}
	
	//根据节点名称以及自己角色获取所属任务（审批人获取待审批任务一级）------------openId:微信认证号
	@RequestMapping("getPointTask")
	@ResponseBody
	public Object getPointTask(String openId){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		String pointId="sp";//节点名称
		List<CarApplyInfo2> realList= new ArrayList<>();
		if(userId!=null){
//			BUserEntity user = bUserService.getUserByUserCode(userCode);
//			int departmentId=user.getDepartmentid();
			//部门idlist
			List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
			//获取登录用户的所有角色id
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findBusinessByTasks(busAndTaskId.keySet(),departmentId);
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId.keySet(),2);
			for(CarApplyInfo2 carapply:carApplyList){
				List<Integer> departIdList=new ArrayList<>();
				String[] split = carapply.getDepartmentId().split(",");
				for(int i=0;i<split.length;i++){
					departIdList.add(Integer.parseInt(split[i]));
				}
				departIdList.retainAll(departList);
				if(userId==carapply.getApprovalUserId()&&departIdList.size()>0){
					String str = carapply.getID()+"";
					String tid=busAndTaskId.get(str);
					carapply.setTaskId(tid);
					
					BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					if(carInfoByNum!=null){
						String carUrl=carInfoByNum.getCarUrl();
						carapply.setCarUrl(carUrl);
					}
					realList.add(carapply);
				}
			}
		}
		return realList;
	}
	//借车审批二级------------appId:申请id，taskId：任务编号
	@RequestMapping("approvalAppForm")
	@ResponseBody
	public CarApplyInfo2 approvalAppForm(Integer appId,String taskId){
		CarApplyInfo2 byId = bCarapplymanageService.getById(appId);
		byId.setCarTypeNum(byId.getCarType()+","+byId.getCarCode());
		byId.setTaskId(taskId);
		return byId;
	}

	// 生成公车申请审批单号 
	@Transactional
	public String getCommonCarApprovalNum(String date,Integer personNum) { // date为当天申请的时间personNum为车辆载人数
		String number = ""; // 定义审批单编号变量，初始为空
		//根据当天时间查询在业务申请信息中的数量
		int appNum=bCarapplymanageService.getAppNumByLike("%"+date+"%");
		int end=0;
		String middleString="";
		String endString="";
		if(appNum==end){
			end=1;
		}else{
			end=appNum+1;
		}
		if(end>0&&end<10){
			endString="00"+end;
		}else if(end>=10&&end<99){
			endString="0"+end;
		}else if(end>=100){
			endString=end+"";
		}
		if(personNum<10){
			middleString="0"+personNum;
		}else{
			middleString=personNum+"";
		}
		number=date+middleString+endString;
		return number;
	}
	//审批----------taskId:任务编号，result：是否通过(true是通过false是驳回)，status：业务状态(3是通过1是驳回)，openId:微信认证编号,comment:驳回意见
	@RequestMapping("climeApprovalTask")
	@ResponseBody
	public void climeApprovalTask(String taskId,String result,Integer status,HttpSession session,String openId,String comment){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		int carApplyId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		CarApplyInfo2 carApply = bCarapplymanageService.getById(carApplyId);
		//根据车牌号查找车辆信息
		BCarbaseinfo carInfo=bCarbaseinfoService.getCarInfoByNum(carApply.getCarCode());
		Date parse = null;
		try {
			parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(carApply.getApplyTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String dateStr = new SimpleDateFormat("yyyyMMdd").format(parse);
		//生成当前申请的审批单号
		if("".equals(carApply.getApplyId())||carApply.getApplyId()==null){
			String applyId=getCommonCarApprovalNum(dateStr,carInfo.getPeasonnum());
			carApply.setApplyId(applyId);
		}
		if(userId!=null){
			UserEntity userById = bUserService.getUserById(userId);
			carApply.setApprovalUserId(userId);
			carApply.setApproveMan(userById.getCode());
			carApply.setRealApproveMan(userById.getRealname());
			taskService.claim(taskId, userById.getCode());
		}
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//出库审批人
//		UserEntity user = bUserService.getUserByUserCode(carApply.get);
		//修改业务状态，通过是3，驳回是1
		if(status>0){
			carApply.setStatus(status);
			if(status==3){
				carApply.setState("已预约");
			}else if(status==1){
				carApply.setState("已驳回");
			}
			bCarapplymanageService.updateCarApply(carApply);
		}
		if(status==3){
			//如果通过，修改审批车辆状态为“已预约”
			String info="2";
			String parentId=Constants.COMMON_CAR;
			String text=dictManage.getByParentAndInfo(info,parentId);
			BCarbaseinfo carinfo=bCarbaseinfoService.getCarInfoByNum(carApply.getCarCode());
			carApply.setLengthBegin(carinfo.getCardistance());
			bCarapplymanageService.updateCarApply(carApply);
			carinfo.setCarstate(text);
			carinfo.setCarLable(2);
			bCarbaseinfoService.update(carinfo);
		}
		//办理业务
		Map<String,Object> map = new HashMap<>();
//		map.put("user", user.getOpenid());
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//判断出车时间是否在工作时间内
		String workTime="08:45:00";//上班时间
		String closingTime="16:45:00";//下班时间
		String outTime=carApply.getBeginTimePlan().substring(11, carApply.getBeginTimePlan().length());//预借出库时间
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
		Date workdate=null;
		Date closingDate=null;
		Date outDate=null;
		try {   
			outDate = fmt.parse(outTime);
			workdate=fmt.parse(workTime);
			closingDate=fmt.parse(closingTime);
			} catch (ParseException e) {  
				e.printStackTrace();  
		} 
		String week=TimeUtil.getWeekOfDate(outDate);
		//公众号消息推送
		//出库员获取推送消息
		if("true".equals(result)){
			String mark1 = "待出库";
			BProcess process=bProcessService.getprocessByName("借车出库");
			int processId=process.getId();
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			TreeSet<Integer> userIdByRoleId = new TreeSet<Integer>(); 
			List <Integer> userList=new ArrayList<>();
			for (Integer integer : roleIds) {
				userList.addAll(bUserService.getUserIdByRoleId(integer));
			}
			UserEntity userById =null;
			for (Integer integer : userList) {
				userIdByRoleId.add(integer);
			}
			for (Integer integer : userIdByRoleId) {
				userById = bUserService.getUserById(integer);
			}
			String GzhOpenId1 = "";
			if(userById!=null){
				GzhOpenId1 = userById.getModifiedby();
			}
			String title1 = "您好,您有一个新的公车申请需要出库,请及时处理。";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setTitle("公车申请");
			weiEntity.setApplyMan(carApply.getApplyUserName());
			weiEntity.setApplyTime(sdf.format(new Date()));
			try {
				CommonUtil.sendPrivateApproveMessage(GzhOpenId1, weiEntity, title1, mark1);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//申请人获取推送消息
		String mark = "";
		String GzhOpenId ="";
		String title = "";
		UserEntity applyuser=bUserService.getUserByUserCode(carApply.getApplyUserName());
		if(applyuser!=null){
			GzhOpenId =applyuser.getModifiedby();
		}
		if("true".equals(result)){
			mark = "审批通过";
			if("星期日".equals(week)||"星期六".equals(week)||TimeUtil.isEffectiveDate(outDate, workdate, closingDate)){
				title = "您好,您有一个新的公车申请已经通过审批,但是出车时间不在工作时间内，请提前联系公车管理员。";
			}else{
				title = "您好,您有一个新的公车申请已经通过审批。";
			}
		}else if("false".equals(result)){
			mark = "待处理";
			title = "您好,您有一个新的公车申请需要处理,请及时处理。";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("公车申请");
		weiEntity.setApplyMan(carApply.getApplyUserName());
		weiEntity.setApplyTime(sdf.format(new Date()));
		try {
			CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//驳回后修改再次发起申请----------------carapply:公共车申请信息
	@RequestMapping("reStartApply")
	@ResponseBody
	public String reStartApply(CarApplyInfo2 carapply,String taskId){
		if(carapply.getStatus()<3){
			UserEntity user = bUserService.getUserByUserCode(carapply.getApproveMan());
			carapply.setApprovalUserId(user.getId());//审批人id
			carapply.setDepartment(carapply.getDepartment()+",");
			//获取审批人的角色
			List<Integer> roleId=bUserService.getRoleIdByUserId(user.getId());
//			审批者的最大级别
			int realGrade=0;
//			审批者的最大级别角色
			int realRoleId=0;
			for (Integer integer : roleId) {
				Integer grade=bRoleService.getGradeFromClass(integer);
				if(grade!=null&&grade>realGrade){
					realGrade=grade;
					realRoleId=integer;
				}
			}
			BRoleEntity roleByRoleId = bRoleService.getRoleByRoleId(realRoleId);
			carapply.setRoleName(roleByRoleId.getRolename());//审批人角色名称
			carapply.setRoleId(realRoleId+"");//审批人角色id
		}
		if(taskId!=null&&!"".equals(taskId)){
			carapply.setState("已申请");
		}
		carapply.setID(carapply.getID());
//		Date now=new Date();
//		String applyTime=TimeUtil.fromDateDateToString(now);
//		carapply.setApplyTime(applyTime);//申请时间
		//获取计划时间差（小时）
		carapply.setAccountPlanTime(TimeUtil.getAccurateHour(carapply.getBeginTimePlan(), carapply.getEndTimePlan()));
		//获取申请id
		bCarapplymanageService.updateCarApply(carapply);
		//获取申请人账号
		String userName=carapply.getApplyUserName();
		//开启流程实例
		System.out.println("***流程重新开启***");
		//完成任务
		if(taskId!=null&&!"".equals(taskId)){
			completeApplyTask(taskId,"", "true", 2);
			//公众号消息推送
			String GzhOpenId = "";
			UserEntity userEntity = bUserService.getUserByUserCode(carapply.getApproveMan());
			if(userEntity!=null){
				GzhOpenId = userEntity.getModifiedby();
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String title = "您好,您有一个新的公车申请需要审批,请及时处理。";
			String mark = "待审批";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setTitle("公车申请");
			weiEntity.setApplyMan(userName);
			weiEntity.setApplyTime(sdf.format(new Date()));
			try {
				CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return userName;
	}
//	*******************************************借用信息***************************************
	//借用信息（一级）------------openId:微信认证编号
	@RequestMapping("sendInfo")
	@ResponseBody
	public List<CarApplyInfo2> sendInfo(String openId){
		List<CarApplyInfo2> sendInfo= new ArrayList<>();
		Integer userId=bUserService.getUserIdByOpenId(openId);
		if(userId!=null){
			UserEntity user=bUserService.getUserById(userId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks =new ArrayList<>();
			List<Task> task1 =workflowUtil.getTaskByIds("applyCarKey", "sqjc");
			List<Task> task2 =workflowUtil.getTaskByIds("applyCarKey", "sp");
			List<Task> task3 =workflowUtil.getTaskByIds("applyCarKey", "jcck");
			List<Task> task4 =workflowUtil.getTaskByIds("applyCarKey", "hcrk");
			tasks.addAll(task1);
			tasks.addAll(task2);
			tasks.addAll(task3);
			tasks.addAll(task4);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByUserCode(busAndTaskId.keySet(),user.getCode());
			for(CarApplyInfo2 carapply:carApplyList){
				String str = carapply.getID()+"";
				String tid=busAndTaskId.get(str);
				carapply.setTaskId(tid);
				BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
				if(carInfoByNum!=null){
					String carUrl=carInfoByNum.getCarUrl();
					carapply.setCarUrl(carUrl);
				}
				sendInfo.add(carapply);
			}
		}
		return sendInfo;
	}
	//借用信息（二级）------------appForm.action?appId=?
	//撤销申请------------appId:业务申请id,taskId:业务id
	@RequestMapping("revokeApply")
	@ResponseBody
	public void revokeApply(Integer appId,String taskId){
		CarApplyInfo2 byId = bCarapplymanageService.getById(appId);
		if(byId.getStatus()==1||byId.getStatus()==3){
			String GzhOpenId = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String title = "您好,"+byId.getApplyUserName()+"于"+byId.getApplyTime()+"申请的车牌号是"+byId.getCarCode()+"的公车申请已撤销！";
			String mark = "已撤销";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setTitle("公车申请");
			weiEntity.setApplyMan(byId.getApplyUserName());
			weiEntity.setApplyTime(sdf.format(new Date()));
			UserEntity userEntity = bUserService.getUserByUserCode(byId.getApproveMan());
			if(userEntity!=null){
				GzhOpenId = userEntity.getModifiedby();
			}
			try {
				CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		int appCount=bCarapplymanageService.getCountByCarNum(byId.getCarCode());
		if(appCount==1){
			String info="1";
			String parentId=Constants.COMMON_CAR;
			String text=dictManage.getByParentAndInfo(info,parentId);
			BCarbaseinfo carinfo=bCarbaseinfoService.getCarInfoByNum(byId.getCarCode());
			carinfo.setCarstate(text);
			carinfo.setCarLable(1);
			bCarbaseinfoService.update(carinfo);
		}
		if(taskId!=null&&!"null".equals(taskId)){
			workflowUtil.deleteProcess(taskId);
		}
		bCarapplymanageService.deleteById(appId);
	}
//	*******************************************借车出库***************************************
	//获取借车出库信息-----------------openId:微信认证编号
	@RequestMapping("getOutTask")
	@ResponseBody
	public AnccResult getOutTask(String openId,Integer nowPage,Integer pageSize,CarApplyDTO carUseDetailDTO){
		if(carUseDetailDTO.getEndtime()!=null&&!"null".equals(carUseDetailDTO.getEndtime())){
			carUseDetailDTO.setEndtime(carUseDetailDTO.getEndtime()+" 24:00:00");
		}
//		carUseDetailDTO.setDepartment(carUseDetailDTO.getDepartment()+",");
		Map<String,Object> minmap = new HashMap<>();
		List<CarApplyInfo2> realList= new ArrayList<>();
		//获取用户的id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		if(userId!=null){
			//借车出库节点
			String pointId="jcck";
			UserEntity user = bUserService.getUserById(userId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", pointId);
			//获取登录账号的所有流程角色
//			List<Integer> role = bProcessService.getRoleIdByUserId(userId);
			//获取出库的角色
			BProcess process=bProcessService.getprocessByName("借车出库");
			int processId=process.getId();
			//获取登录账号的所有角色
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			roleIdByUserId.retainAll(roleIds);
			//获取领导级别的角色
			Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
			roleIdFromClass.retainAll(roleIdByUserId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId.keySet(),3);
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByCase(busAndTaskId.keySet(),3,carUseDetailDTO);
			for(CarApplyInfo2 carapply:carApplyList){
//				!user.getCode().equals(carapply.getApplyUserName())&&
				if(roleIdByUserId.size()>0){
					if(roleIdFromClass.size()>0){
						carapply.setIfApproval(0);
					}else{
						carapply.setIfApproval(1);
					}
					BCarbaseinfo carInfo=bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					carapply.setLengthBegin(carInfo.getCardistance());
					String str = carapply.getID()+"";
					String tid=busAndTaskId.get(str);
					carapply.setTaskId(tid);
					carapply.setTitle("公车申请");
					carapply.setSortTime(carapply.getApplyTime());
					carapply.setUrl("applyCar/completeOutTask.action");
					carapply.setParam("taskId,status,beginTime,cardistance");
					carapply.setDepartment(carapply.getDepartment().replace(",", ""));
					carapply.setDepartmentId(carapply.getDepartmentId().replace(",", ""));
					carapply.setCarTypeNum(carapply.getCarType()+","+carapply.getCarCode());
					BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					if(carInfoByNum!=null){
						String carUrl=carInfoByNum.getCarUrl();
						carapply.setCarUrl(carUrl);
					}
					realList.add(carapply);
	 		  }
			}
		}
//		sort(realList);
		int start = (nowPage - 1) * pageSize;
		int total = realList.size();
		List<publicEntity> list_all = new ArrayList<>();
	    if (start + pageSize <= total)
	      list_all.addAll(realList.subList(start, start + pageSize));
	    else
	      list_all.addAll(realList.subList(start, total));
	    minmap.put("pagesize", realList.size());
	    minmap.put("list", list_all);
		return AnccResult.ok(minmap);
	}
	//进入借车出库详情页面-----------------approvalAppForm.action?appId=?taskId=?
	//通过借车出库---------------taskId：任务编号，status：通过状态4，beginTime:借出时间，remarks:出库留言，openId:微信认证编号，lengthBegin:出库里程
	@RequestMapping("completeOutTask")
	@ResponseBody
	public void completeOutTask(String taskId,Integer status,String beginTime,String remarks,String openId,float lengthBegin){
		Integer userId = bUserService.getUserIdByOpenId(openId);
		UserEntity userById = bUserService.getUserById(userId);
		int applyId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		CarApplyInfo2 carApply = bCarapplymanageService.getById(applyId);
		//获取出库车的行驶里程
//		BCarbaseinfo carInfo=bCarbaseinfoService.getCarInfoByNum(carApply.getCarCode());
		carApply.setLengthBegin(lengthBegin);
		carApply.setRemarks(remarks);
		//实际出车时间
//		Date now=new Date();
//		String beginTime = TimeUtil.fromDateDateToString(now);
		carApply.setBeginTime(beginTime);

		carApply.setStatus(status);
		//修改业务状态
		if(status>0){
			carApply.setState("使用中");
			carApply.setOutTreasuryMan(userById.getCode());
			bCarapplymanageService.updateCarApply(carApply);
		}
		if(status==4){
			//如果通过，修改审批车辆状态为“已借出”
			String info="3";
			String parentId=Constants.COMMON_CAR;
			String text=dictManage.getByParentAndInfo(info,parentId);
			BCarbaseinfo carinfo=bCarbaseinfoService.getCarInfoByNum(carApply.getCarCode());
			carinfo.setCarstate(text);
			carinfo.setCarLable(3);
			bCarbaseinfoService.update(carinfo);
		}
		//完成任务
		Map<String,Object> map = new HashMap<>();
		map.put("result", "true"); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//公众号消息推送
		//获取入库的角色
		BProcess process=bProcessService.getprocessByName("还车入库");
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		TreeSet<Integer> userIdByRoleId = new TreeSet<Integer>(); 
		List <Integer> userList=new ArrayList<>();
		String GzhOpenId1 = "";
		for (Integer integer : roleIds) {
			userList.addAll(bUserService.getUserIdByRoleId(integer));
		}
		UserEntity user =null;
		for (Integer integer : userList) {
			userIdByRoleId.add(integer);
		}
		for (Integer integer : userIdByRoleId) {
			user = bUserService.getUserById(integer);
		}
		if(user!=null){
			GzhOpenId1 = user.getModifiedby();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String title1 = "您好,您有一个新的公车申请需要入库,请及时处理。";
		String mark1 = "待入库";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("公车申请");
		weiEntity.setApplyMan(carApply.getApplyUserName());
		weiEntity.setApplyTime(sdf.format(new Date()));
		
		//申请人获取推送消息
		String mark2 = "已出库";
		UserEntity applyuser=bUserService.getUserByUserCode(carApply.getApplyUserName());
		String GzhOpenId2 =applyuser.getModifiedby();
		String title2 = "您好,您有一个新的公车申请已经出库，请及时拿公车钥匙。";
		try {
			CommonUtil.sendPrivateApproveMessage(GzhOpenId1, weiEntity, title1, mark1);
			CommonUtil.sendPrivateApproveMessage(GzhOpenId2, weiEntity, title2, mark2);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
//	*******************************************还车入库***************************************
	//判断是否有权限入库-----------------openId:微信认证编号，moduleNum:模块id
//	public void sort (List<CarApplyInfo2> realList){
//		Collections.sort(realList,new Comparator<publicEntity>() {
//			public int compare(publicEntity o1, publicEntity o2) {
//				SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
//				Date before = null;
//				Date after = null;
//				try {
//					before = smf.parse(o1.sortTime);
//					after = smf.parse(o2.sortTime);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				return (int) (before.getTime()-after.getTime());
//			}
//			
//		});
//	}
	//获取还车入库信息-------------openId:微信认证编号
	@RequestMapping("getInTask")
	@ResponseBody
	public AnccResult getInTask(String openId,Integer nowPage,Integer pageSize,CarApplyDTO carUseDetailDTO){
		if(carUseDetailDTO.getEndtime()!=null&&!"null".equals(carUseDetailDTO.getEndtime())){
			carUseDetailDTO.setEndtime(carUseDetailDTO.getEndtime()+" 24:00:00");
		}
		carUseDetailDTO.setDepartment(carUseDetailDTO.getDepartment()+",");
		Map<String,Object> minmap = new HashMap<>();
		List<CarApplyInfo2> realList= new ArrayList<>();
		//获取用户的id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		if(userId!=null){
			String pointId="hcrk";
			UserEntity user = bUserService.getUserById(userId);
			//获取用户所有角色
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(user.getId());
			//获取入库的角色
			BProcess process=bProcessService.getprocessByName("还车入库");
			int processId=process.getId();
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			roleIdByUserId.retainAll(roleIds);
			//获取领导级别的角色
			Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
			roleIdFromClass.retainAll(roleIdByUserId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("applyCarKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId.keySet(),4);
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByCase(busAndTaskId.keySet(),4,carUseDetailDTO);
			for(CarApplyInfo2 carapply:carApplyList){
//				!user.getCode().equals(carapply.getApplyUserName())&&
				if(roleIdByUserId.size()>0){
					if(roleIdFromClass.size()>0){
						carapply.setIfApproval(0);
					}else{
						carapply.setIfApproval(1);
					}
					String str = carapply.getID()+"";
					String tid=busAndTaskId.get(str);
					carapply.setSortTime(carapply.getApplyTime());
					carapply.setTitle("公车申请");
					carapply.setUrl("applyCar/completeInTask.action");
					carapply.setParam("taskId,status,endLength,endTime,useCarTime");
					carapply.setTaskId(tid);
					carapply.setDepartment(carapply.getDepartment().replace(",", ""));
					carapply.setCarTypeNum(carapply.getCarType()+","+carapply.getCarCode());
					BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					if(carInfoByNum!=null){
						String carUrl=carInfoByNum.getCarUrl();
						carapply.setCarUrl(carUrl);
					}
					realList.add(carapply);
	 		  }
			}
		}
		
//		sort(realList);
		int start = (nowPage - 1) * pageSize;
		int total = realList.size();
		List<publicEntity> list_all = new ArrayList<>();
	    if (start + pageSize <= total)
	      list_all.addAll(realList.subList(start, start + pageSize));
	    else
	      list_all.addAll(realList.subList(start, total));
	    minmap.put("pagesize", realList.size());
	    minmap.put("list", list_all);
		return AnccResult.ok(minmap);
	}
	//进入还车入库详情页面-----------------approvalAppForm.action?appId=?taskId=?
	//通过还车入库，结束流程-------------------taskId:任务编号，status:完成标记为5,endLength:还车里程,endTime:还车时间，useCarTime：借车时长,openId:微信认证编号
	@RequestMapping("completeInTask")
	@ResponseBody
	public void completeInTask(String taskId,Integer status,Float endLength,String endTime,double useCarTime,String openId){
		Integer userId = bUserService.getUserIdByOpenId(openId);
		UserEntity userById = bUserService.getUserById(userId);
		//实际获得
		int applyId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		CarApplyInfo2 carApply = bCarapplymanageService.getById(applyId);
		carApply.setStatus(status);
		carApply.setLengthEnd(endLength);
		double feeLength=endLength-carApply.getLengthBegin();
		carApply.setAccountLength(feeLength);//计费里程
//		Date now=new Date();
//		String endTime = TimeUtil.fromDateDateToString(now);
		carApply.setEndTime(endTime);//还车时间
//		double useCarTime=TimeUtil.getAccurateHour(carApply.getBeginTime(), carApply.getEndTime());
		carApply.setAccountRealTime(useCarTime);//实际用车时间
		carApply.setState("已归还");
		//修改业务完成信息
		if(status>0){
			carApply.setInTreasuryMan(userById.getCode());
			bCarapplymanageService.updateCarApply(carApply);
		}
		if(status==5){
			//如果通过，修改审批车辆状态为“在库”
			String info="1";
			String parentId=Constants.COMMON_CAR;
			String text=dictManage.getByParentAndInfo(info,parentId);
			BCarbaseinfo carinfo=bCarbaseinfoService.getCarInfoByNum(carApply.getCarCode());
			carinfo.setCarstate(text);
			carinfo.setCarLable(1);
			carinfo.setCardistance((float) endLength);
			bCarbaseinfoService.update(carinfo);
		}
		//完成任务
		Map<String,Object> map = new HashMap<>();
		map.put("result", "true"); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//申请人获取推送消息
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("公车申请");
		weiEntity.setApplyMan(carApply.getApplyUserName());
		weiEntity.setApplyTime(sdf.format(new Date()));
		String mark = "已入库";
		UserEntity applyuser=bUserService.getUserByUserCode(carApply.getApplyUserName());
		String GzhOpenId ="";
		if(applyuser!=null){
			GzhOpenId =applyuser.getModifiedby();
		}
		String title = "您好,您有一个新的公车申请已经入库。";
		try {
			CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
//	*******************************************基本信息***************************************
	//获取车辆的基本信息(一级)
	@RequestMapping("getCarBase")
	@ResponseBody
	public List<BCarbaseinfo> getCarBase(){
		List<BCarbaseinfo> carList=bCarbaseinfoService.getAllCar();
		return carList;
	}
	//获取车辆的基本信息(二级)-----------id:车辆id
	@RequestMapping("getCarDetailInfo")
	@ResponseBody
	public BCarbaseinfo getCarDetailInfo(Integer id){
		BCarbaseinfo carInfo=bCarbaseinfoService.getCarInfoById(id);
		return carInfo;
	}
	//获取车辆的保险信息------------carNum:车牌号
	@RequestMapping("getCarInsurance")
	@ResponseBody
	public List<BInsurance> getCarInsurance(String carNum){
		//保险信息
		List<BInsurance> insureList=bInsuranceService.getByCarNum(carNum);
		return insureList;
	}
	//获取车辆的加油充值信息--------------id:车辆id
	@RequestMapping("getCarGasInfo")
	@ResponseBody
	public List<BCargasinfo> getCarGasInfo(Integer id){
		//加油信息
		List<BCargasinfo> cargasList=bCargasinfoService.getByCarId(id);
		return cargasList;
	}
//	*******************************************使用明细***************************************

	//获取开始信息-------------------------openId:微信认证编号
	@RequestMapping("getAllInfo")
	@ResponseBody
	public HashMap<String, Object> getAllInfo(String openId){
		HashMap<String, Object> hashMap = new HashMap<>();
		int userId=bUserService.getUserIdByOpenId(openId);
		//根据用户id获取用户的角色
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		int maxGrade=0;
		int maxRole=0;
		//获取用户最大级别的角色级别
		for (Integer integer : roleIdByUserId) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxGrade){
				maxGrade=gradeFromClass;
				maxRole=integer;
			}
		}
		String moduleNumByRole = bRoleService.getModuleNumByRole(maxRole);
		List<BDepartmentEntity> departList=bDepartmentService.getAllDepartment();
		List<BCarbaseinfoDTO> carOutList=new ArrayList<BCarbaseinfoDTO>();
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
				car.setPeasonnum(bCarbaseinfo.getPeasonnum());
				car.setSuspendday(bCarbaseinfo.getSuspendday());
				car.setCarNum(bCarbaseinfo.getCarnum());
				car.setTypenum(bCarbaseinfo.getCartype()+","+bCarbaseinfo.getCarnum());
				carOutList.add(car);
		}
		
		UserEntity userById = bUserService.getUserById(userId);
		//获取自己所属部门信息
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userId);
		List<BDepartmentEntity> ownDepartList=bDepartmentService.getOwnDepartment(departmentIdByUserId);
		//获取本部门所有员工信息
		List<UserEntity> alluser=new ArrayList<>();
		//获取所有员工
		List<UserEntity> all = bUserService.getAll();
		for (UserEntity userEntity : all) {
			//获取员工的所在部门
			List<Integer> departmentIdByUserId2 = bDepartmentService.getDepartmentIdByUserId(userEntity.getId());
			if(departmentIdByUserId.retainAll(departmentIdByUserId2)){
				alluser.add(userEntity);
			}
		}
		if(moduleNumByRole.equals("1*")){
			hashMap.put("car", carOutList);
			hashMap.put("user", userById);
			hashMap.put("userLable", "disable");
			hashMap.put("department", ownDepartList);
			hashMap.put("departmentLable", "disable");
		}else if("2*".equals(moduleNumByRole)){
			hashMap.put("car", carOutList);
//			hashMap.put("user", alluser);
			hashMap.put("user", userById);
			hashMap.put("userLable", "online");
			hashMap.put("department", ownDepartList);
			hashMap.put("departmentLable", "disable");
		}else if(moduleNumByRole.equals("3*")){
			hashMap.put("department", departList);
			hashMap.put("car", carOutList);
			hashMap.put("user", userById);
			hashMap.put("userLable", "online");
			hashMap.put("departmentLable", "online");
		}
        return hashMap;
	}
	//使用明细-------------------departpartmentId:部门编号，carNum：车牌号，applyMan：申请人账号，beginTime：起始时间，endTime：结束时间
	@RequestMapping("carUserDetailSearch")
	@ResponseBody
	public List<CarApplyInfo2> carUserDetailSearch(String departpartmentId,String carNum,String applyMan,String beginTime,String endTime) throws ParseException{
		String result="select * from b_carapplymanage where 1=1";//sql语句
		if(departpartmentId!=null && !"".equals(departpartmentId)){
			result+=" and DepartmentId ='"+departpartmentId+"' or DepartmentId like '%,"+departpartmentId+"%' or DepartmentId like '%,"+departpartmentId+",%' or DepartmentId like '%"+departpartmentId+",%'";
		}
		if(carNum!=null && !"".equals(carNum)){
			result+=" and CarCode ='"+carNum+"'";
		}
		if(applyMan!=null && !"".equals(applyMan)){
			result+=" and ApplyUserName like '%"+applyMan+"%' or ApplyMan like '%"+applyMan+"%'";
		}
		if(beginTime!=null && !"".equals(beginTime)){
			result+=" and ApplyTime >='"+beginTime+"'";
		}
		if(endTime!=null && !"".equals(endTime)){
			result+=" and ApplyTime <='"+endTime+"'";
		}
		List<CarApplyInfo2> carApply=bCarapplymanageService.getBySql(result);
       for (CarApplyInfo2 carApplyInfo2 : carApply) {
    	   BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carApplyInfo2.getCarCode());
			if(carInfoByNum!=null){
				String carUrl=carInfoByNum.getCarUrl();
				carApplyInfo2.setCarUrl(carUrl);
			}
	}
		return carApply;
	}
	//详细信息-------------------appForm.action?appId=?
//	*******************************************汇总分析***************************************
	//判断是否能显示该模块--------------------openId:微信认证编号
	@RequestMapping("ifDisplay")
	@ResponseBody
	public HashMap<String, Object> ifDisplay(String openId){
		HashMap<String, Object> hashMap = new HashMap<>();
		Integer userId=bUserService.getUserIdByOpenId(openId);
		int maxGrade=0;
		int maxRole=0;
		//根据用户id获取用户的角色
		List<Integer> roleIdByUserId = null;
		if(userId!=null){
			roleIdByUserId=bUserService.getRoleIdByUserId(userId);
			//获取用户最大级别的角色级别
			for (Integer integer : roleIdByUserId) {
				Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
				if(gradeFromClass!=null&&gradeFromClass>maxGrade){
					maxGrade=gradeFromClass;
					maxRole=integer;
				}
			}
		}
		String moduleNumByRole = bRoleService.getModuleNumByRole(maxRole);
		if(moduleNumByRole.equals("1*")){
			hashMap.put("permission", false);
		}else if(moduleNumByRole.equals("2*")){
			hashMap.put("permission", true);
			getAnalysisInfo(openId);
		}else if(moduleNumByRole.equals("3*")){
			hashMap.put("permission", true);
			getAnalysisInfo(openId);
		}
        return hashMap;
	}
	//获取汇总条件--------------------openId:微信认证编号
	@RequestMapping("getAnalysisInfo")
	@ResponseBody
	public HashMap<String, Object> getAnalysisInfo(String openId){
		HashMap<String, Object> hashMap = new HashMap<>();
		int userId=bUserService.getUserIdByOpenId(openId);
		//根据用户id获取用户的角色
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		int maxGrade=0;
		int maxRole=0;
		//获取用户最大级别的角色级别
		for (Integer integer : roleIdByUserId) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxGrade){
				maxGrade=gradeFromClass;
				maxRole=integer;
			}
		}
		String moduleNumByRole = bRoleService.getModuleNumByRole(maxRole);
		List<BDepartmentEntity> departList=bDepartmentService.getAllDepartment();
		List<BCarbaseinfoDTO> carOutList=new ArrayList<BCarbaseinfoDTO>();
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
				car.setPeasonnum(bCarbaseinfo.getPeasonnum());
				car.setSuspendday(bCarbaseinfo.getSuspendday());
				car.setCarNum(bCarbaseinfo.getCarnum());
				car.setTypenum(bCarbaseinfo.getCartype()+","+bCarbaseinfo.getCarnum());
				carOutList.add(car);
		}
		
		//获取自己所属部门信息
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userId);
		List<BDepartmentEntity> ownDepartList=bDepartmentService.getOwnDepartment(departmentIdByUserId);
		if(moduleNumByRole.equals("1*")){
			hashMap.put("permission", false);
		}else if(moduleNumByRole.equals("2*")){
			hashMap.put("permission", true);
			hashMap.put("car", carOutList);
			hashMap.put("department", ownDepartList);
			hashMap.put("departmentLable", "disable");
		}else if(moduleNumByRole.equals("3*")){
			hashMap.put("permission", true);
			hashMap.put("department", departList);
			hashMap.put("car", carOutList);
			hashMap.put("departmentLable", "online");
		}
        return hashMap;
	}
	//汇总分析-------------------departpartmentId:部门编号，carNum：车牌号，beginTime：起始时间，endTime：结束时间
	@RequestMapping("MetaAnalysis")
	@ResponseBody
	public HashMap<String, Object> MetaAnalysis(Integer departpartmentId,String carNum,String beginTime,String endTime) throws ParseException{
		HashMap<String, Object> hashMap = new HashMap<>();
		List<CarApplyDTO> analysis=new ArrayList<>();
		String result="select * from b_carapplymanage where 1=1";//sql语句
		if(departpartmentId!=null){
			result+=" and DepartmentId ='"+departpartmentId+"' or DepartmentId like '%,"+departpartmentId+"%' or DepartmentId like '%,"+departpartmentId+",%' or DepartmentId like '%"+departpartmentId+",%'";
		}
		if(carNum!=null && !"".equals(carNum)){
			result+=" and CarCode ='"+carNum+"'";
		}
		if(beginTime!=null && !"".equals(beginTime)){
			result+=" and ApplyTime >='"+beginTime+"'";
		}
		if(endTime!=null && !"".equals(endTime)){
			result+=" and ApplyTime <='"+endTime+"'";
		}
		Double count = 0.0;
		BigDecimal bd1 = new BigDecimal(Double.toString(count));
		 double doubleValue = 0 ;
		List<CarApplyInfo2> carApply=bCarapplymanageService.getBySql(result);
		for (CarApplyInfo2 carApplyInfo2 : carApply) {
			CarApplyDTO car=new CarApplyDTO();
			car.setCardistance(carApplyInfo2.getAccountLength());
			car.setCarNum(carApplyInfo2.getCarCode());
	         BigDecimal bd2 = new BigDecimal(Double.toString(carApplyInfo2.getAccountLength())); 
	         doubleValue=doubleValue+bd1.add(bd2).doubleValue();
			if(departpartmentId==null){
				String[] split = carApplyInfo2.getDepartment().split(",");
				car.setDepartment(split[0]);
			}else{
				BDepartmentEntity departmentIdById = bDepartmentService.getDepartmentIdById(departpartmentId);
				car.setDepartment(departmentIdById.getDepartmentname());
			}
			analysis.add(car);
		}
		hashMap.put("count", doubleValue);
		hashMap.put("sendInfo", analysis);
        return hashMap;
	}
	
	//提交申请并进行推送
	@RequestMapping("/sendApplyCarMessage")
	@ResponseBody
	public  AnccResult sendApplyCarMessage(String openId){
		Integer userIdByOpenId = bUserService.getUserIdByOpenId(openId);
		UserEntity userById = bUserService.getUserById(userIdByOpenId);
		Object selectNum = bCarapplymanageService.selectNum(userById.getCode());
		return AnccResult.ok(selectNum);
	}
	//公车查询
	@RequestMapping("/searchGCInfo")
	@ResponseBody
	public  Object searchGCInfo(CarApplyDTO carUseDetailDTO,String openId,Integer nowPage,Integer pageSize){
		Map<String,Object> minmap = new HashMap<>();
		List<CarApplyInfo2> list1 = new ArrayList<CarApplyInfo2>();
		String dep = carUseDetailDTO.getDepartment()+",";
//		String department = "%"+carUseDetailDTO.getDepartment()+"%";
		/*String applyman = "%"+carUseDetailDTO.getApplyman()+"%";*/
		String carcode = carUseDetailDTO.getCarinfo();
		String startTime = carUseDetailDTO.getStarttime();
		String endTime = "null";
		if(carUseDetailDTO.getEndtime()!=null&&!"null".equals(carUseDetailDTO.getEndtime())){
			endTime = carUseDetailDTO.getEndtime()+" 24:00:00";
		}
		//根据openID获取用户id
		Integer userid = bUserService.getUserIdByOpenId(openId);
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		//根据用户id获取用户所有角色id
		/*List<Integer> appRole = bUserService.getRoleIdByUserId(userid);
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}*/
		String applyMan = "";
//		null==carUseDetailDTO.getApplyman()||
		if("null".equals(carUseDetailDTO.getApplyman())){
			applyMan ="%"+ user.getCode()+"%";
		}else{
			applyMan ="%"+carUseDetailDTO.getApplyman()+"%";
		}
		
		
		int departmentId=departmentIdByUserId.get(0);
		String result="";
//		if(carUseDetailDTO==null){
//			result="select * from b_carapplymanage where Status=5 and state='已归还' and ApplyUserName='"+user.getCode()+"' and DepartmentId="+departmentId;
//		}else{
			result="select * from b_carapplymanage where Status=5 and state='已归还'";
			if(carUseDetailDTO.getDepartment()!=null&&!carUseDetailDTO.getDepartment().equals("全部")&&!carUseDetailDTO.getDepartment().equals("null")){
				result+=" and Department ='"+dep+"'";
			}
//			applyMan!=null&&
			if(carUseDetailDTO.getApplyman()!=null){
				if(!"".equals(carUseDetailDTO.getApplyman())&&!"null".equals(carUseDetailDTO.getApplyman())){
					result+=" and (ApplyUserName like '"+applyMan+"' or ApplyMan like '"+applyMan+"')";
				}
			}
			if(!"全部".equals(carcode)&&carcode!=null&&!"".equals(carcode)&&!"null".equals(carcode)){
				result+=" and CarCode='"+carcode+"'";
			}
			if(startTime!=null&&!"".equals(startTime)&&!"null".equals(startTime)){
				result+=" and ApplyTime>='"+startTime+"'";
			}
			if(endTime!=null&&!"".equals(endTime)&&!"null".equals(endTime)){
				result+=" and ApplyTime<='"+endTime+"'";
			}
//		}
			result+=" order by ApplyTime desc";
		list1 = bCarapplymanageService.getBySql(result);
		List<CarApplyInfo2> list = new ArrayList<CarApplyInfo2>();
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));//当前年
		for (CarApplyInfo2 carApplyInfo2 : list1) {
			if(carApplyInfo2.getApplyTime().contains(year)){
				carApplyInfo2.setTitle("公车申请");
				carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
				list.add(carApplyInfo2);
			}
		}
//		sort(list);
		int start = (nowPage - 1) * pageSize;
		int total = list.size();
		List<publicEntity> list_all = new ArrayList<>();
	    if (start + pageSize <= total)
	      list_all.addAll(list.subList(start, start + pageSize));
	    else
	      list_all.addAll(list.subList(start, total));
	    minmap.put("pagesize", list.size());
	    minmap.put("list", list_all);
		return AnccResult.ok(minmap);
	}
	//借用状态三级------------------------appId：申请id
//	appForm.action
	//查看个人本年度用车情况
	@RequestMapping("/userCommonCarCase")
	@ResponseBody
	public Object userCommonCarCase(String openId){
		Integer userid = bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userid);
		List<CarApplyInfo2> list = new ArrayList<CarApplyInfo2>();
		list = bCarapplymanageService.getFinishByAppUserName(user.getCode());
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));//当前年
		int countLength=0;
		int countApply=0;
		for (CarApplyInfo2 carApplyInfo2 : list) {
			if(carApplyInfo2.getApplyTime().contains(year)){
				countLength+=carApplyInfo2.getAccountLength();
				countApply+=1;
			}
		}
		Map<String, Object> caseMap = new HashMap<String, Object>();// 定义map
		caseMap.put("countLength", countLength);
		caseMap.put("countApply", countApply);
		return caseMap;
		}
	//出库入库的徽章
	@RequestMapping("/outInHandleMessage")
	@ResponseBody
	public  int outInHandleMessage(String openId,Integer nowPage,Integer pageSize,CarApplyDTO carUseDetailDTO){
		nowPage = 0;
		int count=0;
		int count1=0;
		int count2=0;
		pageSize = 0;
		if(openId!=null&&!openId.equals("")){
			AnccResult mainControcller1 = getInTask(openId,nowPage,pageSize,carUseDetailDTO);
			AnccResult mainControcller2 = getOutTask(openId,nowPage,pageSize,carUseDetailDTO);
			Object data1 = mainControcller1.getData();
			Object data2 = mainControcller2.getData();
			Iterator iter2 = ((Map<String, Object>) data2).entrySet().iterator();
			while (iter2.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter2.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if(key.toString().equals("pagesize")){
					count2=(int) val;
				}
			}
			Iterator iter1 = ((Map<String, Object>) data1).entrySet().iterator();
			while (iter1.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter1.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
//			if(!val.toString().equals("[]")){
//				count1=Integer.parseInt(val.toString());
//			}
			if("pagesize".equals(key.toString())){
				count1=(int) val;
			}
		}
	
		count=count1+count2;
		}
		return count;
	}
//	*******************************************后台管理系统***************************************
	//公车使用信息
	//获取所有申请信息
	@RequestMapping("/getAllApplyInfo")
	@ResponseBody
	public Object getAllApplyInfo(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
//		int intPage = 1;// 页数
//		int number =15;
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<CarApplyInfo2> list = new ArrayList<CarApplyInfo2>();
		list = bCarapplymanageService.getAll(start, number);
		for (CarApplyInfo2 carApplyInfo2 : list) {
			carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bCarapplymanageService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//加载所有车辆信息-----------getCarBase.action
	//查询
	@RequestMapping("/searchCommonCarInfo")
	@ResponseBody
	public Object carDisplayByPage(HttpServletRequest request,CarApplyDTO carUseDetailDTO){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<CarApplyInfo2> list = new ArrayList<CarApplyInfo2>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		
		String countResult="select count(*) from b_carapplymanage where Status>2 and state='已归还'";
		String result="select * from b_carapplymanage where Status>2 and state='已归还' ";
		String dep = carUseDetailDTO.getDepartment();
		String department = "%"+carUseDetailDTO.getDepartment()+"%";
		String applyman = "%"+carUseDetailDTO.getApplyman()+"%";
		String carcode = carUseDetailDTO.getCarinfo();
		String startTime = carUseDetailDTO.getStarttime();
		String endTime = carUseDetailDTO.getEndtime();
		if(!dep.equals("全部")){
			countResult+=" and Department like '"+department+"'";
			result+=" and Department like '"+department+"'";
		}
		if(carUseDetailDTO.getApplyman()!=null&&!"".equals(carUseDetailDTO.getApplyman())){
			countResult+=" and ApplyUserName like '"+applyman+"' or ApplyMan like '"+applyman+"'";
			result+=" and ApplyUserName like '"+applyman+"' or ApplyMan like '"+applyman+"'";
		}
		if(!carcode.equals("全部")){
			countResult+=" and CarCode='"+carcode+"'";
			result+=" and CarCode='"+carcode+"'";
		}
		if(startTime!=null&&!"".equals(startTime)){
			countResult+=" and ApplyTime>='"+startTime+"'";
			result+=" and ApplyTime>='"+startTime+"'";
		}
		if(endTime!=null&&!"".equals(endTime)){
			countResult+=" and ApplyTime<='"+endTime+"'";
			result+=" and ApplyTime<='"+endTime+"'";
		}
		result+=" limit "+start+","+number;
		list = bCarapplymanageService.getBySql(result);
		for (CarApplyInfo2 carApplyInfo2 : list) {
			carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
		}
		int total=bCarapplymanageService.getCountBySQL(countResult);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//导出表单
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
		String fileName = "公车使用信息" + date + ".xlsx";
		String filePath = path + "/" + fileName;
		int flag = bCarapplymanageService.export(nlist, filePath);
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

	//显示所有申请公车已完成信息（转换）
	@RequestMapping("/showApplyCommonCar")
	@ResponseBody
	public ModelAndView showApplyCommonCar(BCarbaseinfo car){
		ModelAndView result=new ModelAndView("commonCar/commonCar");
		return result;
	}
	//公车申请信息
	//获取所有公车申请进行中业务信息
	@RequestMapping("/getAllRunTimeInfo")
	@ResponseBody
	public Object getAllRunTimeInfo(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<CarApplyInfo2> list = new ArrayList<CarApplyInfo2>();
		list = bCarapplymanageService.getAllRunTimeInfo(start, number);
		for (CarApplyInfo2 carApplyInfo2 : list) {
			carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bCarapplymanageService.getAllRunTimeCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//显示所有申请公车已完成信息（转换）
	@RequestMapping("/showRunTimeInfo")
	@ResponseBody
	public ModelAndView showRunTimeInfo(){
		ModelAndView result=new ModelAndView("commonCar/carApplyInfo");
		return result;
	}
}
