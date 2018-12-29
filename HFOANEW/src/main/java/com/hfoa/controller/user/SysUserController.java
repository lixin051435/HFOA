package com.hfoa.controller.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hfoa.common.AnccResult;
import com.hfoa.dao.entertain.EntertainMapper;
import com.hfoa.dao.privatecar.PrivateMapper;
import com.hfoa.dao.travelExpenses.ApplyExpensesMapper;
import com.hfoa.entity.entertain.Entertainapplyinfo;
import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.privatecar.PrivateCarEntity;
import com.hfoa.entity.travelExpenses.ApplyExpensesEntity;
import com.hfoa.entity.user.DepartmentEntity;
import com.hfoa.entity.user.FindLeaderDTO;
import com.hfoa.entity.user.UserEntity;
import com.hfoa.entity.vacation.LearYear;
import com.hfoa.service.department.BDepartmentService;
import com.hfoa.service.leaver.YearLearService;
import com.hfoa.service.permission.BPermissionService;
import com.hfoa.service.permission.BProcessService;
import com.hfoa.service.role.BRoleService;
import com.hfoa.service.user.DepartmentService;
import com.hfoa.service.user.USerService;
import com.hfoa.util.DynamicGetRoleUtil;
import com.hfoa.util.MD5Util;


@Controller
@RequestMapping("user")
public class SysUserController {
	
	@Autowired
	private USerService usSerService;
	
	@Autowired
	private ApplyExpensesMapper applyExpensesMapper;
	
	@Autowired
	private PrivateMapper privateMapper;
	
	@Autowired
	private EntertainMapper entertainMapper;
	
	@Autowired
	private DepartmentService departmentService;
	
	@Autowired
	private YearLearService yearLearService;
	
	@Autowired
	private BDepartmentService bDepartmentService;
	@Autowired
	private BRoleService bRoleService;
	@Autowired
	private BPermissionService bPermissionService;
	@Autowired
	private DynamicGetRoleUtil dynamicGetRoleUtil;
	@Autowired
	private BProcessService bProcessService;
	@RequestMapping(value="/getDay",method = RequestMethod.GET)
	@ResponseBody
	public AnccResult getUser(String id) throws ParseException{
		UserEntity user = usSerService.select(id);
		
		return AnccResult.ok(user.getCreateby());
	}
	
	/**
	 * 根据不同的权限获取不同的部门
	 * @param openId
	 * @return
	 */
	@RequestMapping("/departmentopenId")
	@ResponseBody
	public AnccResult departmentOpenId(String openId){
		
		return AnccResult.ok(usSerService.getdepartmentOpenId(openId));
	}
	
	
	
	
	
	@RequestMapping("/getDepartment")
	@ResponseBody
	public List<DepartmentEntity> getDepartment(HttpServletRequest request){
		
		Object object = request.getSession().getAttribute("name");
		System.out.println("object是"+object);
		System.out.println("查看部门");
		System.out.println("查询的部门是"+departmentService.selectDepartment());
		return departmentService.selectDepartment();
	}
	
	
	@RequestMapping("/finaceApplyExpense")
	@ResponseBody
	public AnccResult finaceApplyExpense(String processname){
		processname="财务审批";
		Set<UserEntity> users = dynamicGetRoleUtil.getApprovalUsers(processname);
		return AnccResult.ok(users);
	}
	
