package com.hfoa.controller.department;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.entity.department.BDepartmentEntity;
import com.hfoa.entity.role.BRoleEntity;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.USerService;
/**
 * 
 * @author wzx
 * 部门信息
 */
@Controller
@RequestMapping("/department")
public class DepartmentController {

	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private USerService usSerService;
	@Autowired
	private BRoleService bRoleService;
	//获取所有部门信息
	@RequestMapping("/getAllDepartment")
	@ResponseBody
	public List<BDepartmentEntity> getAllDepartment(){
		System.out.println("查询所有部门");
		List<BDepartmentEntity> departList=bDepartmentService.getAllDepartment();
        return departList;
	}
	//获取所有可用的部门
	@RequestMapping("/getAllAvalibleDepart")
	@ResponseBody
	public List<BDepartmentEntity> getAllAvalibleDepart(){
		List<BDepartmentEntity> departList=bDepartmentService.getAllAvalibleDepart();
        return departList;
	}
	//根据部门id获取所有角色信息
	@RequestMapping("/getAlldepartByDId")
	@ResponseBody
	public String getAlldepartByDId(int departmentid){
		List<Integer> departmentIds=bDepartmentService.getAlldepartByDId(departmentid);
//		List<BDepartmentEntity> list =new ArrayList<>();
		String did="";
		if(departmentIds!=null&&departmentIds.size()>0){
			for (Integer integer : departmentIds) {
//				BDepartmentEntity depart=bDepartmentService.getDepartmentIdById(integer);
//				list.add(depart);
				did=did+integer+",";
			}
		}
		return did;
	}
	//部门信息
	//Web-显示部门相关信息
	@RequestMapping("/getAllDepartmentByPage")
	@ResponseBody
	public Object getAllDepartmentByPage(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BDepartmentEntity> list = new ArrayList<BDepartmentEntity>();
		list = bDepartmentService.departmentDisplayByPage(start, number);
		for (BDepartmentEntity bDepartmentEntity : list) {
			if(bDepartmentEntity.getAvailable()==1){
				bDepartmentEntity.setShowAvailable("是");
			}else{
				bDepartmentEntity.setShowAvailable("否");
			}
			bDepartmentEntity.setForeditRole(bDepartmentEntity.getId());
			bDepartmentEntity.setForShowRole(bDepartmentEntity.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bDepartmentService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	} 
	//添加部门信息
	@RequestMapping("/insertDepartment")
	@ResponseBody
	public Object insertDepartment(BDepartmentEntity department){
		return bDepartmentService.insert(department);
	}
	//修改部门信息
	@RequestMapping("/updateDepartment")
	@ResponseBody
	public Object updateDepartment(BDepartmentEntity department){
		return bDepartmentService.update(department);
	}
	//删除部门信息
	@RequestMapping("/deleteDepartment")
	@ResponseBody
	public Object deleteDepartment(int id){
		Integer result=bDepartmentService.deleteById(id);
		if(result!=0&&result!=null){
			usSerService.deleteMiddleDepartment(id);
			bRoleService.deleteMiddleDepartment(id);
		}
		return result;
	}
	//部门的模糊查询
	@RequestMapping("/searchDepartment")
	@ResponseBody
	public Object searchDepartment(HttpServletRequest request,String title){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<BDepartmentEntity> list = new ArrayList<BDepartmentEntity>();
		list = bDepartmentService.departmentVagueByPage(start, number,title);
		for (BDepartmentEntity bDepartmentEntity : list) {
			if(bDepartmentEntity.getAvailable()==1){
				bDepartmentEntity.setShowAvailable("是");
			}else{
				bDepartmentEntity.setShowAvailable("否");
			}
			bDepartmentEntity.setForeditRole(bDepartmentEntity.getId());
			bDepartmentEntity.setForShowRole(bDepartmentEntity.getId());
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = bDepartmentService.getVagueCount(title);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//修改部门角色中间表
	@RequestMapping("/updateMiddleDepartment")
	@ResponseBody
	public Object updateMiddleDepartment(String roleids,int departmentid){
		String[] split = roleids.split(",");
		bRoleService.deleteMiddleDepartment(departmentid);
		int result=0;
		for(int i=0;i<split.length;i++){
			result=bDepartmentService.insertMiddleRole(departmentid,Integer.parseInt(split[i]));
		}
		return result;
	}
	//修改抄送部门表
	@RequestMapping("/updateCopyDepartment")
	@ResponseBody
	public Object updateCopyDepartment(String departmentIds,int departmentid){
		String[] split = departmentIds.split(",");
		bDepartmentService.deleteCopyDepartmentByDid(departmentid);
		int result=0;
		for(int i=0;i<split.length;i++){
			result=bDepartmentService.insertCopyDepartment(departmentid,Integer.parseInt(split[i]));
		}
		return result;
	}
	
	//在前台显示部门信息（转换）
	@RequestMapping("/showDepartmentInfo")
	@ResponseBody
	public ModelAndView showDepartmentInfo(){
		ModelAndView result=new ModelAndView("department/department");
		return result;
	} 
}
