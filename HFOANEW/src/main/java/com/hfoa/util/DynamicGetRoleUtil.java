package com.hfoa.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xslf.model.geom.IfElseExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hfoa.dao.department.BDepartmentMapper;
import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.role.BRoleClass;
import com.hfoa.entity.role.BRoleEntity;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.BUserDepartment;
import com.hfoa.service.user.USerService;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import scala.Array;
@Component
public class DynamicGetRoleUtil {

	@Autowired
	private USerService bUserService;
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
    private BProcessService bProcessService;
	
	@Value("#{configProperties['businessEntertainment']}") 
	private String businessEntertainment;//
	
	@Value("#{configProperties['leaderBusinessEntrtainmentMoney']}") 
	private String leaderBusinessEntrtainmentMoney;//
	
	@Value("#{configProperties['managerBusinessEntrtainmentMoney']}") 
	private String managerBusinessEntrtainmentMoney;//
	
	@Value("#{configProperties['staffBusinessEntrtainmentMoney']}") 
	private String staffBusinessEntrtainmentMoney;//
	
	@Value("#{configProperties['officialHospitality']}") 
	private String officialHospitality;//
	
	@Value("#{configProperties['foreignAffairsReception']}")
	private String foreignAffairsReception;
	
	
	@Value("#{configProperties['leaderOfficialHospitalityMoney']}") 
	private String leaderOfficialHospitalityMoney;//
	
	@Value("#{configProperties['managerOfficialHospitalityMoney']}") 
	private String managerOfficialHospitalityMoney;//
	
	@Value("#{configProperties['staffOfficialHospitalityMoney']}") 
	private String staffOfficialHospitalityMoney;//
	
	@Autowired
	private BDepartmentMapper bDepartmentMapper;
	
