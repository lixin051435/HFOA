package com.hfoa.service.car;

import java.util.List;
import java.util.Set;

import com.hfoa.entity.car.CarApplyDTO;
import com.hfoa.entity.car.CarApplyInfo2;
/**
 * 
 * @author wzx
 *公车申请的service接口
 */
public interface BCarapplymanageService {

	//插入公车申请的业务信息
	int insertCarApply(CarApplyInfo2 carapply);

	//根据业务id修改业务状态
	int updateStatus(int status, int id);

	//只能查询本部门的业务
	List<CarApplyInfo2> findBusinessByTasks(Set<String> keySet, int departmentId);

	//查询申请中最后一个审批单号
	String getlastApply();

	//根据业务id查询业务信息
	CarApplyInfo2 getById(int carApplyId);

	//修改业务信息
	void updateCarApply(CarApplyInfo2 carApply);

	//根据业务状态查找业务
	List<CarApplyInfo2> findTasksByStatus(Set<String> keySet, int i);

	//根据当天时间查询在业务申请信息中的数量
	int getAppNumByLike(String date);

	//根据业务id，部门id查询业务
	List<CarApplyInfo2> findBusinessByTaskses(Set<String> keySet, List<Integer> departList);

	//根据sql语句查找公车申请信息
	List<CarApplyInfo2> getBySql(String result);

	//获取申请数量
	int getAppCount(String carcode, String mon);

	//获取已预约数量
	int getAppointmentCount(String carcode, String mon);

	//根据车牌号获取到车辆的借用信息
	List<CarApplyInfo2> getByCarNum(String carNum);

	//根据申请人的账号查询用户申请的借车信息
	List<CarApplyInfo2> getByAppUserName(String code);

	//根据业务id删除业务信息
	void deleteById(Integer appId);

	//根据车牌号查询该车辆正在申请的数量
	int getCountByCarNum(String carCode);

	//查询用户的正在办理的业务
	List<CarApplyInfo2> findTasksByUserCode(Set<String> keySet, String code);

	//根据状态获取业务信息
	List<CarApplyInfo2> findByStatus(int i);

	//根据用户账号获取待审批的业务信息
	Object selectNum(String code);

	//获取所有申请信息
	List<CarApplyInfo2> getAll(int start, int number);

	//导出表单
	int export(String[] nlist, String filePath);

	//获取模糊查询总数
	int getCountBySQL(String countResult);

	//获取所有业务信息总数
	int getAllCount();

	//根据出库或者入库审批人获取业务信息
	List<CarApplyInfo2> findByOutOrInMan(String code);

	//获取所有“我”发起的并且完成的业务信息
	List<CarApplyInfo2> getAllComplate();

	//根据申请车辆车牌号获取该车辆还未进行完的申请信息
	List<CarApplyInfo2> getNoFinishedInfo(String carCode, int id);

	//条件查询公车申请信息
	List<CarApplyInfo2> findTasksByCase(Set<String> keySet, int status, CarApplyDTO carUseDetailDTO);

	//根据申请人获取完成的申请信息
	List<CarApplyInfo2> getFinishByAppUserName(String code);

	//导数据
	List<CarApplyInfo2> getAllYuyue();

	//获取所有公车申请进行中业务信息
	List<CarApplyInfo2> getAllRunTimeInfo(int start, int number);

	//获取所有公车申请进行中业务信息数量
	int getAllRunTimeCount();

}
