package com.hfoa.service.permission;

import java.util.List;

import com.hfoa.entity.permission.BProcess;

/**
 * 
 * @author wzx
 * 流程service接口
 */
public interface BProcessService {

	//分页查询流程信息
	List<BProcess> processDisplayByPage(int start, int number);

	//获取流程信息总数量
	int getAllCount();

	//获取流程模块
	List<BProcess> getByParentId(int i);

	//根据sql语句查询流程信息
	List<BProcess> getBySql(String result);

	//根据sql语句查询流程信息数量
	int getCountBysql(String countResult);

	//导出表单
	int export(String[] nlist, String filePath);

	//根据节点获取流程信息
	Object getByNodeType(String parentId);

	//根据角色id获取流程id
	List<Integer> getProcessByRole(int roleId);

	//删除角色流程中间表角色id相同信息
	void deleteMiddleRole(int id);

	//根据角色id获取流程id集合
	List<Integer> getProcessByRoleId(int roleId);

	//根据节点名称获取流程信息
	BProcess getprocessByName(String string);

	//根据id查询流程节点信息
	BProcess selectById(Integer pid);

	//根据业务节点名称获取所有节点内容
	List<BProcess> getByName(String string);

	//根据流程id获取流程内容
	BProcess getById(Integer id);

}
