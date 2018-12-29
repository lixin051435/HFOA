package com.hfoa.controller.applyExpense;

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
import java.util.List;
import java.util.Map;
import com.hfoa.common.AnccResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;
import com.hfoa.service.TravelExpes.TravelExpessService;
import com.hfoa.util.WorkflowUtil;


@Controller
@RequestMapping("/applyExpenses")
public class ApplyExpensesController {
	
	@Autowired
	private TravelExpessService travelExpessService;
	
	@Autowired
	private WorkflowUtil workflowUtil;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private  IdentityService identityService;
	
	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	
	
	
	
	@RequestMapping("/createApplyExpense")
	@ResponseBody
	public String createProcess(){
		String src = "";
		Deployment deployment = processEngine.getRepositoryService()
						.createDeployment()
						.name("差旅费")
						.addClasspathResource("activiti/TravelExpnese.bpmn")
						.addClasspathResource("activiti/TravelExpnese.png")
						.deploy();
		//system.out.println("部署ID是"+deployment.getId());
		//system.out.println("部署名称是"+deployment.getName());
		return "部署成功";
	}
	
	
	
	/**
	 * 统计
	 * @param openId
	 * @return
	 */
	@RequestMapping("/countApplyExpense")
	@ResponseBody
	public AnccResult countApplyExpense(String openId){
		
		return AnccResult.ok(travelExpessService.countApplyExpense(openId));
	}
	
	
	
	
	
