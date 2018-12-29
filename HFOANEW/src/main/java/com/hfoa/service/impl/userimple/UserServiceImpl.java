package com.hfoa.service.impl.userimple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.department.BDepartmentMapper;
import com.hfoa.dao.role.BRoleMapper;
import com.hfoa.dao.user.UserMapper;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.fatherEntity.publicEntity;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.service.user.USerService;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.MD5Util;


/**
 *年假                                                                                                                                                                                                                                                     
 */
@Service
public class UserServiceImpl implements USerService{
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private BRoleMapper bRoleMapper;
	
	
	
/*	@Autowired
	private BRoleService bRoleService;
	
	@Autowired
	private USerService bUserService;
	*/
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	
	@Autowired
	private BDepartmentMapper bDepartmentMapper;

	@Override
	public UserEntity select(String id) {
		return userMapper.selectDays(id);
	}

	//查询用户
	@Override
	public List<UserEntity> findUserByNameAndDept(UserEntity user) {
		return userMapper.findUserByNameAndDept(user);
	}
	

	/**
	 * 查询普通员工审批人
	 */
//	@Override
//	public List<UserEntity> findLeader(String department, String roleid) {
//		return userMapper.findLeader(department, roleid);
//	}

	/**
	 * 部门副经理审批人
	 */
	/*@Transactional
	@Override
	public List<UserEntity> findLeader1(String department, String roleid, String username) {
		return userMapper.findLeader1(department,"2",username);
	}*/

	/**
	 * 部门经理查询审批人
	 */
//	@Override
//	public List<UserEntity> findleader2(String workgroupid) {
//		return userMapper.findleader2(workgroupid);
//	}

	/**
	 * 公司领导查询审批人
	 */
//	@Override
//	public List<UserEntity> findUserByNameAndId(String id, String username) {
//		
//		return userMapper.findUserByNameAndId(id, username);
//	}

	@Override
	public UserEntity tologin(String name, String password) {
		String pwd = MD5Util.toMD5(password);
		System.out.println("加密的密码"+pwd);
		return userMapper.loginUser(name, pwd);
	}

	@Override
	public UserEntity loginOpenId(String openId) {
		UserEntity userEntity = userMapper.getopenId(openId);
		if(userEntity!=null){
			boolean getopenIduser = dynamicGetRoleUtil.getopenIduser(userEntity.getId());
			String roleName = "0";
			if(getopenIduser){//公司领导或者经理
				roleName = "1";
			}
			userEntity.setDuty(roleName);
		}
		return userEntity;
	}

	@Override
	public int updateUserDays(UserEntity userEntity) {
		return userMapper.updateUserDays(userEntity);
	}

	@Override
	public UserEntity loginUserName(String userName) {
		return userMapper.loginUserName(userName);
	}

	@Override
	public int updateGzhOpenId(UserEntity userEntity) {
		return userMapper.updateGzhOpenId(userEntity);
	}
	//根据用户账号查找用户信息
	@Override
	public UserEntity getUserByUserCode(String userCode) {
		return userMapper.getUserByUserCode(userCode);
	}

	//根据角色id获取所有用户id
	@Override
	public List<Integer> getallUserId(Integer roleid) {
		return userMapper.getallUserId(roleid);
	}

	//根据用户id获取用户信息
	@Override
	public UserEntity getUserById(Integer userid) {
		return userMapper.getUserById(userid);
	}

	//根据部门id获取该部门下所有用户信息
	@Override
	public List<UserEntity> getAllUserByDepartId(Integer departmentId) {
		return userMapper.getAllUserByDepartId(departmentId);
	}

	//根据用户账号的关键字查询所有用户信息
	@Override
	public List<UserEntity> getUserByLike(String checkman) {
		return userMapper.getUserByLike(checkman);
	}

	//通过用户id获取所有角色id
	@Override
	public List<Integer> getRoleIdByUserId(int userId) {
		return userMapper.getRoleIdByUserId(userId);
	}

	//查询所有用户
	@Override
	public List<UserEntity> getAll() {
		return userMapper.getAll();
	}

