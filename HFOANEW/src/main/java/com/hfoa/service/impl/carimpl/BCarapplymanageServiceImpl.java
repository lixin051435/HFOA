package com.hfoa.service.impl.carimpl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hfoa.dao.car.BCarapplymanageMapper;
import com.hfoa.entity.car.CarApplyDTO;
import com.hfoa.entity.car.CarApplyInfo2;
import com.hfoa.service.car.BCarapplymanageService;

/**
 * 
 * @author wzx
 *公车申请的service实现类
 */
@Service
public class BCarapplymanageServiceImpl implements BCarapplymanageService{
	@Autowired
	private BCarapplymanageMapper bCarapplymanageMapper;

	//插入公车申请的业务信息
	@Override
	public int insertCarApply(CarApplyInfo2 carapply) {
		return bCarapplymanageMapper.insertCarApply(carapply);
	}

	//根据业务id修改业务状态
	@Override
	public int updateStatus(int status, int id) {
		return bCarapplymanageMapper.updateStatus(status,id);
	}

	//只能查询本部门的业务
	@Override
	public List<CarApplyInfo2> findBusinessByTasks(Set<String> businessIds, int departmentId) {
		List<String> list = new ArrayList<>();
		list.addAll(businessIds);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
	    return bCarapplymanageMapper.findByIds(list,departmentId);
	}
	
	//查询申请中最后一个审批单号
	@Override
	public String getlastApply() {
		return bCarapplymanageMapper.getlastApply();
	}

	//根据业务id查询业务信息
	@Override
	public CarApplyInfo2 getById(int carApplyId) {
		return bCarapplymanageMapper.getById(carApplyId);
	}

	//修改业务信息
	@Override
	public void updateCarApply(CarApplyInfo2 carApply) {
		bCarapplymanageMapper.updateCarApply(carApply);
	}

	//获取这个状态下的业务信息
	@Override
	public List<CarApplyInfo2> findTasksByStatus(Set<String> businessIds, int status) {
		List<String> list = new ArrayList<>();
		list.addAll(businessIds);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bCarapplymanageMapper.findTasksByStatus(list,status);
	}

	//根据当天时间查询在业务申请信息中的数量
	@Override
	public int getAppNumByLike(String date) {
		return bCarapplymanageMapper.getAppNumByLike(date);
	}

