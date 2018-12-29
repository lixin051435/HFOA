package com.hfoa.controller.leaver;

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
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.hfoa.common.AnccResult;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.leaver.LearTimeService;
import com.hfoa.service.leaver.YearLearService;
import com.hfoa.service.user.DepartmentService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.WorkflowUtil;

import net.sf.ehcache.constructs.nonstop.ThrowTimeoutException;


/**
 *年假
 */
@Controller
@RequestMapping("/leavel")
public class YearLeavelCotroller {
	
	@Autowired
	private YearLearService yearLearService;
	
	@Autowired 
	private LearTimeService learTimeService;
	
	@Autowired
	private USerService usSerService;
	
	@Autowired
	private DepartmentService departmnetService;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private WorkflowUtil workflowUtil;

	@Autowired
	private TaskService taskService;
	
	
	
	
	//部署流程实例
			private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			
			@RequestMapping("/createleaver")
			@ResponseBody
			public String createProcess(){
				String src = "";
				Deployment deployment = processEngine.getRepositoryService()
								.createDeployment()
								.name("年假管理")
								.addClasspathResource("activiti/leaverYear.bpmn")
								.addClasspathResource("activiti/leaverYear.png")
								.deploy();
				System.out.println("部署ID是"+deployment.getId());
				System.out.println("部署名称是"+deployment.getName());
				return "部署成功";
			}
	
	
	
	
	/**
	 * 提交年假计划
	 * @param learYear
	 * @return
	 * @throws ParseException 
	 */
	@Transactional
	@RequestMapping(value="/insertLeavel",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult addLeavel(String openId,String formId,LearYear learYear,String beginTime,String endTime,String offdays,HttpServletRequest request) throws ParseException{
		System.out.println("推送id是"+learYear.getApproveManOpenId());
		String pc = learYear.getFrequency();
		learYear.setOpenId(openId);
		System.out.println("opendi是"+openId);
		String Id = UUID.randomUUID().toString();
		learYear.setId(Id);
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		learYear.setSfyc("0");
		learYear.setApplyTime(smf.format(date));
		int flag = yearLearService.insertYearLeaver(learYear);
		Map<String,Object> activitiMap = new HashMap<>();
		if(flag==1){
			System.out.println("增加年假"+learYear);
			System.out.println("开始时间"+beginTime);
			System.out.println("结束时间"+endTime);
			String[] start = beginTime.split(",");
			String[] end = endTime.split(",");
			String[] day = offdays.split(",");
			int daytime = 0;
			for(int i=0;i<Integer.parseInt(pc);i++){
				LearTime learTime = new LearTime();
				learTime.setId(UUID.randomUUID().toString());
				learTime.setLeave_id(Id);
				learTime.setBeingTime(start[i]);
				learTime.setEndTime(end[i]);
				learTime.setDays(day[i]);
				daytime+=Integer.parseInt(day[i]);
				learTime.setState("0");
				learTime.setSfyc("0");
				learTime.setStatus("0");
				learTimeService.insertTime(learTime);
			}
			UserEntity applyManUser = usSerService.loginOpenId(openId);
			if(applyManUser!=null){
				activitiMap.put("leaverApply", applyManUser.getCode());
	    		//开启流程
				String applyId = Id+"";
				String objId="leaverYear:"+applyId;
				identityService.setAuthenticatedUserId(applyManUser.getCode());
				runtimeService.startProcessInstanceByKey("leaverYear",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("leaverYear",applyManUser.getCode(),"leaverapply");
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
				
				UserEntity userEntity = usSerService.loginOpenId(learYear.getApproveManOpenId());
				String GZhopenID = "";//
				if(userEntity!=null){
					GZhopenID = userEntity.getModifiedby();
					System.out.println("任务编号："+taskId);
					Map<String,Object> map = new HashMap<>();
					map.put("leaveApprove", userEntity.getCode());
					//完成任务
					System.out.println("任务编号TaskId是"+taskId);
					completeApplyTask(taskId,null,map);
					
				}
				//微信公众号推送
				System.out.println("推送");
				String title = "您好,您有新的年假待审批";
				String mrak = "待审批";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setApplyTime(smf.format(date));//申请时间
				weiEntity.setApplyMan(learYear.getApplyMan());
				weiEntity.setDays(String.valueOf(daytime));
				weiEntity.setFrequency(learYear.getFrequency());
				weiEntity.setLeaveType(learYear.getLeaveType());//年假类型
				try {
					CommonUtil.sendMessage(GZhopenID,weiEntity,title,mrak);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		}
		return AnccResult.ok(flag);
	}
	
	
	public   void completeApplyTask(String taskId,String comment,Map<String,Object>map){
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowUtil.completeTask(workflowBean, map);
	}

	
	
	
	/**
	 * 领导审批
	 * @return
	 */
	@RequestMapping("/leaverApprove")
	@ResponseBody
	public AnccResult leaverApprove(String id, String taskId,String result,String comment){
		
		return AnccResult.ok(yearLearService.leaverApprove(id,taskId,result,comment));
	}
	
	@RequestMapping("/getnull")
	@ResponseBody
	public AnccResult nullscs() throws ParseException{
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-mm-dd");
		smf.parse("sasdasdas");
		return AnccResult.ok(new ThrowTimeoutException());
	}
	
	/**
	 * 员工确认和放弃
	 * @param id
	 * @param taskId//任务Id
	 * @param leaverStaffResult 1确认休假 2放弃
	 * @return
	 */
	@RequestMapping("/staffApprove")
	@ResponseBody
	public AnccResult staffApprove(String id,String timeId,String openId1,String taskId,String leaverStaffResult){
		System.out.println("id是"+id);
		LearYear yearLeaver = yearLearService.selectYearLeaver(id);
		Map<String,Object> map = new HashMap<>();
		if(leaverStaffResult.equals("1")){//确认休假
			LearTime learTime = learTimeService.getLearTime(timeId);
			int result = learTimeService.updateState(timeId);
			List<LearTime> lear = learTimeService.selectLearTime(learTime.getLeave_id());
			System.out.println("查询"+lear);
			boolean flag = false;
			if(null == lear || lear.size() ==0){//如果所有字表的年假都完成。那么年假表也修改状态
				flag = true;
			}	
			System.out.print(flag);
			if(flag){
				yearLeaver.setStatus("完结");
				yearLearService.updateYearLeaver(yearLeaver);
				yearLearService.updateState(id);
				map.put("leaverStaffResult", leaverStaffResult);
				completeApplyTask(taskId, null, map);
			}	
			
			
			return AnccResult.ok(result);
		}
		if(leaverStaffResult.equals("2")){
			LearYear leaver = yearLearService.selectYearLeaver(id);
			LearTime learTime = learTimeService.getLearTime(timeId);
			
			int result = learTimeService.updateStateStatus(timeId);
			List<LearTime> lear = learTimeService.selectLearTime(leaver.getId());
			System.out.println("查询"+lear);
			boolean flag = false;
			if(null == lear || lear.size() ==0){//如果所有字表的年假都完成。那么年假表也修改状态
				flag = true;
			}	
			if(flag){
				leaver.setStatus("放弃");
				yearLearService.updateYearLeaver(leaver);
				yearLearService.updateState(id);
				map.put("leaverStaffResult", leaverStaffResult);
				completeApplyTask(taskId, null, map);
			}
			String openId = "";
			if(leaver!=null){
				openId = leaver.getApproveManOpenId();
			}
			UserEntity userEntity = usSerService.loginOpenId(openId);
			String gzhOpenId = "";
			if(userEntity!=null){
				gzhOpenId = userEntity.getModifiedby();
			}
			//微信公众号推送
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setLeaveType(leaver.getLeaveType());
			if(learTime!=null){
				weiEntity.setDays(learTime.getDays());
			}
			weiEntity.setFrequency(leaver.getFrequency());
			weiEntity.setApplyMan(leaver.getApplyMan());
			String status = "";
			if(null!=leaver.getStatus()){
				status = CommonUtil.setStatus(leaver.getStatus());
			}
			weiEntity.setStatus(status);
			String title = "您好，员工放弃修年假";
			String mark = "员工放弃";
			try {
				CommonUtil.sendMessage(gzhOpenId, weiEntity, title, mark);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return AnccResult.ok(result);
		}
		return AnccResult.ok("ok");
	}
	
	
	
	/**
	 * 统计标示
	 * @param openId
	 * @return
	 */
	
	@RequestMapping("/countLeaver")
	@ResponseBody
	public AnccResult countLeaver(String openId){
		
		
		
		return AnccResult.ok(yearLearService.countLeaver(openId));
	}
	
	
	
	
	/**
	 * 撤销年假
	 * @param id 年假主键 唯一标识
	 * @param taskId 任务Id
	 * @param cause 原因
	 * @return
	 */
	@RequestMapping("/deleteLeaver")
	@ResponseBody
	public AnccResult deleteLeaver(String id,String taskId,String cause){
		int flag = 0;
		LearYear leaver = yearLearService.selectYearLeaver(id);
		if(leaver!=null){
			workflowUtil.deleteProcess(taskId);
			flag = yearLearService.deleteYearLeaver(id);
			learTimeService.deleteTime(id);
			UserEntity userEntity = usSerService.loginOpenId(leaver.getApproveManOpenId());
			String GZhopenID = "";//
			if(userEntity!=null){
				GZhopenID = userEntity.getModifiedby();
				
			}
			int daytime = 0;
			List<LearTime> lear = learTimeService.listLear(id);
			for(LearTime learTime:lear){
				if(learTime.equals("0")){
					daytime+= Integer.parseInt(learTime.getDays());
				}
			}
			//微信公众号推送
			String title = "您好,年假申请撤销";
			String mrak = "已撤销";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApplyTime(leaver.getApplyTime());//申请时间
			weiEntity.setApplyMan(leaver.getApplyMan());
			weiEntity.setDays(String.valueOf(daytime));
			weiEntity.setFrequency(leaver.getFrequency());
			weiEntity.setLeaveType(leaver.getLeaveType());//年假类型
			try {
				CommonUtil.sendMessage(GZhopenID,weiEntity,title,mrak);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return AnccResult.ok(flag);
	}
	
	
	/**
	 * 查询待执行的年假
	 * @param openId
	 * @return
	 */
		@RequestMapping("/leaverexecuted")
		@ResponseBody
		public AnccResult Leaverexecuted(String openId){
			LearYear leaver = yearLearService.selectUserIDLeaver(openId);
			List<Map<String, Object>> executed = new ArrayList<>();
			if(leaver==null){
				return AnccResult.ok(executed);
			}
			 executed = yearLearService.executed(openId,leaver.getId());
			return AnccResult.ok(executed);
		}
		
		
		
		@RequestMapping("/listLeaverExcuted")
		@RequestScoped
		public AnccResult listLeaverExcuted(String openId){
			LearYear leaver = yearLearService.selectUserIDLeaver(openId);
			if(leaver!=null){
				List<LearTime> listLear = learTimeService.listLear(leaver.getId());
				leaver.setLeaver(listLear);
			}
			
			
			return AnccResult.ok(leaver);
		}
	
	
	
	
	
	
	
	
	
	

	
	/*@RequestMapping(value="/updateLeavel",method = RequestMethod.POST)
	@ResponseBody
	public AnccResult updateLeavel(LearYear learYear,String beginTime,String endTime,String days,String ids,HttpServletRequest requset){
		System.out.println("修改年假"+learYear);
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date data = new Date();
		learYear.setApplyTime(smf.format(data));
		LearTime learTime = new LearTime();
		learTime.setId(ids);
		learTime.setBeingTime(beginTime);
		learTime.setEndTime(endTime);
		learTime.setDays(days);
		System.out.println("更新附属表");
		learTimeService.updateBach(learTime);

		System.out.println("list是"+learTime);
		//批量修改
		
		return AnccResult.ok(yearLearService.updateYearLeaver(learYear));
	}*/
	/**
	 * 调整年假
	 * @param learYear
	 * @return
	 */
	@Transactional
	@RequestMapping("/update")
	@ResponseBody
	public AnccResult update(LearYear leaver,String beginTime,String endTime,String offdays){
		LearYear learYear = yearLearService.selectYearLeaver(leaver.getId());
		
		String GzhyOpenId = "";
		String approveMan = "";
		leaver.setStatus("待审批");
		yearLearService.updateYearLeaver(leaver);
		UserEntity userEntity = usSerService.loginOpenId(learYear.getApproveManOpenId());
		if(userEntity!=null){
			GzhyOpenId = userEntity.getModifiedby();
			approveMan = userEntity.getCode();
		}
		int daytime = 0;
		int deleteTime = learTimeService.deleteTime(leaver.getId());
		System.out.println("删除成功"+deleteTime);
		if(deleteTime!=0){
			String pc = "0";
			if(null!=leaver.getFrequency()){
				pc = leaver.getFrequency();
			}
			System.out.println("pc是"+pc);
			System.out.println("调整的次数"+learYear.getFrequency());
			String[] startTime = beginTime.split(",");
			String[] endtime = endTime.split(",");
			String[] day = offdays.split(",");
			for(int i=0;i<Integer.parseInt(pc);i++){
				LearTime learTime = new LearTime();
				String Id = UUID.randomUUID().toString();
				learTime.setId(Id);
				learTime.setLeave_id(leaver.getId());
				learTime.setBeingTime(startTime[i]);
				learTime.setEndTime(endtime[i]);
				learTime.setDays(day[i]);
				daytime+=Integer.parseInt(day[i]);
				learTime.setSfyc("0");
				learTime.setState("0");
				learTimeService.insertTime(learTime);
			}
			Map<String,Object>map = new HashMap<>();
			/*map.put("leaverStaffResult", "1");*/
			String taskId = leaver.getTaskId();
			Task task = taskService.createTaskQuery()//
					.taskId(taskId)//使用任务ID查询
					.singleResult();
			String key = task.getTaskDefinitionKey();
			if(key.equals("leaveYearStafuser")){
				map.put("leaveApprove", approveMan);
				map.put("leaverStaffResult", "4");
				completeApplyTask(leaver.getTaskId(),null,map);
				workflowUtil.TaskRollBack(taskId);
				String title = "您好,您有新的年假待审批";
				String mrak = "待审批";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setApplyTime(learYear.getApplyTime());//申请时间
				weiEntity.setApplyMan(learYear.getApplyMan());
				weiEntity.setDays(String.valueOf(daytime));
				weiEntity.setFrequency(leaver.getFrequency());
				weiEntity.setLeaveType(learYear.getLeaveType());//年假类型
				try {
					CommonUtil.sendMessage(GzhyOpenId,weiEntity,title,mrak);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				if(!key.equals("leaverApprove")){
					map.put("leaveApprove", approveMan);
					completeApplyTask(leaver.getTaskId(),null,map);
					String title = "您好,您有新的年假待审批";
					String mrak = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setApplyTime(learYear.getApplyTime());//申请时间
					weiEntity.setApplyMan(learYear.getApplyMan());
					weiEntity.setDays(String.valueOf(daytime));
					weiEntity.setFrequency(leaver.getFrequency());
					weiEntity.setLeaveType(learYear.getLeaveType());//年假类型
					try {
						CommonUtil.sendMessage(GzhyOpenId,weiEntity,title,mrak);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return AnccResult.ok("ok");
	}
	//休假审批状态
	@RequestMapping("/statusLeaver")
	@ResponseBody
	public AnccResult statusLeaver(String openId){
		System.out.println("查询"+openId);
		List<LearYear> list = new ArrayList<>();
		LearYear selectUserIDLeaver = yearLearService.selectUserIDLeaver(openId);
		if(selectUserIDLeaver==null){
			
			return AnccResult.ok(list);
		}
		list.add(selectUserIDLeaver);
		
		if(selectUserIDLeaver!=null){
			if(selectUserIDLeaver.getSfyc().equals("1")){
				selectUserIDLeaver.setStatus("9");
			}
			selectUserIDLeaver.setStatus(CommonUtil.setStatus(selectUserIDLeaver.getStatus()));
		}
		return AnccResult.ok(list);
	}
	
	/**
	 * 修改查询年假回写
	 * @return
	 */
	@RequestMapping(value = "/listupdateLeaver",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult getUpdateLeavl(String openId){
		System.out.println("修改查询");
		LearYear leaver = yearLearService.selectUserIDLeaver(openId);
		String leave_id = "";
		if(leaver!=null){
			leave_id = leaver.getId();
			int pc = 0;
			int days = 0;
			List<LearTime> listLear = learTimeService.listLear(leave_id);
			if(listLear!=null){
				for(LearTime learTime:listLear){
					if(learTime.getState().equals("1")){
						pc++;
						days+=Integer.parseInt(learTime.getDays());
					}
				}
			}
			leaver.setLeaver(listLear);
			leaver.setPc(pc);
			leaver.setDays(days);
		}
		return AnccResult.ok(leaver);
	}
	
	/**
	 * 确认休假
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/breakupLeavel",method = RequestMethod.GET)	
	@ResponseBody
	public AnccResult breakupLeavel(String PRO_ID,String id,String formId,String oppenid){
		System.out.println("主键是"+PRO_ID);
		System.out.println("id是"+id);
		System.out.println("formId是"+formId);
		System.out.println("openid是"+oppenid);
		LearTime learTime = learTimeService.getLearTime(id);
		int result = learTimeService.updateState(id);
		List<LearTime> lear = learTimeService.selectLearTime(learTime.getLeave_id());
		System.out.println("查询"+lear);
		boolean flag = false;
		if(null == lear || lear.size() ==0){//如果所有字表的年假都完成。那么年假表也修改状态
			flag = true;
		}	
		System.out.print(flag);
		if(flag){
			yearLearService.updateEnd(PRO_ID);
			yearLearService.updateState(PRO_ID);
		}	
		return AnccResult.ok(AnccResult.ok(result));
	}
	
	
	
	//领导查看状态标识
	@RequestMapping("/countLeavel")
	@ResponseBody
	public AnccResult countLeavel(String openId){
		System.out.println("查看Id是"+openId);
		return AnccResult.ok(yearLearService.selectNum(openId));
	}
	
	
	
	//审批人查询年假
	@RequestMapping("/listLeaver")
	@ResponseBody
	public AnccResult listLeavel(String openId ){
		System.out.println("查询"+openId);
		List<LearYear> list = yearLearService.listYearLeaver(openId);
		Map<String,Object> map = new HashMap<>();
		map.put("lear", list);
		return AnccResult.ok(map);
	}
	//查询每一条年假的详细信息
	@RequestMapping("/getLeaver")
	@ResponseBody
	public AnccResult getLeavel(String id,String timeid){
		LearYear learYear = yearLearService.selectYearLeaver(id);
		System.out.println("年假"+learYear);
		LearTime listLear = learTimeService.getLearTime(timeid);
		learYear.setTime(listLear);
		return AnccResult.ok(learYear);
	}
	
	//查询每一条年假的详细信息
	@RequestMapping("/aproveLeavel")
	@ResponseBody
	public AnccResult aproveLeavel(String id){
		LearYear learYear = yearLearService.selectYearLeaver(id);
		System.out.println("年假"+learYear);
		List<LearTime> listLear = learTimeService.listLear(learYear.getId());
		learYear.setLeaver(listLear);
		return AnccResult.ok(learYear);
	}

	/**
	 * 查看年假
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/selectLeavle",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult selectLeavel(String id){
		System.out.println("查看"+id);
		return AnccResult.ok(yearLearService.selectYearLeaver(id));
	}
	
	/**
	 * 查看附属字表
	 */
	@RequestMapping(value="/listTime",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult listTime(String leave_id,String openId){
		 LearYear year = yearLearService.selectUserIDLeaver(openId);
		 List<LearTime> listTime = new ArrayList<>();
		 LearTime learTime = learTimeService.getLearTime(leave_id);
		 if(learTime!=null){
			 listTime.add(learTime);
			 year.setIds(learTime.getId());
			 year.setLeaver(listTime);
		 }
		System.out.println("附属表ID是"+leave_id);
		return AnccResult.ok(year);
	}
	
	
	
	/**
	 * 驳回待修改
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updateReject",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateReject(String id,String formId,String oppenid,HttpServletRequest request){
		int flag = 0;
		flag = yearLearService.updateReject(id);
		LearYear leaver = yearLearService.selectYearLeaver(id);
		System.out.println("查询审批人是"+leaver.getApproveMan());
		String GzhOpenId = "";
		if(leaver!=null){
			UserEntity userEntity = usSerService.loginOpenId(leaver.getOpenId());
			GzhOpenId = userEntity.getModifiedby();//拿到公众号OpenId进行推送
		}
		if(flag==1){
			WeiEntity weiEntity = new WeiEntity();
			if(leaver!=null){
				weiEntity.setApproveMan(leaver.getApproveMan());
				weiEntity.setLeaveType(leaver.getLeaveType());
				String status = CommonUtil.setStatus(leaver.getStatus());
				weiEntity.setStatus(status);
			}
			String status = "驳回修改";
			String view = "领导审批完成";
			try {
				CommonUtil.sendApply(GzhOpenId, weiEntity,status,view);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return AnccResult.ok(flag);
	}
	/**
	 * 部门审批通过
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updatePass",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updatePass(String id,String openId,String formId,HttpServletRequest request){
		int flag = yearLearService.updatePass(id);
		LearYear leaver = yearLearService.selectYearLeaver(id);
		String GzhOpenId = "";
		if(null!=leaver.getOpenId()){
			UserEntity userEntity = usSerService.loginOpenId(leaver.getOpenId());
			if(null!=userEntity.getModifiedby()){
				GzhOpenId = userEntity.getModifiedby();
			}
		}
		if(flag==1){
			//推送
			WeiEntity weiEntity = new WeiEntity();
			System.out.println("处理人是"+leaver.getApproveMan());
			System.out.println("类型是"+leaver.getLeaveType());
			weiEntity.setApproveMan(leaver.getApproveMan());
			weiEntity.setLeaveType(leaver.getLeaveType());
			String status = "";
			if(null!=leaver.getStatus()){
				status = CommonUtil.setStatus(leaver.getStatus());
			}
			weiEntity.setStatus(status);
			status = "审批通过";
			String view = "领导审批完毕";
			try {
				CommonUtil.sendApply(GzhOpenId, weiEntity,status,view);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return AnccResult.ok(flag);
	}
	
	/**
	 * 员工放弃
	 * @param id
	 * @result 判断是员工自己放弃还是领导点击放弃
	 * @return
	 */
	@RequestMapping(value = "/updateRenounce",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateRenounce(String id,String PRO_ID,String state){
		System.out.println("主键是"+PRO_ID);
		System.out.println("id是"+id);
		LearYear leaver = yearLearService.selectYearLeaver(PRO_ID);
		LearTime learTime = learTimeService.getLearTime(id);
		System.out.println("查询年假的信息是"+leaver);
		/*String openId = "";
		if(null!=leaver.getOpenId()){
			openId = leaver.getApproveManOpenId();
		}
		UserEntity userEntity = usSerService.loginOpenId(openId);
		String gzhOpenId = "";
		if(userEntity!=null){
			gzhOpenId = userEntity.getModifiedBy();
		}*/
		int result = learTimeService.updateState(id);
		List<LearTime> lear = learTimeService.selectLearTime(leaver.getId());
		System.out.println("查询"+lear);
		boolean flag = false;
		if(null == lear || lear.size() ==0){//如果所有字表的年假都完成。那么年假表也修改状态
			flag = true;
		}	
		if(flag){
			yearLearService.updateRenounce(PRO_ID);
			yearLearService.updateState(PRO_ID);
		}
		
		if(state.equals("0")){
			String openId = "";
			if(leaver!=null){
				openId = leaver.getApproveManOpenId();
			}
			UserEntity userEntity = usSerService.loginOpenId(openId);
			String gzhOpenId = "";
			if(userEntity!=null){
				gzhOpenId = userEntity.getModifiedby();
			}
			//微信公众号推送
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setLeaveType(leaver.getLeaveType());
			if(learTime!=null){
				weiEntity.setDays(learTime.getDays());
			}
			weiEntity.setFrequency(leaver.getFrequency());
			weiEntity.setApplyMan(leaver.getApplyMan());
			String status = "";
			if(null!=leaver.getStatus()){
				status = CommonUtil.setStatus(leaver.getStatus());
			}
			weiEntity.setStatus(status);
			String title = "您好，员工放弃修年假";
			String mark = "员工放弃";
			try {
				CommonUtil.sendMessage(gzhOpenId, weiEntity, title, mark);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("领导点击放弃");
			int i = learTimeService.updateStateLeave(PRO_ID);
			System.out.println("更新附属字表的状态"+i);
			yearLearService.updateRenounce(PRO_ID);
			yearLearService.updateState(PRO_ID);
			String openId = "";
			if(leaver!=null){
				openId = leaver.getOpenId();
			}
			UserEntity userEntity = usSerService.loginOpenId(openId);
			String gzhOpenId = "";
			if(userEntity!=null){
				gzhOpenId = userEntity.getModifiedby();
			}
			WeiEntity weiEntity = new WeiEntity();
			gzhOpenId = userEntity.getModifiedby();
			weiEntity.setLeaveType(leaver.getLeaveType());
			weiEntity.setApproveMan(leaver.getApproveMan());
			String status = "请填写纸质说明,《员工考勤休假确认单》";
			String view = "领导审批完毕";
			try {
				CommonUtil.sendApply(gzhOpenId, weiEntity, status, view);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return AnccResult.ok(result);
	}
	/**
	 * 转接第二年
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updateTransfer",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateTransfer(String id,String oppenId,String formId,HttpServletRequest request){
		int flag = 0;
		List<LearTime> listTime = learTimeService.listLear(id);
		int i =0 ;
		for(LearTime list:listTime ){
			if(list.getState().equals("0")){//如果年假没有修过则转接到第二年
				i+=Integer.parseInt(list.getDays());
			}
		}
		LearYear leaver = yearLearService.selectYearLeaver(id);
		String gzhOpenId = "";
		UserEntity userEntity = new UserEntity();
		if(leaver!=null){
			userEntity = usSerService.loginOpenId(leaver.getOpenId());
		}
		System.out.println("天数"+Integer.parseInt(userEntity.getCreateby())+i);
		
		userEntity.setCreateby(String.valueOf(Integer.parseInt(userEntity.getCreateby())+i));
		System.out.println("查询到的用户是"+userEntity);
		//年假时间更新
		usSerService.updateUserDays(userEntity);
		flag = yearLearService.updateTransfer(id);//转接到第二年
		yearLearService.updateState(id);//任务无效
		learTimeService.updateStateLeave(id);//附属表无效
		if(flag==1){
			//推送微信消息
			WeiEntity weiEntity = new WeiEntity();
			gzhOpenId = userEntity.getModifiedby();
			weiEntity.setLeaveType(leaver.getLeaveType());
			weiEntity.setApproveMan(leaver.getApproveMan());
			String status = "转接第二年";
			String view = "领导审批完毕";
			try {
				CommonUtil.sendApply(gzhOpenId, weiEntity, status, view);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return AnccResult.ok(flag);
	}
	/**
	 * 现金补偿
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updateCash",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateCash(String id){
		int cash = yearLearService.updateCash(id);
		yearLearService.updateState(id);//任务无效
		learTimeService.updateStateLeave(id);
		if(cash==1){
			LearYear leaver = yearLearService.selectYearLeaver(id);
			
			
			String gzhOpendId = "";
			if(leaver!=null){
				UserEntity userEntity = usSerService.loginOpenId(leaver.getOpenId());
				//恢复天数
				/////............
				
				if(userEntity!=null){
					gzhOpendId = userEntity.getModifiedby();
				}
			}
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setLeaveType(leaver.getLeaveType());
			weiEntity.setApproveMan(leaver.getApproveMan());
			String status = "现金补偿";
			String view ="领导审批完成";
			try {
				CommonUtil.sendApply(gzhOpendId, weiEntity, status, view);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return AnccResult.ok("ok");
	}
	/**
	 * 异常
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/updateAbnormal",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateAbnormal(String id){
		return AnccResult.ok(yearLearService.updateAbnormal(id));
	}

	
	/**
	 * 查询明细
	 * @param leaver
	 * @return
	 */
	@RequestMapping("/searLeaver")
	@ResponseBody
	public AnccResult seraLeaver(LearYear leaver,String openId,Integer nowPage,Integer pageSize){
	/*	System.out.println("查询出来的数据"+yearLearService.searLeaver(leaver));*/
		return AnccResult.ok(yearLearService.searLeaver(leaver,openId,nowPage,pageSize));
	}

	//查看完成的附属表
	@RequestMapping("/searTime")
	@ResponseBody
	public AnccResult searTime(String id){
		LearYear selectYearLeaver = yearLearService.selectYearLeaver(id);
		selectYearLeaver.setLeaver(learTimeService.seartTime(selectYearLeaver.getId()));
		return AnccResult.ok(selectYearLeaver);
	}
	
	
	
	//@Scheduled(cron="0/5 * *  * * ? ")
	@Scheduled(cron="0 15 10 ? * *")//每天上午10点15执行一次
    public void scheduler()throws InterruptedException, ParseException{      
        System.out.println("5秒执行一次");
        List<Map<String,Object>> list = yearLearService.listExecuted();//获取所有年假
        System.out.println("list是"+list);
        String begingTime = "";
        String endingTime = "";
        String id = "";
		Date nowDate = new Date();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		if(list!=null){
			for(Map<String,Object> map:list){
				begingTime = map.get("beingTime").toString();
				endingTime = map.get("endTime").toString();
				id = map.get("id").toString();
				System.out.println("开始时间是"+begingTime);
				Date date = smf.parse(begingTime);
				System.out.println("当前时间"+smf.format(nowDate));
				int days = (int) ((date.getTime() - nowDate.getTime()) / (1000*3600*24));
				System.out.println("天数是"+days);
				if(days<=3&&days>=0){//
					//距离休假还有三天所以开始推送
					System.out.println("推送");
					String openId = map.get("openId").toString();//获取待执行的OpenId进行推送
					//需要将模板消息推送给一个人
					UserEntity entity = usSerService.loginOpenId(openId);
					WeiEntity weitity = new WeiEntity();
					weitity.setLeaveType(map.get("leaveType").toString());
					weitity.setBegingTime(begingTime);
					weitity.setEndTime(endingTime);
					if(entity!=null){
						String GzhOpneId = entity.getModifiedby();//获取公众号Id
						System.out.println("公众号Id是"+GzhOpneId);
						try {
							CommonUtil.sendSubmit(GzhOpneId, weitity);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else if(days<0){
					//生成异常
					LearYear learYearEntity = yearLearService.selectYearLeaver(id);
					String number = learYearEntity.getId();
					List<Task> tasks = workflowUtil.getTaskByIds("leaverYear",learYearEntity.getOpenId(),"leaveYearStafuser");
					List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
					System.out.println("bidr是"+bids);
					int i=0;
					for(;i<bids.size();i++){
						System.out.println("bids.get(i)是"+bids.get(i));
						if(bids.get(i)==number)
							System.out.println("相等"+i);
							break;
					}
					String taskId=tasks.get(i).getId();
					System.out.println("任务编号："+taskId);
					Map<String,Object> activitimap = new HashMap<>();
					activitimap.put("leaverAbnormaluser", learYearEntity.getApproveManOpenId());
					//完成任务
					System.out.println("任务编号TaskId是"+taskId);
					
					completeApplyTask(taskId,null,activitimap);
					/*yearLearService.updateYearLeaver(learYearEntity);
					yearLearService.updateAbnormal(map.get("id").toString());
					System.out.println("异常");*/
					LearYear learYear = yearLearService.selectYearLeaver(id);
					String openId = learYear.getApproveManOpenId();
					UserEntity userEntity = usSerService.loginOpenId(openId);
					String gzhOpenId = "";
					if(userEntity!=null){
						gzhOpenId = userEntity.getModifiedby();
					}
					if(learYear!=null){
						WeiEntity weiEntity = new WeiEntity();
						weiEntity.setBegingTime(begingTime);
						weiEntity.setEndTime(endingTime);
						try {
							CommonUtil.sendAbnormalMessage(gzhOpenId, weiEntity);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					
				}
		}
		


			
	}
	
	

	
	
	
    }
	
	
	/*************************F***************************************************************************************************/

	@RequestMapping("/webleaver")
	public ModelAndView leaverYear(){
		System.out.println("查看年假的网页");
		ModelAndView result = new ModelAndView("leave/index");
		return result;
	}
	
	//web端查
	@RequestMapping("/searchLearYear")
	@ResponseBody
	public Object searchLearYear(LearYear learYear,HttpServletRequest request,String name){
		List<DepartmentEntity> department_list = departmnetService.selectDepartment();
		/*System.out.println("name是"+name);
		request.getSession().setAttribute("name", name);
		String departmentName = "";
		for(DepartmentEntity department:department_list){
			if(name.indexOf(department.getDepartmentName())!=-1){
				departmentName = department.getDepartmentName();
			}
		}
		System.out.println("部门是"+departmentName);*/
		String page = request.getParameter("page");// 第几页
	    String rows = request.getParameter("rows");// 每页多少条
	    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
	    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
	    // 每页的开始记录
	    int start = (intPage - 1) * number;
	    /*if(!departmentName.equals("综合办公室")){
	    	learYear.setDepartment(departmentName);
	    }*/
	    
	    List<LearYear> list_all = yearLearService.searchLeaver(learYear);
	    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<LearYear> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	
	
	@RequestMapping("/searchlistLearYear")
	@ResponseBody
	public Object searLearYear(LearYear learYear,HttpServletRequest request){
		
		String page = request.getParameter("page");// 第几页
	    String rows = request.getParameter("rows");// 每页多少条
	    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
	    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
	    // 每页的开始记录
	    int start = (intPage - 1) * number;
	    List<LearYear> list_all = yearLearService.searchLeaver(learYear);
	    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<LearYear> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	
	
	
	
	
	
	
	
	
	
	@RequestMapping("/listLeaverTime")
	@ResponseBody
	public Object listLeaverTime(String id){
		List<LearTime> list = learTimeService.listLear(id);
		for(LearTime learTime:list){
			String state = "";
			if("0".equals(learTime.getStatus())){
				state = "否";
			}
			if("1".equals(learTime.getStatus())){
				state = "是";
			}
			learTime.setState(state);
			String sfyc = "";
			if("0".equals(learTime.getSfyc())){
				sfyc = "否";
			}
			if("1".equals(learTime.getSfyc())){
				sfyc = "是";
			}
			learTime.setSfyc(sfyc);
		}
		return list;
	}
	
	//导出信息
	@RequestMapping("/exportLeavel")
	@ResponseBody
	public void exportYearLeavel(HttpServletRequest request,HttpServletResponse response,LearYear learYear){
		
		String path = request.getSession().getServletContext().getRealPath("/");
	    String fileName = "年假统计表" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xlsx";
	    String filePath = path + "/" + fileName;
		int flag = yearLearService.exportYearLeaver(learYear, filePath);
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
	
	
	/**
     * 待部门审批
     * @param filePath excel文件的绝对路径
     * 
     */
    @RequestMapping("/getDataFromExcel")
    @ResponseBody
    public  String getDataFromExcel(String filePath)
    {
    	filePath = "C:\\leave.xlsx";//差旅
        //String filePath = "E:\\123.xlsx";
        //判断是否为excel类型文件
        if(!filePath.endsWith(".xls")&&!filePath.endsWith(".xlsx"))
        {
            System.out.println("文件不是excel类型");
        }
        FileInputStream fis =null;
        XSSFWorkbook wookbook = null;
        
        try
        {
            //获取一个绝对地址的流
              fis = new FileInputStream(filePath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
            try
            {
                //2007版本的excel，用.xlsx结尾
                wookbook = new XSSFWorkbook(fis);//得到工作簿
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        //得到一个工作表
        Sheet sheet = wookbook.getSheetAt(0);
        //获得表头
        Row rowHead = sheet.getRow(0);
        
        //获得数据的总行数
        int totalRowNum = sheet.getLastRowNum();
        //要获得属性
     
       //获得所有数据
        for(int i = 1 ; i <= totalRowNum ; i++)
        {
            //获得第i行对象
            Row row = sheet.getRow(i);
            
            //获得获得第i行第0列的 String类型对象
           
            if(row.getCell((short)2)!=null){
            	 Cell cell = row.getCell((short)2);
            	 String applMan = cell.getStringCellValue().toString();
                 System.out.println("ID是"+applMan);
                 
                 cell = row.getCell((short)10);
                 int days = (int)cell.getNumericCellValue();
                 System.out.println("申请人"+days );
                 cell = row.getCell((short)12);
                 String approveMan = cell.getStringCellValue().toString();
                 UserEntity userByUserCode = usSerService.getUserByUserCode(applMan);
                 if(userByUserCode!=null){
                 	userByUserCode.setCreateby(days+"");
                 	usSerService.updateUserDays(userByUserCode);
                 }
            }
            
        }
        return "SUCCESS";
    }
	
}
