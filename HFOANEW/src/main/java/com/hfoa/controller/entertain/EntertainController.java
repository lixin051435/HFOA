package com.hfoa.controller.entertain;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.common.AnccResult;
import com.hfoa.dao.entertain.EntertainAnnualBudgetMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.entertain.EntertainAnnualInfoDTO;
import com.hfoa.entity.entertain.Entertainannualbudget;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainhospitality;
import com.hfoa.entity.entertain.EntertainhospitalitySubsidiary;
import com.hfoa.entity.entertain.Entertaininvoiceunit;
import com.hfoa.entity.entertain.Entertainobjecttype;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.service.entertain.EntertainService;
import com.hfoa.util.TimeUtil;
import com.hfoa.util.WorkflowUtil;






@Controller
@RequestMapping("/entertain")
public class EntertainController {
	
	@Autowired
	private EntertainService entertainService;
	
	@Autowired
	private  IdentityService identityService;
	
	@Autowired
	private WorkflowUtil workflowUtil;
	
	
	@Autowired
	private RuntimeService runtimeService;
	
	//部署流程实例
			private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			
			@RequestMapping("/createEntertain")
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
			
	
	/**
	 * 事前申请登记
	 * @param entertainapplyinfo
	 * @return
	 */
	@RequestMapping("/insertEntertain")
	@ResponseBody
	public AnccResult Application(Entertainapplyinfo entertainapplyinfo){
		int i = entertainService.insertEntertain(entertainapplyinfo);
		Map<String,Object> map = new HashMap<>();
		if(i==1){
			map.put("status", "成功");
			map.put("data", i);
		}else{
			map.put("status", "失败");
			map.put("data", i);
		}
		return AnccResult.ok(map);
	}
	
	
	/**
	 * 业务招待统计
	 * @param openId
	 * @return
	 */
	@RequestMapping("/countEntertain")
	@ResponseBody
	public AnccResult countEntertain(String openId){
		
		return AnccResult.ok(entertainService.countEntertain(openId));
	}
	
	
	
	
	/**
	 * 用户修改登记信息
	 * @param entertainapplyinfo
	 * @return
	 */
	@RequestMapping("/updateEntertain")
	@ResponseBody
	public AnccResult editEntertain(Entertainapplyinfo entertainapplyinfo,Entertainregisterinfo entertainregisterinfo){
	
		int flag = entertainService.updateEntertain(entertainapplyinfo,entertainregisterinfo);
		Map<String,Object>map = new HashMap<>(); 
		if(flag==1){
			map.put("status","成功");
			map.put("data", flag);
		}else{
			map.put("status", "失败");
			map.put("data", flag);
			
		}
		return AnccResult.ok(map);
	}
	
	
	/**
	 * 领导驳回修改查看
	 * @param openId
	 * @return
	 */
	@RequestMapping("/openIdbusinessapply")
	@ResponseBody
	public AnccResult openIdbusinessapply(String openId){
		
		return AnccResult.ok(entertainService.openIdbusinessapply(openId));
	}
	
	/**
	 * 员工查询事后登记
	 * @param openId
	 * @return
	 */
	@RequestMapping("/openIdEntertain")
	@ResponseBody
	public AnccResult openIdEntertain(String openId){
		
		return AnccResult.ok(entertainService.openIdEntertain(openId));
	}
	
	
	