	//绑定openId
	@RequestMapping("/updateUserOpenId")
	@ResponseBody
	public AnccResult bindingUser(String userName,String password,String openId){
		System.out.println("绑定用户"+userName);
		System.out.println("密码"+password);
		System.out.println("OpenId是"+openId);
		/*UserEntity entity = usSerService.loginOpenId(openId);
		if(entity!=null){
			
		}*/
		Map<String,Object> map = new HashMap<String,Object>();
		
		UserEntity userEntity = usSerService.tologin(userName, password);
		if(userEntity==null){
			map.put("status","1");
			map.put("error", "账号密码输入错误");
			return AnccResult.ok(map);
		}
		if(userEntity.getOpenid()!=null&&!userEntity.getOpenid().trim().equals("")){
			map.put("status", "1");
			map.put("error", "此用户已经绑定");
			return AnccResult.ok(map);
		}
		UserEntity entity = usSerService.loginOpenId(openId);
		if(entity==null){
			userEntity.setOpenid(openId);
			int i = usSerService.updateOpenId(userEntity);
			ApplyExpensesEntity applyExpensesEntity = new ApplyExpensesEntity();
			applyExpensesEntity.setApplyMan(userEntity.getCode());
			applyExpensesEntity.setOpenId(openId);
			applyExpensesMapper.updateOpenId(applyExpensesEntity);//差旅
			applyExpensesEntity.setApproveMan(userEntity.getCode());
			applyExpensesEntity.setApproveManOpenId(openId);
			applyExpensesMapper.updateApproveOpenId(applyExpensesEntity);
			PrivateCarEntity privateCarEntity = new PrivateCarEntity();
			privateCarEntity.setApplyMan(userEntity.getCode());
			privateCarEntity.setOpenId(openId);
			privateMapper.updateOpenId(privateCarEntity);//私车
			privateCarEntity.setApproveMan(userEntity.getCode());
			privateCarEntity.setApproveOpenId(openId);
			privateMapper.updateApproveOpenId(privateCarEntity);
			Entertainapplyinfo entertainapplyinfo = new Entertainapplyinfo();
			entertainapplyinfo.setOpenId(openId);
			entertainapplyinfo.setManager(userEntity.getCode());
			entertainMapper.updateOpenId(entertainapplyinfo);
			entertainapplyinfo.setApprover(userEntity.getCode());
			entertainapplyinfo.setApproverOpenid(openId);
			entertainMapper.updateApproverOpenid(entertainapplyinfo);
			if(i==1){
				map.put("status", "0");
				map.put("error", "绑定成功");
				return AnccResult.ok(map);
			}
		}else{
			map.put("status", "1");
			map.put("error", "已绑定用户");
			return AnccResult.ok(map);
		}
		return AnccResult.ok("绑定失败");
		
	}
	
	@RequestMapping("/getopenId")
	@ResponseBody
	public AnccResult getopenId(String openId){
		Map<String,Object> map = new HashMap<>();
		if(!openId.equals("")&&openId!=null){
			UserEntity userEntity = usSerService.loginOpenId(openId);
			
			if(userEntity!=null){
				map.put("status", "1");
				map.put("user", userEntity);
			}else{
				map.put("status", "0");
				map.put("error", "用户查询失败");
			}
		}else{
			map.put("status", "0");
			map.put("error", "没有openId");
		}
		
		return AnccResult.ok(map);
	}

	/**
	 * 解绑
	 * @param userName
	 * @param password
	 * @return
	 */
	@RequestMapping("/untieOpenIdUser")
	@ResponseBody
	public AnccResult untieOpenIdUser(String openId){
		UserEntity userEntity = usSerService.loginOpenId(openId);
		if(userEntity==null){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("status","1");
			map.put("error", "用户查询失败");
			return AnccResult.ok(map);
		}
		userEntity.setOpenid("");
		int i = usSerService.updateOpenId(userEntity);
		if(i==1){
			return AnccResult.ok("解绑成功");
		}
		return AnccResult.ok("解绑失败");
	}
	
