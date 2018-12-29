<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>待审核事后登记列表</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/entertainImg/favicon.ico">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/icon.css">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/jquery.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/easyui-lang-zh_CN.js"></script>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/PublicStyle.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/DataImport.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/main.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/UserManage.css">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/jquery.form.js"></script>
<script type="text/javascript">
	var es = "新建";
	//提交action 的url
	var url = "";
	var idd = 1;
	//返回json对象的长度
	function getJsonObjLength(jsonObj) {
		var Length = 0;
		for ( var item in jsonObj) {
			Length++;
		}
		return Length;
	}

	//格式化json
	function formatJson(json) {
		var jsobj = eval(json);
		return '{"total":' + jsobj.bingdings.length + ',"rows":' + json + "}";
	}


	function search() {
		$('#searchUser').dialog('open').dialog('setTitle', '查询');

	}
	
/* 	function approve(){
		$('#dlg').dialog('open').dialog('setTitle', '审核');
	} */
	
	$(function() {
		//$(param.llbid) 传递过来得履历本id
		//汉化 datagrid 翻页
		$("#llbinfo").datagrid({
			url : '${pageContext.request.contextPath}/entertain/wRGetAllEntertain',
			method : 'post',
			pageSize : 20,
			rownumbers : true,
			singleSelect : true,
			showFooter:true, // 增添脚注行
			pagination : true,
			toolbar : [ /* {
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					search();
				}

			} */],
			
			onLoadSuccess:function(data){  
				 $('.preScan').linkbutton({text:'审核',plain:true,iconCls:'icon-search'}); 
			}
		});

		var pager = $('#llbinfo').datagrid().datagrid('getPager');
		pager.pagination({
			beforePageText : "转到",
			afterPageText : "共{pages}页",
			displayMsg : '当前显示从{from}到{to}  共{total}记录',
			onBeforeRefresh : function(pageNumber, pageSize) {
				$(this).pagination('loading');

				$(this).pagination('loaded');
			}
		});

		// 审核通过按钮--只是将状态改为通过
		$("#btn_ok").click(
			function() {
			var row = $('#llbinfo').datagrid('getSelected');
			var number = row.number;
			var taskId = row.taskId;
			var param="number="+number+"&taskId="+taskId;
				$.post(
						"${pageContext.request.contextPath}/entertain/approveX",
						param, function(result) {
									$("#dlg").dialog("close");
									alert("事后登记已通过审核！");
									//window.location.reload(); 
									$("#llbinfo").datagrid({
										url : '${pageContext.request.contextPath}/entertain/wRGetAllEntertain',
										method : 'post',
										pageSize : 20,
										rownumbers : true,
										singleSelect : true,
										showFooter:true, // 增添脚注行
										pagination : true,
										toolbar : [ /* {
											text : '查询',
											iconCls : 'icon-search',
											handler : function() {
												search();
											}

										} */],
										
										onLoadSuccess:function(data){  
											 $('.preScan').linkbutton({text:'审核',plain:true,iconCls:'icon-search'}); 
										}
									});
						});
			
		});
		// 驳回事后登记
		$("#btn_cancel").click(function() {
			var row = $('#llbinfo').datagrid('getSelected');
			var uid = row.number;
			var taskId = row.taskId;
			var param = "number="+uid+"&taskId="+taskId;
			$.post(
					"${pageContext.request.contextPath}/entertain/notThrough",
					param, function(result) {
								$("#dlg").dialog("close");
								console.info(result);
								alert("事后登记未通过审核！");
								$("#llbinfo").datagrid({
									url : '${pageContext.request.contextPath}/entertain/wRGetAllEntertain',
									method : 'post',
									pageSize : 20,
									rownumbers : true,
									singleSelect : true,
									showFooter:true, // 增添脚注行
									pagination : true,
									toolbar : [ /* {
										text : '查询',
										iconCls : 'icon-search',
										handler : function() {
											search();
										}

									} */],
									
									onLoadSuccess:function(data){  
										 $('.preScan').linkbutton({text:'审核',plain:true,iconCls:'icon-search'}); 
									}
								});
							});
			
		});
		// 上传取消按钮
		$("#uploadFileCancel").click(function() {
			document.getElementById('fileImport').value = null;
			$('#import-excel').window('close')
		});
		
		//查询用户
		$("#search_ok").click(
						function() {
							var param = "department="+ $("adepartment").textbox("getText").trim()
									+ "&manager="+ $("#amanager").textbox("getText").trim()
									+ "&startTime=" + $("#astarttime").textbox("getText")
									+ "&endTime="+ $("#aendtime").textbox("getText")
									+ "&entertainObject="+ $("#aentertainobject").textbox("getText").trim();
							$.post(
									"${pageContext.request.contextPath}/entertain/searchApply",
									param, function(result) {
										$("#searchUser").dialog("close");
												$("#llbinfo").datagrid("loadData", result);
											});
						});

		$("#search_cancel").click(function() {
			$("#searchUser").dialog("close");

		});

	});
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	
	// 操作列
	function operation(value,row,rowIndex){
		//console.info(row.id);
		return '<a class="preScan" href="#" iconCls="icon-search" onclick="approve('+ row.id +','+rowIndex+')">查看明细</a>';
	}


	
	//增添事后表单
	function approve(id,index){
		var now = new Date();
	    var year = now.getFullYear();       //年
	    var month = now.getMonth() + 1;     //月
	    var day = now.getDate();            //日
		var clock = year + "-";
	    var cmonth="";
	    var cday="";
        
        if(month < 10){
        	cmonth="0"+month;
            clock += month+ "-";}//保持不变
        else{cmonth=month;
        clock += month + "-";}
        
        //if(day < 10)
            //clock += "0";
            
        clock += day;
        
		var row = $('#llbinfo').datagrid('getData').rows[index];
		$('#dlg').dialog('open').dialog('setTitle', '审核单号'+row.number);
		$("#upaidtime").textbox("setText", clock);
		$("#uvouchernum1").textbox("setValue", year);
		$("#uvouchernum2").textbox("setValue", cmonth);
		$("#studentInfo").datagrid({
			url : '${pageContext.request.contextPath}/entertain/registerDetail?number='+row.number+'&taskId='+row.taskId,
			method : 'post',
			rownumbers : true,
			singleSelect : true,});
	}
	
