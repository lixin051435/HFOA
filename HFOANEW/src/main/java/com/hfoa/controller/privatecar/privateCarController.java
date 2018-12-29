package com.hfoa.controller.privatecar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.common.AnccResult;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.car.CarApplyInfo2;
import com.hfoa.entity.common.DictEntity;
import com.hfoa.entity.common.TaskDTO;
import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.privatecar.PrivatecarinvoiceEntity;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.privatecarservice.PrivateCarService;
import com.hfoa.util.WorkflowUtil;

import jxl.Workbook;




@Controller
@RequestMapping("/privateCar")
public class privateCarController {
	
		@Autowired
		private PrivateCarService privateCarService;
	
		@Autowired
		private IdentityService identityService;
		
		@Autowired
		private RuntimeService runtimeService;
		
		@Autowired
		private WorkflowUtil workflowUtil;
		@Autowired
		private DictManage dictManage;
		@Autowired
		private TaskService taskService;
	
		//部署流程实例
		private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		
		@RequestMapping("/createprivateCar")
		@ResponseBody
		public String createProcess(){
			String src = "";
			Deployment deployment = processEngine.getRepositoryService()
							.createDeployment()
							.name("私车公用")
							.addClasspathResource("activiti/privateCar.bpmn")
							.addClasspathResource("activiti/privateCar.png")
							.deploy();
			System.out.println("部署ID是"+deployment.getId());
			System.out.println("部署名称是"+deployment.getName());
			return "部署成功";
		}
		
		/**
		 * 申请私车公用
		 * @param privateCarEntity
		 * @return
		 */
		@RequestMapping("/insertPrivateCar")
		@ResponseBody
		public AnccResult privateApplication(PrivateCarEntity privateCarEntity){
			Map<String,Object> map = new HashMap<>();
			int flag = privateCarService.insertPrivateCar(privateCarEntity);
			if(flag==1){
				map.put("status", "成功");
				map.put("flag", flag);
				return AnccResult.ok(map);
			}
			map.put("status", "失败");
			map.put("flag", flag);
			return AnccResult.ok(map);
		}
		
		
		/**
		 * 领导驳回员工修改查询
		 * @param openId
		 * @return
		 */
		@RequestMapping("/privateApplytion")
		@ResponseBody
		public AnccResult privateApplytion(String openId){
			
			return AnccResult.ok(privateCarService.privateApplytion(openId));
		}
		
		/**
		 * 员工修改
		 * @param privateCarEntity
		 * @return
		 */
		@RequestMapping("/updatePrivatecar")
		@ResponseBody
		public AnccResult updatePrivatecar(PrivateCarEntity privateCarEntity){
			
			
			return AnccResult.ok(privateCarService.updatePrivateCar(privateCarEntity));
		}
		
		
		/**
		 * 领导查看
		 * @param openId
		 * @return
		 */
		@RequestMapping("/approvemanApplication")
		@ResponseBody
		public AnccResult approvemanApplication(String openId){
			
			return AnccResult.ok(privateCarService.listOpendIdPrivateCar(openId));
		}
		
		
		/**
		 * 用户撤销
		 * @param applyId 主键
		 * @param taskId 任务Id
		 * case 原因
		 * @return
		 */
		@RequestMapping("/deletePrivateCar")
		@ResponseBody
		public AnccResult deletePrivateCarappli(String applyId,String taskId,String cause){
			return AnccResult.ok(privateCarService.deletePrivateCar(applyId,taskId));
		}
		
		
		/**
		 * 领导审批
		 * @param applyId
		 * @param taskId
		 * @param result
		 * @return
		 */
		@RequestMapping("/approvalPrivateCar")
		@ResponseBody
		public AnccResult approvalPrivateCar(String applyId,String taskId,String result,String comment){
			return AnccResult.ok(privateCarService.approvalPrivateCar(applyId, taskId, result,comment));
		}
		
		/**
		 * 员工执行查询
		 * @param openId
		 * @return
		 */
		@RequestMapping("/staffprivateCar")
		@ResponseBody
		public AnccResult staffPrivateCar(String openId){
			
			return AnccResult.ok(privateCarService.staffPrivateCar(openId));
		}
	
