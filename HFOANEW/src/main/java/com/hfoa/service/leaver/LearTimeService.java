package com.hfoa.service.leaver;

import java.util.List;

import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;

/**
 */
public interface LearTimeService {
	
	public int insertTime(LearTime learTime);
	
	public int deleteTime(String leaveId);
	
	public LearTime getLearTime(String id);
	
	public int updateState(String id);
	
	public List<LearTime> listLear(String leave_id);
	
	public List<LearTime> selectLearTime(String leave_id);
	
	public List<LearTime> seartTime(String leave_id);
	
	public int updateSfyc(String id);
	
	public int updateBach(LearTime list);
	
	public int updateStateStatus(String id);
	
	public int updateStateLeave(String leave_id);
	
}
