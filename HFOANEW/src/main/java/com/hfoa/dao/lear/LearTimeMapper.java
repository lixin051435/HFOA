package com.hfoa.dao.lear;

import java.util.List;
import java.util.Map;

import com.hfoa.entity.vacation.LearTime;

public interface LearTimeMapper {
	
	public int insert(LearTime learTime);
	
	public int deleteleave(String leave_Id);
	
	public int updateBatch(List<LearTime> list);
	
	public LearTime getLearTime(String id);
	
	public int updateState(String id);
	
	public List<LearTime> listLearTime(String leave_id);
	
	public List<LearTime> seartTime(String leave_id);
	
	public List<LearTime> selectLearTime(String leave_id);
	
	public int updateSfyc(String id);
	
	public int updateTime(LearTime learTime);
	
	public int updateStateLeave(String leave_id);
	
	
	public int updateStateStatus(String leave_id);


}