	/**
	 * 用户撤销
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteEntertain")
	@ResponseBody
	public AnccResult deleteEntertain(int id,String taskId){
		
		return AnccResult.ok(entertainService.deleteEntertain(id,taskId));
	}
	
	
	
	
	/**
	 * 领导审批查看
	 * @param openId
	 * @return
	 */
	@RequestMapping("/listApproval")
	@ResponseBody
	public AnccResult approveEntertainlist(String openId){
		System.out.println("领导审批人查看");
		List<Entertainapplyinfo> list = entertainService.listApproveEntertainAppliyInfo(openId);
		return AnccResult.ok(list);
	}
	
	
	

	
	/**
	 * 领导审批
	 * @return
	 */
	@RequestMapping("/approveEntetain")
	@ResponseBody
	public AnccResult approveEntertain(String id,String taskId,String result,String comment){
		
		return AnccResult.ok(entertainService.approveEntertainApplyinfo(Integer.parseInt(id), taskId, result,comment));
	}
	
	
	/**
	 * 查看状态
	 * @param openId
	 * @return
	 */
	@RequestMapping("/statusEntertain")
	@ResponseBody
	public AnccResult statusEntertain(String openId){
		
		return AnccResult.ok(entertainService.statusEntertainApplyInfo(openId));
	}
	
	
	/**
	 * 事后登记
	 * @return
	 */
	@RequestMapping("/postregistration")
	@ResponseBody
	public AnccResult postregistration(Entertainregisterinfo entertainregisterinfo,String taskId){
		
		return AnccResult.ok(entertainService.postregistration(entertainregisterinfo, taskId));
	}
	
	
	
	/**
	 * 财务审批查看
	 * @param openId
	 * @return
	 */
	@RequestMapping("/finaceEntertain")
	@ResponseBody
	public AnccResult finaceEntertain(String openId){
		
		return AnccResult.ok(entertainService.finaceEntertainApplyinfo(openId));
	}
	
	/**
	 * 财务审批登记
	 * @param entertainregisterinfo
	 * @param id
	 * @param taskId
	 * @param result
	 * @return
	 */
	@RequestMapping("/finaceEntertainApprove") 
	@ResponseBody
	public AnccResult finaceEntertainApprove(Entertainregisterinfo entertainregisterinfo,String id,String taskId,String result,String comment){
		
		return AnccResult.ok(entertainService.finaceEntertainApprove(entertainregisterinfo, Integer.parseInt(id), taskId, result,comment));
	}
	
	
	@RequestMapping("/searchEntertainApprove")
	@ResponseBody
	public AnccResult searchEntertainApprove(Entertainapplyinfo entertainapplyinfo,String openId,Integer nowPage,Integer pageSize){
		
		return AnccResult.ok(entertainService.searchEntertainApprove(entertainapplyinfo, openId,nowPage,pageSize));
	}
	
	
	