	/**
	 * 登录
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/toLogin")
	@ResponseBody
	public AnccResult login(String openId,HttpServletRequest request) throws ParseException{
		System.out.println("登录通过Openid"+openId);
		UserEntity userEntity = usSerService.loginOpenId(openId);
		SimpleDateFormat smf = new SimpleDateFormat("yyyy");
		
		LearYear leaver = yearLearService.getOpenIdLeaver(openId,smf.format(new Date()));
		if(userEntity==null){
			
			return AnccResult.build(1, "用户查询失败");
		}
		
		FindLeaderDTO findLeaderDTO = new FindLeaderDTO();
		UserEntity userByUserCode = usSerService.getUserByUserCode(userEntity.getCode());
		List<Integer> departmentIdByUserId = bDepartmentService.getDepartmentIdByUserId(userByUserCode.getId());
		for (Integer departId : departmentIdByUserId) {
			String department=bDepartmentService.getDepartmentIdById(departId).getDepartmentname();
			findLeaderDTO.setDepartment(department);
		}
		
		if(null!=userEntity.getWorkgroupid()&&!userEntity.getWorkgroupid().equals("")){
			findLeaderDTO.setWorkgroupid(userEntity.getWorkgroupid());
		}
		if(!userEntity.getUsername().equals("")&&userEntity.getUsername()!=null){
			findLeaderDTO.setUsername(userEntity.getUsername());
		}
//		AnccResult leader = findLeader(findLeaderDTO); 
		List<UserEntity> approvalMan = dynamicGetRoleUtil.getApprovalMan(userByUserCode.getId());
		approvalMan = removeDuplicate(approvalMan);
		
		
		AnccResult days = getUser(String.valueOf(userEntity.getId()));
		//获取权限模块
		List<Integer> roleIds=usSerService.getRoleIdByUserId(userEntity.getId());
		Set<Integer> permissionIds=new HashSet<Integer>();
		for (Integer integer : roleIds) {
			List<Integer> per=bPermissionService.getPermissionByRole(integer);
			permissionIds.addAll(per);
			
		}
//		for (Integer integer2 : permissionIds) {
//			System.out.println(integer2+"权限id");
//		}
		List<BPermissionEntity> permissionList=new ArrayList<>();
		List<BPermissionEntity> myPermissionList=new ArrayList<>();
		List<BPermissionEntity> serchPermissionList=new ArrayList<>();
		List<BPermissionEntity> approvalPermissionList=new ArrayList<>();
//		Map<String ,Object>map1 = new HashMap<>();
		for (Integer integer : permissionIds) {
			BPermissionEntity per=bPermissionService.getById(integer);
			
//			if("2".equals(per.getUrlclass())||"3".equals(per.getUrlclass())){
			if("2".equals(per.getUrlclass())&&"first".equals(per.getPermissionlable())){
				//是否可以提交申请
				if(per.getPermissionname().contains("私车")){
					BProcess process=bProcessService.getprocessByName("私车申请（不可操作）");
					if(process!=null){
						int processId=process.getId();
						List<Integer> roleids=bRoleService.getRoleByProcessId(processId);
						List<Integer> roleIdByUserId = usSerService.getRoleIdByUserId(userByUserCode.getId());
						roleIdByUserId.retainAll(roleids);
						if(roleIdByUserId.size()<=0){
							permissionList.add(per);
						}
					}
					
				}
				if(!per.getPermissionname().contains("私车")){
					permissionList.add(per);
				}
			}
			if("2".equals(per.getUrlclass())&&"my".equals(per.getPermissionlable())){
				myPermissionList.add(per);
			}
			
		}
		//查询里边显示模块
		for (BPermissionEntity bPermissionEntity : permissionList) {
			List<BPermissionEntity> byNodeType = bPermissionService.getByNodeType(bPermissionEntity.getId()+"");
			serchPermissionList.addAll(byNodeType);
		}
		//待我审批界面跳转显示
		
		List<Integer> roleId = usSerService.getRoleIdByUserId(userByUserCode.getId());
		Set<BProcess> processlist=new HashSet<BProcess>();
		Set<Integer> processIds=new HashSet<Integer>();
		for (Integer rid : roleId) {
			List<Integer> processId = bProcessService.getProcessByRole(rid);
			processIds.addAll(processId);
		}
		for (Integer pid : processIds) {
			BProcess process=bProcessService.selectById(pid);
			processlist.add(process);
		}
		//动态加载按钮
//		for (BPermissionEntity bPermissionEntity : permissionList) {
//			List<BPermissionEntity> approval=bPermissionService.getByNodeType(bPermissionEntity.getId()+"");
//			if(approval!=null){
//				map1.put(bPermissionEntity.getId()+"", approval);
//			}
//		}
		Map<String ,Object>map = new HashMap<>();
		map.put("userEntity", userEntity);
		map.put("leader",  approvalMan);
		map.put("days", days.getData());
		map.put("leaver", leaver);
		if(permissionList.size()>0){
			map.put("firstPermission", permissionList);
		}else if(permissionList.size()==0){
			map.put("firstPermission", null);
		}
		if(myPermissionList.size()>0){
			map.put("myPermission", myPermissionList);
		}else if(myPermissionList.size()==0){
			map.put("myPermission", null);
		}
		
//		map.put("approvalPermission", map1);
		map.put("process", processlist);
		map.put("search", serchPermissionList);
		return AnccResult.ok(map);
	}
	
	/**
	 * 通过Openid查询用户和部门执行完成的
	 */
	@RequestMapping("/getOpenIdUserentity")
	@ResponseBody
	public AnccResult tologedit(String openId){
		UserEntity userEntity = usSerService.loginOpenId(openId);
		Map<String,String> map = new HashMap<>();
		if(userEntity==null){
			map.put("status", "1");
			map.put("mage", "获取用户失败");
			return AnccResult.ok(map);
		}
		
		return AnccResult.ok(userEntity);
		
	}
	
