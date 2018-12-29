package com.hfoa.service.impl.entertainimpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hfoa.dao.entertain.EntertainAnnualBudgetMapper;
import com.hfoa.dao.entertain.EntertainHospitalityMapper;
import com.hfoa.dao.entertain.EntertainHospitalitysubsidiaryMapper;
import com.hfoa.dao.entertain.EntertainMapper;
import com.hfoa.dao.entertain.EntertaininvoiceunitMapper;
import com.hfoa.dao.entertain.EntertainobjecttypeMapper;
import com.hfoa.dao.entertain.EntertainregisterinfoMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.activity.WorkflowBean;
import com.hfoa.entity.entertain.EntertainAnnualInfoDTO;
import com.hfoa.entity.entertain.Entertainannualbudget;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainhospitality;
import com.hfoa.entity.entertain.EntertainhospitalitySubsidiary;
import com.hfoa.entity.entertain.Entertaininvoiceunit;
import com.hfoa.entity.entertain.Entertainobjecttype;
import com.hfoa.entity.entertain.Entertainregisterinfo;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.weixin.WeiEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.entertain.EntertainService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.CommonUtil;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.ListSortUtil;
import com.hfoa.util.WorkflowUtil;







/**
 * 
 * @author wzx
 * 部门service实现类
 */
@Service
public class EntertainServiceImpl implements EntertainService{
	
	@Autowired
	private EntertainMapper entertainMapper;

	@Autowired
	private EntertainregisterinfoMapper entertainregisterinfoMapper;
	
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
	private EntertainAnnualBudgetMapper entertainAnnualBudgetMapper;
	
	@Autowired
	private EntertainHospitalityMapper entertainHospitalityMapper;
	
	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private EntertainobjecttypeMapper entertainobjecttypeMapper;
	
	@Autowired
	private EntertaininvoiceunitMapper entertaininvoiceunitMapper;
	
	@Autowired
	private  IdentityService identityService;
	
	@Autowired
	private TaskService taskService;
	
	@Value("#{configProperties['earlyentertan']}") 
	private String enter;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	@Autowired
	private EntertainHospitalitysubsidiaryMapper entertainHospitalitysubsidiaryMapper;
	