	public List<UserEntity> getCclistuser(Integer userId){
		List<UserEntity> userList=new ArrayList<>();
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){
			UserEntity userById = bUserService.getUserById(userId);
			if(userById.getWorkgroupid()!=null&&!"".equals(userById.getWorkgroupid())){
				UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
				userList.add(leader);
			}
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				for (Integer integer : countlist) {
					for (UserEntity u : allUser) {
						//获取每个人的角色
						List<Integer>  roleIdByUserId= bUserService.getRoleIdByUserId(u.getId());
						if(roleIdByUserId!=null){
							if(roleIdByUserId.contains(integer)&&u.getId()!=userId){
								List<Integer> roleByGradeAndMod = bRoleService.getRoleByGradeAndMod("2*", 3);
								if(roleByGradeAndMod.get(0)==integer){
									UserEntity userById = bUserService.getUserById(u.getId());
									UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
									userList.add(leader);
								}
							}
						}
						
					}
					
				}
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				for (UserEntity u : allUser) {
					//获取每个人的角色
					List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(u.getId());
					if(roleIdByUserId.contains(minRoleId)&&u.getId()!=userId){
						UserEntity userById = bUserService.getUserById(u.getId());
						UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
						userList.add(leader);
					}
				}
			}
			//申请人是部门经理
			/*if(countlist.size()==1&&roleClassList.size()>1){
				UserEntity userById = bUserService.getUserById(userId);
				UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
				userList.add(leader);
	
			}*/
			
		}

		
		return userList;
	}
	
	
	
	
	
	
	
	
	
	
	
	public boolean getopenIduser(Integer userId){
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		//2、确定申请人角色，在三种类型中级别最高的为准
		boolean flag = false;
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){//领导
			flag = true;
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				flag = true;
			}
			//申请人是部门经理
			if(countlist.size()==1&&roleClassList.size()>1){
				flag = true;
			
		}
		
	}
		
		return flag;
	}
	
	
	
	
	
	
	/**
	 * 获取金额
	 * @param userId
	 * @return
	 */
	public boolean getMoney(Integer userId,String entertainCategory,String perBudget){
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		//2、确定申请人角色，在三种类型中级别最高的为准
		boolean flag = true;
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){//领导
			if(entertainCategory.equals(businessEntertainment)||entertainCategory.equals(foreignAffairsReception)){//商务招待
				if(Integer.parseInt(perBudget)>Integer.parseInt(leaderBusinessEntrtainmentMoney)){
					flag = false;
				}
				
				
			}else if(entertainCategory.equals(officialHospitality)){//外事招待
				if(Integer.parseInt(perBudget)>Integer.parseInt(leaderOfficialHospitalityMoney)){
					flag = false;
				}
			}
			
			
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				if(entertainCategory.equals(businessEntertainment)||entertainCategory.equals(foreignAffairsReception)){//商务招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(staffBusinessEntrtainmentMoney)){
						flag = false;
					}
					
					
				}else if(entertainCategory.equals(officialHospitality)){//公务招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(staffOfficialHospitalityMoney)){
						flag = false;
					}
				}
				
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				if(entertainCategory.equals(businessEntertainment)||entertainCategory.equals(foreignAffairsReception)){//商务招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(managerBusinessEntrtainmentMoney)){
						flag = false;
					}
					
					
				}else if(entertainCategory.equals(officialHospitality)){//外事招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(managerOfficialHospitalityMoney)){
						flag = false;
					}
				}
			}
			//申请人是部门经理
			if(countlist.size()==1&&roleClassList.size()>1){
				if(entertainCategory.equals(businessEntertainment)||entertainCategory.equals(foreignAffairsReception)){//商务招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(managerBusinessEntrtainmentMoney)){
						flag = false;
					}
					
				}else if(entertainCategory.equals(officialHospitality)){//公务招待
					if(Integer.parseInt(perBudget)>Integer.parseInt(managerOfficialHospitalityMoney)){
						flag = false;
					}
				}
			}
			
		}
		
		
		return flag;
	}
	
	@RequestMapping("/getGzhopenId")
	@ResponseBody
	public List<String> getGzhopenId(String departmentName){
		List<String> list = new ArrayList<>();
		int departmentIdByName = bDepartmentMapper.getDepartmentIdByName(departmentName);
		List<Integer> userIdByDId = bDepartmentService.getUserIdByDId(departmentIdByName);
		Set<Integer> realuserId=new  HashSet<>();
		if(userIdByDId!=null){
			for(Integer userId:userIdByDId){
				List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
//				for(Integer roleId:roleIdByUserId){
//					/**
//					 * 大致流程是数据库里查出比较时间有限先先死，将来改一下
//					 */
//					if(roleId.equals(3)||roleId.equals(2)||roleId.equals(4)){
//						UserEntity userEntity = bUserService.getUserById(userId);
//						if(userEntity!=null){
//							list.add(userEntity.getModifiedby());
//						}
//					}
//				}
				if(roleIdByUserId.contains(2)||roleIdByUserId.contains(3)||roleIdByUserId.contains(4)){
					realuserId.add(userId);
				}
			}
			for (Integer integer : realuserId) {
				UserEntity userEntity = bUserService.getUserById(integer);
				if(userEntity!=null){
					list.add(userEntity.getModifiedby());
				}
			}
		}
		
		return list;
	}
	
	
	@RequestMapping("getdepartment")
	@ResponseBody
	public List<BDepartmentEntity> getdepartment(Integer userId){
		
		List<BDepartmentEntity> list = new ArrayList<>();
		
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){
			UserEntity userById = bUserService.getUserById(userId);
			if(userById.getWorkgroupid()!=null&&!"".equals(userById.getWorkgroupid())){
				BDepartmentEntity bdDepartmentEntity = new BDepartmentEntity();
				bdDepartmentEntity.setDepartmentname("全部部门");
				list.add(bdDepartmentEntity);
				list .addAll(bDepartmentMapper.getAllDepartment()) ;
				
			}
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				for (Integer integer : countlist) {
					for (UserEntity u : allUser) {
						//获取每个人的角色
						List<Integer>  roleIdByUserId= bUserService.getRoleIdByUserId(u.getId());
						if(roleIdByUserId!=null){
							if(roleIdByUserId.contains(integer)&&u.getId()!=userId){
								List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(u.getId());
								list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
							}
						}
					}
				}
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				for (UserEntity u : allUser) {
					//获取每个人的角色
					List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(u.getId());
					if(roleIdByUserId.contains(minRoleId)&&u.getId()!=userId){
						List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(u.getId());
						list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
					}
				}
			}
			//申请人是部门经理
			if(countlist.size()==1&&roleClassList.size()>1){
				List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(userId);
				list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
			}
			
		}

		//处理顺序
		/*for (UserEntity user : userList) {
			//1、根据用户id获取用户的所有角色  
			List<Integer> appRole1=bUserService.getRoleIdByUserId(user.getId());
			//2、确定申请人角色，在三种类型中级别最高的为准
			int maxAppGrade1=0;
			int maxAppRole1=0;
			for (Integer integer : appRole1) {
				Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
				if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
					maxAppGrade1=gradeFromClass;
					maxAppRole1=integer;
				}
			}
			user.setGrade(maxAppGrade1);
		}*/
		return list;
	}
	
	
	@RequestMapping("getroleApplyense")
	@ResponseBody
	public String getroleApplyense(Integer userId){
		String status = "0";
		List<BDepartmentEntity> list = new ArrayList<>();
		
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){
			UserEntity userById = bUserService.getUserById(userId);
			if(userById.getWorkgroupid()!=null&&!"".equals(userById.getWorkgroupid())){
				status="4";
			}
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				for (Integer integer : countlist) {
					for (UserEntity u : allUser) {
						//获取每个人的角色
						List<Integer>  roleIdByUserId= bUserService.getRoleIdByUserId(u.getId());
						if(roleIdByUserId!=null){
							if(roleIdByUserId.contains(integer)&&u.getId()!=userId){
								List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(u.getId());
								list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
							}
						}
					}
				}
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				for (UserEntity u : allUser) {
					//获取每个人的角色
					List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(u.getId());
					if(roleIdByUserId.contains(minRoleId)&&u.getId()!=userId){
						List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(u.getId());
						list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
					}
				}
			}
			//申请人是部门经理
			if(countlist.size()==1&&roleClassList.size()>1){
				List<Integer> departmentIdByUserId = bDepartmentMapper.getDepartmentIdByUserId(userId);
				list = bDepartmentMapper.getOwnDepartment(departmentIdByUserId);
			}
			
		}

		return status;
	}
	
	
	/**
	 * 1、获取公共级审批人
	 * param:用户id，功能模块id
	 * result：审批人账号
	 */
	@RequestMapping("getApprovalMan")
	@ResponseBody
	public List<UserEntity> getApprovalMan(Integer userId){
		List<UserEntity> userList=new ArrayList<>();
		//1、根据用户id获取用户的所有角色  
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//2、确定申请人角色，在三种类型中级别最高的为准
		int maxAppGrade=0;
		int maxAppRole=0;
		for (Integer integer : appRole) {
			Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
			if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
				maxAppGrade=gradeFromClass;
				maxAppRole=integer;
			}
		}
		//获取三种类型中级别最高的级别
		int commonMaxGrade=bRoleService.getLeaderGrade();
		//获取用户部门id
		List<Integer> departmentList=bDepartmentService.getDepartmentIdByUserId(userId);
		//如果申请人的级别跟领导级别一致,查找他的groupid对应的领导人
		if(commonMaxGrade==maxAppGrade){
			UserEntity userById = bUserService.getUserById(userId);
			if(userById.getWorkgroupid()!=null&&!"".equals(userById.getWorkgroupid())){
				UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
				userList.add(leader);
			}
		}else{
			//非领导级别
			//获取所有大于申请人公共级别的roleid
			List<Integer> roleidlist=bRoleService.getAfterRole(maxAppGrade);
			//获取这些角色中级别最小的角色
			Integer[] split3 = new Integer[roleidlist.size()];
			roleidlist.toArray(split3);
			int minGrade=bRoleService.getGradeFromClass(split3[0]);
			int minRoleId=split3[0];
			for(int k=0;k<split3.length;k++){
				if(bRoleService.getGradeFromClass(split3[k])!=null&&bRoleService.getGradeFromClass(split3[k])<minGrade){
					minGrade=bRoleService.getGradeFromClass(split3[k]);
					minRoleId=split3[k];
				}
			}
			
			//判断该角色在role_class中公共类别数量
			String modulenum=bRoleService.getModuleNumByRole(minRoleId);
			//根据公共类别查询数量
			List<Integer> countlist=bRoleService.getRoleClassList(modulenum);
			//拿到申请人的公共类型
			String moduleNumByRole = bRoleService.getModuleNumByRole(maxAppRole);
			//查找申请用户角色在公共类别中的数量
			List<Integer> roleClassList = bRoleService.getRoleClassList(moduleNumByRole);
			//获取该部门所有用户
			List<UserEntity> allUser=new ArrayList<>();
			List<BUserDepartment> userIdList=bDepartmentService.getUserIdByDIds(departmentList);
			for (BUserDepartment integer : userIdList) {
				UserEntity u=bUserService.getUserById(integer.getUserid());
				if(u!=null){
					allUser.add(u);
				}
			}
			//申请人是普通员工
			if(countlist.size()>1&&roleClassList.size()==1){
				for (Integer integer : countlist) {
					for (UserEntity u : allUser) {
						//获取每个人的角色
						List<Integer>  roleIdByUserId= bUserService.getRoleIdByUserId(u.getId());
						if(roleIdByUserId!=null){
							if(roleIdByUserId.contains(integer)&&u.getId()!=userId){
								userList.add(u);
							}
						}
						
					}
					
				}
			}
			//申请人是部门副经理
			if(countlist.size()>1&&roleClassList.size()>1){
				for (UserEntity u : allUser) {
					//获取每个人的角色
					List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(u.getId());
					if(roleIdByUserId.contains(minRoleId)&&u.getId()!=userId){
						userList.add(u);
					}
				}
			}
			//申请人是部门经理
			if(countlist.size()==1&&roleClassList.size()>1){
				UserEntity userById = bUserService.getUserById(userId);
				UserEntity leader=bUserService.getUserById(Integer.parseInt(userById.getWorkgroupid()));
				userList.add(leader);
			}
			
		}

		//处理顺序
		for (UserEntity user : userList) {
			//1、根据用户id获取用户的所有角色  
			List<Integer> appRole1=bUserService.getRoleIdByUserId(user.getId());
			//2、确定申请人角色，在三种类型中级别最高的为准
			int maxAppGrade1=0;
			int maxAppRole1=0;
			for (Integer integer : appRole1) {
				Integer gradeFromClass = bRoleService.getGradeFromClass(integer);
				if(gradeFromClass!=null&&gradeFromClass>maxAppGrade){
					maxAppGrade1=gradeFromClass;
					maxAppRole1=integer;
				}
			}
			user.setGrade(maxAppGrade1);
		}
		ListSortUtil<UserEntity> sortList = new ListSortUtil<UserEntity>();
		sortList.sort(userList, "grade", "desc");
		return userList;
	}
	/**
	 * 2、开启流程后获取到接下来的级别
	 * param:用户id，参与模块功能的级别,功能模块id
	 * result：接下来业务的级别
	 */
	@RequestMapping("getNextGrade")
	@ResponseBody
	public String getNextGrade(Integer userId,String joinProcessGrade,String moduleNum){
		String gradeStr="";
		//根据用户id获取用户的所有角色
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		if(joinProcessGrade==null){//说明流程刚刚开启
			//根据模块id获取所有等级
			Set<Integer> gradeList=bRoleService.getGradeByModule(moduleNum);
			Integer[] integer = new Integer[gradeList.size()];
			gradeList.toArray(integer);
			//获取该模块中等级最小
			int minGrade=integer[0];
			for (int i=0;i<integer.length;i++) {
				if(integer[i]<minGrade){
					minGrade=integer[i];
				}
			}
			//拿到等级最小的角色
			List<Integer> minRoleId=bRoleService.getRoleByGradeAndMod(moduleNum,minGrade);
			//获取用户以及最小级别的角色的交集
			Collection realA = new ArrayList<Integer>(minRoleId);
			Collection realB = new ArrayList<Integer>(appRole);
			realA.retainAll(realB);
			if(realA.size()>=1){//说明该用户具有申请功能
				//移除掉所有等级中的最小的等级
				gradeList.remove(minGrade);
				for (Integer grade : gradeList) {
					gradeStr+=grade+",";
				}
			}
		}else{//说明在流程中
			String[] split = joinProcessGrade.split(",");
			int minGrade=Integer.parseInt(split[0]);
			for(int i=0;i<split.length;i++){
				if(Integer.parseInt(split[i])<minGrade){
					minGrade=Integer.parseInt(split[i]);
				}
			}
			for(int j=0;j<split.length;j++){
				if(Integer.parseInt(split[j])!=minGrade){
					gradeStr+=Integer.parseInt(split[j])+",";
				}
			}
		}
		return gradeStr;
	}
	/**
	 * 3、用户是否可看到相关数据
	 * param:用户id，参与模块级别,功能模块id，部门id集合
	 * result：true有权获取任务，false无权获取
	 */
	@RequestMapping("getTask")
	@ResponseBody
	public Boolean getTask(Integer userId,String joinProcessGrade,String moduleNum,List<Integer> dlist){
		Boolean getTask=false;
		//根据用户id获取用户部门
		List<Integer> departIdList=bDepartmentService.getDepartmentIdByUserId(userId);
		//根据用户id获取用户的所有角色
		List<Integer> appRole=bUserService.getRoleIdByUserId(userId);
		//拿到传过来的最小的级别
		String[] split = joinProcessGrade.split(",");
		int minGrade=Integer.parseInt(split[0]);
		for(int i=0;i<split.length;i++){
			if(Integer.parseInt(split[i])<minGrade){
				minGrade=Integer.parseInt(split[i]);
			}
		}
		//根据最小级别获取相关角色
		List<Integer> minRoleId=bRoleService.getRoleByGradeAndMod(moduleNum,minGrade);
		if(dlist!=null){//根据部门获取任务
			for (Integer dpartId : dlist) {
				//根据部门id获取部门所有角色id
				List<Integer> dRoleId=bDepartmentService.getRoleIds(dpartId);
				List<Integer> userIdByDId = bDepartmentService.getUserIdByDId(dpartId);//根据部门获取的用户id
				Collection realA = new ArrayList<Integer>(dRoleId);//部门的角色id
				Collection realB = new ArrayList<Integer>(minRoleId);//最小级别角色
				Collection realC = new ArrayList<Integer>(appRole);//用户角色
				realA.retainAll(realB);
				realC.retainAll(realB);
				if(realA.size()>0&&realC.size()>0&&userIdByDId.contains(userId)){
					getTask=true;
				}
			}
		}else{//不按部门获取任务
			Collection realA = new ArrayList<Integer>(appRole);//用户的角色id
			Collection realB = new ArrayList<Integer>(minRoleId);//最小级别角色
			realA.retainAll(realB);
			if(realA.size()>0){
				getTask=true;
			}
		}
		return getTask;
	}
	/**
	 * 4、获取业务流程中每一步的审批人
	 * param:参与模块功能的角色id,功能模块id，级别字符串,部门id集合，经理类别是否区分
	 * result：审批人id
	 */
	@RequestMapping("getNextApprovalMan")
	@ResponseBody
	public List<Integer> getNextApprovalMan(String joinProcessRole,String moduleNum,String departIds,Boolean differManage){
		List<Integer> userList=new ArrayList<>();//最终用户
		//部门id
		List<Integer> departmentList=new ArrayList<>();
		if(departIds!=null){
			String[] split2 = departIds.split(",");
			for(int i=0;i<split2.length;i++){
				departmentList.add(Integer.parseInt(split2[i]));
			}
		}
		//根据模块获取所有级别
		Set<Integer> gradeList=bRoleService.getGradeByModule(moduleNum);
		//拿到经理级别的角色id
		List<Integer> manageRole=bRoleService.getRoleClassList("2*");
		
		//获取所有用户
		List<UserEntity> all = bUserService.getAll();
		if(joinProcessRole==null){//说明初次申请
			Integer[] integer = new Integer[gradeList.size()];
			gradeList.toArray(integer);
			int minOneGrade=integer[0];
			List<Integer> minOneRole=bRoleService.getRoleByGradeAndMod(moduleNum, minOneGrade);
			for (int i=0;i<integer.length;i++) {
				if(integer[i]<minOneGrade){
					minOneGrade=integer[i];
					minOneRole=bRoleService.getRoleByGradeAndMod(moduleNum, integer[i]);
				}
			}
			
			gradeList.remove(minOneGrade);
			Integer[] integer2 = new Integer[gradeList.size()];
			gradeList.toArray(integer2);
			int minTwoGrade=integer2[0];
			List<Integer> minTwoRole=bRoleService.getRoleByGradeAndMod(moduleNum, minTwoGrade);
			for (int i=0;i<integer2.length;i++) {
				if(integer2[i]<minTwoGrade){
					minTwoGrade=integer2[i];
					minTwoRole=bRoleService.getRoleByGradeAndMod(moduleNum, integer2[i]);
				}
			}
			//获取到经理级别的用户在该模块中最大的级别
			int maxGrade=0;
			int maxRole=0;
			for (Integer integer1 : manageRole) {
				int grade=bRoleService.getGradeByModuleNum(integer1, moduleNum);
				if(grade>maxGrade){
					maxGrade=grade;
					maxRole=integer1;
				}
			}
			if(departIds==null&&!differManage){//1、不限制部门，不限制经理级别
				for (UserEntity bUserEntity : all) {
					List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
					Collection realA = new ArrayList<Integer>(roleId);
					Collection realB = new ArrayList<Integer>(minTwoRole);
					realA.retainAll(realB);
					if(realA.size()>0){
						userList.add(bUserEntity.getId());
					}
				}
			}else if(departIds!=null&&!differManage){//2、限制部门，不限制经理级别
				for (Integer departId : departmentList) {
					for (UserEntity bUserEntity : all) {
						List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
						Collection realA = new ArrayList<Integer>(roleId);
						Collection realB = new ArrayList<Integer>(minTwoRole);
						realA.retainAll(realB);
						List<Integer> departIdList=bDepartmentService.getDepartmentIdByUserId(bUserEntity.getId());
						if(realA.size()>0&&departIdList.contains(departId)){
							userList.add(bUserEntity.getId());
						}
					}
				}
			}else if(departIds==null&&differManage){//3、不限制部门，限制经理级别
				for (UserEntity bUserEntity : all) {
					List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
					Collection realA = new ArrayList<Integer>(roleId);
					Collection realB = new ArrayList<Integer>(minTwoRole);
					Collection realC = new ArrayList<Integer>(manageRole);
					realC.retainAll(realA);
					realA.retainAll(realB);
					if(realA.size()>0){
						userList.add(bUserEntity.getId());
					}
					if(maxGrade>=minTwoGrade){
					if(realC.size()>0){
						for (Object object : realC) {
							int manageGrade=bRoleService.getGradeByModuleNum((Integer)object, moduleNum);
							gradeList.remove(minTwoGrade);
							if(roleId.contains(object)){
								if(userList.contains(bUserEntity.getId())){
									userList.remove(bUserEntity.getId());
								}
								userList.add(bUserEntity.getId());
							}
							}
						}
					}
				}
			}else{//4、限制部门，限制经理级别
				for (Integer departId : departmentList) {
					for (UserEntity bUserEntity : all) {
						List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
						Collection realA = new ArrayList<Integer>(roleId);
						Collection realB = new ArrayList<Integer>(minTwoRole);
						Collection realC = new ArrayList<Integer>(manageRole);
						realC.retainAll(realA);
						realA.retainAll(realB);
						List<Integer> departIdList=bDepartmentService.getDepartmentIdByUserId(bUserEntity.getId());
						if(realA.size()>0&&departIdList.contains(departId)){
							userList.add(bUserEntity.getId());
							
						}
						if(maxGrade>=minTwoGrade){
						if(departIdList.contains(departId)){
							if(realC.size()>0){
								for (Object object : realC) {
									if(roleId.contains(object)){
										int manageGrade=bRoleService.getGradeByModuleNum((Integer)object, moduleNum);
										gradeList.remove(manageGrade);
										if(userList.contains(bUserEntity.getId())){
											userList.remove(bUserEntity.getId());
										}
										userList.add(bUserEntity.getId());
									}
									}
								}
							}
						}
					}
				}
			}
		}else{
			String[] split = joinProcessRole.split(",");
			int minThreeGrade=Integer.parseInt(split[0]);
			List<Integer> minThreeRole=bRoleService.getRoleByGradeAndMod(moduleNum, minThreeGrade);
			for (int i=0;i<split.length;i++) {
				if(Integer.parseInt(split[i])<minThreeGrade){
					minThreeGrade=Integer.parseInt(split[i]);
					minThreeRole=bRoleService.getRoleByGradeAndMod(moduleNum, Integer.parseInt(split[i]));
				}
			}
			
			//获取到经理级别的用户在该模块中最大的级别
			int maxGrade=0;
			int maxRole=0;
			for (Integer integer : manageRole) {
				int grade=bRoleService.getGradeByModuleNum(integer, moduleNum);
				if(grade>maxGrade){
					maxGrade=grade;
					maxRole=integer;
				}
			}
			if(departIds==null&&!differManage){//1、不限制部门，不限制经理级别
				for (UserEntity bUserEntity : all) {
					List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
					Collection realA = new ArrayList<Integer>(roleId);
					Collection realB = new ArrayList<Integer>(minThreeRole);
					realA.retainAll(realB);
					if(realA.size()>0){
						userList.add(bUserEntity.getId());
					}
				}
			}else if(departIds!=null&&!differManage){//2、限制部门，不限制经理级别
				for (Integer departId : departmentList) {
					for (UserEntity bUserEntity : all) {
						List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
						Collection realA = new ArrayList<Integer>(roleId);
						Collection realB = new ArrayList<Integer>(minThreeRole);
						realA.retainAll(realB);
						List<Integer> departIdList=bDepartmentService.getDepartmentIdByUserId(bUserEntity.getId());
						if(realA.size()>0&&departIdList.contains(departId)){
							userList.add(bUserEntity.getId());
						}
					}
				}
				
			}else if(departIds==null&&differManage){//3、不限制部门，限制经理级别
				for (UserEntity bUserEntity : all) {
					List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
					Collection realA = new ArrayList<Integer>(roleId);
					Collection realB = new ArrayList<Integer>(minThreeRole);
					Collection realC = new ArrayList<Integer>(manageRole);
					realC.retainAll(realA);
					realA.retainAll(realB);
					if(realA.size()>0){
						userList.add(bUserEntity.getId());
					}
					if(maxGrade>=minThreeGrade){
					if(realC.size()>0){
						for (Object object : realC) {
								if(roleId.contains(object)){
									if(userList.contains(bUserEntity.getId())){
										userList.remove(bUserEntity.getId());
									}
									userList.add(bUserEntity.getId());
								}
							}
						}
				   }
					
				   
			     }
			}else{//4、限制部门，限制经理级别
				for (Integer departId : departmentList) {
					for (UserEntity bUserEntity : all) {
						List<Integer> roleId=bUserService.getRoleIdByUserId(bUserEntity.getId());
						Collection realA = new ArrayList<Integer>(roleId);
						Collection realB = new ArrayList<Integer>(minThreeRole);
						Collection realC = new ArrayList<Integer>(manageRole);
						realC.retainAll(realA);
						realA.retainAll(realB);
						List<Integer> departIdList=bDepartmentService.getDepartmentIdByUserId(bUserEntity.getId());
						if(realA.size()>0&&departIdList.contains(departId)){
							userList.add(bUserEntity.getId());
						}
						if(maxGrade>=minThreeGrade){
						if(departIdList.contains(departId)){
							if(realC.size()>0){
								for (Object object : realC) {
									if(roleId.contains(object)){
										if(userList.contains(bUserEntity.getId())){
											userList.remove(bUserEntity.getId());
										}
										userList.add(bUserEntity.getId());
									}
									}
								}
							}
						}
					}
				}
			}
		}
		return userList;
	}
	/**
	 * 5、获取业务流程中下一步的业务级别
	 * param:参与模块功能的角色id,功能模块id，级别字符串,部门id集合，经理类别是否区分
	 * result：接下来的级别
	 */
	@RequestMapping("getNextProcessGrade")
	@ResponseBody
	public String getNextProcessGrade(String joinProcessRole,String moduleNum,String departIds,Boolean differManage){
	String grade="";
	List<Integer> departList=new ArrayList<>();
	if(departIds!=null){
		String[] split = departIds.split(",");
		for(int k=0;k<split.length;k++){
			departList.add(Integer.parseInt(split[k]));
		}
	}
	//根据模块获取所有级别
	Set<Integer> gradeList=bRoleService.getGradeByModule(moduleNum);
	//拿到经理级别的角色id
	List<Integer> manageRole=bRoleService.getRoleClassList("2*");
	if(joinProcessRole==null){//说明初次申请
		//获取到最小级别
		Integer firstGrade[]=new Integer[gradeList.size()];
		gradeList.toArray(firstGrade);
		int mingrade=firstGrade[0];
		List<Integer> minRole=bRoleService.getRoleByGradeAndMod(moduleNum, mingrade);
		for(int i=0;i<firstGrade.length;i++){
			if(mingrade>firstGrade[i]){
				mingrade=firstGrade[i];
				minRole=bRoleService.getRoleByGradeAndMod(moduleNum, firstGrade[i]);
			}
		}
		gradeList.remove(mingrade);
		for (Integer integer : gradeList) {
			grade+=integer+",";
		}
	}else{
		List<Integer> list =new ArrayList<>();
		String[] split = joinProcessRole.split(",");
		int midMinGrade=Integer.parseInt(split[0]);
		List<Integer> midMinRole=bRoleService.getRoleByGradeAndMod(moduleNum, midMinGrade);
		for(int j=0;j<split.length;j++){
			list.add(Integer.parseInt(split[j]));
			if(Integer.parseInt(split[j])<midMinGrade){
				midMinGrade=Integer.parseInt(split[j]);
				midMinRole=bRoleService.getRoleByGradeAndMod(moduleNum, Integer.parseInt(split[j]));
			}
		}
		
		
		//流程中
		if(departIds==null&&!differManage){//不限制部门，不限制经理级别
			for (int i=0;i<list.size();i++) {
				if(midMinGrade==list.get(i)){
					list.remove(i);
				}
			}
			for (Integer string : list) {
				grade+=string+",";
			}
		}else if(departIds!=null&&!differManage){//限制部门不限制级别
			for (Integer did : departList) {
				//根据部门id获取所有角色id
				List<Integer> roleId=bDepartmentService.getRoleIds(did);
				Collection realA = new ArrayList<Integer>(roleId);
				Collection realB = new ArrayList<Integer>(midMinRole);
				realA.retainAll(realB);
				if(realA.size()>0){
					for (int i=0;i<list.size();i++) {
						if(midMinGrade==list.get(i)){
							list.remove(i);
						}
					}
				}
			}
			for (Integer string : list) {
				grade+=string+",";
			}
		}else if(departIds==null&&differManage){//不限制部门，限制经理级别
			
			Collection realA = new ArrayList<Integer>(manageRole);
			Collection realB = new ArrayList<Integer>(midMinRole);
			realA.retainAll(realB);
			if(realA.size()>0){
				for (int i=0;i<list.size();i++) {
					if(midMinGrade==list.get(i)){
						list.remove(i);
					}
				}
				for (Integer manageroleid : manageRole) {
					int manegegrade=bRoleService.getGradeByModuleNum(manageroleid,moduleNum);
					if(list.contains(manegegrade)){
						for (int i=0;i<list.size();i++) {
							if(manegegrade==list.get(i)){
								list.remove(i);
							}
						}
					}
				}
			}
			for (Integer string : list) {
				grade+=string+",";
			}
		}else{//限制部门，限制经理级别
			for (Integer did : departList) {
				//根据部门id获取所有角色id
				List<Integer> roleId=bDepartmentService.getRoleIds(did);
				Collection realA = new ArrayList<Integer>(roleId);
				Collection realB = new ArrayList<Integer>(midMinRole);
				Collection realC = new ArrayList<Integer>(manageRole);
				realC.retainAll(realA);
				realA.retainAll(realB);
				if(realA.size()>0){
					if(realC.size()>0){
						for (Object object : realC) {
							int manageGrade=bRoleService.getGradeByModuleNum((Integer)object, moduleNum);
							for (int i=0;i<list.size();i++) {
								if(manageGrade==list.get(i)){
									list.remove(i);
								}
							}
						}
					}else{
						for (int i=0;i<list.size();i++) {
							if(midMinGrade==list.get(i)){
								list.remove(i);
							}
						}
					}
				}
			}
			for (Integer string : list) {
				grade+=string+",";
			}
		}
	}
	return grade;
	}
	/**
	 * 动态获取流程角色
	 * param:openID
	 * result:用户角色id以及该流程节点的角色id的交集，若想判断是否可以审批，只要这个交集大于0即可
	 * 注意：还需要判断申请用户不能和审批用户是一个人
	 */
	@RequestMapping("getRoleList")
	@ResponseBody
	public List<Integer> getRoleList(String openId,String processname){
		//获取用户的id
		int userId=bUserService.getUserIdByOpenId(openId);
		//获取登录账号的所有角色
		List<Integer> roleIdByUserId = bUserService.getRoleIdByUserId(userId);
		//获取出库的角色
		BProcess process=bProcessService.getprocessByName(processname);
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		roleIdByUserId.retainAll(roleIds);
		return roleIdByUserId;
	}
	/**
	 * 动态每个节点审批人
	 * param:节点名称
	 * result:该节点审批用户
	 */
	@RequestMapping("getApprovalUsers")
	@ResponseBody
	public Set<UserEntity> getApprovalUsers(String processname){
		System.out.println("角色是"+processname);
		//获取出库的角色
		BProcess process=bProcessService.getprocessByName(processname);
		System.out.println("process是"+process);
		int processId=process.getId();
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		Set<UserEntity> userList=new HashSet<UserEntity>();
		Set<Integer> userIdList=new HashSet<Integer>();
		for (Integer integer : roleIds) {
			List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(integer);
			userIdList.addAll(userIdByRoleId);
		}
		for (Integer integer : userIdList) {
			UserEntity user=bUserService.getUserById(integer);
			if(user!=null){
				userList.add(user);
			}
		}
		return userList;
	}
	/**
	 * 获取抄送人
	 * param:openId
	 * result:该部门的所有抄送人
	 */
	@RequestMapping("getCopyUsers")
	@ResponseBody
	public Set<UserEntity> getCopyUsers(String openId){
		Integer userid = bUserService.getUserIdByOpenId(openId);
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userid);
		UserEntity user = bUserService.getUserById(userid);
		int realDepartmentid=departmentIdByUserId.get(0);
		//根据用户id获取用户所有角色id
		List<Integer> appRole = bUserService.getRoleIdByUserId(userid);
		BProcess process=bProcessService.getprocessByName("抄送人");
		int processId=process.getId();
		//所有抄送人角色id
		List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
		List<Integer> userId=new ArrayList<>();
		for (Integer integer : roleIds) {
			List<Integer> userIdByRoleId = bUserService.getUserIdByRoleId(integer);
			userId.addAll(userIdByRoleId);
		}
		//所有抄送人id
		Set<Integer> realuserid=new HashSet<>();
        for (Integer integer : userId) {
    	   realuserid.add(integer);
		}
        //获取自己部门拥有的抄送部门
        List<Integer> ownCopyDepart=bDepartmentService.getcopyDepartId(realDepartmentid);
        //所有抄送人部门id
        Set<UserEntity> userList=new HashSet<UserEntity>();
        List<Integer> departId=new ArrayList<>();
        for (Integer integer : realuserid) {
        	List<Integer> departid = bDepartmentService.getDepartmentIdByUserId(integer);
        	if(departid.size()>0){
        		departId.add(departid.get(0));
            	if(ownCopyDepart.contains(departid.get(0))){
            		UserEntity userById = bUserService.getUserById(integer);
            		userList.add(userById);
            	}
        	}
        	
        }
		return userList;
	}
}
