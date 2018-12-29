package com.hfoa.service.impl.privatecarimpl;

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
import java.util.Set;

import javax.jws.soap.SOAPBinding.Use;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.fop.layoutmgr.AreaAdditionUtil;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.permission.BClassMapper;
import com.hfoa.dao.privatecar.PrivateMapper;
import com.hfoa.dao.privatecar.PrivatecarinvoiceMapper;
import com.hfoa.dao.user.DepartmentMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.impl.travelApplyImple.ApplyExpenseServiceImpl;
import com.hfoa.service.permission.BClassService;
import com.hfoa.service.privatecarservice.PrivateCarService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.WorkflowUtil;

import oracle.net.aso.p;


/**
 * 
 * @author wzx
 * 权限类型service实现类
 */
@Service
public class PrivateCarServiceImpl implements PrivateCarService{
	
	
	@Autowired
	private PrivateMapper privateMapper;
	
	@Autowired
	private USerService bUserService;
	
	@Autowired
	private BDepartmentService bDepartmentService;
	
	@Autowired
	private BRoleService bRoleService;
	
	@Autowired
	private IdentityService identityService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private PrivatecarinvoiceMapper privatecarinvoiceMapper;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private WorkflowUtil workflowUtil;
	
	@Autowired
	private DepartmentMapper departmentMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private TaskService taskService;

