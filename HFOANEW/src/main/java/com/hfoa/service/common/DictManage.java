package com.hfoa.service.common;

import java.util.List;
import java.util.Map;

import com.hfoa.entity.common.DictEntity;

/**
 * 
 * @author wzx
 *
 */

public interface DictManage {
	
	//根据节点获取子菜单内容
	List<DictEntity> getByNodeType(String nodeType);
	
	//删除单级菜单
	int delete(String id);

	//修改字典内容
	int update(DictEntity dict);
//	
	List<Map<String,Object>> getMenuByNodeType(String nodeType,String userId);

	//添加字典
	int insert(DictEntity dict);

	//删除父节点及其子节点
	Object deleteAllModule(String id);

	//根据节点获取节点信息
	DictEntity getNodeInfo(String id);

	//根据标识和父节点查找描述信息
	String getByParentAndInfo(String info, String parentId);

	//根据父节点跟含义获取节点标识
	String getByTextAndParentId(String text, String parentId);
	
}
