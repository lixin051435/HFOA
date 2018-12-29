package com.hfoa.controller.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import java.util.Iterator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hfoa.common.AnccResult;
import com.hfoa.dao.car.BCarapplymanageMapper;
import com.hfoa.dao.entertain.EntertainMapper;
import com.hfoa.dao.entertain.EntertainregisterinfoMapper;
import com.hfoa.dao.lear.LearTimeMapper;
import com.hfoa.dao.lear.YearLearMapper;
import com.hfoa.dao.printing.BGzapplyinfoMapper;
import com.hfoa.dao.privatecar.PrivateMapper;
import com.hfoa.dao.privatecar.PrivatecarinvoiceMapper;
import com.hfoa.dao.travelExpenses.ApplyExpensesMapper;
import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.entity.car.CarApplyInfo2;
import com.hfoa.entity.common.TaskDTO;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;
import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.service.car.BCarapplymanageService;
import com.hfoa.service.car.BCarbaseinfoService;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.printing.BGzapplyinfoService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.WorkflowUtil;


@Controller
@RequestMapping("/main")
public class MainController {
	
	@Autowired
	private ApplyExpensesMapper applyExpensesMapper;
	
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	
	
	@Autowired
	private EntertainMapper entertainMapper;
	
	@Autowired
	private EntertainregisterinfoMapper entertainregisterinfoMapper;
	
	@Autowired
	private PrivateMapper privateMapper;
	
	@Autowired
	private PrivatecarinvoiceMapper privatecarinvoiceMapper;
	
	@Autowired
	private YearLearMapper yearLearMapper;
	
	@Autowired
	private LearTimeMapper learTimeMapper;
	
	
	@Autowired
	private BCarapplymanageMapper bCarapplymanageMapper;
	@Autowired
	private BGzapplyinfoMapper bGzapplyinfoMapper;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired 
	private RuntimeService runtimeService;
	
	@Autowired
	private HistoryService historyService;
	@Autowired
	private WorkflowUtil workflowUtil;
	@Autowired
	private BCarapplymanageService bCarapplymanageService;
	@Autowired
	private BCarbaseinfoService bCarbaseinfoService;
	@Autowired
	private USerService bUserService;
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
    private BProcessService bProcessService;
	@Autowired
    private BGzapplyinfoService bGzapplyinfoService;
	