		/**
		 * 员工执行
		 * @param applyId
		 * @param taskId
		 * @param staffresult
		 * @return
		 */
		@RequestMapping("/staffPrivateCarApprove")
		@ResponseBody
		public AnccResult staffPrivateCarApprove(String applyId,String taskId,String staffresult){
			
			return AnccResult.ok(privateCarService.staffPrivateCarApprove(applyId, taskId, staffresult));
		}
		
		
		/**
		 * 凭票报销查询
		 * @param openId
		 * @return
		 */
		@RequestMapping("/reimbursementPrivateCar")
		@ResponseBody
		public AnccResult reimbursementPrivateCar(String openId){
			
			return AnccResult.ok(privateCarService.reimbursementPrivateCar(openId));
		}
		
		
		/**
		 * 凭票报销
		 * @return
		 */
		@RequestMapping("/reimbursementPrivateCarApprove")
		@ResponseBody
		public AnccResult reimbursementPrivateCarApprove(PrivatecarinvoiceEntity privatecarinvoiceEntity,String taskIds){
			
			return AnccResult.ok(privateCarService.reimbursementPrivateCarApprove(privatecarinvoiceEntity, taskIds));
		}
		
		
		/**
		 * 财务审批查看
		 * @param openId
		 * @return
		 */
		
		@RequestMapping("/financePrivateCar")
		@ResponseBody
		public AnccResult financePrivateCar(String openId){
			return AnccResult.ok(privateCarService.financePrivateCar(openId));
		}

		
		@RequestMapping("/financePrivatreCartaskId")
		@ResponseBody
		public AnccResult financePrivatreCartaskId(String openId,String applyIds,String canUpdate){
			
			return AnccResult.ok(privateCarService.financePrivatreCartaskId(openId, applyIds,canUpdate));
		}
		
		
		/**
		 * 财务审批
		 * @param taskId
		 * @param applyId
		 * @param finaceresult
		 * @return
		 */
		@RequestMapping("/financePrivateCarApprove")
		@ResponseBody
		public AnccResult financePrivateCarApprove(String taskId,String applyId,String finaceresult,String applyIdinvoice,
				String voucherNum,String paidTime,String comment){
			
			return AnccResult.ok(privateCarService.financePrivateCarApprove(taskId, applyId, finaceresult,applyIdinvoice,voucherNum,paidTime,comment));
		}
		
		
		/**
		 * 凭证号登记
		 * @return
		 */
		@RequestMapping("/privateRegistervoucher")
		@ResponseBody
		public AnccResult privateRegistervoucher(String taskId,String applyId,String voucherNum,String paidTime){
			
			return AnccResult.ok(privateCarService.privateRegistervoucher(taskId,applyId,voucherNum,paidTime));
		}
		
		
		
		
		
		/**
		 * 已登记明细
		 * @return
		 */
		@RequestMapping("/registrationDetails")
		@ResponseBody
		public ModelAndView privateCarregistrationDetails(){
			ModelAndView result = new ModelAndView("private/privateCar");
			return result;
		}
		
		
		/**
		 * 待登记明细
		 * @return
		 */
		@RequestMapping("/detailsWaitingRegistration")
		@ResponseBody
		public ModelAndView privateCardetailsWaitingRegistration(){
			ModelAndView result = new ModelAndView("private/privateCarInvoice");
			return result;
		}
		
		
		
		/**
		 * 待审核明细
		 * @return
		 */
		@RequestMapping("/auditedDetails")
		@ResponseBody
		public ModelAndView privateCarauditedDetails(){
			ModelAndView result = new ModelAndView("private/unprivateCarInvoice");
			return result;
		}
		
		
		/**
		 * 待审核明细
		 * @return
		 */
		@RequestMapping("/uninvoiceDisplay")
		@ResponseBody
		public Object uninvoiceDisplay(HttpServletRequest request){
			String page = request.getParameter("page");// 第几页
		    String rows = request.getParameter("rows");// 每页多少条
		    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		    // 每页的开始记录
		    int start = (intPage - 1) * number;
		    List<PrivatecarinvoiceEntity> list_all = new ArrayList<PrivatecarinvoiceEntity>();
		    list_all = privateCarService.uninvoiceDisplay();
		    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		    int total = list_all.size();
		    List<PrivatecarinvoiceEntity> list = new ArrayList<>();
		    if (start + number <= total)
		    	list.addAll(list_all.subList(start, start + number));
			else
				list.addAll(list_all.subList(start, total));
			jsonMap.put("total", total);// total存放总记录数
			jsonMap.put("rows", list);// rows键，存放每页记录list
			return jsonMap;
		}
		