	/**********************************************部门剩余金额****************************************************************************/
	// 根据部门获取该部门剩余的金额
	@RequestMapping(value = "/getLastSum")
	@ResponseBody
	public Object getLastSum(String department,String openId) {
		
		return entertainService.getLastSum(department,openId);
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
	// 获取招待客户名称 √
	@RequestMapping(value = "/getType")
	@ResponseBody
	public List<Entertainobjecttype> getType() {
		List<Entertainobjecttype> type = entertainService.getType();
		List<Entertainobjecttype> realList=removeDuplicate1(type);
		return realList;
	}
	
	// 获取发票出具单位名称
    @RequestMapping(value = "/getInvoiceUnitType")
	@ResponseBody
	public List<Entertaininvoiceunit> getInvoiceUnitType() {
		return entertainService.getInvoiceUnitType();
	}
    
    
    /*************************************web端代码************************************************************************/
    
    /**
     * 待审核
     * @param request
     * @return
     */
    @RequestMapping("/wRGetAllEntertain")
    @ResponseBody
    public Object wRGetAllEntertain(HttpServletRequest request){
    	String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<Entertainapplyinfo> list_all = entertainService.wRGetAllEntertain(start, number);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<Entertainapplyinfo> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
    	return jsonMap;
    }
    
    
    @RequestMapping("/registerDetail")
    @ResponseBody
    public Object  registerDetail(String number,String taskId){
    	System.out.println("任务id是"+taskId);
    	
    	System.out.println("查询的数据是"+entertainService.registerDetail(number,taskId));
    	
    	return entertainService.registerDetail(number,taskId);
    }
    
    
    /**
     * 财务通过
     * @param number
     * @param taskId
     * @return
     */
    
    @RequestMapping("/approveX")
    @ResponseBody
    public int approveX(String number,String taskId){
    	System.out.println("确认登记");
    	System.out.println("taskId是"+taskId);
    	
    	
    	return entertainService.approveX(number, taskId);
    }
    
    
    /**
     * 财务驳回
     * @param number
     * @param taskId
     * @return
     */
    
    @RequestMapping("/notThrough")
    @ResponseBody
    public int notThrough(String number,String taskId){
    	
    	System.out.println("驳回");
    	return entertainService.notThrough(number, taskId);
    }
    
    
    
    /**
     * 待审核事后登记
     * @return
     */
    @RequestMapping("/Entertain")
    @ResponseBody
    public ModelAndView Entertain(){
    	ModelAndView result = new ModelAndView("entertain/unapproved");
    	return result;
    }
    
    
    /**
     * 已审核招待明细
     * @return
     */
    @RequestMapping("/approveEntertainWeb")
    @ResponseBody
    public ModelAndView approveEntertainWeb(){
    	ModelAndView result = new ModelAndView("entertain/approved");
    	return result;
    }
    
    /**
     * 已驳回招待明细
     * @return
     */
    @RequestMapping("/allenterEntertainWeb")
    @ResponseBody
    public ModelAndView allenterEntertainWeb(){
    	ModelAndView result = new ModelAndView("entertain/allentertain");
    	return result;
    	
    }
    
    /**
     * 已审核
     * @param request
     * @return
     */
    @RequestMapping("/wGetAllApproved")
    @ResponseBody
    public Object wGetAllApproved(HttpServletRequest request){
    	String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<Entertainapplyinfo> list_all = entertainService.wGetAllApproved();
		System.out.println("list_all是"+list_all);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<Entertainapplyinfo> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    System.out.println("list是"+list);
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
	    return jsonMap;
		
    }
    
    /**
     * 已驳回招待明细
     * @param request
     * @return
     */
    @RequestMapping("/wRGetRegisterEntertain")
    @ResponseBody
    public Object wRGetRegisterEntertain(HttpServletRequest request){
    	String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<Entertainapplyinfo> list_all = entertainService.wRGetRegisterEntertain();
		System.out.println("list_all是"+list_all);
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
	    int total = list_all.size();
	    List<Entertainapplyinfo> list = new ArrayList<>();
	    if (start + number <= total)
	      list.addAll(list_all.subList(start, start + number));
	    else
	      list.addAll(list_all.subList(start, total));
	    System.out.println("list是"+list);
	    jsonMap.put("total", total);// total存放总记录数
	    jsonMap.put("rows", list);// rows键，存放每页记录list
	    return jsonMap;
    }
    
    
    
    /**
     * 凭证号查询
     * @param number
     * @return
     */
    @RequestMapping("/applyDetail")
    @ResponseBody
    public Object applyDetail(String number){
    	
    	return entertainService.getapplyDetail(number);
    }
    
    /**
     * 凭证号登记
     * @param number
     * @param paidTime
     * @param voucherNum
     * @return
     */
    @RequestMapping("/insertAllVoucherNum")
    @ResponseBody
    public int insertAllVoucherNum(String number,String paidTime,String voucherNum){
    	System.out.println("登记凭证号");
    	System.out.println("number是:"+number);
    	System.out.println("painTime是"+paidTime);
    	System.out.println("vocherNum是"+voucherNum);
    	return entertainService.insertAllVoucherNum(number, paidTime, voucherNum);
    }
    
    
    /**
     * 查询
     * @param number
     * @return
     */
    @RequestMapping("/getOneRegister")
    @ResponseBody
	public Object getOneRegister(String number){
		return entertainService.getOneRegister(number);
	}
    
    
    
    @RequestMapping("/wGetSearchApprovedByPage")
    @ResponseBody
    public Object wGetSearchApprovedByPage(HttpServletRequest request,Entertainapplyinfo entertainapplyinfo){
    	System.out.println("参数"+entertainapplyinfo);
    	String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		
		Entertainapplyinfo enter = new Entertainapplyinfo();
		if(entertainapplyinfo.getDepartment()!=null&&!entertainapplyinfo.getDepartment().equals("")){
			enter.setDepartment(entertainapplyinfo.getDepartment());
		}
		if(entertainapplyinfo.getManager()!=null&&!entertainapplyinfo.getManager().equals("")){
			enter.setManager(entertainapplyinfo.getManager());
		}
		if(entertainapplyinfo.getStartTime()!=null&&!entertainapplyinfo.getStartTime().equals("")){
			enter.setStartTime(entertainapplyinfo.getStartTime());
		}
		if(entertainapplyinfo.getEndTime()!=null&&!entertainapplyinfo.getStartTime().equals("")){
			enter.setEndTime(entertainapplyinfo.getEndTime());
		}
		if(entertainapplyinfo.getInvoiceNumber()!=null&&!entertainapplyinfo.getInvoiceNumber().equals("")){
			enter.setInvoiceNumber(entertainapplyinfo.getInvoiceNumber());
		}
		if(entertainapplyinfo.getInvoiceSum()!=null&&!entertainapplyinfo.getInvoiceSum().equals("")){
			enter.setInvoiceSum(entertainapplyinfo.getInvoiceSum());
		}
		
		
		List<Entertainapplyinfo> list_all = entertainService.wgtServApply(enter);
		
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = list_all.size();
	    List<Entertainapplyinfo> list = new ArrayList<>();
	    if (start + number <= total)
	    	list.addAll(list_all.subList(start, start + number));
	      else
	    	  list.addAll(list_all.subList(start, total));
	    
	    jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list_all);// rows键，存放每页记录list
		return jsonMap;
    }
    
    
    @RequestMapping("/updateEntertaion")
    @ResponseBody
    public int updateEntertaion(Entertainregisterinfo entertainregisterinfo){
    	
    	return entertainService.updateEntertaion(entertainregisterinfo);
    }
    
    
 // Web-已审核招待明细-导出表单
 	@RequestMapping(value = "/exportExcel")
 	public void exportExcel(HttpServletRequest request, HttpServletResponse response, String number,Entertainapplyinfo entertainapplyinfo) {
 		System.out.println("穿过来的数据"+number);
 		Entertainapplyinfo enter = new Entertainapplyinfo();
		if(entertainapplyinfo.getDepartment()!=null&&!entertainapplyinfo.getDepartment().equals("")){
			enter.setDepartment(entertainapplyinfo.getDepartment());
		}
		if(entertainapplyinfo.getManager()!=null&&!entertainapplyinfo.getManager().equals("")){
			enter.setManager(entertainapplyinfo.getManager());
		}
		if(entertainapplyinfo.getStartTime()!=null&&!entertainapplyinfo.getStartTime().equals("")){
			enter.setStartTime(entertainapplyinfo.getStartTime());
		}
		if(entertainapplyinfo.getEndTime()!=null&&!entertainapplyinfo.getStartTime().equals("")){
			enter.setEndTime(entertainapplyinfo.getEndTime());
		}
		if(entertainapplyinfo.getInvoiceNumber()!=null&&!entertainapplyinfo.getInvoiceNumber().equals("")){
			enter.setInvoiceNumber(entertainapplyinfo.getInvoiceNumber());
		}
		if(entertainapplyinfo.getInvoiceSum()!=null&&!entertainapplyinfo.getInvoiceSum().equals("")){
			enter.setInvoiceSum(entertainapplyinfo.getInvoiceSum());
		}
		List<Entertainapplyinfo> list_all = entertainService.wgtServApply(enter);
 		// 获取导出文件夹
 		String path = request.getSession().getServletContext().getRealPath("/");
 		// 生成导出的文件名
 		Date dt = new Date();
 		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
 		String date = matter.format(dt);
 		String fileName = "业务招待明细" + date + ".xlsx";
 		String filePath = path + "/" + fileName;
 		int flag = entertainService.export(list_all, filePath);
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
 			response.setCharacterEncoding("UTF- 8");
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
    
    
    
    /*****************************************************************************************************************************/
   
 	
 	@RequestMapping("/budgetEntertainHoapita")
 	@ResponseBody
 	public ModelAndView budgetEntertainHospita(){
 		ModelAndView result = new ModelAndView("hospitalityBudget/implementationnew");
 		
 		return result;
 	}
 	
 	
 	
 	
    @RequestMapping("/budgetenforcement")
    public ModelAndView budgetenforcement(){
    	ModelAndView result = new ModelAndView("hospitalityBudget/implementation");
    	return result;
    }
    
    @RequestMapping("/getYear")
	@ResponseBody
	public Object getYear(){
    	//entertainInfoManageImpl.getYear();
		return entertainService.getYear();
	}
    
    
    
 // web-显示预算执行情况列表
 	@RequestMapping("/wGetNowAnnual")
 	@ResponseBody
 	public List<Entertainannualbudget> wGetNowAnnual(){
 		return entertainService.wGetNowAnnual();
 	}
    
 	@RequestMapping("/wGetNowAnnual1")
	@ResponseBody
	public List<Entertainannualbudget> wGetNowAnnual1(String year){
		return entertainService.wGetNowAnnual1(year);
	}
    
 	
 	@RequestMapping("/wGetHospitality")
 	public List<Entertainhospitality> wGetHospitality(){
 		
 		return entertainService.wGetHospitality();
 	}
 	
 	
 	
 	
 	
    
    @RequestMapping("/annualbudget")
    public ModelAndView annualbudget(){
    	ModelAndView result = new ModelAndView("hospitalityBudget/annualBudget");
    	return result;
    }
    
 // web-招待年度预算-显示
 	/*@RequestMapping("/wGetAnnualBudget")
 	@ResponseBody
 	public List<EntertainAnnualInfoDTO> wGetAnnualBudget(){
 		
 		return entertainService.wGetAnnualBudget();
 	}*/
    
    @RequestMapping("/wGetAnnualBudget")
    @ResponseBody
 	public List<Entertainhospitality> wGetAnnualBudget(){
 		
 		return entertainService.wGetAnnualBudgethospitality();
 	}
 	//根据年份查询相应的预算情况
    @RequestMapping("/wGetSearchAnnual")
    @ResponseBody
 	public List<Entertainhospitality> wGetSearchAnnual(String year){
//    	List<Entertainhospitality> wGetSearchAnnual = entertainService.wGetSearchAnnual(year);
// 		if(wGetSearchAnnual!=null){
// 			return entertainService.wGetSearchAnnual(year);
// 		}
    	return entertainService.wGetSearchAnnual(year);
 	}
 	
    
    
 // web-获得选中年份每个部门的发生额
	@RequestMapping("/wGetSelectedUsed")
	@ResponseBody
	public Object wGetSelectedUsed(String year){
		return entertainService.wGetSelectedUsed(year);
	}
    
    
    
//	@RequestMapping("/wSetAdjust")
//	@ResponseBody
//	public int wSetAdjust(String param){
//		
//		
//		return entertainService.wSetAdjust(param);
//	}
    
    /**
     * 读取出filePath中的所有数据信息
     * @param filePath excel文件的绝对路径
     * 获取待审批
     */
    @RequestMapping("/getDataFromExcel")
    @ResponseBody
    public  void getDataFromExcel(String filePath)
    {
    	filePath = "C:\\b_entertainapplyinfo.xlsx";
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
            int id = (int)cell.getNumericCellValue();
            System.out.println("ID是"+id);
            cell = row.getCell((short)1);
           /* String number =  cell.getStringCellValue().toString();
            System.out.println("number是"+number);*/
            
            cell = row.getCell((short)12);
            String applyMan = cell.getStringCellValue().toString();
            System.out.println("申请人"+applyMan);
            cell = row.getCell((short)13);
            String approveMan = cell.getStringCellValue().toString();
            System.out.println("审批人是"+approveMan);
            Map<String,Object> activitiMap = new HashMap<>();
            activitiMap.put("ApplicationUser", applyMan);
			//开启流程
			String number = id+"";
			String objId="businesshospitality:"+number;
			identityService.setAuthenticatedUserId(applyMan);
			runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
			List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyMan,"businessapply");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			System.out.println("bidr是"+bids);
			int j=0;
			for(;j<bids.size();j++){
				System.out.println("bids.get(i)是"+bids.get(j));
				if(bids.get(j)==number)
					System.out.println("相等"+j);
					break;
			}
			String taskId=tasks.get(j).getId();
			System.out.println("任务编号："+taskId);
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			Map<String,Object> map = new HashMap<>();
			map.put("businessUser", approveMan);
			workflowUtil.completeTask(workflowBean, map);
        }
    }
	
	//获取被驳回的数据
    @RequestMapping("/reject")
    @ResponseBody
    public String reject(String filePath){
    	
    	filePath = "C:\\b_entertainapplyinfo.xlsx";
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
            int id = (int)cell.getNumericCellValue();
            System.out.println("ID是"+id);
            cell = row.getCell((short)1);
           /* String number =  cell.getStringCellValue().toString();
            System.out.println("number是"+number);*/
            
            cell = row.getCell((short)12);
            String applyMan = cell.getStringCellValue().toString();
            System.out.println("申请人"+applyMan);
            cell = row.getCell((short)13);
            String approveMan = cell.getStringCellValue().toString();
            System.out.println("审批人是"+approveMan);
            Map<String,Object> activitiMap = new HashMap<>();
            activitiMap.put("ApplicationUser", applyMan);
			//开启流程
			String number = id+"";
			String objId="businesshospitality:"+number;
			identityService.setAuthenticatedUserId(applyMan);
			runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
			List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyMan,"businessapply");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			System.out.println("bidr是app"+bids);
			int j=0;
			for(;j<bids.size();j++){
				System.out.println("bids.get(i)是"+bids.get(j));
				if(bids.get(j)==number)
					System.out.println("相等"+j);
					break;
			}
			String taskId=tasks.get(j).getId();
			System.out.println("任务编号："+taskId);
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			Map<String,Object> map = new HashMap<>();
			map.put("businessUser", approveMan);
			workflowUtil.completeTask(workflowBean, map);
			List<Task> approveTask = workflowUtil.getTaskByIds("businesshospitality", approveMan, "businessapprove");
			Map<String,String> approve = workflowUtil.getTaskAndBussIdByTask(approveTask);
			for(String apprveId:approve.keySet()){
				taskId = approve.get(apprveId+"");
				Map<String,Object> rejectMap = new HashMap<>();
				rejectMap.put("ApplicationUser", applyMan);
				rejectMap.put("result", "false");
				workflowUtil.JumpEndActivity(taskId, "businessapply", rejectMap);
			}
			
			
        }
    	
    	
    	return "SUESSS";
    }
	
    @RequestMapping("test")
    @ResponseBody
    public void text(String taskId){
    	
    	workflowUtil.JumpEndActivity(taskId, "financialapproval", null);
    }
    
    
    
  //获取事后登记的数据
    @RequestMapping("/excute")
    @ResponseBody
    public String excute(String filePath){
    	
    	filePath = "C:\\b_entertainapplyinfo.xlsx";
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
            int id = (int)cell.getNumericCellValue();
            System.out.println("ID是"+id);
            cell = row.getCell((short)1);
           /* String number =  cell.getStringCellValue().toString();
            System.out.println("number是"+number);*/
            
            cell = row.getCell((short)12);
            String applyMan = cell.getStringCellValue().toString();
            System.out.println("申请人"+applyMan);
            cell = row.getCell((short)13);
            String approveMan = cell.getStringCellValue().toString();
            System.out.println("审批人是"+approveMan);
            Map<String,Object> activitiMap = new HashMap<>();
            activitiMap.put("ApplicationUser", applyMan);
			//开启流程
			String number = id+"";
			String objId="businesshospitality:"+number;
			identityService.setAuthenticatedUserId(applyMan);
			runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
			List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyMan,"businessapply");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			System.out.println("bidr是app"+bids);
			int j=0;
			for(;j<bids.size();j++){
				System.out.println("bids.get(i)是"+bids.get(j));
				if(bids.get(j)==number)
					System.out.println("相等"+j);
					break;
			}
			String taskId=tasks.get(j).getId();
			System.out.println("任务编号："+taskId);
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			Map<String,Object> map = new HashMap<>();
			map.put("businessUser", approveMan);
			workflowUtil.completeTask(workflowBean, map);
			List<Task> approveTask = workflowUtil.getTaskByIds("businesshospitality", approveMan, "businessapprove");
			Map<String,String> approve = workflowUtil.getTaskAndBussIdByTask(approveTask);
			for(String apprveId:approve.keySet()){
				taskId = approve.get(apprveId+"");
				Map<String,Object> rejectMap = new HashMap<>();
				rejectMap.put("afterwardsUser", applyMan);
				rejectMap.put("result", "true");
				workflowUtil.JumpEndActivity(taskId, "registration", rejectMap);
			}
			
			
        }
    	
    	
    	return "SUESSS";
    }
    
    
    
  //获取财务审批的数据
    @RequestMapping("/fince")
    @ResponseBody
    public String fince(String filePath){
    	
    	filePath = "C:\\b_entertainapplyinfo.xlsx";
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
            int id = (int)cell.getNumericCellValue();
            System.out.println("ID是"+id);
            cell = row.getCell((short)1);
           /* String number =  cell.getStringCellValue().toString();
            System.out.println("number是"+number);*/
            
            cell = row.getCell((short)12);
            String applyMan = cell.getStringCellValue().toString();
            System.out.println("申请人"+applyMan);
            cell = row.getCell((short)13);
            String approveMan = cell.getStringCellValue().toString();
            System.out.println("审批人是"+approveMan);
            Map<String,Object> activitiMap = new HashMap<>();
            activitiMap.put("ApplicationUser", applyMan);
			//开启流程
			String number = id+"";
			String objId="businesshospitality:"+number;
			identityService.setAuthenticatedUserId(applyMan);
			runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
			List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyMan,"businessapply");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			System.out.println("bidr是app"+bids);
			int j=0;
			for(;j<bids.size();j++){
				System.out.println("bids.get(i)是"+bids.get(j));
				if(bids.get(j)==number)
					System.out.println("相等"+j);
					break;
			}
			String taskId=tasks.get(j).getId();
			System.out.println("任务编号："+taskId);
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			Map<String,Object> map = new HashMap<>();
			map.put("businessUser", approveMan);
			workflowUtil.completeTask(workflowBean, map);
			List<Task> approveTask = workflowUtil.getTaskByIds("businesshospitality", approveMan, "businessapprove");
			Map<String,String> approve = workflowUtil.getTaskAndBussIdByTask(approveTask);
			for(String apprveId:approve.keySet()){
				taskId = approve.get(apprveId+"");
				Map<String,Object> rejectMap = new HashMap<>();
				rejectMap.put("afterwardsUser", applyMan);
				rejectMap.put("result", "true");
				workflowUtil.JumpEndActivity(taskId, "registration", rejectMap);
				List<Task> tasksapplyStaff = workflowUtil.getTaskByIds("businesshospitality", applyMan,"registration");
				Map<String,String> staffMap = workflowUtil.getTaskAndBussIdByTask(tasksapplyStaff);
				for(String staffId:staffMap.keySet()){
					
					
					
					taskId = staffMap.get(staffId+"");
					workflowUtil.JumpEndActivity(taskId, "financialapproval",null);
				}
				
			}
			
			
        }
    	
    	
    	return "SUESSS";
    }
    
    
  //获取财务审批的数据
    @RequestMapping("/fincereject")
    @ResponseBody
    public String fincerejects(String filePath){
    	
    	filePath = "C:\\b_entertainapplyinfo.xlsx";
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
            int id = (int)cell.getNumericCellValue();
            System.out.println("ID是"+id);
            cell = row.getCell((short)1);
           /* String number =  cell.getStringCellValue().toString();
            System.out.println("number是"+number);*/
            
            cell = row.getCell((short)12);
            String applyMan = cell.getStringCellValue().toString();
            System.out.println("申请人"+applyMan);
            cell = row.getCell((short)13);
            String approveMan = cell.getStringCellValue().toString();
            System.out.println("审批人是"+approveMan);
            Map<String,Object> activitiMap = new HashMap<>();
            activitiMap.put("ApplicationUser", applyMan);
			//开启流程
			String number = id+"";
			String objId="businesshospitality:"+number;
			identityService.setAuthenticatedUserId(applyMan);
			runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
			List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyMan,"businessapply");
			List<String> bids=workflowUtil.getBussinessIdsByTasks(tasks);
			System.out.println("bidr是app"+bids);
			int j=0;
			for(;j<bids.size();j++){
				System.out.println("bids.get(i)是"+bids.get(j));
				if(bids.get(j)==number)
					System.out.println("相等"+j);
					break;
			}
			String taskId=tasks.get(j).getId();
			System.out.println("任务编号："+taskId);
			WorkflowBean workflowBean = new WorkflowBean();
			workflowBean.setTaskId(taskId);
			Map<String,Object> map = new HashMap<>();
			map.put("businessUser", approveMan);
			workflowUtil.completeTask(workflowBean, map);
			List<Task> approveTask = workflowUtil.getTaskByIds("businesshospitality", approveMan, "businessapprove");
			Map<String,String> approve = workflowUtil.getTaskAndBussIdByTask(approveTask);
			for(String apprveId:approve.keySet()){
				taskId = approve.get(apprveId+"");
				Map<String,Object> rejectMap = new HashMap<>();
				rejectMap.put("afterwardsUser", applyMan);
				rejectMap.put("result", "true");
				workflowUtil.JumpEndActivity(taskId, "registration", rejectMap);
				List<Task> tasksapplyStaff = workflowUtil.getTaskByIds("businesshospitality", applyMan,"registration");
				Map<String,String> staffMap = workflowUtil.getTaskAndBussIdByTask(tasksapplyStaff);
				for(String staffId:staffMap.keySet()){
					taskId = staffMap.get(staffId+"");
					workflowUtil.JumpEndActivity(taskId, "financialapproval",null);
					List<Task> finace = workflowUtil.getTaskByIds("businesshospitality", "financialapproval");
					Map<String,String> finaceMap = workflowUtil.getTaskAndBussIdByTask(finace);
					for(String finaceId:finaceMap.keySet()){
						taskId = finaceMap.get(finaceId+"");
						workflowBean.setTaskId(taskId);
						Map<String,Object> finaceMaps = new HashMap<>();
						finaceMaps.put("cofirm", "false");
						finaceMaps.put("afterwardsUser", applyMan);
						workflowUtil.completeTask(workflowBean, finaceMaps);
					}
					
				}
				
			}
			
			
        }
    	
    	
    	return "SUESSS";
    }
    //查看招待预算详情
    @RequestMapping("/annualBudgetDetail")
    @ResponseBody
 	public List<EntertainhospitalitySubsidiary> annualBudgetDetail(String hospitalityId){
    	return entertainService.getAnnualBudgetDetail(hospitalityId);
 	}
    //调整预算金额
    @RequestMapping(value = "/wSetAdjust")
	@ResponseBody
	public Object wSetAdjust(String id,String money) {
		Date now=new Date();
		String datenew = new SimpleDateFormat("yyyy-MM-dd").format(now);
		String date = new SimpleDateFormat("yyyy").format(now);
		return entertainService.wSetAdjust(id,money,datenew,date);
	}
}
