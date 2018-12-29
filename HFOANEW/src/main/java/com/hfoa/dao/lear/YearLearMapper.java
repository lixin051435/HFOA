package com.hfoa.dao.lear;

import java.util.List;
import java.util.Map;

import com.hfoa.entity.vacation.LearYear;


public interface YearLearMapper {
	
	public int insert(LearYear learYear);
	
	public int updateByPrimaryKey(LearYear learYear);
	
	public int deleteByPrimaryKey(String id);
	
	public LearYear findById(String id);
	
	public List<LearYear> listYear(String approveMan);
	
	public LearYear findUserID(String user_id);
	
	public LearYear getOpenIdLeaver(String openId,String year);
	
	public int updateReject(String id);
	
	public int updatePass(String id);
	
	public int updateAffirm(String id);
	
	public int updateEnd(String id);
	
	public int updateRenounce(String id);
	
	public int updateTransfer(String id);
	
	public int updateCash(String id);
	
	public int updateAbnormal(String id);
	
	public int updateState(String id);
	
	public int selectNum(String approveManOpenId);
	
	public List<Map<String,Object>> countLeaver(String openId,String applyTime);
	
	public List<Map<String,Object>>executed(String openId,String leve_id);
	
	public List<Map<String,Object>>getUpdate(String openId,String leave_id);
	
	
	public List<LearYear> searLeaver(LearYear learYear);//查询
	
	
	public List<Map<String,Object>> listExecuted();//查询所有待执行年假
	
	public List<LearYear> searchLeaver(LearYear learYear);


}