		/**
		 * 审核查看
		 * @param applyid
		 * @return
		 */
		@RequestMapping("/selectByApplyIds")
		@ResponseBody
		public Object selectByApplyIds(String applyid){
			System.out.println("applyId是"+applyid);
			String openId = "";
			return privateCarService.selectByApplyIds(applyid,openId);
		}
		
		
		/**
		 * 审核通过
		 * @param applyid
		 * @param applyids
		 * @param taskId
		 * @return
		 */
		@RequestMapping("/passPrivateCars")
		@ResponseBody
		public int passPrivateCars(String applyid,String applyids,String taskId){
			
			return privateCarService.passPrivateCars(applyid,applyids,taskId);
		}
		
		
		/**
		 * 统计
		 */
		@RequestMapping("/countPrivate")
		@ResponseBody
		public AnccResult countPrivate(String openId){
			
			return AnccResult.ok(privateCarService.countPrivate(openId));
		}
		
		/**
		 * 
		 * @param applyid
		 * @param taskId
		 * @return
		 */
		@RequestMapping("/backPrivateCars")
		@ResponseBody
		public int backPrivateCars(String applyid,String taskId){
			
			return privateCarService.backPrivateCars(applyid,taskId);
		}
		
		
		@RequestMapping("/searPrivateCar")
		@ResponseBody
		public AnccResult searPrivateCar(PrivateCarEntity privateCarEntity,Integer nowPage,Integer pageSize){
			
			
			return AnccResult.ok(privateCarService.searPrivateCar(privateCarEntity,nowPage,pageSize));
		}
		
		
		
		/**
		 * 待登记明细
		 * @param request
		 * @return
		 */
		@RequestMapping("/unSignInvoiceDisplay")
		@ResponseBody
		public Object unSignInvoiceDisplay(HttpServletRequest request){
			String page = request.getParameter("page");// 第几页
		    String rows = request.getParameter("rows");// 每页多少条
		    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		    // 每页的开始记录
		    int start = (intPage - 1) * number;
		    List<PrivatecarinvoiceEntity> list_all = new ArrayList<PrivatecarinvoiceEntity>();
		    list_all = privateCarService.unSignInvoiceDisplay();
		    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		    int total = list_all.size();
		    List<PrivatecarinvoiceEntity> list = new ArrayList<>();
		    if (start + number <= total)
		    	list.addAll(list_all.subList(start, start + number));
			else
				list.addAll(list_all.subList(start, total));
			jsonMap.put("total", total);// total存放总记录数
			jsonMap.put("rows", list);// rows键，存放每页记录list
			return jsonMap;
			
		}
		
		
		@RequestMapping("/invoiceDisplay")
		@ResponseBody
		public Object invoiceDisplay(HttpServletRequest request){
			String page = request.getParameter("page");// 第几页
		    String rows = request.getParameter("rows");// 每页多少条
		    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		    // 每页的开始记录
		    int start = (intPage - 1) * number;
		    List<PrivatecarinvoiceEntity> list_all = new ArrayList<PrivatecarinvoiceEntity>();
		    list_all = privateCarService.invoiceDisplay();
		    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		    int total = list_all.size();
		    List<PrivatecarinvoiceEntity> list = new ArrayList<>();
		    if (start + number <= total)
		    	list.addAll(list_all.subList(start, start + number));
			else
				list.addAll(list_all.subList(start, total));
			jsonMap.put("total", total);// total存放总记录数
			jsonMap.put("rows", list);// rows键，存放每页记录list
			return jsonMap;
		}
		
		
		@RequestMapping("/selectByApplyIdsChild")
		@ResponseBody
		public Object selectByApplyIdsChild(String applyid){
			
			return privateCarService.selectByApplyIds(applyid);
		}
		
		
		
