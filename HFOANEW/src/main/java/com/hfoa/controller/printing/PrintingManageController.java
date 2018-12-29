package com.hfoa.controller.printing;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.common.AnccResult;
import com.hfoa.dao.entertain.EntertainobjecttypeMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainobjecttype;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.entity.printing.BGzkind;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.entertain.EntertainService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.printing.BGzapplyinfoService;
import com.hfoa.service.printing.BGzkindService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.BUserDepartment;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.ListSortUtil;
import com.hfoa.util.WorkflowUtil;

/**
 * 
 * @author wzx
 * 用印管理
 */
@Controller
@RequestMapping("print")
public class PrintingManageController {
	@Autowired
	private WorkflowUtil workflowUtil;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private USerService bUserService;
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	@Autowired
    private DictManage dictManage;
	@Autowired
    private BGzapplyinfoService bGzapplyinfoService;
	@Autowired
    private BGzkindService bGzkindService;
	@Autowired
    private BProcessService bProcessService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private EntertainService entertainService;
	@Autowired
	private EntertainobjecttypeMapper entertainobjecttypeMapper;
//	*******************************************导数据***************************************
	//申请
	@RequestMapping("apply")
	@ResponseBody
	public void apply(){
		List<BGzapplyinfo> applyLIst=bGzapplyinfoService.getAllPass();
		for (BGzapplyinfo bGzapplyinfo : applyLIst) {
			UserEntity user=bUserService.getUserByUserCode(bGzapplyinfo.getApplyusername());
			Integer departmentId=bDepartmentService.getDepartmentIdByUserId(user.getId()).get(0);
			int applyId=bGzapplyinfo.getId();
			//开启流程实例
			Map<String,Object> activitiMap = new HashMap<>();
			if(departmentId!=null){
				activitiMap.put("user", departmentId);
			}
			String objId="printApply:"+applyId;
			identityService.setAuthenticatedUserId(user.getCode());
			runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
			//根据流程定义id和节点id查询任务Id
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			int i=0;
			for(;i<bids.size();i++){
				int id=Integer.parseInt(bids.get(i));
				if(id==applyId)
					break;
			}
			String taskId=tasks.get(i).getId();
			completeApplyTask(taskId,"0", "通过");
		}
	}
	//跳转
	@RequestMapping("jump")
	@ResponseBody
	public void jump(String taskId){
		workflowUtil.JumpEndActivity(taskId, "yycl",null); 
	}
	//获取处理
	@RequestMapping("handle")
	@ResponseBody
	public Object handle(){
	//用印处理节点
	String pointId="yycl";
	//根据节点的名称获和用户名称取任务
	List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
	//根据任务获取业务
	Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
	List<BGzapplyinfo> applyinfoList= bGzapplyinfoService.findTasksByStatus(busAndTaskId.keySet(),"通过");
	
		return applyinfoList;
	}
//	*******************************************用印申请***************************************
	//部署流程实例
	@RequestMapping(value="/deployment",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String deployment(){
		workflowUtil.deployment("activiti/printingApply", "申请用印");
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
	 public List<Entertainobjecttype> removeDuplicate1(List<Entertainobjecttype> list) {
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
		List<String> unitList=new ArrayList<>();
		List<Entertainobjecttype> type = entertainService.getType();
		List<Entertainobjecttype> realList=removeDuplicate1(type);
		for (Entertainobjecttype entertainobjecttype : realList) {
			unitList.add(entertainobjecttype.getoName());
		}
		int userId=bUserService.getUserIdByOpenId(openId);
		//判断用户级别
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取部门经理的用户
		//获取用户部门id
		List<UserEntity> allUser=new ArrayList<>();
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
		for (BUserDepartment integer : userIdList) {
			UserEntity u=bUserService.getUserById(integer.getUserid());
			if(u!=null){
				allUser.add(u);
			}
		}
		String modulenum=bRoleService.getModuleNumByRole(maxAppRole);
		//根据公共类别查询数量
		List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
		int manageGrade=0;
		int manageRole=0;
		if(countlist.size()>1){
			for (Integer integer : countlist) {
				int grade=bRoleService.getGradeFromClass(integer);
				if(grade>manageGrade){
					manageGrade=grade;
					manageRole=integer;
				}
			}
		}
		
		Map<String,Object> map=new HashMap<>();
		UserEntity u=bUserService.getUserById(userId);
		List<UserEntity> approvalMan = dynamicGetRoleUtil.getApprovalMan(userId);
		List<UserEntity> leaderman = dynamicGetRoleUtil.getApprovalMan(userId);
		//获取邓主任信息
		BProcess proce=bProcessService.getprocessByName("部门经理审批");
		List<Integer> roleIdes=bRoleService.getRoleByProcessId(proce.getId());
		//判断是否是特别申请人
		BProcess pro=bProcessService.getprocessByName("用印申请");
		List<Integer> roleids=bRoleService.getRoleByProcessId(pro.getId());
		roleids.retainAll(appRole);
		if(roleids.size()>0){
			TreeSet<Integer> userIdByRoleId = new TreeSet<Integer>(); 
			List <Integer> userList=new ArrayList<>();
			for (Integer integer : roleIdes) {
				userList.addAll(bUserService.getUserIdByRoleId(integer));
			}
			UserEntity userById =null;
			for (Integer integer : userList) {
				userIdByRoleId.add(integer);
			}
			for (Integer integer : userIdByRoleId) {
				userById = bUserService.getUserById(integer);
				if(userById!=null){
					leaderman.add(userById);
				}
			}
		}
		
		List<BGzkind> gzlist=getPrintingType();
		List<UserEntity> approvalMan1 =new ArrayList<>();
		List<UserEntity> approvalMan2 =new ArrayList<>();
		if(maxAppGrade==1){//普通员工
			approvalMan=removeDuplicate(approvalMan);
			leaderman=removeDuplicate(leaderman);
			map.put("user", u);
			map.put("approvalMan0", leaderman);
			map.put("approvalMan1", approvalMan);
			map.put("gzkind", gzlist);
			map.put("approvalCount", 3);
			map.put("ifSelect", true);//判断是否要选择审批人
			map.put("grade", maxAppGrade);
			map.put("unit", unitList);
		}else if(maxAppGrade==2){
			approvalMan=removeDuplicate(approvalMan);
			leaderman=removeDuplicate(leaderman);
			map.put("user", u);
			map.put("approvalMan0", leaderman);
			map.put("approvalMan1", approvalMan);
			for (UserEntity user : approvalMan) {
//				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(user.getId());
//				if(roleIdByUserId.contains(manageRole)){
//					approvalMan1.add(user);
//				}
				if(user!=null&&user.getWorkgroupid()!=null&&!"".equals(user.getWorkgroupid())){
					UserEntity leader=bUserService.getUserById(Integer.parseInt(user.getWorkgroupid()));
					approvalMan1.add(leader);
				}
			}
			map.put("approvalMan2", approvalMan1);
//			for (UserEntity userEntity : approvalMan1) {
//				if(userEntity.getWorkgroupid()!=null&&!"".equals(userEntity.getWorkgroupid())){
//					UserEntity leader=bUserService.getUserById(Integer.parseInt(userEntity.getWorkgroupid()));
//					approvalMan2.add(leader);
//				}
//				
//			}
			int leaderGrade = bRoleService.getLeaderGrade();
			int leaderRole = bRoleService.getLeaderRole(leaderGrade);
			List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(leaderRole);
			List<UserEntity> allMananger = new ArrayList<>();
			for (Integer userid : userIdByRoleId) {
				UserEntity userById = bUserService.getUserById(userid);
				allMananger.add(userById);
			}
			//三级审批审批人必须是总经理
			BProcess process=bProcessService.getprocessByName("三级审批（用印）");
			int processId=process.getId();
			for (UserEntity userEntity : allMananger) {
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
				List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
				roleIds.retainAll(roleIdByUserId);
				if(roleIds.size()>0){
					approvalMan2.add(userEntity);
				}
			}
			map.put("approvalMan3", approvalMan2);
			map.put("gzkind", gzlist);
			map.put("approvalCount", 3);
			map.put("ifSelect", false);//判断是否要选择审批人
			map.put("grade", maxAppGrade);
			map.put("unit", unitList);
		}else if(maxAppGrade==3){
			approvalMan=removeDuplicate(approvalMan);
			leaderman=removeDuplicate(leaderman);
			map.put("approvalMan0", leaderman);
			map.put("user", u);
			map.put("approvalMan1", approvalMan);
//			for (UserEntity userEntity : approvalMan) {
//				if(userEntity.getWorkgroupid()!=null&&!"".equals(userEntity.getWorkgroupid())){
//					UserEntity leader=bUserService.getUserById(Integer.parseInt(userEntity.getWorkgroupid()));
//					approvalMan1.add(leader);
//				}
//				
//			}
			//三级审批审批人必须是总经理
			int leaderGrade = bRoleService.getLeaderGrade();
			int leaderRole = bRoleService.getLeaderRole(leaderGrade);
			List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(leaderRole);
			List<UserEntity> allMananger = new ArrayList<>();
			for (Integer userid : userIdByRoleId) {
				UserEntity userById = bUserService.getUserById(userid);
				allMananger.add(userById);
			}
			BProcess process=bProcessService.getprocessByName("三级审批（用印）");
			int processId=process.getId();
			for (UserEntity userEntity : allMananger) {
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
				List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
				roleIds.retainAll(roleIdByUserId);
				if(roleIds.size()>0){
					approvalMan1.add(userEntity);
				}
			}
			map.put("approvalMan2", approvalMan1);
			map.put("gzkind", gzlist);
			map.put("approvalCount", 2);
			map.put("ifSelect", false);//判断是否要选择审批人
			map.put("grade", maxAppGrade);
			map.put("unit", unitList);
		}else if(maxAppGrade==4){
			map.put("user", u);
			int leaderGrade = bRoleService.getLeaderGrade();
			int leaderRole = bRoleService.getLeaderRole(leaderGrade);
			List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(leaderRole);
			List<UserEntity> allMananger = new ArrayList<>();
			for (Integer userid : userIdByRoleId) {
				UserEntity userById = bUserService.getUserById(userid);
				allMananger.add(userById);
			}
			BProcess process=bProcessService.getprocessByName("三级审批（用印）");
			int processId=process.getId();
			for (UserEntity userEntity : allMananger) {
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
				List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
				roleIds.retainAll(roleIdByUserId);
				if(roleIds.size()>0){
					approvalMan1.add(userEntity);
				}
			}
			map.put("approvalMan1", approvalMan1);
			
			leaderman=approvalMan1;
			map.put("approvalMan0", leaderman);
			map.put("gzkind", gzlist);
			map.put("approvalCount", 1);
			map.put("ifSelect", false);//判断是否要选择审批人
			map.put("grade", maxAppGrade);
			map.put("unit", unitList);
		}
		return map;
}
	//根据选中人获取相应的审批人(部门经理及以上级别)----------userId:用户id
	@RequestMapping("getNextAppriovalMan")
	@ResponseBody
	public Object getNextAppriovalMan(int userId){
		//申请人信息
		UserEntity user=bUserService.getUserById(userId);
		Map<String,Object> map=new HashMap<>();
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		int departmentId=bDepartmentService.getDepartmentIdByUserId(user.getId()).get(0);
		List<UserEntity> allUserByDepartId = bUserService.getAllUserByDepartId(departmentId);
		
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		if(maxAppGrade==2){
			for (UserEntity userEntity : allUserByDepartId) {
				List<Integer> roleByGradeAndMod = bRoleService.getRoleByGradeAndMod("2*", 2);
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
				roleByGradeAndMod.retainAll(roleIdByUserId);
				if(roleByGradeAndMod.size()>0){
					user=userEntity;
				}
		}
		}
		List<UserEntity> approvalMan =new ArrayList<>();
		if(!"".equals(user.getWorkgroupid())&&user.getWorkgroupid()!=null){
			UserEntity businessMan=bUserService.getUserById(Integer.parseInt(user.getWorkgroupid()));
			approvalMan.add(businessMan);
		}
		List<UserEntity> approvalMan1=new ArrayList<>();
		int leaderGrade = bRoleService.getLeaderGrade();
		int leaderRole = bRoleService.getLeaderRole(leaderGrade);
		List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(leaderRole);
		List<UserEntity> allMananger = new ArrayList<>();
		for (Integer userid : userIdByRoleId) {
			UserEntity userById = bUserService.getUserById(userid);
			allMananger.add(userById);
		}
		//三级审批审批人必须是总经理
		BProcess process=bProcessService.getprocessByName("三级审批（用印）");
		int processId=process.getId();
		
		for (UserEntity userEntity : allMananger) {
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			roleIds.retainAll(roleIdByUserId);
			if(roleIds.size()>0){
				approvalMan1.add(userEntity);
			}
		}
		map.put("approvalMan2", approvalMan);
		map.put("approvalMan3", approvalMan1);
		return map;
	}
	//获取综合办公室主任
	@RequestMapping("getComOfficeLeader")
	@ResponseBody
	public Object getComOfficeLeader(){
		Map<String,Object> map=new HashMap<>();
		List<UserEntity> approvalMan =new ArrayList<>();
		List<UserEntity> approvalMan1=new ArrayList<>();
		List<UserEntity> user=bUserService.getAll();
		//三级审批审批人是综合办公室主任---身份证复印件
		BProcess process=bProcessService.getprocessByName("二级审批（用印）");
		int processId=process.getId();
		
		for (UserEntity userEntity : user) {
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			roleIds.retainAll(roleIdByUserId);
			if(roleIds.size()>0){
				approvalMan1.add(userEntity);
				approvalMan.add(userEntity);
			}
		}
		map.put("approvalMan2", approvalMan);
		map.put("approvalMan3", approvalMan1);
		return map;
	}
	//获取总经理
	@RequestMapping("getManager")
	@ResponseBody
	public Object getManager(){
		Map<String,Object> map=new HashMap<>();
		List<UserEntity> approvalMan =new ArrayList<>();
		List<UserEntity> user=bUserService.getAll();
		//三级审批审批人必须是总经理
		BProcess process=bProcessService.getprocessByName("三级审批（用印）");
		int processId=process.getId();
		
		for (UserEntity userEntity : user) {
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			roleIds.retainAll(roleIdByUserId);
			if(roleIds.size()>0){
				approvalMan.add(userEntity);
			}
		}
		map.put("approvalMan1", approvalMan);
		return map;
	}
	//获取合同管理员
	@RequestMapping("getApprovalmanForHT")
	@ResponseBody
	public Object getApprovalmanForHT(String openId){
		int userId=bUserService.getUserIdByOpenId(openId);
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		
		List<UserEntity> approvalMan = dynamicGetRoleUtil.getApprovalMan(userId);
		Map<String,Object> map=new HashMap<>();
		if(maxAppGrade==1){
			List<UserEntity> user=bUserService.getAll();
			BProcess process=bProcessService.getprocessByName("合同专用章审批");
			int processId=process.getId();
			
			for (UserEntity userEntity : user) {
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userEntity.getId());
				List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
				roleIds.retainAll(roleIdByUserId);
				if(roleIds.size()>0){
					if(userEntity.getId()!=userId){
						approvalMan.add(userEntity);
					}
				}
			}
		}
		approvalMan=removeDuplicate(approvalMan);
		map.put("approvalMan1", approvalMan);
		return map;
	}
	//获取公章类型
	@RequestMapping("getPrintingType")
	@ResponseBody
	public List<BGzkind> getPrintingType(){
		List<BGzkind> allGzKind = bGzkindService.getAllGzKind();
//		ListSortUtil<BGzkind> sortList = new ListSortUtil<BGzkind>();
//		sortList.sort(allGzKind, "id", "asc");
		return allGzKind;
	}

	//获取用印处理人(目前一个)
	public List<Integer> getHandleMan(){
		//获取出库的角色
		BProcess process=bProcessService.getprocessByName("用印处理");
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		//目前用印处理人只有一个
//		List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(roleIds.get(0));
//		UserEntity handleuser=bUserService.getUserById(userIdByRoleId.get(0));
		return roleIds;
	}
	//提交申请，开启用印流程-------------applyinfo：业务信息
	@RequestMapping("startApply")
	@ResponseBody
	public String startApply(BGzapplyinfo applyinfo){
		Set<String> unitList=new HashSet<>();
		List<Entertainobjecttype> type = entertainService.getType();
		for (Entertainobjecttype entertainobjecttype : type) {
			unitList.add(entertainobjecttype.getoName());
		}
		Entertainobjecttype enter=new Entertainobjecttype();
		if(!unitList.contains(applyinfo.getSendto())){
			//将新录入的发往单位添加
//			Entertainobjecttype entertainob = new Entertainobjecttype();
//			enter.setId(entertainobjecttypeMapper.selectMax().getId()+1);
//			enter.setoName(applyinfo.getSendto());
			entertainobjecttypeMapper.insertOName(applyinfo.getSendto());
		}
		//根据申请人账号获取申请人信息
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		int departmentId=bDepartmentService.getDepartmentIdByUserId(user.getId()).get(0);
//		applyinfo.setGzkind("1,4,");//公章类型
		//判断用户级别
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(user.getId());
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//真实获取
		//获取所有公章中最小的级别
		int minGrade=bGzkindService.getMinGrade();
		
		//测试数据
		//若是传过来多种类型的公章
		String[] gztype = applyinfo.getGzkind().split(",");//传公章id
		for(int j=0;j<gztype.length;j++){
			BGzkind kind=bGzkindService.getById(Integer.parseInt(gztype[j]));
			if(kind.getGrade()==1){
				BGzapplyinfo applyinfo1=new BGzapplyinfo();
				applyinfo1.setApplyusername(applyinfo.getApplyusername());//申请人账号
				applyinfo1.setDepartment(applyinfo.getDepartment());//部门名称
				applyinfo1.setGzkind(kind.getGzkind());//公章类型
				applyinfo1.setApproveman(applyinfo.getApproveman());//审批人账号
				applyinfo1.setCopies(applyinfo.getCopies());//份数
				applyinfo1.setSendto(applyinfo.getSendto());//发往单位
				applyinfo1.setIssecret(applyinfo.getIssecret());//是否涉密
				applyinfo1.setApplytime(applyinfo.getApplytime());//申请时间
				applyinfo1.setReason(applyinfo.getReason());//申请事由
//				applyinfo1.setHandleman(getHandleMan());//处理人
				applyinfo1.setMaxgarde(kind.getGrade());
				applyinfo1.setDepartmentid(applyinfo.getDepartmentid());//部门id
				applyinfo1.setGzId(Integer.parseInt(gztype[j]));//公章id
				if(applyinfo.getContracAmount()>0){
					applyinfo1.setContracAmount(applyinfo.getContracAmount());
					if(applyinfo.getContracAmount()>=50&&"甲方".equals(applyinfo.getContractType())){
						applyinfo1.setBusinessManager(applyinfo.getBusinessManager());
						applyinfo1.setConfirmman(applyinfo.getConfirmman());
						applyinfo1.setContractType(applyinfo.getContractType());
					}
				}
				//判断法人章
				if(Integer.parseInt(gztype[j])==5){
					Object manager = getManager();
					Iterator iter = ((Map<String, Object>) manager).entrySet().iterator();
					while (iter.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						List<UserEntity> val = (List<UserEntity>) entry.getValue();
						if(key.toString().equals("approvalMan1")){
							applyinfo1.setApproveman(val.get(0).getCode());//业务主管审批人
						}
						
					}
				}
				bGzapplyinfoService.insert(applyinfo1);
				int applyId=applyinfo1.getId();
				//开启流程实例
				Map<String,Object> activitiMap = new HashMap<>();
				activitiMap.put("user", departmentId);
				String objId="printApply:"+applyId;
				identityService.setAuthenticatedUserId(user.getCode());
				runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
				//根据流程定义id和节点id查询任务Id
				List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
				List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
				int i=0;
				for(;i<bids.size();i++){
					int id=Integer.parseInt(bids.get(i));
					if(id==applyId)
						break;
				}
				String taskId=tasks.get(i).getId();
				System.out.println("***流程开启1***");
				//完成任务
				if(applyinfo.getContracAmount()>=50&&"甲方".equals(applyinfo.getContractType())){
					if(maxAppGrade==1||maxAppGrade==2){//普通员工
					completeApplyTask(taskId,"1", "待审批");
					}
					if(maxAppGrade==3){
						completeApplyTask(taskId,"2", "待审批");
					}
					if(maxAppGrade==4){
						completeApplyTask(taskId,"3", "待审批");
					}
				}else{
					completeApplyTask(taskId,"0", "待审批");
				}
				
				//公众号消息推送
				String GzhOpenId = "";
				UserEntity userEntity = bUserService.getUserByUserCode(applyinfo1.getApproveman());
				if(userEntity!=null){
					GzhOpenId = userEntity.getModifiedby();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
				String mark = "待审批";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setTitle("用印申请");
				weiEntity.setApplyMan(applyinfo1.getApplyusername());
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
			if(kind.getGrade()==2){
				//判断是法人身份证复印件
				if(Integer.parseInt(gztype[j])==6){
					BGzapplyinfo applyinfofrfyj=new BGzapplyinfo();
					Object comOfficeLeader = getComOfficeLeader();
					Iterator iter = ((Map<String, Object>) comOfficeLeader).entrySet().iterator();
					while (iter.hasNext()) {
						@SuppressWarnings("rawtypes")
						Map.Entry entry = (Map.Entry) iter.next();
						Object key = entry.getKey();
						List<UserEntity> val = (List<UserEntity>) entry.getValue();
						if(key.toString().equals("approvalMan2")){
							applyinfofrfyj.setBusinessManager(val.get(0).getCode());//业务主管审批人
						}
						if(key.toString().equals("approvalMan3")){
							applyinfofrfyj.setConfirmman(val.get(0).getCode());//确认人
						}
					}
					applyinfofrfyj.setApplyusername(applyinfo.getApplyusername());//申请人账号
					applyinfofrfyj.setDepartment(applyinfo.getDepartment());//部门名称
					applyinfofrfyj.setDepartmentid(applyinfo.getDepartmentid());//部门id
					applyinfofrfyj.setGzkind(kind.getGzkind());//公章类型
					applyinfofrfyj.setApproveman(applyinfo.getApproveman());//审批人账号
					applyinfofrfyj.setCopies(applyinfo.getCopies());//份数
					applyinfofrfyj.setSendto(applyinfo.getSendto());//发往单位
					applyinfofrfyj.setIssecret(applyinfo.getIssecret());//是否涉密
					applyinfofrfyj.setApplytime(applyinfo.getApplytime());//申请时间
					applyinfofrfyj.setReason(applyinfo.getReason());//申请事由
//					applyinfo2.setHandleman(getHandleMan());//处理人
					applyinfofrfyj.setMaxgarde(kind.getGrade());
					applyinfofrfyj.setGzId(Integer.parseInt(gztype[j]));//公章id
					bGzapplyinfoService.insert(applyinfofrfyj);
					int applyId = applyinfofrfyj.getId();
					//开启流程实例
					Map<String,Object> activitiMap = new HashMap<>();
					activitiMap.put("user", departmentId);
					String objId="printApply:"+applyId;
					identityService.setAuthenticatedUserId(user.getCode());
					runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
					//根据流程定义id和节点id查询任务Id
					List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					int i=0;
					for(;i<bids.size();i++){
						int id=Integer.parseInt(bids.get(i));
						if(id==applyId)
							break;
					}
					String taskId=tasks.get(i).getId();
					System.out.println("***法人身份证复印件流程开启***");
					//完成任务
					completeApplyTask(taskId,"1", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfofrfyj.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfofrfyj.getApplyusername());
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
				}else{
					if(maxAppGrade==1||maxAppGrade==2){//普通员工 副经理
						BGzapplyinfo applyinfo2=new BGzapplyinfo();
						applyinfo2.setApplyusername(applyinfo.getApplyusername());//申请人账号
						applyinfo2.setDepartment(applyinfo.getDepartment());//部门名称
						applyinfo2.setDepartmentid(applyinfo.getDepartmentid());//部门id
						applyinfo2.setGzkind(kind.getGzkind());//公章类型
						applyinfo2.setApproveman(applyinfo.getApproveman());//审批人账号
						applyinfo2.setCopies(applyinfo.getCopies());//份数
						applyinfo2.setSendto(applyinfo.getSendto());//发往单位
						applyinfo2.setIssecret(applyinfo.getIssecret());//是否涉密
						applyinfo2.setApplytime(applyinfo.getApplytime());//申请时间
						applyinfo2.setReason(applyinfo.getReason());//申请事由
//						applyinfo2.setHandleman(getHandleMan());//处理人
						applyinfo2.setMaxgarde(kind.getGrade());
						applyinfo2.setGzId(Integer.parseInt(gztype[j]));//公章id
						applyinfo2.setConfirmman(applyinfo.getConfirmman());//确认人
						applyinfo2.setBusinessManager(applyinfo.getBusinessManager());//业务主管审批人
						if(kind.getGzkind().contains("原件")||kind.getGzkind().contains("公章外带")){
							applyinfo2.setBorrowTime(applyinfo.getBorrowTime());
							applyinfo2.setReturnTime(applyinfo.getReturnTime());
						}
						bGzapplyinfoService.insert(applyinfo2);
						int applyId = applyinfo2.getId();
						//开启流程实例
						Map<String,Object> activitiMap = new HashMap<>();
						activitiMap.put("user", departmentId);
						String objId="printApply:"+applyId;
						identityService.setAuthenticatedUserId(user.getCode());
						runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
						//根据流程定义id和节点id查询任务Id
						List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
						List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
						int i=0;
						for(;i<bids.size();i++){
							int id=Integer.parseInt(bids.get(i));
							if(id==applyId)
								break;
						}
						String taskId=tasks.get(i).getId();
						System.out.println("***流程开启2***");
						//完成任务
						completeApplyTask(taskId,"1", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo2.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo2.getApplyusername());
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
					if(maxAppGrade==3){//部门经理
						BGzapplyinfo applyinfo3=new BGzapplyinfo();
						applyinfo3.setApplyusername(applyinfo.getApplyusername());//申请人账号
						applyinfo3.setDepartment(applyinfo.getDepartment());//部门名称
						applyinfo3.setDepartmentid(applyinfo.getDepartmentid());//部门id
						applyinfo3.setGzkind(kind.getGzkind());//公章类型
						applyinfo3.setCopies(applyinfo.getCopies());//份数
						applyinfo3.setSendto(applyinfo.getSendto());//发往单位
						applyinfo3.setIssecret(applyinfo.getIssecret());//是否涉密
						applyinfo3.setApplytime(applyinfo.getApplytime());//申请时间
						applyinfo3.setReason(applyinfo.getReason());//申请事由
						applyinfo3.setGzId(Integer.parseInt(gztype[j]));//公章id
//						applyinfo3.setHandleman(getHandleMan());//处理人
						applyinfo3.setMaxgarde(kind.getGrade());
						applyinfo3.setConfirmman(applyinfo.getBusinessManager());//确认人
						applyinfo3.setBusinessManager(applyinfo.getApproveman());//业务主管审批人
						if(kind.getGzkind().contains("原件")||kind.getGzkind().contains("公章外带")){
							applyinfo3.setBorrowTime(applyinfo.getBorrowTime());
							applyinfo3.setReturnTime(applyinfo.getReturnTime());
						}
						
						bGzapplyinfoService.insert(applyinfo3);
						int applyId = applyinfo3.getId();
						//开启流程实例
						Map<String,Object> activitiMap = new HashMap<>();
						activitiMap.put("user", departmentId);
						String objId="printApply:"+applyId;
						identityService.setAuthenticatedUserId(user.getCode());
						runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
						//根据流程定义id和节点id查询任务Id
						List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
						List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
						int i=0;
						for(;i<bids.size();i++){
							int id=Integer.parseInt(bids.get(i));
							if(id==applyId)
								break;
						}
						String taskId=tasks.get(i).getId();
						System.out.println("***流程开启3***");
						//完成任务
						completeApplyTask(taskId,"2", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo3.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo3.getApplyusername());
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
					if(maxAppGrade==4){//领导
						BGzapplyinfo applyinfo4=new BGzapplyinfo();
						applyinfo4.setApplyusername(applyinfo.getApplyusername());//申请人账号
						applyinfo4.setDepartment(applyinfo.getDepartment());//部门名称
						applyinfo4.setDepartmentid(applyinfo.getDepartmentid());//部门id
						applyinfo4.setGzkind(kind.getGzkind());//公章类型
						applyinfo4.setCopies(applyinfo.getCopies());//份数
						applyinfo4.setSendto(applyinfo.getSendto());//发往单位
						applyinfo4.setIssecret(applyinfo.getIssecret());//是否涉密
						applyinfo4.setApplytime(applyinfo.getApplytime());//申请时间
						applyinfo4.setReason(applyinfo.getReason());//申请事由
//						applyinfo4.setHandleman(getHandleMan());//处理人
						applyinfo4.setConfirmman(applyinfo.getApproveman());//确认人
						applyinfo4.setGzId(Integer.parseInt(gztype[j]));//公章id
						applyinfo4.setMaxgarde(kind.getGrade());
						if(kind.getGzkind().contains("原件")||kind.getGzkind().contains("公章外带")){
							applyinfo4.setBorrowTime(applyinfo.getBorrowTime());
							applyinfo4.setReturnTime(applyinfo.getReturnTime());
						}
						bGzapplyinfoService.insert(applyinfo4);
						int applyId = applyinfo4.getId();
						//开启流程实例
						Map<String,Object> activitiMap = new HashMap<>();
						activitiMap.put("user", departmentId);
						String objId="printApply:"+applyId;
						identityService.setAuthenticatedUserId(user.getCode());
						runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
						//根据流程定义id和节点id查询任务Id
						List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
						List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
						int i=0;
						for(;i<bids.size();i++){
							int id=Integer.parseInt(bids.get(i));
							if(id==applyId)
								break;
						}
						String taskId=tasks.get(i).getId();
						System.out.println("***流程开启4***");
						//完成任务
						completeApplyTask(taskId,"3", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo4.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo4.getApplyusername());
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
				}
			}
			if(kind.getGrade()==3){
				if(maxAppGrade==1||maxAppGrade==2){//普通员工    副经理
					BGzapplyinfo applyinfo5=new BGzapplyinfo();
					applyinfo5.setApplyusername(applyinfo.getApplyusername());//申请人账号
					applyinfo5.setDepartment(applyinfo.getDepartment());//部门名称
					applyinfo5.setDepartmentid(applyinfo.getDepartmentid());//部门id
					applyinfo5.setGzkind(kind.getGzkind());//公章类型
					applyinfo5.setApproveman(applyinfo.getApproveman());//审批人账号
					applyinfo5.setCopies(applyinfo.getCopies());//份数
					applyinfo5.setSendto(applyinfo.getSendto());//发往单位
					applyinfo5.setIssecret(applyinfo.getIssecret());//是否涉密
					applyinfo5.setApplytime(applyinfo.getApplytime());//申请时间
					applyinfo5.setReason(applyinfo.getReason());//申请事由
					applyinfo5.setEntrustedman(applyinfo.getEntrustedman());//受托人
					applyinfo5.setEntrustedpost(applyinfo.getEntrustedpost());//受托人职务
					applyinfo5.setEntrustedcardtype(applyinfo.getEntrustedcardtype());//受托人证件类型
					applyinfo5.setEntrustedcardnum(applyinfo.getEntrustedcardnum());//证件号码
					applyinfo5.setEntrustedpermission(applyinfo.getEntrustedpermission());//委托权限
					applyinfo5.setEntrustedmatter(applyinfo.getEntrustedmatter());//委托事项
					applyinfo5.setEntrustedstarttime(applyinfo.getEntrustedstarttime());//委托开始时间
					applyinfo5.setEntrustedendtime(applyinfo.getEntrustedendtime());//委托结束时间
//					applyinfo5.setHandleman(getHandleMan());//处理人
					applyinfo5.setConfirmman(applyinfo.getConfirmman());//确认人
					applyinfo5.setGzId(Integer.parseInt(gztype[j]));//公章id
					applyinfo5.setMaxgarde(kind.getGrade());
					applyinfo5.setBusinessManager(applyinfo.getBusinessManager());//业务主管审批人
					bGzapplyinfoService.insert(applyinfo5);
					int applyId=applyinfo5.getId();
					//开启流程实例
					Map<String,Object> activitiMap = new HashMap<>();
					activitiMap.put("user", departmentId);
					String objId="printApply:"+applyId;
					identityService.setAuthenticatedUserId(user.getCode());
					runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
					//根据流程定义id和节点id查询任务Id
					List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					int i=0;
					for(;i<bids.size();i++){
						int id=Integer.parseInt(bids.get(i));
						if(id==applyId)
							break;
					}
					String taskId=tasks.get(i).getId();
					System.out.println("***流程开启5***");
					completeApplyTask(taskId,"1", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo5.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo5.getApplyusername());
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
				if(maxAppGrade==3){//部门经理
					BGzapplyinfo applyinfo6=new BGzapplyinfo();
					applyinfo6.setApplyusername(applyinfo.getApplyusername());//申请人账号
					applyinfo6.setDepartment(applyinfo.getDepartment());//部门名称
					applyinfo6.setDepartmentid(applyinfo.getDepartmentid());//部门id
					applyinfo6.setGzkind(kind.getGzkind());//公章类型
					applyinfo6.setCopies(applyinfo.getCopies());//份数
					applyinfo6.setSendto(applyinfo.getSendto());//发往单位
					applyinfo6.setIssecret(applyinfo.getIssecret());//是否涉密
					applyinfo6.setApplytime(applyinfo.getApplytime());//申请时间
					applyinfo6.setReason(applyinfo.getReason());//申请事由
					applyinfo6.setEntrustedman(applyinfo.getEntrustedman());//受托人
					applyinfo6.setEntrustedpost(applyinfo.getEntrustedpost());//受托人职务
					applyinfo6.setEntrustedcardtype(applyinfo.getEntrustedcardtype());//受托人证件类型
					applyinfo6.setEntrustedcardnum(applyinfo.getEntrustedcardnum());//证件号码
					applyinfo6.setEntrustedpermission(applyinfo.getEntrustedpermission());//委托权限
					applyinfo6.setEntrustedmatter(applyinfo.getEntrustedmatter());//委托事项
					applyinfo6.setEntrustedstarttime(applyinfo.getEntrustedstarttime());//委托开始时间
					applyinfo6.setEntrustedendtime(applyinfo.getEntrustedendtime());//委托结束时间
//					applyinfo6.setHandleman(getHandleMan());//处理人
					applyinfo6.setGzId(Integer.parseInt(gztype[j]));//公章id
					applyinfo6.setMaxgarde(kind.getGrade());
					applyinfo6.setConfirmman(applyinfo.getBusinessManager());//确认人
					applyinfo6.setBusinessManager(applyinfo.getApproveman());//业务主管审批人
					bGzapplyinfoService.insert(applyinfo6);
					int applyId=applyinfo6.getId();
					//开启流程实例
					Map<String,Object> activitiMap = new HashMap<>();
					activitiMap.put("user", departmentId);
					String objId="printApply:"+applyId;
					identityService.setAuthenticatedUserId(user.getCode());
					runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
					//根据流程定义id和节点id查询任务Id
					List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					int i=0;
					for(;i<bids.size();i++){
						int id=Integer.parseInt(bids.get(i));
						if(id==applyId)
							break;
					}
					String taskId=tasks.get(i).getId();
					System.out.println("***流程开启6***");
					completeApplyTask(taskId,"2", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo6.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo6.getApplyusername());
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
				if(maxAppGrade==4){//领导
					BGzapplyinfo applyinfo7=new BGzapplyinfo();
					applyinfo7.setApplyusername(applyinfo.getApplyusername());//申请人账号
					applyinfo7.setDepartment(applyinfo.getDepartment());//部门名称
					applyinfo7.setDepartmentid(applyinfo.getDepartmentid());//部门id
					applyinfo7.setGzkind(kind.getGzkind());//公章类型
					applyinfo7.setCopies(applyinfo.getCopies());//份数
					applyinfo7.setSendto(applyinfo.getSendto());//发往单位
					applyinfo7.setReason(applyinfo.getReason());//申请事由
					applyinfo7.setIssecret(applyinfo.getIssecret());//是否涉密
					applyinfo7.setApplytime(applyinfo.getApplytime());//申请时间
					applyinfo7.setEntrustedman(applyinfo.getEntrustedman());//受托人
					applyinfo7.setEntrustedpost(applyinfo.getEntrustedpost());//受托人职务
					applyinfo7.setEntrustedcardtype(applyinfo.getEntrustedcardtype());//受托人证件类型
					applyinfo7.setEntrustedcardnum(applyinfo.getEntrustedcardnum());//证件号码
					applyinfo7.setEntrustedpermission(applyinfo.getEntrustedpermission());//委托权限
					applyinfo7.setEntrustedmatter(applyinfo.getEntrustedmatter());//委托事项
					applyinfo7.setEntrustedstarttime(applyinfo.getEntrustedstarttime());//委托开始时间
					applyinfo7.setEntrustedendtime(applyinfo.getEntrustedendtime());//委托结束时间
//					applyinfo7.setHandleman(getHandleMan());//处理人
					applyinfo7.setConfirmman(applyinfo.getApproveman());//确认人
					applyinfo7.setGzId(Integer.parseInt(gztype[j]));//公章id
					applyinfo7.setMaxgarde(kind.getGrade());
					bGzapplyinfoService.insert(applyinfo7);
					int applyId=applyinfo7.getId();
					//开启流程实例
					Map<String,Object> activitiMap = new HashMap<>();
					activitiMap.put("user", departmentId);
					String objId="printApply:"+applyId;
					identityService.setAuthenticatedUserId(user.getCode());
					runtimeService.startProcessInstanceByKey("printApplyKey",objId,activitiMap);
					//根据流程定义id和节点id查询任务Id
					List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", "yysq");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					int i=0;
					for(;i<bids.size();i++){
						int id=Integer.parseInt(bids.get(i));
						if(id==applyId)
							break;
					}
					String taskId=tasks.get(i).getId();
					System.out.println("***流程开启7***");
					completeApplyTask(taskId,"3", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo7.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo7.getApplyusername());
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
			}
		}
		return user.getRealname();
	}
	//驳回后重新申请-------------applyinfo：业务信息
	@RequestMapping("reStartApply")
	@ResponseBody
	public String reStartApply(BGzapplyinfo applyinfo,String taskId){
		Set<String> unitList=new HashSet<>();
		List<Entertainobjecttype> type = entertainService.getType();
		for (Entertainobjecttype entertainobjecttype : type) {
			unitList.add(entertainobjecttype.getoName());
		}
		Entertainobjecttype enter=new Entertainobjecttype();
		if(!unitList.contains(applyinfo.getSendto())){
			//将新录入发往单位添加
			entertainobjecttypeMapper.insertOName(applyinfo.getSendto());
		}
		//根据申请人账号获取申请人信息
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		int departmentId=bDepartmentService.getDepartmentIdByUserId(user.getId()).get(0);
		//判断用户级别
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(user.getId());
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取所有公章中最小的级别
		int minGrade=bGzkindService.getMinGrade();
		int applyId=applyinfo.getId();
		BGzkind kind=bGzkindService.getById(applyinfo.getGzId());
		applyinfo.setGzkind(kind.getGzkind());
		bGzapplyinfoService.update(applyinfo);
		//若是传过来多种类型的公章
//		String[] gztype = applyinfo.getGzkind().split(",");//传公章id
//		for(int j=0;j<gztype.length;j++){
//			BGzkind kind=bGzkindService.getById(Integer.parseInt(gztype[j]));
		if(taskId!=null&&!"".equals(taskId)){
			if(kind.getGrade()==1){
				System.out.println("***流程开启1***");
				//完成任务
				if(applyinfo.getContracAmount()!=0){
					if(applyinfo.getContracAmount()>=50&&"甲方".equals(applyinfo.getContractType())){
						if(maxAppGrade==1||maxAppGrade==2){//普通员工
						completeApplyTask(taskId,"1", "待审批");
						}
						if(maxAppGrade==3){
							completeApplyTask(taskId,"2", "待审批");
						}
						if(maxAppGrade==4){
							completeApplyTask(taskId,"3", "待审批");
						}
					}else{
						completeApplyTask(taskId,"0", "待审批");
					}
				}else{
					completeApplyTask(taskId,"0", "待审批");
				}
//				completeApplyTask(taskId,"0", "待审批");
				//公众号消息推送
				String GzhOpenId = "";
				UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
				if(userEntity!=null){
					GzhOpenId = userEntity.getModifiedby();
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
				String mark = "待审批";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setTitle("用印申请");
				weiEntity.setApplyMan(applyinfo.getApplyusername());
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
			if(kind.getGrade()==2){
				//判断是法人身份证复印件
				if(applyinfo.getGzId()==6){
					System.out.println("***法人身份证复印件流程开启***");
					//完成任务
					completeApplyTask(taskId,"1", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo.getApplyusername());
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
				}else{
					if(maxAppGrade==1||maxAppGrade==2){//普通员工
						//完成任务
						completeApplyTask(taskId,"1", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo.getApplyusername());
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
					if(maxAppGrade==3){//部门经理
						System.out.println("***流程开启3***");
						//完成任务
						completeApplyTask(taskId,"2", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo.getApplyusername());
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
					if(maxAppGrade==4){//领导
						System.out.println("***流程开启4***");
						//完成任务
						completeApplyTask(taskId,"3", "待审批");
						//公众号消息推送
						String GzhOpenId = "";
						UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
						if(userEntity!=null){
							GzhOpenId = userEntity.getModifiedby();
						}
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("用印申请");
						weiEntity.setApplyMan(applyinfo.getApplyusername());
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
				}
			}
			if(kind.getGrade()==3){
				if(maxAppGrade==1||maxAppGrade==2){//普通员工    副经理
					System.out.println("***流程开启5***");
					completeApplyTask(taskId,"1", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo.getApplyusername());
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
				if(maxAppGrade==3){//部门经理
					System.out.println("***流程开启6***");
					completeApplyTask(taskId,"2", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo.getApplyusername());
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
				if(maxAppGrade==4){//领导
					System.out.println("***流程开启7***");
					completeApplyTask(taskId,"3", "待审批");
					//公众号消息推送
					String GzhOpenId = "";
					UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getApproveman());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String title = "您好,您有一个新的用印申请需要审批,请及时处理。";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setTitle("用印申请");
					weiEntity.setApplyMan(applyinfo.getApplyusername());
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
			}
		}
		
//		}
		return user.getRealname();
	}
	//业务流程，完成申请（没有角色）
	@RequestMapping("completeApplyTask")
	@ResponseBody
	public void completeApplyTask(String taskId,String result,String status){
		int id = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		BGzapplyinfo applyinfo=bGzapplyinfoService.getById(id);
		//修改业务状态
		if(status!=null&&!"".equals(status)){
			applyinfo.setStatus(status);
			bGzapplyinfoService.update(applyinfo);
		}
		//通过 result=true
		//完成任务
		Map<String,Object> map = new HashMap<>();
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
	}
//	*******************************************用印审批***************************************
	//生成审批单号
	@RequestMapping("createApprovalNum")
	@ResponseBody
	public String createApprovalNum(int departmentId){
		String approvalId="";
		String first =new SimpleDateFormat("yyyyMMdd") .format(new Date() );
		String second="";
		if(departmentId>0&&departmentId<10){
			second="0"+departmentId;
		}
		if(departmentId>10){
			second=departmentId+"";
		}
		//查询当天数量
		int count=bGzapplyinfoService.getApprovalCount("%"+first+second+"%");
		if(count==0){
			approvalId=first+second+"001";
		}else if(count>0&&count<10){
			approvalId=first+second+"00"+count;
		}else if(count>=10&&count<100){
			approvalId=first+second+"0"+count;
		}else if(count>=100){
			approvalId=first+second+count;
		}
//		return new SimpleDateFormat("yyyyMMddHHmmss") .format(new Date() );
		return approvalId;
	}
	
	//一类审批（审批人获取待审批任务一级）------------openId:微信认证号
	@RequestMapping("getApproval0Task")
	@ResponseBody
	public Object getApproval0Task(String openId){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		List<BGzapplyinfo> realList= new ArrayList<>();
		UserEntity user=null;
		if(userId!=null){
			user=bUserService.getUserById(userId);
			String pointId="sp0";//节点名称
			//部门idlist
			List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
			List<BGzapplyinfo> printApplyList= bGzapplyinfoService.findTasksByStatus(busAndTaskId.keySet(),"待审批");
			for(BGzapplyinfo applyinfo:printApplyList){
				if(user.getCode().equals(applyinfo.getApproveman())&&departList.contains(applyinfo.getDepartmentid())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId.get(str);
					applyinfo.setTaskId(tid);
					realList.add(applyinfo);
				}
			}
		}
		return realList;
	}
	//用印审批二级------------appId:申请id，taskId：任务编号,openId:微信认证编号
	@RequestMapping("approvalAppForm")
	@ResponseBody
	public BGzapplyinfo approvalAppForm(Integer appId,String taskId,String openId){
		BGzapplyinfo applyinfo = bGzapplyinfoService.getById(appId);
		applyinfo.setTaskId(taskId);
		applyinfo.setOpenId(openId);
		return applyinfo;
	}
	//一类审批----------taskId:任务编号，result：是否通过(true是通过false是驳回)，status：业务状态("通过"是通过"被否决"是驳回),comment:驳回原因
	@RequestMapping("climeApproval0Task")
	@ResponseBody
	public void climeApproval0Task(String taskId,String result,String status,String comment){
		//获取用户id
		int applyinfoId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		BGzapplyinfo applyinfo = bGzapplyinfoService.getById(applyinfoId);
		//获取申请人的部门id
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		List<Integer> departIds=bDepartmentService.getDepartmentIdByUserId(user.getId());
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//修改业务状态，通过是通过，驳回是被否决
		if(status!=null&&!"".equals(status)){
			if("true".equals(result)){
				applyinfo.setStatus("通过");
			}else if("false".equals(result)){
				applyinfo.setStatus(status);
			}
			if("".equals(applyinfo.getApprovalid())||applyinfo.getApprovalid()==null){
			applyinfo.setApprovalid(createApprovalNum(departIds.get(0)));
			}
			applyinfo.setApprovalLable(1);
			bGzapplyinfoService.update(applyinfo);
		}
		//办理业务
		Map<String,Object> map = new HashMap<>();
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//公众号消息推送
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("用印申请");
		weiEntity.setApplyMan(applyinfo.getApplyusername());
		weiEntity.setApplyTime(sdf.format(new Date()));
		//审批人获取消息推送
		if("true".equals(result)){
			String mark1 = "待审批";
			String GzhOpenId1="";
			BProcess process=bProcessService.getprocessByName("用印处理");
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
			if(userById!=null){
				GzhOpenId1 = userById.getModifiedby();
			}
			String title1 = "您好,您有一个新的用印申请需要审批,请及时处理。";
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
		//申请人获取消息推送
		String mark = "";
		String GzhOpenId ="";
		String title = "";
		UserEntity applyuser=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		if(applyuser!=null){
			GzhOpenId =applyuser.getModifiedby();
		}
		if("true".equals(result)){
			mark = applyinfo.getApproveman()+"审批通过";
			title = "您好,您有一个新的用印申已经通过"+applyinfo.getApproveman()+"的审批。";
		}else if("false".equals(result)){
			mark = "待处理";
			title = "您好,您有一个新的用印申请需要处理,请及时处理。";
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
	//二类审批（审批人获取待审批任务一级）------------openId:微信认证号
	@RequestMapping("getApproval1Task")
	@ResponseBody
	public Object getApproval1Task(String openId){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		List<BGzapplyinfo> realList= new ArrayList<>();
		UserEntity user=null;
		if(userId!=null){
			user=bUserService.getUserById(userId);
			String pointId="sp1";//节点名称
			//部门idlist
			List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
			List<BGzapplyinfo> printApplyList= bGzapplyinfoService.findTasksByStatus(busAndTaskId.keySet(),"待审批");
			
			for(BGzapplyinfo applyinfo:printApplyList){
				if(user.getCode().equals(applyinfo.getApproveman())&&departList.contains(applyinfo.getDepartmentid())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId.get(str);
					applyinfo.setTaskId(tid);
					realList.add(applyinfo);
				}
			}
		}
		return realList;
	}
	//二类获取用印审批二级------------approvalAppForm.action?appId:申请id，taskId：任务编号
	//二类审批----------taskId:任务编号，result：是否通过(true是通过false是驳回)，status：业务状态("待审批"是通过"被否决"是驳回),comment:驳回原因
	@RequestMapping("climeApproval1Task")
	@ResponseBody
	public void climeApproval1Task(String taskId,String result,String status,String comment){
		//获取用户id
		int applyinfoId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		BGzapplyinfo applyinfo = bGzapplyinfoService.getById(applyinfoId);
		//获取申请人的部门id
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		List<Integer> departIds=bDepartmentService.getDepartmentIdByUserId(user.getId());
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//修改业务状态，通过是通过，驳回是被否决
		if(status!=null&&!"".equals(status)){
		applyinfo.setStatus(status);
		if("".equals(applyinfo.getApprovalid())||applyinfo.getApprovalid()==null){
		applyinfo.setApprovalid(createApprovalNum(departIds.get(0)));
		}
		applyinfo.setApprovalLable(1);
			bGzapplyinfoService.update(applyinfo);
		}
		//办理业务
		Map<String,Object> map = new HashMap<>();
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//公众号消息推送
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("用印申请");
		weiEntity.setApplyMan(applyinfo.getApplyusername());
		weiEntity.setApplyTime(sdf.format(new Date()));
		//推送给业务主管
		if("true".equals(result)){
			String GzhOpenId1="";
			String mark1 = "待审批";
			UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getBusinessManager());
			if(userEntity!=null){
				GzhOpenId1 = userEntity.getModifiedby();
			}
			String title1 = "您好,您有一个新的用印申请需要审批,请及时处理。";
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
		//推送给申请人
		String mark = "";
		String GzhOpenId ="";
		String title = "";
		UserEntity applyuser=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		if(applyuser!=null){
			GzhOpenId =applyuser.getModifiedby();
		}
		if("true".equals(result)){
			mark = applyinfo.getApproveman()+"审批通过";
			title = "您好,您有一个新的用印申已经通过"+applyinfo.getApproveman()+"的审批。";
		}else if("false".equals(result)){
			mark = "待处理";
			title = "您好,您有一个新的用印申请需要处理,请及时处理。";
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
	//业务主管审批（审批人获取待审批任务一级）------------openId:微信认证号
	@RequestMapping("getApproval2Task")
	@ResponseBody
	public Object getApproval2Task(String openId){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		List<BGzapplyinfo> realList= new ArrayList<>();
		UserEntity user=null;
		if(userId!=null){
			user=bUserService.getUserById(userId);
			String pointId="sp2";//节点名称
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
			List<BGzapplyinfo> printApplyList= bGzapplyinfoService.findTasksByStatuses(busAndTaskId.keySet(),"待审批","通过");
			for(BGzapplyinfo applyinfo:printApplyList){
				if(user.getCode().equals(applyinfo.getBusinessManager())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId.get(str);
					applyinfo.setTaskId(tid);
					realList.add(applyinfo);
				}
			}
		}
		
		return realList;
	}
	//业务主管审批二级------------approvalAppForm.action?appId:申请id，taskId：任务编号
	//业务主管审批----------taskId:任务编号，result：是否通过(true是通过false是驳回)，status：业务状态("待审批"是通过"被否决"是驳回),comment：驳回原因
	@RequestMapping("climeApproval2Task")
	@ResponseBody
	public void climeApproval2Task(String taskId,String result,String status,String comment,String borrowTime,String returnTime){
		//获取用户id
		int applyinfoId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//获取到当前任务的数据信息
		BGzapplyinfo applyinfo = bGzapplyinfoService.getById(applyinfoId);
		//公众号消息推送
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("用印申请");
		weiEntity.setApplyMan(applyinfo.getApplyusername());
		weiEntity.setApplyTime(sdf.format(new Date()));
		//获取申请人的部门id
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		List<Integer> departIds=bDepartmentService.getDepartmentIdByUserId(user.getId());
		if(applyinfo.getBusinessManager().equals(applyinfo.getConfirmman())&&"true".equals(result)){
			workflowUtil.JumpEndActivity(taskId, "yycl",null); 
			if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
				applyinfo.setBorrowTime(borrowTime);
				applyinfo.setReturnTime(returnTime);
			}
			if(status!=null&&!"".equals(status)){
				if("true".equals(result)){
					applyinfo.setStatus("通过");
				}else if("false".equals(result)){
					applyinfo.setStatus(status);
				}
				if("".equals(applyinfo.getApprovalid())||applyinfo.getApprovalid()==null){
					applyinfo.setApprovalid(createApprovalNum(departIds.get(0)));
				}
				applyinfo.setBussinessLable(2);
				applyinfo.setConfirmLable(3);
				bGzapplyinfoService.update(applyinfo);
			}
			String mark1 = "待审批";
			String GzhOpenId1="";
			BProcess process=bProcessService.getprocessByName("用印处理");
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
			if(userById!=null){
				GzhOpenId1 = userById.getModifiedby();
			}
			String title1 = "您好,您有一个新的用印申请需要审批,请及时处理。";
			
			String mark = applyinfo.getConfirmman()+"审核通过";
			String title = "您好,您有一个新的用印申已经通过"+applyinfo.getConfirmman()+"的审批。";
			UserEntity applyuser=bUserService.getUserByUserCode(applyinfo.getApplyusername());
			String GzhOpenId="";
			if(applyuser!=null){
				GzhOpenId =applyuser.getModifiedby();
			}
			try {
				CommonUtil.sendPrivateApproveMessage(GzhOpenId1, weiEntity, title1, mark1);
				CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			//修改业务状态，通过是通过，驳回是被否决
			if(status!=null&&!"".equals(status)){
				applyinfo.setStatus(status);
				if("".equals(applyinfo.getApprovalid())||applyinfo.getApprovalid()==null){
					applyinfo.setApprovalid(createApprovalNum(departIds.get(0)));
				}
				applyinfo.setBussinessLable(2);
				bGzapplyinfoService.update(applyinfo);
			}
			//办理业务
			Map<String,Object> map = new HashMap<>();
			map.put("result", result); 
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			workflowUtil.completeTask(workflowBean, map);
			
			//推送消息给总经理
			if("true".equals(result)){
				String mark1 = "待审批";
				String GzhOpenId1="";
				UserEntity userEntity = bUserService.getUserByUserCode(applyinfo.getConfirmman());
				if(userEntity!=null){
					GzhOpenId1 = userEntity.getModifiedby();
				}
				String title1 = "您好,您有一个新的用印申请需要审批,请及时处理。";
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
			//推送消息给申请人
			String mark = "";
			String GzhOpenId ="";
			String title = "";
			UserEntity applyuser=bUserService.getUserByUserCode(applyinfo.getApplyusername());
			if(applyuser!=null){
				GzhOpenId =applyuser.getModifiedby();
			}
			if("true".equals(result)){
				mark = applyinfo.getBusinessManager()+"审批通过";
				title = "您好,您有一个新的用印申已经通过"+applyinfo.getBusinessManager()+"的审批。";
			}else if("false".equals(result)){
				mark = "待处理";
				title = "您好,您有一个新的用印申请需要处理,请及时处理。";
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
		
	}
	//总经理审批（审批人获取待审批任务一级）------------openId:微信认证号
	@RequestMapping("getApproval3Task")
	@ResponseBody
	public Object getApproval3Task(String openId){
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		List<BGzapplyinfo> realList= new ArrayList<>();
		UserEntity user=null;
		if(userId!=null){
			user=bUserService.getUserById(userId);
			String pointId="sp3";//节点名称
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
			
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
			List<BGzapplyinfo> printApplyList= bGzapplyinfoService.findTasksByStatuses(busAndTaskId.keySet(),"待审批","通过");
			for(BGzapplyinfo applyinfo:printApplyList){
				if(user.getCode().equals(applyinfo.getConfirmman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId.get(str);
					applyinfo.setTaskId(tid);
					realList.add(applyinfo);
				}
			}
		}
		return realList;
	}
	//总经理审批二级------------approvalAppForm.action?appId:申请id，taskId：任务编号
	//总经理审批----------taskId:任务编号，result：是否通过(true是通过false是驳回)，status：业务状态("通过"是通过"被否决"是驳回),returnTime:归还时间，borrowTime：借用时间,comment:驳回原因
	@RequestMapping("climeApproval3Task")
	@ResponseBody
	public void climeApproval3Task(String taskId,String result,String status,String borrowTime,String returnTime,String comment){
		//获取用户id
		int applyinfoId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
		//获取到当前任务的数据信息
		BGzapplyinfo applyinfo = bGzapplyinfoService.getById(applyinfoId);
		//获取申请人的部门id
		UserEntity user=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		List<Integer> departIds=bDepartmentService.getDepartmentIdByUserId(user.getId());
		if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
			applyinfo.setBorrowTime(borrowTime);
			applyinfo.setReturnTime(returnTime);
		}
		//添加评论
		if(StringUtils.isNotBlank(comment)){//如果不为无的话这添加评论
			workflowUtil.addComment(taskId,comment);
		}
		//修改业务状态，通过是通过，驳回是被否决
		if(status!=null&&!"".equals(status)){
			if("true".equals(result)){
				applyinfo.setStatus("通过");
			}else if("false".equals(result)){
				applyinfo.setStatus(status);
			}
			if("".equals(applyinfo.getApprovalid())||applyinfo.getApprovalid()==null){
				applyinfo.setApprovalid(createApprovalNum(departIds.get(0)));
			}
			applyinfo.setConfirmLable(3);
			bGzapplyinfoService.update(applyinfo);
		}
		//办理业务
		Map<String,Object> map = new HashMap<>();
		map.put("result", result); 
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
		//公众号消息推送
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("用印申请");
		weiEntity.setApplyMan(applyinfo.getApplyusername());
		weiEntity.setApplyTime(sdf.format(new Date()));
		//推送消息给综合办公室
		if("true".equals(result)){
			String mark1 = "待审批";
			String GzhOpenId1="";
			BProcess process=bProcessService.getprocessByName("用印处理");
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
			if(userById!=null){
				GzhOpenId1 = userById.getModifiedby();
			}
			String title1 = "您好,您有一个新的用印申请需要审批,请及时处理。";
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
		
		//推送消息给申请人
		String mark = "";
		String GzhOpenId ="";
		String title = "";
		UserEntity applyuser=bUserService.getUserByUserCode(applyinfo.getApplyusername());
		if(applyuser!=null){
			GzhOpenId =applyuser.getModifiedby();
		}
		if("true".equals(result)){
			mark = applyinfo.getConfirmman()+"审核通过";
			title = "您好,您有一个新的用印申已经通过"+applyinfo.getConfirmman()+"的审批。";
		}else if("false".equals(result)){
			mark = "待处理";
			title = "您好,您有一个新的用印申请需要处理,请及时处理。";
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
//	*******************************************用印处理***************************************
	//获取待处理信息-----------------openId:微信认证编号
	@RequestMapping("getTobetreatedTask")
	@ResponseBody
	public AnccResult getTobetreatedTask(BGzapplyinfo gzapplyinfo,String openId,Integer nowPage,Integer pageSize){
		if(gzapplyinfo.getEndtime()!=null&&!"null".equals(gzapplyinfo.getEndtime())){
			gzapplyinfo.setEndtime(gzapplyinfo.getEndtime()+" 24:00:00");
		}
		Map<String,Object> minmap = new HashMap<>();
		//获取可以处理的角色
		List<Integer> handleMan = getHandleMan();
		List<BGzapplyinfo> realList= new ArrayList<>();
		//获取用户的id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		if(userId!=null){
			//用印处理节点
			String pointId="yycl";
			UserEntity user = bUserService.getUserById(userId);
			//获取登录用户的角色
			List<Integer> role=bUserService.getRoleIdByUserId(userId);
			handleMan.retainAll(role);
			//根据节点的名称获和用户名称取任务
			List<Task> tasks = workflowUtil.getTaskByIds("printApplyKey", pointId);
			//根据任务获取业务
			Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//			List<BGzapplyinfo> applyinfoList= bGzapplyinfoService.findTasksByStatus(busAndTaskId.keySet(),"通过");
			List<BGzapplyinfo> applyinfoList= bGzapplyinfoService.findTasksByCase(busAndTaskId.keySet(),"通过",gzapplyinfo);
			for(BGzapplyinfo applyinfo:applyinfoList){
				if(handleMan.size()>0){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId.get(str);
					applyinfo.setTaskId(tid);
					applyinfo.setTitle("用印申请");
					applyinfo.setCanUseSeal("1");
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					applyinfo.setUrl("print/completePrintTask.action");
					applyinfo.setParam("taskId,openId");
					applyinfo.setSortTime(applyinfo.getApplytime());
					realList.add(applyinfo);
				}
			}
		}
		sort(realList);
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
	//用印处理申请单页面-----------------approvalAppForm.action?appId=?taskId=？
	//通过处理---------------taskId：任务编号
	@RequestMapping("completePrintTask")
	@ResponseBody
	public void completePrintTask(String taskId,String openId){
		//获取用户的id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		if(userId!=null){
			UserEntity user = bUserService.getUserById(userId);
			int applyId = Integer.parseInt(workflowUtil.findBussinessIdByTaskId(taskId));
			BGzapplyinfo applyinfo = bGzapplyinfoService.getById(applyId);
			applyinfo.setStatus("已完成");
			applyinfo.setHandleman(user.getCode());
			bGzapplyinfoService.update(applyinfo);
			//完成任务
			Map<String,Object> map = new HashMap<>();
			map.put("result", "true"); 
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			workflowUtil.completeTask(workflowBean, map);
			
			//公众号消息推送
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setTitle("用印申请");
			weiEntity.setApplyMan(applyinfo.getApplyusername());
			weiEntity.setApplyTime(sdf.format(new Date()));
			//推送消息给综合办公室
			String mark = "用印申请通过";
			String GzhOpenId="";
			BProcess process=bProcessService.getprocessByName("用印处理");
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
			if(userById!=null){
				GzhOpenId = userById.getModifiedby();
			}
			String title = "您好,您有一个新的用印申请已通过。";
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
	}
//	*******************************************借用信息***************************************
	//获取借用申请信息-------------openId:微信认证编号
	@RequestMapping("getApplyInfo")
	@ResponseBody
	public Object getApplyInfo(String openId){
		//获取用户的id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		//根据节点的名称获和用户名称取任务
//		List<Task> tasks =new ArrayList<>();
//		List<Task> task1 =workflowUtil.getTaskByIds("printApplyKey", "yysq");
//		List<Task> task2 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
//		List<Task> task3 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
//		List<Task> task4 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
//		List<Task> task5 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
//		List<Task> task6 =workflowUtil.getTaskByIds("printApplyKey", "yycl");
//		tasks.addAll(task1);
//		tasks.addAll(task2);
//		tasks.addAll(task3);
//		tasks.addAll(task4);
//		tasks.addAll(task5);
//		tasks.addAll(task6);
		//根据任务获取业务
//		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
//		List<BGzapplyinfo> applyinfoList= bGzapplyinfoService.findTasksByUserCode(busAndTaskId.keySet(),user.getCode());
//		List<BGzapplyinfo> realList= new ArrayList<>();
//		for(BGzapplyinfo applyinfo:applyinfoList){
//			String str = applyinfo.getId()+"";
//			String tid=busAndTaskId.get(str);
//			applyinfo.setTaskId(tid);
//			realList.add(applyinfo);
//		}
		List<BGzapplyinfo> applyinfoList= bGzapplyinfoService.getByAppUser(user.getCode());	
		return applyinfoList;
	}
	//查看申请信息（二级）------------approvalAppForm.action?appId:申请id，taskId：任务编号
	//撤销申请------------appId:业务申请id,taskId:业务id
	@RequestMapping("revokeApply")
	@ResponseBody
	public void revokeApply(Integer appId,String taskId){
		BGzapplyinfo applyinfo=bGzapplyinfoService.getById(appId);
		//存储已经审批过的审批人账号
		String approveman[]=new String[3];
		if(applyinfo.getApprovalLable()!=0){
			approveman[0]=applyinfo.getApproveman();
		}
		if(applyinfo.getBusinessManager()!=null&&applyinfo.getConfirmman()!=null){
			if(!applyinfo.getBusinessManager().equals(applyinfo.getConfirmman())){
				if(applyinfo.getBussinessLable()!=0){
					approveman[1]=applyinfo.getBusinessManager();
				}
				if(applyinfo.getConfirmLable()!=0){
					approveman[2]=applyinfo.getConfirmman();
				}
			}else{
				if(applyinfo.getBussinessLable()!=0){
					approveman[1]=applyinfo.getBusinessManager();
				}
			}
		}
		//公众号消息推送
		String GzhOpenId = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String title = "您好,"+applyinfo.getApplyusername()+"于"+applyinfo.getApplytime()+"申请的"+applyinfo.getGzkind()+"申请已撤销！";
		String mark = "已撤销";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setTitle("用印申请");
		weiEntity.setApplyMan(applyinfo.getApplyusername());
		weiEntity.setApplyTime(sdf.format(new Date()));
		for(int j=0;j<approveman.length;j++){
			UserEntity userEntity = bUserService.getUserByUserCode(approveman[j]);
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
		
		int deleteById = bGzapplyinfoService.deleteById(appId);
		if(deleteById!=0){
			if(taskId!=null&&!"null".equals(taskId)){
				workflowUtil.deleteProcess(taskId);
			}
		}
	}
//	查看申请信息（二级）------------appForm.action?appId=?
  //撤销申请------------appId:业务申请id
//	@RequestMapping("revokeApply")
//	@ResponseBody
//	public void revokeApply(Integer appId,String taskId){
//		if(taskId!=null&&!"null".equals(taskId)){
//			workflowUtil.deleteProcess(taskId);
//		}
//		bGzapplyinfoService.deleteById(appId);
//	}
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
		//所有部门
		List<BDepartmentEntity> departList=bDepartmentService.getAllDepartment();
		//所有用印
		List<BGzkind> gzKindList = getPrintingType();
		UserEntity userById = bUserService.getUserById(userId);
		//获取自己所属部门信息
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userId);
		List<BDepartmentEntity> ownDepartList=bDepartmentService.getOwnDepartment(departmentIdByUserId);
		
		if(moduleNumByRole.equals("1*")){
			hashMap.put("user", userById);
			hashMap.put("userLable", "disable");
			hashMap.put("department", ownDepartList);
			hashMap.put("departmentLable", "disable");
			hashMap.put("gzKind", gzKindList);
		}else if("2*".equals(moduleNumByRole)){
			hashMap.put("user", userById);
			hashMap.put("userLable", "online");
			hashMap.put("department", ownDepartList);
			hashMap.put("departmentLable", "disable");
			hashMap.put("gzKind", gzKindList);
		}else if(moduleNumByRole.equals("3*")){
			hashMap.put("user", userById);
			hashMap.put("userLable", "online");
			hashMap.put("department", departList);
			hashMap.put("departmentLable", "online");
			hashMap.put("gzKind", gzKindList);
		}
        return hashMap;
	}
	//使用明细-------------------departpartmentId:部门编号，applyMan：申请人账号，beginTime：起始时间，endTime：结束时间,gzKind:公章类型
	@RequestMapping("printUseDetailSearch")
	@ResponseBody
	public List<BGzapplyinfo> printUseDetailSearch(String departpartmentId,String applyMan,String beginTime,String endTime,String gzKind) throws ParseException{
		String result="select * from b_gzapplyinfo where 1=1";//sql语句
		if(departpartmentId!=null && !"".equals(departpartmentId)){
			result+=" and DepartmentId ="+departpartmentId;
		}
		if(gzKind!=null && !"".equals(gzKind)){
			result+=" and GZKind ='"+gzKind+"'";
		}
		if(applyMan!=null && !"".equals(applyMan)){
			result+=" and ApplyUserName like '%"+applyMan+"%'";
		}
		if(beginTime!=null && !"".equals(beginTime)){
			result+=" and ApplyTime >='"+beginTime+"'";
		}
		if(endTime!=null && !"".equals(endTime)){
			result+=" and ApplyTime <='"+endTime+"'";
		}
		List<BGzapplyinfo> applyinfo=bGzapplyinfoService.getBySql(result);
		return applyinfo;
	}
	//详细信息-------------------appForm.action?appId=?
	@RequestMapping("appForm")
	@ResponseBody
	public BGzapplyinfo appForm(int appId){
		BGzapplyinfo applyinfo=bGzapplyinfoService.getById(appId);
		return applyinfo;
	}
	
	//提交申请并进行推送
	@RequestMapping("/sendApplyInfoMessage")
	@ResponseBody
	public  AnccResult sendApplyInfoMessage(String openId){
		Integer userIdByOpenId = bUserService.getUserIdByOpenId(openId);
		UserEntity userById = bUserService.getUserById(userIdByOpenId);
		Object selectNum = bGzapplyinfoService.selectNum(userById.getCode());
		return AnccResult.ok(selectNum);
	}
	public void sort (List<BGzapplyinfo> list){
		Collections.sort(list,new Comparator<publicEntity>() {

			@Override
			public int compare(publicEntity o1, publicEntity o2) {
				SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
				Date before = null;
				Date after = null;
				try {
					before = smf.parse(o1.sortTime);
					after = smf.parse(o2.sortTime);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return (int) (before.getTime()-after.getTime());
			}
			
		});
	}
	//用印查询
	@RequestMapping("/searchGzInfo")
	@ResponseBody
	public  Object searchGzInfo(BGzapplyinfo applyinfo,String openId,Integer nowPage,Integer pageSize){
		Map<String,Object> minmap = new HashMap<>();
		List<BGzapplyinfo> list1 = new ArrayList<BGzapplyinfo>();
		String department = applyinfo.getDepartment();
		String startTime = applyinfo.getStarttime();
		String endTime = "null";
		if(applyinfo.getEndtime()!=null&&!"null".equals(applyinfo.getEndtime())){
			endTime = applyinfo.getEndtime()+" 24:00:00";
		}
//		String gzkind=applyinfo.getGzkind();
		Integer gzId=applyinfo.getGzId();
		//根据openID获取用户id
		Integer userid = bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userid);
		//根据用户id获取用户所有角色id
		List<Integer> appRole = bUserService.getRoleIdByUserId(userid);
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		String result="";
		String applyMan = "";
		if("null".equals(applyinfo.getApplyusername())){
			result="select * from b_gzapplyinfo where Status='已完成'";
		}else{
			applyMan ="%"+applyinfo.getApplyusername()+"%";
			result="select * from b_gzapplyinfo where Status='已完成' and ApplyUserName like '"+applyMan+"'";
		}
		
		if(!"全部".equals(department)&&department!="null"){
			result+=" and Department = '"+department+"'";
		}
//		if(!"全部".equals(gzkind)&&!gzkind.equals("null")&&!"".equals(gzkind)){
//			result+=" and GZKind = '"+gzkind+"'";
//		}
		if(gzId!=null&&gzId!=0){
			result+=" and gzId ="+gzId;
		}
		if(!startTime.equals("null")&&!"".equals(startTime)){
			result+=" and ApplyTime>='"+startTime+"'";
		}
		if(!endTime.equals("null")&&!"".equals(endTime)){
			result+=" and ApplyTime<='"+endTime+"'";
		}
		result+=" order by ApplyTime desc";
		list1 = bGzapplyinfoService.getBySql(result);
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));//当前年
		for (BGzapplyinfo bGzapplyinfo : list1) {
			if(bGzapplyinfo.getApplytime().contains(year)){
				bGzapplyinfo.setTitle("用印申请");
				bGzapplyinfo.setApplytime(bGzapplyinfo.getApplytime().substring(0, 10));
				list.add(bGzapplyinfo);
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
//	查看申请信息（二级）------------appForm.action?appId=?
	//发往单位匹配
	@RequestMapping("/getAllSendUnit")
	@ResponseBody
	public  Object getAllSendUnit(String companyName){
		List<String> list = new ArrayList<String>();
		list = bGzapplyinfoService.getAllSendUnit("%"+companyName+"%");
		return list;
	}
	//查看个人本年度用印借用情况
	@RequestMapping("/userPrintingCase")
	@ResponseBody
	public Object userPrintingCase(String openId){
		Integer userid = bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userid);
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		list = bGzapplyinfoService.getFinishByAppUser(user.getCode());
		Calendar cal = Calendar.getInstance();
		String year = String.valueOf(cal.get(Calendar.YEAR));//当前年
		int countSecret=0;
		int countApply=0;
//		int countNoSercet=0;
//		int countgzqt=0;//公章（其他）
//		int countfrz=0;//法人章
//		int countgzhtsqwts=0; //公章（合同授权委托书）
//		int counthtzyz=0;//合同专用章
//		int countyyzzfy=0;//营业执照(复印件)
//		int countgz=0;//公章
//		int counthtsqwts=0;//合同授权委托书
//		int countyyzzyj=0;//营业执照(原件)
//		int countfrsffyj=0;//法人身份证复印件
//		int countfrqscl=0;//法人签署材料
//		int countfrsqwt=0;//法人授权委托

		for (BGzapplyinfo bGzapplyinfo : list) {
			if(bGzapplyinfo.getApplytime().contains(year)){
				if("是".equals(bGzapplyinfo.getIssecret())){
					countSecret+=1;
				}
				countApply+=1;
//				else{
//					countNoSercet+=1;
//				}
//				if("公章(其他)".equals(bGzapplyinfo.getGzkind())){
//					countgzqt+=1;
//				}
//				if("法人章".equals(bGzapplyinfo.getGzkind())){
//					countfrz+=1;
//				}
//				if("公章（合同授权委托书）".equals(bGzapplyinfo.getGzkind())){
//					countgzhtsqwts+=1;
//				}
//				if("合同专用章".equals(bGzapplyinfo.getGzkind())){
//					counthtzyz+=1;
//				}
//				if("营业执照(复印件)".equals(bGzapplyinfo.getGzkind())){
//					countyyzzfy+=1;
//				}
//				if("公章".equals(bGzapplyinfo.getGzkind())){
//					countgz+=1;
//				}
//				if("合同授权委托书".equals(bGzapplyinfo.getGzkind())){
//					counthtsqwts+=1;			
//				}
//				if("营业执照(原件)".equals(bGzapplyinfo.getGzkind())){
//					countyyzzyj+=1;
//				}
//				if("法人身份证复印件".equals(bGzapplyinfo.getGzkind())){
//					countfrsffyj+=1;
//				}
//				if("法人签署材料".equals(bGzapplyinfo.getGzkind())){
//					countfrqscl+=1;
//				}
//				if("法人授权委托".equals(bGzapplyinfo.getGzkind())){
//					countfrsqwt+=1;
//				}
			}
		}
		Map<String, Object> caseMap = new HashMap<String, Object>();// 定义map
		caseMap.put("countSecret", countSecret);
		caseMap.put("countApply", countApply);
//		caseMap.put("countNoSercet", countNoSercet);
//		caseMap.put("countgzqt", countgzqt);
//		caseMap.put("countfrz", countfrz);
//		caseMap.put("countgzhtsqwts", countgzhtsqwts);
//		caseMap.put("counthtzyz", counthtzyz);
//		caseMap.put("countyyzzfy", countyyzzfy);
//		caseMap.put("countgz", countgz);
//		caseMap.put("counthtsqwts", counthtsqwts);
//		caseMap.put("countyyzzyj", countyyzzyj);
//		caseMap.put("countfrqscl", countfrqscl);
//		caseMap.put("countfrsqwt", countfrsqwt);
//		caseMap.put("countfrsffyj", countfrsffyj);
		return caseMap;
		}
	//用印处理的徽章
	@RequestMapping("/toTreatHandleMessage")
	@ResponseBody
	public  int toTreatHandleMessage(String openId,Integer nowPage,Integer pageSize,BGzapplyinfo gzapplyinfo){
		nowPage = 0;
		int count=0;
		pageSize = 0;
		if(openId!=null&&!openId.equals("")){
			AnccResult mainControcller = getTobetreatedTask(gzapplyinfo,openId,nowPage,pageSize);
			Object data = mainControcller.getData();
			@SuppressWarnings("unchecked")
			Iterator iter = ((Map<String, Object>) data).entrySet().iterator();
			while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			if(!val.toString().equals("[]")){
				count=Integer.parseInt(val.toString());
			}
		}
		}
		return count;
	}
//	*******************************************后台管理系统***************************************
	//用印完成信息
	//获取所有申请信息
	@RequestMapping("/getAllApplyInfo")
	@ResponseBody
	public Object getAllApplyInfo(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		list = bGzapplyinfoService.getAll(start, number);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bGzapplyinfoService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//查询
	@RequestMapping("/displaySearch")
	@ResponseBody
	public Object displaySearch(HttpServletRequest request,BGzapplyinfo applyinfo){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		
		String countResult="select count(*) from b_gzapplyinfo where Status='已完成'";
		String result="select * from b_gzapplyinfo where Status='已完成'";
		String department = applyinfo.getDepartment();
		String applyman = "%"+applyinfo.getApplyusername()+"%";
		String startTime = applyinfo.getStarttime();
		String endTime = applyinfo.getEndtime();
		String gzkind=applyinfo.getGzkind();
		int gzId=applyinfo.getGzId();
		if(!department.equals("全部")&&department!=null){
			countResult+=" and Department = '"+department+"'";
			result+=" and Department = '"+department+"'";
		}
		if(applyinfo.getApplyusername()!=null&&!"".equals(applyinfo.getApplyusername())){
			countResult+=" and ApplyUserName like '"+applyman+"'";
			result+=" and ApplyUserName like '"+applyman+"'";
		}
		if(!gzkind.equals("全部")&&gzkind!=null&&!gzkind.equals("0")){
			countResult+=" and gzId ="+gzId;
			result+=" and gzId = "+gzId;
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
		list = bGzapplyinfoService.getBySql(result);
		
		int total=bGzapplyinfoService.getCountBySQL(countResult);
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
		String fileName = "用印使用信息" + date + ".xlsx";
		String filePath = path + "/" + fileName;
		int flag = bGzapplyinfoService.export(nlist, filePath);
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

	//显示所有用印申请信息（转换）
	@RequestMapping("/showApplyGZ")
	@ResponseBody
	public ModelAndView showApplyGZ(){
		ModelAndView result=new ModelAndView("printing/officialSeal");
		return result;
	}
	
	//用印申请信息
	//获取所有进行中的申请信息
	@RequestMapping("/getAllRunTimeApplyInfo")
	@ResponseBody
	public Object getAllRunTimeApplyInfo(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		list = bGzapplyinfoService.getAllRunTime(start, number);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bGzapplyinfoService.getAllRunTimeCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//进行中查询
	@RequestMapping("/displayRunTimeSearch")
	@ResponseBody
	public Object displayRunTimeSearch(HttpServletRequest request,BGzapplyinfo applyinfo){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BGzapplyinfo> list = new ArrayList<BGzapplyinfo>();
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		
		String countResult="select count(*) from b_gzapplyinfo where Status!='已完成'";
		String result="select * from b_gzapplyinfo where Status!='已完成'";
		String department = applyinfo.getDepartment();
		String applyman = "%"+applyinfo.getApplyusername()+"%";
		String startTime = applyinfo.getStarttime();
		String endTime = applyinfo.getEndtime();
		String gzkind=applyinfo.getGzkind();
		int gzId=applyinfo.getGzId();
		String status=applyinfo.getStatus();
		if(!department.equals("全部")&&department!=null){
			countResult+=" and Department = '"+department+"'";
			result+=" and Department = '"+department+"'";
		}
		if(applyinfo.getApplyusername()!=null&&!"".equals(applyinfo.getApplyusername())){
			countResult+=" and ApplyUserName like '"+applyman+"'";
			result+=" and ApplyUserName like '"+applyman+"'";
		}
		if(!status.equals("全部")&&status!=null){
			countResult+=" and Status like '%"+status+"%'";
			result+=" and Status like '%"+status+"%'";
		}
		if(!gzkind.equals("全部")&&gzkind!=null&&!gzkind.equals("0")){
			countResult+=" and gzId ="+gzId;
			result+=" and gzId = "+gzId;
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
		list = bGzapplyinfoService.getBySql(result);
		
		int total=bGzapplyinfoService.getCountBySQL(countResult);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
		}
	//带出进行中业务的数据为Excel
	@RequestMapping("/exportRunTimeExcel")
	@ResponseBody
	public void exportRunTimeExcel(HttpServletRequest request, HttpServletResponse response, String number) {
		String[] nlist = number.split(","); // 获得传递过来的number列表
		// 获取导出文件夹
		String path = request.getSession().getServletContext().getRealPath("/");
		// 生成导出的文件名
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String date = matter.format(dt);
		String fileName = "用印申请信息" + date + ".xlsx";
		String filePath = path + "/" + fileName;
		int flag = bGzapplyinfoService.exportRunTime(nlist, filePath);
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
	//显示所有用印申请信息进行中（转换）
	@RequestMapping("/showRunTimeApplyGZ")
	@ResponseBody
	public ModelAndView showRunTimeApplyGZ(){
		ModelAndView result=new ModelAndView("printing/gzApplyInfo");
		return result;
	}
}
