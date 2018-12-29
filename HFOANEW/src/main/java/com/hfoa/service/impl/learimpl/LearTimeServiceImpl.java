package com.hfoa.service.impl.learimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfoa.dao.lear.LearTimeMapper;
import com.hfoa.dao.lear.YearLearMapper;
import com.hfoa.entity.vacation.LearTime;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.service.leaver.LearTimeService;
import com.hfoa.service.leaver.YearLearService;


/**
 *年假附属表                                                                                                                                                                                                                                                    
 */
@Service
public class LearTimeServiceImpl implements LearTimeService{
	
	
	@Autowired
	private LearTimeMapper learTimeMapper;

	@Transactional
	@Override
	public int insertTime(LearTime learTime) {
		System.out.println("年假附属表"+learTime);
		return learTimeMapper.insert(learTime);
	}

	@Override
	public int deleteTime(String leave_Id) {
		return learTimeMapper.deleteleave(leave_Id);
	}

	@Override
	public LearTime getLearTime(String id) {
		return learTimeMapper.getLearTime(id);
	}

	@Override
	public int updateState(String id) {
		return learTimeMapper.updateState(id);
	}

	@Override
	public List<LearTime> listLear(String leave_id) {
		return learTimeMapper.listLearTime(leave_id);
	}

	@Override
	public int updateSfyc(String id) {
		return learTimeMapper.updateSfyc(id);
	}

	@Override
	public int updateBach(LearTime learTime) {
		return learTimeMapper.updateTime(learTime);
	}

	@Override
	public int updateStateLeave(String leave_id) {
		return learTimeMapper.updateStateLeave(leave_id);
	}

	@Override
	public List<LearTime> seartTime(String leave_id) {
		return learTimeMapper.seartTime(leave_id);
	}

	@Override
	public List<LearTime> selectLearTime(String leave_id) {
		return learTimeMapper.selectLearTime(leave_id);
	}

	@Override
	public int updateStateStatus(String id) {
		
		return learTimeMapper.updateStateStatus(id);
	}

	

	

}
