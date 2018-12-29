package com.hfoa.controller.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.service.permission.BPermissionService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.DepartmentService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.ListSortUtil;
import com.hfoa.util.MD5Util;

/**
 * 
 * @author wzx
 * 登录模块
 */
@Controller
@RequestMapping("login")
public class LoginController {

	@Autowired
	private USerService usSerService;
	
	@Autowired
	private DepartmentService departmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
	private BPermissionService bPermissionService;
	@RequestMapping(value = "/login")
	@ResponseBody
	public String login(HttpServletRequest request) {
		String flag = "0";
		
		String username = request.getParameter("username");// 获取用户名
		String pword = request.getParameter("password");
		
		UserEntity user = new UserEntity();
		if (pword==null){pword="";}
		String password = usSerService.getUserByUserCode(username).getUserpassword();// 获取密码
		String p = MD5Util.toMD5(pword);
		if (password.equals(p)) {
//			if("9".equals(description)){
//				flag = String.valueOf(description);
//			}else if("1".equals(qicq)){
//				flag = String.valueOf("3");
//			}else{
//				flag = String.valueOf(deptId);// 跳转
//			}
//			OperationRecord operationRecord = new OperationRecord();
//			operationRecord.setOperateInfo(username+"登录了系统");
//			operationRecord.setRealName(username);
//			operateRecordManage.insert(operationRecord);
			user=usSerService.getUserByUserCode(username);
			List<Integer> roleIds=usSerService.getRoleIdByUserId(user.getId());
			Set<Integer> permissionIds=new HashSet<Integer>();
			for (Integer integer : roleIds) {
				List<Integer> per=bPermissionService.getPermissionByRole(integer);
				permissionIds.addAll(per);
			}
			List<BPermissionEntity> permissionList=new ArrayList<>();
			for (Integer integer : permissionIds) {
				BPermissionEntity per=bPermissionService.getById(integer);
				permissionList.add(per);
			}
			ListSortUtil<BPermissionEntity> sortList = new ListSortUtil<BPermissionEntity>();
			sortList.sort(permissionList, "id", "asc");
			request.getSession().setAttribute("username", username);
			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("titles", permissionList);
			request.getSession().setAttribute("menus", permissionList);
//			model.addAttribute("user", user);
//			model.addAttribute("titles", permissionList);
//			model.addAttribute("menus", permissionList);
			flag = "1";
		} else {
			flag = "0";// 用户名或密码错误！
		}
		return flag;
	}
	@RequestMapping("/showMenu")
	@ResponseBody
	public List<BPermissionEntity> showMenu(HttpSession session,Model model){
		String code=(String) session.getAttribute("username");
	    UserEntity user=usSerService.getUserByUserCode(code);
		List<Integer> roleIds=usSerService.getRoleIdByUserId(user.getId());
		Set<Integer> permissionIds=new HashSet<Integer>();
		if(roleIds.size()>0){
			for (Integer integer : roleIds) {
				List<Integer> per=bPermissionService.getPermissionByRole(integer);
				if(per.size()>0){
					permissionIds.addAll(per);
				}
			}
		}
		
		List<BPermissionEntity> permissionList=new ArrayList<>();
		if(permissionIds.size()>0){
			for (Integer integer : permissionIds) {
				BPermissionEntity per=bPermissionService.getById(integer);
				permissionList.add(per);
			}
		}
		return permissionList;
	}
	//在前台显示角色信息（转换）
	@RequestMapping("/mainPage")
	@ResponseBody
	public ModelAndView mainPage(){
		ModelAndView result=new ModelAndView("common/hfoa");
		return result;
	}
	//登录界面
	@RequestMapping("/loginPage")
	@ResponseBody
	public ModelAndView loginPage(){
		ModelAndView result=new ModelAndView("common/login");
		return result;
	}
}