	/**
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/mainApprove")
	@ResponseBody
	
	public AnccResult mainControcller(String openId,Integer nowPage,Integer pageSize){
				//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		String userCode ="";
		UserEntity user = bUserService.getUserById(userId);
		List<Integer> departList = new  ArrayList<>();
		//部门idlist
		if(user!=null){
			departList=bDepartmentService.getDepartmentIdByUserId(userId);
			//获取登录用户的所有角色id
			List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
			//获取领导级别的角色
			Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
			userCode = user.getCode();
			roleIdFromClass.retainAll(roleIdByUserId);
		}
		List<publicEntity> list = new ArrayList<>();
		
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userCode)
				 .orderByTaskCreateTime().desc()
				 .list();
		Map<TaskDTO, String> map = new HashMap<>();
		if(tasks.size()>0){
			 map = getTaskAndBussIdByTask(tasks);
		}
		
		int i=0;
		for(TaskDTO taskDto:map.keySet()){
			String id = taskDto.getBussinessId();
			String processInstanceId = taskDto.getProcessInstanceId();
			String startUserId = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult().getStartUserId();
			if(null!=startUserId&!startUserId.equals(userCode)){
				if(id.indexOf("applyExpenses")!=-1){
					id = id.split("\\:")[1];
					ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
					if(applyExpens!=null){
							String taskId = map.get(taskDto);
							applyExpens.setTitle("差旅费");
							applyExpens.setTaskId(taskId);
							applyExpens.setApproveState(taskDto.getName());
							applyExpens.setSortTime(applyExpens.getApplyTime());
							list.add(applyExpens);
						
					}
				}
				if(id.indexOf("businesshospitality")!=-1){
					////system.out.println("业务招待");
					id = id.split("\\:")[1];
					////system.out.println("id是"+id);
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					if(entertain!=null){
						if(taskDto.getKey().equals("financialapproval")){//财务
							List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
							entertain.setEntertainregisterinfo(number);
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem");
							entertain.setShowButton("0");
						}else if(taskDto.getKey().equals("businessapprove")){
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
							entertain.setShowButton("1");
						}
						List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
						entertain.setEntertainregisterinfo(number);
						String taskId = map.get(taskDto);
						entertain.setStatus(taskDto.getName());
						entertain.setTaskId(taskId);
						entertain.setSortTime(entertain.getApplyTime());
						entertain.setTitle("业务招待");
						list.add(entertain);
					}
				}
				if(id.indexOf("privateCar")!=-1){
					if(taskDto.getKey().equals("privateCarApprove")){
						id = id.split("\\:")[1];
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
						if(privateCar!=null){
							String taskId = map.get(taskDto);
							privateCar.setTitle("私车公用");
							privateCar.setTaskId(taskId);
							privateCar.setStatus(taskDto.getName());
							privateCar.setSortTime(privateCar.getApplyTime());
							list.add(privateCar);
						}
					}
				}
				
				if(id.indexOf("leaverYear")!=-1){//年假查询
					////system.out.println("");
					id = id.split("\\:")[1];
					LearYear learYear = yearLearMapper.findById(id);
					if(learYear!=null){
						List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
						learYear.setLeaver(listLearTime);
						String taskId = map.get(taskDto);
						learYear.setTitle("年假管理");
						learYear.setTaskId(taskId);
						learYear.setSortTime(learYear.getApplyTime());
						list.add(learYear);
					}
				}
	
			
		}
		
		}
		//公车管理
		List<Task> task1 =workflowUtil.getTaskByIds("applyCarKey", "sp");
		//公车审批
		Map<String,String> busAndTaskId1 = workflowUtil.getTaskAndBussIdByTask(task1);
		List<CarApplyInfo2> carApplyList1= bCarapplymanageService.findTasksByStatus(busAndTaskId1.keySet(),2);
		for(CarApplyInfo2 carapply:carApplyList1){
			if(userId==carapply.getApprovalUserId()){
				String str = carapply.getID()+"";
				String tid=busAndTaskId1.get(str);
				carapply.setTaskId(tid);//不能删
				carapply.setSortTime(carapply.getApplyTime());
				carapply.setTitle("公车申请");
				carapply.setDepartment(carapply.getDepartment().replace(",", ""));
				carapply.setDepartmentId(carapply.getDepartmentId().replace(",", ""));
				carapply.setUrl("applyCar/climeApprovalTask.action");
				carapply.setParam("taskId,result,status,openId");
				String comment=null;
				if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
					comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
				}
				carapply.setComment(comment);
				BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
				if(carInfoByNum!=null){
					String carUrl=carInfoByNum.getCarUrl();
					carapply.setCarUrl(carUrl);
				}
				list.add(carapply);
			}
		}

		//用印管理
		List<Task> task4 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
		List<Task> task5 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
		List<Task> task6 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
		List<Task> task7 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
		//一类审批
		Map<String,String> busAndTaskId4 = workflowUtil.getTaskAndBussIdByTask(task4);
		List<BGzapplyinfo> printApplyList1= bGzapplyinfoService.findTasksByStatus(busAndTaskId4.keySet(),"待审批");
		for(BGzapplyinfo applyinfo:printApplyList1){
//			if(departList.size()>0){&&departList.contains(applyinfo.getDepartmentid())
			if(user!=null){	
			if(user.getCode().equals(applyinfo.getApproveman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId4.get(str);
					applyinfo.setTaskId(tid);
					applyinfo.setUrl("print/climeApproval0Task.action");
					applyinfo.setParam("taskId,status,result");
					String comment=null;
					if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
						comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
					}
					applyinfo.setComment(comment);
					applyinfo.setTitle("用印申请");
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					list.add(applyinfo);
				}
			}
		}
		//二类审批
		Map<String,String> busAndTaskId5= workflowUtil.getTaskAndBussIdByTask(task5);
		List<BGzapplyinfo> printApplyList2= bGzapplyinfoService.findTasksByStatus(busAndTaskId5.keySet(),"待审批");
		for(BGzapplyinfo applyinfo:printApplyList2){
//			if(departList.size()>0){&&departList.contains(applyinfo.getDepartmentid())
			if(user!=null){	
			if(user.getCode().equals(applyinfo.getApproveman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId5.get(str);
					applyinfo.setTaskId(tid);
					applyinfo.setUrl("print/climeApproval1Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					list.add(applyinfo);
				}
			}
		}
		//三类审批
		Map<String,String> busAndTaskId6 = workflowUtil.getTaskAndBussIdByTask(task6);
//		List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findTasksByStatuses(busAndTaskId6.keySet(),"待审批","通过");
		List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findTasksLikeStatuses(busAndTaskId6.keySet(),"%通过%");
		for(BGzapplyinfo applyinfo:printApplyList3){
			if(user!=null){
				if(user.getCode().equals(applyinfo.getBusinessManager())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId6.get(str);
					applyinfo.setTaskId(tid);
					if(applyinfo.getBusinessManager().equals(applyinfo.getConfirmman())){
						if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
							applyinfo.setDisplayTime("1");
						}
					}
					applyinfo.setUrl("print/climeApproval2Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					list.add(applyinfo);
				}
			}
		}
		//四类审批
		Map<String,String> busAndTaskId7 = workflowUtil.getTaskAndBussIdByTask(task7);
//		List<BGzapplyinfo> printApplyList4= bGzapplyinfoService.findTasksByStatuses(busAndTaskId7.keySet(),"待审批","通过");
		List<BGzapplyinfo> printApplyList4= bGzapplyinfoService.findTasksLikeStatuses(busAndTaskId7.keySet(),"%通过%");
		for(BGzapplyinfo applyinfo:printApplyList4){
			if(user!=null){
				if(user.getCode().equals(applyinfo.getConfirmman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId7.get(str);
					applyinfo.setTaskId(tid);
					if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
						applyinfo.setDisplayTime("1");
					}
					applyinfo.setUrl("print/climeApproval3Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					list.add(applyinfo);
				}
			}
		}
		sort(list);
		Map<String,Object> mainmap = paging(list, nowPage, pageSize);
		return AnccResult.ok(mainmap);
	}
	
	
	/**
	 * 财务审批
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/thirdMainapprove")
	@ResponseBody
	public AnccResult thirdMainapprove(String openId,Integer nowPage,Integer pageSize){
		List<publicEntity> list = new ArrayList<>();
		Set<UserEntity> listroleuser = dynamicGetRoleUtil.getApprovalUsers("财务审批");
		for(UserEntity roleUser:listroleuser){

			if(roleUser!=null&&roleUser.getOpenid()!=null&&!"".equals(roleUser.getOpenid())&&openId.equals(roleUser.getOpenid())){
				List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity = privatecarinvoiceMapper.selectPrivatecarinvoiceEntity();
				for(PrivatecarinvoiceEntity privatecarinvoiceEntity:selectPrivatecarinvoiceEntity){
					privatecarinvoiceEntity.setTitle("财务审批私车");
					privatecarinvoiceEntity.setSortTime(privatecarinvoiceEntity.getApplyTime());
					String applyIds = privatecarinvoiceEntity.getApplyIds();
					PrivateCarEntity privateCar = new PrivateCarEntity();
					if(applyIds.indexOf(",")==-1){
						 privateCar = privateMapper.getPrivateCar(applyIds);
					}
					privateCar = privateMapper.getPrivateCar(applyIds.split(",")[0]);
					if(privateCar!=null){
						privatecarinvoiceEntity.setSureLength(String.valueOf(applyIds.split(",").length+""));
						privatecarinvoiceEntity.setFatherId(privatecarinvoiceEntity.getApplyId());
						list.add(privatecarinvoiceEntity);
					}
				}
				List<Task> taskByIds3 = workflowUtil.getTaskByIds("businesshospitality","financialapproval");
				Map<TaskDTO, String> map = getTaskAndBussIdByTask(taskByIds3);
				for(TaskDTO taskDto:map.keySet()){
					String id = taskDto.getBussinessId();
					id = id.split("\\:")[1];
					/*////system.out.println("id是"+id);*/
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					/*////system.out.println("enteratuin是"+entertain);*/
						if(taskDto.getKey().equals("financialapproval")){//财务
							if(entertain!=null){
								List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
								entertain.setEntertainregisterinfo(number);
								entertain.setUrl("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem");
								entertain.setShowButton("0");
								String taskId = map.get(taskDto);
								entertain.setTaskId(taskId);
								entertain.setTitle("业务招待");
								entertain.setFatherId(entertain.getId()+"");
								entertain.setSortTime(entertain.getApplyTime());
								//图片路径
								entertain.setImgUrl("https://gongche.hfga.com.cn/HFOANEW/images/WeiXin/公车.png");
								list.add(entertain);
							}
						}
				}				
			}
		}
		sort(list);
		List <publicEntity> listnew = new ArrayList(new TreeSet(list)); 
		Map<String,Object> minmap = paging(list, nowPage, pageSize);
	
		return AnccResult.ok(minmap);
	}
	
	
	
	
	/**
	 * 财务查看近一个月我已审批的
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/thirdMainapproved")
	@ResponseBody
	public AnccResult thirdMainapproved(String openId,Integer nowPage,Integer pageSize){
		List<publicEntity> list = new ArrayList<>();
		Calendar after = Calendar.getInstance();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
	    after.add(Calendar.DAY_OF_MONTH, -30);
	    Date afterThrid = after.getTime();
		Set<UserEntity> listroleuser = dynamicGetRoleUtil.getApprovalUsers("财务审批");
		for(UserEntity roleUser:listroleuser){
			if(roleUser!=null&&openId.equals(roleUser.getOpenid())){
				List<HistoricTaskInstance> listhist = historyService//与历史数据（历史表）相关的Service
		                .createHistoricTaskInstanceQuery().processDefinitionKey("businesshospitality")
						.taskDefinitionKey("financialapproval").list();
						if(listhist.size()>0&&listhist!=null){
							for(HistoricTaskInstance his:listhist){
								Date now = new Date();
								//////system.out.println(his.getEndTime());
								if(his.getEndTime()!=null&&afterThrid.getTime()<=his.getEndTime().getTime()&&his.getEndTime().getTime()<=now.getTime()){
									 HistoricProcessInstance processInstance = historyService//与历史数据（历史表）相关的Service
					                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
					                        .processInstanceId(his.getExecutionId())//使用流程实例ID查询
					                        .singleResult();
									 if(processInstance!=null){
										 String id = processInstance.getBusinessKey();
										 if(id.indexOf("businesshospitality")!=-1){
												//.out.println("业务招待");
												id = id.split("\\:")[1];
												Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
												if(entertain!=null){
													entertain.setTitle("业务招待");
													entertain.setUrl("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem");
													entertain.setShowButton("0");
													entertain.setSortTime(entertain.getApplyTime());
													entertain.setFatherId(entertain.getId()+"");
													entertain.setEntertainregisterinfo(entertainregisterinfoMapper.getNumber(entertain.getNumber()));
													list.add(entertain);
													
												}
											}
									 }
								}
							}
						}
						Calendar calendar = new GregorianCalendar();
						calendar.setTime(new Date());
						calendar.add(calendar.DATE,1);
						List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity = privatecarinvoiceMapper.invoiceDisplayApplyTime(smf.format(afterThrid), smf.format(calendar.getTime()));
						for(PrivatecarinvoiceEntity privatecarinvoiceEntity:selectPrivatecarinvoiceEntity){
							privatecarinvoiceEntity.setTitle("财务审批私车");
							privatecarinvoiceEntity.setSortTime(privatecarinvoiceEntity.getApplyTime());
							String applyIds = privatecarinvoiceEntity.getApplyIds();
							PrivateCarEntity privateCar = new PrivateCarEntity();
							if(applyIds.indexOf(",")==-1){
								privateCar = privateMapper.getPrivateCar(applyIds);
							}
							
							privateCar = privateMapper.getPrivateCar(applyIds.split(",")[0]);
							if(privateCar!=null){
								privatecarinvoiceEntity.setSureLength(String.valueOf(applyIds.split(",").length+""));
								privatecarinvoiceEntity.setFatherId(privatecarinvoiceEntity.getApplyId());
								list.add(privatecarinvoiceEntity);
							}
							
						}
						
						
				}
				
			}
			/*sort(list);*/
			List <publicEntity> listnew = new ArrayList(new TreeSet(list)); 
			Map<String,Object> minmap = paging(listnew, nowPage, pageSize);
			return AnccResult.ok(minmap);
	}
	
	
	
	
	
	public void sort (List<publicEntity>list){
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
				return (int) (after.getTime()-before.getTime());
			}
			
		});
	}
	//获取流程角色id
	public List<Integer> getHandleMan(String name){
		BProcess process=bProcessService.getprocessByName(name);
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		return roleIds;
	}
	/**
	 * 待我审批推送
	 */
	@RequestMapping("/mainApprovalMessage")
	@ResponseBody
	public  int mainApprovalMessage(String openId,Integer nowPage,Integer pageSize){
		/*Map<String,Object> mainControcller = mainControcller(openId,nowPage,pageSize);*/
		int count=0;
		if(!openId.equals("")&&openId!=null){
			AnccResult mainControcller = mainControcller(openId, nowPage, pageSize);
			
			Object data = mainControcller.getData();
			@SuppressWarnings("unchecked")
			Iterator iter = ((Map<String, Object>) data).entrySet().iterator();
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			if(entry.getKey().equals("pagesize")){
				Object val = entry.getValue();
				count = Integer.parseInt(val+"");
			}
			
		}
		return count;
	}
	/**
	 * 待我处理推送
	 */
	@RequestMapping("/mainHandleMessage")
	@ResponseBody
	public  int mainHandleMessage(String openId,Integer nowPage,Integer pageSize){
		int count=0;
		if(openId!=null&&!openId.equals("")){
			AnccResult mainControcller = mainApply(openId,nowPage,pageSize);
			Object data = mainControcller.getData();
			@SuppressWarnings("unchecked")
			Iterator iter = ((Map<String, Object>) data).entrySet().iterator();
			while (iter.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			if(key.equals("pagesize")){
				Object val = entry.getValue();
				count=Integer.parseInt(val+"");
			}
			
		}
		}
		return count;
	}
	/**
	 * 查看我已审批的
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/mainApproved")
	@ResponseBody
	public AnccResult mainApprovedControcller(String openId,Integer nowPage,Integer pageSize){
		List<publicEntity> list = new ArrayList<>();
		Integer userId=bUserService.getUserIdByOpenId(openId);
		String userCode ="";
		UserEntity user = bUserService.getUserById(userId);
		if(user!=null){
			userCode = user.getCode();
		}
		List<HistoricTaskInstance> listhist = historyService//与历史数据（历史表）相关的Service
                .createHistoricTaskInstanceQuery()//创建历史任务实例查询
                .taskAssignee(userCode)//指定历史任务的办理人
                .list();
		if(listhist!=null){
			for(HistoricTaskInstance historicTaskInstance:listhist){
				Date endTime = historicTaskInstance.getEndTime();
				HistoricProcessInstance pi = null;
				if(endTime!=null){
					pi = historyService//与历史数据（历史表）相关的Service
		                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
		                        .processInstanceId(historicTaskInstance.getExecutionId())//使用流程实例ID查询
		                        .singleResult();
				}
	
				if(pi!=null){
					if(pi.getEndTime()==null){
						String startUserId = pi.getStartUserId();
						if(null!=startUserId&&!startUserId.equals(userCode)){
							////system.out.println("用户主键"+pi.getBusinessKey());
							String id = pi.getBusinessKey();
							if(id.indexOf("applyExpenses")!=-1){
								////system.out.println("差旅费");
								id = id.split("\\:")[1];
								////system.out.println("id是"+id);
								ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
								if(applyExpens!=null){
									if(!applyExpens.getApproveState().equals("待审批")){
										applyExpens.setTitle("差旅费");
										applyExpens.setSortTime(applyExpens.getApplyTime());
										applyExpens.setFaterApplyman(applyExpens.getApplyMan());
										applyExpens.setFatherdepartment(applyExpens.getDepartment());
										applyExpens.setFathertitle(applyExpens.getTitle());
										applyExpens.setFatherId(applyExpens.getId());
										list.add(applyExpens);
									}
								}
							}
							if(id.indexOf("businesshospitality")!=-1){
								////system.out.println("业务招待");
								id = id.split("\\:")[1];
								Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
								if(entertain!=null){
									if(!entertain.getStatus().equals("待审核")){
										entertain.setTitle("业务招待");
										entertain.setSortTime(entertain.getApplyTime());
										entertain.setFaterApplyman(entertain.getManager());
										entertain.setFatherdepartment(entertain.getDepartment());
										entertain.setFathertitle(entertain.getTitle());
										entertain.setFatherId(String.valueOf(entertain.getId()+""));
										list.add(entertain);
									}
									
								}
							}
							if(id.indexOf("privateCar")!=-1){
								////system.out.println("私车公用");
								id = id.split("\\:")[1];
								PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
								if(privateCar!=null){
									if(!privateCar.getStatus().equals("待审核")){
										privateCar.setTitle("私车公用");
										privateCar.setSortTime(privateCar.getApplyTime());
										privateCar.setFaterApplyman(privateCar.getApplyMan());
										privateCar.setFatherdepartment(privateCar.getDepartment());
										privateCar.setFathertitle(privateCar.getTitle());
										privateCar.setFatherId(privateCar.getApplyId());
										list.add(privateCar);
									}
								}
							}
							if(id.indexOf("leaverYear")!=-1){
								id = id.split("\\:")[1];
								LearYear learYear = yearLearMapper.findById(id);
								if(learYear!=null){
									if(!learYear.getStatus().equals("待审批")){
										List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
										learYear.setTitle("年假管理");
										learYear.setLeaver(listLearTime);
										learYear.setSortTime(learYear.getApplyTime());
										learYear.setFaterApplyman(learYear.getApplyMan());
										learYear.setFatherdepartment(learYear.getDepartment());
										learYear.setFathertitle(learYear.getTitle());
										learYear.setFatherId(learYear.getId());
										list.add(learYear);
									}
									
								}
							}
						}
					}
					
				}
			}
		}
		
		//部门idlist
		List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
		//获取登录用户的所有角色id
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		//获取领导级别的角色
		Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
		roleIdFromClass.retainAll(roleIdByUserId);
		//公车管理
		List<CarApplyInfo2> carApplyList1= bCarapplymanageService.findByOutOrInMan(user.getCode());
		for(CarApplyInfo2 carapply:carApplyList1){
			if(carapply.getStatus()!=5&&!"已归还".equals(carapply.getState())){
				carapply.setTitle("公车申请");
				carapply.setDepartment(carapply.getDepartment().replace(",", ""));
				carapply.setDepartmentId(carapply.getDepartmentId().replace(",", ""));
				carapply.setUrl("applyCar/climeApprovalTask.action");
				carapply.setParam("taskId,result,status,openId");
				carapply.setSortTime(carapply.getApplyTime());
				carapply.setFaterApplyman(carapply.getApplyMan());
				carapply.setFatherdepartment(carapply.getDepartment());
				carapply.setFathertitle(carapply.getTitle());
				carapply.setFatherId(carapply.getApplyId());
				BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
				if(carInfoByNum!=null){
					String carUrl=carInfoByNum.getCarUrl();
					carapply.setCarUrl(carUrl);
				}
				list.add(carapply);
			}
		}
		//用印管理
		//一类审批
		List<BGzapplyinfo> printApplyList1= bGzapplyinfoService.findApprovalAndLable(user.getCode(),1);
		for(BGzapplyinfo applyinfo:printApplyList1){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(String.valueOf(applyinfo.getId()+""));
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
			
		}
		//二类审批
		List<BGzapplyinfo> printApplyList2= bGzapplyinfoService.findBussinessAndLable(user.getCode(),2);
		for(BGzapplyinfo applyinfo:printApplyList2){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(String.valueOf(applyinfo.getId()+""));
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
			}
		//三类审批
		List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findConfirmAndLable(user.getCode(),3);
		for(BGzapplyinfo applyinfo:printApplyList3){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(String.valueOf(applyinfo.getId()+""));
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
		}
		/*mainmap.put("count",list.size());
		mainmap.put("pagesize",list.size());
		mainmap.put("list", list);*/
		/*sort(list);*/
		/*List <publicEntity> listnew = new ArrayList(new TreeSet(list)); */
		List <publicEntity> listnew = new ArrayList(new TreeSet(list));
		sort(listnew);
		Map<String,Object> mainmap = paging(listnew, nowPage, pageSize);
		return AnccResult.ok(mainmap);
	}
	/**
	 * 查看我发起的（待我处理的）
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/mainApply")
	@ResponseBody
	public AnccResult mainApply(String openId,Integer nowPage,Integer pageSize){

		
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		String userCode = "";
		if(user!=null){
			userCode = user.getCode();
		}
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userCode)
				 .orderByTaskCreateTime().desc()
				 .list();
		
				/* .listPage(nowPage, pageSize);*/
		////system.out.println("任务是"+tasks);
		
		Map<TaskDTO, String> map = getTaskAndBussIdByTask(tasks);
		////system.out.println("mnaps"+map.keySet());
		List<publicEntity> list = new ArrayList<>();       
		for(TaskDTO taskDto:map.keySet()){
			////system.out.println(taskDto);
			String id = taskDto.getBussinessId();
			String processInstanceId = taskDto.getProcessInstanceId();
			String startUserId = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult().getStartUserId();
			HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstanceId).singleResult();
			////system.out.println("用户id是"+startUserId);
			////system.out.println("用户主键Id是"+id);
			if(null!=startUserId&&startUserId.equals(userCode)){
				if(id.indexOf("applyExpenses")!=-1){
					////system.out.println("差旅费");
					id = id.split("\\:")[1];
					////system.out.println("id是"+id);
					ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
					if(applyExpens!=null){
						String taskId = map.get(taskDto);
						applyExpens.setTaskId(taskId);
						applyExpens.setSortTime(applyExpens.getApplyTime());
						applyExpens.setFatherId(applyExpens.getId());
						if(taskDto.getKey().equals("cltjsq")){
							applyExpens.setApproveState("已驳回");
						}else{
							applyExpens.setApproveState(taskDto.getName());
						}
						applyExpens.setTitle("差旅费");
						if(taskDto.getKey().equals("cltjsq")){//驳回修改
							String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
							////system.out.println("查看的Id是"+instanceId);
							List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
							if(processInstanceComments!=null&&processInstanceComments.size()>0){
								applyExpens.setComment(processInstanceComments.get(0).getFullMessage());
							}
							applyExpens.setUrl("/pages/operatePages/myCheck/checkItem/travelItem/travelItem");
							applyExpens.setCanConfirm("0");
							applyExpens.setCanUpdate("1");
						}
						if(taskDto.getKey().equals("applyStaff")){//员工确认
							if(applyExpens.getApproveState().equals("驳回待修改")){
								String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
								////system.out.println("查看的Id是"+instanceId);
								List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
								if(processInstanceComments!=null&&processInstanceComments.size()>0){
									applyExpens.setComment(processInstanceComments.get(0).getFullMessage());
									applyExpens.setApproveState("财务驳回");
								}
							}
							applyExpens.setUrl("/pages/operatePages/myCheck/checkItem/travelItem/travelItem");
							applyExpens.setCanConfirm("1");
							applyExpens.setCanUpdate("0");
						}						
						list.add(applyExpens);
					}
				}
				if(id.indexOf("businesshospitality")!=-1){
					////system.out.println("业务招待");
					id = id.split("\\:")[1];
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					if(entertain!=null){
						entertain.setFatherId(entertain.getId()+"");
						List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
						String taskId = map.get(taskDto);
						entertain.setSortTime(entertain.getApplyTime());
						entertain.setTaskId(taskId);
						entertain.setTitle("业务招待");
						if(taskDto.getKey().equals("businessapply")){
							entertain.setStatus("已驳回");
						}else{
							entertain.setStatus(taskDto.getName());
						}
						
						if(taskDto.getKey().equals("businessapply")){//驳回
							String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
							////system.out.println("查看的Id是"+instanceId);
							List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
							if(processInstanceComments!=null&&processInstanceComments.size()>0){
								entertain.setComment(processInstanceComments.get(0).getFullMessage());
							}
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
							entertain.setCanConfirm("0");
							entertain.setCanUpdate("1");
						}else if(taskDto.getKey().equals("registration")){//事后登记
							if(entertain.getStatus().equals("驳回")){
								String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
								////system.out.println("查看的Id是"+instanceId);
								List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
								if(processInstanceComments!=null&&processInstanceComments.size()>0){
									entertain.setComment(processInstanceComments.get(0).getFullMessage());
									entertain.setStatus("财务驳回");
								}
							}
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
							entertain.setCanConfirm("0");
							entertain.setCanUpdate("0");
							entertain.setCanReimburse("1");
						}
						entertain.setEntertainregisterinfo(number);
						
						list.add(entertain);
					}
				}
				if(id.indexOf("privateCar")!=-1){
					////system.out.println("私车公用");
					id = id.split("\\:")[1];
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
					if(privateCar!=null){
						privateCar.setStatus(taskDto.getName());
						String taskId = map.get(taskDto);
						privateCar.setFatherId(privateCar.getApplyId());
						privateCar.setSortTime(privateCar.getApplyTime());
						if(taskDto.getKey().equals("privateCarApply")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("0");
							privateCar.setStatus("已驳回");
							privateCar.setCanUpdate("1");
							String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
							////system.out.println("查看的Id是"+instanceId);
							List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
							if(processInstanceComments!=null&&processInstanceComments.size()>0){
								privateCar.setComment(processInstanceComments.get(0).getFullMessage());
							}
						}
						if(taskDto.getKey().equals("privateCarStaff")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("1");
							privateCar.setCanUpdate("0");
						}
						privateCar.setTitle("私车公用");
						if(taskDto.getKey().equals("privateCarReimbursement")){
							privateCar.setTitle("私车公用凭票报销");
							if(privateCar.getIfPass().equals("财务驳回")){
								String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
								////system.out.println("查看的Id是"+instanceId);
								List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
								if(processInstanceComments!=null&&processInstanceComments.size()>0){
									privateCar.setComment(processInstanceComments.get(0).getFullMessage());
									privateCar.setStatus("财务驳回");
								}
							}
						}
						privateCar.setTaskId(taskId);
						list.add(privateCar);
					}
				}
				if(id.indexOf("leaverYear")!=-1){
					id = id.split("\\:")[1];
					LearYear learYear = yearLearMapper.findById(id);
					if(learYear!=null){
						List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
						learYear.setTitle("年假管理");
						String taskId = map.get(taskDto);
						learYear.setTaskId(taskId);
						learYear.setSortTime(learYear.getApplyTime());
						learYear.setFatherId(learYear.getId());
						learYear.setLeaver(listLearTime);
						if(taskDto.getKey().equals("leaverapply")){
							String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
							////system.out.println("查看的Id是"+instanceId);
							List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
							if(processInstanceComments!=null&&processInstanceComments.size()>0){
								learYear.setComment(processInstanceComments.get(0).getFullMessage());
							}
							learYear.setStatus("已驳回");
							learYear.setConUpdate("1");
						}
						
						if(taskDto.getKey().equals("leaveYearStafuser")){
							learYear.setConUpdate("1");
							learYear.setConConfirm("1");
						}
						
						list.add(learYear);
					}
				}
				
			}
		}	
		//获取进行中的，并且“我”可操作的
		//公车管理
		List<Task> task0 =new ArrayList<>();
		List<Task> task1 =workflowUtil.getTaskByIds("applyCarKey", "sqjc");
		List<Task> task2 =workflowUtil.getTaskByIds("applyCarKey", "sp");
		List<Task> task3 =workflowUtil.getTaskByIds("applyCarKey", "jcck");
		List<Task> task4 =workflowUtil.getTaskByIds("applyCarKey", "hcrk");
		task0.addAll(task1);
		task0.addAll(task2);
		task0.addAll(task3);
		task0.addAll(task4);
		//根据任务获取业务
		Map<String,String> busAndTaskId1 = workflowUtil.getTaskAndBussIdByTask(task0);
		if(user!=null){
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByUserCode(busAndTaskId1.keySet(),user.getCode());
			for(CarApplyInfo2 carapply:carApplyList){
				if(carapply.getStatus()==1){
					String str = carapply.getID()+"";
					String tid=busAndTaskId1.get(str);
					carapply.setTaskId(tid);
					String comment=null;
					if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
						comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
					}
					carapply.setComment(comment);
					carapply.setDepartment(carapply.getDepartment().replace(",", ""));
					carapply.setTitle("公车申请");
					carapply.setCanRevoke("1");
					carapply.setCanUpdate("1");
					carapply.setFatherId(carapply.getApplyId());
					carapply.setSortTime(carapply.getApplyTime());
					BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					if(carInfoByNum!=null){
						String carUrl=carInfoByNum.getCarUrl();
						carapply.setCarUrl(carUrl);
						carapply.setCarryNum(carInfoByNum.getPeasonnum());
					}
					list.add(carapply);
				}
			}
		}
		
		//用印申请
		List<Task> task5 =new ArrayList<>();
		List<Task> task6 =workflowUtil.getTaskByIds("printApplyKey", "yysq");
		List<Task> task7 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
		List<Task> task8 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
		List<Task> task9 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
		List<Task> task10 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
		List<Task> task11 =workflowUtil.getTaskByIds("printApplyKey", "yycl");
		task5.addAll(task6);
		task5.addAll(task7);
		task5.addAll(task8);
		task5.addAll(task9);
		task5.addAll(task10);
		task5.addAll(task11);
		//根据任务获取业务
		Map<String,String> busAndTaskId2 = workflowUtil.getTaskAndBussIdByTask(task5);
		List<BGzapplyinfo> gzlist= bGzapplyinfoService.findTasksByUserCode(busAndTaskId2.keySet(),user.getCode());
		for(BGzapplyinfo applyinfo:gzlist){
//			if(applyinfo.getStatus().equals("被否决")){
				if(applyinfo.getStatus().contains("否决")){
				String str = applyinfo.getId()+"";
				String tid=busAndTaskId2.get(str);
				String comment=null;
				if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
					comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
				}
				applyinfo.setComment(comment);
				applyinfo.setTaskId(tid);
				applyinfo.setCanUpdate("1");
				applyinfo.setCanRevoke("1");
				if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
					applyinfo.setDisplayTime("1");
				}
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setTitle("用印申请");
				list.add(applyinfo);
			}
		}
