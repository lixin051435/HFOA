package com.hfoa.service.impl.travelApplyImple;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfoa.dao.travelExpenses.ApplyExpensesMapper;
import com.hfoa.dao.travelExpenses.ApproveExpensesMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;
import com.hfoa.entity.travelExpenses.TravelAddressDTO;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.TravelExpes.TravelExpessService;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.WorkflowUtil;

import net.sf.json.JSONArray;



/**
 *差旅                                                                                                                                                                                                                                       
 */
@Transactional
@Service
public class ApplyExpenseServiceImpl implements TravelExpessService{
	
	@Autowired
	private ApplyExpensesMapper applyExpensesMapper;
	
	
	@Autowired
	private USerService bUserService;
	
	@Autowired
	private BDepartmentService bDepartmentService;
	
	@Autowired
	private BRoleService bRoleService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private WorkflowUtil workflowUtil;
	
	@Autowired
	private UserMapper userMapper;
	
	
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ApproveExpensesMapper approveExpensesMapper;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int insertTravelExpenss(ApplyExpensesEntity applyExpensesEntity) {
		int flag =0; 
		if(!applyExpensesEntity.getApproveManOpenId().equals("")&&applyExpensesEntity.getApproveManOpenId()!=null){
			Map<String,Object> activitiMap = new HashMap<>();
			applyExpensesEntity.setId(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			applyExpensesEntity.setTripDetails_list(transformJson(applyExpensesEntity.getTripDetails()));
			applyExpensesEntity.setApproveState("待部门审批");
			applyExpensesEntity.setConfirm("0");
			applyExpensesEntity.setTripDetails(JSONArray.fromObject(applyExpensesEntity.getTripDetails_list()).toString());
			flag = applyExpensesMapper.insertApplyExpensesEntity(applyExpensesEntity);
			
			if(flag==1){
				UserEntity applymanUser = userMapper.getopenId(applyExpensesEntity.getOpenId());
				if(applymanUser!=null){
					activitiMap.put("user", applymanUser.getCode());
					String applyId=applyExpensesEntity.getId();
					String objId="applyExpenses:"+applyId;
					identityService.setAuthenticatedUserId(applymanUser.getCode());
					runtimeService.startProcessInstanceByKey("applyExpens",objId,activitiMap);
					List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",applymanUser.getCode(),"cltjsq");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					System.out.println("bidr是"+bids);
					int i=0;
					for(;i<bids.size();i++){
						System.out.println("bids.get(i)是"+bids.get(i));
						System.out.println("applyId是"+applyId);
						if(bids.get(i)==applyId)
							System.out.println("相等"+i);
							break;
					}
					System.out.println("i是"+i);
					String taskId=tasks.get(i).getId();
					System.out.println("任务编号："+taskId);
					Map<String,Object> map = new HashMap<>();
					
					String GZhopenID = "";
					UserEntity userEntity = userMapper.getopenId(applyExpensesEntity.getApproveManOpenId());
					if(userEntity!=null){
						GZhopenID = userEntity.getModifiedby();
						map.put("inputUser", userEntity.getCode());
						//完成任务
						System.out.println("任务编号TaskId是"+taskId);
						completeApplyTask(taskId,null, "","",map);
					}
					List<TravelAddressDTO> list = applyExpensesEntity.getTripDetails_list();
					String beginAddress = "";
					String trip = "";
					for(TravelAddressDTO traveAddressDTO:list){
						beginAddress += traveAddressDTO.getBeginAddress()+"-"+traveAddressDTO.getEndAddress()+",";
						trip += traveAddressDTO.getVehicle()+";";
					}
					String title = "您好,"+applyExpensesEntity.getApplyMan()+"提交的出差行程需要您审批";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setApproveMan(applyExpensesEntity.getApproveMan());
					weiEntity.setApplyMan(applyExpensesEntity.getApplyMan());
					weiEntity.setStatus(applyExpensesEntity.getCause());
					weiEntity.setDepartMent(applyExpensesEntity.getRemarks());
					weiEntity.setTripContent(applyExpensesEntity.getApplyTime()+","+beginAddress+trip);
					//sendout(GZhopenID,weiEntity,title,mark);//推送消息
				}
			}
		}
		return flag;
	}

	
	@Override
	public int updateTravelExpenss(ApplyExpensesEntity applyExpensesEntity) {
		applyExpensesEntity.setTripDetails_list(transformJson(applyExpensesEntity.getTripDetails()));
		applyExpensesEntity.setTripDetails(JSONArray.fromObject(applyExpensesEntity.getTripDetails_list()).toString());
		ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(applyExpensesEntity.getId());
		List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpens.getTripDetails()),TravelAddressDTO.class);
		List<TravelAddressDTO> aftertravelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpensesEntity.getTripDetails()),TravelAddressDTO.class);
		String taskId = applyExpensesEntity.getTaskId();
		String openId = applyExpens.getOpenId();
		UserEntity approveManUser = userMapper.getopenId(applyExpensesEntity.getApproveManOpenId());
		String approveMan = "";
		if(approveManUser!=null){
			approveMan = approveManUser.getCode();
		}
		
		if(null !=taskId  && !"".equals(taskId.trim())){
			boolean flag = true;
			Map<String,Object> map = new HashMap<>();
			String status = workflowUtil.getActivityId(taskId);
			if(status.equals("cltjsq")){//被驳回了 
				if(applyExpens.getConfirm().equals("1")){//说明被部门驳回了
					map.put("inputUser", approveMan);
					map.put("result", "true");
					completeApplyTask(taskId, "", "", "待部门领导审批", map);
					sendCclistMeasage(applyExpensesEntity);
				}else if(applyExpens.getConfirm().equals("2")){//说明是财务驳回
					flag = cheackapply(taskId,applyExpensesEntity,approveMan);
					if(flag){//flag=true说明没有改这些 ，节点跳转至财务审批
						applyExpensesEntity.setApproveState("待财务审批");
						workflowUtil.JumpEndActivity(taskId, "finaceApply",null);
					}else if(!applyExpensesEntity.getTravelers().equals(applyExpens.getTravelers())){//出行人改变
						sendCclistMeasage(applyExpensesEntity);
					}
				}
			}else if(status.equals("applyStaff")){//员工确认的修改
				if(decideTravel(travelAddress,aftertravelAddress)){//修改起点终点改变
					applyExpensesEntity.setApproveState("待部门领导审批");
					workflowUtil.JumpEndActivity(taskId, "applyApprove",null);
					sendMesage(applyExpensesEntity);
					sendCclistMeasage(applyExpensesEntity);
				}else if(judgeTravel(travelAddress, aftertravelAddress, applyExpensesEntity)){//修改出行方式改变
					applyExpensesEntity.setApproveState("待部门领导审批");
					workflowUtil.JumpEndActivity(taskId, "applyApprove",null);
					sendMesage(applyExpensesEntity);
					sendCclistMeasage(applyExpensesEntity);
				}else if(datechkeck(applyExpens.getBeginTime(),applyExpens.getEndTime(),applyExpensesEntity.getBeginTime(),applyExpensesEntity.getEndTime(),
						applyExpens.getTravelDays(),applyExpensesEntity.getTravelDays())){//时间抄送
					//workflowUtil.JumpEndActivity(taskId, "applyApprove");
					sendCclistMeasage(applyExpensesEntity);
				}else if(!applyExpensesEntity.getTravelers().equals(applyExpens.getTravelers())){//出差人改变
					//已无力抗拒，推送抄送人
					sendCclistMeasage(applyExpensesEntity);
				}
				
				
			}else if(status.equals("finaceApply")){//财务审批的修改
				if(decideTravel(travelAddress,aftertravelAddress)){//修改起点终点改变
					applyExpensesEntity.setApproveState("待部门领导审批");
					workflowUtil.JumpEndActivity(taskId, "applyApprove",null);
					sendMesage(applyExpensesEntity);
				}else if(judgeTravel(travelAddress, aftertravelAddress, applyExpensesEntity)){//修改出行方式改变
					applyExpensesEntity.setApproveState("待部门领导审批");
					workflowUtil.JumpEndActivity(taskId, "applyApprove",null);
					sendMesage(applyExpensesEntity);
				}else if(datechkeck(applyExpens.getBeginTime(),applyExpens.getEndTime(),applyExpensesEntity.getBeginTime(),applyExpensesEntity.getEndTime(),
						applyExpens.getTravelDays(),applyExpensesEntity.getTravelDays())){//时间抄送
					//workflowUtil.JumpEndActivity(taskId, "applyApprove");
					sendCclistMeasage(applyExpensesEntity);
				}else if(!applyExpensesEntity.getTravelers().equals(applyExpens.getTravelers())){
					sendCclistMeasage(applyExpensesEntity);
				}
			}
		}
		
		
		
		
		int i = applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		return i;
	}

	
	public void sendMesage(ApplyExpensesEntity applyExpensesEntity){
		
		String GZhopenID = "";
		UserEntity userEntity = userMapper.getopenId(applyExpensesEntity.getApproveManOpenId());
		if(userEntity!=null){
			GZhopenID = userEntity.getModifiedby();
		}
		
		List<TravelAddressDTO> list = applyExpensesEntity.getTripDetails_list();
		String beginAddress = "";
		String trip = "";
		for(TravelAddressDTO traveAddressDTO:list){
			beginAddress += traveAddressDTO.getBeginAddress()+"-"+traveAddressDTO.getEndAddress();
			trip += traveAddressDTO.getVehicle()+";";
		}
		String title = "您好,"+applyExpensesEntity.getApplyMan()+"提交的出差行程需要您审批";
		String mark = "待审批";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setApproveMan(applyExpensesEntity.getApproveMan());
		weiEntity.setApplyMan(applyExpensesEntity.getApplyMan());
		weiEntity.setStatus(applyExpensesEntity.getCause());
		weiEntity.setDepartMent(applyExpensesEntity.getRemarks());
		weiEntity.setTripContent(applyExpensesEntity.getApplyTime()+","+beginAddress+","+trip);
		sendout(GZhopenID,weiEntity,title,mark);//推送消息
	}
	
	
	
	//财务驳回的比较
	private boolean  cheackapply(String taskId,ApplyExpensesEntity applyExpensesEntity,String approveMan){
		applyExpensesEntity.setTripDetails_list(transformJson(applyExpensesEntity.getTripDetails()));
		applyExpensesEntity.setTripDetails(JSONArray.fromObject(applyExpensesEntity.getTripDetails_list()).toString());
		
		ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(applyExpensesEntity.getId());
		List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpens.getTripDetails()),TravelAddressDTO.class);
		System.out.println("travelAddress是"+travelAddress);
		System.out.println("查看出现方式"+applyExpensesEntity.getTripDetails());
		List<TravelAddressDTO> aftertravelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpensesEntity.getTripDetails()),TravelAddressDTO.class);
		Map<String,Object> map = new HashMap<>();
		boolean flag = true;
		if(decideTravel(travelAddress,aftertravelAddress)){//出行地判断
			map.put("inputUser", approveMan);
			map.put("result", "true");
			completeApplyTask(taskId, "", "", "待部门领导审批", map);
			sendMesage(applyExpensesEntity);
			sendCclistMeasage(applyExpensesEntity);
			flag = false;
		}else if(judgeTravel(travelAddress, aftertravelAddress, applyExpensesEntity)){//出行方式判断
			map.put("inputUser",approveMan);
			map.put("result", "true");
			completeApplyTask(taskId, "", "", "待部门领导审批", map);
			sendMesage(applyExpensesEntity);
			sendCclistMeasage(applyExpensesEntity);
			flag = false;
		}else if(datechkeck(applyExpens.getBeginTime(),applyExpens.getEndTime(),applyExpensesEntity.getBeginTime(),applyExpensesEntity.getEndTime(),
				applyExpens.getTravelDays(),applyExpensesEntity.getTravelDays())){
			sendCclistMeasage(applyExpensesEntity);
		}
		return flag;
	}
	
	
	
	
	public void  sendCclistMeasage(ApplyExpensesEntity applyExpensesEntity){
		String[] opeid = applyExpensesEntity.getcCListOpenId().split(",");
		for(int i=0;i<opeid.length;i++){
			String GZhopenID = "";
			if(!opeid[i].equals("")&&opeid[i]!=null){
				UserEntity userEntity = userMapper.getopenId(opeid[i]);
				if(userEntity!=null){
					GZhopenID = userEntity.getModifiedby();
				}
				List<TravelAddressDTO> list = applyExpensesEntity.getTripDetails_list();
				String beginAddress = "";
				String trip = "";
				for(TravelAddressDTO traveAddressDTO:list){
					beginAddress += traveAddressDTO.getBeginAddress()+"-"+traveAddressDTO.getEndAddress();
					trip += traveAddressDTO.getVehicle()+";";
				}
				String title = "您好,"+applyExpensesEntity.getApplyMan()+"提交的出差行程需要您查看";
				String mark = "待查看";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setApproveMan(applyExpensesEntity.getApproveMan());
				weiEntity.setApplyMan(applyExpensesEntity.getApplyMan());
				weiEntity.setStatus(applyExpensesEntity.getCause());
				weiEntity.setDepartMent(applyExpensesEntity.getRemarks());
				weiEntity.setTripContent(applyExpensesEntity.getApplyTime()+","+beginAddress+","+trip);
				sendout(GZhopenID,weiEntity,title,mark);
			}
		}
			
	}
	
	
	
	
	
	private boolean datechkeck(String bengingdate,String enddate,String afterdate,String afterenddate,int date,int endDate){
		  SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		  Date bening = null;
		  Date ending = null; 
		  Date afterbening = null;
		  Date afterending = null;
		try {
			bening = smf.parse(bengingdate);
			ending = smf.parse(enddate);
			afterbening = smf.parse(afterdate);
			afterending = smf.parse(afterenddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		  System.out.println("开始时间"+bengingdate);
		  System.out.println("结束时间"+enddate);
		  System.out.println("修改时间"+afterdate);
		  System.out.println("修改结束时间"+afterenddate);
		  long change = (bening.getTime()-afterbening.getTime())/(1000*3600*24);
		  long endchange = (ending.getTime()-afterending.getTime())/(1000*3600*24);
		  long start = (bening.getTime()-afterbening.getTime())/(1000*3600*24);
		  long end = (ending.getTime()-afterending.getTime())/(1000*3600*24);
		  
		  if(change-endchange>3){
			  return true;
		  }else if(start>3){
			  return true;
		  }else if(end>3){
			  return true;
		  }
		  
		  
		  return false;
		  
		  
	  }
	
	
	@Override
	public int deleteTravelExpess(String taskId,String id) {
		ApplyExpensesEntity applyExpensesEntity = applyExpensesMapper.getApplyExpens(id);
		if(applyExpensesEntity!=null){
			String GZhopenID = "";
			UserEntity userEntity = userMapper.getopenId(applyExpensesEntity.getApproveManOpenId());
			applyExpensesEntity.setTripDetails(JSONArray.fromObject(applyExpensesEntity.getTripDetails_list()).toString());
			List<TravelAddressDTO> list =  (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpensesEntity.getTripDetails()),TravelAddressDTO.class);
			/*String beginAddress = "";
			String trip = "";
			if(list.size()>0){
				for(TravelAddressDTO traveAddressDTO:list){
					beginAddress += traveAddressDTO.getBeginAddress()+"-"+traveAddressDTO.getEndAddress();
					trip += traveAddressDTO.getVehicle()+";";
				}
			}*/
			
			if(userEntity!=null){
				GZhopenID = userEntity.getModifiedby();
				String title = "您好,"+applyExpensesEntity.getApplyMan()+"提交的出差行程已经撤销";
				String mark = "已撤销";
				SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setApplyMan(applyExpensesEntity.getApplyMan());
				weiEntity.setStatus("已撤销");
				weiEntity.setApplyTime(smf.format(new Date()));
				/*weiEntity.setTripContent(applyExpensesEntity.getApplyTime()+","+beginAddress+","+trip);*/
				try {
					CommonUtil.sendPrivateApproveMessage(GZhopenID, weiEntity, title, mark);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		workflowUtil.deleteProcess(taskId);
		return applyExpensesMapper.deleteApplyExpese(id);
	}
	
	private List<TravelAddressDTO> transformJson(String tripDetails) {
	    List<TravelAddressDTO> travelAddressDTOS = new ArrayList<TravelAddressDTO>();
	    System.out.println("tripDetail是"+tripDetails);
	    String[] arr1 = tripDetails.split("\\*");
	    for (int i = 0; i < arr1.length; i++) {
	      String[] arr2 = arr1[i].split(",");
	      TravelAddressDTO travelAddressDTO = new TravelAddressDTO();
	      travelAddressDTO.setBeginAddress(arr2[0]);
	      travelAddressDTO.setEndAddress(arr2[1]);
	      travelAddressDTO.setVehicle(arr2[2]);
	      travelAddressDTOS.add(travelAddressDTO);
	    }
	    return travelAddressDTOS;
	  }

	//员工确认查询
	@Override
	public List<ApplyExpensesEntity> listApplyExpense(String openId) {
		System.out.println("openId是"+openId);
		List<Task> tasks = workflowUtil.getTaskByIds("applyExpens", openId,"applyStaff");
		System.out.println("任务是"+tasks);
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		System.out.println("获取Id是"+busAndTaskId);
		System.out.println("获取"+busAndTaskId.keySet());
		List<ApplyExpensesEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
			String travelers = applyExpens.getTripDetails();
			JSONArray fromObject = JSONArray.fromObject(travelers);
			/*for(java.util.Iterator tor=fromObject.iterator();tor.hasNext();){
				JSONObject job = (JSONObject)tor.next();
				System.out.println("name是"+job.get("beginAddress"));
			}*/
			System.out.println("json格式化"+JSONArray.fromObject(travelers));
			System.out.println("字符串是"+travelers);
			String str = applyExpens.getId()+"";
			String taskId = busAndTaskId.get(str);
			applyExpens.setTaskId(taskId);
			list.add(applyExpens);
		}
		System.out.println("查询差旅"+list);
		return list;
	}

	//领导审批
	@Override
	public int approveApplyExpense(String id,String taskId,String result,String comment) {
		System.out.println("taskId是"+taskId);
		System.out.println("领导审批");
		ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
		System.out.println("查看"+applyExpens);
		System.out.println("id是"+id);
		String GZhopenID = "";
		String applyMan = "";
		UserEntity userEntity = userMapper.getopenId(applyExpens.getOpenId());
		if(userEntity!=null){
			GZhopenID = userEntity.getModifiedby();
			applyMan = userEntity.getCode();
		}
		Map<String,Object> map = new HashMap<>();
		map.put("user", applyMan);
		map.put("result", result);
		List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpens
		          .getTripDetails()), TravelAddressDTO.class);
		System.out.println("查看"+travelAddress);
		String beginAddress = "";
		String trip = "";
		for(TravelAddressDTO traveAddressDTO:travelAddress){
			beginAddress += traveAddressDTO.getBeginAddress()+"-"+traveAddressDTO.getEndAddress();
			trip += traveAddressDTO.getVehicle()+";";
		}
		System.out.println("判断是驳回还是通过"+result);
		if(result.equals("false")){
			completeApplyTask( taskId, comment, result, "待修改", map);
			String title = "您好，您提交的出差行程已审批驳回";
			String mark = "请在微信小程序查看详情";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApproveMan(applyExpens.getApproveMan());//审批人
			weiEntity.setApplyMan(applyExpens.getApplyMan());
			weiEntity.setStatus(applyExpens.getCause());
			weiEntity.setTripContent(applyExpens.getApplyTime()+","+beginAddress+","+trip);
			sendout(GZhopenID,weiEntity,title,mark);//推送消息
			return 1;
		}
		
		completeApplyTask(taskId,comment,result,"员工确认",map);
		String title = "您好，您提交的出差行程已审批通过";
		String mark = "审批通过后点击执行可报销";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setApproveMan(applyExpens.getApproveMan());//审批人
		weiEntity.setApplyMan(applyExpens.getApplyMan());
		weiEntity.setStatus(applyExpens.getCause());
		weiEntity.setTripContent(applyExpens.getApplyTime()+","+beginAddress+","+trip);
		sendout(GZhopenID,weiEntity,title,mark);//微信消息推送
		return 1;
	}

	//领导人审批查看
	@Override
	public List<ApplyExpensesEntity> selectApplyExpense(String openId) {
		//openId = "21211212";
		openId = "oSCXE5BJ0sn0rCbIRCeChf7A4XLM";//领导人审批
		System.out.println("拿到的openId是"+openId);
		List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",openId,"applyApprove");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		
		/*List<String> s = workflowUtil.getBussinessIdsByTasks(tasks);*/
		List<ApplyExpensesEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			System.out.println("获取值是"+busAndTaskId.values());
			ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
			if(applyExpens!=null){
				String str = applyExpens.getId()+"";
				String taskId = busAndTaskId.get(str);
				String instanceId = workflowUtil.getTaskById(taskId).getProcessInstanceId();
				System.out.println("查看的Id是"+instanceId);
				List<Comment> processInstanceComments = taskService.getProcessInstanceComments(instanceId);
				if(processInstanceComments.size()>0){
					applyExpens.setComment(processInstanceComments.get(0).getFullMessage());
				}
				applyExpens.setTaskId(taskId);
				applyExpens.setTitle("差旅费");
				list.add(applyExpens);
			}
			
		}
		return list;
	}


	//员工确认
	@Override
	public int confirmApplyExpense(String id,String taskId,String confirm) {
		System.out.println("员工确认");
		Map<String,Object> map = new HashMap<>();
		if(confirm.equals("false")){
			map.put("cofirm", confirm);
			completeApplyTask(taskId, "", confirm, "完结", map);
			applyExpensesMapper.deleteApplyExpese(id);
			return 1;
		}
		
		/*map.put("finaceApply", userOpenId);*/
		map.put("cofirm", confirm);
		System.out.println("财务审批");
		/*completeApplyTask(taskId,"",confirm,"财务审批",map);*/
		ApplyExpensesEntity applyExpensesEntity = applyExpensesMapper.getApplyExpens(id);
		if(applyExpensesEntity!=null){
			applyExpensesEntity.setApproveState("财务审批");
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
			workflowUtil.JumpEndActivity(taskId, "finaceApply",null);
		}
		
		return 1;
	}


	public List<ApplyExpensesEntity> queryApplyExpense(ApplyExpensesEntity applyExpensesEntity){
		StringBuffer sql = new StringBuffer("select * from b_travelexpenses  where 1=1");
		if(applyExpensesEntity.getApproveState() != null)
		   if(!"".equals(applyExpensesEntity.getApproveState())){
			sql.append(" and ApproveState like '%" + applyExpensesEntity.getApproveState() + "%'");
				
			}
		if (applyExpensesEntity.getDepartment() != null)
		  if (!"".equals(applyExpensesEntity.getDepartment())) {
	       sql.append(" and Department like '%" + applyExpensesEntity.getDepartment() + "%'");
			 }
		if (applyExpensesEntity.getTravelers() != null)
	      if (!"".equals(applyExpensesEntity.getTravelers())) {
	        sql.append(" and Travelers like '%" + applyExpensesEntity.getTravelers() + "%'");
	      }
	    if (applyExpensesEntity.getTripDetails() != null)
	      if (!"".equals(applyExpensesEntity.getTripDetails())) {
	        sql.append(" and TripDetails like '%" + applyExpensesEntity.getTripDetails() + "%'");
	      }
	    if (applyExpensesEntity.getBeginTime() != null)
	      if (!"".equals(applyExpensesEntity.getBeginTime())) {
	        sql.append(" and BeginTime >= "+"'" + applyExpensesEntity.getBeginTime()+"'" +"");
	      }
	    if (applyExpensesEntity.getEndTime() != null)
	      if (!"".equals(applyExpensesEntity.getEndTime())) {
	        sql.append(" and EndTime <= " +"'"+ applyExpensesEntity.getEndTime()+"'" + "");
	      }
	    if (applyExpensesEntity.getApplyMan() != null)
	      if (!"".equals(applyExpensesEntity.getApplyMan())) {
	        sql.append(" and ApplyMan like '%" + applyExpensesEntity.getApplyMan() + "%'");
	      }
	    
	    sql.append(" order by ID desc");
	    System.out.println(sql);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql.toString());
		/*List<ApplyExpensesEntity> searchApplyExpense = applyExpensesMapper.searchApplyExpense(applyExpensesEntity);
		*/
		List<ApplyExpensesEntity> searchApplyExpense = new ArrayList<>();
	    for (Map<String, Object> map : queryForList) {
	    	ApplyExpensesEntity tE = new ApplyExpensesEntity();
	        tE.setId(map.get("ID").toString());
	        tE.setDepartment(map.get("Department") == null ? "" : map.get("Department").toString());
	        tE.setTravelers(map.get("Travelers") == null ? "" : map.get("Travelers").toString());
	        tE.setCause(map.get("Cause") == null ? "" : map.get("Cause").toString());
//		        tE.setTripMode(map.get("TripMode") == null ? "" : map.get("TripMode").toString());
//		        tE.setStartAddress(map.get("StartAddress") == null ? "" : map.get("StartAddress").toString());
//		        tE.setMiddAddress(map.get("MiddAddress") == null ? "" : map.get("MiddAddress").toString());
//		        tE.setEndAddress(map.get("EndAddress") == null ? "" : map.get("EndAddress").toString());
	        tE.setTripDetails(map.get("TripDetails") == null ? "" : map.get("TripDetails").toString());
	        tE.setBeginTime(map.get("BeginTime") == null ? "" : map.get("BeginTime").toString());
	        tE.setEndTime(map.get("EndTime") == null ? "" : map.get("EndTime").toString());
	        tE.setTravelDays(map.get("TravelDays") == null ? 0 : Integer.valueOf(map.get("TravelDays").toString()));
	        tE.setTotalBudget(map.get("TotalBudget") == null ? 0.0f : Float.parseFloat(map.get("TotalBudget").toString()));
	        tE.setIsTest(map.get("IsTest") == null ? "" : map.get("IsTest").toString());
	        tE.setApplyTime(map.get("ApplyTime") == null ? "" : map.get("ApplyTime").toString());
	        tE.setApplyMan(map.get("ApplyMan") == null ? "" : map.get("ApplyMan").toString());
	        tE.setApproveMan(map.get("ApproveMan") == null ? "" : map.get("ApproveMan").toString());
	        tE.setRemarks(map.get("Remarks") == null ? "" : map.get("Remarks").toString());
	        tE.setApproveState(map.get("ApproveState") == null ? "" : map.get("ApproveState").toString());

	        //json字符串转成list
	        List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(tE
	            .getTripDetails()), TravelAddressDTO.class);
	        tE.setTripDetails_list(travelAddress);
	        //list转成string
	        System.out.println("jso转换list数据"+travelAddress);
	        tE.setTripDetails(transformToString(travelAddress));
	        System.out.println("字符修改是"+transformToString(travelAddress));
	        searchApplyExpense.add(tE);
	      }
	    return searchApplyExpense;
	}
	
	
	@Override
	public List<ApplyExpensesEntity> finaceApplyExpense(ApplyExpensesEntity applyExpensesEntity) {
		
		List<ApplyExpensesEntity> searchApplyExpense = queryApplyExpense(applyExpensesEntity);
		List<Task> tasks = workflowUtil.getTaskByIds("applyExpens","finaceApply");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		System.out.println("获取Id是"+busAndTaskId);
		System.out.println("获取"+busAndTaskId.keySet());
		List<ApplyExpensesEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			System.out.println("获取值"+id);
			for(ApplyExpensesEntity applExpensesEntity:searchApplyExpense){
				if(id.equals(applExpensesEntity.getId())){
					ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
					//json字符串转成list
				    List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpens
				          .getTripDetails()), TravelAddressDTO.class);
				    applyExpens.setTripDetails_list(travelAddress);
				    applyExpens.setTripDetails(transformToString(travelAddress));
					String str = applyExpens.getId()+"";
					String taskId = busAndTaskId.get(str);
					applyExpens.setTaskId(taskId);
					applyExpens.setTitle("差旅费");
					list.add(applyExpens);
				}
			}
			
		}
		
		return list;
	}

	
	
	//财务登记
	@Override
	public int registerApplyExpense(String taskId,String result,ApproveExpensesEntity approveExpensesEntity) {
		Map<String,Object> map = new HashMap<>();
		approveExpensesEntity.setId(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
		int flag = approveExpensesMapper.insertApproveExpense(approveExpensesEntity);
		if(flag==1){
			map.put("finance", "true");
			completeApplyTask(taskId,"","true","已登记",map);
			return 1;
		}

		
		
		return flag;
	}



	//员工使用OpenId获取差率
	@Override
	public List<ApplyExpensesEntity> searApplyExpense(String openId) {
		List<Task> tasks = workflowUtil.getTaskByIds("applyExpens", openId,"cltjsq");
		System.out.println("任务是"+tasks);
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		System.out.println("获取Id是"+busAndTaskId);
		System.out.println("获取"+busAndTaskId.keySet());
		List<ApplyExpensesEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
			String str = applyExpens.getId()+"";
			String taskId = busAndTaskId.get(str);
			applyExpens.setTaskId(taskId);
			list.add(applyExpens);
		}
		return list;
	}



	
	//小程序查看明细汇总
	@Override
	public Map<String,Object> searchApplyExpense(ApplyExpensesEntity applyExpensesEntity,Integer nowPage,Integer pageSize) {
		Map<String,Object> map = new HashMap<>();
		Integer userid = bUserService.getUserIdByOpenId(applyExpensesEntity.getOpenId());
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		applyExpensesEntity.setTravelers(applyExpensesEntity.getApplyMan());
		if(applyExpensesEntity.getApplyMan()==null){
			applyExpensesEntity.setApplyMan(user.getCode());
		}
		
		if(applyExpensesEntity.getApplyMan().equals("1")){
			applyExpensesEntity.setApplyMan(null);
		}
		
		
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat s = new SimpleDateFormat("yyyy");
	    if(applyExpensesEntity.getSearchBeginTime()==null&&applyExpensesEntity.getSearchEndTime()==null){
	    	String format = s.format(new Date());
	    	applyExpensesEntity.setSearchBeginTime(format+"-01-01");
	    	applyExpensesEntity.setEndTime(smf.format(new Date()));
	    }
		List<ApplyExpensesEntity> listnew = applyExpensesMapper.searchApplyExpense(applyExpensesEntity);
		for(ApplyExpensesEntity apply:listnew){
			List<ApproveExpensesEntity> approveExpensesByTId = approveExpensesMapper.getApproveExpensesByTId(apply.getId());
			float vehicleCost = 0;
			float foodAllowance = 0;
			float hotelExpense = 0;
			float parValueAllowance =0;
			float urbanTraffic =0;
			float otherCost =0;
			float repayCost = 0;
			float supplementalCost = 0;
			float inputTax = 0;
			float totalExpenses = 0;
			String paidTime = "";
			String fundSource = "";
			String voucherNum = "";
			String isTestCost = "";
			String testSite = "";
			String illustration = "";
			for(ApproveExpensesEntity approveExpensesEntity:approveExpensesByTId){
				vehicleCost+=applyExpensesEntity.getVehicleCost();
				foodAllowance+=applyExpensesEntity.getFoodAllowance();
				hotelExpense+=applyExpensesEntity.getHotelExpense();
				parValueAllowance+=applyExpensesEntity.getParValueAllowance();
				urbanTraffic+=applyExpensesEntity.getUrbanTraffic();
				otherCost+=applyExpensesEntity.getOtherCost();
				repayCost+=applyExpensesEntity.getRepayCost();
				supplementalCost+=applyExpensesEntity.getSupplementalCost();
				inputTax+=applyExpensesEntity.getInputTax();
				totalExpenses+=applyExpensesEntity.getTotalExpenses();
				paidTime+=applyExpensesEntity.getPaidTime()+";";
				fundSource+=applyExpensesEntity.getFundSource()+";";
				voucherNum+=applyExpensesEntity.getVoucherNum()+";";
				isTestCost+=applyExpensesEntity.getIsTestCost()+";";
				testSite+=applyExpensesEntity.getTestSite()+";";
				illustration+=applyExpensesEntity.getIllustration()+";";
			}
			apply.setVehicleCost(vehicleCost);
			apply.setFoodAllowance(foodAllowance);
			apply.setHotelExpense(hotelExpense);
			apply.setParValueAllowance(parValueAllowance);
			apply.setUrbanTraffic(urbanTraffic);
			apply.setOtherCost(otherCost);
			apply.setRepayCost(repayCost);
			apply.setSupplementalCost(supplementalCost);
			apply.setInputTax(inputTax);
			apply.setTotalExpenses(totalExpenses);
			apply.setPaidTime(paidTime);
			apply.setFundSource(fundSource);
			apply.setVoucherNum(voucherNum);
			apply.setIsTestCost(isTestCost);
			apply.setTestSite(testSite);
			apply.setIllustration(illustration);
		}
		
		
		if(nowPage !=0 && pageSize != 0){
			int start = (nowPage - 1) * pageSize;
			int total = listnew.size();
			List<publicEntity> list_all = new ArrayList<>();
		    if (start + pageSize <= total)
		      list_all.addAll(listnew.subList(start, start + pageSize));
		    else
		      list_all.addAll(listnew.subList(start, total));
		    map.put("list", list_all);
		    map.put("pagesize", listnew.size());
		}else{
			map.put("pagesize", listnew.size());
		}
		
		
		return map;
	}
	  

	
	
	public   void completeApplyTask(String taskId,String comment,String result,String status,Map<String,Object>map){
		System.out.println("taskIds"+taskId);
		String id = workflowUtil.findBussinessIdByTaskId(taskId);
		System.out.println("Id主键是"+id);
		ApplyExpensesEntity applyExpensesEntity = applyExpensesMapper.getApplyExpens(id);
		//修改业务状态
		if(status.equals("驳回待修改")){
			applyExpensesEntity.setApproveState("驳回待修改");
			applyExpensesEntity.setConfirm("2");//财务退回
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		}
		
		if(status.equals("待修改")){
			System.out.println("待修改");
			applyExpensesEntity.setApproveState("待修改");
			applyExpensesEntity.setConfirm("1");//部门退回
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		}
		
		if(status.equals("待部门审批")){
			applyExpensesEntity.setApproveState("待审批");
			applyExpensesEntity.setConfirm("0");
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		}
		
		if(status.equals("员工确认")){
			applyExpensesEntity.setApproveState("员工确认");
			applyExpensesEntity.setConfirm("3");//员工确认
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
			
		}
		if(status.equals("财务审批")){
			applyExpensesEntity.setApproveState("财务审批");
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		}
		
		if(status.equals("已登记")){
			applyExpensesEntity.setApproveState("已登记");
			applyExpensesMapper.updateApplyExpens(applyExpensesEntity);
		}
		
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowBean.setComment(comment);
		workflowUtil.completeTask(workflowBean, map);
	}


	@Override
	public List<ApplyExpensesEntity> statusApplyExpense(String openId) {
		return applyExpensesMapper.statusApplyExpense(openId);
	}
	
	
	public boolean decideTravel(List<TravelAddressDTO> travelAddress,List<TravelAddressDTO> aftertravelAddress){
		String star ="";
		  for(TravelAddressDTO add:travelAddress){
			  star+=add.getEndAddress()+";";
		  }
		  String end="";
		  for(TravelAddressDTO after:aftertravelAddress){
			  end+=after.getEndAddress()+";";
		  }
		  System.out.println("传入的数据为"+star);
		  System.out.println("查到的数据是"+end);
		  String [] ends = end.split(";");
		  int a = 0; 
		  int b = 0;
		  for(b=0;b<ends.length;b++){
			  System.out.println("截取数据"+ends[b]);
			  
			  if(star.indexOf(ends[b])==-1){
				  a++;
			  }
		  }
		  System.out.println("a是"+a);
		  System.out.println("b"+b);
		  return Math.abs(a)>=b-1;
	}
	
	
	//公司领导出行方式审批
	public boolean judgeTravel(List<TravelAddressDTO> travelAddress,List<TravelAddressDTO> aftertravelAddress,
			ApplyExpensesEntity applyExpensesEntity){
		boolean flag = false;
		
		UserEntity userEntity = userMapper.getopenId(applyExpensesEntity.getOpenId());
		List<Integer> id = userMapper.getRoleIdByUserId(userEntity.getId());
		String roleName = userMapper.getRoleName(id.get(0));
		 if(travelAddress.size()==aftertravelAddress.size()){
			 if(roleName.equals("公司领导")){
				 for(int i=0;i<travelAddress.size();i++){
					  System.out.println("公司老总的出行方式");
					  if(proIDcheck(travelAddress.get(i).getVehicle(),aftertravelAddress.get(i).getVehicle())){
						  flag=true;
					  }
				  } 
				}else{
					for(int i=0;i<travelAddress.size();i++){
						  System.out.println("出行方式"+travelAddress.get(i).getVehicle());
						  System.out.println("传过来的出行方式"+aftertravelAddress.get(i).getVehicle());
						  if(chkeck(travelAddress.get(i).getVehicle(),aftertravelAddress.get(i).getVehicle())){
							  flag=true;
						  }
					  } 
				}
		 }
		
		return flag;
	}

	
	 
	  //公司领导出差优先级判断
	  private boolean proIDcheck(String address,String afteradderss){
		  int a =0;
		  int b =0;
		  if(address.equals("飞机商务舱")){
			  a=1;
		  }
		  if(afteradderss.equals("飞机商务舱")){
			  b=1;
		  }
		  if(address.equals("飞机经济舱")){
			  a=0;
		  }
		  if(afteradderss.equals("飞机经济舱")){
			  b=0;
		  }
		  if(address.equals("火车高铁一等座")||address.equals("火车动车一等座")){
			  a=0;
		  }
		  if(afteradderss.equals("火车高铁一等座")||afteradderss.equals("火车动车一等座")){
			  b=0;
		  }
		  
		  if(address.equals("火车高铁二等座")||address.equals("火车动车软卧")||address.equals("火车动车二等座")||address.equals("火车其他列车软卧")||address.equals("火车其他列车硬卧")
				  ||address.equals("火车其他列车硬座")){
			  a=0;
		  }
		  if(afteradderss.equals("火车高铁二等座")||afteradderss.equals("火车动车软卧")||afteradderss.equals("火车动车二等座")||afteradderss.equals("火车其他列车软卧")||afteradderss.equals("火车其他列车硬卧")
				  ||address.equals("火车其他列车硬座")){
			  b=0;
		  }
		  
		  if(address.equals("轮船二等舱")||address.equals("轮船三等舱")||address.equals("租车")||address.equals("自驾")||address.equals("汽车")){
			  a=0;
		  }
	      if(afteradderss.equals("轮船二等舱")||afteradderss.equals("轮船三等舱")||afteradderss.equals("租车")||afteradderss.equals("自驾")||afteradderss.equals("汽车")){
			  b=0;
		  }
	      
	      if(a>b){
	    	  return true;
	      }else if(a==b){
	    	  return false;
	      }else if(a<b){
	    	  return false;
	      }
	      
		  
		  return false;
	  }
	
	//一般人员传入出行方式判断优先级
	  private boolean chkeck(String address,String afteradderss){
		  int a =0;
		  int b =0;
		  if(address.equals("飞机商务舱")){
			  a=4;
		  }
		  if(afteradderss.equals("飞机商务舱")){
			  b=4;
		  }
		  if(address.equals("飞机经济舱")){
			  a=3;
		  }
		  if(afteradderss.equals("飞机经济舱")){
			  b=3;
		  }
		  if(address.equals("火车高铁一等座")||address.equals("火车动车一等座")){
			  a=2;
		  }
		  if(afteradderss.equals("火车高铁一等座")||afteradderss.equals("火车动车一等座")){
			  b=2;
		  }
		  
		  if(address.equals("火车高铁二等座")||address.equals("火车动车软卧")||address.equals("火车动车二等座")||address.equals("火车其他列车软卧")||address.equals("火车其他列车硬卧")
				  ||address.equals("火车其他列车硬座")){
			  a=1;
		  }
		  if(afteradderss.equals("火车高铁二等座")||afteradderss.equals("火车动车软卧")||afteradderss.equals("火车动车二等座")||afteradderss.equals("火车其他列车软卧")||afteradderss.equals("火车其他列车硬卧")
				  ||afteradderss.equals("火车其他列车硬座")){
			  b=1;
		  }
		  
		  if(address.equals("轮船二等舱")||address.equals("轮船三等舱")||address.equals("租车")||address.equals("自驾")||address.equals("汽车")){
			  a=0;
		  }
	      if(afteradderss.equals("轮船二等舱")||afteradderss.equals("轮船三等舱")||afteradderss.equals("租车")||afteradderss.equals("自驾")||afteradderss.equals("汽车")){
			  b=0;
		  }
	      System.out.println("a是"+a);
	      System.out.println("b是"+b);
	      
	      if(a>b){
	    	  return false;
	      }else if(a==b){
	    	  return false;
	      }else if(a<b){
	    	  return true;
	      }
	      return true;
		
	  }


	@Override
	public Map<String,Object> listcCListApplyExpense(String cCListOpenId,Integer nowPage,Integer pageSize) {
		System.out.println("抄送人查看");
		Map<String,Object> mainmap =new HashMap<>();
		System.out.println("cCListOpenId是"+cCListOpenId);
		List<ApplyExpensesEntity> list = applyExpensesMapper.getCCListOpenId(cCListOpenId);
		for(ApplyExpensesEntity applyExpensesEntity : list){
			applyExpensesEntity.setTitle("差旅费");
		}
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
	
	
	//微信推送
	public void sendout(String GZhopenID,WeiEntity weiEntity,String title,String mark){
		try {
			CommonUtil.sendTravelApproveMessage(GZhopenID,weiEntity,title,mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String transformToString(List<TravelAddressDTO> list) {
	    String s = "";
	    if (list.size() == 2) {
	      s += "起点：" + list.get(0).getBeginAddress() + "，出差地：" +
	          list.get(0).getEndAddress() + "，交通方式：" + list.get(0).getVehicle() + "<br>";
	      s += "出差地：" + list.get(1).getBeginAddress() + "，终点：" +
	          list.get(1).getEndAddress() + "，交通方式：" + list.get(1).getVehicle() + "<br>";
	    } else if (list.size() > 2) {
	      s += "起点：" + list.get(0).getBeginAddress() + "，出差地1：" +
	          list.get(0).getEndAddress() + "，交通方式：" + list.get(0).getVehicle() + "<br> ";
	      for (int i = 1; i < list.size(); i++) {
	        s += "出差地" + i + "：" + list.get(i).getBeginAddress() + "，出差地" + (i + 1) + "：" +
	            list.get(i).getEndAddress() + "，交通方式：" + list.get(i).getVehicle() + "<br> ";
	      }
	      int last = list.size() - 1;
	      s += "出差地" + last + "：" + list.get(last).getBeginAddress() + "，终点：" +
	          list.get(last).getEndAddress() + "，交通方式：" + list.get(last).getVehicle() + "； ";
	    }
	    return s;
	  }


	@Override
	public List<ApplyExpensesEntity> searApplyExpenseApprove(ApplyExpensesEntity applyExpensesEntity) {
		applyExpensesEntity.setApproveState("已登记");
		List<ApplyExpensesEntity> listapplyExpens = queryApplyExpense(applyExpensesEntity);
		for(ApplyExpensesEntity applyExpens : listapplyExpens){
				/*if(applyExpens!=null){
					List<TravelAddressDTO> travelAddress = (List<TravelAddressDTO>) JSONArray.toCollection(JSONArray.fromObject(applyExpens
					          .getTripDetails()), TravelAddressDTO.class);
					applyExpens.setTripDetails_list(travelAddress);
					applyExpens.setTripDetails(transformToString(travelAddress));
					listapplyExpens.add(applyExpens);
				}*/
		}
		
		
		return listapplyExpens;
	}


	@Override
	public int financeReviewFail(String taskId) {
		Map<String,Object> map = new HashMap<>();
		map.put("finance", "false");
		completeApplyTask(taskId, "", "false", "驳回待修改", map);
		
		return 1;
	}


	@Override
	public int exportApplyExpenses(ApplyExpensesEntity applyExpensesEntity, String filePath) {
		List<ApplyExpensesEntity> applyExpenses = new ArrayList<ApplyExpensesEntity>();
	    applyExpenses = finaceApplyExpense(applyExpensesEntity);
	    // 如果不为null
	    if (applyExpenses.size() > 0) {
	      return exportApplyExpensesExcel(applyExpenses, filePath);
	    } else {
	      return 0;
	    }
	}
	
	private int exportApplyExpensesExcel(List<ApplyExpensesEntity> applyExpenses, String path) {
	    // 创建一个工作簿
	    XSSFWorkbook workbook;
	    try {
	      workbook = new XSSFWorkbook();
	      // 添加一个sheet,sheet名
	      XSSFSheet sheet = workbook.createSheet("差旅费待登记列表");
	      // 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
	      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 15));
	      // 创建第一行
	      XSSFRow titleRow = sheet.createRow(0);
	      // 创建标题单元格格式
	      XSSFCellStyle titleStyle = workbook.createCellStyle();
	      // 创建一个居中格式
	      titleStyle.setAlignment(HorizontalAlignment.CENTER);
	      // 创建一个字体
	      XSSFFont titleFont = workbook.createFont();
	      titleFont.setFontHeight(16);// 标题字体为16号
	      // 将字体应用到当前的格式中
	      titleStyle.setFont(titleFont);
	      // 在第一行中创建一个单元格
	      XSSFCell titleCell = titleRow.createCell(0);
	      // 设置值和样式，标题
	      titleCell.setCellValue("差旅费待登记列表");
	      titleCell.setCellStyle(titleStyle);
	      // ------------以上为第一行------------
	      // 在合适位置调整自适应
	      sheet.setColumnWidth(0, 4700);
	      sheet.setColumnWidth(1, 4000);
	      sheet.autoSizeColumn(2);
	      sheet.autoSizeColumn(3);
	      sheet.setColumnWidth(4, 3300);
	      sheet.setColumnWidth(5, 3300);
	      sheet.setColumnWidth(6, 4000);
	      sheet.setColumnWidth(7, 3300);
	      sheet.setColumnWidth(8, 3300);
	      sheet.autoSizeColumn(9);
	      sheet.autoSizeColumn(10);
	      sheet.setColumnWidth(11, 3700);
	      sheet.setColumnWidth(12, 3300);
	      sheet.autoSizeColumn(13);
	      sheet.autoSizeColumn(14);
	      sheet.autoSizeColumn(15);
	      // 设置其他正文单元格格式
	      XSSFCellStyle style = workbook.createCellStyle();
	      style.setAlignment(HorizontalAlignment.CENTER);
	      // 设置第二行表头
	      XSSFRow rowHeader = sheet.createRow(1);
	      XSSFCell cell = rowHeader.createCell(0);// 第一列
	      cell.setCellValue("审批单号");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(1);// 第二列
	      cell.setCellValue("部门");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(2);// 第三列
	      cell.setCellValue("出差人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(3);// 第四列
	      cell.setCellValue("事由");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(4);// 第五列
	      cell.setCellValue("出发地");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(5);// 第六列
	      cell.setCellValue("目的地");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(6);// 第七列
	      cell.setCellValue("交通方式");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(7);// 第八列
	      cell.setCellValue("出发日期");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(8);// 第九列
	      cell.setCellValue("返回日期");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(9);// 第十列
	      cell.setCellValue("出差天数");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(10);// 第十一列
	      cell.setCellValue("总预算");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(11);// 第十二列
	      cell.setCellValue("是否属于试验");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(12);// 第十三列
	      cell.setCellValue("申请时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(13);// 第十四列
	      cell.setCellValue("申请人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(14);// 第十五列
	      cell.setCellValue("审批人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(15);// 第十六列
	      cell.setCellValue("备注");
	      cell.setCellStyle(style);
	      // 表头完成------------------
	      int index = 1;// 行序号，应从第三行开始，每次循环进行++
	      // 遍历集合将数据写到excel中
	      if (applyExpenses.size() > 0) {
	        for (ApplyExpensesEntity applyExpense : applyExpenses) {
	          // 行号++，2开始
	          index++;
	          // 生成一个新行
	          XSSFRow row = sheet.createRow(index);
	          XSSFCell rowCell = row.createCell(0);// 第一列
	          rowCell.setCellValue(applyExpense.getId());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(1);// 第二列
	          rowCell.setCellValue(applyExpense.getDepartment());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(2);// 第三列
	          rowCell.setCellValue(applyExpense.getTravelers());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(3);// 第四列
	          rowCell.setCellValue(applyExpense.getCause());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(7);// 第八列
	          rowCell.setCellValue(applyExpense.getBeginTime());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(8);// 第九列
	          rowCell.setCellValue(applyExpense.getEndTime());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(9);// 第十列
	          rowCell.setCellValue(applyExpense.getTravelDays());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(10);// 第十一列
	          rowCell.setCellValue(applyExpense.getTotalBudget());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(11);// 第十二列
	          rowCell.setCellValue(applyExpense.getIsTest());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(12);// 第十三列
	          rowCell.setCellValue(applyExpense.getApplyTime());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(13);// 第十四列
	          rowCell.setCellValue(applyExpense.getApplyMan());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(14);// 第十五列
	          rowCell.setCellValue(applyExpense.getApproveMan());
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(15);// 第十六列
	          rowCell.setCellValue(applyExpense.getRemarks());
	          rowCell.setCellStyle(style);

	          if (applyExpense.getTripDetails_list().size() > 0) {
	            rowCell = row.createCell(4);// 第五列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(0).getBeginAddress());
	            rowCell.setCellStyle(style);

	            rowCell = row.createCell(5);// 第六列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(0).getEndAddress());
	            rowCell.setCellStyle(style);

	            rowCell = row.createCell(6);// 第七列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(0).getVehicle());
	            rowCell.setCellStyle(style);
	          } else {
	            rowCell = row.createCell(4);// 第五列
	            rowCell.setCellValue("");
	            rowCell.setCellStyle(style);

	            rowCell = row.createCell(5);// 第六列
	            rowCell.setCellValue("");
	            rowCell.setCellStyle(style);

	            rowCell = row.createCell(6);// 第七列
	            rowCell.setCellValue("");
	            rowCell.setCellStyle(style);
	          }
	          int rowNum = 0;
	          for (int i = 1; i < applyExpense.getTripDetails_list().size(); i++) {
	            rowNum = i;
	            index++;
	            // 生成一个新行
	            XSSFRow newRow = sheet.createRow(index);
	            rowCell = newRow.createCell(4);// 第五列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(i).getBeginAddress());
	            rowCell.setCellStyle(style);

	            rowCell = newRow.createCell(5);// 第六列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(i).getEndAddress());
	            rowCell.setCellStyle(style);

	            rowCell = newRow.createCell(6);// 第七列
	            rowCell.setCellValue(applyExpense.getTripDetails_list().get(i).getVehicle());
	            rowCell.setCellStyle(style);
	          }
	          if (rowNum > 0) {
	            for (int i = 0; i < 16; i++) {
	              if (i != 4 && i != 5 && i != 6)
	                sheet.addMergedRegion(new CellRangeAddress((index - rowNum), index, i, i));
	            }
	          }
	        }
	      }
	      // 将文件保存到指定位置
	      FileOutputStream out = new FileOutputStream(path);
	      workbook.write(out);
	      out.close();
	      return 1;
	    } catch (Exception e) {
	      e.printStackTrace();
	      return 0;
	    }
	  }


	@Override
	public List<Map<String, Object>> countApplyExpense(String openId) {
		Date date = new Date();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		String format = smf.format(date);
		UserEntity userEntity = userMapper.getopenId(openId);
		String applyman = "";
		if(userEntity!=null){
			applyman = userEntity.getCode();
		}
		return applyExpensesMapper.countApplyExpense(applyman,format+"%");
	}


	@Override
	public Map<String, Object> getcClistApplyExpense(String cCListOpenId, Integer nowPage, Integer pageSize,
			String beginTime, String endTime, String applyMan,String state) {
		List<ApplyExpensesEntity> list = new ArrayList<>();
		Map<String,Object> minmap = new HashMap<>();
		ApplyExpensesEntity applyExpensesEntity = new ApplyExpensesEntity();
		if(state.equals("0")){
			applyExpensesEntity.setcCListOpenId(cCListOpenId);
			applyExpensesEntity.setApplyMan(applyMan);
			applyExpensesEntity.setBeginTime(beginTime);
			applyExpensesEntity.setEndTime(endTime);
			applyExpensesEntity.setTitle("差旅费");
			list = applyExpensesMapper.getCCListOpenIdsearch(applyExpensesEntity);
			for(ApplyExpensesEntity apply:list){
				apply.setTitle("差旅费");
			}
		}else if(state.equals("1")){
			applyExpensesEntity.setcCListOpenId(cCListOpenId);
			applyExpensesEntity.setApplyMan(applyMan);
			applyExpensesEntity.setBeginTime(beginTime);
			applyExpensesEntity.setEndTime(endTime);
			list = applyExpensesMapper.getCCListOpenIdsearchState(applyExpensesEntity);
			for(ApplyExpensesEntity apply:list){
				apply.setTitle("差旅费");
			}
		}
		if(nowPage !=0 && pageSize != 0){
			int start = (nowPage - 1) * pageSize;
			int total = list.size();
			List<publicEntity> list_all = new ArrayList<>();
		    if (start + pageSize <= total)
		      list_all.addAll(list.subList(start, start + pageSize));
		    else
		      list_all.addAll(list.subList(start, total));
			
		    minmap.put("pagesize", list.size());
		    minmap.put("list", list_all);
		}else{
			minmap.put("pagesize", list.size());
		}
		
		return minmap;
	}


	@Override
	public int ifState(String id) {
		ApplyExpensesEntity applyExpens = applyExpensesMapper.getApplyExpens(id);
		applyExpens.setState("1");
		int flag = applyExpensesMapper.updateApplyExpens(applyExpens);
		if(flag==1){
			return flag;
		}
		return flag;
	}


	@Override
	public Map<String, Object> getcClistApplyExpenseState(String cCListOpenId, Integer nowPage, Integer pageSize) {
		Map<String,Object> mainmap =new HashMap<>();
		System.out.println("cCListOpenId是"+cCListOpenId);
		List<ApplyExpensesEntity> list = applyExpensesMapper.getCCListOpenIdState(cCListOpenId);
		for(ApplyExpensesEntity applyExpensesEntity : list){
			applyExpensesEntity.setTitle("差旅费");
		}
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
