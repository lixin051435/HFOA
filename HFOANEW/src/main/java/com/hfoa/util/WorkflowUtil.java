package com.hfoa.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hfoa.entity.activity.WorkflowBean;

@Component
public class WorkflowUtil {
	/*ApplicationContext applicationContext= new ClassPathXmlApplicationContext(
			new String[]{"classpath:spring-mybatis.xml","classpath:spring-mvc.xml"});
	ProcessEngine processEngine=(ProcessEngine)applicationContext.getBean("processEngine");
	*/
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	IdentityService identityService;
	@Autowired
	TaskService taskService;
	@Autowired
	FormService formService;
	@Autowired 
	RuntimeService runtimeService;
	@Autowired
	HistoryService historyService;
	
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	/**根据zip文件发布流程实例*/
	public void saveNewDeploye(File file, String filename) {
		try {
			//2：将File类型的文件转化成ZipInputStream流
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment()//创建部署对象
							.name(filename)//添加部署名称
							.addZipInputStream(zipInputStream)//
							.deploy();//完成部署
			/*for(BRoleEntity role:roles){
				identityService.saveGroup((Group) role);
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**bpmn文件的地址+名字发布流程*/
	public String deployment(String src, String name) {
		Deployment deployment = repositoryService.createDeployment()
				.name(name)
				.addClasspathResource(src+".bpmn")
				.addClasspathResource(src+".png")
				.deploy();
		
		return deployment.getId();
	}
	
	/**添加评论*/
	public void addComment(String taskId,String msg){
		//获取当前用户的信息
		/*Session session = SecurityUtils.getSubject().getSession();
		ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");*/
		//根据taskId获取任务的所属者,如果是组任务，则获取的任务执行者有问题

		
	   List<IdentityLink> list = taskService.getIdentityLinksForTask(taskId);
	   String user = "";
	   if(list!=null&list.size()>0){
		   user = list.get(0).getUserId();
	   }
		/*注意：添加批注的时候由于Activiti底层使用：
		 * String userId = Authentication.getAuthenticatedUserId();
		 * CommentEntity comment = new CommentEntity();
		 * comment.setUserId(userId);
		 * 所以需要制定任务的办理人，对应act_hi_comment表中的User_ID字段，不添加审核人，默认为null
		 * 所以要求添加配置，执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人 */
		
		Authentication.setAuthenticatedUserId(user);
		String processInstanceId = getTaskById(taskId).getProcessInstanceId();
		taskService.addComment(taskId, processInstanceId, msg);
	}
	
	/**添加评论*/
	public void addComment(String taskId,String msg,String type){
		//获取当前用户的信息
		Session session = SecurityUtils.getSubject().getSession();
//		ActiveUser activeUser = (ActiveUser) session.getAttribute("activeUser");
		Authentication.setAuthenticatedUserId("11");
		String processInstanceId = getTaskById(taskId).getProcessInstanceId();
		taskService.addComment(taskId, processInstanceId,type,msg);
	}
	
	/**查询部署对象信息，对应表（act_re_deployment）*/
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService
				.createDeploymentQuery()//创建部署对象查询
				.orderByDeploymenTime().asc()//
				.list();
		return list;
	}
	
	/**查询流程定义的信息，对应表（act_re_procdef）*/
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery()//创建流程定义查询
				.orderByProcessDefinitionVersion().asc()//
				.list();
		return list;
	}
	
	/**使用部署对象ID，删除流程定义*/
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		//级联删除：删除当前规则相关的所有信息，包括正在执行的信息和历史信息。
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>*/
	public List<Task> findTaskListByName(String name) {
		List<Task> list = taskService
				.createTaskQuery()//
				.taskAssignee(name)//指定个人任务查询
				.orderByTaskCreateTime().desc()//最近的放最上面
				.list();
		return list;
	}
	
	/**使用当前用户名查询组任务，获取当前任务的集合List<Task>*/
	public List<Task> findGroupTaskListByName(String name) {
		List<Task> list = taskService
				.createTaskQuery()//
				.taskCandidateUser(name)//指定个人任务查询
				.orderByTaskCreateTime().asc()//
				.list();
		return list;
	}
	