//		if(list!=null&&list.size()>0){
//			list.subList(nowPage, pageSize);
//		}

		sort(list);
		/*List <publicEntity> listnew = new ArrayList(new TreeSet(list)); */

		/*mainMap.put("count",list.size());
		mainMap.put("pagesize",tasks.size());
		mainMap.put("list", list);*/
		Map<String,Object> mainMap = paging(list, nowPage, pageSize);
		return AnccResult.ok(mainMap);
	}
	
	
	
	
	/**
	 * 查看总览也就是已完成的
	 * 
	 * @param openId
	 * @return
	 */
	@RequestMapping("/mainQueryHistory")
	@ResponseBody
	public AnccResult mainQueryHistory(String openId){		
		List<Object> list = new ArrayList<>();
		List<HistoricProcessInstance> historicProcessInstanceList = 
				historyService.createHistoricProcessInstanceQuery().startedBy(openId).list();
		for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
			String id = historicProcessInstance.getBusinessKey();
			if(null!=historicProcessInstance.getEndActivityId()&&!historicProcessInstance.getEndActivityId().equals("")){
				Task task = taskService.createTaskQuery().executionId(historicProcessInstance.getId()).singleResult();
				String taskId = "";
				if(task==null){
					if(id.indexOf("applyExpenses")!=-1){
						////system.out.println("差旅费");
						id = id.split("\\:")[1];
						////system.out.println("id是"+id);
						ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
						if(applyExpens!=null){
							applyExpens.setTaskId(taskId);
							applyExpens.setTitle("差旅费");
							list.add(applyExpens);
						}
					}
					if(id.indexOf("businesshospitality")!=-1){
						////system.out.println("业务招待");
						id = id.split("\\:")[1];
						Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
						if(entertain!=null){
							/*String taskId = map.get(taskDto);
							entertain.setTaskId(taskId);
							entertain.setTitle("业务招待");
							entertain.setTaskId(taskId);*/
							entertain.setTitle("业务招待");
							list.add(entertain);
						}
					}
					if(id.indexOf("privateCar")!=-1){
						////system.out.println("私车公用");
						id = id.split("\\:")[1];
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
						/*String taskId = map.get(taskDto);
						if(taskDto.getKey().equals("privateCarApply")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("0");
							privateCar.setCanUpdate("1");
						}
						if(taskDto.getKey().equals("privateCarStaff")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("0");
							privateCar.setCanUpdate("0");
						}
						privateCar.setTaskId(taskId);*/
						if(privateCar!=null){
							privateCar.setTitle("私车公用");
							privateCar.setStatus("已完成");
							privateCar.setTaskId(taskId);
							list.add(privateCar);
						}						
					}
					if(id.indexOf("applyCar")!=-1){
						id = id.split("\\:")[1];
						CarApplyInfo2 carApplyInfo2 = bCarapplymanageMapper.getById(Integer.parseInt(id));
						list.add(carApplyInfo2);
					}
					if(id.indexOf("leaverYear")!=-1){
						id = id.split("\\:")[1];
						LearYear learYear = yearLearMapper.findById(id);
						if(learYear!=null){
							List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
							learYear.setTitle("年假管理");
							learYear.setLeaver(listLearTime);
							list.add(learYear);
						}
					}
				}
				/*if(task!=null){
					taskId = task.getId();
				}
				System.out.println(""+historicProcessInstance.getId());
				if(id.indexOf("applyExpenses")!=-1){
					System.out.println("差旅费");
					id = id.split("\\:")[1];
					System.out.println("id是"+id);
					ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
					if(applyExpens!=null){
						applyExpens.setTaskId(taskId);
						applyExpens.setTitle("差旅费");
						applyExpens.setTaskId(taskId);
						list.add(applyExpens);
					}
				}
				if(id.indexOf("businesshospitality")!=-1){
					System.out.println("业务招待");
					id = id.split("\\:")[1];
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					if(entertain!=null){
						String taskId = map.get(taskDto);
						entertain.setTaskId(taskId);
						entertain.setTitle("业务招待");
						entertain.setTaskId(taskId);
						list.add(entertain);
					}
				}
				if(id.indexOf("privateCar")!=-1){
					System.out.println("私车公用");
					id = id.split("\\:")[1];
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
					String taskId = map.get(taskDto);
					if(taskDto.getKey().equals("privateCarApply")){
						privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
						privateCar.setCanConfirm("0");
						privateCar.setCanUpdate("1");
					}
					if(taskDto.getKey().equals("privateCarStaff")){
						privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
						privateCar.setCanConfirm("0");
						privateCar.setCanUpdate("0");
					}
					privateCar.setTaskId(taskId);
					if(privateCar!=null){
						privateCar.setTitle("私车公用");
						privateCar.setTaskId(taskId);
						list.add(privateCar);
					}
					
				}
				if(id.indexOf("applyCar")!=-1){
					id = id.split("\\:")[1];
					CarApplyInfo2 carApplyInfo2 = bCarapplymanageMapper.getById(Integer.parseInt(id));
					list.add(carApplyInfo2);
				}*/
			}
		}
		
		return AnccResult.ok(list);
	}
	
	
	
	
	/**
	 * 
	 * @param openId
	 * @return
	 */
	@RequestMapping("/mainRuntimeHistory")
	@ResponseBody
	public AnccResult mainRuntimeHistory(String openId,Integer nowPage,Integer pageSize){
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		String userCode = "";
		if(user!=null){
			userCode = user.getCode();
		}
		List<publicEntity> list = new ArrayList<>();
		List<HistoricProcessInstance> historicProcessInstanceList = 
				historyService.createHistoricProcessInstanceQuery().startedBy(userCode).list();
		for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
			////system.out.println(""+historicProcessInstance.getEndActivityId());
			Date endActivityId = historicProcessInstance.getEndTime();
			if(null==endActivityId){
				Task task = taskService.createTaskQuery().executionId(historicProcessInstance.getId()).singleResult();
				String taskId = "";
				if(task!=null){
					String id = historicProcessInstance.getBusinessKey();
					taskId = task.getId();
					if(id.indexOf("applyExpenses")!=-1){
						////system.out.println("差旅费");
						id = id.split("\\:")[1];
						////system.out.println("id是"+id);
						ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
						if(applyExpens!=null){
							/*applyExpens.setTaskId(taskId);*/
							applyExpens.setApproveState(task.getName());
							if(!task.getTaskDefinitionKey().equals("applyStaff")){
								if(!task.getTaskDefinitionKey().equals("cltjsq")){
									applyExpens.setSortTime(applyExpens.getApplyTime());
									applyExpens.setFatherId(applyExpens.getId());
									applyExpens.setTitle("差旅费");
									applyExpens.setTaskId(taskId);
									list.add(applyExpens);
								}
							}
							
						}
					}
					if(id.indexOf("businesshospitality")!=-1){
						id = id.split("\\:")[1];
						Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
						if(entertain!=null){
							if(!task.getTaskDefinitionKey().equals("businessapply")){
								if(!task.getTaskDefinitionKey().equals("registration")){
									List<Entertainregisterinfo> entertainregisterinfo = entertainregisterinfoMapper.getNumber(entertain.getNumber());
									entertain.setEntertainregisterinfo(entertainregisterinfo);
									entertain.setTitle("业务招待");
									entertain.setFatherId(entertain.getId()+"");
									entertain.setStatus(task.getName());
									entertain.setSortTime(entertain.getApplyTime());
									entertain.setTaskId(taskId);
									list.add(entertain);
								}
							}
						}
					}
					if(id.indexOf("privateCar")!=-1){
						////system.out.println("私车公用");
						id = id.split("\\:")[1];
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
						if(!task.getTaskDefinitionKey().equals("privateCarApply")){
							if(!task.getTaskDefinitionKey().equals("privateCarStaff")){
								if(!task.getTaskDefinitionKey().equals("privateCarReimbursement")){
									if(privateCar!=null){
										privateCar.setFatherId(privateCar.getApplyId());
										privateCar.setTitle("私车公用");
										privateCar.setTaskId(taskId);
										privateCar.setSortTime(privateCar.getApplyTime());
										privateCar.setStatus(task.getName());
										if(task.getName().equals("私车提交申请")){
											privateCar.setStatus("已驳回");
										}
										list.add(privateCar);
									}
								}
							}
							
							
						}
						/*String taskId = map.get(taskDto);
						if(taskDto.getKey().equals("privateCarApply")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("0");
							privateCar.setCanUpdate("1");
						}
						if(taskDto.getKey().equals("privateCarStaff")){
							privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
							privateCar.setCanConfirm("0");
							privateCar.setCanUpdate("0");
						}
						privateCar.setTaskId(taskId);*/
						
						
						
					}
					if(id.indexOf("applyCar")!=-1){
						id = id.split("\\:")[1];
						CarApplyInfo2 carApplyInfo2 = bCarapplymanageMapper.getById(Integer.parseInt(id));
						if(carApplyInfo2!=null&&carApplyInfo2.getApplyMan().equals(user.getCode())&&carApplyInfo2.getStatus()!=5&&carApplyInfo2.getStatus()!=1){
							String comment=null;
							if(!taskId.equals("null")&&workflowUtil.findCommentByTaskId(taskId).size()>0){
								comment=workflowUtil.findCommentByTaskId(taskId).get(0).getFullMessage();
							}
							if(carApplyInfo2.getStatus()<4){
								carApplyInfo2.setCanRevoke("1");
							}
							if(carApplyInfo2.getStatus()<3){
								carApplyInfo2.setCanRevoke("1");
								carApplyInfo2.setCanUpdate("1");
							}
							carApplyInfo2.setSortTime(carApplyInfo2.getApplyTime());
							carApplyInfo2.setComment(comment);
							carApplyInfo2.setFatherId(carApplyInfo2.getApplyId());
							carApplyInfo2.setTitle("公车申请");
							carApplyInfo2.setTaskId(taskId);
							carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
							BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carApplyInfo2.getCarCode());
							if(carInfoByNum!=null){
								String carUrl=carInfoByNum.getCarUrl();
								carApplyInfo2.setCarUrl(carUrl);
								carApplyInfo2.setCarryNum(carInfoByNum.getPeasonnum());
							}
							list.add(carApplyInfo2);
						}
					}
					if(id.indexOf("printApply")!=-1){
						id = id.split("\\:")[1];
						BGzapplyinfo applyinfo = bGzapplyinfoMapper.getById(Integer.parseInt(id));
						if(applyinfo!=null){
							if(applyinfo.getApplyusername().equals(user.getCode())&&!applyinfo.getStatus().contains("否决")){
								String comment=null;
								if(!taskId.equals("null")&&workflowUtil.findCommentByTaskId(taskId).size()>0){
									comment=workflowUtil.findCommentByTaskId(taskId).get(0).getFullMessage();
								}
								applyinfo.setTaskId(taskId);
								applyinfo.setFatherId(applyinfo.getId()+"");
								applyinfo.setCanUpdate("1");
								applyinfo.setCanRevoke("1");
								if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
									applyinfo.setDisplayTime("1");
								}
								applyinfo.setSortTime(applyinfo.getApplytime());
								applyinfo.setComment(comment);
								applyinfo.setTitle("用印申请");
								applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
								list.add(applyinfo);
							}
						}
					}
					if(id.indexOf("leaverYear")!=-1){
						id = id.split("\\:")[1];
						
						LearYear learYear = yearLearMapper.findById(id);
						if(!task.getTaskDefinitionKey().equals("leaverapply")){
							if(!task.getTaskDefinitionKey().equals("leaveYearStafuser")){
								if(learYear!=null){
									List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
									learYear.setTitle("年假管理");
									learYear.setConUpdate("1");
									learYear.setFatherId(learYear.getId());
									List<LearTime> seartTime = learTimeMapper.seartTime(id);
									////system.out.println("searchtime是"+seartTime);
									learYear.setCanRevoke("1");
									if(seartTime.size()>0){//说明修过年假
										learYear.setCanRevoke("0");
									}
									learYear.setLeaver(listLearTime);
									learYear.setSortTime(learYear.getApplyTime());
									learYear.setTaskId(taskId);
									list.add(learYear);
								}
							}
						}
					}
				}
			}
		}
		////system.out.println("list是"+list);
		
		List <publicEntity> listnew = new ArrayList<>(); 
		for(int i=0;i<list.size();i++){  
            if(!listnew.contains(list.get(i))){  
            	listnew.add(list.get(i));  
            }  
        }  
		sort(listnew);
		Map<String,Object> mainmap = new HashMap<>();
		int start = (nowPage - 1) * pageSize;
		int total = listnew.size();
		List<publicEntity> list_all = new ArrayList<>();
	    if (start + pageSize <= total)
	      list_all.addAll(listnew.subList(start, start + pageSize));
	    else
	      list_all.addAll(listnew.subList(start, total));

	    mainmap.put("pagesize", listnew.size());
	    mainmap.put("list", list_all);
		return AnccResult.ok(mainmap);
	}
	
	
	
	
	
	
	
	
	
	public Map<TaskDTO,String> getTaskAndBussIdByTask(List<Task> tasks){
		Map<TaskDTO, String> map = new LinkedHashMap<TaskDTO,String>();
		for(int i=0;i<tasks.size();i++){
			String taskId=tasks.get(i).getId();
			TaskDTO taskDTO = findBussinessIdByTaskId(taskId);
			map.put(taskDTO,taskId);
		}
		return map;
	}
	
	/**一：使用任务ID，查找业务对象ID*/
	public TaskDTO findBussinessIdByTaskId(String taskId) {
		TaskDTO taskdto = new TaskDTO();
		//1：使用任务ID，查询任务对象Task
		Task task = getTaskById(taskId);
		////system.out.println("task.getFormKey()是"+task.getTaskDefinitionKey());
		taskdto.setName(task.getName());
		taskdto.setKey(task.getTaskDefinitionKey());
		//2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService
				.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)//使用流程实例ID查询
				.singleResult();
		//4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		pi.getProcessInstanceId();
		
		////system.out.println("对象的key是"+buniness_key);
		//5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）
		String id = "";
		if(StringUtils.isNotBlank(buniness_key)){
			//截取字符串，取buniness_key小数点的第2个值
			/*id = buniness_key.split("\\:")[1];*/
			id = buniness_key;
		}
		taskdto.setBussinessId(id);
		taskdto.setProcessInstanceId(processInstanceId);
		//返回业务对象ID
		return taskdto;
	}
	
	/**根据taskID获取task*/
	public Task getTaskById(String taskId){
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
		return task;
	}
	
	
	
	/**
	 * 我发起的查询
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param applyMan
	 * @param departmentId
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/mainApplyScreen")
	@ResponseBody
	public AnccResult mainApplyScreen(String openId,Integer nowPage,Integer pageSize,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		//获取用户id
				Integer userId=bUserService.getUserIdByOpenId(openId);
				UserEntity user = bUserService.getUserById(userId);
				String userCode = "";
				if(user!=null){
					userCode = user.getCode();
				}
				List<Task> tasks = taskService.createTaskQuery().taskAssignee(userCode)
						 .orderByTaskCreateTime().desc()
						 .list();
				
						/* .listPage(nowPage, pageSize);*/
				////system.out.println("任务是"+tasks);
				
				Map<TaskDTO, String> map = getTaskAndBussIdByTask(tasks);
				////system.out.println("mnaps"+map.keySet());
				List<publicEntity> list = new ArrayList<>();       
				for(TaskDTO taskDto:map.keySet()){
					////system.out.println(taskDto);
					String id = taskDto.getBussinessId();
					String processInstanceId = taskDto.getProcessInstanceId();
					String startUserId = historyService.createHistoricProcessInstanceQuery()
							.processInstanceId(processInstanceId).singleResult().getStartUserId();
					HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
					////system.out.println("用户id是"+startUserId);
					////system.out.println("用户主键Id是"+id);
					if(null!=startUserId&&startUserId.equals(userCode)){
						if(id.indexOf("applyExpenses")!=-1){
							////system.out.println("差旅费");
							id = id.split("\\:")[1];
							////system.out.println("id是"+id);
							ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
							if(applyExpens!=null){
								String taskId = map.get(taskDto);
								applyExpens.setTaskId(taskId);
								applyExpens.setSortTime(applyExpens.getApplyTime());
								if(taskDto.getKey().equals("cltjsq")){
									applyExpens.setApproveState("已驳回");
								}else{
									applyExpens.setApproveState(taskDto.getName());
								}
								applyExpens.setTitle("差旅费");
								if(taskDto.getKey().equals("cltjsq")){//驳回修改
									applyExpens.setUrl("/pages/operatePages/myCheck/checkItem/travelItem/travelItem");
									applyExpens.setCanConfirm("0");
									applyExpens.setCanUpdate("1");
								}
								if(taskDto.getKey().equals("applyStaff")){//员工确认
									applyExpens.setUrl("/pages/operatePages/myCheck/checkItem/travelItem/travelItem");
									applyExpens.setCanConfirm("1");
									applyExpens.setCanUpdate("0");
								}
								applyExpens.setFaterApplyman(applyExpens.getApplyMan());
								applyExpens.setFatherdepartment(applyExpens.getDepartment());
								applyExpens.setFathertitle(applyExpens.getTitle());
								list.add(applyExpens);
							}
						}
						if(id.indexOf("businesshospitality")!=-1){
							////system.out.println("业务招待");
							id = id.split("\\:")[1];
							Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
							List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
							if(entertain!=null){
								String taskId = map.get(taskDto);
								entertain.setSortTime(entertain.getApplyTime());
								entertain.setTaskId(taskId);
								entertain.setTitle("业务招待");
								entertain.setFaterApplyman(entertain.getManager());
								entertain.setFatherdepartment(entertain.getDepartment());
								entertain.setFathertitle(entertain.getTitle());
								if(taskDto.getKey().equals("businessapply")){
									entertain.setStatus("已驳回");
								}else{
									entertain.setStatus(taskDto.getName());
								}
								
								if(taskDto.getKey().equals("businessapply")){//驳回
									entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
									entertain.setCanConfirm("0");
									entertain.setCanUpdate("1");
								}else if(taskDto.getKey().equals("registration")){//事后登记
									entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
									entertain.setCanConfirm("0");
									entertain.setCanUpdate("0");
									entertain.setCanReimburse("1");
								}
								entertain.setEntertainregisterinfo(number);
								
								list.add(entertain);
							}
						}
						if(id.indexOf("privateCar")!=-1){
							////system.out.println("私车公用");
							id = id.split("\\:")[1];
							PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
							if(privateCar!=null){
								String taskId = map.get(taskDto);
								privateCar.setSortTime(privateCar.getApplyTime());
								if(taskDto.getKey().equals("privateCarApply")){
									privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
									privateCar.setCanConfirm("0");
									privateCar.setCanUpdate("1");
								}
								if(taskDto.getKey().equals("privateCarStaff")){
									privateCar.setUrl("/pages/operatePages/myCheck/checkItem/privateItem/privateItem");
									privateCar.setCanConfirm("1");
									privateCar.setCanUpdate("0");
								}
								privateCar.setTitle("私车公用");
								privateCar.setFathertitle(privateCar.getTitle());
								if(taskDto.getKey().equals("privateCarReimbursement")){
									privateCar.setTitle("私车公用凭票报销");
								}
								
								if(taskDto.getKey().equals("privateCarApply")){
									privateCar.setStatus("已驳回");
								}else{
									privateCar.setStatus(taskDto.getName());
								}
								privateCar.setStatus(taskDto.getName());
								privateCar.setFaterApplyman(privateCar.getApplyMan());
								privateCar.setFatherdepartment(privateCar.getDepartment());
								privateCar.setTaskId(taskId);
								list.add(privateCar);
							}
						}
						if(id.indexOf("leaverYear")!=-1){
							////system.out.println("年假申请");
							id = id.split("\\:")[1];
							LearYear learYear = yearLearMapper.findById(id);
							if(learYear!=null){
								List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
								learYear.setTitle("年假管理");
								String taskId = map.get(taskDto);
								learYear.setTaskId(taskId);
								learYear.setSortTime(learYear.getApplyTime());
								learYear.setFaterApplyman(learYear.getApplyMan());
								learYear.setFatherdepartment(learYear.getDepartment());
								learYear.setFathertitle(learYear.getTitle());
								learYear.setLeaver(listLearTime);
								list.add(learYear);
							}
						}
						
					}
				}
			
				//获取进行中的，并且“我”可操作的
				//公车管理
				List<Task> task0 =new ArrayList<>();
				List<Task> task1 =workflowUtil.getTaskByIds("applyCarKey", "sqjc");
				List<Task> task2 =workflowUtil.getTaskByIds("applyCarKey", "sp");
				List<Task> task3 =workflowUtil.getTaskByIds("applyCarKey", "jcck");
				List<Task> task4 =workflowUtil.getTaskByIds("applyCarKey", "hcrk");
				task0.addAll(task1);
				task0.addAll(task2);
				task0.addAll(task3);
				task0.addAll(task4);
				//根据任务获取业务
				Map<String,String> busAndTaskId1 = workflowUtil.getTaskAndBussIdByTask(task0);
				List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByUserCode(busAndTaskId1.keySet(),user.getCode());
				for(CarApplyInfo2 carapply:carApplyList){
					if(carapply.getStatus()==1){
						String str = carapply.getID()+"";
						String tid=busAndTaskId1.get(str);
						carapply.setTaskId(tid);
						carapply.setDepartment(carapply.getDepartment().replace(",", ""));
						String comment=null;
						if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
							comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
						}
						carapply.setComment(comment);
						carapply.setTitle("公车申请");
						carapply.setCanRevoke("1");
						carapply.setCanUpdate("1");
						carapply.setSortTime(carapply.getApplyTime());
						carapply.setFaterApplyman(carapply.getApplyMan());
						carapply.setFatherdepartment(carapply.getDepartment());
						carapply.setFathertitle(carapply.getTitle());
						BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
						if(carInfoByNum!=null){
							String carUrl=carInfoByNum.getCarUrl();
							carapply.setCarUrl(carUrl);

						}
						list.add(carapply);
					}
				}
				//用印申请
				List<Task> task5 =new ArrayList<>();
				List<Task> task6 =workflowUtil.getTaskByIds("printApplyKey", "yysq");
				List<Task> task7 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
				List<Task> task8 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
				List<Task> task9 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
				List<Task> task10 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
				List<Task> task11 =workflowUtil.getTaskByIds("printApplyKey", "yycl");
				task5.addAll(task6);
				task5.addAll(task7);
				task5.addAll(task8);
				task5.addAll(task9);
				task5.addAll(task10);
				task5.addAll(task11);
				//根据任务获取业务
				Map<String,String> busAndTaskId2 = workflowUtil.getTaskAndBussIdByTask(task5);
				List<BGzapplyinfo> gzlist= bGzapplyinfoService.findTasksByUserCode(busAndTaskId2.keySet(),user.getCode());
				for(BGzapplyinfo applyinfo:gzlist){
//					if(applyinfo.getStatus().equals("被否决")){
						if(applyinfo.getStatus().contains("否决")){
						String str = applyinfo.getId()+"";
						String tid=busAndTaskId2.get(str);
						applyinfo.setTaskId(tid);
						String comment=null;
						if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
							comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
						}
						applyinfo.setComment(comment);
						applyinfo.setCanUpdate("1");
						applyinfo.setCanRevoke("1");
						if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
							applyinfo.setDisplayTime("1");
						}
						applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
						applyinfo.setSortTime(applyinfo.getApplytime());
						applyinfo.setTitle("用印申请");
						applyinfo.setFaterApplyman(applyinfo.getApplyusername());
						applyinfo.setFatherdepartment(applyinfo.getDepartment());
						applyinfo.setFathertitle(applyinfo.getTitle());
						list.add(applyinfo);
					}
				}

				sort(list);
				List<publicEntity> searchlist = search(list, type, beginTime, endTime, applyMan, departmentId);
				sort(searchlist);
				Map<String,Object> mainMap = paging(searchlist, nowPage, pageSize);
				return AnccResult.ok(mainMap);
	}
	
	
	
	/**
	 * 财务审批查询
	 * @param openId 唯一标识
	 * @param nowPage 1
	 * @param pageSize 4
	 * @param type 类型
	 * @param beginTime 开始
	 * @param endTime 结束
	 * @param applyMan 申请人
	 * @param departmentId 部门名称
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/thirdMainapproveSearch")
	@ResponseBody
	public AnccResult thirdMainapproveSearch(String openId,Integer nowPage,Integer pageSize,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		List<publicEntity> list = new ArrayList<>();
		Set<UserEntity> listroleuser = dynamicGetRoleUtil.getApprovalUsers("财务审批");
		for(UserEntity roleUser:listroleuser){
			if(roleUser!=null&&roleUser.getOpenid()!=null&&!"".equals(roleUser.getOpenid())&&openId.equals(roleUser.getOpenid())){
				List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity = privatecarinvoiceMapper.selectPrivatecarinvoiceEntity();
				for(PrivatecarinvoiceEntity privatecarinvoiceEntity:selectPrivatecarinvoiceEntity){
					privatecarinvoiceEntity.setTitle("财务审批私车");
					privatecarinvoiceEntity.setSortTime(privatecarinvoiceEntity.getApplyTime());
					privatecarinvoiceEntity.setFaterApplyman(privatecarinvoiceEntity.getApplyMan());
					privatecarinvoiceEntity.setFathertitle(privatecarinvoiceEntity.getTitle());
					String applyIds = privatecarinvoiceEntity.getApplyIds();
					PrivateCarEntity privateCar = new PrivateCarEntity();
					if(applyIds.indexOf(",")==-1){
						 privateCar = privateMapper.getPrivateCar(applyIds);
					}
					privateCar = privateMapper.getPrivateCar(applyIds.split(",")[0]);
					if(privateCar!=null){
						privatecarinvoiceEntity.setSureLength(String.valueOf(applyIds.split(",").length+""));
						privatecarinvoiceEntity.setFatherId(privatecarinvoiceEntity.getApplyId());
						privatecarinvoiceEntity.setFatherdepartment(privateCar.getDepartment());
						list.add(privatecarinvoiceEntity);
					}
				}
				List<Task> taskByIds3 = workflowUtil.getTaskByIds("businesshospitality","financialapproval");
				Map<TaskDTO, String> map = getTaskAndBussIdByTask(taskByIds3);
				for(TaskDTO taskDto:map.keySet()){
					String id = taskDto.getBussinessId();
					id = id.split("\\:")[1];
					/*////system.out.println("id是"+id);*/
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					/*////system.out.println("enteratuin是"+entertain);*/
						if(entertain!=null&&taskDto.getKey().equals("financialapproval")){//财务
							List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
							entertain.setEntertainregisterinfo(number);
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem");
							entertain.setShowButton("0");
							String taskId = map.get(taskDto);
							entertain.setTaskId(taskId);
							entertain.setTitle("业务招待");
							entertain.setFathertitle(entertain.getTitle());
							entertain.setFatherId(entertain.getId()+"");
							entertain.setSortTime(entertain.getApplyTime());
							entertain.setFaterApplyman(entertain.getManager());
							entertain.setFatherdepartment(entertain.getDepartment());
							//图片路径
							entertain.setImgUrl("https://gongche.hfga.com.cn/HFOANEW/images/WeiXin/公车.png");
							list.add(entertain);
						}
				}				
			}
		}
		System.out.println("listsize是"+list.size());
		List<publicEntity> searchlist = search(list, type, beginTime, endTime, applyMan, departmentId);
		sort(searchlist);
		Map<String,Object> minmap = paging(searchlist, nowPage, pageSize);
		
		return AnccResult.ok(minmap);
	}
	
	@RequestMapping("/thirdMainapprovedSerach")
	@ResponseBody
	public AnccResult thirdMainapprovedSerach(String openId,Integer nowPage,Integer pageSize,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		List<publicEntity> list = new ArrayList<>();
		Calendar after = Calendar.getInstance();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
	    after.add(Calendar.DAY_OF_MONTH, -30);
	    Date afterThrid = after.getTime();
	    Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		calendar.add(calendar.DATE,1);
		Set<UserEntity> listroleuser = dynamicGetRoleUtil.getApprovalUsers("财务审批");
		for(UserEntity roleUser:listroleuser){
			if(openId.equals(roleUser.getOpenid())){
				List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity = privatecarinvoiceMapper.invoiceDisplayApplyTime(smf.format(afterThrid), smf.format(calendar.getTime()));
				for(PrivatecarinvoiceEntity privatecarinvoiceEntity:selectPrivatecarinvoiceEntity){
					privatecarinvoiceEntity.setTitle("财务审批私车");
					privatecarinvoiceEntity.setSortTime(privatecarinvoiceEntity.getApplyTime());
					String applyIds = privatecarinvoiceEntity.getApplyIds();
					privatecarinvoiceEntity.setSureLength(String.valueOf(applyIds.split(",").length+""));
					privatecarinvoiceEntity.setSortTime(privatecarinvoiceEntity.getApplyTime());
					privatecarinvoiceEntity.setFaterApplyman(privatecarinvoiceEntity.getApplyMan());
					privatecarinvoiceEntity.setFathertitle(privatecarinvoiceEntity.getTitle());
					privatecarinvoiceEntity.setFatherId(privatecarinvoiceEntity.getApplyId());
					PrivateCarEntity privateCar = new PrivateCarEntity();
					if(applyIds.lastIndexOf(",")==1){
						privateCar = privateMapper.getPrivateCar(applyIds);
					}
					
					privateCar = privateMapper.getPrivateCar(applyIds.split(",")[0]);
					if(privateCar!=null){
						list.add(privatecarinvoiceEntity);
					}
					
				}
				List<HistoricTaskInstance> listhist = historyService//与历史数据（历史表）相关的Service
                .createHistoricTaskInstanceQuery().processDefinitionKey("businesshospitality")
				.taskDefinitionKey("financialapproval").list();
				if(listhist.size()>0&&listhist!=null){
					for(HistoricTaskInstance his:listhist){
						Date now = new Date();
						////system.out.println(his.getEndTime());
						if(his.getEndTime()!=null&&afterThrid.getTime()<=his.getEndTime().getTime()&&his.getEndTime().getTime()<=now.getTime()){
							 HistoricProcessInstance processInstance = historyService//与历史数据（历史表）相关的Service
			                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
			                        .processInstanceId(his.getExecutionId())//使用流程实例ID查询
			                        .singleResult();
							 if(processInstance!=null){
								 String id = processInstance.getBusinessKey();
								 if(id.indexOf("businesshospitality")!=-1){
										////system.out.println("业务招待");
										id = id.split("\\:")[1];
										Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
										if(entertain!=null){
											entertain.setTitle("业务招待");
											entertain.setFaterApplyman(entertain.getManager());
											entertain.setFatherdepartment(entertain.getDepartment());
											entertain.setFathertitle(entertain.getTitle());
											entertain.setSortTime(entertain.getApplyTime());
											entertain.setFatherId(entertain.getId()+"");
											entertain.setEntertainregisterinfo(entertainregisterinfoMapper.getNumber(entertain.getNumber()));
											list.add(entertain);
											
										}
									}
							 }
						}
					}
				}
				
		}
		
	}
		
		////system.out.println("list"+list);
		////system.out.println("listSize"+list.size());
		List <publicEntity> listnew = new ArrayList(new TreeSet(list)); 
		List<publicEntity> searchlist = search(listnew, type, beginTime, endTime, applyMan, departmentId);
		sort(searchlist);
		Map<String,Object> minmap = paging(searchlist, nowPage, pageSize);
		
		return AnccResult.ok(minmap);
	}
	
	
	
	
	/**
	 * 待我审批筛选
	 * param:openId:微信认证编号
	 *       nowPage，起始页
	 *       pageSize：数据条数
	 *       type：类型
	 *       beginTime：申请起始时间
	 *       endTime：申请结束时间
	 *       applyMan：申请人
	 *       departmentId：部门
	 * result:list
	 * @throws ParseException 
	 */
	@RequestMapping("/mainApproveScreen")
	@ResponseBody
	public AnccResult mainApproveScreen(String openId,Integer nowPage,Integer pageSize,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		////system.out.println("部门名称是"+departmentId);
		
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		String userCode = "";
		if(user!=null){
			userCode = user.getCode();
		}
		//部门idlist
		List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
		//获取登录用户的所有角色id
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		//获取领导级别的角色
		Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
		roleIdFromClass.retainAll(roleIdByUserId);
		List<publicEntity> list = new ArrayList<>();
		Set<UserEntity> listroleuser = dynamicGetRoleUtil.getApprovalUsers("财务审批");
		List<Task> tasks = taskService.createTaskQuery().taskAssignee(userCode)
				 .orderByTaskCreateTime().desc()
				 .list();
		Map<TaskDTO, String> map = getTaskAndBussIdByTask(tasks);
		int i=0;
		for(TaskDTO taskDto:map.keySet()){
			String id = taskDto.getBussinessId();
			String processInstanceId = taskDto.getProcessInstanceId();
			String startUserId = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult().getStartUserId();
			if(null!=startUserId&!startUserId.equals(userCode)){
				if(id.indexOf("applyExpenses")!=-1){
					id = id.split("\\:")[1];
					////system.out.println("id是"+id);
					ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
					if(applyExpens!=null){
						////system.out.println("判断"+!taskDto.getKey().equals("finaceApply"));
						if(!taskDto.getKey().equals("finaceApply")){//财务
							String taskId = map.get(taskDto);
							////system.out.println("查看任务Ids是"+taskId);
							applyExpens.setTitle("差旅费");
							applyExpens.setTaskId(taskId);
							applyExpens.setApproveState(taskDto.getName());
							applyExpens.setSortTime(applyExpens.getApplyTime());
							applyExpens.setFaterApplyman(applyExpens.getApplyMan());
							applyExpens.setFatherdepartment(applyExpens.getDepartment());
							applyExpens.setFathertitle(applyExpens.getTitle());
							applyExpens.setFatherId(applyExpens.getId());
							list.add(applyExpens);
						}
						
					}
				}
				if(id.indexOf("businesshospitality")!=-1){
					id = id.split("\\:")[1];
					Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
					if(entertain!=null){
						if(taskDto.getKey().equals("financialapproval")){//财务
							List<Entertainregisterinfo> number = entertainregisterinfoMapper.getNumber(entertain.getNumber());
							entertain.setEntertainregisterinfo(number);
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem");
							entertain.setShowButton("0");
						}else if(taskDto.getKey().equals("businessapprove")){
							entertain.setUrl("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem");
							entertain.setShowButton("1");
						}
						String taskId = map.get(taskDto);
						entertain.setTitle("业务招待");
						entertain.setFatherId(entertain.getId()+"");
						entertain.setStatus(taskDto.getName());
						entertain.setTaskId(taskId);
						entertain.setSortTime(entertain.getApplyTime());
						entertain.setFaterApplyman(entertain.getManager());
						entertain.setFatherdepartment(entertain.getDepartment());
						entertain.setFathertitle(entertain.getTitle());
						
						list.add(entertain);
					}
				
				}
				
				if(id.indexOf("privateCar")!=-1){
					if(taskDto.getKey().equals("privateCarApprove")){
						id = id.split("\\:")[1];
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
						if(privateCar!=null){
							String taskId = map.get(taskDto);
							privateCar.setTitle("私车公用");
							privateCar.setTaskId(taskId);
							privateCar.setStatus(taskDto.getName());
							privateCar.setSortTime(privateCar.getApplyTime());
							privateCar.setFaterApplyman(privateCar.getApplyMan());
							privateCar.setFatherdepartment(privateCar.getDepartment());
							privateCar.setFatherId(privateCar.getApplyId());
							privateCar.setFathertitle(privateCar.getTitle());
							list.add(privateCar);
						}
						
					}

				}
				
				if(id.indexOf("leaverYear")!=-1){//年假查询
					////system.out.println("");
					id = id.split("\\:")[1];
					LearYear learYear = yearLearMapper.findById(id);
					if(learYear!=null){
						List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
						learYear.setLeaver(listLearTime);
						String taskId = map.get(taskDto);
						learYear.setTitle("年假管理");
						learYear.setFatherId(learYear.getId());
						learYear.setTaskId(taskId);
						learYear.setSortTime(learYear.getApplyTime());
						learYear.setFaterApplyman(learYear.getApplyMan());
						learYear.setFatherdepartment(learYear.getDepartment());
						learYear.setFathertitle(learYear.getTitle());
						list.add(learYear);
				}
			}
					
		}
		
		}
		//获取进行中的，并且“我”可操作的
				//公车管理
		List<Task> task0 =new ArrayList<>();
		List<Task> task2 =workflowUtil.getTaskByIds("applyCarKey", "sp");
		task0.addAll(task2);
		//根据任务获取业务
		Map<String,String> busAndTaskId1 = workflowUtil.getTaskAndBussIdByTask(task0);
		if(user!=null){
//			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByUserCode(busAndTaskId1.keySet(),user.getCode());
			List<CarApplyInfo2> carApplyList= bCarapplymanageService.findTasksByStatus(busAndTaskId1.keySet(),2);
			for(CarApplyInfo2 carapply:carApplyList){
				if(user.getCode().equals(carapply.getApproveMan())){
					String str = carapply.getID()+"";
					String tid=busAndTaskId1.get(str);
					carapply.setTaskId(tid);
					carapply.setSortTime(carapply.getApplyTime());
					carapply.setTitle("公车申请");
					carapply.setDepartment(carapply.getDepartment().replace(",", ""));
					carapply.setDepartmentId(carapply.getDepartmentId().replace(",", ""));
					carapply.setUrl("applyCar/climeApprovalTask.action");
					carapply.setParam("taskId,result,status,openId");
					String comment=null;
					if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
						comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
					}
					carapply.setComment(comment);
					BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
					if(carInfoByNum!=null){
						String carUrl=carInfoByNum.getCarUrl();
						carapply.setCarUrl(carUrl);
					}
					carapply.setSortTime(carapply.getApplyTime());
					carapply.setFaterApplyman(carapply.getApplyMan());
					carapply.setFatherdepartment(carapply.getDepartment());
					carapply.setFathertitle(carapply.getTitle());
					list.add(carapply);
				}
			}
		}
		//用印申请