	/**
	 * 查询审批人
	 * @return
	 */
//	@RequestMapping("/findLeader")
//	@ResponseBody
//	public AnccResult findLeader(FindLeaderDTO leaderDTO){
//		System.out.println("传过来的数据"+leaderDTO);
//		// 获取传过来的DTO
//		String department = leaderDTO.getDepartment();
//		String roleid = leaderDTO.getRoleid();
//		String workgroupid = leaderDTO.getWorkgroupid();
//		String username = leaderDTO.getUsername();
//		HashMap<String,String>map = new HashMap<>();
//		map.put("department", department);
//		map.put("username", username);
//		UserEntity users = new UserEntity();
//		users.setDepartmentname(department);
//		users.setUsername(username);
//		UserEntity user = usSerService.findUserByNameAndDept(users).get(0);
//		System.out.println("user是"+user);
//		String secondManager = user.getWorkcategory();
//		String userLeader = user.getWorkgroupname();
//		String leaderId = user.getMobile();
//		HashMap<String,String> findMap = new HashMap<>();
//		//部门副经理 WorkCategory=1说明是部门副经理
//		if("1".equals(secondManager)){
//			
//			List<UserEntity> usersEntity = usSerService.findLeader1(user);
//			System.out.println("部门副经理"+usersEntity);
//			return AnccResult.ok(usersEntity);
//		}
//		//普通员工
//		if ("1".equals(roleid)) {
//			List<UserEntity> findLeader = usSerService.findLeader(department, "2");
//			
//			return AnccResult.ok(findLeader);
//		//部门经理
//		} else if ("2".equals(roleid)) {
//			System.out.println("查询id是"+workgroupid);
//			List<UserEntity> findLeader2 = usSerService.findleader2(workgroupid);
//			System.out.println("部门经理"+findLeader2);
//			
//			return AnccResult.ok(findLeader2);
//		//公司领导 roleid==3&&department==公司领导
//		} else if ("3".equals(roleid)&&"公司领导".equals(department)) {
//			System.out.println("leaderId是="+leaderId);
//			System.out.println("userLeader是="+userLeader);
//			List<UserEntity> findUserByNameAndId = usSerService.findUserByNameAndId(leaderId, userLeader);
//			
//			return AnccResult.ok(findUserByNameAndId);
//		}
//		
//		 
//		return AnccResult.ok(usSerService.findLeader(department,"2"));
//	}
	