</script>
</head>
<body>
	<div class="mdiv" style="width:100%;height:94%;">
		<table id="llbinfo" class="easyui-datagrid" title="待审核事后登记列表"
			style="width: auto; height:100%;">
			<thead>
				<tr>
				 	
					<th
						data-options="field:'number',width:fixWidth(0.06),align:'center'">审批单号</th>
					<th 
						data-options="field:'department',width:fixWidth(0.06),align:'center'">部门</th>
					<th
						data-options="field:'manager',width:fixWidth(0.06),align:'center'">经办人</th>
					<th
						data-options="field:'approver',width:fixWidth(0.06),align:'center'">审批人</th>
					<th 
						data-options="field:'entertainObject',width:fixWidth(0.18),align:'center'">招待对象</th>
					<th
						data-options="field:'entertainCategory',width:fixWidth(0.05),align:'center'">招待类别</th>
					<th 
						data-options="field:'entertainReason',width:fixWidth(0.1),align:'center'">招待事由</th> 
					<th 
						data-options="field:'entertainNum',width:fixWidth(0.04),align:'center'">招待人数</th>
					<th 
						data-options="field:'accompanyNum',width:fixWidth(0.04),align:'center'">陪同人数</th>
					<th 
						data-options="field:'wineType',width:fixWidth(0.1),align:'center'">酒水</th>
					<th
						data-options="field:'masterBudget',width:fixWidth(0.04),align:'center'">总预算</th>
					<th
						data-options="field:'applyTime',width:fixWidth(0.06),align:'center'">申请时间</th>
					<th
						data-options="field:'beforeDate',width:fixWidth(0.06),align:'center'">实际申请时间</th>
						<th
						data-options="field:'taskId',width:fixWidth(0.06),hidden:'true'">任务id</th>
					<th
						data-options="field:'operation',width:fixWidth(0.08),formatter:operation,align:'center'">操作</th>
				</tr>
			</thead>
		</table>  
	</div>
	<!-- 审核登记对话框 -->
	<div id="dlg" class="easyui-dialog"
		style="width: 90%; height: 350px; padding: 0px 0px" closed="true"
		buttons="#dlg-buttons">
		<!--<div>用户编辑</div>  -->
		<!-- <form id="fm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>审批单号</label></td>
						<td><input id="unumber" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>发票开具日期</label></td>
						<td><input id="uinvoicedate" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>开票内容</label></td>
						<td><input id="uinvoicecontent" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>发票金额</label></td>
						<td><input id="uinvoicesum" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>发票张数</label></td>
						<td><input id="uinvoicenum" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>发票开具单位</label></td>
						<td><input id="uinvoiceunit" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>报销时间</label></td>
						<td><input id="upaidtime" class="easyui-textbox" style="width: 160px;"></input></td>
					</tr>
					<tr>
						<td><label>凭证号</label></td>
						<td><input id="uvouchernum1" class="easyui-textbox" style="width: 40px;"></input>年
						<input id="uvouchernum2" class="easyui-textbox" style="width: 25px;"></input>月
						&nbsp;第<input id="uvouchernum3" class="easyui-textbox" style="width: 40px;"></input>号</td>
					</tr>
					<tr>
						<td><label>备注</label></td>
						<td><input id="uremark" class="easyui-textbox" disabled="disabled" style="width: 160px;"></input></td>
					</tr>
				</table>
			</div> -->
			<table id="studentInfo" class="easyui-datagrid" title="事后登记表单"
			style="width: 100%; height: 230px;">
			<thead>
				<tr>
					<th
						data-options="field:'number',width:fixWidth(0.08),align:'center'">审批单号</th> 
					<th 
						data-options="field:'invoiceDate',width:fixWidth(0.1),align:'center'">发票开具日期</th>
					<th
						data-options="field:'invoiceContent',width:fixWidth(0.06),align:'center'">开票内容</th>
					<th 
						data-options="field:'invoiceNumber',width:fixWidth(0.1),align:'center'">发票号</th>
					<th 
						data-options="field:'invoiceSum',width:fixWidth(0.05),align:'center'">发票金额</th>
					<th 
						data-options="field:'invoiceNum',width:fixWidth(0.05),align:'center'">发票张数</th>
					<th 
						data-options="field:'invoiceUnit',width:fixWidth(0.2),align:'center'">发票开具单位</th>
					<th 
						data-options="field:'wineSum',width:fixWidth(0.05),align:'center'">酒水金额</th>
					<th 
						data-options="field:'personSum',width:fixWidth(0.05),align:'center'">人均招待费用</th>
					<th 
						data-options="field:'enterSum',width:fixWidth(0.05),align:'center'">招待总费用</th>
					<!-- <th
						data-options="field:'paidTime',width:fixWidth(0.1),align:'center'">报销时间</th> -->
					<th
						data-options="field:'remark',width:fixWidth(0.1),align:'center'">备注</th>
				</tr>
			</thead>
		</table>
		<br>
			<div style="text-align: center; margin-top: 20px;">
				<a href="#" id="btn_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">通过</a> 
				<a href="#" id="btn_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">驳回</a>
			</div>
	</div>
<!-- 查询信息对话框 -->
	<div id="searchUser" class="easyui-dialog"
		style="width: 400px; height: 320px; padding: 10px 20px" closed="true">
		<form id="sfm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>审批单号</label></td>
						<td><input id="uunumber" class="easyui-textbox"></input></td>
					</tr>
					<tr>
						<td><label>发票开具日期</label></td>
						<td><input id="uuinvoicedate" class="easyui-textbox"></input></td>
					</tr>
					<tr>
						<td><label>开票内容</label></td>
						<td><input id="astarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'"></input></td>
					</tr>
					<tr>
						<td><label>发票金额</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'"></input></td>
					</tr>
					<tr>
						<td><label>招待对象</label></td>
						<td><input id="aentertainobject" class="easyui-textbox"></input></td>
					</tr>

				</table>
				<div id="cc" class="easyui-calendar"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="search_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 20%;">查询</a> <a
					href="#" id="search_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
			</div>
		</form>
	</div>
</body>
</html>