	/**使用节点名和当前用户名查询个人任务，获取当前组任务的集合List<Task>*/
	public List<Task> findGroupTaskListByName(String pointName,String userName) {
		List<Task> list = taskService
				.createTaskQuery()//
				.taskName(pointName)
				.taskCandidateUser(userName)//候选人查询
				.orderByTaskCreateTime().asc()//
				.list();
		return list;
	}
	
	/**使用节点名和当前用户名查询个人任务，获取当前个人任务的集合List<Task>*/
	public List<Task> findPersonalTaskListByName(String pointName,String userName) {
		List<Task> list = taskService
				.createTaskQuery()//
				.taskName(pointName)
				.taskAssignee(userName)//指定个人任务查询
				.orderByTaskCreateTime().asc()//
				.list();
		return list;
	}
	
	/**使用任务ID，获取当前任务节点中对应的Form key中的连接的值？*/
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		//获取Form key的值
		String url = formData.getFormKey();
		return url;
	}
	
	/**一：使用任务ID，查找业务对象ID*/
	public String findBussinessIdByTaskId(String taskId) {
		//1：使用任务ID，查询任务对象Task
		Task task = getTaskById(taskId);
		//2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService
				.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)//使用流程实例ID查询
				.singleResult();
		//4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		//5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）
		String id = "";
		if(StringUtils.isNotBlank(buniness_key)){
			//截取字符串，取buniness_key小数点的第2个值
			id = buniness_key.split("\\:")[1];
		}
		//返回业务对象ID
		return id;
	}
	
	/**二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中*/
	public List<String> findOutComeListByTaskId(String taskId) {
		//返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		//1:使用任务ID，查询任务对象
		Task task = getTaskById(taskId);
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)repositoryService
				.getProcessDefinition(processDefinitionId);
		//使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
					.processInstanceId(processInstanceId)//使用流程实例ID查询
					.singleResult();
		//获取当前活动的id
		String activityId = pi.getActivityId();
		//4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		//5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if(pvmList!=null && pvmList.size()>0){
			for(PvmTransition pvm:pvmList){
				String name = (String) pvm.getProperty("name");
				if(StringUtils.isNotBlank(name)){
					list.add(name);
				}
				else{
					list.add("直接办理");
				}
			}
		}
		return list;
	}
	
	/**获取批注信息，传递的是当前任务ID，获取历史任务ID对应的批注*/
	public List<Comment> findCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		//使用当前的任务ID，查询当前流程对应的历史任务ID
		//使用当前任务ID，获取当前任务对象
		Task task = getTaskById(taskId);
		//获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		//1.获取当前的评论
		//List<Comment> currentComment=taskService.getTaskComments(taskId);
		//List<Comment> currentComment=taskService.getProcessInstanceComments(processInstanceId);
		//list.addAll(currentComment);
		//2.获取历史意见
		List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//锟斤拷史锟斤拷锟斤拷锟斤拷询
				.processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().list();//使锟斤拷锟斤拷锟斤拷实锟斤拷ID锟斤拷询
		//遍历集合，获取每个任务ID
		if(htiList!=null && htiList.size()>0){
			for(HistoricTaskInstance hti:htiList){
				String htaskId = hti.getId();//任务ID
				//获取批注信息
				List<Comment> taskList = taskService.getTaskComments(htaskId);//锟斤拷锟斤拷锟斤拷史锟斤拷珊锟斤拷锟斤拷锟斤拷ID
				list.addAll(taskList);
			}
		}
		/*List<Comment> result = new ArrayList<Comment>();
		for(Comment comment:list){
			boolean bool=true;
			for(int i=0;i<result.size();i++){
				if(result.get(i).getId().equals(comment.getId())){
					bool=false;
					break;
				}
			}
			if(bool)
				result.add(comment);
		}*/
		return list;
	}
	
	//根据taskId获取当前任务是否已经写入了意见
	public Comment findLastCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		list=taskService.getTaskComments(taskId);
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**指定连线的名称，完成任务*/
	public void completeTask(WorkflowBean workflowBean,Map<String,Object> map){
		//获取任务ID
		String taskId = workflowBean.getTaskId();
		
		//获取连线的名称
		String outcome = workflowBean.getOutcome();
		//批注信息
		String message = workflowBean.getComment();
		//1.在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
		if(StringUtils.isNotBlank(message))
			addComment(taskId,message);
		//2.如果是“默认提交”就不需要这一步，否则需要设置流程变量
		//完成任务之前，设置流程变量，按照连线的名称去完成任务
		//流程变量的名称：outcome
		taskService.complete(taskId,map);
	}
	
	/**根据任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象*/
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		//使用任务ID，查询任务对象
		Task task = getTaskById(taskId);
		//获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		//查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()//创建流程定义查询对象，对应表act_re_procdef 
					.processDefinitionId(processDefinitionId)//使用流程定义ID查询
					.singleResult();
		return pd;
	}
	
	/**根据taskID获取task*/
	public Task getTaskById(String taskId){
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)//使用任务ID查询
				.singleResult();
		return task;
	}
	
	//根据任务名称获取任务列表
	public List<Task> getTaskByPointName(String name){
		List<Task> tasks = taskService.createTaskQuery()
				.taskName(name)
				.orderByTaskCreateTime().asc().list();
		return tasks;
	}
	
	//根据流程Id和节点Id称获取任务列表
	public List<Task> getTaskByIds(String processId,String pointId){
		List<Task> tasks = taskService.createTaskQuery()
				.processDefinitionKey(processId)
				.taskDefinitionKey(pointId)
				.orderByTaskCreateTime().asc().list();
		return tasks;
	}
	public List<Task> getTaskByIds(String processId,String assignee,String pointId){
		List<Task> tasks = taskService.createTaskQuery()
				.processDefinitionKey(processId)
				.taskDefinitionKey(pointId)
				.taskAssignee(assignee)
				.orderByTaskCreateTime().desc().list();
		return tasks;
	}
	
	
	public List<Task> getTaskByIds(String assignee){
		List<Task> tasks = taskService.createTaskQuery()
				.taskAssignee(assignee)
				.orderByTaskCreateTime().desc().listPage(0, 20);
		return tasks;
	}
	
	
	
	//根据任务列表获取业务ID字符串
	public List<String> getBussinessIdsByTasks(List<Task> tasks){
		List<String> businessIds = new ArrayList<String>();
		for(int i=0;i<tasks.size();i++){
			String bussinessId = findBussinessIdByTaskId(tasks.get(i).getId());
			businessIds.add(bussinessId);
		}
		return businessIds;
	}
	
	//根据任务列表获取业务ID字符串,返回taskId和businessId
	public Map<String,String> getTaskAndBussIdByTask(List<Task> tasks){
		Map<String, String> map = new HashMap<String,String>();
		for(int i=0;i<tasks.size();i++){
			String taskId=tasks.get(i).getId();
			String bussinessId = findBussinessIdByTaskId(taskId);
			map.put(bussinessId,taskId);
		}
		return map;
	}
	
	//将个人任务退回到组任务
	public void setAssigneeTask(String taskId){
		taskService.setAssignee(taskId, null);
	}
	
	//查询一次流程经过了多少个节点（可以用来计算被退回的次数）
	/*public int getCount(String taskId){
		Task task = getTaskById(taskId);
		//2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		task.getExecutionId();
		return 0;
	}*/
	
	public  void TaskRollBack(String taskId){
	    try {
            Map<String, Object> variables;
            // 取得当前任务
            HistoricTaskInstance currTask = historyService
                    .createHistoricTaskInstanceQuery().taskId(taskId)
                    .singleResult();
            // 取得流程实例
            ProcessInstance instance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(currTask.getProcessInstanceId())
                    .singleResult();
            if (instance == null) {
               
                //流程结束
            }
            variables = instance.getProcessVariables();
            // 取得流程定义
            ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (processEngine.getRepositoryService().getProcessDefinition(currTask
                            .getProcessDefinitionId()));
           
            if (definition == null) {
               
                //log.error("流程定义未找到");
                return ;
            }
            // 取得上一步活动
            ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
                    .findActivity(currTask.getTaskDefinitionKey());
            List<PvmTransition> nextTransitionList = currActivity
                    .getIncomingTransitions();
            // 清除当前活动的出口
            List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
            List<PvmTransition> pvmTransitionList = currActivity
                    .getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitionList) {
                oriPvmTransitionList.add(pvmTransition);
            }
            pvmTransitionList.clear();
 
            // 建立新出口
            List<TransitionImpl> newTransitions = new ArrayList<TransitionImpl>();
            for (PvmTransition nextTransition : nextTransitionList) {
                PvmActivity nextActivity = nextTransition.getSource();
                ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition)
                        .findActivity(nextActivity.getId());
                TransitionImpl newTransition = currActivity
                        .createOutgoingTransition();
                newTransition.setDestination(nextActivityImpl);
                newTransitions.add(newTransition);
            }
            // 完成任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(instance.getId())
                    .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
            for (Task task : tasks) {
                taskService.complete(task.getId(), variables);
                historyService.deleteHistoricTaskInstance(task.getId());
            }
            // 恢复方向
            for (TransitionImpl transitionImpl : newTransitions) {
                currActivity.getOutgoingTransitions().remove(transitionImpl);
            }
            for (PvmTransition pvmTransition : oriPvmTransitionList) {
                pvmTransitionList.add(pvmTransition);
            }
 
          
            return ;
        } catch (Exception e) {
           
            return ;
        }
	
	}
	
	
	
	public String getActivityId(String taskId){
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		 String processDefinitionId = task.getProcessInstanceId();
		 String definitionId = task.getProcessDefinitionId();
		 ActivityImpl currentActivity = qureyCurrentTask(processDefinitionId,definitionId);
		 return currentActivity.getId();
	}
	
	
	/**
	 * 工作流实现任意跳转
	 * @param taskId
	 * @param title
	 */
	public void JumpEndActivity(String taskId,String title,Map<String,Object>map){
		 TaskService taskService = processEngine.getTaskService();
		 Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		 if(task!=null){
			 String processDefinitionId = task.getProcessInstanceId();
			 String definitionId = task.getProcessDefinitionId();
			 ActivityImpl currentActivity = qureyCurrentTask(processDefinitionId,definitionId);
			 ActivityImpl targetActivity = queryTargetActivity(definitionId,title);
			   //通过活动可以获得流程 将要出去的路线，只要更改出去的目的Activity ，就可以实现自由的跳转
			    
			List<PvmTransition> outgoingTransitions = currentActivity.getOutgoingTransitions();
			  for (PvmTransition pvmTransition : outgoingTransitions) {
				  TransitionImpl transitionImpl= (TransitionImpl)pvmTransition;
				  transitionImpl.setDestination(targetActivity);
				  
			 }
			  if(map!=null){
				  taskService.complete(task.getId(),map);
			  }else{
				  taskService.complete(task.getId());
			  }
			  
			 
		 }
	   
	}

	
	/**
	 * 根据ActivityId 查询出来想要活动Activity
	 * @param id
	 * @return
	 */
	public ActivityImpl  queryTargetActivity(String depoyId,String id){
			System.out.println("id是"+id);
			RepositoryService repositoryService = processEngine.getRepositoryService();
			ReadOnlyProcessDefinition deployedProcessDefinition = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(depoyId);
	    List<ActivityImpl> activities = (List<ActivityImpl>) deployedProcessDefinition.getActivities();
	    for (ActivityImpl activityImpl : activities) {
			if(activityImpl.getId().equals(id)){
				return activityImpl;
			}
		 }
	    return null;
	}
	
	
	public void  queryAllActivities(){
		
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition deployedProcessDefinition = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition("ziyouliu:1:4");
	    List<ActivityImpl> activities = (List<ActivityImpl>) deployedProcessDefinition.getActivities();
	    for (ActivityImpl activityImpl : activities) {
			System.out.println(activityImpl.getId()+"活动节点的名称:"+activityImpl.getProperty("name"));
			
		}
	}


	/**
	 * 查询当前的活动节点
	 */
	public ActivityImpl qureyCurrentTask(String processInstanceId,String depolyId){
		RuntimeService runtimeService = processEngine.getRuntimeService();
//		String processDefinitionId="ziyouliu:1:4";
	    Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).singleResult();
	    String activityId = execution.getActivityId();
	    ActivityImpl currentActivity = queryTargetActivity(depolyId,activityId);
	    System.out.println(currentActivity.getId()+""+currentActivity.getProperty("name"));
	    return currentActivity;
	}






	//根据任务Id删除流程实例
	public void deleteProcess(String taskId){
		/*taskService.deleteTask(taskId, true);*/
		Task task=getTaskById(taskId);
		if(task!=null){
			String pid=task.getProcessInstanceId();
			runtimeService.deleteProcessInstance(pid,"没有理由");
		}
		
	}

}