	//获取审批人
	/**
	 * 
	 * @param openId 小程序Id
	 * @param moduleNum 功能Id
	 * @return
	 */
	@RequestMapping("/getFinder")
	@ResponseBody
	public AnccResult getFinder(String openId,String moduleNum){
		List<UserEntity> finder = usSerService.getFinder(openId, moduleNum);
		UserEntity userEntity = usSerService.loginOpenId(openId);
		Map<String,Object> map = new HashMap<>();
		map.put("finder", finder);
		map.put("user", userEntity);
		return AnccResult.ok(map);
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
	
	//业务招待获取审批人
	/**
	 * 
	 * @param openId 小程序Id
	 * @param moduleNum 功能Id
	 * @return
	 */
	@RequestMapping("/getEntertainFinder")
	@ResponseBody
	public AnccResult getEntertainFinder(String openId){
		Integer userId = usSerService.getUserIdByOpenId(openId);
		List<UserEntity> approvalMan = dynamicGetRoleUtil.getApprovalMan(userId);
		approvalMan = removeDuplicate(approvalMan);
		List<Integer> roleids = usSerService.getRoleIdByUserId(userId);
		BProcess pro=bProcessService.getprocessByName("事前申请");
		List<Integer> roleIdes=bRoleService.getRoleByProcessId(pro.getId());
		roleIdes.retainAll(roleids);
		if(roleIdes.size()>0){
			//获取业务招待审批
			int processId=0;
			List<BProcess> processlist=bProcessService.getByName("领导审批");
			for (BProcess bProcess : processlist) {
				BProcess process=bProcessService.getById(bProcess.getParentid());
				if("业务招待".equals(process.getProcessname())){
					processId=bProcess.getId();
				}
			}
			
			List<Integer> roleIds=bRoleService.getRoleByProcessId(processId);
			TreeSet<Integer> userIdByRoleId = new TreeSet<Integer>(); 
			List <Integer> userList=new ArrayList<>();
			for (Integer integer : roleIds) {
				userList.addAll(usSerService.getUserIdByRoleId(integer));
			}
			UserEntity userById =null;
			for (Integer integer : userList) {
				userIdByRoleId.add(integer);
			}
			for (Integer integer : userIdByRoleId) {
				userById = usSerService.getUserById(integer);
				if(userById!=null){
					approvalMan.add(userById);
				}
			}
		}
		UserEntity userEntity = usSerService.loginOpenId(openId);
		Map<String,Object> map = new HashMap<>();
		map.put("finder", approvalMan);
		map.put("user", userEntity);
		return AnccResult.ok(map);
	}
	
	@RequestMapping("/getCcList")
	@ResponseBody
	public AnccResult getCcList(String moduleNum,String openId){
		System.out.println("查看抄送人");
		
		return AnccResult.ok(usSerService.getCcList(moduleNum,openId));
	}
	
	@RequestMapping("/defaultgetCcList")
	@ResponseBody
	public AnccResult defaultgetCcList(String openId){
		
		
		return AnccResult.ok(usSerService.defaultgetCcList(openId));
	}
	
	
	@RequestMapping("/getmoney")
	@ResponseBody
	public AnccResult getmoney(String openId,String entertainCategory,String perBudget){
		
		return AnccResult.ok(usSerService.getmoney(openId,entertainCategory,perBudget));
	}
	
	
	
	
	
	public int checkeDays(String hireDate) throws ParseException{
	     SimpleDateFormat smf = new SimpleDateFormat("YYYY-MM-DD");
	     Date startTime = smf.parse(hireDate);
	     Date nowDate = new Date();
	     System.out.println("now是"+nowDate);
	     return  differentDaysByMillisecond(nowDate,startTime);
	}
	
	
	 public  int differentDaysByMillisecond(Date date1,Date date2)
	    {
	        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
	        return days;
	    }
	 
	//用户信息
	//Web-显示用户相关信息
	@RequestMapping("/showAllUser")
	@ResponseBody
	public Object showAllUser(HttpServletRequest request){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<UserEntity> list = new ArrayList<UserEntity>();
		List<UserEntity> reallist = usSerService.userDisplayByPage(start, number);
		for (UserEntity userEntity : reallist) {
			if(!"admin".equals(userEntity.getUsername())){
				list.add(userEntity);
			}
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = usSerService.getAllCount();
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	} 
	//添加用户信息
	@RequestMapping("/insertUser")
	@ResponseBody
	public Object insertUser(UserEntity user){
		int flag=1;
		String code=user.getCode();
		//获取所有的用户账号
		List<UserEntity> all = usSerService.getAll();
		for (UserEntity userEntity : all) {
			if(code.equals(userEntity.getCode())){
				flag=0;
			}
		}
		if(flag==1){
//			String roleId=user.getDuty();
			String pwd5= MD5Util.toMD5(user.getUserpassword());
			user.setUserpassword(pwd5);
			Date now=new Date();
			user.setCreateon(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
			usSerService.insert(user);
			Integer id=user.getId();
			if(id!=0||id!=null){
				bDepartmentService.insertMiddleUser(id,user.getDepartmentid());
//				String[] split = roleId.split(",");
//				for(int i=0;i<split.length;i++){
//					bRoleService.inserMiddleUser(id,Integer.parseInt(split[i]));
//				}
				
			}
		}

		return flag;
	}
	//修改用户信息
	@RequestMapping("/updateUser")
	@ResponseBody
	public Object updateUser(UserEntity user){
		//获取原密码
		UserEntity userById = usSerService.getUserById(user.getId());
		int flag=1;
		String code=user.getCode();
		//根据账号获取数量
		int count=usSerService.getCountByCode(code);
		if(count>=1){
			UserEntity userByUserCode = usSerService.getUserByUserCode(code);
			if(!userByUserCode.getId().equals(user.getId())){
				flag=0;
			}else{
				flag=1;
			}
		}else{
			flag=1;
		}
//		List<UserEntity> all = usSerService.getAll();
//		for (UserEntity userEntity : all) {
//			if(code.equals(userEntity.getCode())&&user.getId()!=userEntity.getId()){
//				flag=0;
//			}
//		}
		if(flag==1){
			if("".equals(user.getUserpassword())){
				user.setUserpassword(userById.getUserpassword());
			}else{
				String pwd5= MD5Util.toMD5(user.getUserpassword());
				user.setUserpassword(pwd5);
			}
		if(!"".equals(user.getLeadername())){
			Integer uid=usSerService.getUserByUserCode(user.getLeadername()).getId();
			if(uid!=null){
				user.setWorkgroupid(uid+"");
			}
			user.setLeadername(user.getLeadername());
		}
		
		int departmentId=bDepartmentService.getDepartmentIdByName(user.getDepartmentname());
		user.setDepartmentid(departmentId);
		Integer id=usSerService.update(user);
//		String[] split;
		if(id!=0||id!=null){
			bDepartmentService.updateMiddleUser(user.getId(),departmentId);
//			bRoleService.deleteMiddleUser(user.getId());
//			if(!"".equals(user.getDuty())){
//				split = user.getDuty().split(",");
//				for(int i=0;i<split.length;i++){
//					bRoleService.inserMiddleUser(user.getId(),Integer.parseInt(split[i]));
//				}
//			}
		}
		//修改表中是领导人的账号
		usSerService.updateApprovalName(user.getCode(),user.getId());
		}
		return flag;
	}
	//根据用户id修改角色
	@RequestMapping("/updateRoleByUserId")
	@ResponseBody
	public void updateRoleByUserId(String roleids,int userId){
		String[] split=roleids.split(",");
		bRoleService.deleteMiddleUser(userId);
		if(!"".equals(roleids)){
		for(int i=0;i<split.length;i++){
			bRoleService.inserMiddleUser(userId,Integer.parseInt(split[i]));
		}
		}
	}
	//删除用户
	@RequestMapping("/deleteUser")
	@ResponseBody
	public Object deleteUser(int id){
		Integer result=usSerService.deleteById(id);
		if(result!=0&&result!=null){
			bDepartmentService.deleteMiddleUser(id);
			bRoleService.deleteMiddleUser(id);
		}
		return result;
	}
	//用户的模糊查询
	@RequestMapping("/searchUser")
	@ResponseBody
	public Object searchUser(HttpServletRequest request,String title){
		String page = request.getParameter("page");// 第几页
		String rows = request.getParameter("rows");// 每页多少条
		int intPage = Integer.parseInt((page == null || page == "0") ? "1" : page);// 页数
		int number = Integer.parseInt((rows == null || rows == "0") ? "20" : rows);// 每页个数
		// 每页的开始记录
		int start = (intPage - 1) * number;
		List<UserEntity> list = new ArrayList<UserEntity>();
		List<UserEntity> reallist = usSerService.userVagueByPage(start, number,title);
		for (UserEntity userEntity : reallist) {
			if(!"admin".equals(userEntity.getUsername())){
				list.add(userEntity);
			}
		}
		Map<String, Object> jsonMap = new HashMap<String, Object>();// 定义map
		int total = usSerService.getVagueCount(title);
		jsonMap.put("total", total);// total存放总记录数
		jsonMap.put("rows", list);// rows键，存放每页记录list
		return jsonMap;
	}
	//获取所有领导级别的人
	@RequestMapping("/getAllLeader")
	@ResponseBody
	public Object getAllLeader(){
		//获取领导级别
		int grade=bRoleService.getLeaderGrade();
		//获取所有领导级别的角色id
		int roleId=bRoleService.getLeaderRole(grade);
		//根据角色id获取所有用户id
		List<Integer> userIdList=usSerService.getUserIdByRoleId(roleId);
		List<UserEntity> list = new ArrayList<>();
		for (Integer uid : userIdList) {
			UserEntity user=usSerService.getUserById(uid);
			list.add(user);
		}
		
		return list;
	}
	//根据用户id获取角色id
	@RequestMapping("/getRoleByUserId")
	@ResponseBody
	public String getRoleByUserId(int userId){
		String roleId="";
		List<Integer> roleIds = usSerService.getRoleIdByUserId(userId);
		for (Integer integer : roleIds) {
			roleId+=integer+",";
		}
		return roleId;
	}
	//在前台显示用户信息（转换）
	@RequestMapping("/showUserInfo")
	@ResponseBody
	public ModelAndView showUserInfo(){
		ModelAndView result=new ModelAndView("user/user");
		return result;
	} 
	 
	 
	
	
	
	
	
	
	@RequestMapping("/getDataFromExcel")
	@ResponseBody
	 public  String getDataFromExcel(String filePath)
	    {
	    	filePath = "C:\\u_user.xlsx";//差旅
	        //String filePath = "E:\\123.xlsx";
	        //判断是否为excel类型文件
	        if(!filePath.endsWith(".xls")&&!filePath.endsWith(".xlsx"))
	        {
	            System.out.println("文件不是excel类型");
	        }
	        FileInputStream fis =null;
	        XSSFWorkbook wookbook = null;
	        
	        try
	        {
	            //获取一个绝对地址的流
	              fis = new FileInputStream(filePath);
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	        }
	            try
	            {
	                //2007版本的excel，用.xlsx结尾
	                wookbook = new XSSFWorkbook(fis);//得到工作簿
	            } catch (IOException e)
	            {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        //得到一个工作表
	        Sheet sheet = wookbook.getSheetAt(0);
	        //获得表头
	        Row rowHead = sheet.getRow(0);
	        
	        //获得数据的总行数
	        int totalRowNum = sheet.getLastRowNum();
	        //要获得属性
	     
	       //获得所有数据
	        for(int i = 1 ; i <= totalRowNum ; i++)
	        {
	            //获得第i行对象
	            Row row = sheet.getRow(i);
	            
	            //获得获得第i行第0列的 String类型对象
	           
	            if(row.getCell((short)1)!=null){
	            	 Cell cell = row.getCell((short)1);
	            	 String code = cell.getStringCellValue().toString();
	                 System.out.println("ID是"+code);
	                 
	                 cell = row.getCell((short)52);
	                 String openId = cell.getStringCellValue().toString();
	                 System.out.println("申请人"+openId );
	                 UserEntity userEntity = usSerService.loginOpenId(openId);
	                 if(userEntity!=null){
	                	 ApplyExpensesEntity applyExpensesEntity = new ApplyExpensesEntity();
	         			applyExpensesEntity.setApplyMan(userEntity.getCode());
	         			applyExpensesEntity.setOpenId(openId);
	         			applyExpensesMapper.updateOpenId(applyExpensesEntity);//差旅
	         			applyExpensesEntity.setApproveMan(userEntity.getCode());
	         			applyExpensesEntity.setApproveManOpenId(openId);
	         			applyExpensesMapper.updateApproveOpenId(applyExpensesEntity);
	         			PrivateCarEntity privateCarEntity = new PrivateCarEntity();
	         			privateCarEntity.setApplyMan(userEntity.getCode());
	         			privateCarEntity.setOpenId(openId);
	         			privateMapper.updateOpenId(privateCarEntity);//私车
	         			privateCarEntity.setApproveMan(userEntity.getCode());
	         			privateCarEntity.setApproveOpenId(openId);
	         			privateMapper.updateApproveOpenId(privateCarEntity);
	         			Entertainapplyinfo entertainapplyinfo = new Entertainapplyinfo();
	         			entertainapplyinfo.setOpenId(openId);
	         			entertainapplyinfo.setManager(userEntity.getCode());
	         			entertainMapper.updateOpenId(entertainapplyinfo);
	         			entertainapplyinfo.setApprover(userEntity.getCode());
	         			entertainapplyinfo.setApproverOpenid(openId);
	         			entertainMapper.updateApproverOpenid(entertainapplyinfo);
	                 }
	            }
	            
	        }
	        return "SUCCESS";
	    }
		
	
	
	
	
	
	
	
	
	
	
	
	
	 
}
