package com.hfoa.service.impl.departmentimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.department.BDepartmentMapper;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.user.BUserDepartment;

/**
 * 
 * @author wzx
 * 部门service实现类
 */
@Service
public class BDepartmentServiceImpl implements BDepartmentService{

	@Autowired
	private BDepartmentMapper bDepartmentMapper;

	//根据部门id获取所有该部门的角色id
	@Override
	public List<Integer> getRoleIds(int departmentid) {
		return bDepartmentMapper.getRoleIds(departmentid);
	}

	//根据部门id获取用户id
	@Override
	public List<Integer> getUserIdByDId(Integer departmentId) {
		return bDepartmentMapper.getUserIdByDId(departmentId);
	}

	//根据用户id获取部门id
	@Override
	public List<Integer> getDepartmentIdByUserId(int userId) {
		return bDepartmentMapper.getDepartmentIdByUserId(userId);
	}

	//根据id获取部门信息
	@Override
	public BDepartmentEntity getDepartmentIdById(Integer integer) {
		return bDepartmentMapper.getDepartmentIdById(integer);
	}

	//根据部门集合查找这些部门的用户
	@Override
	public List<BUserDepartment> getUserIdByDIds(List<Integer> dlist) {
		return bDepartmentMapper.getUserIdByDIds(dlist);
	}

	//获取所有部门信息
	@Override
	public List<BDepartmentEntity> getAllDepartment() {
		return bDepartmentMapper.getAllDepartment();
	}

	//获取所有自己的部门信息
	@Override
	public List<BDepartmentEntity> getOwnDepartment(List<Integer> departmentIdByUserId) {
		return bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
	}

	//添加用户部门中间表
	@Override
	public void insertMiddleUser(Integer id, Integer departmentid) {
		bDepartmentMapper.insertMiddleUser(id,departmentid);
	}

	//根据部门名称获取部门id
	@Override
	public int getDepartmentIdByName(String departmentname) {
		return bDepartmentMapper.getDepartmentIdByName(departmentname);
	}

	//修改用户部门中间表
	@Override
	public void updateMiddleUser(Integer id, int departmentId) {
		bDepartmentMapper.updateMiddleUser(id,departmentId);
	}

	//删除用户部门中间表中相同用户ID的信息
	@Override
	public void deleteMiddleUser(int id) {
		bDepartmentMapper.deleteMiddleUser(id);
	}

	//删除角色部门中间表中相同角色ID的信息
	@Override
	public void deleteMiddleRole(int id) {
		bDepartmentMapper.deleteMiddleRole(id);
	}

	//分页获取部门信息
	@Override
	public List<BDepartmentEntity> departmentDisplayByPage(int start, int number) {
		return bDepartmentMapper.departmentDisplayByPage(start,number);
	}

	//获取部门总数量
	@Override
	public int getAllCount() {
		return bDepartmentMapper.getAllCount();
	}

	//添加部门信息
	@Override
	public int insert(BDepartmentEntity department) {
		return bDepartmentMapper.insert(department);
	}

	//修改部门信息
	@Override
	public int update(BDepartmentEntity department) {
		return bDepartmentMapper.update(department);
	}

	//根据id删除部门信息
	@Override
	public Integer deleteById(int id) {
		return bDepartmentMapper.deleteById(id);
	}

	//分页模糊查询部门信息
	@Override
	public List<BDepartmentEntity> departmentVagueByPage(int start, int number, String title) {
		title="%"+title+"%";
		return bDepartmentMapper.departmentVagueByPage(start,number,title);
	}

	//获取模糊查询的部门数量
	@Override
	public int getVagueCount(String title) {
		title="%"+title+"%";
		return bDepartmentMapper.getVagueCount(title);
	}

	//添加部门角色中间表部门id相同数据
	@Override
	public int insertMiddleRole(int departmentid, int roleId) {
		return bDepartmentMapper.insertMiddleRole(departmentid,roleId);
	}

	//获取所有可用的部门
	@Override
	public List<BDepartmentEntity> getAllAvalibleDepart() {
		return bDepartmentMapper.getAllAvalibleDepart();
	}

	//根据部门id获取所有抄送的部门id
	@Override
	public List<Integer> getAlldepartByDId(int departmentid) {
		return bDepartmentMapper.getAlldepartByDId(departmentid);
	}

	//根据部门id删除抄送部门信息
	@Override
	public void deleteCopyDepartmentByDid(int departmentid) {
		bDepartmentMapper.deleteCopyDepartmentByDid(departmentid);
	}

	//插入抄送部门信息
	@Override
	public int insertCopyDepartment(int departmentid, int copyDepartmentId) {
		return bDepartmentMapper.insertCopyDepartment(departmentid,copyDepartmentId);
	}

	//根据部门id获取抄送部门Id
	@Override
	public List<Integer> getcopyDepartId(int realDepartmentid) {
		return bDepartmentMapper.getcopyDepartId(realDepartmentid);
	}
}