	@Override
	public int insertPrivateCar(PrivateCarEntity privateCarEntity) {
		String GzhOpenId = "";
		String approveMan = "";
		UserEntity userEntity = userMapper.getopenId(privateCarEntity.getApproveOpenId());
		
		if(userEntity!=null){
			GzhOpenId = userEntity.getModifiedby();
			approveMan = userEntity.getCode();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		privateCarEntity.setStatus("待审批");
		/*privateCarEntity.setDoubleLength("1");*/
		privateCarEntity.setIfPass("未报销");
		privateCarEntity.setConfirm("0");
		privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
		privateCarEntity.setApplyTime(CommonUtil.getTime());
		if(privateCarEntity.getIfBefore()==null){
			privateCarEntity.setIfBefore("0");
			privateCarEntity.setBeforeDate("");
		}
		String bDate = privateCarEntity.getBeforeDate();
    	String aDate = privateCarEntity.getUserCarTime();
    	if("1".equals(privateCarEntity.getIfBefore())){
    		Date bDate1;
    		Date aDate1;
			try {
				bDate1 = sdf.parse(bDate);
				aDate1 = sdf.parse(aDate);
				String newBDate = sdf.format(bDate1);
				String newADate = sdf.format(aDate1);
				if(newADate.equals(newBDate)){
					privateCarEntity.setIfBefore("0");
		    		privateCarEntity.setBeforeDate("");
	    		}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
    	Map<String,Object> activitiMap = new HashMap<>();
    	int flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
    	if(flag==1){
    		UserEntity applyManUser = userMapper.getopenId(privateCarEntity.getOpenId());
    		if(applyManUser!=null){
    			activitiMap.put("privateCarApply", applyManUser.getCode());
        		//开启流程
    			String applyId = privateCarEntity.getApplyId()+"";
    			String objId="privateCar:"+applyId;
    			identityService.setAuthenticatedUserId(applyManUser.getCode());
    			synchronized (objId) {
    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
				}
    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",applyManUser.getCode(),"privateCarApply");
    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
    			System.out.println("bidr是"+bids);
    			int i=0;
    			for(;i<bids.size();i++){
    				System.out.println("bids.get(i)是"+bids.get(i));
    				if(bids.get(i)==applyId)
    					System.out.println("相等"+i);
    					break;
    			}
    			String taskId=tasks.get(i).getId();
    			System.out.println("任务编号："+taskId);
    			Map<String,Object> map = new HashMap<>();
    			map.put("privateCarApprove", approveMan);
    			//完成任务
    			System.out.println("任务编号TaskId是"+taskId);
    			completeApplyTask(taskId,null,map);
    			String title = "您好,您有一个新的私车公用需要审批,请及时处理。";
    			String mark = "待审批";
    			WeiEntity weiEntity = new WeiEntity();
    			weiEntity.setTitle("私车公用");
    			weiEntity.setApplyMan(privateCarEntity.getApproveMan());
    			weiEntity.setApplyTime(sdf.format(new Date()));
    			System.out.println("私车时间"+weiEntity.getApplyTime());
    			try {
    				CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
    			} catch (JsonGenerationException e) {
    				e.printStackTrace();
    			} catch (JsonMappingException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			return flag;
        	}
    	}
    		
    		
		return 0;
	}
	
	


	@Override
	public int updatePrivateCar(PrivateCarEntity privateCarEntity) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		int flag = 0;
		String GzhOpenId = "";
		UserEntity userEntity = userMapper.getopenId(privateCarEntity.getApproveOpenId());
		String approveMan = "";
		if(userEntity!=null){
			GzhOpenId = userEntity.getModifiedby();
			approveMan = userEntity.getCode();
		}
		String taskId = privateCarEntity.getTaskId();
		Map<String,Object>map = new HashMap<>();
		if(null!=taskId&&!taskId.equals("")){
			if(privateCarEntity.getCanUpdate().equals("0")){//待处理
				if(privateCarEntity.getConfirm().equals("0")){
					privateCarEntity.setStatus("领导审批");
					flag = privateMapper.updatePrivateCar(privateCarEntity);
					if(flag==1){
						map.put("privateCarApprove", approveMan);
						completeApplyTask(taskId, null, map);
						String title = "您好,您有一个新的私车公用需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("私车公用");
						weiEntity.setApplyMan(privateCarEntity.getApproveMan());
						weiEntity.setApplyTime(smf.format(new Date()));
						try {
							CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
						} catch (JsonGenerationException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return flag;
					}
					return flag;
				}
			}else if(privateCarEntity.getCanUpdate().equals("1")){//进行中
				PrivateCarEntity privateCar = privateMapper.getPrivateCar(privateCarEntity.getApplyId());
				PrivatecarinvoiceEntity privatecarinvoiceEntity = privatecarinvoiceMapper.getApplyIdsPrivatecarinvoiceEntity(privateCarEntity.getApplyId());
				if(privateCar!=null){
					boolean flagdate = checkDate(privateCarEntity.getUserCarTime(), privateCarEntity.getUserCarTime());
					if(flagdate){//用车时间更改变
						privateCarEntity.setStatus("待审批");
						String title = "您好,您有一个新的私车公用需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("私车公用");
						weiEntity.setApplyMan(privateCarEntity.getApproveMan());
						weiEntity.setApplyTime(smf.format(new Date()));
						try {
							CommonUtil.sendPrivateApproveMessage(GzhOpenId, weiEntity, title, mark);
						} catch (JsonGenerationException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						workflowUtil.JumpEndActivity(taskId, "privateCarApprove",null);
						
						if(privatecarinvoiceEntity!=null){
							String[] id = privatecarinvoiceEntity.getApplyIds().split(",");
							cawiu(id,privatecarinvoiceEntity,privateCarEntity);
						}
						
					} 
					if(privateCarEntity.getSureLength()>(privateCar.getSureLength())){
						privateCarEntity.setStatus("待审批");
						workflowUtil.JumpEndActivity(taskId, "privateCarApprove",null);
						if(privatecarinvoiceEntity!=null){
							String[] id = privatecarinvoiceEntity.getApplyIds().split(",");
							cawiu(id,privatecarinvoiceEntity,privateCarEntity);
						}
						String title = "您好,您有一个新的私车公用需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("私车公用");
						weiEntity.setApplyMan(privateCarEntity.getApproveMan());
						weiEntity.setApplyTime(smf.format(new Date()));
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
					
					String beginAddress = privateCar.getBeginAddress()+"-"+privateCar.getPassAddress()+"-"+privateCar.getDestination();
					String endinAddress = privateCarEntity.getBeginAddress()+"-"+privateCarEntity.getPassAddress()+"-"+privateCarEntity.getDestination();
					if(!beginAddress.contains(endinAddress)){//不包含重新审批
						privateCarEntity.setStatus("待审批");
						workflowUtil.JumpEndActivity(taskId, "privateCarApprove",null);
						if(privatecarinvoiceEntity!=null){
							String[] id = privatecarinvoiceEntity.getApplyIds().split(",");
							cawiu(id,privatecarinvoiceEntity,privateCarEntity);
						}
						String title = "您好,您有一个新的私车公用需要审批,请及时处理。";
						String mark = "待审批";
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setTitle("私车公用");
						weiEntity.setApplyMan(privateCarEntity.getApproveMan());
						weiEntity.setApplyTime(smf.format(new Date()));
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
		}
		
		return privateMapper.updatePrivateCar(privateCarEntity);
	}
	
	
	
	public void cawiu(String[] id, PrivatecarinvoiceEntity privatecarinvoiceEntity,PrivateCarEntity privateCarEntity){
		if(id.length==1){
			privatecarinvoiceMapper.deletePrivateCarinvoice(privatecarinvoiceEntity.getApplyId());
		}else{
			String applys = removeString(privatecarinvoiceEntity.getApplyIds(),privateCarEntity.getApplyId());
			int i = id.length;
			privatecarinvoiceEntity.setApplyIds(applys);
			privatecarinvoiceEntity.setSureLength(String.valueOf(i--));
			String sum = privatecarinvoiceEntity.getSum();
			double sureLength = privateCarEntity.getSureLength();
			privatecarinvoiceEntity.setSum(String.valueOf(Double.valueOf(sum)-sureLength));
			int j = privatecarinvoiceMapper.updatePrivateCarinvoice(privatecarinvoiceEntity);
		}
		
	}
	

	public boolean checkDate(String beforeDate,String afterDate){
		boolean flag = false;
		if(!beforeDate.equals(afterDate)){
			flag = true;
		}
		return flag;
		
	}
	
	
	
	@Override
	public int deletePrivateCar(String applyId,String taskId) {
		PrivateCarEntity privateCarEntity = privateMapper.getPrivateCar(applyId);
		if(privateCarEntity!=null){
			UserEntity userEntity = userMapper.getopenId(privateCarEntity.getApproveOpenId());
			String GzhOpenId = "";
			if(userEntity!=null){
				GzhOpenId = userEntity.getModifiedby();
			}
			String title = "您好,"+privateCarEntity.getApplyMan()+"的私车公用已经撤销。";
			String mark = "已撤销";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setTitle("私车公用");
			weiEntity.setApplyMan(privateCarEntity.getApplyMan());
			weiEntity.setApplyTime(privateCarEntity.getApplyTime());
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
		workflowUtil.deleteProcess(taskId);
		return privateMapper.deletePrivateCar(applyId);
	}
	
	
	/**
	 * 员工查询
	 * @param openId
	 * @return
	 */
	@Override
	public List<PrivateCarEntity> privateApplytion(String openId) {
		openId = "213213";
		List<Task> tasks = workflowUtil.getTaskByIds("privateCar",openId,"privateCarApply");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
				Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
				List<PrivateCarEntity> list = new ArrayList<>();
				for(String id:busAndTaskId.keySet()){
					 PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
					if(privateCar!=null){
						String str = privateCar.getApplyId()+"";
						String taskId = busAndTaskId.get(str);
						privateCar.setTaskId(taskId);
						list.add(privateCar);
					}
				}
		return list;
	}

	
	/**
	 * 领导查询审批
	 */
	@Override
	public List<PrivateCarEntity> listOpendIdPrivateCar(String openId) {
		openId = "12321312";
		List<Task> tasks = workflowUtil.getTaskByIds(openId);
		System.out.println("任务是"+tasks);
		/*HistoricProcessInstance hi = historyService.createHistoricProcessInstanceQuery().list();*/
		/*String startUserId = historyService.createHistoricProcessInstanceQuery()
		.processInstanceId(openId).singleResult().getStartUserId();
		System.out.println("startUserId是:"+startUserId);*/
		/*System.out.println("我发起的是"+hi.getStartUserId());*/
		List<PrivateCarEntity> list = new ArrayList<>();
		for(int i=0;i<tasks.size();i++){
			String taskId=tasks.get(i).getId();
			Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
			//2：使用任务对象Task获取流程实例ID
			String processInstanceId = task.getProcessInstanceId();
			//3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
			String startUserId = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult().getStartUserId();
			
		    HistoricActivityInstance singleResult = historyService.createHistoricActivityInstanceQuery()//  
	        .processInstanceId(processInstanceId)//  
	        .unfinished()//未完成的活动(任务)  
	        .singleResult();  
			
		    System.out.println("名称"+singleResult.getActivityName());
			System.out.println(startUserId);
		}
		
		
		//根据任务获取业务
		/*Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<PrivateCarEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			System.out.println(busAndTaskId.keySet());
			 PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
			if(privateCar!=null){
				String str = privateCar.getApplyId()+"";
				String taskId = busAndTaskId.get(str);
				privateCar.setTaskId(taskId);
				list.add(privateCar);
			}
		}*/
		
		
		return list;
	}
	

	@Override
	public PrivateCarEntity getPrivateCar(String applyId) {
		return privateMapper.getPrivateCar(applyId);
	}

	public String getDateTime() {
	    Date dtDate=new Date();
		SimpleDateFormat sm=new SimpleDateFormat("yyyyMMdd");
		return sm.format(dtDate);
	}
	
	public String CreateId(String department) {
		//String maxId="";
		String resultString="";
		System.out.println("部门是"+department);
		System.out.println("时间"+getDateTime());
		List<Object> i= privateMapper.getMaxId(department,getDateTime()+"%");
		resultString=getDateTime()+"0"+String.valueOf(bDepartmentService.getDepartmentIdByName(department))+"001";
		if (i.get(0)==null) {
			return resultString;
		}
		else {
			String idString=i.get(0).toString();
			System.out.print(i.size());
			int id1=Integer.valueOf(idString.substring(idString.length()-3,idString.length() ));
			String idsubString=idString.substring(0, idString.length()-3);
			return idsubString.concat(addZero(id1)) ;
		}
	}
	//生成id最大的后三位
	public  String addZero(int id)
	{
		String reString=String.valueOf(id+1);
		if((id+1)<10)
		{
			reString="00"+reString;
		}
		else if((id+1)>=10&&id<100) {
			reString="0"+reString;
		}
		else {
		}
		return reString;
	}
	
	/**
	 * 领导审批
	 */
	@Override
	public int approvalPrivateCar(String applyId, String taskId, String result,String comment) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String,Object> map = new HashMap<>();
		PrivateCarEntity privateCarEntity = privateMapper.getPrivateCar(applyId);
		String GzhOpenId = "";
		String applyMan = "";
		UserEntity userEntity = userMapper.getopenId(privateCarEntity.getOpenId());
		if(userEntity!=null){
			GzhOpenId = userEntity.getModifiedby();
			applyMan = userEntity.getCode();
		}
		
		if(result.equals("true")){
			privateCarEntity.setStatus("已通过");
			privateCarEntity.setApproveTime(CommonUtil.getTime());
			privateMapper.updatePrivateCar(privateCarEntity);
			map.put("privateCarStaff", applyMan);
			map.put("result", result);
			workflowUtil.JumpEndActivity(taskId, "privateCarStaff", map);
			/*completeApplyTask(taskId, "", map);*/
			String title = "您好,您的私车申请审批已经已通过,请及时处理。";
			String mark = "已通过";
			WeiEntity weiEntity = new WeiEntity();
			
			weiEntity.setApplyMan(privateCarEntity.getApproveMan());
			weiEntity.setStatus("审批通过");
			weiEntity.setApplyTime(smf.format(new Date()));
			try {
				CommonUtil.sendPrivateCarApplyMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return 1;
		}
		
		if(result.equals("false")){
			privateCarEntity.setStatus("待修改");
			privateCarEntity.setApproveTime(CommonUtil.getTime());
			int updatePrivateCar = privateMapper.updatePrivateCar(privateCarEntity);
			System.out.println("修改是"+updatePrivateCar);
			map.put("privateCarApply", applyMan);
			map.put("result", result);
			workflowUtil.JumpEndActivity(taskId, "privateCarApply", map);
			/*completeApplyTask(taskId, comment, map);*/
			String title = "您好,您的私车申请审批已经驳回,请及时处理。";
			String mark = "待审批";
			WeiEntity weiEntity = new WeiEntity();
			
			weiEntity.setApproveMan(privateCarEntity.getApproveMan());
			weiEntity.setStatus("已驳回");
			weiEntity.setApplyTime(smf.format(new Date()));
			try {
				CommonUtil.sendPrivateCarApplyMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return 1;
		}
		
		return 0;
	}
	
	
	public   void completeApplyTask(String taskId,String comment,Map<String,Object>map){
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowBean.setComment(comment);
		workflowUtil.completeTask(workflowBean, map);
	}




	/**
	 * 员工执行查询
	 */
	@Override
	public List<PrivateCarEntity> staffPrivateCar(String openId) {
		openId = "213213";
		List<Task> tasks = workflowUtil.getTaskByIds("privateCar",openId,"privateCarStaff");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<PrivateCarEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			 PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
			if(privateCar!=null){
				String str = privateCar.getApplyId()+"";
				String taskId = busAndTaskId.get(str);
				privateCar.setTaskId(taskId);
				list.add(privateCar);
			}
		}
		
		return list;
	}



	/**
	 * 员工执行
	 */

	@Override
	public int staffPrivateCarApprove(String applyId, String taskId, String staffresult) {
		
		Map<String,Object> map = new HashMap<>();
		PrivateCarEntity privateCarEntity = privateMapper.getPrivateCar(applyId);
		if(privateCarEntity!=null){
			UserEntity userEntity = userMapper.getopenId(privateCarEntity.getOpenId());
			String applyMan = "";
			if(userEntity!=null){
				applyMan = userEntity.getCode();
			}
			if(staffresult.equals("true")){
				privateCarEntity.setIfPerform("已执行");
				privateCarEntity.setIfPass("未报销");
				privateCarEntity.setIfSub("0");
				privateMapper.updatePrivateCar(privateCarEntity);
				map.put("privateCarReimbursement", applyMan);
				map.put("staffresult", staffresult);
				workflowUtil.JumpEndActivity(taskId, "privateCarReimbursement",map);
				/*completeApplyTask(taskId, "", map);*/
				return 1;
			}
			if(staffresult.equals("false")){
				privateMapper.deletePrivateCar(applyId);
				/*map.put("privateCarApply", privateCarEntity.getOpenId());*/
				map.put("staffresult", staffresult);
				completeApplyTask(taskId, "", map);
				return 1;
			}
		}
		return 0;
	}




	/**
	 * 凭票报销查询
	 */
	@Override
	public List<PrivateCarEntity> reimbursementPrivateCar(String openId) {
		List<Task> tasks = workflowUtil.getTaskByIds("privateCar",openId,"privateCarReimbursement");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<PrivateCarEntity> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			 PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
			if(privateCar!=null){
				String str = privateCar.getApplyId()+"";
				String taskId = busAndTaskId.get(str);
				privateCar.setTaskId(taskId);
				list.add(privateCar);
			}
		}
		
		return list;
	}




	/**
	 * 凭票报销
	 * @param privatecarinvoiceEntity
	 * @param taskId
	 * @return
	 */
	@Override
	public int reimbursementPrivateCarApprove(PrivatecarinvoiceEntity privatecarinvoiceEntity, String taskIds) {
		String applyIds = privatecarinvoiceEntity.getApplyIds();
		
		String[] ids = applyIds.split(",");
		String applyid = new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(new Date() );
		privatecarinvoiceEntity.setApplyId(applyid);
		privatecarinvoiceEntity.setApplyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") .format(new Date()));
		privatecarinvoiceEntity.setStatus("待财务审核");       
		privatecarinvoiceEntity.setSureLength(String.valueOf(ids.length));
		int flag = privatecarinvoiceMapper.insertPrivateCarinvoice(privatecarinvoiceEntity);
		if(flag==1){
			for(int i=0;i<ids.length;i++){
				PrivateCarEntity privateCar = privateMapper.getPrivateCar(ids[i]);
				privateCar.setIfSub("1");
				privateMapper.updatePrivateCar(privateCar);
			}
			
			String[] split = taskIds.split(",");
			Map<String,Object>map = new HashMap<>();
			/*map.put("privateCarFinance", "oSCXE5BDhAU_pKzZ0hG7zV9nb55k");*/
			for(int i=0;i<split.length;i++){
				String taskId = split[i];
				if(flag==1){
					completeApplyTask(taskId, "", map);
				}
			}
			
		}
		
		return flag;
	}




	@Override
	public List<PrivatecarinvoiceEntity> financePrivateCar(String openId) {
		
		List<PrivatecarinvoiceEntity> selectPrivatecarinvoiceEntity = privatecarinvoiceMapper.selectPrivatecarinvoiceEntity();
		
		
		return selectPrivatecarinvoiceEntity;
	}




	@Override
	public List<PrivateCarEntity> financePrivatreCartaskId(String openId, String applyIds,String canUpdate) {
		List<PrivateCarEntity> list = new ArrayList<>();
		try{
			if(canUpdate.equals("0")){//左边
				List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
				System.out.println("任务是"+tasks);
				//根据任务获取业务
				Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
				for(String id:busAndTaskId.keySet()){
					if(applyIds.indexOf(id)!=-1){
						if(id.indexOf(",")!=-1){
							String[] split = id.split(",");
							for(int i=0;i<split.length;i++){
								PrivateCarEntity privateCar = privateMapper.getPrivateCar(split[i]);
								if(privateCar!=null){
									String str = id+"";
									String taskId = busAndTaskId.get(str);
									privateCar.setTaskId(taskId);
									list.add(privateCar);
								}
							}
						}else{
							PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
							if(privateCar!=null){
								String str = privateCar.getApplyId()+"";
								String taskId = busAndTaskId.get(str);
								privateCar.setTaskId(taskId);
								list.add(privateCar);
							}
						}
						
					}
				}
				
			}else if(canUpdate.equals("1")){//右边
				PrivateCarEntity privateCar = privateMapper.getPrivateCar(applyIds);
				list.add(privateCar);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return list;
	}


	
	/**
	 * 凭证号报销
	 */
	
	@Override
	public int privateRegistervoucher(String taskId, String applyId, String voucherNum, String paidTime) {
		PrivatecarinvoiceEntity applyIdsPrivatecarinvoiceEntity = privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyId);
		String[] task = taskId.split(",");
		applyIdsPrivatecarinvoiceEntity.setVoucherNum(voucherNum);
		applyIdsPrivatecarinvoiceEntity.setPaidTime(paidTime);
		applyIdsPrivatecarinvoiceEntity.setStatus("已完成");
		applyIdsPrivatecarinvoiceEntity.setApproveMan("刘静");
		int carinvoice = privatecarinvoiceMapper.updatePrivateCarinvoice(applyIdsPrivatecarinvoiceEntity);
		Map<String,Object>map = new HashMap<>();
		for(int i=0;i<task.length;i++){
			completeApplyTask(task[i], "", map);
		}
		
		return carinvoice;
	}


	/**
	 * 财务审批
	 * @param taskId
	 * @param applyId
	 * @param finaceresult
	 * @return
	 */
	@Override
	public int financePrivateCarApprove(String taskId, String applyId, String finaceresult,String applyIdinvoice,String voucherNum,String paidTime,String comment) {
		System.out.println("taskId是"+taskId);
		System.out.println("applyId是"+applyId);
		System.out.println("app"+applyIdinvoice);
		/*PrivateCarEntity privateCar = privateMapper.getPrivateCar(applyId);*/
		PrivatecarinvoiceEntity applyIdsPrivatecarinvoiceEntity = privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyIdinvoice);
		
		String[] apply = applyId.split(",");
		String title = "您好,您的私车申请审批已经结束。";
		
		String applyman = "";
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		WeiEntity weiEntity = new WeiEntity();
		String GzhOpenId = "";
		Map<String ,Object> map = new HashMap<>();
		if(finaceresult.equals("true")){
			applyIdsPrivatecarinvoiceEntity.setVoucherNum(voucherNum);
			applyIdsPrivatecarinvoiceEntity.setPaidTime(paidTime);
			applyIdsPrivatecarinvoiceEntity.setStatus("已完成");
			applyIdsPrivatecarinvoiceEntity.setApproveMan("财务");
			int carinvoice = privatecarinvoiceMapper.updatePrivateCarinvoice(applyIdsPrivatecarinvoiceEntity);
			if(taskId!=null&&!taskId.equals("")){
				String[] task = taskId.split(",");
				for(int i=0;i<task.length;i++){
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(apply[i]);
					privateCar.setIfPass("已报销");
					privateCar.setIfSub("1");
					applyman = privateCar.getApplyMan();
					privateMapper.updatePrivateCar(privateCar);
					map.put("finaceresult", finaceresult);
					completeApplyTask(task[i], "", map);
					
					UserEntity userEntity = userMapper.getopenId(privateCar.getOpenId());
					if(userEntity!=null){
						GzhOpenId = userEntity.getModifiedby();
					}
					
				}
			}
			String mark = "已完结";
			weiEntity.setApproveMan(applyman);
			weiEntity.setStatus("审批通过");
			weiEntity.setApplyTime(smf.format(new Date()));
			try {
				CommonUtil.sendPrivateCarApplyMessage(GzhOpenId, weiEntity, title, mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return carinvoice;
		}
		
		if(finaceresult.equals("false")){
			PrivateCarEntity privateCar = privateMapper.getPrivateCar(applyId);
			System.out.println("boh"+privateCar);
			privateCar.setIfPass("财务驳回");
			privateCar.setIfSub("0");
			
			privateMapper.updatePrivateCar(privateCar);
			if(applyIdsPrivatecarinvoiceEntity!=null) {
				String applyIds = applyIdsPrivatecarinvoiceEntity.getApplyIds();
				String[] ids = applyIds.split(",");
				String sum = applyIdsPrivatecarinvoiceEntity.getSum();
				double sureLength = privateCar.getSureLength();
				int z =0;
				if(privateCar.getIfPass().equals("财务驳回")){
					z++;
				}
				System.out.println("z"+z);
				int i = ids.length;
				System.out.println("i是"+i);
				if(z==ids.length){
					int j = privatecarinvoiceMapper.deletePrivateCarinvoice(applyIdinvoice);
					if(j==1){
						map.put("privateCarReimbursement", privateCar.getApplyMan());
						map.put("finaceresult", finaceresult);
						completeApplyTask(taskId, comment, map);
						weiEntity.setApplyMan("财务");
						weiEntity.setStatus("已驳回");
						String mark = "已驳回";
						weiEntity.setApplyTime(smf.format(new Date()));
						try {
							CommonUtil.sendPrivateCarApplyMessage(GzhOpenId, weiEntity, title, mark);
						} catch (JsonGenerationException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return j;
					}
				}else{
					String applys = removeString(applyIds,applyId);
					applyIdsPrivatecarinvoiceEntity.setApplyIds(applys);
					System.out.println("i--s"+i--);
					applyIdsPrivatecarinvoiceEntity.setSureLength(String.valueOf(i--));
					applyIdsPrivatecarinvoiceEntity.setSum(String.valueOf(Double.valueOf(sum)-sureLength));
					System.out.println("------------"+applyIdsPrivatecarinvoiceEntity);
					int j = privatecarinvoiceMapper.updatePrivateCarinvoice(applyIdsPrivatecarinvoiceEntity);
					if(j==1){
						if(!taskId.equals("")&&taskId!=null){
							map.put("privateCarReimbursement", privateCar.getApplyMan());
							map.put("finaceresult", finaceresult);
							completeApplyTask(taskId, null, map);
						}
						weiEntity.setApplyMan("财务");
						weiEntity.setStatus("已驳回");
						String mark = "已驳回";
						weiEntity.setApplyTime(smf.format(new Date()));
						try {
							CommonUtil.sendPrivateCarApplyMessage(GzhOpenId, weiEntity, title, mark);
						} catch (JsonGenerationException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return j;
					}
				}
			}
			
		}
		
		
		return 0;
	}


	
	public String removeString(String applyIds,String applyId){
		System.out.println("appliDss "+applyIds);
		System.out.println("appl"+applyId);
		String apply = "";
		int indexOf = applyIds.indexOf(applyId);
		System.out.println(indexOf);
		if(indexOf==0){//说明是第一个
			apply = applyIds.replace(applyId+",","");
			return apply;
		}
		
		if(indexOf>applyIds.lastIndexOf(",")){//说明是最后一个
			apply = applyIds.replace(","+applyId,"");
			return apply;
		}
		
		char frontCharAt = applyIds.charAt(indexOf-1);
		char afterCharAt = applyIds.charAt(indexOf+13);
		if(String.valueOf(frontCharAt).equals(",")&&String.valueOf(afterCharAt).equals(",")){//在中间
			apply = applyIds.replace(applyId+",","");
			return apply;
		}else if(String.valueOf(frontCharAt).equals(",")){//说明是后一个
			apply = applyIds.replace(","+applyId, "");
		}
		
		
		return apply;
	}


	@Override
	public List<PrivatecarinvoiceEntity> uninvoiceDisplay() {
		
		
		return privatecarinvoiceMapper.selectPrivatecarinvoiceEntity();
	}




	
	/**
	 * 查看私车使用记录
	 */
	@Override
	public List<PrivateCarEntity> selectByApplyIds(String applyId,String openId) {
		PrivatecarinvoiceEntity privatecarinvoiceEntity = privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyId);
		System.out.println("查看私车"+privatecarinvoiceEntity);
		String applyIds = "";
		if(privatecarinvoiceEntity!=null){
			applyIds = privatecarinvoiceEntity.getApplyIds();
		}
		System.out.println("applyIds是"+applyIds);
		List<PrivateCarEntity> list = new ArrayList<>();
		List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		for(String id:busAndTaskId.keySet()){
			if(applyIds.indexOf(id)!=-1){
				if(id.indexOf(",")!=-1){
					String[] split = id.split(",");
					for(int i=0;i<split.length;i++){
						PrivateCarEntity privateCar = privateMapper.getPrivateCar(split[i]);
						if(privateCar!=null){
							String str = privateCar.getApplyId()+"";
							String taskId = busAndTaskId.get(str);
							privateCar.setTaskId(taskId);
							list.add(privateCar);
						}
					}
				}else{
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
					if(privateCar!=null){
						String str = privateCar.getApplyId()+"";
						String taskId = busAndTaskId.get(str);
						privateCar.setTaskId(taskId);
						list.add(privateCar);
					}
				}
				
			}
		}
		
		return list;
	}




	@Override
	public int passPrivateCars(String applyid, String applyId2, String taskId) {
		
		PrivatecarinvoiceEntity applyIdsPrivatecarinvoiceEntity = privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyid);
		String[] task = taskId.split(",");
		
		Map<String ,Object> map = new HashMap<>();
		
		/*applyIdsPrivatecarinvoiceEntity.setVoucherNum(voucherNum);
		applyIdsPrivatecarinvoiceEntity.setPaidTime(paidTime);*/
		applyIdsPrivatecarinvoiceEntity.setStatus("已报销");
		applyIdsPrivatecarinvoiceEntity.setApproveMan("刘静");
		int carinvoice = privatecarinvoiceMapper.updatePrivateCarinvoice(applyIdsPrivatecarinvoiceEntity);
		for(int i=0;i<task.length;i++){
			map.put("finaceresult", "true");
			completeApplyTask(task[i], null, map);
		}
		return carinvoice;
	}




	@Override
	public int backPrivateCars(String applyid, String taskId) {
		return 0;
	}




	@Override
	public List<PrivatecarinvoiceEntity> unSignInvoiceDisplay() {
		return privatecarinvoiceMapper.unSignInvoiceDisplay();
	}




	@Override
	public List<PrivatecarinvoiceEntity> invoiceDisplay() {
		return privatecarinvoiceMapper.invoiceDisplay();
	}




	@Override
	public List<PrivateCarEntity> selectByApplyIds(String applyId) {
		PrivatecarinvoiceEntity entity = privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyId);
		String ids = entity.getApplyIds();
		String[] id = ids.split(",");
		List<PrivateCarEntity> list = new ArrayList<>();
		for(int i=0;i<id.length;i++){
			PrivateCarEntity privateCar = privateMapper.getPrivateCar(id[i]);
			list.add(privateCar);
		}
		return list;
	}




	@Override
	public int privateCarInvoiceexportExcel(String number, String filePath) {
		List<PrivatecarinvoiceEntity> privateList = new ArrayList<PrivatecarinvoiceEntity>();
		privateList = getPrivateListByNum(number);
		return exportExcel(privateList, filePath);
	}
	
	//获得相关的私车公用信息
	private List<PrivatecarinvoiceEntity> getPrivateListByNum(String nlist) {
		List<PrivatecarinvoiceEntity> list = privatecarinvoiceMapper.invoiceDisplay();// 最终返回的list
		return list;
	}
	
	private int exportExcel(List<PrivatecarinvoiceEntity> privateList, String filePath) {
		// 创建一个工作簿
		XSSFWorkbook workbook;
		String sheetName = "私车公用明细表";
		try {
			workbook = new XSSFWorkbook();
			// 添加一个sheet,sheet名
			XSSFSheet sheet = workbook.createSheet(sheetName);
			// 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 18));
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
			titleCell.setCellValue(sheetName);
			titleCell.setCellStyle(titleStyle);
			// ------------以上为第一行------------
			// 在合适位置调整自适应
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(15);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			// 设置其他正文单元格格式
			XSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			// 设置第二行表头
			XSSFRow rowHeader = sheet.createRow(1);
			XSSFCell cell = rowHeader.createCell(0);// 第1列
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(1);// 第2列
			cell.setCellValue("审批单号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(2);// 第3列
			cell.setCellValue("部门");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(3);// 第4列
			cell.setCellValue("用车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(4);// 第5列
			cell.setCellValue("事由");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(5);// 第6列
			cell.setCellValue("出发地");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(6);// 第7列
			cell.setCellValue("途径地");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(7);// 第8列
			cell.setCellValue("目的地");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(8);// 第9列
			cell.setCellValue("状态");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(9);// 第10列
			cell.setCellValue("单程里程");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(10);// 第11列
			cell.setCellValue("计价里程");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(11);// 第11列
			cell.setCellValue("核定价格");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(12);// 第12列
			cell.setCellValue("申请人");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(13);// 第13列
			cell.setCellValue("审批人");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(14);// 第14列
			cell.setCellValue("申请时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(15);// 第15列
			cell.setCellValue("审批时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(16);// 第16列
			cell.setCellValue("执行状况");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(17);// 第16列
			cell.setCellValue("凭票报销金额");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(18);// 第16列
			cell.setCellValue("凭证号");
			cell.setCellStyle(style);
			
			// 表头完成------------------
			int index = 1;// 行号，应从第三行开始，每次循环进行++
			int z = 0;
			PrivatecarinvoiceEntity invoice = new PrivatecarinvoiceEntity();
			// 遍历集合将数据写到excel中
			if (privateList.size() > 0) {
				for (int i = 0; i < privateList.size(); i++) {
					
					// 对象
					invoice = privateList.get(i);
					String[] size = invoice.getApplyIds().split(",");
					// 得到数据行数
					int hs = size.length;
					
					for(String id:size){
						// 行号++，2开始
						index++;
						PrivateCarEntity priv = privateMapper.getPrivateCar(id);
						//合并审批时间、金额、凭证号
						sheet.addMergedRegion(new CellRangeAddress(index, index+hs-1, 15, 15));//首行、最后一行、首列、最后一列
						sheet.addMergedRegion(new CellRangeAddress(index, index+hs-1, 17, 17));//首行、最后一行、首列、最后一列
						sheet.addMergedRegion(new CellRangeAddress(index, index+hs-1, 18, 18));//首行、最后一行、首列、最后一列
						//将途径地json转为逗号隔开的字符串
						String pass = priv.getPassAddress();
						String newPass = "";
						if(pass!=null&&!"".equals(pass)&&pass.startsWith("[")){
							try {
								//将json解析为string
								JSONArray json = new JSONArray(pass);
								int iSize = json.length();
								if(iSize!=0){
									for(int x=0;x<iSize;x++){
										JSONObject obj = json.getJSONObject(x);
										if(x==iSize-1){
											newPass += obj.get("addressName");
										}else{
											newPass += obj.get("addressName")+",";
										}
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						XSSFRow row = sheet.createRow(index);
						
						XSSFCell rowCell = row.createCell(0);// 第1列(序号)
						rowCell.setCellValue(z + 1 + "");
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(1);// 第2列(私车-审批单号)
						rowCell.setCellValue(priv.getApplyId());
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(2);// 第3列(私车-部门)
						rowCell.setCellValue(priv.getDepartment());
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(3);// 第4列(私车-用车时间)
						rowCell.setCellValue(priv.getUserCarTime());
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(4);// 第5列(私车-事由)
						rowCell.setCellValue(priv.getReason());
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(5);// 第6列(私车-出发地)
						rowCell.setCellValue(priv.getBeginAddress());
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(6);// 第7列(私车-途径地)
						rowCell.setCellValue(newPass);
						rowCell.setCellStyle(style);
	
						rowCell = row.createCell(7);// 第8列(私车-目的地)
						rowCell.setCellValue(priv.getDestination());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(8);// 第9列(私车-状态)
						rowCell.setCellValue(priv.getStatus());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(9);// 第10列(私车-单程里程)
						rowCell.setCellValue(priv.getSingleLength());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(10);// 第11列(私车-计价里程)
						rowCell.setCellValue(priv.getCountLength());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(11);// 第12列(私车-计价里程)
						rowCell.setCellValue(priv.getSureLength());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(12);// 第13列(私车-申请人)
						rowCell.setCellValue(priv.getApplyMan());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(13);// 第14列(私车-审批人)
						rowCell.setCellValue(priv.getApproveMan());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(14);// 第15列(私车-申请时间)
						rowCell.setCellValue(priv.getApplyTime());
						rowCell.setCellStyle(style);
					  	
						rowCell = row.createCell(15);// 第16列(私车-审批时间)
						rowCell.setCellValue(invoice.getPaidTime());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(16);// 第17列(私车-执行状况)
						rowCell.setCellValue(priv.getIfPerform());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(17);// 第18列(私车-执行状况)
						rowCell.setCellValue(invoice.getSum());
						rowCell.setCellStyle(style);
						
						rowCell = row.createCell(18);// 第19列(私车-执行状况)
						rowCell.setCellValue(invoice.getVoucherNum());
						rowCell.setCellStyle(style);
						z++;
					}
					
//					XSSFRow row = sheet.createRow(index);
					// 合并对应行
//					for(int j=0; j < 18; j++){
//						sheet.addMergedRegion(new CellRangeAddress(index, index+hs-1, j, j));//首行、最后一行、首列、最后一列
//					}
					
//					XSSFCell rowCell = row.createCell(0);// 第1列(序号)
//					rowCell.setCellValue(i + 1 + "");
//					rowCell.setCellStyle(style);
					

				}
					//index=index+1;
				}
			//}
			
			// 将文件保存到指定位置
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}




	@Override
	public int updateInvoice(PrivatecarinvoiceEntity privatecarinvoiceEntity) {
		
		
		return privatecarinvoiceMapper.updatePrivateCarinvoice(privatecarinvoiceEntity);
	}




	@Override
	public PrivatecarinvoiceEntity getOneRegister(String applyid) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Map<String,Object> searPrivateCar(PrivateCarEntity privateCarEntity,Integer nowPage,Integer pageSize) {
		Map<String,Object> mainmap = new HashMap<>();
		Integer userid = bUserService.getUserIdByOpenId(privateCarEntity.getOpenId());
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		if(null==privateCarEntity.getApplyMan()){
			privateCarEntity.setApplyMan(user.getCode());
		}
		
		if(privateCarEntity.getApplyMan().equals("1")){
			privateCarEntity.setApplyMan(null);
		}
		
		
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat s = new SimpleDateFormat("yyyy");
		
	    if(privateCarEntity.getBeingTime()==null&&privateCarEntity.getEndTime()==null){
	    	privateCarEntity.setBeingTime(s.format(new Date())+"-01-01");
	    	privateCarEntity.setEndTime(smf.format(new Date()));
	    }
	    
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
		}
		
		if(maxAppGrade==1){//普通员工
			privateCarEntity.setApplyMan(user.getCode());
		}
		
		if(maxAppGrade==2||maxAppGrade==3){
			BDepartmentEntity departmentEntity = bDepartmentService.getDepartmentIdById(departmentIdByUserId.get(0));
			if(departmentEntity!=null){
				privateCarEntity.setDepartment(departmentEntity.getDepartmentname());
			}
		}
		
		if(maxAppGrade==4){
			
		}*/
		List<PrivateCarEntity> listnew = privateMapper.searPrivateCar(privateCarEntity);
		
		/*List<HistoricProcessInstance> historicProcessInstanceList = 
				historyService.createHistoricProcessInstanceQuery().startedBy(privateCarEntity.getOpenId()).list();
		if(historicProcessInstanceList!=null){
			for(HistoricProcessInstance historicProcessInstance:historicProcessInstanceList){
				Date date = historicProcessInstance.getEndTime();
				if(null!=date){
					String id = historicProcessInstance.getBusinessKey();
					id = id.split("\\:")[1];
					for(PrivateCarEntity seCarEntity:searPrivateCar){
						if(seCarEntity.getApplyId().equals(id)){
							PrivateCarEntity privateCar = privateMapper.getPrivateCar(id);
							list.add(privateCar);
						}
					}
				}
				
			}
		}*/
		if(nowPage !=0 && pageSize != 0){
			int start = (nowPage - 1) * pageSize;
			int total = listnew.size();
			List<publicEntity> list_all = new ArrayList<>();
		    if (start + pageSize <= total)
		      list_all.addAll(listnew.subList(start, start + pageSize));
		    else
		      list_all.addAll(listnew.subList(start, total));
		    mainmap.put("list", list_all);
		    mainmap.put("pagesize", listnew.size());
		}else{
			mainmap.put("pagesize", listnew.size());
		}
		
		return mainmap;
	}




	@Override
	public List<Map<String, Object>> countPrivate(String openId) {
		UserEntity userEntity = userMapper.getopenId(openId);
		List<Map<String,Object>> list = new ArrayList<>();
		Double sum = 0d;
		
		Integer length = 0;
		
		if(userEntity!=null){
			String applyman = userEntity.getCode();
			Map<String,Object> map = new HashMap<>();
			Date date = new Date();
			SimpleDateFormat smf = new SimpleDateFormat("yyyy");
			String format = smf.format(date);
			List<PrivatecarinvoiceEntity> applymanPrivatecarinvoice = privatecarinvoiceMapper.applymanPrivatecarinvoice(applyman,format+"%");
			for(PrivatecarinvoiceEntity privatecarinvoiceEntity:applymanPrivatecarinvoice){
				String applyIds = privatecarinvoiceEntity.getApplyIds();
				String[] applyId = applyIds.split(",");
				sum+=Double.parseDouble(privatecarinvoiceEntity.getSum());
				length+=applyId.length;
			}
			map.put("sum", sum);
			map.put("price",length);
			list.add(map);
		}
		return list;
	}




	//获取所有私车申请信息
	@Override
	public List<PrivateCarEntity> allApplyDisplay(int start, int number) {
		return privateMapper.allApplyDisplay(start,number);
	}




	//获取所有申请信息的总数
	@Override
	public int getAllCount() {
		return privateMapper.getAllCount();
	}




	//根据申请id获取相应的执行id
	@Override
	public PrivatecarinvoiceEntity getbyLikeApplyId(String applyId) {
		return privateMapper.getbyLikeApplyId(applyId);
	}




	//条件查询私车申请信息
	@Override
	public List<PrivateCarEntity> allApplySearchDisplay(PrivateCarEntity applyinfo, int start, int number) {
		applyinfo.setStart(start);
		applyinfo.setNumber(number);
		return privateMapper.allApplySearchDisplay(applyinfo);
	}




	//条件查询申请信息数量
	@Override
	public int getAllSearchCount(PrivateCarEntity applyinfo) {
		return privateMapper.getAllSearchCount(applyinfo);
	}




	//财务添加申请信息
	@Override
	public int SaveNew(PrivateCarEntity privateCarEntity) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int flag =0;
		String applyId="";
		String openId=userMapper.getOpenIdByCode(privateCarEntity.getApplyMan());
		String approveOpenId=userMapper.getOpenIdByCode(privateCarEntity.getApproveMan());
		privateCarEntity.setApproveOpenId(approveOpenId);
		privateCarEntity.setOpenId(openId);
		if(privateCarEntity.getIfBefore()==null){
			privateCarEntity.setIfBefore("0");
			privateCarEntity.setBeforeDate("");
		}
		String bDate = privateCarEntity.getApplyTime();
    	String aDate = privateCarEntity.getUserCarTime();
    	if("1".equals(privateCarEntity.getIfBefore())){
    		Date bDate1;
    		Date aDate1;
			try {
				bDate1 = sdf.parse(bDate);
				aDate1 = sdf.parse(aDate);
				String newBDate = sdf.format(bDate1);
				String newADate = sdf.format(aDate1);
				if(newADate.equals(newBDate)){
					privateCarEntity.setIfBefore("0");
		    		privateCarEntity.setBeforeDate("");
	    		}else{
	    			privateCarEntity.setBeforeDate(privateCarEntity.getApplyTime());
	    		}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
		//1、发起申请
		if("待审批".equals(privateCarEntity.getStatus())){
			privateCarEntity.setIfPass("未报销");
			privateCarEntity.setConfirm("0");
			privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
			privateCarEntity.setApproveTime("");
			privateCarEntity.setSubmitTime("");
			privateCarEntity.setPaidTime("");
	    	flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
	    	Map<String,Object> activitiMap = new HashMap<>();
	    	//进入申请流程
	    	if(flag==1){
	    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
	    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
	        		//开启流程
	    			applyId = privateCarEntity.getApplyId()+"";
	    			String objId="privateCar:"+applyId;
	    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
	    			synchronized (objId) {
	    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
					}
	    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
	    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	    			int i=0;
	    			for(;i<bids.size();i++){
	    				if(bids.get(i).equals(applyId))
	    					break;
	    			}
	    			String taskId=tasks.get(i).getId();
	    			Map<String,Object> map = new HashMap<>();
	    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
	    			//完成任务
	    			completeApplyTask(taskId,null,map);
	        	}
	    	}
		}else if("被否决".equals(privateCarEntity.getStatus())){
			privateCarEntity.setStatus("待修改");
			privateCarEntity.setIfPass("未报销");
			privateCarEntity.setConfirm("0");
			privateCarEntity.setSubmitTime("");
			privateCarEntity.setPaidTime("");
			privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
			flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
	    	Map<String,Object> activitiMap = new HashMap<>();
	    	//进入申请流程
	    	if(flag==1){
	    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
	    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
	        		//开启流程
	    			applyId = privateCarEntity.getApplyId()+"";
	    			String objId="privateCar:"+applyId;
	    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
	    			synchronized (objId) {
	    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
					}
	    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
	    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	    			int i=0;
	    			for(;i<bids.size();i++){
	    				if(bids.get(i).equals(applyId))
	    					break;
	    			}
	    			String taskId=tasks.get(i).getId();
	    			Map<String,Object> map = new HashMap<>();
	    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
	    			//完成任务
	    			completeApplyTask(taskId,null,map);
	        	}
	    	}
	    	List<Task> nexttask1 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
			List<String> nextbids1=workflowUtil.getBussinessIdsByTasks(nexttask1);
			int i=0;
			for(;i<nextbids1.size();i++){
				if(nextbids1.get(i).equals(applyId))
					break;
			}
			String taskId=nexttask1.get(i).getId();
	    	workflowUtil.JumpEndActivity(taskId, "privateCarApply",null); 
		}else if("已通过".equals(privateCarEntity.getStatus())){
			//领导审批通过
			//1、未执行
			if("待执行".equals(privateCarEntity.getIfPerform())){
				privateCarEntity.setIfPass("未报销");
				privateCarEntity.setConfirm("0");
				privateCarEntity.setSubmitTime("");
				privateCarEntity.setPaidTime("");
				privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
		    	flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
		    	Map<String,Object> activitiMap = new HashMap<>();
		    	//进入申请流程
		    	if(flag==1){
		    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
		    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
		        		//开启流程
		    			applyId = privateCarEntity.getApplyId()+"";
		    			String objId="privateCar:"+applyId;
		    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
		    			synchronized (objId) {
		    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
						}
		    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
		    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
		    			int i=0;
		    			for(;i<bids.size();i++){
		    				if(bids.get(i).equals(applyId))
		    					break;
		    			}
		    			String taskId=tasks.get(i).getId();
		    			Map<String,Object> map = new HashMap<>();
		    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
		    			//完成任务
		    			completeApplyTask(taskId,null,map);
		        	}
		    	}
		    	
		    	List<Task> nexttask2 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
				List<String> nextbids2=workflowUtil.getBussinessIdsByTasks(nexttask2);
				int i=0;
				for(;i<nextbids2.size();i++){
					if(nextbids2.get(i).equals(applyId))
						break;
				}
				String taskId=nexttask2.get(i).getId();
				Map<String,Object> map0 = new HashMap<>();
				map0.put("privateCarStaff", privateCarEntity.getApplyMan());
				map0.put("result", "true");
				workflowUtil.JumpEndActivity(taskId, "privateCarStaff", map0);
			}else if("已执行".equals(privateCarEntity.getIfPerform())){//已执行
				if("未报销".equals(privateCarEntity.getIfPass())){
					privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
					flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
			    	Map<String,Object> activitiMap = new HashMap<>();
			    	//进入申请流程
			    	if(flag==1){
			    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
			    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
			        		//开启流程
			    			applyId = privateCarEntity.getApplyId()+"";
			    			String objId="privateCar:"+applyId;
			    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
			    			synchronized (objId) {
			    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
							}
			    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
			    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			    			int i=0;
			    			for(;i<bids.size();i++){
			    				if(bids.get(i).equals(applyId))
			    					break;
			    			}
			    			String taskId=tasks.get(i).getId();
			    			Map<String,Object> map = new HashMap<>();
			    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
			    			//完成任务
			    			completeApplyTask(taskId,null,map);
			        	}
			    	}
					
					PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
					try {
						Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
						carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					carinvoice.setStatus("待财务审核");       
					carinvoice.setSureLength("1");
					carinvoice.setApplyIds(privateCarEntity.getApplyId());
					carinvoice.setPaidTime("");
					carinvoice.setVoucherNum("");
					carinvoice.setSum(privateCarEntity.getSum());
					carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
					carinvoice.setApplyMan(privateCarEntity.getApplyMan());
					carinvoice.setApproveMan("");
					int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
					privateCarEntity.setIfSub("0");
					privateCarEntity.setApplyId(applyId);
					privateMapper.updatePrivateCar(privateCarEntity);
					List<Task> nexttask3 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
					List<String> nextbids3=workflowUtil.getBussinessIdsByTasks(nexttask3);
					int i=0;
					for(;i<nextbids3.size();i++){
						if(nextbids3.get(i).equals(applyId))
							break;
					}
					String taskId=nexttask3.get(i).getId();
					workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
			    	//新的
//			    	Map<String,Object> map2 = new HashMap<>();
//			    	map2.put("privateCarReimbursement", privateCarEntity.getApplyMan());
//					map2.put("staffresult", "true");
//					workflowUtil.JumpEndActivity(taskId, "privateCarReimbursement",map2);
				}else if("已报销".equals(privateCarEntity.getIfPass())){
					privateCarEntity.setApplyId(CreateId(privateCarEntity.getDepartment()));//privateCarEntity.getDepartment())
					flag = privateMapper.insertPrivateCar(privateCarEntity);//提交申请
			    	Map<String,Object> activitiMap = new HashMap<>();
			    	//进入申请流程
			    	if(flag==1){
			    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
			    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
			        		//开启流程
			    			applyId = privateCarEntity.getApplyId()+"";
			    			String objId="privateCar:"+applyId;
			    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
			    			synchronized (objId) {
			    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
							}
			    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
			    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			    			int i=0;
			    			for(;i<bids.size();i++){
			    				if(bids.get(i).equals(applyId))
			    					break;
			    			}
			    			String taskId=tasks.get(i).getId();
			    			Map<String,Object> map = new HashMap<>();
			    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
			    			//完成任务
			    			completeApplyTask(taskId,null,map);
			        	}
			    	}
					
					PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
					try {
						Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
						carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					carinvoice.setStatus("已完成");       
					carinvoice.setSureLength("1");
					carinvoice.setApplyIds(privateCarEntity.getApplyId());
					carinvoice.setPaidTime(privateCarEntity.getApproveTime());
					carinvoice.setVoucherNum(privateCarEntity.getVoucherNum());
					carinvoice.setSum(privateCarEntity.getSum());
					carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
					carinvoice.setApplyMan(privateCarEntity.getApplyMan());
					carinvoice.setApproveMan(privateCarEntity.getPaidMan());
					int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
					privateCarEntity.setIfSub("1");
					privateCarEntity.setApplyId(applyId);
					privateMapper.updatePrivateCar(privateCarEntity);
					List<Task> nexttask4 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
					List<String> nextbids4=workflowUtil.getBussinessIdsByTasks(nexttask4);
					int i=0;
					for(;i<nextbids4.size();i++){
						if(nextbids4.get(i).equals(applyId))
							break;
					}
					String taskId=nexttask4.get(i).getId();
			    	workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
			    	List<Task> nexttask5 = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
			    	List<String> nextbids5=workflowUtil.getBussinessIdsByTasks(nexttask5);
					int j=0;
					for(;j<nextbids5.size();j++){
						if(nextbids5.get(j).equals(applyId))
							break;
					}
					String taskId1=nexttask5.get(j).getId();
					Map<String,Object> map1 = new HashMap<>();
					map1.put("finaceresult", "true");
					completeApplyTask(taskId1, "", map1);
				}
			}
			
	    	
		}
		return flag;
	}


	//根据报告单号删除报告单
	@Override
	public void deleteByApplyid(String applyId) {
		privatecarinvoiceMapper.deleteByApplyid(applyId);
	}

	//根据申请编号删除私车申请信息
	@Override
	public Integer deleteByApplyId(String applyId) {
		return privateMapper.deleteByApplyId(applyId);
	}




	//财务修改私车申请信息
	@Override
	public Object updateNew(PrivateCarEntity privateCarEntity) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String openId=userMapper.getOpenIdByCode(privateCarEntity.getApplyMan());
		String approveOpenId=userMapper.getOpenIdByCode(privateCarEntity.getApproveMan());
		privateCarEntity.setApproveOpenId(approveOpenId);
		privateCarEntity.setOpenId(openId);
		int flag =0;
		if(privateCarEntity.getIfBefore()==null){
			privateCarEntity.setIfBefore("0");
			privateCarEntity.setBeforeDate("");
		}
		String bDate = privateCarEntity.getApplyTime();
    	String aDate = privateCarEntity.getUserCarTime();
    	if("1".equals(privateCarEntity.getIfBefore())){
    		Date bDate1;
    		Date aDate1;
			try {
				bDate1 = sdf.parse(bDate);
				aDate1 = sdf.parse(aDate);
				String newBDate = sdf.format(bDate1);
				String newADate = sdf.format(aDate1);
				if(newADate.equals(newBDate)){
					privateCarEntity.setIfBefore("0");
		    		privateCarEntity.setBeforeDate("");
	    		}else{
	    			privateCarEntity.setBeforeDate(privateCarEntity.getApplyTime());
	    		}
			} catch (ParseException e) {
				e.printStackTrace();
			}
    	}
		 //根据applyid获取申请信息
		 PrivateCarEntity privateCar = privateMapper.getPrivateCar(privateCarEntity.getApplyId());
		 //1、之前是待审批状态
		 if("待审批".equals(privateCar.getStatus())){
			 //1、1修改后状态不变
			 if("待审批".equals(privateCarEntity.getStatus())){
				 privateCarEntity.setIfPass("未报销");
					privateCarEntity.setConfirm("0");
					privateCarEntity.setApproveTime("");
					privateCarEntity.setSubmitTime("");
					privateCarEntity.setPaidTime("");
					flag =privateMapper.updatePrivateCar(privateCarEntity);
			 }
			 //1、2修改后变为被否决
			 if("被否决".equals(privateCarEntity.getStatus())){
				privateCarEntity.setStatus("待修改");
				privateCarEntity.setIfPass("未报销");
				privateCarEntity.setConfirm("0");
				privateCarEntity.setSubmitTime("");
				privateCarEntity.setPaidTime("");
				flag =privateMapper.updatePrivateCar(privateCarEntity);
				//将流程变为已审批
				List<Task> nexttask1 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
				List<String> nextbids1=workflowUtil.getBussinessIdsByTasks(nexttask1);
				int i=0;
				for(;i<nextbids1.size();i++){
					if(nextbids1.get(i).equals(privateCarEntity.getApplyId()))
						break;
				}
				String taskId=nexttask1.get(i).getId();
		    	workflowUtil.JumpEndActivity(taskId, "privateCarApply",null);
			 }
			 //1、3修改后变为已通过
			 if("已通过".equals(privateCarEntity.getStatus())){
				 //1、3、1未执行
				 if("待执行".equals(privateCarEntity.getIfPerform())){
					privateCarEntity.setIfPass("未报销");
					privateCarEntity.setConfirm("0");
					privateCarEntity.setSubmitTime("");
					privateCarEntity.setPaidTime("");
					flag =privateMapper.updatePrivateCar(privateCarEntity);
			    	List<Task> nexttask2 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
					List<String> nextbids2=workflowUtil.getBussinessIdsByTasks(nexttask2);
					int i=0;
					for(;i<nextbids2.size();i++){
						if(nextbids2.get(i).equals(privateCarEntity.getApplyId()))
							break;
					}
					String taskId=nexttask2.get(i).getId();
					Map<String,Object> map0 = new HashMap<>();
					map0.put("privateCarStaff", privateCarEntity.getApplyMan());
					map0.put("result", "true");
					workflowUtil.JumpEndActivity(taskId, "privateCarStaff", map0);
				 }else if("已执行".equals(privateCarEntity.getIfPerform())){
					 //1、3、2已执行
					 if("未报销".equals(privateCarEntity.getIfPass())){
						 flag = privateMapper.updatePrivateCar(privateCarEntity);
						PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
						try {
							Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
							carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						carinvoice.setStatus("待财务审核");       
						carinvoice.setSureLength("1");
						carinvoice.setApplyIds(privateCarEntity.getApplyId());
						carinvoice.setPaidTime("");
						carinvoice.setVoucherNum("");
						carinvoice.setSum(privateCarEntity.getSum());
						carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
						carinvoice.setApplyMan(privateCarEntity.getApplyMan());
						carinvoice.setApproveMan("");
						int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
						privateCarEntity.setIfSub("0");
						privateCarEntity.setApplyId(privateCarEntity.getApplyId());
						privateMapper.updatePrivateCar(privateCarEntity);
						List<Task> nexttask3 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
						List<String> nextbids3=workflowUtil.getBussinessIdsByTasks(nexttask3);
						int i=0;
						for(;i<nextbids3.size();i++){
							if(nextbids3.get(i).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId=nexttask3.get(i).getId();
						workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
						}else if("已报销".equals(privateCarEntity.getIfPass())){
						flag =privateMapper.updatePrivateCar(privateCarEntity);
						PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
						try {
							Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
							carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						carinvoice.setStatus("已完成");       
						carinvoice.setSureLength("1");
						carinvoice.setApplyIds(privateCarEntity.getApplyId());
						carinvoice.setPaidTime(privateCarEntity.getApproveTime());
						carinvoice.setVoucherNum(privateCarEntity.getVoucherNum());
						carinvoice.setSum(privateCarEntity.getSum());
						carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
						carinvoice.setApplyMan(privateCarEntity.getApplyMan());
						carinvoice.setApproveMan(privateCarEntity.getPaidMan());
						int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
						privateCarEntity.setIfSub("1");
						privateCarEntity.setApplyId(privateCarEntity.getApplyId());
						privateMapper.updatePrivateCar(privateCarEntity);
						List<Task> nexttask4 = workflowUtil.getTaskByIds("privateCar","privateCarApprove");
						List<String> nextbids4=workflowUtil.getBussinessIdsByTasks(nexttask4);
						int i=0;
						for(;i<nextbids4.size();i++){
							if(nextbids4.get(i).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId=nexttask4.get(i).getId();
				    	workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
				    	List<Task> nexttask5 = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
				    	List<String> nextbids5=workflowUtil.getBussinessIdsByTasks(nexttask5);
						int j=0;
						for(;j<nextbids5.size();j++){
							if(nextbids5.get(j).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId1=nexttask5.get(j).getId();
						Map<String,Object> map1 = new HashMap<>();
						map1.put("finaceresult", "true");
						completeApplyTask(taskId1, "", map1);
					}
				 }
		 }
		 }else if("待修改".equals(privateCar.getStatus())){//2、之前是被否决状态
			 if("被否决".equals(privateCarEntity.getStatus())){
				 flag =privateMapper.updatePrivateCar(privateCarEntity);
			 }
			 //2、1修改后是待审批
			 if("待审批".equals(privateCarEntity.getStatus())){
				privateCarEntity.setConfirm("0");
				privateCarEntity.setApproveTime("");
				privateCarEntity.setSubmitTime("");
				privateCarEntity.setPaidTime("");
				flag =privateMapper.updatePrivateCar(privateCarEntity);
				//开启流程
	    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
//	    			Map<String,Object> activitiMap = new HashMap<>();
//	    			activitiMap.put("privateCarApply", privateCarEntity.getApplyMan());
//	        		//开启流程
//	    			applyId = privateCarEntity.getApplyId()+"";
//	    			String objId="privateCar:"+applyId;
//	    			identityService.setAuthenticatedUserId(privateCarEntity.getApplyMan());
//	    			synchronized (objId) {
//	    				runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
//					}
	    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar","privateCarApply");
	    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	    			int i=0;
	    			for(;i<bids.size();i++){
	    				if(bids.get(i).equals(privateCarEntity.getApplyId()))
	    					break;
	    			}
	    			String taskId=tasks.get(i).getId();
	    			Map<String,Object> map = new HashMap<>();
	    			map.put("privateCarApprove", privateCarEntity.getApproveMan());
	    			//完成任务
	    			completeApplyTask(taskId,null,map);
	        	}
			 }
			 //2、2修改后变为已通过
			 if("已通过".equals(privateCarEntity.getStatus())){
				 if("待执行".equals(privateCarEntity.getIfPerform())){
					privateCarEntity.setIfPass("未报销");
					privateCarEntity.setConfirm("0");
					privateCarEntity.setSubmitTime("");
					privateCarEntity.setPaidTime("");
					flag =privateMapper.updatePrivateCar(privateCarEntity);
			    	List<Task> nexttask2 = workflowUtil.getTaskByIds("privateCar","privateCarApply");
					List<String> nextbids2=workflowUtil.getBussinessIdsByTasks(nexttask2);
					int i=0;
					for(;i<nextbids2.size();i++){
						if(nextbids2.get(i).equals(privateCarEntity.getApplyId()))
							break;
					}
					String taskId=nexttask2.get(i).getId();
					Map<String,Object> map0 = new HashMap<>();
					map0.put("privateCarStaff", privateCarEntity.getApplyMan());
					map0.put("result", "true");
					workflowUtil.JumpEndActivity(taskId, "privateCarStaff", map0);
				 }else if("已执行".equals(privateCarEntity.getIfPerform())){
					 if("未报销".equals(privateCarEntity.getIfPass())){
						 flag = privateMapper.updatePrivateCar(privateCarEntity);
						PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
						try {
							Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
							carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						carinvoice.setStatus("待财务审核");       
						carinvoice.setSureLength("1");
						carinvoice.setApplyIds(privateCarEntity.getApplyId());
						carinvoice.setPaidTime("");
						carinvoice.setVoucherNum("");
						carinvoice.setSum(privateCarEntity.getSum());
						carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
						carinvoice.setApplyMan(privateCarEntity.getApplyMan());
						carinvoice.setApproveMan("");
						int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
						privateCarEntity.setIfSub("0");
						privateCarEntity.setApplyId(privateCarEntity.getApplyId());
						privateMapper.updatePrivateCar(privateCarEntity);
						List<Task> nexttask3 = workflowUtil.getTaskByIds("privateCar","privateCarApply");
						List<String> nextbids3=workflowUtil.getBussinessIdsByTasks(nexttask3);
						int i=0;
						for(;i<nextbids3.size();i++){
							if(nextbids3.get(i).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId=nexttask3.get(i).getId();
						workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
						}else if("已报销".equals(privateCarEntity.getIfPass())){
						flag =privateMapper.updatePrivateCar(privateCarEntity);
						PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
						try {
							Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
							carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						carinvoice.setStatus("已完成");       
						carinvoice.setSureLength("1");
						carinvoice.setApplyIds(privateCarEntity.getApplyId());
						carinvoice.setPaidTime(privateCarEntity.getApproveTime());
						carinvoice.setVoucherNum(privateCarEntity.getVoucherNum());
						carinvoice.setSum(privateCarEntity.getSum());
						carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
						carinvoice.setApplyMan(privateCarEntity.getApplyMan());
						carinvoice.setApproveMan(privateCarEntity.getPaidMan());
						int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
						privateCarEntity.setIfSub("1");
						privateCarEntity.setApplyId(privateCarEntity.getApplyId());
						privateMapper.updatePrivateCar(privateCarEntity);
						List<Task> nexttask4 = workflowUtil.getTaskByIds("privateCar","privateCarApply");
						List<String> nextbids4=workflowUtil.getBussinessIdsByTasks(nexttask4);
						int i=0;
						for(;i<nextbids4.size();i++){
							if(nextbids4.get(i).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId=nexttask4.get(i).getId();
				    	workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
				    	List<Task> nexttask5 = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
				    	List<String> nextbids5=workflowUtil.getBussinessIdsByTasks(nexttask5);
						int j=0;
						for(;j<nextbids5.size();j++){
							if(nextbids5.get(j).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId1=nexttask5.get(j).getId();
						Map<String,Object> map1 = new HashMap<>();
						map1.put("finaceresult", "true");
						completeApplyTask(taskId1, "", map1);
					}
				 }
			 }
		 }else if("已通过".equals(privateCar.getStatus())){//3、之前状态是已通过
			 if("待执行".equals(privateCar.getIfPerform())){//之前是未执行
				//3、1修改后是待审批
				 if("待审批".equals(privateCarEntity.getStatus())){
					privateCarEntity.setConfirm("0");
					privateCarEntity.setApproveTime("");
					privateCarEntity.setSubmitTime("");
					privateCarEntity.setPaidTime("");
					privateCarEntity.setIfPerform("未执行");
					flag =privateMapper.updatePrivateCar(privateCarEntity);
					
		    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
		    			List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
				    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
						int j=0;
						for(;j<nextbids.size();j++){
							if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId1=nexttask.get(j).getId();
						workflowUtil.JumpEndActivity(taskId1, "privateCarApprove",null); 
		        	}
				 }else if("被否决".equals(privateCarEntity.getStatus())){//修改后是被否决
					privateCarEntity.setConfirm("0");
					privateCarEntity.setSubmitTime("");
					privateCarEntity.setPaidTime("");
					privateCarEntity.setIfPerform("未执行");
					flag =privateMapper.updatePrivateCar(privateCarEntity);
					List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
			    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
					int j=0;
					for(;j<nextbids.size();j++){
						if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
							break;
					}
					String taskId1=nexttask.get(j).getId();
					workflowUtil.JumpEndActivity(taskId1, "privateCarApply",null); 
				 }else if("已通过".equals(privateCarEntity.getStatus())){
					 if("待执行".equals(privateCarEntity.getIfPerform())){//修改后还是待执行
						 flag =privateMapper.updatePrivateCar(privateCarEntity);
					 }else if("已执行".equals(privateCarEntity.getIfPerform())){
						 if("未报销".equals(privateCarEntity.getIfPass())){//修改后是未报销
							flag = privateMapper.updatePrivateCar(privateCarEntity);
							PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
							try {
								Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
								carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							carinvoice.setStatus("待财务审核");       
							carinvoice.setSureLength("1");
							carinvoice.setApplyIds(privateCarEntity.getApplyId());
							carinvoice.setPaidTime("");
							carinvoice.setVoucherNum("");
							carinvoice.setSum(privateCarEntity.getSum());
							carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
							carinvoice.setApplyMan(privateCarEntity.getApplyMan());
							carinvoice.setApproveMan("");
							int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
							privateCarEntity.setIfSub("0");
							privateCarEntity.setApplyId(privateCarEntity.getApplyId());
							privateMapper.updatePrivateCar(privateCarEntity);
							List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
					    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
							int j=0;
							for(;j<nextbids.size();j++){
								if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
									break;
							}
							String taskId1=nexttask.get(j).getId();
							workflowUtil.JumpEndActivity(taskId1, "privateCarFinance",null); 
						 }else if("已报销".equals(privateCarEntity.getIfPass())){
							flag =privateMapper.updatePrivateCar(privateCarEntity);
							PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
							try {
								Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
								carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							
							carinvoice.setStatus("已完成");       
							carinvoice.setSureLength("1");
							carinvoice.setApplyIds(privateCarEntity.getApplyId());
							carinvoice.setPaidTime(privateCarEntity.getApproveTime());
							carinvoice.setVoucherNum(privateCarEntity.getVoucherNum());
							carinvoice.setSum(privateCarEntity.getSum());
							carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
							carinvoice.setApplyMan(privateCarEntity.getApplyMan());
							carinvoice.setApproveMan(privateCarEntity.getPaidMan());
							int result = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
							privateCarEntity.setIfSub("1");
							privateCarEntity.setApplyId(privateCarEntity.getApplyId());
							privateMapper.updatePrivateCar(privateCarEntity);
							List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
					    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
							int j=0;
							for(;j<nextbids.size();j++){
								if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
									break;
							}
							String taskId1=nexttask.get(j).getId();
							workflowUtil.JumpEndActivity(taskId1, "privateCarFinance",null); 
					    	List<Task> nexttask5 = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
					    	List<String> nextbids5=workflowUtil.getBussinessIdsByTasks(nexttask5);
							int i=0;
							for(;i<nextbids5.size();i++){
								if(nextbids5.get(i).equals(privateCarEntity.getApplyId()))
									break;
							}
							String taskId=nexttask5.get(i).getId();
							Map<String,Object> map1 = new HashMap<>();
							map1.put("finaceresult", "true");
							completeApplyTask(taskId, "", map1);
						 }
					 }
				 }
			 }else if("已执行".equals(privateCar.getIfPerform())){//之前是已执行
				 if("未报销".equals(privateCar.getIfPass())){//之前是未报销
					 //修改后变为待审批
					 if("待审批".equals(privateCarEntity.getStatus())){
						privateCarEntity.setConfirm("0");
						privateCarEntity.setApproveTime("");
						privateCarEntity.setSubmitTime("");
						privateCarEntity.setPaidTime("");
						privateCarEntity.setIfPerform("未执行");
						flag =privateMapper.updatePrivateCar(privateCarEntity);
							
			    		if(privateCarEntity.getApplyMan()!=null&&!"".equals(privateCarEntity.getApplyMan())){
			    			List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
					    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
							int j=0;
							for(;j<nextbids.size();j++){
								if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
									break;
							}
							String taskId1=nexttask.get(j).getId();
							workflowUtil.JumpEndActivity(taskId1, "privateCarApprove",null); 
			        	} 
			    		//修改该申请对应的报销信息
			    		PrivatecarinvoiceEntity getbyLikeApplyId = privateMapper.getbyLikeApplyId("%"+privateCarEntity.getApplyId()+"%");
			    		if(getbyLikeApplyId!=null){
			    			if(getbyLikeApplyId.getApplyIds().split(",").length==1){
			    				deleteByApplyid(getbyLikeApplyId.getApplyId());
			  			  }else if(getbyLikeApplyId.getApplyIds().split(",").length>1){
			  				  String applyIds="";
			  				  if(getbyLikeApplyId.getApplyIds().split(",")[0].equals(privateCarEntity.getApplyId())){
			  					  applyIds=getbyLikeApplyId.getApplyIds().replace(privateCarEntity.getApplyId()+",","");
			  				  }else{
			  					  applyIds=getbyLikeApplyId.getApplyIds().replace(","+privateCarEntity.getApplyId(),"");
			  				  }
			  				getbyLikeApplyId.setApplyIds(applyIds);
			  				getbyLikeApplyId.setSum(String.valueOf(Double.parseDouble(getbyLikeApplyId.getSum())-privateCarEntity.getSureLength()));
			  				updateInvoice(getbyLikeApplyId);
			  			  }
			    		}
					 }else if("被否决".equals(privateCarEntity.getStatus())){//修改后是被否决
						privateCarEntity.setConfirm("0");
						privateCarEntity.setSubmitTime("");
						privateCarEntity.setPaidTime("");
						privateCarEntity.setIfPerform("未执行");
						flag =privateMapper.updatePrivateCar(privateCarEntity);
						List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
				    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
						int j=0;
						for(;j<nextbids.size();j++){
							if(nextbids.get(j).equals(privateCarEntity.getApplyId()))
								break;
						}
						String taskId1=nexttask.get(j).getId();
						workflowUtil.JumpEndActivity(taskId1, "privateCarApply",null); 
						//修改申请信息对应的报销信息
						PrivatecarinvoiceEntity getbyLikeApplyId = privateMapper.getbyLikeApplyId("%"+privateCarEntity.getApplyId()+"%");
			    		if(getbyLikeApplyId!=null){
			    			if(getbyLikeApplyId.getApplyIds().split(",").length==1){
			    				deleteByApplyid(getbyLikeApplyId.getApplyId());
			  			  }else if(getbyLikeApplyId.getApplyIds().split(",").length>1){
			  				  String applyIds="";
			  				  if(getbyLikeApplyId.getApplyIds().split(",")[0].equals(privateCarEntity.getApplyId())){
			  					  applyIds=getbyLikeApplyId.getApplyIds().replace(privateCarEntity.getApplyId()+",","");
			  				  }else{
			  					  applyIds=getbyLikeApplyId.getApplyIds().replace(","+privateCarEntity.getApplyId(),"");
			  				  }
			  				getbyLikeApplyId.setSureLength(getbyLikeApplyId.getApplyIds().split(",").length+"");
			  				getbyLikeApplyId.setApplyIds(applyIds);
			  				getbyLikeApplyId.setSum(String.valueOf(Double.parseDouble(getbyLikeApplyId.getSum())-privateCarEntity.getSureLength()));
			  				updateInvoice(getbyLikeApplyId);
			  			  }
			    		}
					 }else if("已通过".equals(privateCarEntity.getStatus())){//修改后是已通过状态
						 if("未报销".equals(privateCarEntity.getIfPass())){//修改后是未报销
							 flag =privateMapper.updatePrivateCar(privateCarEntity);
						 }else if("已报销".equals(privateCarEntity.getIfPass())){//修改后是已报销
							 privateCarEntity.setIfSub("1");
							flag =privateMapper.updatePrivateCar(privateCarEntity);
							PrivatecarinvoiceEntity carinvoice = privateMapper.getbyLikeApplyId("%"+privateCarEntity.getApplyId()+"%");
							if(carinvoice!=null){
				    			if(carinvoice.getApplyIds().split(",").length==1){
				    				carinvoice.setStatus("已完成");       
									carinvoice.setSureLength(carinvoice.getApplyIds().split(",").length+"");
									carinvoice.setPaidTime(privateCarEntity.getApproveTime());
									carinvoice.setVoucherNum(privateCarEntity.getVoucherNum());
									carinvoice.setSum(privateCarEntity.getSum());
									carinvoice.setApproveMan(privateCarEntity.getPaidMan());
									carinvoice.setApplyTime(privateCarEntity.getSubmitTime());
									carinvoice.setApplyMan(privateCarEntity.getApplyMan());
									updateInvoice(carinvoice);
				  			  }else if(carinvoice.getApplyIds().split(",").length>1){
				  				  String applyIds="";
				  				  if(carinvoice.getApplyIds().split(",")[0].equals(privateCarEntity.getApplyId())){
				  					  applyIds=carinvoice.getApplyIds().replace(privateCarEntity.getApplyId()+",","");
				  				  }else{
				  					  applyIds=carinvoice.getApplyIds().replace(","+privateCarEntity.getApplyId(),"");
				  				  }
				  				carinvoice.setSureLength(carinvoice.getApplyIds().split(",").length+"");
				  				carinvoice.setApplyIds(applyIds);
				  				carinvoice.setSum(String.valueOf(Double.parseDouble(carinvoice.getSum())-privateCarEntity.getSureLength()));
				  				updateInvoice(carinvoice);
				  				PrivatecarinvoiceEntity newprivate = new PrivatecarinvoiceEntity();
				  				
				  				try {
									Date applydate=sdf1.parse(privateCarEntity.getSubmitTime());
									newprivate.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
								} catch (ParseException e) {
									e.printStackTrace();
								}
								
				  				newprivate.setStatus("已完成");       
				  				newprivate.setSureLength("1");
				  				newprivate.setApplyIds(privateCarEntity.getApplyId());
				  				newprivate.setPaidTime(privateCarEntity.getApproveTime());
				  				newprivate.setVoucherNum(privateCarEntity.getVoucherNum());
				  				newprivate.setSum(privateCarEntity.getSum());
				  				newprivate.setApplyTime(privateCarEntity.getSubmitTime());
				  				newprivate.setApplyMan(privateCarEntity.getApplyMan());
				  				newprivate.setApproveMan(privateCarEntity.getPaidMan());
								int result = privatecarinvoiceMapper.insertPrivateCarinvoice(newprivate);
				  			  }
							
						 }
					    	List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
					    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
							int i=0;
							for(;i<nextbids.size();i++){
								if(nextbids.get(i).equals(privateCarEntity.getApplyId()))
									break;
							}
							String taskId=nexttask.get(i).getId();
							Map<String,Object> map1 = new HashMap<>();
							map1.put("finaceresult", "true");
							completeApplyTask(taskId, "", map1);
						}
					 }
				 }else if("已报销".equals(privateCar.getIfPass())){//修改之前是已报销，说明流程已结束
					 //流程结束修改一条数据，只修改申请信息的基本信息以及该申请信息对应的报销信息
					 PrivatecarinvoiceEntity getbyLikeApplyId = privateMapper.getbyLikeApplyId("%"+privateCarEntity.getApplyId()+"%");
					 getbyLikeApplyId.setApplyTime(privateCarEntity.getSubmitTime());
					 getbyLikeApplyId.setPaidTime(privateCarEntity.getPaidTime());
					 getbyLikeApplyId.setApproveMan(privateCarEntity.getPaidMan());
					 getbyLikeApplyId.setVoucherNum(privateCarEntity.getVoucherNum());
					 PrivateCarEntity privateCar2 = privateMapper.getPrivateCar(privateCarEntity.getApplyId());
					 getbyLikeApplyId.setSum(String.valueOf(Double.parseDouble(getbyLikeApplyId.getSum())-privateCar2.getSureLength()+privateCarEntity.getSureLength()));
					 privatecarinvoiceMapper.updatePrivateCarinvoice(getbyLikeApplyId);
					 privateCarEntity.setApplyTime(privateCarEntity.getApplyTime());
					 privateCarEntity.setApproveTime(privateCarEntity.getApproveTime());
					 privateCarEntity.setBeginAddress(privateCarEntity.getBeginAddress());
					 privateCarEntity.setCountLength(privateCarEntity.getCountLength());
					 privateCarEntity.setDepartment(privateCarEntity.getDepartment());
					 privateCarEntity.setDestination(privateCarEntity.getDestination());
					 privateCarEntity.setDoubleLength(privateCarEntity.getDoubleLength());
					 privateCarEntity.setEndLength(privateCarEntity.getEndLength());
					 privateCarEntity.setPassAddress(privateCarEntity.getPassAddress());
					 privateCarEntity.setWayModel(privateCarEntity.getWayModel());
					 privateCarEntity.setWayDetail(privateCarEntity.getWayDetail());
					 privateCarEntity.setUserCarTime(privateCarEntity.getUserCarTime());
					 privateCarEntity.setSureLength(privateCarEntity.getSureLength());
					 privateCarEntity.setSingleLength(privateCarEntity.getSingleLength());
					 privateCarEntity.setReason(privateCarEntity.getReason());
					 flag =privateMapper.updatePrivateCar(privateCarEntity);
				 }
			 }
			 
		 }
		 return flag;
	}




	//根据业务id获取进行中的业务信息
	@Override
	public List<PrivateCarEntity> findTasksByApplyId(Set<String> keySet) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return privateMapper.findTasksByApplyId(list);
	}




	 //批量事后登记
	@Override
	public Object perform(String applyIds, String subtime, String sum,String applyman) {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int flag =0;
		PrivatecarinvoiceEntity privatecarinvoice =privateMapper.getbyLikeApplyId("%"+applyIds+"%");
		if(privatecarinvoice==null){
			PrivatecarinvoiceEntity carinvoice =new PrivatecarinvoiceEntity();
			 try {
				Date applydate=sdf1.parse(subtime);
				carinvoice.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			carinvoice.setStatus("待财务审核");       
			carinvoice.setSureLength("1");
			carinvoice.setApplyIds(applyIds);
			carinvoice.setPaidTime("");
			carinvoice.setVoucherNum("");
			carinvoice.setSum(sum);
			carinvoice.setApplyTime(subtime);
			carinvoice.setApplyMan(applyman);
			carinvoice.setApproveMan("");
			flag = privatecarinvoiceMapper.insertPrivateCarinvoice(carinvoice);
			String[] split = applyIds.split(",");
			for(int i=0;i<split.length;i++){
				PrivateCarEntity privateCar = privateMapper.getPrivateCar(split[i]);
				privateCar.setIfSub("0");
				privateMapper.updatePrivateCar(privateCar);
				List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
				List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
				int j=0;
				for(;j<nextbids.size();j++){
					if(nextbids.get(j).equals(split[i]))
						break;
				}
				String taskId=nexttask.get(j).getId();
				workflowUtil.JumpEndActivity(taskId, "privateCarFinance",null); 
			}
		}else{
			privatecarinvoice.setApplyTime(subtime);
			privatecarinvoice.setSum(sum);
			privatecarinvoiceMapper.updatePrivateCarinvoice(privatecarinvoice);
		}
		 
		return flag;
	}

	 //批量报销
	@Override
	public Object reimbursement(String applyIds, String registtime, String sum, String registman, String vouchernum,String subtime,String applyman) {
		int flag =0;
		PrivatecarinvoiceEntity carinvoice = privateMapper.getbyLikeApplyId(applyIds);
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String[] split = applyIds.split(",");
		if(carinvoice!=null){
			if(carinvoice.getPaidTime()==null||"".equals(carinvoice.getPaidTime())){
				carinvoice.setStatus("已完成");       
				carinvoice.setSureLength(split.length+"");
				carinvoice.setPaidTime(registtime);
				carinvoice.setVoucherNum(vouchernum);
				carinvoice.setSum(sum);
				carinvoice.setApproveMan(registman);
				flag = privatecarinvoiceMapper.updatePrivateCarinvoice(carinvoice);
				for(int i=0;i<split.length;i++){
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(split[i]);
					privateCar.setIfSub("1");
					privateCar.setIfPerform("已执行");
					privateCar.setIfPass("已报销");
					privateMapper.updatePrivateCar(privateCar);
					List<Task> nexttask1 = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
			    	List<String> nextbids1=workflowUtil.getBussinessIdsByTasks(nexttask1);
					int n=0;
					for(;n<nextbids1.size();n++){
						if(nextbids1.get(n).equals(split[i]))
							break;
					}
					String taskId2=nexttask1.get(n).getId();
					workflowUtil.JumpEndActivity(taskId2, "privateCarFinance",null); 
					List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
			    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
					int j=0;
					for(;j<nextbids.size();j++){
						if(nextbids.get(j).equals(split[i]))
							break;
					}
					String taskId1=nexttask.get(j).getId();
					Map<String,Object> map1 = new HashMap<>();
					map1.put("finaceresult", "true");
					completeApplyTask(taskId1, "", map1);
				}
			}else{
				carinvoice.setStatus("已完成");       
				carinvoice.setSureLength(split.length+"");
				carinvoice.setPaidTime(registtime);
				carinvoice.setVoucherNum(vouchernum);
				carinvoice.setSum(sum);
				carinvoice.setApproveMan(registman);
				flag = privatecarinvoiceMapper.updatePrivateCarinvoice(carinvoice);
				for(int i=0;i<split.length;i++){
					PrivateCarEntity privateCar = privateMapper.getPrivateCar(split[i]);
					privateCar.setIfSub("1");
					privateCar.setIfPerform("已执行");
					privateCar.setIfPass("已报销");
					privateMapper.updatePrivateCar(privateCar);
				}
			}
			
		}else{
			PrivatecarinvoiceEntity privatecar = new PrivatecarinvoiceEntity();
			try {
				Date applydate=sdf1.parse(subtime);
				privatecar.setApplyId(new SimpleDateFormat("yyyyMMddHHmmssSSS") .format(applydate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			privatecar.setStatus("已完成");       
			privatecar.setSureLength(split.length+"");
			privatecar.setApplyIds(applyIds);
			privatecar.setPaidTime(registtime);
			privatecar.setVoucherNum(vouchernum);
			privatecar.setSum(sum);
			privatecar.setApplyTime(subtime);
			privatecar.setApplyMan(applyman);
			privatecar.setApproveMan(registman);
			int result = privatecarinvoiceMapper.insertPrivateCarinvoice(privatecar);
			
			for(int i=0;i<split.length;i++){
				PrivateCarEntity privateCar2 = privateMapper.getPrivateCar(split[i]);
				privateCar2.setIfSub("1");
				privateCar2.setIfPerform("已执行");
				privateCar2.setIfPass("已报销");
				privateMapper.updatePrivateCar(privateCar2);
				List<Task> nexttask = workflowUtil.getTaskByIds("privateCar","privateCarStaff");
		    	List<String> nextbids=workflowUtil.getBussinessIdsByTasks(nexttask);
				int j=0;
				for(;j<nextbids.size();j++){
					if(nextbids.get(j).equals(split[i]))
						break;
				}
				String taskId1=nexttask.get(j).getId();
				workflowUtil.JumpEndActivity(taskId1, "privateCarFinance",null); 
		    	List<Task> nexttask5 = workflowUtil.getTaskByIds("privateCar","privateCarFinance");
		    	List<String> nextbids5=workflowUtil.getBussinessIdsByTasks(nexttask5);
				int m=0;
				for(;m<nextbids5.size();m++){
					if(nextbids5.get(m).equals(split[i]))
						break;
				}
				String taskId=nexttask5.get(m).getId();
				Map<String,Object> map1 = new HashMap<>();
				map1.put("finaceresult", "true");
				completeApplyTask(taskId, "", map1);
			}
			
			
		}
		
		
		return flag;
	}







	



//	@Override
//	public int updateInvoice(PrivatecarinvoiceEntity privatecarinvoiceEntity) {
//		
//		
//		return privatecarinvoiceMapper.updatePrivateCarinvoice(privatecarinvoiceEntity);
//	}




//	@Override
//	public PrivatecarinvoiceEntity getOneRegister(String applyid) {
//		
//		return privatecarinvoiceMapper.getApplyIdPrivatecarinvoiceEntity(applyid);
//	}




	



	




	
}