	//根据openid获取用户id
	@Override
	public Integer getUserIdByOpenId(String openId) {
		return userMapper.getUserIdByOpenId(openId);
	}

//	@Override
//	public List<UserEntity> findLeader1(UserEntity user) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public int updateOpenId(UserEntity userEntity) {
		return userMapper.updateOpenId(userEntity);
	}

	@Override
	public List<UserEntity> getFinder(String openId, String moduleNum) {
		System.out.println("openID是"+openId);
		int user_id = userMapper.getUserIdByOpenId(openId);
		System.out.println("用户id是"+user_id);
		List<Integer> departmentList = bDepartmentMapper.getDepartmentIdByUserId(user_id);
		List<UserEntity> approvalMan = dynamicGetRoleUtil.getApprovalMan(user_id);
		/*LinkedHashSet<UserEntity> listset = new LinkedHashSet<>(approvalMan);*/
		List<UserEntity> list = removeDuplicate(approvalMan);
		
		return list;
	}
	
	 public List<UserEntity> removeDuplicate(List<UserEntity> list) {
	        for (int i = 0; i < list.size() - 1; i++) {
	            for (int j = list.size() - 1; j > i; j--) {
	                if (list.get(j).equals(list.get(i))) {
	                    list.remove(j);
	                }
	            }
	        }
			return list;
	       
	    }
	

	@Override
	public List<UserEntity> getCcList(String moduleNum,String openId) {
		List<UserEntity> list_Entity = new ArrayList<>();
		Set<UserEntity> users = dynamicGetRoleUtil.getCopyUsers(openId);
		if(users!=null&&users.size()>0){
			for(UserEntity userEntity:users){
				list_Entity.add(userEntity);
			}
		}
		
		/*List<UserEntity> list = defaultgetCcList(openId);
		list_Entity.addAll(list);
		LinkedHashSet<UserEntity> set = new LinkedHashSet<>(list_Entity);
		List<UserEntity> listNew = new ArrayList<>();
		for(UserEntity userEntity:set){
			listNew.add(userEntity);
		}
		*/
		
	
		return list_Entity;
	}

	//分页查询用户信息
	@Override
	public List<UserEntity> userDisplayByPage(int start, int number) {
		return userMapper.userDisplayByPage(start,number);
	}

	//获取用户数量
	@Override
	public int getAllCount() {
		return userMapper.getAllCount();
	}

	//添加用户信息
	@Override
	public Integer insert(UserEntity user) {
		return userMapper.insert(user);
	}

	//修改用户信息
	@Override
	public Integer update(UserEntity user) {
		return userMapper.update(user);
	}

	//根据用户id删除用户信息
	@Override
	public Integer deleteById(int id) {
		return userMapper.deleteById(id);
	}

	//分页模糊查询用户的基本信息
	@Override
	public List<UserEntity> userVagueByPage(int start, int number, String title) {
		title="%"+title+"%";
		return userMapper.userVagueByPage(start,number,title);
	}

	//模糊查询用户数量
	@Override
	public int getVagueCount(String title) {
		title="%"+title+"%";
		return userMapper.getVagueCount(title);
	}

	//删除用户部门中间表相同部门id信息
	@Override
	public void deleteMiddleDepartment(int id) {
		userMapper.deleteMiddleDepartment(id);
	}

	//根据角色id获取用户id
	@Override
	public List<Integer> getUserIdByRoleId(int roleId) {
		return userMapper.getUserIdByRoleId(roleId);
	}

	//修改表中以此人为领导人的领导人账号
	@Override
	public void updateApprovalName(String code,Integer uid) {
		String id=uid+"";
		userMapper.updateApprovalName(code,id);
	}

	@Override
	public boolean getmoney(String openId,String entertainCategory ,String perBudget) {
		UserEntity userEntity = userMapper.getopenId(openId);
		boolean flag = false;
		if(userEntity!=null){
			flag = dynamicGetRoleUtil.getMoney(userEntity.getId(),entertainCategory,perBudget);
		}
		
		return flag;
	}

	@Override
	public List<BDepartmentEntity> getdepartmentOpenId(String openId) {
		UserEntity userEntity = userMapper.getopenId(openId);
		List<BDepartmentEntity> list = new ArrayList<>();
		if(userEntity!=null){
			list = dynamicGetRoleUtil.getdepartment(userEntity.getId());
		}
		
		return list;
	}

	//根据账号获取数量
	@Override
	public int getCountByCode(String code) {
		return userMapper.getCountByCode(code);
	}

	@Override
	public List<UserEntity> defaultgetCcList(String openId) {
		UserEntity userEntity = userMapper.getopenId(openId);
		List<UserEntity> cclistuser = new ArrayList<>();
		if(userEntity!=null){
			cclistuser = dynamicGetRoleUtil.getCclistuser(userEntity.getId());
		}
		return cclistuser;
	}

}







