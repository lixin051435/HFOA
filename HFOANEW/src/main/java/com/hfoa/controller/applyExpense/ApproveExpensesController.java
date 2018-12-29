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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.travelExpenses.ApproveExpensesEntity;
import com.hfoa.service.TravelExpes.ApproveExpensesService;
import com.hfoa.service.TravelExpes.TravelExpessService;




@Controller
@RequestMapping("/approveExpenses")
public class ApproveExpensesController {
  
	
	@Autowired
	private ApproveExpensesService approveExpensesService;
	
	@Autowired
	private TravelExpessService travelExpessService;
	
	@RequestMapping("/registeredApplyExpense")
	@ResponseBody
	public ModelAndView registeredApplyExpense(){
		//system.out.println("查看差旅已登记");
		ModelAndView result = new ModelAndView("travel/registeredList");
		return result;
	}
  

	
	/**
	 * 已登记查看
	 * @param approveExpense
	 * @param starTime
	 * @param searendTime
	 * @param voucherNum
	 * @param request
	 * @return
	 */
	@RequestMapping("/searApplyExpenseApprove")
	@ResponseBody
	public Object searApplyExpenseApprove(ApplyExpensesEntity approveExpense,String starTime,String searendTime,
			  String voucherNum,HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		//system.out.println("page是"+page);
		String rows = request.getParameter("rows");// 每页多少条
		//system.out.println("row是"+rows);
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		//system.out.println("时间"+approveExpense.getBeginTime()+"结束时间"+approveExpense.getEndTime());
		// 每页的开始记录
	    int start = (intPage - 1) * number;
		List<ApplyExpensesEntity> applyExpense = approveExpensesService.searchApplyExpense(approveExpense,starTime,searendTime,voucherNum);
		Map<String,Object> jsonMap = new HashMap<String,Object>();
		int toatl = applyExpense.size();
		List<ApplyExpensesEntity> list = new ArrayList<ApplyExpensesEntity>();
		 if (start + number <= toatl)
			 list.addAll(applyExpense.subList(start, start + number));
		 else
			 list.addAll(applyExpense.subList(start, toatl));
		 jsonMap.put("total", toatl);// total存放总记录数
		 jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	
	
	
	
	 @RequestMapping("/getApproveExpenses")
	  @ResponseBody
	  public Object getApproveExpensesByTId(String travelExpenseId) {

	    return approveExpensesService.getApproveExpensesByTId(travelExpenseId);
	  }
	
	
	 /**
	  * web端查看差旅费详情
	  * @param travelExpenseId
	  * @return
	  */
	 @RequestMapping("/getroveExpenses")
	 @ResponseBody
	 public Object getroveExpenses(String travelExpenseId){
		 
		 return approveExpensesService.getApproveExpensesByTId(travelExpenseId);
	 }
	
	 
	/**
	 * 添加
	 * @param approveExpensesEntity
	 * @return
	 */
	@RequestMapping("/insertFinanceReview")
	@ResponseBody
	public int insertFinanceReview(ApproveExpensesEntity approveExpensesEntity){
		
		return approveExpensesService.insertApproveExpensesService(approveExpensesEntity);
	}
	 
	/**
	 * 删除
	 * @param Id
	 * @return
	 */
	@RequestMapping("/deleteapproveExpenses")
	@ResponseBody
	public int deleteapproveExpenses(String Id){
		
		return approveExpensesService.deleteapproveExpenses(Id);
	}
	 
	
	/**
	 * 修改
	 * @param approveExpensesEntity
	 * @return
	 */
	@RequestMapping("/modifyFinanceReview")
	@ResponseBody
	public int modifyFinanceReview(ApproveExpensesEntity approveExpensesEntity){
		
		return approveExpensesService.modifyFinanceReview(approveExpensesEntity);
	}
	 
	 
	/*导出已登记列表
	   * 
	   */
	  @RequestMapping("/exportApproveExpenses")
	  @ResponseBody
	  public void exportApplyExpenses(HttpServletRequest request, HttpServletResponse response,
			  ApplyExpensesEntity applyExpensesDTO,String starTime,String searendTime,String voucherNum) {
	    // 获取导出文件夹
	    String path = request.getSession().getServletContext().getRealPath("/");
	    //system.out.println("导出的查询数据为"+applyExpensesDTO);
	    String fileName = "差旅费已登记列表" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xlsx";
	    String filePath = path + "/" + fileName;
	    int flag = approveExpensesService.exportapproveExpenses(applyExpensesDTO, filePath,starTime,searendTime,voucherNum);
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
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	
}
