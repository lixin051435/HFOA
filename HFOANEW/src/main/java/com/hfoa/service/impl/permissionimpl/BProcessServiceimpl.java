package com.hfoa.service.impl.permissionimpl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.hfoa.dao.permission.BProcessMapper;
import com.hfoa.entity.permission.BPermissionEntity;
import com.hfoa.entity.permission.BProcess;
import com.hfoa.entity.printing.BGzapplyinfo;
import com.hfoa.service.permission.BProcessService;
/**
 * 
 * @author wzx
 * 流程service实现类
 */
@Service
public class BProcessServiceimpl implements BProcessService{

	@Autowired
	private BProcessMapper bProcessMapper;

	//分页查询流程信息
	@Override
	public List<BProcess> processDisplayByPage(int start, int number) {
		return bProcessMapper.processDisplayByPage(start,number);
	}

	//获取流程信息总数量
	@Override
	public int getAllCount() {
		return bProcessMapper.getAllCount();
	}

	//获取流程模块
	@Override
	public List<BProcess> getByParentId(int id) {
		return bProcessMapper.getByParentId(id);
	}

	//根据sql语句查询流程信息
	@Override
	public List<BProcess> getBySql(String result) {
		return bProcessMapper.getBySql(result);
	}

	//根据sql语句查询流程信息数量
	@Override
	public int getCountBysql(String countResult) {
		return bProcessMapper.getCountBysql(countResult);
	}

	//导出表单
	@Override
	public int export(String[] nlist, String filePath) {
		List<BProcess> process = new ArrayList<BProcess>();
		process = bProcessMapper.findAllInfo();// 最终返回的list
		return exportExcel(process, filePath);
	}
	private int exportExcel(List<BProcess> process, String filePath) {
		XSSFWorkbook workbook;
		String sheetName = "流程信息";
		try {
			workbook = new XSSFWorkbook();
			// 添加一个sheet,sheet名
			XSSFSheet sheet = workbook.createSheet(sheetName);
			// 合并单元格 参数意思是 第一行、最后一行、第一列、最后一列
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
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
//			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			// 设置其他正文单元格格式
			XSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.CENTER);
			// 设置第二行表头
			XSSFRow rowHeader = sheet.createRow(1);
			XSSFCell cell = rowHeader.createCell(0);// 第1列
			cell.setCellValue("编号");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(1);// 第2列
			cell.setCellValue("流程节点");
			cell.setCellStyle(style);
			cell = rowHeader.createCell(2);// 第3列
			cell.setCellValue("流程路径");
			cell.setCellStyle(style);
			
			// 表头完成------------------
			int index = 1;// 行号，应从第三行开始，每次循环进行++
			BProcess p = new BProcess();
			// 遍历集合将数据写到excel中
			if (process.size() > 0) {
				for (int i = 0; i < process.size(); i++) {
					// 行号++，2开始
					index++;
					
					// 对象
					p = process.get(i);
					
					// 得到数据行数
					int hs = process.size();
					
					// 生成相应的行数
					XSSFRow row = sheet.createRow(index);
					
					XSSFCell rowCell = row.createCell(0);// 第1列(序号)
//					rowCell.setCellValue(i + 1 + "");
//					rowCell.setCellStyle(style);
					
					rowCell = row.createCell(0);// 第2列(id)
					rowCell.setCellValue(p.getId());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(1);// 第3列(节点)
					rowCell.setCellValue(p.getProcessname());
					rowCell.setCellStyle(style);

					rowCell = row.createCell(2);// 第4列(路径)
					rowCell.setCellValue(p.getProcessurl());
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

	@Override
	public Object getByNodeType(String parentId) {
		List<BProcess> list=bProcessMapper.getByNodeType(Integer.parseInt(parentId));
		List<BProcess> listDTO=new ArrayList<BProcess>();
		for(int i=0;i<list.size();i++){
			BProcess process=list.get(i);
			if(getChildsprocessByRootId(process.getParentid()).size()>0){
				process.setState("closed");
			}else{
				process.setState("open");
			}
			process.setText(process.getProcessname());
			listDTO.add(process);
		}
		return listDTO;
	}
	//获取子权限
	public List<BProcess> getChildsprocessByRootId(int id) {
		return bProcessMapper.getByNodeType(id);
	}

	//根据角色id获取流程id
	@Override
	public List<Integer> getProcessByRole(int roleId) {
		return bProcessMapper.getProcessByRole(roleId);
	}

	//删除角色流程中间表角色id相同信息
	@Override
	public void deleteMiddleRole(int id) {
		bProcessMapper.deleteMiddleRole(id);
	}

	//根据角色id获取流程id集合
	@Override
	public List<Integer> getProcessByRoleId(int roleId) {
		return bProcessMapper.getProcessByRoleId(roleId);
	}

	//根据节点名称获取流程信息
	@Override
	public BProcess getprocessByName(String name) {
		return bProcessMapper.getprocessByName(name);
	}

	//根据id查询流程节点信息
	@Override
	public BProcess selectById(Integer pid) {
		return bProcessMapper.selectById(pid);
	}

	//根据业务节点名称获取所有节点内容
	@Override
	public List<BProcess> getByName(String processName) {
		return bProcessMapper.getByName(processName);
	}

	//根据流程id获取流程内容
	@Override
	public BProcess getById(Integer id) {
		return bProcessMapper.getById(id);
	}
}
