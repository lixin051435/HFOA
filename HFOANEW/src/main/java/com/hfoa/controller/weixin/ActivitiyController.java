package com.hfoa.controller.weixin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;


@Controller
@RequestMapping("/Activiti")
public class ActivitiyController {
	//部署流程实例
	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@RequestMapping("/createLeave")
	@ResponseBody
	public String createProcess(){
		String src = "";
		Deployment deployment = processEngine.getRepositoryService()
						.createDeployment()
						.name("业务招待")
						.addClasspathResource("activiti/Businesshospitality.bpmn")
						.addClasspathResource("activiti/Businesshospitality.png")
						.deploy();
		System.out.println("部署ID是"+deployment.getId());
		System.out.println("部署名称是"+deployment.getName());
		return "部署成功";
	}
	
	@RequestMapping("/deployStart")
	@ResponseBody
	public void deployStart(){
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
				.startProcessInstanceByKey("applyExpens");
		
		System.out.println("流程实例ID:"+pi.getId());//流程实例ID    
		System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID
		
	}
	
	@RequestMapping("/getName")
	@ResponseBody
	public void createTaskquery(){
		processEngine.getTaskService().complete("62501");
		System.out.println("完成");
		
	}
	
	
	
	
	
	
	 
}