	@Override
	public int insertEntertain(Entertainapplyinfo entertainapplyinfo) {
		System.out.println("查询"+entertainMapper.getSqlLast());
		int id = 0;
		if(null==entertainMapper.getSqlLast()){
			id = 0;
		}else{
			id = entertainMapper.getSqlLast()+1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		entertainapplyinfo.setId(id);
		entertainapplyinfo.setStatus("待审批");
		entertainapplyinfo.setNumber(getNum(getSqlLastNum()));
		entertainapplyinfo.setRemainingBudget(entertainapplyinfo.getMasterBudget());
		String date = entertainapplyinfo.getApplyTime();
		
		Date date1 = null;
		try {
			date1 = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String newDate = sdf.format(date1);
		entertainapplyinfo.setApplyTime(newDate);
		if(entertainapplyinfo.getIfWine()==null||"(null)".equals(entertainapplyinfo.getIfWine())||"null".equals(entertainapplyinfo.getIfWine())||"".equals(entertainapplyinfo.getIfWine())){
			entertainapplyinfo.setIfWine("0");
		}
		if(entertainapplyinfo.getWineType()==null||"(null)".equals(entertainapplyinfo.getWineType())||"null".equals(entertainapplyinfo.getWineType())||"".equals(entertainapplyinfo.getWineType())){
			entertainapplyinfo.setWineType("");
		}
		if(entertainapplyinfo.getWineNum()==null||"(null)".equals(entertainapplyinfo.getWineNum())||"null".equals(entertainapplyinfo.getWineNum())||"".equals(entertainapplyinfo.getWineNum())){
			entertainapplyinfo.setWineNum("0");
		}
		if(entertainapplyinfo.getIfBefore()==null||"(null)".equals(entertainapplyinfo.getIfBefore())||"null".equals(entertainapplyinfo.getIfBefore())||"".equals(entertainapplyinfo.getIfBefore())){
			entertainapplyinfo.setIfBefore("0");
		}
		if(entertainapplyinfo.getBeforeDate()==null||"(null)".equals(entertainapplyinfo.getBeforeDate())||"null".equals(entertainapplyinfo.getBeforeDate())||"".equals(entertainapplyinfo.getBeforeDate())){
			entertainapplyinfo.setBeforeDate("");
		}
		if("1".equals(entertainapplyinfo.getIfBefore())){
			String beforeDate = entertainapplyinfo.getBeforeDate();
			Date date2 = null;
			try {
				date2 = sdf.parse(beforeDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String newDate2 = sdf.format(date2);
			if(newDate2.equals(newDate)){
				entertainapplyinfo.setIfBefore("0");
				entertainapplyinfo.setBeforeDate("");
			}
		}
		Map<String,Object> activitiMap = new HashMap<>();
		System.out.println("插入内容是"+entertainapplyinfo);
		List<Entertainobjecttype> list = entertainobjecttypeMapper.getType();
		boolean check = false;
		for(Entertainobjecttype entertainobjecttype:list){
			if(!entertainobjecttype.getoName().equals(entertainapplyinfo.getEntertainObject())){//不包含
				check = true;
			}
		}
		
		if(check){
			//将新录入的招待公司添加
			Entertainobjecttype entertainob = new Entertainobjecttype();
			entertainob.setId(entertainobjecttypeMapper.selectMax().getId()+1);
			entertainob.setoName(entertainapplyinfo.getEntertainObject());
			entertainobjecttypeMapper.insertType(entertainob);
		}
		
		int flag = entertainMapper.insertEntertain(entertainapplyinfo);//提交申请
		if(flag==1){
			UserEntity applyManUser = userMapper.getopenId(entertainapplyinfo.getOpenId());
			if(applyManUser!=null){
				activitiMap.put("ApplicationUser", applyManUser.getCode());
				//开启流程
				String number = entertainapplyinfo.getId()+"";
				String objId="businesshospitality:"+number;
				identityService.setAuthenticatedUserId(applyManUser.getCode());
				runtimeService.startProcessInstanceByKey("businesshospitality",objId,activitiMap);
				List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",applyManUser.getCode(),"businessapply");
				
				
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
				UserEntity userEntity = null;
				String GZhopenID = "";
				try {
					userEntity = userMapper.getopenId(entertainapplyinfo.getApproverOpenid());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if(userEntity!=null){
					GZhopenID = userEntity.getModifiedby();
					Map<String,Object> map = new HashMap<>();
					map.put("businessUser", userEntity.getCode());
					//完成任务
					System.out.println("任务编号TaskId是"+taskId);
					
					completeApplyTask(taskId,null,map);
				}
				String title = "您好,"+entertainapplyinfo.getManager()+"提交的业务招待需要您审批";
				String mark = "待审批";
				WeiEntity weiEntity = new WeiEntity();
				weiEntity.setApplyMan(entertainapplyinfo.getManager());
				weiEntity.setApplyTime(entertainapplyinfo.getApplyTime());
				weiEntity.setMasterBudget(entertainapplyinfo.getMasterBudget());
				weiEntity.setCause(entertainapplyinfo.getEntertainReason());
				weiEntity.setType(entertainapplyinfo.getEntertainCategory());
				try {
					CommonUtil.sendEntertainApproveMessage(GZhopenID,weiEntity,title,mark);
				} catch (JsonGenerationException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return flag;
	}

	@Override
	public int updateEntertain(Entertainapplyinfo entertainapplyinfo,Entertainregisterinfo entertainregisterinfo) {
		String taskId = entertainapplyinfo.getTaskId();
		Map<String,Object> map = new HashMap<>();
			if(null!=taskId&&!taskId.equals("")){
				if(entertainapplyinfo.getCanUpdate().equals("0")){//左边
					entertainapplyinfo.setStatus("待审批");
					String GZhopenID = "";
					UserEntity userEntity = userMapper.getopenId(entertainapplyinfo.getApproverOpenid());
					if(userEntity!=null){
						GZhopenID = userEntity.getModifiedby();
						map.put("businessUser", userEntity.getCode());
						completeApplyTask(taskId, "", map);
					}
					String title = "您好,"+entertainapplyinfo.getManager()+"提交的业务招待需要您审批";
					String mark = "待审批";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setApplyMan(entertainapplyinfo.getManager());
					weiEntity.setApplyTime(entertainapplyinfo.getApplyTime());
					weiEntity.setMasterBudget(entertainapplyinfo.getMasterBudget());
					weiEntity.setCause(entertainapplyinfo.getEntertainReason());
					weiEntity.setType(entertainapplyinfo.getEntertainCategory());
					try {
						CommonUtil.sendEntertainApproveMessage(GZhopenID,weiEntity,title,mark);
					} catch (JsonGenerationException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(entertainapplyinfo.getCanUpdate().equals("1")){//右边 进行中修改
					Entertainapplyinfo aproveEntertain = entertainMapper.getAproveEntertain(entertainapplyinfo.getId());
					entertainregisterinfo.setId(entertainregisterinfo.getRegisterId());
					entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
					if(aproveEntertain!=null){
						boolean falg = false;
						if(Double.parseDouble(entertainapplyinfo.getMasterBudget())>Double.parseDouble(aproveEntertain.getMasterBudget())){//招待预算超出
							falg = true;
						}
						if(falg){
							String GZhopenID = "";
							UserEntity userEntity = userMapper.getopenId(entertainapplyinfo.getApproverOpenid());
							if(userEntity!=null){
								GZhopenID = userEntity.getModifiedby();
							}
							String title = "您好,"+entertainapplyinfo.getManager()+"提交的业务招待需要您审批";
							String mark = "待审批";
							WeiEntity weiEntity = new WeiEntity();
							weiEntity.setApplyMan(entertainapplyinfo.getManager());
							weiEntity.setApplyTime(entertainapplyinfo.getApplyTime());
							weiEntity.setMasterBudget(entertainapplyinfo.getMasterBudget());
							weiEntity.setCause(entertainapplyinfo.getEntertainReason());
							weiEntity.setType(entertainapplyinfo.getEntertainCategory());
							try {
								CommonUtil.sendEntertainApproveMessage(GZhopenID,weiEntity,title,mark);
							} catch (JsonGenerationException e) {
								e.printStackTrace();
							} catch (JsonMappingException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							entertainapplyinfo.setStatus("待审批");
							workflowUtil.JumpEndActivity(taskId, "businessapprove",null);//领导重新审批
						}
						if(!entertainapplyinfo.getEntertainObject().equals(aproveEntertain.getEntertainObject())){
							String GZhopenID = "";
							UserEntity userEntity = userMapper.getopenId(entertainapplyinfo.getApproverOpenid());
							if(userEntity!=null){
								GZhopenID = userEntity.getModifiedby();
							}
							String title = "您好,"+entertainapplyinfo.getManager()+"提交的业务招待单位改变";
							String mark = "招待单位改变";
							WeiEntity weiEntity = new WeiEntity();
							weiEntity.setApplyMan(entertainapplyinfo.getManager());
							weiEntity.setApplyTime(entertainapplyinfo.getApplyTime());
							weiEntity.setMasterBudget(entertainapplyinfo.getMasterBudget());
							weiEntity.setCause(entertainapplyinfo.getEntertainReason());
							weiEntity.setType(entertainapplyinfo.getEntertainCategory());
							try {
								CommonUtil.sendEntertainApproveMessage(GZhopenID,weiEntity,title,mark);
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
		return entertainMapper.updateEntertain(entertainapplyinfo);
	}

	@Override
	public int deleteEntertain(int id,String taskId) {
		workflowUtil.deleteProcess(taskId);
		Entertainapplyinfo aproveEntertain = entertainMapper.getAproveEntertain(id);
		if(aproveEntertain!=null){
			entertainregisterinfoMapper.deleteEntertainregisterinfonumber(aproveEntertain.getNumber());
			UserEntity userEntity = null;
			String GZhopenID = "";
			try {
				userEntity = userMapper.getopenId(aproveEntertain.getApproverOpenid());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(userEntity!=null){
				GZhopenID = userEntity.getModifiedby();
			}
			String title = "您好,"+aproveEntertain.getManager()+"提交的业务招待已撤销";
			String mark = "已撤销";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApplyMan(aproveEntertain.getManager());
			weiEntity.setApplyTime(aproveEntertain.getApplyTime());
			weiEntity.setMasterBudget(aproveEntertain.getMasterBudget());
			weiEntity.setCause(aproveEntertain.getEntertainReason());
			weiEntity.setType(aproveEntertain.getEntertainCategory());
			try {
				CommonUtil.sendEntertainApproveMessage(GZhopenID,weiEntity,title,mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return entertainMapper.deleteEntertain(id);
	}
	
	
	public String getNum(String num) { // num为数据库表中查询出的最大编号

		String number = ""; // 定义审批单编号变量，初始为空

		// 截取当前日期中的年份后两位
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(new Date());
		String year = str.substring(2, 4);

		// 如果表中没有记录，给出初始编号
		if (num.equals("")||num==null) {
			number = "ZD" + year + "0001";
		} else {
			String n = num.substring(4); // 截取当前表中最大编号的后四位流水号
			String s2 = ""; // 在当前表中最大流水号的基础上+1
			int lg = Integer.parseInt(n); // 将当前表中最大流水号转为整数

			// 对流水号结尾的四位数字进行判断，分情况进行增加
			if (lg > 0 && lg < 9) {
				s2 = "000" + (lg + 1);
			} else if (lg >= 9 && lg < 99) {
				s2 = "00" + (lg + 1);
			} else if (lg >= 99 && lg < 999) {
				s2 = "0" + (lg + 1);
			} else if (lg >= 999 && lg < 9999) {
				s2 = "" + (lg + 1);
			}
			number = "ZD" + year + s2;
		}
		return number;
	}
	
	
	
	
	
	@Override
	public List<Entertainapplyinfo> listApproveEntertainAppliyInfo(String openId) {
		openId = "oSCXE5DCUDgqBEhxUV48mkO-rqCI";
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",openId,"businessapprove");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				entertainapplyinfo.setTitle("业务招待");
				list.add(entertainapplyinfo);
			}
		}
		
		return list;
	}

	@Override
	public int approveEntertainApplyinfo(int id, String taskId, String result,String comment) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Map<String,Object> map = new HashMap<>();
		Entertainapplyinfo entertain = entertainMapper.getAproveEntertain(id);
		String GZhopenId = "";
		String applyMan = "";
		UserEntity userEntity = userMapper.getopenId(entertain.getOpenId());
		if(userEntity!=null){
			GZhopenId = userEntity.getModifiedby();
			applyMan = userEntity.getCode();
		}
		
		if(result.equals("false")){
			entertain.setStatus("驳回修改");
			entertain.setApproveTime(smf.format(date));
			entertainMapper.updateEntertain(entertain);
			map.put("ApplicationUser", applyMan);
			map.put("result", result);
			workflowUtil.JumpEndActivity(taskId, "businessapply", map);
			/*(taskId, comment, map);*/
			String title = "您好,您的申请已经审批,请及时查看审批结果";
			String mark =entertain.getStatus();
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApproveMan(entertain.getApprover());
			weiEntity.setStatus(entertain.getStatus());
			weiEntity.setApplyTime(smf.format(date));
			try {
				CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 1;
		}
		if(result.equals("true")){
			entertain.setStatus("通过");
			entertain.setApproveTime(smf.format(date));
			entertainMapper.updateEntertain(entertain);
			map.put("afterwardsUser", applyMan);
			map.put("result", result);
			workflowUtil.JumpEndActivity(taskId, "registration", map);
			/*completeApplyTask(taskId, null, map);*/
			String title = "您好,您的申请已经审批,请及时查看审批结果";
			String mark =entertain.getStatus();
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApproveMan(entertain.getApprover());
			weiEntity.setStatus(entertain.getStatus());
			weiEntity.setApplyTime(smf.format(date));
			try {
				CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
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

	@Override
	public List<Entertainapplyinfo> statusEntertainApplyInfo(String openId) {
		return entertainMapper.statusApproveEntertain(openId);
	}
	
	
	// 获取事前审批数据库表中最后一条数据的审批单号 √
	public String getSqlLastNum() {
		String num = "";
		int ob=entertainMapper.getApplyObject();
		if(ob==0){
			return num;
		}
		else{
		return entertainMapper.getSqlLastNumber();}
	}

	
	//事后登记员工查询
	@Override
	public List<Entertainapplyinfo> openIdEntertain(String openId) {
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality", openId,"registration");
		System.out.println("任务是"+tasks);
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		System.out.println("获取Id是"+busAndTaskId);
		System.out.println("获取"+busAndTaskId.keySet());
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				list.add(entertainapplyinfo);
			}
		}
		return list;
	}

	
	//事后登记
	@Override
	public int postregistration(Entertainregisterinfo entertainregisterinfo,String taskId) {
		String enterSum = entertainregisterinfo.getEnterSum();//招待总金额
		String number = entertainregisterinfo.getNumber();
		entertainregisterinfo.setStatus("待财务审核");
		Entertainapplyinfo entertainApplyInfo = entertainMapper.getNumberEntertainApplyInfo(number);
		String masterBudget = entertainApplyInfo.getMasterBudget();//总预算
		double remainingBudget = (Double.parseDouble(masterBudget))-Double.parseDouble(enterSum);
		entertainApplyInfo.setRemainingBudget(String.valueOf(remainingBudget));
		entertainApplyInfo.setRegisterNum(1);
		int id = 0;
		if(null==entertainregisterinfoMapper.getSqlLast()){
			id = 0;
		}else{
			id = entertainregisterinfoMapper.getSqlLast()+1;
		}
		entertainregisterinfo.setId(id);
		Map<String,Object> map = new HashMap<>();
		int flag = entertainregisterinfoMapper.insertEntertainregisterinfo(entertainregisterinfo);
		List<Entertaininvoiceunit> list = entertaininvoiceunitMapper.getInvoiceUnitType();
		boolean check = true;
		for(Entertaininvoiceunit entertaininvoiceunit:list){
			if(entertaininvoiceunit.getInvoiceUnit()!=null&&!entertaininvoiceunit.getInvoiceUnit().equals("")
					&&!entertaininvoiceunit.getInvoiceUnit().equals("null")){
				if(entertaininvoiceunit.getInvoiceUnit().equals(entertainregisterinfo.getInvoiceUnit())){
					check = false;
				}
			}
		}
		
		if(check){
			//将新的加入
			Entertaininvoiceunit entertaininvoiceunit = new Entertaininvoiceunit();
			entertaininvoiceunit.setId(new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(new Date()));
			entertaininvoiceunit.setInvoiceUnit(entertainregisterinfo.getInvoiceUnit());
			entertaininvoiceunitMapper.insertUnitType(entertaininvoiceunit);
		}
		
		if(flag==1){
			  entertainMapper.updateEntertain(entertainApplyInfo);//修改状态
			  //开启业务流
			  workflowUtil.JumpEndActivity(taskId, "financialapproval", null);
		}
		
		return 1;
	}

	
	//财务审批查看
	@Override
	public List<Entertainapplyinfo> finaceEntertainApplyinfo(String openId) {
	
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality","financialapproval");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				entertainapplyinfo.setTitle("业务招待");
				list.add(entertainapplyinfo);
			}
		}
		
		return list;
	}

	@Override
	public int finaceEntertainApprove(Entertainregisterinfo entertainregisterinfo,int id, String taskId, String result,String comment) {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Entertainapplyinfo aproveEntertain = entertainMapper.getAproveEntertain(id);
		UserEntity userEntity = userMapper.getopenId(aproveEntertain.getOpenId());
		String GZhopenId = "";
		if(userEntity!=null){
			GZhopenId = userEntity.getModifiedby();
		}
		Map<String,Object>map = new HashMap<>();
		if(result.equals("true")){
			map.put("cofirm", result);
			aproveEntertain.setStatus("已完成");
			aproveEntertain.setConfirm("1");
			entertainMapper.updateEntertain(aproveEntertain);
			entertainregisterinfo.setStatus("通过");
			entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
			completeApplyTask(taskId, "", map);
			String title = "您好,您的申请已经审批,请及时查看审批结果";
			String mark = "财务审批通过";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApplyMan("刘静");
			weiEntity.setApplyTime("财务审批通过");
			weiEntity.setApplyTime(smf.format(date));
			try {
				CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
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
			aproveEntertain.setStatus("驳回");
			entertainMapper.updateEntertain(aproveEntertain);
			entertainregisterinfo.setStatus("驳回");
			entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
			map.put("cofirm", result);
			map.put("afterwardsUser", aproveEntertain.getManager());
			completeApplyTask(taskId, comment, map);
			String title = "您好,您的申请已经审批,请及时查看审批结果";
			String mark = "财务审批驳回";
			WeiEntity weiEntity = new WeiEntity();
			weiEntity.setApplyMan("财务");
			weiEntity.setApplyTime("财务审批驳回");
			weiEntity.setApplyTime(smf.format(date));
			try {
				CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
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

	@Override
	public List<Entertainapplyinfo> openIdbusinessapply(String openId) {
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality",openId,"businessapply");
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				list.add(entertainapplyinfo);
			}
		}
		
		return list;
	}

	@Override
	public Object getLastSum(String department,String openId) {
		//预算budget
				//剩余预算lastBudget
				//发生额usedBudget
				//使用率percent
				
			// 获取当前年份
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
	        Date date = new Date();
	        String year = sdf.format(date);
	        // 该部门本年度已使用的金额相关信息
	        String syear = year+"%";
	        List<String> list = entertainMapper.getOneDepartmentEntertainSum(department,syear);
	        Double mm=0.0d;//已使用的金额数
	        for(int i=0;i<list.size();i++){
				String sum = list.get(i).toString();
				mm=mm+Double.parseDouble(sum);
	        }
	        
	        
	        /*if(department.equals("财务管理部")){
	        	department="计划财务部";
	        }*/
	       if(department.equals("质量部")){
	        	department="质量安全部";
	        }
	        else if(department.equals("市场部")){
	        	department="市场开发部";
	        }
	        Double lastNum=0.0d;
	        Entertainannualbudget entity = entertainAnnualBudgetMapper.getEntity(department,year);
	        String time=entity.getTime();
	        if(time.equals("0")){
	        	lastNum=0.0d;
	        }
	        else if(time.equals("1")){
	    		lastNum=Double.parseDouble(entity.getBudgetSum0());
	    	}
	    	else if(time.equals("2")){
	    		lastNum=Double.parseDouble(entity.getBudgetSum0())+Double.parseDouble(entity.getBudgetSum1());
	    		
	    	}
	    	else if(time.equals("3")){
	    		lastNum=Double.parseDouble(entity.getBudgetSum0())+Double.parseDouble(entity.getBudgetSum1())+Double.parseDouble(entity.getBudgetSum2());
	    	}
	    	else if(time.equals("4")){
	    		lastNum=Double.parseDouble(entity.getBudgetSum0())+Double.parseDouble(entity.getBudgetSum1())+Double.parseDouble(entity.getBudgetSum2())+Double.parseDouble(entity.getBudgetSum3());
	    	}
	    	else if(time.equals("5")){ 
	    		lastNum=Double.parseDouble(entity.getBudgetSum0())+Double.parseDouble(entity.getBudgetSum1())+Double.parseDouble(entity.getBudgetSum2())+Double.parseDouble(entity.getBudgetSum3())+Double.parseDouble(entity.getBudgetSum4());
	    	}
	    	else{
	    		lastNum=Double.parseDouble(entity.getBudgetSum0())+Double.parseDouble(entity.getBudgetSum1())+Double.parseDouble(entity.getBudgetSum2())+Double.parseDouble(entity.getBudgetSum3())+Double.parseDouble(entity.getBudgetSum4())+Double.parseDouble(entity.getBudgetSum5());
	    	}
	        DecimalFormat df=new DecimalFormat("#.00");
	        System.out.println(mm);
	        double usedBudget = Double.parseDouble(df.format(mm));//已使用金额数
	        double budget =  Double.parseDouble(df.format(lastNum))*10000; // 把预算转为double类型
	        double lastBudget = budget-usedBudget; // 剩余预算保留四位有效数字
	        //int budget = //预算budget
	        System.out.println(""+enter);
	        // 计算百分比
	        // 创建一个数值格式化对象  
	        NumberFormat numberFormat = NumberFormat.getInstance();  
	        // 设置精确到小数点后4位  
	        numberFormat.setMaximumFractionDigits(4);  
	        String percent = numberFormat.format(usedBudget / budget * 100);  
	        System.out.println("比率是"+usedBudget / budget );
	        Double percen = 0.0;
	        if(enter!=null){
	        	percen = Double.parseDouble(enter.replace("%", ""))*0.01;
	        }
//			System.out.println("percen是"+percen);
//			System.out.println("毕竟"+(usedBudget / budget));
			if((usedBudget / budget)>percen){
				List<String> listgzhopenId = dynamicGetRoleUtil.getGzhopenId(department);
				for(String gzhOpenId:listgzhopenId){
					String title = "您好,您部门的预算已经超过90%,请及时查看处理";
					String mark = "预警推送";
					WeiEntity weiEntity = new WeiEntity();
					weiEntity.setApplyMan(department);
					SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
					weiEntity.setApplyTime(smf.format(new Date()));
					try {
						CommonUtil.sendEntertainApplyMessage(gzhOpenId,weiEntity,title,mark);
					} catch (JsonGenerationException e) {
						e.printStackTrace(); 	
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
	        
	        Map<String,String> jsonMap = new HashMap<String,String>();// 定义map
	        jsonMap.put("budget", Double.toString(budget));
	        jsonMap.put("usedBudget", Double.toString(usedBudget));
	        jsonMap.put("lastBudget", Double.toString(lastBudget));
	        jsonMap.put("percent", percent+"%");
			return jsonMap;
	}

	@Override
	public List<Entertainobjecttype> getType() {
		List<Entertainobjecttype> list = entertainobjecttypeMapper.getType();
		List<Entertainobjecttype> listNew = new ArrayList<>();
		LinkedHashSet<Entertainobjecttype> set = new LinkedHashSet<>(list);
		for(Entertainobjecttype entertainobjecttype:set){
			listNew.add(entertainobjecttype);
		}
		
		return listNew;
	}

	@Override
	public List<Entertaininvoiceunit> getInvoiceUnitType() {
		List<Entertaininvoiceunit> list = entertaininvoiceunitMapper.getInvoiceUnitType();
		List<Entertaininvoiceunit> listnew = new ArrayList<>();
		LinkedHashSet<Entertaininvoiceunit> set = new LinkedHashSet<>(list);
		for(Entertaininvoiceunit entertaininvoiceunit :set){
			listnew.add(entertaininvoiceunit);
		}

		return listnew;
				
	}

	/****************web端操作*****************************************************************************************/
	
	@Override
	public List<Entertainapplyinfo> wRGetAllEntertain(Integer start, Integer number) {
		
		/*String openId = "oSCXE5BDhAU_pKzZ0hG7zV9nb55k";*/
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality","financialapproval");
		System.out.println("任务是"+tasks);
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				entertainapplyinfo.setTitle("业务招待");
				list.add(entertainapplyinfo);
			}
		}
		ListSortUtil<Entertainapplyinfo> sortList = new ListSortUtil<Entertainapplyinfo>();
		sortList.sort(list, "applyTime", "desc");
		
		return list;
	}

	@Override
	public List<Entertainregisterinfo> registerDetail(String number,String taskId) {
		List<Entertainregisterinfo> list = entertainregisterinfoMapper.getNumber(number);
		for(Entertainregisterinfo entertainregisterinfo :list){
			entertainregisterinfo.setTaskId(taskId);
		}
		return list;
	}

	@Override
	public int approveX(String number, String taskId) {
		List<Entertainregisterinfo> list = entertainregisterinfoMapper.getNumber(number);
		if(list!=null){
			for(Entertainregisterinfo entertainregisterinfo:list){
				entertainregisterinfo.setStatus("通过");
				entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
			}
		}
		Entertainapplyinfo aproveEntertain = entertainMapper.getNumberEntertainApplyInfo(number);
		aproveEntertain.setStatus("已完成");
		aproveEntertain.setConfirm("1");
		entertainMapper.updateEntertain(aproveEntertain);
		UserEntity userEntity = userMapper.getopenId(aproveEntertain.getOpenId());
		String GZhopenId = "";
		if(userEntity!=null){
			GZhopenId = userEntity.getModifiedby();
		}
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Map<String,Object>map = new HashMap<>();
		map.put("cofirm", "true");
		completeApplyTask(taskId, null, map);
		String title = "您好,您的申请已经审批,请及时查看审批结果";
		String mark = "财务审批通过";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setApplyMan("刘静");
		weiEntity.setApplyTime("财务审批通过");
		weiEntity.setApplyTime(smf.format(date));
		try {
			CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 1;
		
	}

	@Override
	public int notThrough(String number, String taskId) {
		Entertainapplyinfo aproveEntertain = entertainMapper.getNumberEntertainApplyInfo(number);
		aproveEntertain.setStatus("驳回");
		aproveEntertain.setConfirm("0");
		entertainMapper.updateEntertain(aproveEntertain);
		UserEntity userEntity = userMapper.getopenId(aproveEntertain.getOpenId());
		String GZhopenId = "";
		if(userEntity!=null){
			GZhopenId = userEntity.getModifiedby();
		}
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Map<String,Object>map = new HashMap<>();
		map.put("cofirm", "false");
		map.put("afterwardsUser", aproveEntertain.getManager());
		completeApplyTask(taskId, null, map);
		String title = "您好,您的申请已经审批,请及时查看审批结果";
		String mark = "财务审批驳回";
		WeiEntity weiEntity = new WeiEntity();
		weiEntity.setApplyMan("刘静");
		weiEntity.setApplyTime("财务审批驳回");
		weiEntity.setApplyTime(smf.format(date));
		try {
			CommonUtil.sendEntertainApplyMessage(GZhopenId,weiEntity,title,mark);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	public List<Entertainapplyinfo> wGetAllApproved(){
		List<Entertainapplyinfo> listEntertainApplyInfo = entertainMapper.listEntertainApplyInfo();
		ListSortUtil<Entertainapplyinfo> sortList = new ListSortUtil<Entertainapplyinfo>();
		sortList.sort(listEntertainApplyInfo, "applyTime", "desc");
		return listEntertainApplyInfo;
	}

	@Override
	public List<Entertainapplyinfo> wRGetRegisterEntertain() {
		List<Task> tasks = workflowUtil.getTaskByIds("businesshospitality","registration");
		//根据任务获取业务
		Map<String,String> busAndTaskId = workflowUtil.getTaskAndBussIdByTask(tasks);
		List<Entertainapplyinfo> list = new ArrayList<>();
		for(String id:busAndTaskId.keySet()){
			Entertainapplyinfo entertainapplyinfo = entertainMapper.getAproveEntertain(Integer.parseInt(id));
			if(entertainapplyinfo!=null){
				String str = entertainapplyinfo.getId()+"";
				String taskId = busAndTaskId.get(str);
				entertainapplyinfo.setTaskId(taskId);
				entertainapplyinfo.setTitle("业务招待");
				list.add(entertainapplyinfo);
			}
		}
		ListSortUtil<Entertainapplyinfo> sortList = new ListSortUtil<Entertainapplyinfo>();
		sortList.sort(list, "applyTime", "desc");
		return list;
	}

	@Override
	public Entertainapplyinfo getapplyDetail(String number) {
		return entertainMapper.getNumberEntertainApplyInfo(number);
	}

	@Override
	public int insertAllVoucherNum(String number, String paidTime, String voucherNum) {
		List<Entertainregisterinfo> list = entertainregisterinfoMapper.getNumber(number);
		int flag = 0;
		for(Entertainregisterinfo entertainregisterinfo:list){
			entertainregisterinfo.setPaidTime(paidTime);
			entertainregisterinfo.setVoucherNum(voucherNum);
		    flag = entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
		}
		
		return flag;
	}

	@Override
	public List<Entertainregisterinfo> getOneRegister(String number) {
		return entertainregisterinfoMapper.getNumber(number);
	}

	@Override
	public List<Entertainapplyinfo> wgtServApply(Entertainapplyinfo entertainapplyinfo) {
		return entertainMapper.wgtServApply(entertainapplyinfo);
	}

	@Override
	public int updateEntertaion(Entertainregisterinfo entertainregisterinfo) {
		return entertainregisterinfoMapper.updateEntertainregisterinfo(entertainregisterinfo);
	}

	@Override
	public Map<String,Object> searchEntertainApprove(Entertainapplyinfo entertainapplyinfo,String openId,Integer nowPage,Integer pageSize) {
		Map<String,Object> map = new HashMap<>();
		Integer userid = bUserService.getUserIdByOpenId(openId);
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		if(null==entertainapplyinfo.getManager()){
			entertainapplyinfo.setManager(user.getCode());
		}
		
		if(entertainapplyinfo.getManager().equals("1")){
			entertainapplyinfo.setManager(null);
		}
		
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat s = new SimpleDateFormat("yyyy");
		if(entertainapplyinfo.getBeginTime()==null&&entertainapplyinfo.getEndTime()==null){
			entertainapplyinfo.setBeginTime(s.format(new Date())+"-01-01");
			entertainapplyinfo.setEndTime(smf.format(new Date()));
		}
		
		List<Entertainapplyinfo> listnew = entertainMapper.searchEntertainApprove(entertainapplyinfo);
		for(Entertainapplyinfo enterta:listnew){
			enterta.setEntertainregisterinfo(entertainregisterinfoMapper.getNumber(enterta.getNumber()));
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
	public List<Map<String, Object>> countEntertain(String openId) {
		Date date = new Date();
		SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		String format = smf.format(date);
		String manager = "";
		UserEntity userEntity = userMapper.getopenId(openId);
		if(userEntity!=null){
			manager = userEntity.getCode();
		}
		return entertainMapper.countEntertain(manager,format+"%");
	}

	@Override
	public Object getYear() {
		List<Map<String,Integer>>  list = new ArrayList<Map<String,Integer>>();
		 Calendar a=Calendar.getInstance();
		 Integer y = a.get(Calendar.YEAR);
		 List<String> ylist = new ArrayList<String>(); 
		 ylist = entertainAnnualBudgetMapper.year(y+2);
		 for(int i=0;i<ylist.size();i++){
			 Map<String,Integer> jsonMap = new HashMap<String,Integer>();// 定义map
			 jsonMap.put("id", i);
			 jsonMap.put("year", Integer.parseInt(ylist.get(i)));
			 list.add(jsonMap);
			 }
		
		return list;
	}

	@Override
	public List<Entertainannualbudget> wGetNowAnnual() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String year = sdf.format(date);
		return  entertainAnnualBudgetMapper.wGetAnnualBudget(year);
	}

	@Override
	public List<Entertainannualbudget> wGetNowAnnual1(String year) {
		return entertainAnnualBudgetMapper.wGetAnnualBudget(year);
	}

	@Override
	public List<EntertainAnnualInfoDTO> wGetAnnualBudget() {
		//获取当前年份
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String year = sdf.format(date);
        
    	List<EntertainAnnualInfoDTO> newlist= new ArrayList<EntertainAnnualInfoDTO>();
    	List<Entertainannualbudget> list= new ArrayList<Entertainannualbudget>();
    	
    	list = entertainAnnualBudgetMapper.wGetAnnualBudget(year);// 获得当年全部数据
    	for(int i=0;i<list.size();i++){
    	EntertainAnnualInfoDTO aNewList = new EntertainAnnualInfoDTO(); // 最终表的存储变量
    	Entertainannualbudget aList = new Entertainannualbudget(); // 中间表的存储变量
    	String time=list.get(i).getTime(); //获得第i条的编制次数
    	aList=list.get(i); //获得当年数据中的第i条
    	if(time.equals("0")||time.equals("1")){
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		aNewList.setBudgetSum(aList.getBudgetSum0());
    		aNewList.setCopileTime(aList.getCopileTime0());
    		newlist.add(aNewList);
    	}
    	else if(time.equals("2")){
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		Double sum=Double.parseDouble(aList.getBudgetSum0())+Double.parseDouble(aList.getBudgetSum1());
    		aNewList.setBudgetSum(Double.toString(sum));
    		aNewList.setCopileTime(aList.getCopileTime1());
    		newlist.add(aNewList);
    	}
    	else if(time.equals("3")){
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		System.out.println(""+aList);
    		Double sum=Double.parseDouble(aList.getBudgetSum0())+Double.parseDouble(aList.getBudgetSum1())+Double.parseDouble(aList.getBudgetSum2());
    		aNewList.setBudgetSum(Double.toString(sum));
    		aNewList.setCopileTime(aList.getCopileTime2());
    		newlist.add(aNewList);
    	}
    	else if(time.equals("4")){
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		Double sum=Double.parseDouble(aList.getBudgetSum0())+Double.parseDouble(aList.getBudgetSum1())+Double.parseDouble(aList.getBudgetSum2())+Double.parseDouble(aList.getBudgetSum3());
    		aNewList.setBudgetSum(Double.toString(sum));
    		aNewList.setCopileTime(aList.getCompileTime3());
    		newlist.add(aNewList);
    	}
    	else if(time.equals("5")){
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		Double sum=Double.parseDouble(aList.getBudgetSum0())+Double.parseDouble(aList.getBudgetSum1())+Double.parseDouble(aList.getBudgetSum2())+Double.parseDouble(aList.getBudgetSum3())+Double.parseDouble(aList.getBudgetSum4());
    		aNewList.setBudgetSum(Double.toString(sum));
    		aNewList.setCopileTime(aList.getCompileTime4());
    		newlist.add(aNewList);
    	}
    	else{
    		aNewList.setID(i);
    		aNewList.setDepartment(aList.getDepartment());
    		Double sum=Double.parseDouble(aList.getBudgetSum0())+Double.parseDouble(aList.getBudgetSum1())+Double.parseDouble(aList.getBudgetSum2())+Double.parseDouble(aList.getBudgetSum3())+Double.parseDouble(aList.getBudgetSum4())+Double.parseDouble(aList.getBudgetSum5());
    		aNewList.setBudgetSum(Double.toString(sum));
    		aNewList.setCopileTime(aList.getCompileTime5());
    		newlist.add(aNewList);
    	}
    	}
		return newlist; //返回最终表
	}

	@Override
	public Object wGetSelectedUsed(String year) {
		year = year+"%";
		
	       List<Map<String,Object>> list = entertainMapper.getEntertainSum(year);
			Double zh = 0.0d;
			Double cw = 0.0d;
			Double gh = 0.0d;                             
			Double zl = 0.0d;
			Double sc = 0.0d;
			Double xx = 0.0d;
			Double jd = 0.0d;                       
			Double th = 0.0d;
			Double wrj = 0.0d;
			Double gsld=0.0d;
			Double cp = 0.0d;
			Double zhhk = 0.0d;
			
			Map<String,String> jsonMap = new HashMap<String,String>();// 定义map
			for(int i=0;i<list.size();i++){
				Map<String,Object> map=list.get(i);
				String department = map.get("Department").toString();
				String sum = map.get("InvoiceSum").toString();
				
				if(department.equals("信息技术部")){
					xx = xx + Double.parseDouble(sum);
				}
				else if(department.equals("综合办公室")){
					zh = zh + Double.parseDouble(sum);	
				}
				else if(department.equals("财务管理部")){
					cw = cw + Double.parseDouble(sum);	
				}
				else if(department.equals("质量部")){
					zl = zl + Double.parseDouble(sum);	
				}
				else if(department.equals("通航项目部")){
					th = th + Double.parseDouble(sum);	
				}
				else if(department.equals("机电产品部")){
					jd = jd + Double.parseDouble(sum);	
				} 
				else if(department.equals("市场部")){
					sc = sc + Double.parseDouble(sum);	
				}
				else if(department.equals("规划投资部")){
					gh = gh + Double.parseDouble(sum);	 
				}
				else if(department.equals("无人机项目部")){
					wrj = wrj + Double.parseDouble(sum);
				}
				else if(department.equals("测评中心")){
					cp = cp + Double.parseDouble(sum);
				}else if(department.equals("智慧航空工程实验室")){
					zhhk = zhhk + Double.parseDouble(sum);
				}
				else {
					gsld=gsld + Double.parseDouble(sum);
				}
			}
			jsonMap.put("综合办公室", Double.toString(zh));
			jsonMap.put("财务管理部", Double.toString(cw));
			jsonMap.put("规划投资部", Double.toString(gh));
			jsonMap.put("质量安全部", Double.toString(zl));
			jsonMap.put("市场开发部", Double.toString(sc));
			jsonMap.put("信息技术部", Double.toString(xx));
			jsonMap.put("机电产品部", Double.toString(jd));
			jsonMap.put("通航项目部", Double.toString(th));
	 		jsonMap.put("无人机项目部", Double.toString(wrj));
	 		jsonMap.put("公司领导", Double.toString(gsld));
	 		jsonMap.put("测评中心", Double.toString(cp));
	 		jsonMap.put("智慧航空工程实验室", Double.toString(zhhk));
			
			return jsonMap;
	}

	@Override
	public int export(List<Entertainapplyinfo> nlist, String path) {
		String ids = "";
		for(Entertainapplyinfo entertainapplyinfo:nlist){
			ids+=entertainapplyinfo.getNumber()+",";
		}
		
		List<Entertainapplyinfo> entertainList = getEntertainListByNum(ids.split(","));
		return exportExcel(entertainList, path);
		
	}
	
	
	
	
	
	public int exportExcel(List<Entertainapplyinfo> entertainList, String path)
	  {
	    String sheetName = "业务招待明细表";
	    try {
	      XSSFWorkbook workbook = new XSSFWorkbook();

	      XSSFSheet sheet = workbook.createSheet(sheetName);

	      sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 22));

	      XSSFRow titleRow = sheet.createRow(0);

	      XSSFCellStyle titleStyle = workbook.createCellStyle();

	      titleStyle.setAlignment(HorizontalAlignment.CENTER);

	      XSSFFont titleFont = workbook.createFont();
	      titleFont.setFontHeight(16.0D);

	      titleStyle.setFont(titleFont);

	      XSSFCell titleCell = titleRow.createCell(0);

	      titleCell.setCellValue(sheetName);
	      titleCell.setCellStyle(titleStyle);

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
	      sheet.autoSizeColumn(18);
	      sheet.autoSizeColumn(19);
	      sheet.autoSizeColumn(20);
	      sheet.autoSizeColumn(21);
	      sheet.autoSizeColumn(22);

	      XSSFCellStyle style = workbook.createCellStyle();
	      style.setAlignment(HorizontalAlignment.CENTER);

	      XSSFRow rowHeader = sheet.createRow(1);
	      XSSFCell cell = rowHeader.createCell(0);
	      cell.setCellValue("序号");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(1);
	      cell.setCellValue("审批单号");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(2);
	      cell.setCellValue("部门");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(3);
	      cell.setCellValue("申请时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(4);
	      cell.setCellValue("招待对象");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(5);
	      cell.setCellValue("事由");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(6);
	      cell.setCellValue("招待人数");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(7);
	      cell.setCellValue("陪同人数");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(8);
	      cell.setCellValue("人均标准");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(9);
	      cell.setCellValue("总预算");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(10);
	      cell.setCellValue("招待类别");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(11);
	      cell.setCellValue("经办人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(12);
	      cell.setCellValue("审批人");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(13);
	      cell.setCellValue("发票开具日期");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(14);
	      cell.setCellValue("开票内容");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(15);
	      cell.setCellValue("发票金额");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(16);
	      cell.setCellValue("发票张数");
	      cell.setCellStyle(style);

	      cell = rowHeader.createCell(17);
	      cell.setCellValue("酒水金额");
	      cell.setCellStyle(style);

	      cell = rowHeader.createCell(18);
	      cell.setCellValue("总金额");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(19);
	      cell.setCellValue("报销时间");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(20);
	      cell.setCellValue("凭证号");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(21);
	      cell.setCellValue("发票开具单位");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(22);
	      cell.setCellValue("备注");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(23);
	      cell.setCellValue("是否补录");
	      cell.setCellStyle(style);
	      cell = rowHeader.createCell(24);
	      cell.setCellValue("实际申请日期");
	      cell.setCellStyle(style);

	      int index = 1;
	      Entertainapplyinfo entertain = new Entertainapplyinfo();

	      if (entertainList.size() > 0) {
	        for (int i = 0; i < entertainList.size(); i++)
	        {
	          index++;

	          entertain = (Entertainapplyinfo)entertainList.get(i);
	          int hs = 0;
	          if(entertain.getEntertainregisterinfo()!=null){
	        	  hs = entertain.getEntertainregisterinfo().size();
		          for (int r = 0; r < hs; r++) {
		            XSSFRow row = sheet.createRow(r + index);

		            for (int j = 0; j < 13; j++) {
		              sheet.addMergedRegion(new CellRangeAddress(index, index + hs - 1, j, j));
		            }

		            XSSFCell rowCell = row.createCell(0);
		            rowCell.setCellValue(i + 1);
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(1);
		            rowCell.setCellValue(entertain.getNumber());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(2);
		            rowCell.setCellValue(entertain.getDepartment());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(3);
		            rowCell.setCellValue(entertain.getApplyTime());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(4);
		            rowCell.setCellValue(entertain.getEntertainObject());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(5);
		            rowCell.setCellValue(entertain.getEntertainReason());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(6);
		            rowCell.setCellValue(entertain.getEntertainNum());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(7);
		            rowCell.setCellValue(entertain.getAccompanyNum());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(8);
		            rowCell.setCellValue(entertain.getPerBudget());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(9);
		            rowCell.setCellValue(entertain.getMasterBudget());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(10);
		            rowCell.setCellValue(entertain.getEntertainCategory());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(11);
		            rowCell.setCellValue(entertain.getManager());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(12);
		            rowCell.setCellValue(entertain.getApprover());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(13);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceDate());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(14);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceContent());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(15);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(16);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceNum());
		            rowCell.setCellStyle(style);
		            String wineSum = "0";
		            if ((((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getWineSum() == null) || (((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getWineSum().trim().equals("")) || 
		              (((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum().contains(" "))) {
		              wineSum = "0";
		            }
		            else {
		              wineSum = ((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getWineSum();
		            }
		            String invoiceSum = "0";
		            if ((((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum() == null) || (((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum().trim().equals("")) || 
		              (((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum().contains(" "))) {
		              invoiceSum = "0";
		            }
		            else {
		              invoiceSum = ((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceSum();
		            }
		            rowCell = row.createCell(17);
		            rowCell.setCellValue(wineSum);
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(18);
		            System.out.println(invoiceSum);
		            System.out.print(wineSum);
		            rowCell.setCellValue(Double.parseDouble(invoiceSum) + Double.parseDouble(wineSum));
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(19);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getPaidTime());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(20);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getVoucherNum());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(21);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getInvoiceUnit());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(22);
		            rowCell.setCellValue(((Entertainregisterinfo)entertain.getEntertainregisterinfo().get(r)).getRemark());
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(23);
		            rowCell.setCellValue("1".equals(entertain.getIfBefore()) ? "是" : "否");
		            rowCell.setCellStyle(style);

		            rowCell = row.createCell(24);
		            rowCell.setCellValue(entertain.getBeforeDate());
		            rowCell.setCellStyle(style);
		          }
	          }
	          
	          index = index + hs - 1;
	        }

	      }

	      FileOutputStream out = new FileOutputStream(path);
	      workbook.write(out);
	      return 1;
	    } catch (Exception e) {
	      e.printStackTrace();
	    }return 0;
	  }

	
	
	/**获得全部已完成的招待信息*/
	  public List<Entertainapplyinfo> getEntertainListByNum(String[] nlist) {
		/*List<EntertainApplyInfoEntity> listAll = entertainApplyInfoDAO.getApplyCompleted();*/// 最终返回的
		//不只取当前页记录数，取出全部 nlist（当前页记录数）->nlist1（全部）   3/27
		/*String[] nlist1 = new String[listAll.size()];
		int z=0;
		for(EntertainApplyInfoEntity en:listAll){
			nlist1[z] = en.getNumber();
			z++;
		}*/
		List<Entertainapplyinfo> list = new ArrayList<Entertainapplyinfo>();// 最终返回的list
		List<Entertainapplyinfo> apply = new ArrayList<Entertainapplyinfo>(); // 存贮事前表单的list
		for (int t = 0;t<nlist.length;t++)//根据number取出要导出的事前信息
		{
			Entertainapplyinfo app = new Entertainapplyinfo(); //存储变量
			Entertainapplyinfo entertain = new Entertainapplyinfo(); // 存储变量
			app = entertainMapper.getNumberEntertainApplyInfo(nlist[t]);
			apply.add(app);
			List<Entertainregisterinfo> register = entertainregisterinfoMapper.getNumber(nlist[t]);// 取出对应的已完成的事后表
			entertain.setId(app.getId());
			entertain.setNumber(app.getNumber());
			entertain.setDepartment(app.getDepartment());
			entertain.setApplyTime(app.getApplyTime());
			entertain.setEntertainObject(app.getEntertainObject());
			entertain.setEntertainReason(app.getEntertainReason());
			entertain.setEntertainNum(app.getEntertainNum());
			entertain.setAccompanyNum(app.getAccompanyNum());
			entertain.setPerBudget(app.getPerBudget());
			entertain.setMasterBudget(app.getMasterBudget());
			entertain.setEntertainCategory(app.getEntertainCategory());
			entertain.setManager(app.getManager());
			entertain.setApprover(app.getApprover());
			entertain.setStatus(app.getStatus());
			entertain.setRemark(app.getRemark());
			entertain.setIfWine(app.getIfWine());
			entertain.setWineType(app.getWineType());
			entertain.setWineNum(app.getWineNum());
			entertain.setIfBefore(app.getIfBefore());
			entertain.setBeforeDate(app.getBeforeDate());
			if(register!=null&&!register.isEmpty()){
				entertain.setWineSum(register.get(0).getWineSum());
				entertain.setEeterSum(register.get(0).getEnterSum());
				entertain.setPersonSum(register.get(0).getPersonSum());
			}
			entertain.setEntertainregisterinfo(register);
			list.add(entertain);
		}
		return list;

	}

	@Override
	public int wSetAdjust(String param) {
		String[] list = param.split(",");
		Date dt=new Date();
	    SimpleDateFormat matter=new SimpleDateFormat("yyyy-MM-dd");
	    String df = matter.format(dt);
	    
	    // 获得修改的年份
	    String[] para = list[11].split(":"); 
	    String year = para[1];
	    
		for(int i=0;i<list.length-1;i++){
			if(!list[i].equals("")){
				String[] content=list[i].split(":");
				String time = entertainAnnualBudgetMapper.getTimeById(content[0],year);// 根据id获得Time的值
				Entertainannualbudget entertainannualbudget = new Entertainannualbudget();
				//根据time的值判断更新位置
				if(time.equals("0")&&content.length==2){
					entertainannualbudget.setBudgetSum0(content[1]);
					entertainannualbudget.setCopileTime0(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum(content[0], content[1], df, year);
				}
				else if(time.equals("1")&&content.length==2){
					entertainannualbudget.setBudgetSum1(content[1]);
					entertainannualbudget.setCopileTime1(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum0(content[0], content[1], df, year);
				}
				else if(time.equals("2")&&content.length==2){
					entertainannualbudget.setBudgetSum2(content[1]);
					entertainannualbudget.setCopileTime2(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum1(content[0], content[1], df, year);
				}
				else if(time.equals("3")&&content.length==2){
					entertainannualbudget.setBudgetSum3(content[1]);
					entertainannualbudget.setCompileTime3(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum2(content[0], content[1], df, year);
				}
				
				else if(time.equals("4")&&content.length==2){
					entertainannualbudget.setBudgetSum4(content[0]);
					entertainannualbudget.setCompileTime4(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum3(content[0], content[1], df, year);
				}	
				else if(time.equals("5")&&content.length==2){
					entertainannualbudget.setBudgetSum5(content[0]);
					entertainannualbudget.setCompileTime5(df);
					entertainannualbudget.setYear(year);
					entertainannualbudget.setTime(time);
					entertainannualbudget.setOrder(Integer.parseInt(content[0]));
					entertainAnnualBudgetMapper.updateAnnualBudget(entertainannualbudget);
					//entertainAnnualBudgetDAO.updateBudgetSum4(content[0], content[1], df, year);
				}
			}
			else{
				continue;
			}
		}
	 return 0;
	}

	@Override
	public List<Entertainhospitality> wGetHospitality() {
		
		
		return null;
	}

	@Override
	public List<Entertainhospitality> wGetAnnualBudgethospitality() {
		SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		String year = smf.format(new Date());
		String type = "1";//1代表业务招待预算费
		return entertainHospitalityMapper.getEntertainhospitality(type, year);
	}

	//根据年份查询每一年的招待预算
	@Override
	public List<Entertainhospitality> wGetSearchAnnual(String year) {
		System.out.println("年份："+year);
		String type = "1";//1代表业务招待预算费
		return entertainHospitalityMapper.getEntertainhospitality(type,year);
	}

	//根据对应基本id获取相应的调整详情
	@Override
	public List<EntertainhospitalitySubsidiary> getAnnualBudgetDetail(String hospitalityId) {
		return entertainHospitalitysubsidiaryMapper.getAnnualBudgetDetail(hospitalityId);
	}

	//调整金额
	@Override
	public Object wSetAdjust(String id, String money, String datenew, String date) {
		List<String> departmentname=new ArrayList<>();
		String[] split = id.split(",");
		String[] split2 = money.split(",");
		//获取要调整的部门名称
		for(int i=0;i<split.length;i++){
			departmentname.add(entertainHospitalityMapper.getdepartmentByhospitalId(split[i]));
		}
		//添加相应的调整详情
		for(int j=0;j<split.length;j++){
			EntertainhospitalitySubsidiary enter=new EntertainhospitalitySubsidiary();
			enter.setAdjustmentTime(datenew);
			enter.setHospitalityId(split[j]);
			enter.setSum(split2[j]);
			entertainHospitalitysubsidiaryMapper.insertEntertainhospitalitysubsidiary(enter);
		}
		//修改年度调整信息
		List<Entertainannualbudget> budget = entertainAnnualBudgetMapper.wGetAnnualBudget(date);
		for (Entertainannualbudget enterbudget : budget) {
			for(int m=0;m<split.length;m++){
				if(departmentname.get(m).equals(enterbudget.getDepartment())){
					if("0".equals(enterbudget.getTime())){
						enterbudget.setBudgetSum1(split2[m]);
						enterbudget.setCopileTime1(datenew);
						enterbudget.setTime("1");
						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
					}
					else if("1".equals(enterbudget.getTime())){
						enterbudget.setBudgetSum2(split2[m]);
						enterbudget.setCopileTime2(datenew);
						enterbudget.setTime("2");
						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
					}
					else if("2".equals(enterbudget.getTime())){
						enterbudget.setBudgetSum3(split2[m]);
						enterbudget.setCompileTime3(datenew);
						enterbudget.setTime("3");
						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
					}
					else if("3".equals(enterbudget.getTime())){
						enterbudget.setBudgetSum4(split2[m]);
						enterbudget.setCompileTime4(datenew);
						enterbudget.setTime("4");
						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
					}
					else if("4".equals(enterbudget.getTime())){
						enterbudget.setBudgetSum5(split2[m]);
						enterbudget.setCompileTime5(datenew);
						enterbudget.setTime("5");
						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
					}
//					if("5".equals(enterbudget.getTime())){
//						enterbudget.setBudgetSum5(split2[m]);
//						enterbudget.setCompileTime5(datenew);
//						enterbudget.setTime("6");
//						entertainAnnualBudgetMapper.updateAnnualBudget(enterbudget);
//					}
					
				}
			}
		}
		return 1;
	}
}
