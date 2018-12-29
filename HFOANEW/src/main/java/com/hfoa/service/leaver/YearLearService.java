package com.hfoa.service.leaver;

import java.util.List;
import java.util.Map;

import com.hfoa.entity.vacation.LearYear;

/**
 */
public interface YearLearService {
	
	public int insertYearLeaver(LearYear learYear);
	
	public int updateYearLeaver(LearYear learYear);
	
	public int deleteYearLeaver(String id);
	
	public LearYear selectYearLeaver(String id);
	
	public List<LearYear> listYearLeaver(String openId);//审批人查询
	
	public LearYear selectUserIDLeaver(String user_id);
	
	public LearYear getOpenIdLeaver(String openId,String year);
	
	public Map<String,Object> searLeaver(LearYear learYear,String openId,Integer nowPage,Integer pageSize);
	
	public List<Map<String,Object>>executed(String openId,String levae_id);
	
	public List<Map<String,Object>>getUpdate(String openId,String leave_id);
	
	//查询所有待执行年假
	public List<Map<String,Object>>listExecuted();
	
	public List<LearYear> searchLeaver(LearYear learYear);
	
	public int selectNum(String approveManOpenId);
	
	
	public int leaverApprove(String id,String taskId,String result,String comment);
	
	
	public List<Map<String,Object>> countLeaver(String openId);
	
	
	
	//部门退回待修改
	public int updateReject(String id);
	//部门审批通过
	public int updatePass(String id);
	//综合部确认
	public int updateAffirm(String id);
	//员工确认休假完成
	public int updateEnd(String id);
	//员工放弃
	public int updateRenounce(String id);
	//转接第二年
	public int updateTransfer(String id);
	//现金补偿
	public int updateCash(String id);
	//异常
	public int updateAbnormal(String id);
	
	//无效
	public int updateState(String id);
	
	
	public int exportYearLeaver(LearYear learYear,String filePath);
	
}