	//申请差旅
	@RequestMapping("/insertApply")
	@ResponseBody
	public AnccResult insert(ApplyExpensesEntity applyExpensesEntity){
		
		travelExpessService.insertTravelExpenss(applyExpensesEntity);
		
		return AnccResult.ok("success");
	}
	
	
	//员工查询差旅
	@RequestMapping("/searApplyExpense")
	@ResponseBody
	public AnccResult searApplyExpense(String openId){
		List<ApplyExpensesEntity> list = travelExpessService.searApplyExpense(openId);
		
		return AnccResult.ok(list);
	}
	
	
	//修改差旅
	@RequestMapping(value="/updateApplyExpense",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult updateApplyExpense(ApplyExpensesEntity applyExpensesEntity){
		
		//system.out.println("修改数据");
		
		return AnccResult.ok(travelExpessService.updateTravelExpenss(applyExpensesEntity));
	}
	
	
	//领导查询
	@RequestMapping("/selectApprove")
	@ResponseBody
	public AnccResult selectApprove(String openId){
		List<ApplyExpensesEntity> selectApplyExpense = travelExpessService.selectApplyExpense(openId);
		List<ApplyExpensesEntity> list = travelExpessService.selectApplyExpense(openId);
		list.addAll(selectApplyExpense);		
		return AnccResult.ok(list);
	}
	
	//领导审批
	@RequestMapping("/approveApplyExpense")
	@ResponseBody
	public AnccResult approveApplyExpense(String id,String taskId,String result,String comment){
		
		return AnccResult.ok(travelExpessService.approveApplyExpense(id,taskId,result,comment));
	}
	 
	
	
	
	
	
	//员工确认查询
	@RequestMapping("/listApplyExpense")
	@ResponseBody
	public AnccResult listApplyExpense(String openId){
		List<ApplyExpensesEntity> listApplyExpense = travelExpessService.listApplyExpense(openId);
		return AnccResult.ok(listApplyExpense);
	}
	
	//员工确认
	@RequestMapping("/confirmApplyExpense")
	@ResponseBody
	public AnccResult confirmApplyExpense(String id,String cofirm,String taskId){
		travelExpessService.confirmApplyExpense(id,taskId,cofirm);
		return AnccResult.ok("员工确认");
	}
	
	
	//员工状态查询
	@RequestMapping("/statusApply")
	@ResponseBody
	public AnccResult confirmApplyStatus(String openId){
		//system.out.println("查看OPenid是"+openId);
		return AnccResult.ok(travelExpessService.statusApplyExpense(openId));
	}
	
	
	
	
	/*@RequestMapping("/delete")
	@ResponseBody
	public AnccResult deleteApply(String id){
		//system.out.println("id是"+id);
		travelExpessService.deleteTravelExpess(id);
		return AnccResult.ok("删除");
	}*/
	
	
	
	//抄送人查看
	@RequestMapping("/getcCListApplyExpense")
	@ResponseBody
	public AnccResult getcCListApplyExpense(String cCListOpenId,Integer nowPage,Integer pageSize){
		
		return AnccResult.ok(travelExpessService.listcCListApplyExpense(cCListOpenId,nowPage,pageSize));
	}
	
	/**
	 * 抄送人以查看
	 * @param cCListOpenId
	 * @param nowPage
	 * @param pageSize
	 * @return
	 */
	@RequestMapping("/getcClistApplyExpenseState")
	@ResponseBody
	public AnccResult getcClistApplyExpenseState(String cCListOpenId,Integer nowPage,Integer pageSize){
		
		return AnccResult.ok(travelExpessService.getcClistApplyExpenseState(cCListOpenId,nowPage,pageSize));
	}
	
	
	/**
	 * 抄送人搜索
	 * @param cCListOpenId
	 * @param nowPage
	 * @param pageSize
	 * @param beginTime
	 * @param endTime
	 * @param applyMan
	 * @return
	 */
	@RequestMapping("/getcClistApplyExpensesearch")
	@ResponseBody
	public AnccResult getcClistApplyExpensesearch(String cCListOpenId,Integer nowPage,Integer pageSize,String beginTime,String endTime,String applyMan,String state){
		
		
		return AnccResult.ok(travelExpessService.getcClistApplyExpense(cCListOpenId,nowPage,pageSize,beginTime,endTime,applyMan,state));
		
	}
	
	/**
	 * 抄送人点击访问接口 标识加一
	 * @param id
	 * @return
	 */
	@RequestMapping("/ifState")
	@ResponseBody
	public AnccResult ifState(String id){
		
		
		return AnccResult.ok(travelExpessService.ifState(id));
		
	}
	
	
	
	/*public AnccResult getcClistApplyExpenseSearch(){
		
	}
	*/

	//小程序
	@RequestMapping("/searchApplyExpense")
	@ResponseBody
	public AnccResult searchApplyExpense(ApplyExpensesEntity applyExpensesEntity,Integer nowPage,Integer pageSize){
		//system.out.println("查询名是"+applyExpensesEntity);
		return AnccResult.ok(travelExpessService.searchApplyExpense(applyExpensesEntity,nowPage,pageSize));
	}
	
	
	
	//财务调整modelandview
	@RequestMapping("/checkTravel")
	@ResponseBody
	public ModelAndView applyExpense(){
		//system.out.println("查看差旅待登记调整");
		ModelAndView result = new ModelAndView("travel/waitRegisterList");
		return result;
	}
	
	@RequestMapping("/webleaver")
	public ModelAndView leaverYear(){
		//system.out.println("查看年假的网页");
		ModelAndView result = new ModelAndView("leave/index");
		return result;
	}
	
	
	
	//财务审批查看
	@RequestMapping("/financeApplyExpense")
	@ResponseBody
	public Object financeApplyExpense(Integer state, ApplyExpensesEntity applyExpense, HttpServletRequest request){
		//system.out.println("财务审批查看");
		//system.out.println("传过来的参数是"+applyExpense);
		//system.out.println("人是"+applyExpense.getTravelers());
		/*if(applyExpense.getTravelers().equals("")){
			applyExpense.setTravelers(null);
		}*/
		String page = request.getParameter("page");// 第几页
	    String rows = request.getParameter("rows");// 每页多少条
	    int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
	    int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
	    // 每页的开始记录
	    int start = (intPage - 1) * number;
	    List<ApplyExpensesEntity> list_all = new ArrayList<ApplyExpensesEntity>();
	    list_all = travelExpessService.finaceApplyExpense(applyExpense);
	    Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<ApplyExpensesEntity> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	
	
	
	
	
	
	
	
	
	//财务登记
	@RequestMapping("/registerApplyExpense")
	@ResponseBody
	public AnccResult registerApplyExpense(String taskId,String result,ApproveExpensesEntity approveExpensesEntity){
		
		return AnccResult.ok(travelExpessService.registerApplyExpense(taskId,result,approveExpensesEntity));
	}
	
	
	/**
	 * 财务驳回
	 * @param id
	 * @param taskId
	 * @return
	 */
	 @RequestMapping("/financeReviewFail")
	 @ResponseBody
	 public int financeReviewFail(String id,String taskId){
		 
		 return travelExpessService.financeReviewFail(taskId);
	 }
	 

	 
	 /**
	  * 撤销流程
	  * @param taskId 任务Id
	  * @param id 主键
	  * @return
	  */
	 @RequestMapping("/deleteApplyExpense")
	 @ResponseBody
	 public AnccResult deleteApplyExpense(String taskId,String id){
		 
		 return AnccResult.ok(travelExpessService.deleteTravelExpess(taskId,id));
	 }
	 
	 @RequestMapping("/exportApplyExpenses")
	  @ResponseBody
	  public void exportApplyExpenses(HttpServletRequest request, HttpServletResponse response,
	                                  ApplyExpensesEntity applyExpense) {
	    // 获取导出文件夹
	    String path = request.getSession().getServletContext().getRealPath("/");
	    String fileName = "差旅费待登记列表" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xlsx";
	    String filePath = path + "/" + fileName;
	    int flag = travelExpessService.exportApplyExpenses(applyExpense, filePath);
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
	    public  void getDataFromExcel(String filePath)
	    {
	    	filePath = "C:\\b_travelexpenses.xlsx";//差旅
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
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            System.out.println("ID是"+id);
	            
	            cell = row.getCell((short)11);
	            String applyMan = cell.getStringCellValue().toString();
	            System.out.println("申请人"+applyMan);
	            cell = row.getCell((short)12);
	            String approveMan = cell.getStringCellValue().toString();
	            System.out.println("审批人是"+approveMan);
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("user", applyMan);
				String applyId=id;
				String objId="applyExpenses:"+applyId;
				identityService.setAuthenticatedUserId(applyMan);
				runtimeService.startProcessInstanceByKey("applyExpens",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",applyMan,"cltjsq");
				List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
				int j=0;
				for(;j<bids.size();j++){
					System.out.println("bids.get(i)是"+bids.get(j));
					if(bids.get(j)==applyId)
						System.out.println("相等"+j);
						break;
				}
				String taskId=tasks.get(j).getId();
				System.out.println("任务编号："+taskId);
				WorkflowBean workflowBean = new WorkflowBean();
				workflowBean.setTaskId(taskId);
				Map<String,Object> map = new HashMap<>();
				map.put("inputUser", approveMan);
				workflowUtil.completeTask(workflowBean, map);
	           
	        }
	    }
		
	    
	    /**
	     * 领导驳回数据
	     * @param filePath
	     */
	    @RequestMapping("reject")
	    @ResponseBody
	    public String reject(String filePath){
	    	filePath = "C:\\b_travelexpenses.xlsx";//差旅
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
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            System.out.println("ID是"+id);
	            
	            cell = row.getCell((short)11);
	            String applyMan = cell.getStringCellValue().toString();
	            System.out.println("申请人"+applyMan);
	            cell = row.getCell((short)12);
	            String approveMan = cell.getStringCellValue().toString();
	            System.out.println("审批人是"+approveMan);
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("user", applyMan);
				String applyId=id;
				String objId="applyExpenses:"+applyId;
				identityService.setAuthenticatedUserId(applyMan);
				runtimeService.startProcessInstanceByKey("applyExpens",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",applyMan,"cltjsq");
				List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
				int j=0;
				for(;j<bids.size();j++){
					System.out.println("bids.get(i)是"+bids.get(j));
					if(bids.get(j)==applyId)
						System.out.println("相等"+j);
						break;
				}
				String taskId=tasks.get(j).getId();
				System.out.println("任务编号："+taskId);
				WorkflowBean workflowBean = new WorkflowBean();
				workflowBean.setTaskId(taskId);
				Map<String,Object> map = new HashMap<>();
				map.put("inputUser", approveMan);
				workflowUtil.completeTask(workflowBean, map);
				List<Task> tasksApproveman = workflowUtil.getTaskByIds("applyExpens",approveMan,"applyApprove");
				Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasksApproveman);
				for(String Id:busAndTaskId.keySet()){
					String str = Id+"";
					taskId = busAndTaskId.get(str);
					Map<String,Object> rejectMap = new HashMap<>();
					rejectMap.put("user", applyMan);
					rejectMap.put("result", "false");
					workflowBean.setTaskId(taskId);
					workflowUtil.completeTask(workflowBean, rejectMap);
				}
	        }
	        return "SUCCESS";
	   	 
	    }
	   
	
	    
	    //待执行
	    @RequestMapping("excuted")
	    @ResponseBody
	    public String executed(String filePath){
	    	
	    	filePath = "C:\\b_travelexpenses.xlsx";//差旅
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
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            System.out.println("ID是"+id);
	            
	            cell = row.getCell((short)11);
	            String applyMan = cell.getStringCellValue().toString();
	            System.out.println("申请人"+applyMan);
	            cell = row.getCell((short)12);
	            String approveMan = cell.getStringCellValue().toString();
	            System.out.println("审批人是"+approveMan);
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("user", applyMan);
				String applyId=id;
				String objId="applyExpenses:"+applyId;
				identityService.setAuthenticatedUserId(applyMan);
				runtimeService.startProcessInstanceByKey("applyExpens",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",applyMan,"cltjsq");
				List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
				int j=0;
				for(;j<bids.size();j++){
					System.out.println("bids.get(i)是"+bids.get(j));
					if(bids.get(j)==applyId)
						System.out.println("相等"+j);
						break;
				}
				String taskId=tasks.get(j).getId();
				System.out.println("任务编号："+taskId);
				WorkflowBean workflowBean = new WorkflowBean();
				workflowBean.setTaskId(taskId);
				Map<String,Object> map = new HashMap<>();
				map.put("inputUser", approveMan);
				workflowUtil.completeTask(workflowBean, map);
				List<Task> tasksApproveman = workflowUtil.getTaskByIds("applyExpens",approveMan,"applyApprove");
				Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasksApproveman);
				for(String Id:busAndTaskId.keySet()){
					String str = Id+"";
					taskId = busAndTaskId.get(str);
					Map<String,Object> rejectMap = new HashMap<>();
					rejectMap.put("user", applyMan);
					rejectMap.put("result", "true");
					workflowBean.setTaskId(taskId);
					workflowUtil.completeTask(workflowBean, rejectMap);
				}
	        }
	    	
	    	return "SUCCESS";
	    }
	    
	    
	    //待财务审批
	    @RequestMapping("finace")
	    @ResponseBody
	    public String finace(String filePath){
	    	filePath = "C:\\b_travelexpenses.xlsx";//差旅
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
	            Cell cell = row.getCell((short)0);
	            String id = cell.getStringCellValue().toString();
	            System.out.println("ID是"+id);
	            
	            cell = row.getCell((short)11);
	            String applyMan = cell.getStringCellValue().toString();
	            System.out.println("申请人"+applyMan);
	            cell = row.getCell((short)12);
	            String approveMan = cell.getStringCellValue().toString();
	            System.out.println("审批人是"+approveMan);
	            Map<String,Object> activitiMap = new HashMap<>();
	            activitiMap.put("user", applyMan);
				String applyId=id;
				String objId="applyExpenses:"+applyId;
				identityService.setAuthenticatedUserId(applyMan);
				runtimeService.startProcessInstanceByKey("applyExpens",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("applyExpens",applyMan,"cltjsq");
				List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
				int j=0;
				for(;j<bids.size();j++){
					System.out.println("bids.get(i)是"+bids.get(j));
					if(bids.get(j)==applyId)
						System.out.println("相等"+j);
						break;
				}
				String taskId=tasks.get(j).getId();
				System.out.println("任务编号："+taskId);
				WorkflowBean workflowBean = new WorkflowBean();
				workflowBean.setTaskId(taskId);
				Map<String,Object> map = new HashMap<>();
				map.put("inputUser", approveMan);
				workflowUtil.completeTask(workflowBean, map);
				List<Task> tasksApproveman = workflowUtil.getTaskByIds("applyExpens",approveMan,"applyApprove");
				Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasksApproveman);
				for(String Id:busAndTaskId.keySet()){
					String str = Id+"";
					taskId = busAndTaskId.get(str);
					Map<String,Object> rejectMap = new HashMap<>();
					rejectMap.put("user", applyMan);
					rejectMap.put("result", "true");
					workflowBean.setTaskId(taskId);
					workflowUtil.completeTask(workflowBean, rejectMap);
					List<Task> tasksapplyStaff = workflowUtil.getTaskByIds("applyExpens", applyMan,"applyStaff");
					Map<String,String> staffMap = workflowUtil.getTaskAndBussIdByTask(tasksapplyStaff);
					for(String staffId:staffMap.keySet()){
						taskId = staffMap.get(staffId+"");
						workflowUtil.JumpEndActivity(taskId, "finaceApply",null);
					}
				}
	        }
	    	
	    	return "SUCCESS";
	    	
	    	
	    }
	    
	    
	    
	    
	    
}
