package com.hfoa.service.impl.permissionimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.permission.BPermissionMapper;
import com.hfoa.entity.common.DictEntity;
import com.hfoa.entity.common.PageDTO;
import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.service.permission.BPermissionService;

/**
 * 
 * @author wzx
 * 权限service实现类
 */
@Service
public class BPermissionServiceImpl implements BPermissionService{

	@Autowired
	private BPermissionMapper bPermissionMapper;

	//获取所有权限信息
	@Override
	public List<BPermissionEntity> getAllPermission() {
		return bPermissionMapper.getAllPermission();
	}

	//修改权限信息
	@Override
	public int update(BPermissionEntity bPermissionEntity) {
		return bPermissionMapper.update(bPermissionEntity);
	}

	//分页查询权限信息
	@Override
	public List<BPermissionEntity> permissionDisplayByPage(int start, int number) {
		return bPermissionMapper.permissionDisplayByPage(start,number);
	}

	//查询权限数量
	@Override
	public int getAllCount() {
		return bPermissionMapper.getAllCount();
	}

	//添加权限信息
	@Override
	public int insert(BPermissionEntity permission) {
		return bPermissionMapper.insert(permission);
	}

	//删除权限信息
	@Override
	public int deleteById(int id) {
		return bPermissionMapper.deleteById(id);
	}

	//权限的模糊分页查询
	@Override
	public List<BPermissionEntity> permissionVagueByPage(int start, int number, String title) {
		title="%"+title+"%";
//		PageDTO page=new PageDTO();
//		page.setNumber(number);
//		page.setStart(start);
//		page.setTitle(title);
		return bPermissionMapper.permissionVagueByPage(start,number,title);
	}

	//获取模糊查询的权限数量
	@Override
	public int getVagueCount(String title) {
		title="%"+title+"%";
		return bPermissionMapper.getVagueCount(title);
	}

	//根据根权限获取所有子节点
	@Override
	public List<BPermissionEntity> getByNodeType(String parentId) {
		List<BPermissionEntity> list=bPermissionMapper.getByNodeType(Integer.parseInt(parentId));
		List<BPermissionEntity> listDTO=new ArrayList<BPermissionEntity>();
		for(int i=0;i<list.size();i++){
			BPermissionEntity process=list.get(i);
			if(getChildspermissByRootId(process.getParentid()).size()>0){
				process.setState("closed");
			}else{
				process.setState("open");
			}
			process.setText(process.getPermissionname());
			listDTO.add(process);
		}
		return listDTO;
	}
	//获取子权限
	public List<BPermissionEntity> getChildspermissByRootId(int id) {
		return bPermissionMapper.getByNodeType(id);
	}

	//删除角色权限中间表
	@Override
	public void deleteMiddleRole(int id) {
		bPermissionMapper.deleteMiddleRole(id);
	}

	//删除相同角色id的角色权限中间表
	@Override
	public void deleteMiddlePermission(int id) {
		bPermissionMapper.deleteMiddlePermission(id);
	}

	//根据角色id获取权限
	@Override
	public List<Integer> getPermissionByRole(int roleId) {
		return bPermissionMapper.getPermissionIdByRole(roleId);
	}

	//根据id获取权限信息
	@Override
	public BPermissionEntity getById(Integer id) {
		return bPermissionMapper.getById(id);
	}

	//根据角色回显权限
//	@Override
//	public List<BPermissionEntity> getPermissionByRole(int roleId) {
//		int parentId=1;
//		List<BPermissionEntity> list=bPermissionMapper.getByNodeType(parentId);
//		List<Integer> permissionid=bPermissionMapper.getPermissionIdByRole(roleId);
//		List<BPermissionEntity> listDTO=new ArrayList<BPermissionEntity>();
//		for (BPermissionEntity bPermissionEntity : list) {
//			
//			for (Integer permissionId : permissionid) {
//				if(getChildspermissByRootId(bPermissionEntity.getParentid()).size()<0&&bPermissionEntity.getId()==permissionId){
//					bPermissionEntity.setChecked("true");
//				}else{
//					bPermissionEntity.setChecked("false");
//				}
//				
////				if(bPermissionEntity.getId()==permissionId){
////				
////				}else{
////					
////				}
//			}
//			bPermissionEntity.setState("open");
//			bPermissionEntity.setText(bPermissionEntity.getPermissionname());
//			listDTO.add(bPermissionEntity);
//		}
//		return listDTO;
//	}
}