//		List<Task> task5 =new ArrayList<>();
//		List<Task> task7 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
//		List<Task> task8 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
//		List<Task> task9 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
//		List<Task> task10 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
//		task5.addAll(task7);
//		task5.addAll(task8);
//		task5.addAll(task9);
//		task5.addAll(task10);
		//根据任务获取业务
//		Map<String,String> busAndTaskId2 = workflowUtil.getTaskAndBussIdByTask(task5);
//		List<BGzapplyinfo> gzlist= bGzapplyinfoService.findTasksByUserCode(busAndTaskId2.keySet(),user.getCode());
//		List<BGzapplyinfo> gzlist= bGzapplyinfoService.findTasksByStatus(busAndTaskId2.keySet(),"待审批");
//		for(BGzapplyinfo applyinfo:gzlist){
//			if(user!=null){	
//				if(user.getCode().equals(applyinfo.getApproveman())){
//				String str = applyinfo.getId()+"";
//				String tid=busAndTaskId2.get(str);
//				String comment=null;
//				if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
//					comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
//				}
//				applyinfo.setComment(comment);
//				applyinfo.setTaskId(tid);
//				applyinfo.setCanUpdate("1");
//				applyinfo.setCanRevoke("1");
//				applyinfo.setSortTime(applyinfo.getApplytime());
//				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
//				applyinfo.setTitle("用印申请");
//				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
//				applyinfo.setSortTime(applyinfo.getApplytime());
//				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
//				applyinfo.setFatherdepartment(applyinfo.getDepartment());
//				applyinfo.setFathertitle(applyinfo.getTitle());
//				applyinfo.setFatherId(applyinfo.getFatherId());
//				list.add(applyinfo);
//				}}
//		}
		//用印管理
		List<Task> task4 =workflowUtil.getTaskByIds("printApplyKey", "sp0");
		List<Task> task5 =workflowUtil.getTaskByIds("printApplyKey", "sp1");
		List<Task> task6 =workflowUtil.getTaskByIds("printApplyKey", "sp2");
		List<Task> task7 =workflowUtil.getTaskByIds("printApplyKey", "sp3");
		//一类审批
		Map<String,String> busAndTaskId4 = workflowUtil.getTaskAndBussIdByTask(task4);
		List<BGzapplyinfo> printApplyList1= bGzapplyinfoService.findTasksByStatus(busAndTaskId4.keySet(),"待审批");
		for(BGzapplyinfo applyinfo:printApplyList1){
//					if(departList.size()>0){&&departList.contains(applyinfo.getDepartmentid())
			if(user!=null){	
			if(user.getCode().equals(applyinfo.getApproveman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId4.get(str);
					applyinfo.setTaskId(tid);
					applyinfo.setUrl("print/climeApproval0Task.action");
					applyinfo.setParam("taskId,status,result");
					String comment=null;
					if(!tid.equals("null")&&workflowUtil.findCommentByTaskId(tid).size()>0){
						comment=workflowUtil.findCommentByTaskId(tid).get(0).getFullMessage();
					}
					applyinfo.setComment(comment);
					applyinfo.setTitle("用印申请");
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setFaterApplyman(applyinfo.getApplyusername());
					applyinfo.setFatherdepartment(applyinfo.getDepartment());
					applyinfo.setFathertitle(applyinfo.getTitle());
					applyinfo.setFatherId(applyinfo.getFatherId());
					list.add(applyinfo);
				}
			}
		}
		//二类审批
		Map<String,String> busAndTaskId5= workflowUtil.getTaskAndBussIdByTask(task5);
		List<BGzapplyinfo> printApplyList2= bGzapplyinfoService.findTasksByStatus(busAndTaskId5.keySet(),"待审批");
		for(BGzapplyinfo applyinfo:printApplyList2){
//					if(departList.size()>0){&&departList.contains(applyinfo.getDepartmentid())
			if(user!=null){	
			if(user.getCode().equals(applyinfo.getApproveman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId5.get(str);
					applyinfo.setTaskId(tid);
					applyinfo.setUrl("print/climeApproval1Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setFaterApplyman(applyinfo.getApplyusername());
					applyinfo.setFatherdepartment(applyinfo.getDepartment());
					applyinfo.setFathertitle(applyinfo.getTitle());
					applyinfo.setFatherId(applyinfo.getFatherId());
					list.add(applyinfo);
				}
			}
		}
		//三类审批
		Map<String,String> busAndTaskId6 = workflowUtil.getTaskAndBussIdByTask(task6);
//				List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findTasksByStatuses(busAndTaskId6.keySet(),"待审批","通过");
		List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findTasksLikeStatuses(busAndTaskId6.keySet(),"%通过%");
		for(BGzapplyinfo applyinfo:printApplyList3){
			if(user!=null){
				if(user.getCode().equals(applyinfo.getBusinessManager())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId6.get(str);
					applyinfo.setTaskId(tid);
					if(applyinfo.getBusinessManager().equals(applyinfo.getConfirmman())){
						if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
							applyinfo.setDisplayTime("1");
						}
					}
					applyinfo.setUrl("print/climeApproval2Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setFaterApplyman(applyinfo.getApplyusername());
					applyinfo.setFatherdepartment(applyinfo.getDepartment());
					applyinfo.setFathertitle(applyinfo.getTitle());
					applyinfo.setFatherId(applyinfo.getFatherId());
					list.add(applyinfo);
				}
			}
		}
		//四类审批
		Map<String,String> busAndTaskId7 = workflowUtil.getTaskAndBussIdByTask(task7);
//				List<BGzapplyinfo> printApplyList4= bGzapplyinfoService.findTasksByStatuses(busAndTaskId7.keySet(),"待审批","通过");
		List<BGzapplyinfo> printApplyList4= bGzapplyinfoService.findTasksLikeStatuses(busAndTaskId7.keySet(),"%通过%");
		for(BGzapplyinfo applyinfo:printApplyList4){
			if(user!=null){
				if(user.getCode().equals(applyinfo.getConfirmman())){
					String str = applyinfo.getId()+"";
					String tid=busAndTaskId7.get(str);
					applyinfo.setTaskId(tid);
					if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
						applyinfo.setDisplayTime("1");
					}
					applyinfo.setUrl("print/climeApproval3Task.action");
					applyinfo.setParam("taskId,status,result");
					applyinfo.setTitle("用印申请");
					applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
					applyinfo.setSortTime(applyinfo.getApplytime());
					applyinfo.setFaterApplyman(applyinfo.getApplyusername());
					applyinfo.setFatherdepartment(applyinfo.getDepartment());
					applyinfo.setFathertitle(applyinfo.getTitle());
					applyinfo.setFatherId(applyinfo.getFatherId());
					list.add(applyinfo);
				}
			}
		}
		sort(list);
//		List <publicEntity> listnew = new ArrayList(new TreeSet(list)); 
		List<publicEntity> searchlist = search(list, type, beginTime, endTime, applyMan, departmentId);
		Map<String,Object> mainmap = paging(searchlist, nowPage, pageSize);
		return AnccResult.ok(mainmap);	
	}
	
	
	/**
	 * 我已审批的筛选
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("mainapplyedSearch")
	@ResponseBody
	public AnccResult mainApprovedControcllerSearch(String openId,Integer nowPage,Integer pageSize,
			String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		List<publicEntity> list = new ArrayList<>();
		//获取用户id
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		String userCode = "";
		if(user!=null){
			userCode = user.getCode();
		}
		List<HistoricTaskInstance> listhist = historyService//与历史数据（历史表）相关的Service
                .createHistoricTaskInstanceQuery()//创建历史任务实例查询
                .taskAssignee(userCode)//指定历史任务的办理人
                .list();
		if(listhist!=null){
			for(HistoricTaskInstance historicTaskInstance:listhist){
				Date endTimes = historicTaskInstance.getEndTime();
				HistoricProcessInstance pi = null;
				if(endTimes!=null){
					pi = historyService//与历史数据（历史表）相关的Service
		                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
		                        .processInstanceId(historicTaskInstance.getExecutionId())//使用流程实例ID查询
		                        .singleResult();
				}
	
				if(pi!=null){
					if(pi.getEndTime()==null){
						String startUserId = pi.getStartUserId();
						if(null!=startUserId&&!startUserId.equals(userCode)){
							////system.out.println("用户主键"+pi.getBusinessKey());
							String id = pi.getBusinessKey();
							if(id.indexOf("applyExpenses")!=-1){
								////system.out.println("差旅费");
								id = id.split("\\:")[1];
								////system.out.println("id是"+id);
								ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
								if(applyExpens!=null){
									/*applyExpens.setTaskId(taskId);*/
									if(!applyExpens.getApproveState().equals("待审批")){
										applyExpens.setTitle("差旅费");
										applyExpens.setSortTime(applyExpens.getApplyTime());
										applyExpens.setFaterApplyman(applyExpens.getApplyMan());
										applyExpens.setFatherdepartment(applyExpens.getDepartment());
										applyExpens.setFathertitle(applyExpens.getTitle());
										applyExpens.setFatherId(applyExpens.getId());
										list.add(applyExpens);
									}
								}
							}
							if(id.indexOf("businesshospitality")!=-1){
								////system.out.println("业务招待");
								id = id.split("\\:")[1];
								Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
								if(entertain!=null){
									/*String taskId = map.get(taskDto);
									entertain.setTaskId(taskId);*/
									if(!entertain.getStatus().equals("待审核")){
										entertain.setTitle("业务招待");
										entertain.setSortTime(entertain.getApplyTime());
										entertain.setFaterApplyman(entertain.getManager());
										entertain.setFatherdepartment(entertain.getDepartment());
										entertain.setFathertitle(entertain.getTitle());
										entertain.setFatherId(entertain.getId()+"");
										list.add(entertain);
									}
									
								}
							}
							if(id.indexOf("privateCar")!=-1){
								////system.out.println("私车公用");
								id = id.split("\\:")[1];
								PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
								if(privateCar!=null){
									if(!privateCar.getStatus().equals("待审核")){
										privateCar.setTitle("私车公用");
										privateCar.setSortTime(privateCar.getApplyTime());
										privateCar.setFaterApplyman(privateCar.getApplyMan());
										privateCar.setFatherdepartment(privateCar.getDepartment());
										privateCar.setFathertitle(privateCar.getTitle());
										privateCar.setFatherId(privateCar.getApplyId());
										list.add(privateCar);
									}
								}
							}
							if(id.indexOf("leaverYear")!=-1){
								id = id.split("\\:")[1];
								LearYear learYear = yearLearMapper.findById(id);
								if(learYear!=null){
									if(!learYear.getStatus().equals("待审批")){
										List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
										learYear.setTitle("年假管理");
										learYear.setLeaver(listLearTime);
										learYear.setSortTime(learYear.getApplyTime());
										learYear.setFaterApplyman(learYear.getApplyMan());
										learYear.setFatherdepartment(learYear.getDepartment());
										learYear.setFathertitle(learYear.getTitle());
										learYear.setFatherId(learYear.getId());
										list.add(learYear);
									}
									
								}
							}
						}
					}
					
				}
			}
		}
					
		
		
		//部门idlist
		List<Integer> departList=bDepartmentService.getDepartmentIdByUserId(userId);
		//获取登录用户的所有角色id
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		//获取领导级别的角色
		Set<Integer> roleIdFromClass = bRoleService.getRoleIdFromClass("3*");
		roleIdFromClass.retainAll(roleIdByUserId);
		//公车管理
		List<CarApplyInfo2> carApplyList1= bCarapplymanageService.findByOutOrInMan(user.getCode());
		for(CarApplyInfo2 carapply:carApplyList1){
			if(carapply.getStatus()!=5&&!"已归还".equals(carapply.getState())){
				carapply.setTitle("公车申请");
				carapply.setDepartment(carapply.getDepartment().replace(",", ""));
				carapply.setDepartmentId(carapply.getDepartmentId().replace(",", ""));
				carapply.setUrl("applyCar/climeApprovalTask.action");
				carapply.setParam("taskId,result,status,openId");
				BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carapply.getCarCode());
				carapply.setSortTime(carapply.getApplyTime());
				carapply.setFaterApplyman(carapply.getApplyMan());
				carapply.setFatherdepartment(carapply.getDepartment());
				carapply.setFathertitle(carapply.getTitle());
				carapply.setFatherId(carapply.getApplyId());
				if(carInfoByNum!=null){
					String carUrl=carInfoByNum.getCarUrl();
					carapply.setCarUrl(carUrl);
					
				}
				list.add(carapply);
			}
		}
		//用印管理
		//一类审批
		List<BGzapplyinfo> printApplyList1= bGzapplyinfoService.findApprovalAndLable(user.getCode(),1);
		for(BGzapplyinfo applyinfo:printApplyList1){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(applyinfo.getId()+"");
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
		}
		//二类审批
		List<BGzapplyinfo> printApplyList2= bGzapplyinfoService.findBussinessAndLable(user.getCode(),2);
		for(BGzapplyinfo applyinfo:printApplyList2){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(applyinfo.getId()+"");
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
			}
		//三类审批
		List<BGzapplyinfo> printApplyList3= bGzapplyinfoService.findConfirmAndLable(user.getCode(),3);
		for(BGzapplyinfo applyinfo:printApplyList3){
			if(!"已完成".equals(applyinfo.getStatus())){
				applyinfo.setTitle("用印申请");
				applyinfo.setSortTime(applyinfo.getApplytime());
				applyinfo.setFaterApplyman(applyinfo.getApplyusername());
				applyinfo.setFatherdepartment(applyinfo.getDepartment());
				applyinfo.setFathertitle(applyinfo.getTitle());
				applyinfo.setFatherId(applyinfo.getId()+"");
				applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
				list.add(applyinfo);
			}
		}
		sort(list);
		List <publicEntity> listnew = new ArrayList(new TreeSet(list)); 
		List<publicEntity> searchlist = search(listnew, type, beginTime, endTime, applyMan, departmentId);
		Map<String,Object> mainmap = paging(searchlist, nowPage, pageSize);
		return AnccResult.ok(mainmap);
	}
	
	
	
	/**
	 * 审批中查询分页
	 * @param openId
	 * @param nowPage
	 * @param pageSize
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param applyMan
	 * @param departmentId
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/mainRuntimeHistorySearch")
	@ResponseBody
	public AnccResult mainRuntimeHistorySearch(String openId,Integer nowPage,Integer pageSize,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		Integer userId=bUserService.getUserIdByOpenId(openId);
		UserEntity user = bUserService.getUserById(userId);
		String userCode = "";
		if(user!=null){
			userCode = user.getCode();
		}
		List<publicEntity> list = new ArrayList<>();
		List<HistoricProcessInstance> historicProcessInstanceList = 
				historyService.createHistoricProcessInstanceQuery().startedBy(userCode).list();
		for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
			
			////system.out.println(""+historicProcessInstance.getEndActivityId());
			Date endActivityId = historicProcessInstance.getEndTime();
			if(null==endActivityId){
				Task task = taskService.createTaskQuery().executionId(historicProcessInstance.getId()).singleResult();
				String taskId = "";
				if(task!=null){
					String id = historicProcessInstance.getBusinessKey();
					taskId = task.getId();
					if(id.indexOf("applyExpenses")!=-1){
						////system.out.println("差旅费");
						id = id.split("\\:")[1];
						////system.out.println("id是"+id);
						ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
						if(applyExpens!=null){
							if(!task.getTaskDefinitionKey().equals("applyStaff"))
							if(!task.getTaskDefinitionKey().equals("cltjsq")){
								/*applyExpens.setTaskId(taskId);*/
								applyExpens.setApproveState(task.getName());
								applyExpens.setTitle("差旅费");
								applyExpens.setTaskId(taskId);
								applyExpens.setSortTime(applyExpens.getApplyTime());
								applyExpens.setFaterApplyman(applyExpens.getApplyMan());
								applyExpens.setFatherdepartment(applyExpens.getDepartment());
								applyExpens.setFathertitle(applyExpens.getTitle());
								applyExpens.setFatherId(applyExpens.getId());
								list.add(applyExpens);
							}
						}
					}
					if(id.indexOf("businesshospitality")!=-1){
						////system.out.println("业务招待");
						id = id.split("\\:")[1];
						Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(Integer.parseInt(id));
						if(entertain!=null){
							/*String taskId = map.get(taskDto);
							entertain.setTaskId(taskId);*/
							if(!task.getTaskDefinitionKey().equals("businessapply"))
								if(!task.getTaskDefinitionKey().equals("registration")){
									entertain.setStatus(task.getName());
									entertain.setTitle("业务招待");
									entertain.setTaskId(taskId);
									entertain.setSortTime(entertain.getApplyTime());
									entertain.setFaterApplyman(entertain.getManager());
									entertain.setFatherdepartment(entertain.getDepartment());
									entertain.setFathertitle(entertain.getTitle());
									entertain.setFatherId(entertain.getId()+"");
									list.add(entertain);
								}
						}
					}
					if(id.indexOf("privateCar")!=-1){
						////system.out.println("私车公用");
						id = id.split("\\:")[1];
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
						if(!task.getTaskDefinitionKey().equals("privateCarApply"))
							if(!task.getTaskDefinitionKey().equals("privateCarStaff"))
								if(!task.getTaskDefinitionKey().equals("privateCarReimbursement")){
									if(privateCar!=null){
										privateCar.setTitle("私车公用");
										privateCar.setTaskId(taskId);
										privateCar.setStatus(task.getName());
										privateCar.setSortTime(privateCar.getApplyTime());
										privateCar.setFaterApplyman(privateCar.getApplyMan());
										privateCar.setFatherdepartment(privateCar.getDepartment());
										privateCar.setFathertitle(privateCar.getTitle());
										privateCar.setFatherId(privateCar.getApplyId());
										list.add(privateCar);
									}
								}
					}
					if(id.indexOf("applyCar")!=-1){
						id = id.split("\\:")[1];
						CarApplyInfo2 carApplyInfo2 = bCarapplymanageMapper.getById(Integer.parseInt(id));
						if(carApplyInfo2!=null&&carApplyInfo2.getApplyMan().equals(user.getCode())&&carApplyInfo2.getStatus()!=5&&carApplyInfo2.getStatus()!=1){
							String comment=null;
							if(!taskId.equals("null")&&workflowUtil.findCommentByTaskId(taskId).size()>0){
								comment=workflowUtil.findCommentByTaskId(taskId).get(0).getFullMessage();
							}
							if(carApplyInfo2.getStatus()<4){
								carApplyInfo2.setCanRevoke("1");
							}
							if(carApplyInfo2.getStatus()<3){
								carApplyInfo2.setCanRevoke("1");
								carApplyInfo2.setCanUpdate("1");
							}
							carApplyInfo2.setComment(comment);
							carApplyInfo2.setTaskId(taskId);
							carApplyInfo2.setTitle("公车申请");
							carApplyInfo2.setSortTime(carApplyInfo2.getApplyTime());
							carApplyInfo2.setFaterApplyman(carApplyInfo2.getApplyMan());
							carApplyInfo2.setFatherdepartment(carApplyInfo2.getDepartment());
							carApplyInfo2.setFathertitle(carApplyInfo2.getTitle());
							carApplyInfo2.setDepartment(carApplyInfo2.getDepartment().replace(",", ""));
							carApplyInfo2.setFatherId(carApplyInfo2.getApplyId());
							BCarbaseinfo carInfoByNum = bCarbaseinfoService.getCarInfoByNum(carApplyInfo2.getCarCode());
							if(carInfoByNum!=null){
								String carUrl=carInfoByNum.getCarUrl();
								carApplyInfo2.setCarUrl(carUrl);
								carApplyInfo2.setCarryNum(carInfoByNum.getPeasonnum());
							}
							list.add(carApplyInfo2);
							
						}
					}
					if(id.indexOf("printApply")!=-1){
						id = id.split("\\:")[1];
						BGzapplyinfo applyinfo = bGzapplyinfoMapper.getById(Integer.parseInt(id));
						if(applyinfo!=null){
							if(applyinfo.getApplyusername().equals(user.getCode())&&!applyinfo.getStatus().contains("否决")){
								String comment=null;
								if(!taskId.equals("null")&&workflowUtil.findCommentByTaskId(taskId).size()>0){
									comment=workflowUtil.findCommentByTaskId(taskId).get(0).getFullMessage();
								}
								applyinfo.setTaskId(taskId);
								applyinfo.setCanUpdate("1");
								applyinfo.setCanRevoke("1");
								if(applyinfo.getGzkind().contains("原件")||applyinfo.getGzkind().contains("公章外带")){
									applyinfo.setDisplayTime("1");
								}
								applyinfo.setComment(comment);
								applyinfo.setTitle("用印申请");
								applyinfo.setSortTime(applyinfo.getApplytime());
								applyinfo.setFaterApplyman(applyinfo.getApplyusername());
								applyinfo.setFatherdepartment(applyinfo.getDepartment().replace(",", ""));
								applyinfo.setFathertitle(applyinfo.getTitle());
								applyinfo.setApplytime(applyinfo.getApplytime().substring(0, 10));
								applyinfo.setFatherId(applyinfo.getId()+"");
								list.add(applyinfo);
							}
						}
					}
					if(id.indexOf("leaverYear")!=-1){
						id = id.split("\\:")[1];
						LearYear learYear = yearLearMapper.findById(id);
						if(!task.getTaskDefinitionKey().equals("leaverapply"))
							if(!task.getTaskDefinitionKey().equals("leaveYearStafuser")){
								if(learYear!=null){
									List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
									learYear.setTitle("年假管理");
									learYear.setLeaver(listLearTime);
									List<LearTime> seartTime = learTimeMapper.seartTime(id);
									////system.out.println("searchtime是"+seartTime);
									learYear.setCanRevoke("1");
									if(seartTime.size()>0){//说明修过年假
										learYear.setCanRevoke("0");
									}
									learYear.setFatherId(learYear.getId());
									learYear.setConUpdate("1");
									learYear.setTaskId(taskId);
									learYear.setLeaver(listLearTime);
									learYear.setSortTime(learYear.getApplyTime());
									learYear.setFaterApplyman(learYear.getApplyMan());
									learYear.setFatherdepartment(learYear.getDepartment());
									learYear.setFathertitle(learYear.getTitle());
									list.add(learYear);
								}
							}
						
					}
					
				}
			}
		}
		List <publicEntity> listnew = new ArrayList<>();
		for(int i=0;i<list.size();i++){  
            if(!listnew.contains(list.get(i))){  
            	listnew.add(list.get(i));  
            }  
        }  
		sort(listnew);
		List<publicEntity> searchlist = search(listnew, type, beginTime, endTime, applyMan, departmentId);//查询方法
		Map<String ,Object> mainmap = paging(searchlist, nowPage, pageSize);//分页方法
		return AnccResult.ok(mainmap);
	}
	
	
	
	/**
	 * 查询方法
	 * @param list
	 * @param type
	 * @param beginTime
	 * @param endTime
	 * @param applyMan
	 * @param departmentId
	 * @return
	 * @throws ParseException
	 */
	public List<publicEntity> search(List<publicEntity> list,String type,String beginTime,String endTime,String applyMan,String departmentId) throws ParseException{
		List<publicEntity> searchlist = new ArrayList<>();
		for(publicEntity publicEntity:list){
			SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
			if(null!=applyMan&&type.equals("全部")&&departmentId.equals("全部部门")){//1
				
				if(publicEntity.getFaterApplyman().contains(applyMan)
					&&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
					&&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()){
					searchlist.add(publicEntity);
				}
			}
			 
			if(null!=applyMan&&!type.equals("全部")&&departmentId.equals("全部部门")){//2
				if(publicEntity.getFaterApplyman().contains(applyMan)&&publicEntity.getFathertitle().equals(type)
						&&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
						&&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()){
					searchlist.add(publicEntity);
				}
			}
			
			if(null!=applyMan&&!type.equals("全部")&&!departmentId.equals("全部部门")){//3
				if(publicEntity.getFaterApplyman().contains(applyMan)
				   &&publicEntity.getFathertitle().equals(type)
				   &&publicEntity.getFatherdepartment().replace(",", "").equals(departmentId)
				   &&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()
						){
					searchlist.add(publicEntity);
				}
			}
			if(null!=applyMan&&type.equals("全部")&&!departmentId.equals("全部部门")){//4
				if(publicEntity.getFaterApplyman().contains(applyMan)
				   &&publicEntity.getFatherdepartment().replace(",", "").equals(departmentId)
				   &&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()){
					searchlist.add(publicEntity);
				}
			}
			
			
			if(null==applyMan&&!"全部".equals(type)&&departmentId.equals("全部部门")){//5
				if(publicEntity.getFathertitle().equals(type)&&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()){
					searchlist.add(publicEntity);
				}
			}
				
			if(null==applyMan&&!type.equals("全部")&&!departmentId.equals("全部部门")){//6
				if(publicEntity.getFathertitle().equals(type)
				   &&smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()
				   &&publicEntity.getFatherdepartment().replace(",", "").equals(departmentId)){
					searchlist.add(publicEntity);
				}
			}
			
			if(null==applyMan&&type.equals("全部")&&!departmentId.equals("全部部门")){//7
				if(smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()
				   &&publicEntity.getFatherdepartment().replace(",", "").equals(departmentId)){
					searchlist.add(publicEntity);
				}
			}
			////system.out.println("申请时间"+smf.parse(publicEntity.getSortTime()));
			////system.out.println("申请时间毫秒"+smf.parse(publicEntity.getSortTime()).getTime());
			////system.out.println("开始时间毫秒"+smf.parse(beginTime).getTime());
			////system.out.println("结束时间毫秒"+smf.parse(endTime).getTime());
			if(null==applyMan&&type.equals("全部")&&departmentId.equals("全部部门")){//8
				if(smf.parse(beginTime).getTime()<=smf.parse(publicEntity.getSortTime()).getTime()
				   &&smf.parse(endTime).getTime()>=smf.parse(publicEntity.getSortTime()).getTime()
				   ){
					searchlist.add(publicEntity);
				}
			}
			
		}
		
		
		return searchlist;
	}
	
	
	
	/**
	 * 分页方法
	 * @param list
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	public Map<String,Object>  paging(List<publicEntity> list,Integer nowPage,Integer pageSize){
		Map<String,Object> mainmap = new HashMap<>();
		if(nowPage !=0 && pageSize != 0){
			int start = (nowPage - 1) * pageSize;
			int total = list.size();
			List<publicEntity> list_all = new ArrayList<>();
		    if (start + pageSize <= total)
		      list_all.addAll(list.subList(start, start + pageSize));
		    else
		      list_all.addAll(list.subList(start, total));
			
		    mainmap.put("pagesize", list.size());
		    mainmap.put("list", list_all);
		}else{
			mainmap.put("pagesize", list.size());
		}
		
		return mainmap;
	}
	
	
	
}
