package com.hfoa.dao.entertain;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.hfoa.entity.entertain.Entertaininvoiceunit;


/**
 * 
 * @author wzx
 * 业务招待dao接口
 */
public interface EntertaininvoiceunitMapper {
	
	List<Entertaininvoiceunit> getInvoiceUnitType();//获取开票单位
	
	int insertUnitType(Entertaininvoiceunit entertaininvoiceunit);
	
	@Insert({"insert into B_EntertainInvoiceUnit (InvoiceUnit) values(#{invoiceUnit,jdbcType=VARCHAR})"})
	void insertInvoiceUnit(@Param("invoiceUnit")String invoiceUnit);
	
	
}