package com.hfoa.controller.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.car.BCarbaseinfo;
import com.hfoa.entity.car.BCarbaseinfoDTO;
import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.role.BRoleEntity;
import com.hfoa.service.car.BCarbaseinfoService;
import com.hfoa.service.car.BCargasinfoService;
import com.hfoa.service.common.Constants;
import com.hfoa.service.common.DictManage;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.permission.BPermissionService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.util.TimeUtil;
/**
 * 
 * @author wzx
 * 角色管理
 */
@Controller
@RequestMapping("role")
public class RoleController {
	@Autowired
	private BRoleService bRoleService;
	@Autowired
	private DictManage dictManage;
	@Autowired
	private BPermissionService bPermissionService;
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BProcessService bProcessService;
	//角色信息
	//Web-显示角色相关信息
	@RequestMapping("/showAllRole")
	@ResponseBody
	public Object showAllRole(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BRoleEntity> list = new ArrayList<BRoleEntity>();
		list = bRoleService.roleDisplayByPage(start, number);
		for (BRoleEntity bRoleEntity : list) {
			if("".equals(bRoleEntity.getAvailable()+"")||bRoleEntity.getAvailable()==null){
				bRoleEntity.setShowAvailable("");
			}else if(bRoleEntity.getAvailable()==1){
				bRoleEntity.setShowAvailable("是");
			}else if(bRoleEntity.getAvailable()==0){
				bRoleEntity.setShowAvailable("否");
			}
			bRoleEntity.setForEditPermission(bRoleEntity.getId());
			bRoleEntity.setForEditProcess(bRoleEntity.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bRoleService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//添加角色信息
	@RequestMapping("/insertRole")
	@ResponseBody
	public Object insertRole(BRoleEntity bRoleEntity){
		int insert = bRoleService.insert(bRoleEntity);
		return insert;
	}
	//修改角色信息
	@RequestMapping("/updateRole")
	@ResponseBody
	public Object updateRole(BRoleEntity bRoleEntity){
		return bRoleService.update(bRoleEntity);
	}
	//删除角色
	@RequestMapping("/deleteRole")
	@ResponseBody
	public Object deleteRole(int id){
		int deleteById = bRoleService.deleteById(id);
		if(deleteById!=0){
			bRoleService.deleteMiddleRole(id);
			bPermissionService.deleteMiddlePermission(id);
			bDepartmentService.deleteMiddleRole(id);
			bProcessService.deleteMiddleRole(id);
		}
		return bRoleService.deleteById(id);
	}
	//角色的模糊查询
	@RequestMapping("/searchRole")
	@ResponseBody
	public Object searchRole(HttpServletRequest request,String title){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BRoleEntity> list = new ArrayList<BRoleEntity>();
		list = bRoleService.roleVagueByPage(start, number,title);
		for (BRoleEntity bRoleEntity : list) {
			if("".equals(bRoleEntity.getAvailable()+"")||bRoleEntity.getAvailable()==null){
				bRoleEntity.setShowAvailable("");
			}else if(bRoleEntity.getAvailable()==1){
				bRoleEntity.setShowAvailable("是");
			}else if(bRoleEntity.getAvailable()==0){
				bRoleEntity.setShowAvailable("否");
			}
			bRoleEntity.setForEditPermission(bRoleEntity.getId());
			bRoleEntity.setForEditProcess(bRoleEntity.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bRoleService.getVagueCount(title);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//获取所有角色
	@RequestMapping("/getAllRole")
	@ResponseBody
	public List<BRoleEntity> getAllRole(){
		List<BRoleEntity> list =bRoleService.findAll();
		return list;
	}
	//根据部门id获取所有角色信息
	@RequestMapping("/getAllRoleByDepartId")
	@ResponseBody
	public List<BRoleEntity> getAllRoleByDepartId(int departmentid){
		List<Integer> roleIds=bDepartmentService.getRoleIds(departmentid);
		List<BRoleEntity> list =null;
		if(roleIds!=null&&roleIds.size()>0){
			list =bRoleService.getRoleByRoleIds(roleIds);
		}
		return list;
	}
	//根据部门id获取角色id
	@RequestMapping("/getRoleByDepartmentId")
	@ResponseBody
	public String getRoleByDepartmentId(int departmentid){
		String roleId="";
		List<Integer> roleIds = bDepartmentService.getRoleIds(departmentid);
		for (Integer integer : roleIds) {
			roleId+=integer+",";
		}
		return roleId;
	}
	//分配权限
	@RequestMapping("/editMiddlePermission")
	@ResponseBody
	public void editMiddlePermission(int roleid,String permissionids){
		List<Integer> permissionList=bPermissionService.getPermissionByRole(roleid);
		String[] split = permissionids.split(",");
		if(permissionList.size()==0){
			for(int i=0;i<split.length;i++){
				bRoleService.insertMiddlePermission(roleid, Integer.parseInt(split[i]));
			}
		}else{
			bRoleService.deleteMiddlePermission(roleid);
			for(int j=0;j<split.length;j++){
			bRoleService.insertMiddlePermission(roleid, Integer.parseInt(split[j]));
			}
		}
	}
	//分配流程
	@RequestMapping("/editMiddleProcess")
	@ResponseBody
	public void editMiddleProcess(int roleid,String processids){
		List<Integer> processList=bProcessService.getProcessByRole(roleid);
		String[] split = processids.split(",");
		if(processList.size()==0){
			for(int i=0;i<split.length;i++){
				bRoleService.insertMiddleProcess(roleid, Integer.parseInt(split[i]));
			}
		}else{
			bProcessService.deleteMiddleRole(roleid);
			for(int j=0;j<split.length;j++){
			bRoleService.insertMiddleProcess(roleid, Integer.parseInt(split[j]));
			}
		}
	}
	
	//根据部门id获取角色信息
	@RequestMapping("/getRoleByDepartment")
	@ResponseBody
	public List<BRoleEntity> getRoleByDepartment(int departmentid){
		String roleId="";
		List<Integer> roleIds = bDepartmentService.getRoleIds(departmentid);
		List<BRoleEntity> roleList=null;
		if(roleIds.size()>0){
			roleList=bRoleService.getRoleByRoleIds(roleIds);	
		}
		
		return roleList;
	}
	//在前台显示角色信息（转换）
	@RequestMapping("/showRoleInfo")
	@ResponseBody
	public ModelAndView showRoleInfo(){
		ModelAndView result=new ModelAndView("role/role");
		return result;
	}
}
