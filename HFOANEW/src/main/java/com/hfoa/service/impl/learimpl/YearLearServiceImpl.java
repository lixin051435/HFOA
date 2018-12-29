package com.hfoa.service.impl.learimpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfoa.dao.lear.LearTimeMapper;
import com.hfoa.dao.lear.YearLearMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.leaver.YearLearService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.WorkflowUtil;



/**
 *年假                                                                                                                                                                                                                                                     
 */
@Service
public class YearLearServiceImpl implements YearLearService{
	
	
	@Autowired
	private WorkflowUtil workflowUtil;
	
	@Autowired
	private USerService bUserService;
	
	@Autowired
	private BDepartmentService bDepartmentService;
	
	@Autowired
	private BRoleService bRoleService;
	
	@Autowired
	private YearLearMapper YearLearMapper;
	
	@Autowired
	private LearTimeMapper learTimeMapper;
	
	@Autowired
	private UserMapper userMapper;

	/**
	 * 提交申请
	 */
	@Transactional
	@Override
	public int insertYearLeaver(LearYear learYear) {
		learYear.setStatus("待审批");//待部门审批
		learYear.setConfirm("0");//综合部没有确认
		learYear.setState("0");
		learYear.setLeaveType("年假");
		System.out.println("插入"+learYear);
		return YearLearMapper.insert(learYear);
	}

	@Override
	public int updateYearLeaver(LearYear learYear) {
		System.out.println("更新"+learYear);
		System.out.println("查看频次"+learYear.getFrequency());
		//learYear.setStatus("1");//更改为待部门审批
		return YearLearMapper.updateByPrimaryKey(learYear);
	}

	@Override
	public int deleteYearLeaver(String id) {
		System.out.println("删除id是"+id);
		return YearLearMapper.deleteByPrimaryKey(id);
	}

	@Override
	public LearYear selectYearLeaver(String id) {
		System.out.println("查询"+id);
		return YearLearMapper.findById(id);
	}

	
	
	@Override
	public LearYear selectUserIDLeaver(String user_id) {
		System.out.println("用户ID是"+user_id);
		return YearLearMapper.findUserID(user_id);
	}
	
	//退回待修改
	@Override
	public int updateReject(String id) {
		return YearLearMapper.updateReject(id);
	}
	 
    //部门审批通过
	@Override
	public int updatePass(String id) {
		return YearLearMapper.updatePass(id);
	}
	
    //综合部确认
	@Override
	public int updateAffirm(String id) {
		return YearLearMapper.updateAffirm(id);
	}
	
    //员工确认休假完成
	@Override
	public int updateEnd(String id) {
		return YearLearMapper.updateEnd(id);
	}
	//员工放弃
	@Override
	public int updateRenounce(String id) {
		return YearLearMapper.updateRenounce(id);
	}
	//转接第二年
	@Override
	public int updateTransfer(String id) {
		return YearLearMapper.updateTransfer(id);
	}
	//现金补偿
	@Override
	public int updateCash(String id) {
		return YearLearMapper.updateCash(id);
	}
	//异常
	@Override
	public int updateAbnormal(String id) {
		return YearLearMapper.updateAbnormal(id);
	}

	//无效
	@Override
	public int updateState(String id) {
		return YearLearMapper.updateState(id);
	}

	@Override
	public List<LearYear> listYearLeaver(String openId) {
		return YearLearMapper.listYear(openId);
	}

	@Override
	public List<Map<String, Object>> executed(String id,String leave_id) {
		return YearLearMapper.executed(id,leave_id);
	}