	//根据业务id，部门id查询业务
	@Override
	public List<CarApplyInfo2> findBusinessByTaskses(Set<String> keySet, List<Integer> departList) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bCarapplymanageMapper.findBusinessByTaskses(list,departList);
	}

	//根据sql语句查找公车申请信息
	@Override
	public List<CarApplyInfo2> getBySql(String result) {
		return bCarapplymanageMapper.getBySql(result);
	}

	//获取已申请数量
	@Override
	public int getAppCount(String carcode,String mon) {
		return bCarapplymanageMapper.getAppCount(carcode,mon);
	}

	//获取已预约数量
	@Override
	public int getAppointmentCount(String carcode,String mon) {
		return bCarapplymanageMapper.getAppointmentCount(carcode,mon);
	}

	//根据车牌号获取到车辆借用信息
	@Override
	public List<CarApplyInfo2> getByCarNum(String carNum) {
		return bCarapplymanageMapper.getByCarNum(carNum);
	}

	//根据申请人的账号查找申请人借车的信息
	@Override
	public List<CarApplyInfo2> getByAppUserName(String code) {
		return bCarapplymanageMapper.getByAppUserName(code);
	}

	//根据业务id删除业务信息
	@Override
	public void deleteById(Integer appId) {
		bCarapplymanageMapper.deleteByPrimaryKey(appId);
	}

	//根据车牌号查询该车辆正在申请的数量
	@Override
	public int getCountByCarNum(String carCode) {
		return bCarapplymanageMapper.getCountByCarNum(carCode);
	}

	//查询用户正在办理的业务
	@Override
	public List<CarApplyInfo2> findTasksByUserCode(Set<String> keySet, String code) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bCarapplymanageMapper.findTasksByUserCode(list,code);
	}

	//根据状态获取业务信息
	@Override
	public List<CarApplyInfo2> findByStatus(int i) {
		return bCarapplymanageMapper.findByStatus(i);
	}

	//根据审批人的账号获取待审批的业务信息
	@Override
	public Object selectNum(String code) {
		return bCarapplymanageMapper.selectNum(code);
	}

	//获取所有申请信息
	@Override
	public List<CarApplyInfo2> getAll(int start,int number) {
		return bCarapplymanageMapper.getAll(start,number);
	}

	//导出表单
	@Override
	public int export(String[] nlist, String filePath) {
		// 得到数据集合
		List<CarApplyInfo2> commonList = new ArrayList<CarApplyInfo2>();
		commonList = bCarapplymanageMapper.findAllPass();// 最终返回的list
		return exportExcel(commonList, filePath);
	}
	private int exportExcel(List<CarApplyInfo2> commonList, String filePath) {
		XSSFWorkbook workbook;
		String sheetName = "公车使用信息";
		try {
			workbook = new XSSFWorkbook();
			// 添加一个sheet,sheet名
			XSSFSheet sheet = workbook.createSheet(sheetName);
			// 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
			// 创建第一行
			XSSFRow titleRow = sheet.createRow(0);
			// 创建标题单元格格式
			XSSFCellStyle titleStyle = workbook.createCellStyle();
			// 创建一个居中格式
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			// 创建一个字体
			XSSFFont titleFont = workbook.createFont();
			titleFont.setFontHeight(16);// 标题字体为16号
			// 将字体应用到当前的格式中
			titleStyle.setFont(titleFont);
			// 在第一行中创建一个单元格
			XSSFCell titleCell = titleRow.createCell(0);
			// 设置值和样式，标题
			titleCell.setCellValue(sheetName);
			titleCell.setCellStyle(titleStyle);
			// ------------以上为第一行------------
			// 在合适位置调整自适应
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
			sheet.autoSizeColumn(10);
			sheet.autoSizeColumn(11);
			sheet.autoSizeColumn(12);
			sheet.autoSizeColumn(13);
			sheet.autoSizeColumn(14);
			sheet.autoSizeColumn(16);
			sheet.autoSizeColumn(17);
			sheet.autoSizeColumn(18);
			sheet.autoSizeColumn(19);
			sheet.autoSizeColumn(20);
			sheet.autoSizeColumn(21);
			// 设置其他正文单元格格式
			XSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			// 设置第二行表头
			XSSFRow rowHeader = sheet.createRow(1);
			XSSFCell cell = rowHeader.createCell(0);// 第1列
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(1);// 第2列
			cell.setCellValue("审批单号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(2);// 第3列
			cell.setCellValue("部门");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(3);// 第4列
			cell.setCellValue("申请人");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(4);// 第5列
			cell.setCellValue("审批人");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(5);// 第6列
			cell.setCellValue("车型");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(6);// 第7列
			cell.setCellValue("车牌号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(7);// 第8列
			cell.setCellValue("驾驶人");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(8);// 第9列
			cell.setCellValue("同行人数");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(9);// 第10列
			cell.setCellValue("出车里程");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(10);// 第11列
			cell.setCellValue("还车里程");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(11);// 第12列
			cell.setCellValue("计费里程");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(12);// 第13列
			cell.setCellValue("计划借车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(13);// 第14列
			cell.setCellValue("计划还车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(14);// 第15列
			cell.setCellValue("出车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(15);// 第16列
			cell.setCellValue("还车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(16);// 第17列
			cell.setCellValue("借车事由");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(17);// 第18列
			cell.setCellValue("出发地");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(18);// 第19列
			cell.setCellValue("目的地");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(19);// 第20列
			cell.setCellValue("计划用车时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(20);// 第21列
			cell.setCellValue("实际用车时间");
			cell.setCellStyle(style);
			
			
			// 表头完成------------------
			int index = 1;// 行号，应从第三行开始，每次循环进行++
			CarApplyInfo2 common = new CarApplyInfo2();
			// 遍历集合将数据写到excel中
			if (commonList.size() > 0) {
				for (int i = 0; i < commonList.size(); i++) {
					// 行号++，2开始
					index++;
					
					// 对象
					common = commonList.get(i);
					
					// 得到数据行数
					int hs = commonList.size();
					
					// 生成相应的行数
					XSSFRow row = sheet.createRow(index);
					
					XSSFCell rowCell = row.createCell(0);// 第1列(序号)
					rowCell.setCellValue(i + 1 + "");
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(1);// 第2列(公车-审批单号)
					rowCell.setCellValue(common.getApplyId());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(2);// 第3列(公车-部门)
					rowCell.setCellValue(common.getDepartment().replace(",", ""));
					rowCell.setCellStyle(style);

					rowCell = row.createCell(3);// 第4列(公车-申请人)
					rowCell.setCellValue(common.getApplyMan());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(4);// 第5列(公车-审批人)
					rowCell.setCellValue(common.getApproveMan());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(5);// 第6列(公车-车型)
					rowCell.setCellValue(common.getCarType());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(6);// 第7列(公车-车牌号)
					rowCell.setCellValue(common.getCarCode());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(7);// 第8列(公车-驾驶人)
					rowCell.setCellValue(common.getDriver());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(8);// 第9列(公车-同行人数)
					rowCell.setCellValue(common.getCompareManNum());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(9);// 第10列(公车-出车里程)
					rowCell.setCellValue(common.getLengthBegin());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(10);// 第11列(公车-还车里程)
					rowCell.setCellValue(common.getLengthEnd());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(11);// 第12列(公车-计费里程)
					rowCell.setCellValue(common.getAccountLength());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(12);// 第13列(公车-计划借车时间)
					rowCell.setCellValue(common.getBeginTimePlan());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(13);// 第14列(公车-计划还车时间)
					rowCell.setCellValue(common.getEndTimePlan());
					rowCell.setCellStyle(style);
				  	
					rowCell = row.createCell(14);// 第15列(公车-出车时间)
					rowCell.setCellValue(common.getBeginTime());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(15);// 第16列(公车-还车时间)
					rowCell.setCellValue(common.getEndTime());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(16);// 第17列(公车-借车事由)
					rowCell.setCellValue(common.getUseCarReason());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(17);// 第18列(公车-出发地)
					rowCell.setCellValue(common.getBeginPlace());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(18);// 第19列(公车-目的地)
					rowCell.setCellValue(common.getEndPlace());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(19);// 第20列(公车-计划用车时间)
					if(common.getAccountPlanTime()==null||"".equals(common.getAccountPlanTime())){
						rowCell.setCellValue("");
					}else{
						rowCell.setCellValue(common.getAccountPlanTime());
					}
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(20);// 第21列(公车-实际用车时间)
					if(common.getAccountRealTime()==null||"".equals(common.getAccountRealTime())){
						rowCell.setCellValue("");
					}else{
						rowCell.setCellValue(common.getAccountRealTime());
					}
					rowCell.setCellStyle(style);
				}
					//index=index+1; 
				}
			//}
			
			// 将文件保存到指定位置
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}     
	}

	//获取模糊查询总数
	@Override
	public int getCountBySQL(String countResult) {
		return bCarapplymanageMapper.getCountBySQL(countResult);
	}

	//获取所有业务信息总数
	@Override
	public int getAllCount() {
		return bCarapplymanageMapper.getAllCount();
	}

	//根据出库或者入库审批人获取业务信息
	@Override
	public List<CarApplyInfo2> findByOutOrInMan(String code) {
		return bCarapplymanageMapper.findByOutOrInMan(code);
	}

	//获取所有“我”发起的并且完成的业务信息
	@Override
	public List<CarApplyInfo2> getAllComplate() {
		return bCarapplymanageMapper.getAllComplate();
	}

	//根据申请车辆车牌号获取该车辆还未进行完的申请信息
	@Override
	public List<CarApplyInfo2> getNoFinishedInfo(String carCode,int id) {
		return bCarapplymanageMapper.getNoFinishedInfo(carCode,id);
	}

	//根据条件查询公车申请信息
	@Override
	public List<CarApplyInfo2> findTasksByCase(Set<String> keySet, int status, CarApplyDTO carUseDetailDTO) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		carUseDetailDTO.setList(list);
		carUseDetailDTO.setStatus(status);
		return bCarapplymanageMapper.findTasksByCase(carUseDetailDTO);
	}

	//根据申请人获取完成的申请信息
	@Override
	public List<CarApplyInfo2> getFinishByAppUserName(String code) {
		return bCarapplymanageMapper.getFinishByAppUserName(code);
	}

	//导数据
	@Override
	public List<CarApplyInfo2> getAllYuyue() {
		return bCarapplymanageMapper.getAllYuyue();
	}

	//获取所有公车申请进行中业务信息
	@Override
	public List<CarApplyInfo2> getAllRunTimeInfo(int start, int number) {
		return bCarapplymanageMapper.getAllRunTimeInfo(start,number);
	}

	//获取所有公车申请进行中业务信息数量
	@Override
	public int getAllRunTimeCount() {
		return bCarapplymanageMapper.getAllRunTimeCount();
	}
}
