package com.hfoa.dao.entertain;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.entertain.Entertainobjecttype;
import com.hfoa.service.user.BUserDepartment;
/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertainobjecttypeMapper {
	
	List<Entertainobjecttype> getType();
	
	int insertType(Entertainobjecttype entertainobjecttype);
	
	Entertainobjecttype selectMax();
	
	@Insert({"insert into b_entertainobjecttype (oName) values(#{sendto,jdbcType=VARCHAR})"})
	void insertOName(@Param("sendto")String sendto);
	
}