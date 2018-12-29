package com.hfoa.service.printing;

import java.util.List;
import java.util.Set;

import com.hfoa.entity.printing.BGzapplyinfo;

/**
 * 
 * @author wzx
 * 用印申请service接口
 */
public interface BGzapplyinfoService {

	//修改申请信息
	int update(BGzapplyinfo bGzapplyinfo);

	//添加申请信息
	int insert(BGzapplyinfo bGzapplyinfo);

	//删除申请信息
	int deleteById(int id);

	//根据id获取用印申请信息
	BGzapplyinfo getById(int id);

	//根据业务状态获取业务信息
	List<BGzapplyinfo> findTasksByStatus(Set<String> keySet, String string);

	//查看当天相同部门办理业务数量
	int getApprovalCount(String string);

	//根据申请人账号查找申请人的申请信息
	List<BGzapplyinfo> findTasksByUserCode(Set<String> keySet, String code);

	//根据sql语句获取公章申请信息
	List<BGzapplyinfo> getBySql(String result);

	//根据申请人账号获取所有业务信息
	List<BGzapplyinfo> getByAppUser(String code);

	//分页查询用印申请信息
	List<BGzapplyinfo> getAll(int start, int number);

	//获取用印申请总数量
	int getAllCount();

	//根据sql语句查询用印申请数量
	int getCountBySQL(String countResult);

	//导出表单
	int export(String[] nlist, String filePath);

	//查询待审批或者已经通过的业务信息
	List<BGzapplyinfo> findTasksByStatuses(Set<String> keySet, String status1, String status2);

	//根据流程审批人以及状态获取业务信息
	List<BGzapplyinfo> findApprovalAndLable(String code, int i);

	//根据流程业务主管审批人以及状态获取业务信息
	List<BGzapplyinfo> findBussinessAndLable(String code, int i);

	//根据流程确认人审批人以及状态获取业务信息
	List<BGzapplyinfo> findConfirmAndLable(String code, int i);

	//获取所有业务信息
	List<BGzapplyinfo> getAllInfo();

	List<BGzapplyinfo> findTasksLikeStatuses(Set<String> keySet, String status);

	//根据审批人账号获取审批信息
	Object selectNum(String code);

	//获取所有发往单位
	List<String> getAllSendUnit(String companyName);

	//获取所有发往单位
	Set<String> getAllSendTo();

	//条件查询用印申请信息
	List<BGzapplyinfo> findTasksByCase(Set<String> keySet, String status, BGzapplyinfo gzapplyinfo);

	//根据申请人获取完成的信息
	List<BGzapplyinfo> getFinishByAppUser(String code);

	//导数据
	List<BGzapplyinfo> getAllPass();

	//获取所有进行中的业务申请信息
	List<BGzapplyinfo> getAllRunTime(int start, int number);

	//获取进行中的业务数量
	int getAllRunTimeCount();

	//导出进行中的用印申请信息
	int exportRunTime(String[] nlist, String filePath);

}