		@RequestMapping("/updateInvoice")
		@ResponseBody
		public int updateInvoice(HttpSession session,PrivatecarinvoiceEntity privatecarinvoiceEntity){
			privatecarinvoiceEntity.setStatus("已完成");
			String username = (String) session.getAttribute("username");;
			privatecarinvoiceEntity.setApproveMan(username);
			return privateCarService.updateInvoice(privatecarinvoiceEntity);
		}
		
		
		@RequestMapping("/getOneRegister")
		@ResponseBody
		public Object getOneRegister(String applyid){
			
			return privateCarService.getOneRegister(applyid);
		}
		
		
		@RequestMapping("/privateCarInvoiceexportExcel")
		@ResponseBody
		public void privateCarInvoiceexportExcel(HttpServletRequest request, HttpServletResponse response,
				String number){
			
			 // 获取导出文件夹
		    String path = request.getSession().getServletContext().getRealPath("/");
		    String fileName = "私车公用待登记明细" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xlsx";
		    String filePath = path + "/" + fileName;
		    int flag = privateCarService.privateCarInvoiceexportExcel(number, filePath);
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
	     * 读取出filePath中的所有数据信息
	     * @param filePath excel文件的绝对路径
	     * 
	     */
	    @RequestMapping("/getDataFromExcel")
	    @ResponseBody
	    public  void getDataFromExcel(String filePath)
	    {
	         filePath = "C:\\b_privatecar.xlsx";
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
	        
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            
	            //获得一个数字类型的数据
	            cell = row.getCell((short)11);
	            String applyMan  =  cell.getStringCellValue().toString();
	            
	            cell = row.getCell((short)12);
	            String approMan = cell.getStringCellValue().toString();
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("privateCarApply", applyMan);
	          //开启流程
    			String applyId = id+"";
    			String objId="privateCar:"+applyId;
    			identityService.setAuthenticatedUserId(applyMan);
    			runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",applyMan,"privateCarApply");
    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	            int j= 0;
	            for(;j<bids.size();j++){
    				System.out.println("bids.get(i)是"+bids.get(j));
    				if(bids.get(j)==applyId)
    					System.out.println("相等"+j);
    					break;
    			}
    			String taskId=tasks.get(j).getId();
    			System.out.println("任务编号："+taskId);
    			Map<String,Object> map = new HashMap<>();
    			map.put("privateCarApprove", approMan);
    			WorkflowBean workflowBean = new WorkflowBean();
    			workflowBean.setTaskId(taskId);
    			workflowUtil.completeTask(workflowBean, map);
	        }
	    }
		
		
		
	    
	    @RequestMapping("/reject")
	    @ResponseBody
	    public  void reject(String filePath)
	    {
	         filePath = "C:\\b_privatecar.xlsx";
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
	        
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            
	            //获得一个数字类型的数据
	            cell = row.getCell((short)11);
	            String applyMan  =  cell.getStringCellValue().toString();
	            
	            cell = row.getCell((short)12);
	            String approMan = cell.getStringCellValue().toString();
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("privateCarApply", applyMan);
	          //开启流程
    			String applyId = id+"";
    			String objId="privateCar:"+applyId;
    			identityService.setAuthenticatedUserId(applyMan);
    			runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",applyMan,"privateCarApply");
    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	            int j= 0;
	            for(;j<bids.size();j++){
    				System.out.println("bids.get(i)是"+bids.get(j));
    				if(bids.get(j)==applyId)
    					System.out.println("相等"+j);
    					break;
    			}
    			String taskId=tasks.get(j).getId();
    			System.out.println("任务编号："+taskId);
    			Map<String,Object> map = new HashMap<>();
    			map.put("privateCarApprove", approMan);
    			WorkflowBean workflowBean = new WorkflowBean();
    			workflowBean.setTaskId(taskId);
    			workflowUtil.completeTask(workflowBean, map);
	        }
	    }
		
		
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    @RequestMapping("/excue")
	    @ResponseBody
	    public  void excue(String filePath)
	    {
	         filePath = "C:\\b_privatecar.xlsx";
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
	        
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            
	            //获得一个数字类型的数据
	            cell = row.getCell((short)11);
	            String applyMan  =  cell.getStringCellValue().toString();
	            
	            cell = row.getCell((short)12);
	            String approMan = cell.getStringCellValue().toString();
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("privateCarApply", applyMan);
	          //开启流程
    			String applyId = id+"";
    			String objId="privateCar:"+applyId;
    			identityService.setAuthenticatedUserId(applyMan);
    			runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",applyMan,"privateCarApply");
    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	            int j= 0;
	            for(;j<bids.size();j++){
    				System.out.println("bids.get(i)是"+bids.get(j));
    				if(bids.get(j)==applyId)
    					System.out.println("相等"+j);
    					break;
    			}
    			String taskId=tasks.get(j).getId();
    			System.out.println("任务编号："+taskId);
    			Map<String,Object> map = new HashMap<>();
    			map.put("privateCarApprove", approMan);
    			WorkflowBean workflowBean = new WorkflowBean();
    			workflowBean.setTaskId(taskId);
    			workflowUtil.completeTask(workflowBean, map);
    			List<Task> tasksapprove = workflowUtil.getTaskByIds("privateCar",approMan,"privateCarApprove");//领导审批
    			Map<String, String> taskAndBussIdByTask = workflowUtil.getTaskAndBussIdByTask(tasksapprove);
    			for(String approveId:taskAndBussIdByTask.keySet()){
    				taskId = taskAndBussIdByTask.get(approveId+"");
    				workflowBean.setTaskId(taskId);
    				map.put("privateCarStaff", applyMan);
    				map.put("result", "true");
    				workflowUtil.completeTask(workflowBean, map);//到员工
    				/*List<Task> remibuTask = workflowUtil.getTaskByIds("privateCar", applyMan, "privateCarStaff");//员工执行
    				Map<String, String> remiMap = workflowUtil.getTaskAndBussIdByTask(remibuTask);
    				for(String remibId:remiMap.keySet()){
    					taskId = remiMap.get(remibId+"");
    					workflowBean.setTaskId(taskId);
    					map.put("privateCarReimbursement", applyMan);
    					map.put("staffresult", "true");
    					workflowUtil.completeTask(workflowBean, map);//频漂
    				}
    				*/
    			}
    			
    			
    			
	        }
	    }
	    
	    
	    
	    @RequestMapping("/finace")
	    @ResponseBody
	    public  void finace(String filePath) throws InterruptedException
	    {
	         filePath = "C:\\b_privatecar.xlsx";
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
	        
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	            Cell cell = row.getCell((short)9);
	            if(cell!=null){
	            	 String id = String.valueOf(cell.getStringCellValue());
	 	            String[] ids = id.split(",");
	 	            for(int z=0;z<ids.length;z++){
	 	            	PrivateCarEntity privateCar = privateCarService.getPrivateCar(ids[z]);
	 	            	if(privateCar!=null){
	 	            		 Map<String,Object> activitiMap = new HashMap<>();
	 	 	 	            activitiMap.put("privateCarApply", privateCar.getApplyMan());
	 	 	 	          //开启流程
	 	 	     			String applyId = id+"";
	 	 	     			String objId="privateCar:"+applyId;
	 	 	     			identityService.setAuthenticatedUserId(privateCar.getApplyMan());
	 	 	     			runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
	 	 	     			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",privateCar.getApplyMan(),"privateCarApply");
	 	 	     			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	 	 	 	            int j= 0;
	 	 	 	            for(;j<bids.size();j++){
	 	 	     				System.out.println("bids.get(i)是"+bids.get(j));
	 	 	     				if(bids.get(j)==applyId)
	 	 	     					System.out.println("相等"+j);
	 	 	     					break;
	 	 	     			}
	 	 	     			String taskId=tasks.get(j).getId();
	 	 	     			System.out.println("任务编号："+taskId);
	 	 	     			Map<String,Object> map = new HashMap<>();
	 	 	     			map.put("privateCarApprove", privateCar.getApproveMan());
	 	 	     			WorkflowBean workflowBean = new WorkflowBean();
	 	 	     			workflowBean.setTaskId(taskId);
	 	 	     			workflowUtil.completeTask(workflowBean, map);
	 	 	     			List<Task> tasksapprove = workflowUtil.getTaskByIds("privateCar",privateCar.getApproveMan(),"privateCarApprove");//领导审批
	 	 	     			Map<String, String> taskAndBussIdByTask = workflowUtil.getTaskAndBussIdByTask(tasksapprove);
	 	 	     			for(String approveId:taskAndBussIdByTask.keySet()){
	 	 	     				taskId = taskAndBussIdByTask.get(approveId+"");
	 	 	     				workflowBean.setTaskId(taskId);
	 	 	     				map.put("privateCarStaff", privateCar.getApplyMan());
	 	 	     				map.put("result", "true");
	 	 	     				workflowUtil.completeTask(workflowBean, map);//到员工
	 	 	     				List<Task> remibuTask = workflowUtil.getTaskByIds("privateCar", privateCar.getApplyMan(), "privateCarStaff");//员工执行
	 	 	     				Map<String, String> remiMap = workflowUtil.getTaskAndBussIdByTask(remibuTask);
	 	 	     				for(String remibId:remiMap.keySet()){
	 	 	     					taskId = remiMap.get(remibId+"");
	 	 	     					workflowBean.setTaskId(taskId);
	 	 	     					map.put("privateCarReimbursement", privateCar.getApplyMan());
	 	 	     					map.put("staffresult", "true");
	 	 	     					workflowUtil.completeTask(workflowBean, map);//频漂
	 	 	     					List<Task> taskByIds = workflowUtil.getTaskByIds("privateCar",privateCar.getApplyMan(), "privateCarReimbursement");
	 	 	    					Map<String, String> taskAndBussIdByTask2 = workflowUtil.getTaskAndBussIdByTask(taskByIds);
	 	 	    					for(String tid:taskAndBussIdByTask2.keySet()){
	 	 	    						taskId = taskAndBussIdByTask2.get(tid+"");
	 	 	    						workflowBean.setTaskId(taskId);
	 	 	    						workflowUtil.completeTask(workflowBean, map);
	 	 	    					}
	 	 	     				}
	 	 	     				
	 	 	     			}
	 	            	}
	 	     			
	 	            }
	 	            
	            }
    			
	        }
	    }
	    
		
	    @RequestMapping("/finacereje")
	    @ResponseBody
	    public  void rejecst(String filePath)
	    {
	         filePath = "C:\\b_privatecar.xlsx";
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
	        
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            
	            //获得一个数字类型的数据
	            cell = row.getCell((short)11);
	            String applyMan  =  cell.getStringCellValue().toString();
	            
	            cell = row.getCell((short)12);
	            String approMan = cell.getStringCellValue().toString();
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("privateCarApply", applyMan);
	          //开启流程
    			String applyId = id+"";
    			String objId="privateCar:"+applyId;
    			identityService.setAuthenticatedUserId(applyMan);
    			runtimeService.startProcessInstanceByKey("privateCar",objId,activitiMap);
    			List<Task> tasks = workflowUtil.getTaskByIds("privateCar",applyMan,"privateCarApply");
    			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
	            int j= 0;
	            for(;j<bids.size();j++){
    				System.out.println("bids.get(i)是"+bids.get(j));
    				if(bids.get(j)==applyId)
    					System.out.println("相等"+j);
    					break;
    			}
    			String taskId=tasks.get(j).getId();
    			System.out.println("任务编号："+taskId);
    			Map<String,Object> map = new HashMap<>();
    			map.put("privateCarApprove", approMan);
    			WorkflowBean workflowBean = new WorkflowBean();
    			workflowBean.setTaskId(taskId);
    			workflowUtil.completeTask(workflowBean, map);
    			List<Task> tasksapprove = workflowUtil.getTaskByIds("privateCar",approMan,"privateCarApprove");//领导审批
    			Map<String, String> taskAndBussIdByTask = workflowUtil.getTaskAndBussIdByTask(tasksapprove);
    			for(String approveId:taskAndBussIdByTask.keySet()){
    				taskId = taskAndBussIdByTask.get(approveId+"");
    				workflowBean.setTaskId(taskId);
    				map.put("privateCarStaff", applyMan);
    				map.put("result", "true");
    				workflowUtil.completeTask(workflowBean, map);//到员工
    				List<Task> remibuTask = workflowUtil.getTaskByIds("privateCar", applyMan, "privateCarStaff");//员工执行
    				Map<String, String> remiMap = workflowUtil.getTaskAndBussIdByTask(remibuTask);
    				for(String remibId:remiMap.keySet()){
    					taskId = remiMap.get(remibId+"");
    					workflowBean.setTaskId(taskId);
    					map.put("privateCarReimbursement", applyMan);
    					map.put("staffresult", "true");
    					workflowUtil.completeTask(workflowBean, map);//频漂
    					List<Task> taskByIds = workflowUtil.getTaskByIds("privateCar",applyMan, "privateCarReimbursement");
    					Map<String, String> taskAndBussIdByTask2 = workflowUtil.getTaskAndBussIdByTask(taskByIds);
    					for(String tid:taskAndBussIdByTask2.keySet()){
    						taskId = taskAndBussIdByTask2.get(tid+"");
    						workflowBean.setTaskId(taskId);
    						workflowUtil.completeTask(workflowBean, map);
    						List<Task> taskByIds2 = workflowUtil.getTaskByIds("privateCar", "privateCarFinance");
    						Map<String, String> taskAndBussIdByTask3 = workflowUtil.getTaskAndBussIdByTask(taskByIds2);
    						for(String ids:taskAndBussIdByTask3.keySet()){
    							taskId = taskAndBussIdByTask3.get(ids+"");
    							workflowBean.setTaskId(taskId);
    							map.put("finaceresult", "false");
    							map.put("privateCarReimbursement", applyMan);
    							workflowUtil.completeTask(workflowBean, map);
    						}
    						
    					}
    					
    				}
    				
    				
    			}
    			
    			
    			
	        }
	    }
		
	//财务获取所有私车申请信息
    @RequestMapping("/displayAll")
	@ResponseBody
	public Object displayAll(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
	    String rows = request.getParameter("rows");// 每页多少条
	    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
	    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
	    // 每页的开始记录
	    int start = (intPage - 1) * number;
	    List<PrivateCarEntity> list = new ArrayList<PrivateCarEntity>();
	    list = privateCarService.allApplyDisplay(start,number);
	    for (PrivateCarEntity privateCarEntity : list) {
			if("0".equals(privateCarEntity.getIfBefore())){
				privateCarEntity.setIfBefore("否");
			}else{
				privateCarEntity.setIfBefore("是");
			}
			if("领导审批".equals(privateCarEntity.getStatus())){
				privateCarEntity.setStatus("待审批");
			}
			PrivatecarinvoiceEntity privatecar=privateCarService.getbyLikeApplyId("%"+privateCarEntity.getApplyId()+"%");
			if(privatecar!=null){
				privateCarEntity.setSum(privatecar.getSum());
				privateCarEntity.setSubmitTime(privatecar.getApplyTime());
				privateCarEntity.setPaidTime(privatecar.getPaidTime());
				privateCarEntity.setPaidMan(privatecar.getApproveMan());
				privateCarEntity.setVoucherNum(privatecar.getVoucherNum());
			}
	    }
	    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = privateCarService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
    //查询私车申请信息
    @RequestMapping("/displaySearch")
	@ResponseBody
	public Object displaySearch(HttpServletRequest request,PrivateCarEntity applyinfo){
		String page = request.getParameter("page");// 第几页
	    String rows = request.getParameter("rows");// 每页多少条
	    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
	    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
	    // 每页的开始记录
	    int start = (intPage - 1) * number;
	    List<PrivateCarEntity> list = new ArrayList<PrivateCarEntity>();
	    list = privateCarService.allApplySearchDisplay(applyinfo,start,number);
	    for (PrivateCarEntity privateCarEntity : list) {
			if("0".equals(privateCarEntity.getIfBefore())){
				privateCarEntity.setIfBefore("否");
			}else{
				privateCarEntity.setIfBefore("是");
			}
			PrivatecarinvoiceEntity privatecar=privateCarService.getbyLikeApplyId(privateCarEntity.getApplyId());
			if(privatecar!=null){
				privateCarEntity.setSum(privatecar.getSum());
				privateCarEntity.setSubmitTime(privatecar.getApplyTime());
				privateCarEntity.setPaidTime(privatecar.getPaidTime());
				privateCarEntity.setPaidMan(privatecar.getApproveMan());
				privateCarEntity.setVoucherNum(privatecar.getVoucherNum());
			}
			if("领导审批".equals(privateCarEntity.getStatus())){
				privateCarEntity.setStatus("待审批");
			}
	    }
	    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = privateCarService.getAllSearchCount(applyinfo);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
  //获取预置路线
    @RequestMapping("/getway")
	@ResponseBody
	public Object getway(){
    	String  type = Constants.COMMON_LOCATIONS;//常用地点
    	List<DictEntity> byNodeType = dictManage.getByNodeType(type);
    	for (DictEntity dictEntity : byNodeType) {
    		dictEntity.setId(dictEntity.getId()+"-"+dictEntity.getInfo());
		}
    	return byNodeType;
	}
   
  //显示所有申请公车已完成信息（转换）
	@RequestMapping("/showPrivateApplyInfo")
	@ResponseBody
	public ModelAndView showPrivateApplyInfo(){
		ModelAndView result=new ModelAndView("private/privateCarData");
		return result;
	}    
	//财务添加申请信息
	@RequestMapping("/SaveNew")
	@ResponseBody
	public Object SaveNew(PrivateCarEntity privateCarEntity){
		if(!"-".equals(privateCarEntity.getPassAddress())&&privateCarEntity.getPassAddress()!=null){
			String address=privateCarEntity.getPassAddress().split("-")[0];
			String length=privateCarEntity.getPassAddress().split("-")[1];
		String passaddress="[";
		String[] split = address.split(",");
		String[] split2 = length.split(",");
		for(int i=0;i<split.length;i++){
			if("".equals(split[i])){
				split[i]="";
			}
			if("".equals(split2[i])){
				split2[i]="0";
			}
			passaddress+="{'addressName':'"+split[i]+"','addressValue':'"+split2[i]+"'}"+",";
		}
		passaddress=passaddress.substring(0, passaddress.length()-1)+"]";
		privateCarEntity.setPassAddress(passaddress.replace("'", "\""));
		}else{
			privateCarEntity.setPassAddress("[]");
		}
		int returnresult=privateCarService.SaveNew(privateCarEntity);
    	return returnresult;
	}	
	
	//删除私车申请信息
	 @RequestMapping({"/deleteApplyInfo"})
	 @ResponseBody
	 public Object deleteApplyInfo(String ApplyId)
	  {
		List<Task> task0 =new ArrayList<>();
		List<Task> task1 =workflowUtil.getTaskByIds("privateCar", "privateCarApply");
		List<Task> task2 =workflowUtil.getTaskByIds("privateCar", "privateCarApprove");
		List<Task> task3 =workflowUtil.getTaskByIds("privateCar", "privateCarStaff");
		List<Task> task4 =workflowUtil.getTaskByIds("privateCar", "privateCarReimbursement");
		List<Task> task5 =workflowUtil.getTaskByIds("privateCar", "privateCarFinance");
		task0.addAll(task1);
		task0.addAll(task2);
		task0.addAll(task3);
		task0.addAll(task4);
		task0.addAll(task5); 
		 
		 PrivatecarinvoiceEntity carinvoice = privateCarService.getbyLikeApplyId("%" + ApplyId + "%");
		 PrivateCarEntity one = privateCarService.getPrivateCar(ApplyId);
		 if(carinvoice!=null){
			  if(carinvoice.getApplyIds().split(",").length==1){
				  privateCarService.deleteByApplyid(carinvoice.getApplyId());
			  }else if(carinvoice.getApplyIds().split(",").length>1){
				  String applyIds="";
				  if(carinvoice.getApplyIds().split(",")[0].equals(ApplyId)){
					  applyIds=carinvoice.getApplyIds().replace(ApplyId+",","");
				  }else{
					  applyIds=carinvoice.getApplyIds().replace(","+ApplyId,"");
				  }
				  carinvoice.setApplyIds(applyIds);
				  carinvoice.setSum(String.valueOf(Double.parseDouble(carinvoice.getSum())-one.getSureLength()));
				  privateCarService.updateInvoice(carinvoice);
//				  privateCarInvoiceDAO.updateEntity(carinvoice.getApplyId(), carinvoice.getApplyMan(), carinvoice.getApproveMan(), carinvoice.getApplyTime(), carinvoice.getSum(), carinvoice.getSureLength(), carinvoice.getVoucherNum(), carinvoice.getStatus(), carinvoice.getPaidTime(), carinvoice.getApplyIds());
			  }
		}
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(task0);
		List<PrivateCarEntity> privateList= privateCarService.findTasksByApplyId(busAndTaskId.keySet());
		for (PrivateCarEntity privateCarEntity : privateList) {
			String str = privateCarEntity.getApplyId()+"";
			String taskId=busAndTaskId.get(str);
			if(privateCarEntity.getApplyId().equals(ApplyId)&&taskId!=null&&!"null".equals(taskId)){
				workflowUtil.deleteProcess(taskId);
			}
		}
	   Integer result = privateCarService.deleteByApplyId(ApplyId);
	   return result;
	 }
	//根据预置路线id获取具体信息
	 @RequestMapping({"/getWayByDict"})
	 @ResponseBody
	 public Object getWayByDict(String id)
	  {
	   DictEntity nodeInfo = dictManage.getNodeInfo(id);
	   String result = nodeInfo.getText();
	   return result;
	 }
	 //财务修改私车申请信息
	 @RequestMapping({"/updateNew"})
	 @ResponseBody
	 public Object updateNew(PrivateCarEntity privateCarEntity){
		if(!"-".equals(privateCarEntity.getPassAddress())&&privateCarEntity.getPassAddress()!=null){
			String address=privateCarEntity.getPassAddress().split("-")[0];
			String length=privateCarEntity.getPassAddress().split("-")[1];
		String passaddress="[";
		String[] split = address.split(",");
		String[] split2 = length.split(",");
		for(int i=0;i<split.length;i++){
			if("".equals(split[i])){
				split[i]="";
			}
			if("".equals(split2[i])){
				split2[i]="0";
			}
			passaddress+="{'addressName':'"+split[i]+"','addressValue':'"+split2[i]+"'}"+",";
		}
		passaddress=passaddress.substring(0, passaddress.length()-1)+"]";
		privateCarEntity.setPassAddress(passaddress.replace("'", "\""));
		}else{
			privateCarEntity.setPassAddress("[]");
		}
	   return privateCarService.updateNew(privateCarEntity);
	 }
	 //批量事后登记
	 @RequestMapping({"/performApplyInfo"})
	 @ResponseBody
	 public Object performApplyInfo(String ApplyIds,String subtime,String sum,String applyman){
	   return privateCarService.perform(ApplyIds,subtime,sum,applyman);
	 }
	 //批量报销
	 @RequestMapping({"/registApplyInfo"})
	 @ResponseBody
	 public Object registApplyInfo(String ApplyIds,String registtime,String sum,String registman,String vouchernum,String subtime,String applyman){
	   return privateCarService.reimbursement(ApplyIds,registtime,sum,registman,vouchernum,subtime,applyman);
	 }
	 
}
