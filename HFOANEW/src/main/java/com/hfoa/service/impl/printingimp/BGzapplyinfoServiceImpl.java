package com.hfoa.service.impl.printingimp;

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

import com.hfoa.dao.printing.BGzapplyinfoMapper;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.service.printing.BGzapplyinfoService;

/**
 * 
 * @author wzx
 * 用印service实现类
 */
@Service
public class BGzapplyinfoServiceImpl implements BGzapplyinfoService{

	@Autowired
	private BGzapplyinfoMapper bGzapplyinfoMapper;

	//修改申请信息
	@Override
	public int update(BGzapplyinfo bGzapplyinfo) {
		return bGzapplyinfoMapper.update(bGzapplyinfo);
	}

	//添加申请信息
	@Override
	public int insert(BGzapplyinfo bGzapplyinfo) {
		return bGzapplyinfoMapper.insert(bGzapplyinfo);
	}

	//删除申请信息
	@Override
	public int deleteById(int id) {
		return bGzapplyinfoMapper.deleteById(id);
	}

	//根据id获取用印申请信息
	@Override
	public BGzapplyinfo getById(int id) {
		return bGzapplyinfoMapper.getById(id);
	}

	//根据业务状态获取业务信息
	@Override
	public List<BGzapplyinfo> findTasksByStatus(Set<String> keySet, String status) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bGzapplyinfoMapper.findTasksByStatus(list,status);
	}

	//查看当天相同部门办理业务数量
	@Override
	public int getApprovalCount(String approvalId) {
		return bGzapplyinfoMapper.getApprovalCount(approvalId);
	}

	//根据申请人账号查找申请人的申请信息
	@Override
	public List<BGzapplyinfo> findTasksByUserCode(Set<String> keySet, String code) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bGzapplyinfoMapper.findTasksByUserCode(list,code);
	}

	//根据sql语句获取公章申请信息
	@Override
	public List<BGzapplyinfo> getBySql(String result) {
		return bGzapplyinfoMapper.getBySql(result);
	}

	//根据申请人账号获取所有业务信息
	@Override
	public List<BGzapplyinfo> getByAppUser(String code) {
		return bGzapplyinfoMapper.getByAppUser(code);
	}

	//分页查询用印申请信息
	@Override
	public List<BGzapplyinfo> getAll(int start, int number) {
		return bGzapplyinfoMapper.getAll(start,number);
	}

	//获取用印申请总数量
	@Override
	public int getAllCount() {
		return bGzapplyinfoMapper.getAllCount();
	}

	//根据sql语句查询用印申请数量
	@Override
	public int getCountBySQL(String countResult) {
		return bGzapplyinfoMapper.getCountBySQL(countResult);
	}

	//导出表单
	@Override
	public int export(String[] nlist, String filePath) {
		List<BGzapplyinfo> gzapplyList = new ArrayList<BGzapplyinfo>();
		gzapplyList = bGzapplyinfoMapper.findAllPass();// 最终返回的list
		return exportExcel(gzapplyList, filePath);
	}
	private int exportExcel(List<BGzapplyinfo> gzList, String filePath) {
		XSSFWorkbook workbook;
		String sheetName = "用印申请信息";
		try {
			workbook = new XSSFWorkbook();
			// 添加一个sheet,sheet名
			XSSFSheet sheet = workbook.createSheet(sheetName);
			// 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
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
			cell.setCellValue("申请事由");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(5);// 第6列
			cell.setCellValue("申请时间");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(6);// 第7列
			cell.setCellValue("发往单位");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(7);// 第8列
			cell.setCellValue("公章类型");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(8);// 第9列
			cell.setCellValue("用印份数");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(9);// 第10列
			cell.setCellValue("是否涉密");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(10);// 第11列
			cell.setCellValue("审批人");
			cell.setCellStyle(style);			
			
			// 表头完成------------------
			int index = 1;// 行号，应从第三行开始，每次循环进行++
			BGzapplyinfo gz = new BGzapplyinfo();
			// 遍历集合将数据写到excel中
			if (gzList.size() > 0) {
				for (int i = 0; i < gzList.size(); i++) {
					// 行号++，2开始
					index++;
					
					// 对象
					gz = gzList.get(i);
					
					// 得到数据行数
					int hs = gzList.size();
					
					// 生成相应的行数
					XSSFRow row = sheet.createRow(index);
					
					XSSFCell rowCell = row.createCell(0);// 第1列(序号)
					rowCell.setCellValue(i + 1 + "");
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(1);// 第2列(公章-审批单号)
					rowCell.setCellValue(gz.getApprovalid());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(2);// 第3列(公章-部门)
					rowCell.setCellValue(gz.getDepartment());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(3);// 第4列(公章-申请人)
					rowCell.setCellValue(gz.getApplyusername());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(4);// 第5列(公章-申请事由)
					rowCell.setCellValue(gz.getReason());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(5);// 第6列(公章-申请时间)
					rowCell.setCellValue(gz.getApplytime());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(6);// 第7列(公章-发往单位)
					rowCell.setCellValue(gz.getSendto());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(7);// 第8列(公章-公章类型)
					rowCell.setCellValue(gz.getGzkind());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(8);// 第9列(公章-用印分数)
					rowCell.setCellValue(gz.getCopies());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(9);// 第10列(公章-是否涉密)
					rowCell.setCellValue(gz.getIssecret());
					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(10);// 第11列(公章-审批人)
					rowCell.setCellValue(gz.getApproveman());
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

	//查询待审批或者已经通过的业务信息
	@Override
	public List<BGzapplyinfo> findTasksByStatuses(Set<String> keySet, String status1, String status2) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bGzapplyinfoMapper.findTasksByStatuses(list,status1,status2);
	}

	//根据流程审批人以及状态获取业务信息
	@Override
	public List<BGzapplyinfo> findApprovalAndLable(String code, int i) {
		return bGzapplyinfoMapper.findApprovalAndLable(code,i);
	}

	//根据流程业务主管审批人以及状态获取业务信息
	@Override
	public List<BGzapplyinfo> findBussinessAndLable(String code, int i) {
		return bGzapplyinfoMapper.findBussinessAndLable(code,i);
	}

	//根据流程确认人审批人以及状态获取业务信息
	@Override
	public List<BGzapplyinfo> findConfirmAndLable(String code, int i) {
		return bGzapplyinfoMapper.findConfirmAndLable(code,i);
	}

	//获取所有业务信息
	@Override
	public List<BGzapplyinfo> getAllInfo() {
		return bGzapplyinfoMapper.getAllInfo();
	}

	@Override
	public List<BGzapplyinfo> findTasksLikeStatuses(Set<String> keySet, String status) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		return bGzapplyinfoMapper.findTasksLikeStatuses(list,status);
	}

	//根据审批人账号获取审批信息
	@Override
	public Object selectNum(String code) {
		return bGzapplyinfoMapper.selectNum(code);
	}

	//获取所有发往单位
	@Override
	public List<String> getAllSendUnit(String companyName) {
		return bGzapplyinfoMapper.getAllSendUnit(companyName);
	}

	//获取所有发往单位
	@Override
	public Set<String> getAllSendTo() {
		return bGzapplyinfoMapper.getAllSendTo();
	}

	//条件查询用印申请信息
	@Override
	public List<BGzapplyinfo> findTasksByCase(Set<String> keySet, String status, BGzapplyinfo gzapplyinfo) {
		List<String> list = new ArrayList<>();
		list.addAll(keySet);//将set转化为list集合
		list.add("");//为了避免使用sql查询的时候in中有多余逗号
		gzapplyinfo.setStatus(status);
		gzapplyinfo.setList(list);
		return bGzapplyinfoMapper.findTasksByCase(gzapplyinfo);
	}

	//根据申请人获取完成的信息
	@Override
	public List<BGzapplyinfo> getFinishByAppUser(String code) {
		return bGzapplyinfoMapper.getFinishByAppUser(code);
	}

	//导数据
	@Override
	public List<BGzapplyinfo> getAllPass() {
		return bGzapplyinfoMapper.getAllPass();
	}

	//获取所有进行中的业务申请信息
	@Override
	public List<BGzapplyinfo> getAllRunTime(int start, int number) {
		return bGzapplyinfoMapper.getAllRunTime(start,number);
	}

	//获取进行中的业务数量
	@Override
	public int getAllRunTimeCount() {
		return bGzapplyinfoMapper.getAllRunTimeCount();
	}

	//导出进行中的用印申请信息
	@Override
	public int exportRunTime(String[] nlist, String filePath) {
		List<BGzapplyinfo> gzapplyList = new ArrayList<BGzapplyinfo>();
		gzapplyList = bGzapplyinfoMapper.getAllRunTimeInfo();// 最终返回的list
		return exportExcel(gzapplyList, filePath);
	}
}