	@Override
	public Map<String,Object> searLeaver(LearYear learYear,String openId,Integer nowPage,Integer pageSize) {
		//根据openID获取用户id
		Map<String,Object> map = new HashMap<>();
		Integer userid = bUserService.getUserIdByOpenId(openId);
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		if(null==learYear.getApplyMan()){
			learYear.setApplyMan(user.getCode());
		}
		
		if(learYear.getApplyMan().equals("1")){
			learYear.setApplyMan(null);
		}
		
		SimpleDateFormat s = new SimpleDateFormat("yyyy");
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
	    if(learYear.getBeginTime()==null&&learYear.getEndTime()==null){
	    	String format = s.format(new Date());
	    	learYear.setBeginTime(format+"-01-01");
	    	learYear.setEndTime(smf.format(new Date()));
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
			learYear.setApplyMan(user.getCode());
		}
		if(maxAppGrade==2||maxAppGrade==3){//经理
			BDepartmentEntity departmentEntity = bDepartmentService.getDepartmentIdById(departmentIdByUserId.get(0));
			if(departmentEntity!=null){
				learYear.setDepartment(departmentEntity.getDepartmentname());
			}
		}
		if(maxAppGrade==4){
			
		}*/
		List<LearYear> listnew = YearLearMapper.searLeaver(learYear);
		for(LearYear leYear:listnew){
			leYear.setLeaver(learTimeMapper.listLearTime(leYear.getId()));
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

	@Override
	public List<Map<String, Object>> getUpdate(String openId, String leave_id) {
		return YearLearMapper.getUpdate(openId, leave_id);
	}

	@Override
	public List<Map<String, Object>> listExecuted() {
		return YearLearMapper.listExecuted();
	}

	@Override
	public int selectNum(String approveManOpenId) {
		return YearLearMapper.selectNum(approveManOpenId);
	}

	@Override
	public List<LearYear> searchLeaver(LearYear learYear) {
		List<LearYear> list = YearLearMapper.searchLeaver(learYear);
		System.out.println("查看list是"+list);
		for(LearYear leaver:list){
			/*String status = CommonUtil.setStatus(leaver.getStatus());*/
			/*if(leaver.getState().equals("1")){//已完结
				leaver.setStatus("完结");
			}*/
			
			String sfyc = "";
			if(leaver.getSfyc().equals("0")){
				sfyc = "否";
			}else if(leaver.getSfyc().equals("1")){
				sfyc = "是";
			}else{
				sfyc = "否";
			}
			leaver.setSfyc(sfyc);
		}
		
		return list;
	}

	
	//web导出
	@Override
	public int exportYearLeaver(LearYear learYear, String filePath) {
		List<LearYear> list = searchLeaver(learYear);
		for(LearYear lear:list){
			List<LearTime> listLearTime = learTimeMapper.listLearTime(lear.getId());
			lear.setLeaver(listLearTime);
		}
		System.out.println("查询导出年假是"+list);
		int i = exportApplyExpensesExcel(list, filePath);
		
		return i;
	}

	private int exportApplyExpensesExcel(List<LearYear> listLearYear, String path) {
	    // 创建一个工作簿
	    XSSFWorkbook workbook;
	    try {
	      workbook = new XSSFWorkbook();
	      // 添加一个sheet,sheet名
	      XSSFSheet sheet = workbook.createSheet("年假统计表");
	      // 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
	      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));
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
	      titleCell.setCellValue("年假统计表");
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
	      sheet.setColumnWidth(9, 3300);
	      
	      // 设置其他正文单元格格式
	      XSSFCellStyle style = workbook.createCellStyle();
	      style.setAlignment(HorizontalAlignment.CENTER);
	      // 设置第二行表头
	      XSSFRow rowHeader = sheet.createRow(1);
	      XSSFCell cell = rowHeader.createCell(0);// 第一列
	      cell.setCellValue("部门");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(1);// 第二列
	      cell.setCellValue("申请人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(2);// 第三列
	      cell.setCellValue("申请时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(3);// 第四列
	      cell.setCellValue("审批人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(4);// 第五列
	      cell.setCellValue("休假次数");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(5);// 第六列
	      cell.setCellValue("开始时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(6);// 第七列
	      cell.setCellValue("结束时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(7);// 第八列
	      cell.setCellValue("天数");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(8);// 第九列
	      cell.setCellValue("是否完结");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(9);// 第十列
	      cell.setCellValue("是否异常");
	      cell.setCellStyle(style);
	      
	      // 表头完成------------------
	      int index = 1;// 行序号，应从第三行开始，每次循环进行++
	      // 遍历集合将数据写到excel中
	      if (listLearYear.size() > 0) {
	        for (LearYear learYear : listLearYear) {
	          // 行号++，2开始
	          index++;
	          // 生成一个新行
	          XSSFRow row = sheet.createRow(index);
	          XSSFCell rowCell = row.createCell(0);// 第一列
	          rowCell.setCellValue(learYear.getDepartment());//部门
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(1);// 第二列
	          rowCell.setCellValue(learYear.getApplyMan());//申请人
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(2);// 第三列
	          rowCell.setCellValue(learYear.getApplyTime());//申请时间
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(3);// 第四列
	          rowCell.setCellValue(learYear.getApproveMan());//审批人
	          rowCell.setCellStyle(style);

	          rowCell = row.createCell(4);// 第八列
	          rowCell.setCellValue(learYear.getFrequency());//次数
	          rowCell.setCellStyle(style);
	          
	          rowCell = row.createCell(9);// 
	          rowCell.setCellValue(learYear.getSfyc());//次数
	          rowCell.setCellStyle(style);
	          
	          rowCell = row.createCell(8);// 
	          
	          rowCell.setCellValue(learYear.getStatus());
	          rowCell.setCellStyle(style);
	          
	          if (learYear.getLeaver().size() > 0) {
	              rowCell = row.createCell(5);// 第五列
	              rowCell.setCellValue(learYear.getLeaver().get(0).getBeingTime());
	              rowCell.setCellStyle(style);

	              rowCell = row.createCell(6);// 第六列
	              rowCell.setCellValue(learYear.getLeaver().get(0).getEndTime());
	              rowCell.setCellStyle(style);

	              rowCell = row.createCell(7);// 第七列
	              rowCell.setCellValue(learYear.getLeaver().get(0).getDays());
	              rowCell.setCellStyle(style);
	            } else {
	              rowCell = row.createCell(5);// 第五列
	              rowCell.setCellValue("");
	              rowCell.setCellStyle(style);

	              rowCell = row.createCell(6);// 第六列
	              rowCell.setCellValue("");
	              rowCell.setCellStyle(style);

	              rowCell = row.createCell(7);// 第七列
	              rowCell.setCellValue("");
	              rowCell.setCellStyle(style);
	            }


	          
	          int rowNum = 0;
	          for (int i = 1; i < learYear.getLeaver().size(); i++) {
	            rowNum = i;
	            index++;
	            // 生成一个新行
	            XSSFRow newRow = sheet.createRow(index);
	            rowCell = newRow.createCell(5);// 第五列
	            rowCell.setCellValue(learYear.getLeaver().get(i).getBeingTime());
	            rowCell.setCellStyle(style);

	            rowCell = newRow.createCell(6);// 第六列
	            rowCell.setCellValue(learYear.getLeaver().get(i).getEndTime());
	            rowCell.setCellStyle(style);

	            rowCell = newRow.createCell(7);// 第七列
	            rowCell.setCellValue(learYear.getLeaver().get(i).getDays());
	            rowCell.setCellStyle(style);
	          }
	          if (rowNum > 0) {
	            for (int i = 0; i < 10; i++) {
	              if (i != 5 && i != 6 && i != 7)
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

	/**
	 * 工作流领导审批接口
	 */
	@Override
	public int leaverApprove(String id, String taskId, String result,String comment) {
		LearYear findById = YearLearMapper.findById(id);
		if(findById!=null){
			String GzhOpenId = "";
			String status = "";
			String view = "";
			String applyMan = "";
			WeiEntity weiEntity = new WeiEntity();
			
			UserEntity userEntity = userMapper.getopenId(findById.getOpenId());
			if(userEntity!=null){
				GzhOpenId = userEntity.getModifiedby();
				applyMan = userEntity.getCode();
			}
			Map<String,Object> map = new HashMap<>();
			if(result.equals("1")){//领导通过
				findById.setStatus("已通过");
				YearLearMapper.updateByPrimaryKey(findById);
				map.put("leaverStafuser", applyMan);
				map.put("result", result);
				completeApplyTask(taskId, "", map);
				//推送
				weiEntity.setApproveMan(findById.getApproveMan());
				weiEntity.setLeaveType(findById.getLeaveType());
				status = "";
				if(null!=findById.getStatus()){
					status = CommonUtil.setStatus(findById.getStatus());
				}
				weiEntity.setStatus(status);
				status = "审批通过";
				view = "领导审批完毕";
				try {
					CommonUtil.sendApply(GzhOpenId, weiEntity,status,view);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return 1;
			}
			if(result.equals("2")){//领导驳回
				findById.setStatus("已驳回");
				YearLearMapper.updateByPrimaryKey(findById);
				map.put("leaverApply", applyMan);
				map.put("result", result);
				completeApplyTask(taskId, comment, map);
				
				if(findById!=null){
					weiEntity.setApproveMan(findById.getApproveMan());
					weiEntity.setLeaveType(findById.getLeaveType());
					status = CommonUtil.setStatus(findById.getStatus());
					weiEntity.setStatus(status);
				}
				status = "驳回修改";
				view = "领导审批完成";
				try {
					CommonUtil.sendApply(GzhOpenId, weiEntity,status,view);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				return 1;
			}
			if(result.equals("3")){//调整到第二年
				map.put("result", result);
				findById.setStatus("调整到第二年");
				YearLearMapper.updateByPrimaryKey(findById);
				List<LearTime> listLearTime = learTimeMapper.listLearTime(id);
				int days = 0;
				for(LearTime learTime:listLearTime){
					if(learTime.getState().equals("0")){//查询剩余的天数
						days+=Integer.parseInt(learTime.getDays());
					}
				}
				userEntity.setCreateby(String.valueOf(Integer.parseInt(userEntity.getCreateby())+days));
				System.out.println("查询到的用户是"+userEntity);
				//年假时间更新
				userMapper.updateUserDays(userEntity);
				YearLearMapper.updateState(id);//任务无效
				learTimeMapper.updateStateLeave(id);//附属表无效
				completeApplyTask(taskId, "", map);
				weiEntity.setLeaveType(findById.getLeaveType());
				weiEntity.setApproveMan(findById.getApproveMan());
				status = "转接第二年";
				view = "领导审批完毕";
				try {
					CommonUtil.sendApply(GzhOpenId, weiEntity, status, view);
				} catch (IOException e) {
					e.printStackTrace();
				
				return 1;
				}
			}
			if(result.equals("4")){//现金补偿
				map.put("result", result);
				findById.setStatus("现金补偿");
				YearLearMapper.updateByPrimaryKey(findById);
				userEntity.setCreateby("5");//默认恢复5留下以后再写
				System.out.println("查询到的用户是"+userEntity);
				//年假时间更新
				userMapper.updateUserDays(userEntity);
				YearLearMapper.updateState(id);//任务无效
				/*learTimeMapper.updateStateLeave(id);*///附属表无效
				completeApplyTask(taskId, "", map);
				weiEntity.setLeaveType(findById.getLeaveType());
				weiEntity.setApproveMan(findById.getApproveMan());
				status = "现金补偿";
				view ="领导审批完成";
				try {
					CommonUtil.sendApply(GzhOpenId, weiEntity, status, view);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 1;
			}
			
			if(result.equals("5")){//调整到二年
				map.put("result", result);
				findById.setStatus("调整到第二年");
				YearLearMapper.updateByPrimaryKey(findById);
				List<LearTime> listLearTimes = learTimeMapper.listLearTime(id);
				int day = 0;
				for(LearTime learTime:listLearTimes){
					if(learTime.getState().equals("0")){//查询剩余的天数
						day+=Integer.parseInt(learTime.getDays());
					}
				}
				userEntity.setCreateby(String.valueOf(Integer.parseInt(userEntity.getCreateby())+day));
				System.out.println("查询到的用户是"+userEntity);
				//年假时间更新
				userMapper.updateUserDays(userEntity);
				YearLearMapper.updateState(id);//任务无效
				/*learTimeMapper.updateStateLeave(id);*///附属表无效
				completeApplyTask(taskId, "", map);
				weiEntity.setLeaveType(findById.getLeaveType());
				weiEntity.setApproveMan(findById.getApproveMan());
				status = "转接第二年";
				view = "领导审批完毕";
				try {
					CommonUtil.sendApply(GzhOpenId, weiEntity, status, view);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 1;
				
			}
			
			if(result.equals("6")){//放弃
				map.put("result", result);
				System.out.println("领导点击放弃");
				findById.setStatus("放弃");
				int i = YearLearMapper.updateByPrimaryKey(findById);
				YearLearMapper.updateState(id);
				YearLearMapper.updateState(id);//任务无效
				/*learTimeMapper.updateStateLeave(id);*///附属表无效
				completeApplyTask(taskId, "", map);
				String gzhOpenId = "";
				if(userEntity!=null){
					gzhOpenId = userEntity.getModifiedby();
				}
				gzhOpenId = userEntity.getModifiedby();
				weiEntity.setLeaveType(findById.getLeaveType());
				weiEntity.setApproveMan(findById.getApproveMan());
				status = "请填写纸质说明,《员工考勤休假确认单》";
				view = "领导审批完毕";
				try {
					CommonUtil.sendApply(gzhOpenId, weiEntity, status, view);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return 1;
				
			}
			if(result.equals("7")){//现金补偿
				map.put("result", result);
				findById.setStatus("现金补偿");
				YearLearMapper.updateByPrimaryKey(findById);
				userEntity.setCreateby("5");//默认恢复5留下以后再写
				System.out.println("查询到的用户是"+userEntity);
				//年假时间更新
				userMapper.updateUserDays(userEntity);
				YearLearMapper.updateState(id);//任务无效
				/*learTimeMapper.updateStateLeave(id);*///附属表无效
				completeApplyTask(taskId, "", map);
				return 1;
			}
		}
		return 0;
	}
	
	public   void completeApplyTask(String taskId,String comment,Map<String,Object>map){
		WorkflowBean workflowBean = new WorkflowBean();
		workflowBean.setTaskId(taskId);
		workflowBean.setComment(comment);
		workflowUtil.completeTask(workflowBean, map);
	}

	@Override
	public List<Map<String, Object>> countLeaver(String openId) {
		Date date = new Date();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		String format = smf.format(date);
		/*YearLearMapper.getOpenIdLeaver(openId, year)
		if(){
			
		}*/
		return YearLearMapper.countLeaver(openId,format+"%");
	}

	@Override
	public LearYear getOpenIdLeaver(String openId, String year) {
		return YearLearMapper.getOpenIdLeaver(openId, "%"+year+"%");
	}

}
