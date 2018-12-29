package com.hfoa.service.impl.roleimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.role.BRoleMapper;
import com.hfoa.entity.role.BRoleClass;
import com.hfoa.entity.role.BRoleEntity;
import com.hfoa.service.role.BRoleService;

/**
 * 
 * @author wzx
 * 角色service实现类
 */
@Service
public class BRoleServiceImpl implements BRoleService{

	@Autowired
	private BRoleMapper bRoleMapper;

	//根据角色名称查找角色id
	@Override
	public int getRoleIdByRoleName(String roleName) {
		return bRoleMapper.getRoleIdByRoleName(roleName);
	}

	//根据功能模块编号获取所有具有该功能模块的角色id
	@Override
	public Set<Integer> getRoleIdFromClass(String moduleNum) {
		return bRoleMapper.getRoleIdFromClass(moduleNum);
	}

	//获取用户在公共类型里边级别最高的角色编号
	@Override
	public Integer getGradeFromClass(Integer roleid) {
		return bRoleMapper.getGradeFromClass(roleid);
	}
	
	@Override
	public BRoleClass getmoney(int roleid){
	
		return bRoleMapper.getMoney(roleid);
	}

	//根据用户在公共类型里边的级别查询用户角色id
	@Override
	public int getLeaderRole(Integer grade) {
		return bRoleMapper.getLeaderRole(grade);
	}

	//查询公司领导级别
	@Override
	public int getLeaderGrade() {
		return bRoleMapper.getLeaderGrade();
	}

	//在公共类别里边根据roleid查询类别
	@Override
	public String getModuleNumByRole(int minRoleId) {
		return bRoleMapper.getModuleNumByRole(minRoleId);
	}

	//根据模块类型查询该类型下所有角色id
	@Override
	public List<Integer> getRoleClassList(String modulenum) {
		return bRoleMapper.getRoleClassList(modulenum);
	}

	//根据角色id获取角色信息
	@Override
	public BRoleEntity getRoleByRoleId(Integer roleid) {
		return bRoleMapper.getRoleByRoleId(roleid);
	}

	//获取公共角色
	@Override
	public List<Integer> getShareRoleId() {
		return bRoleMapper.getShareRoleId();
	}

	//获取到某个类型的某个级别及以下的角色id
	@Override
	public List<Integer> getBeforeRoleId(int maxGrade,String moduleNum) {
		return bRoleMapper.getBeforeRoleId(maxGrade,moduleNum);
	}

	//根据模块类别以及角色id获取角色级别
	@Override
	public Integer getGradeByModuleNum(Integer roleId, String moduleNum) {
		return bRoleMapper.getGradeByModuleNum(roleId,moduleNum);
	}

	//查找公共类型中大于某个级别的所有角色
	@Override
	public List<Integer> getAfterRole(int maxAppGrade) {
		return bRoleMapper.getAfterRole(maxAppGrade);
	}

	//申请人在该模块中的级别
	@Override
	public Integer getAppRoleByModule(int maxAppRole, String moduleNum) {
		return bRoleMapper.getAppRoleByModule(maxAppRole,moduleNum);
	}

	//获取某个功能模块某个级别之后的角色id
	@Override
	public List<Integer> getAfterRoleByModule(int appGrade, String moduleNum) {
		return bRoleMapper.getAfterRoleByModule(appGrade,moduleNum);
	}

	//根据角色以及模块id获取所有等级
	@Override
	public List<Integer> getAllGradeByClass(String moduleNum, List<Integer> gradeList) {
//		List<Integer> list=new ArrayList<>();
//		list.addAll(gradeList);//将set转化为list集合
//		list.add();//为了避免使用sql查询的时候in中有多余逗号
		return bRoleMapper.getAllGradeByClass(moduleNum,gradeList);
	}

	//查找一个功能模块中某个级别的所有角色id
	@Override
	public List<Integer> getRoleByGradeAndMod(String moduleNum, int minGrade) {
		return bRoleMapper.getRoleByGradeAndMod(moduleNum,minGrade);
	}

	//根据模块获取所有级别
	@Override
	public Set<Integer> getGradeByModule(String moduleNum) {
		return bRoleMapper.getGradeByModule(moduleNum);
	}

	//分页获取角色信息
	@Override
	public List<BRoleEntity> roleDisplayByPage(int start, int number) {
		return bRoleMapper.roleDisplayByPage(start,number);
	}

	//获取角色总数
	@Override
	public int getAllCount() {
		return bRoleMapper.getAllCount();
	}

	//添加角色信息
	@Override
	public int insert(BRoleEntity bRoleEntity) {
		return bRoleMapper.insert(bRoleEntity);
	}

	//修改角色信息
	@Override
	public int update(BRoleEntity bRoleEntity) {
		return bRoleMapper.update(bRoleEntity);
	}

	//删除角色
	@Override
	public int deleteById(int id) {
		return bRoleMapper.deleteById(id);
	}

	//角色的模糊分页查询
	@Override
	public List<BRoleEntity> roleVagueByPage(int start, int number, String title) {
		title="%"+title+"%";
		return bRoleMapper.roleVagueByPage(start,number,title);
	}

	//获取角色模糊查询的数量
	@Override
	public int getVagueCount(String title) {
		title="%"+title+"%";
		return bRoleMapper.getVagueCount(title);
	}

	//查询所有角色
	@Override
	public List<BRoleEntity> findAll() {
		return bRoleMapper.findAll();
	}

	//插入用户角色中间表
	@Override
	public void inserMiddleUser(Integer id, int roleId) {
		bRoleMapper.inserMiddleUser(id,roleId);
	}

	//删除用户角色中间表中相同用户id的数据
	@Override
	public void deleteMiddleUser(Integer id) {
		bRoleMapper.deleteMiddleUser(id);
	}

	//删除用户角色中间表中相同角色id的数据
	@Override
	public void deleteMiddleRole(int id) {
		bRoleMapper.deleteMiddleRole(id);
	}

	//添加角色权限中间表，角色id相同
	@Override
	public void insertMiddlePermission(int roleid, int permissionid) {
		bRoleMapper.insertMiddlePermission(roleid,permissionid);
	}

	//删除角色权限中间表，角色id相同信息
	@Override
	public void deleteMiddlePermission(int roleid) {
		bRoleMapper.deleteMiddlePermission(roleid);
	}

	//删除角色部门中间表部门id相同信息
	@Override
	public void deleteMiddleDepartment(int id) {
		bRoleMapper.deleteMiddleDepartment(id);
	}

	//根据角色id集合获取角色信息
	@Override
	public List<BRoleEntity> getRoleByRoleIds(List<Integer> roleIds) {
		return bRoleMapper.getRoleByRoleIds(roleIds);
	}

	//插入角色流程中间表角色id相同
	@Override
	public void insertMiddleProcess(int roleid, int processid) {
		bRoleMapper.insertMiddleProcess(roleid,processid);
	}

	//根据流程节点获取相应节点的角色
	@Override
	public List<Integer> getRoleByProcessId(int processId) {
		return bRoleMapper.getRoleByProcessId(processId);
	}
}
