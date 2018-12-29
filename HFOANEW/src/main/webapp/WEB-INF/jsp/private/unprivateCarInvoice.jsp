<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>待审核招待明细列表</title>
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

	// 给查询框赋初值
	function search() {
	 	var now = new Date();
        var nowd = new Date().Format("yyyy-MM-dd");

        var lastmonth = new Date;
        lastmonth.setDate(now.getDate() - 30);
        var ldate = lastmonth.Format("yyyy-MM-dd");
        
		$('#searchUser').dialog('open').dialog('setTitle', '查询');
		$("#sdepartment").combobox("setText", "全部");
		$("#sstarttime").textbox("setText", ldate);
		$("#sendtime").textbox("setText", nowd);
	}
	// js格式化Date方法
	
	Date.prototype.Format = function (fmt) { //author: meizz 
	    var o = { 
	        "M+": this.getMonth() + 1, //月份 
	        "d+": this.getDate(), //日 
	        "h+": this.getHours(), //小时 
	        "m+": this.getMinutes(), //分 
	        "s+": this.getSeconds(), //秒 
	        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
	        "S": this.getMilliseconds() //毫秒 
	    };
	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	    for (var k in o)
	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	    return fmt;
	}
	
	//重写js方法
	/*
	Date.prototype.Format = function () {
		return this.getFullYear()+"-"+parseInt(this.getMonth()+1)+"-"+this.getDate();
	}
	*/
	
	//datagrid-save
	function save() {
		$('#fm').form('submit', {
			url : url,
			onSubmit : function() {
				return $(this).form('validate');
			},
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#dlg').dialog('close'); // close the dialog
					$('#llbinfo').datagrid('reload'); // reload the user data
					$.messager.show({
						title : 'Message',
						msg : '添加成功'
					});
				} else {
					$.messager.show({
						title : 'Error',
						msg : result.msg
					});
				}
			}
		});
	}
	
	$(function() {
		//$(param.llbid) 传递过来得履历本id
		//汉化 datagrid 翻页
		$("#llbinfo").datagrid({
			url : '${pageContext.request.contextPath}/privateCar/uninvoiceDisplay',
			method : 'post',
			pageSize : 20,
			rownumbers : true,
			singleSelect : true,
			pagination : true,
// 			toolbar : [ {
// 				text : '查询',
// 				iconCls : 'icon-search',
// 				handler : function() {
// 					search();
// 				}

// 			}, '-', {
// 				text : '导入数据',
// 				iconCls : 'icon-llb2',
// 				handler : function() {
// 					importExcel();
// 				}
// 			}, '-', {
// 				text : '导出表单',
// 				iconCls : 'icon-llb2',
// 				handler : function() {
// 					exportExcel();
// 				}
// 			}],
			
			onLoadSuccess:function(data){  
// 				 $('.preScan').linkbutton({text:'查看明细',plain:true,iconCls:'icon-search'}); 
				 $('.preScan1').linkbutton({text:'凭证号登记',plain:true,iconCls:'icon-save'}); 
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

		// 确认按钮
		$("#btn_ok").click(
			function() {
				var row = $('#llbinfo').datagrid('getSelected');
				console.log(row.applyId);
				var rows = $("#studentInfo").datagrid("getRows");
// 				console.log(rows);
				var ids = "";
				var taskId = "";
				for(var i=0;i<rows.length;i++){
					if(i==rows.length-1){
						ids+=rows[i].applyId;
						taskId+=rows[i].taskId;
					}else{
						ids+=rows[i].applyId+",";
						taskId+=rows[i].taskId+",";
					}
				}
				$.post(
						"${pageContext.request.contextPath}/privateCar/passPrivateCars",
						{applyid:row.applyId,applyids:ids,taskId:taskId}, function(result) {
							alert("审核通过！");
							$("#dlg").dialog("close");
							$('#llbinfo').datagrid('reload');
								});
			});
		// 取消按钮
		$("#btn_cancel").click(function() {
			$('#dlg').dialog('close')
		});
		// 上传取消按钮
		$("#uploadFileCancel").click(function() {
			document.getElementById('fileImport').value = null;
			$('#import-excel').window('close')
		});
		
		//查询用户
		$("#search_ok").click(
						function() {
							var param = "department="+ $("#sdepartment").combobox("getText")
									+ "&applyman="+ $("#sapplyman").textbox("getText").trim()
									+ "&startTime=" + $("#sstarttime").textbox("getText")
									+ "&endTime="+ $("#sendtime").textbox("getText");
							//alert(param);
							$.post(
									"${pageContext.request.contextPath}/PrivateCar/searchInfo",
									param, function(result) {
										$("#searchUser").dialog("close");
												$("#llbinfo").datagrid("loadData", result);
											});
						});

		$("#search_cancel").click(function() {
			$("#searchUser").dialog("close");

		});
		// 审核通过按钮
		/* $("#btn_ok").click(
			function() {
							var row = $('#llbinfo').datagrid('getSelected');
							var number = row.number;
							
							var paidTime = $("#upaidtime").textbox("getText").trim();
							var month = $("#uvouchernum2").textbox("getValue").trim();
							var num = $("#uvouchernum3").textbox("getText").trim();
							var year = $("#uvouchernum1").textbox("getValue").trim();
							
							
							
							var voucher = year+month+num;
							var pstvoucher = year+"年"+month+"月 "+"第"+num+"号";
							if(voucher.length==10){
								//alert("success");
								var param = "number="+ number + "&paidTime="+ paidTime +"&voucherNum="+ pstvoucher;		
								$.post(
										"${pageContext.request.contextPath}/register/insertAllVoucherNum",
										param, function(result) {
													$("#dlg").dialog("close");
													alert("事后登记已通过审核！");
													//window.location.reload(); 
													$("#llbinfo").datagrid({
														url : '${pageContext.request.contextPath}/entertain/wGetAllApproved',
														method : 'post',
														pageSize : 20,
														rownumbers : true,
														singleSelect : true,
														pagination : true,
														toolbar : [ {
															text : '查询',
															iconCls : 'icon-search',
															handler : function() {
																search();
															}

														}, '-', {
															text : '导出表单',
															iconCls : 'icon-llb2',
															handler : function() {
																exportExcel();
															}
														}],
														
														onLoadSuccess:function(data){  
															 $('.preScan').linkbutton({text:'查看明细',plain:true,iconCls:'icon-search'}); 
															 $('.preScan1').linkbutton({text:'凭证号登记',plain:true,iconCls:'icon-save'}); 
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
										});
							}
							else{
								alert("凭证号格式不正确！");
							}
						}); */

		//上传按钮

		$("#uploadFileOK").click(function() {
			var importName1=document.getElementById("fileImport").value;
			if(importName1==null){
				alert('错误，请选择文件');
				return;
			}
			var importName=importName1.substring(12, importName1.lastIndexOf("."));//截取不带后缀的文件名
			//获取文件类型名称
			var file_typename = importName1.substring(importName1.lastIndexOf('.'), importName1.length);
			if(file_typename == '.xlsx'){
				var options = {  
					url:"${pageContext.request.contextPath}/privateCar/importPrivateCar",
					dataType:'text',
					success: function(result){
						if(result=="2"){
							document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>"
								+ "上传文件大于100M，禁止上传"
								+ "</span>";
						}else if(result=="0"){
							document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>" + "上传失败" + "</span>";
						}else{
							$.messager.show({
								title : 'Message',
								msg : '上传成功'
							});
							//上传成功后将控件内容清空，并显示上传成功信息
							document.getElementById('fileImport').value = null;
							var file = document.getElementById("fileImport");
							file.outerHTML = file.outerHTML;
							document.getElementById('uploadInfo').innerHTML = "";
							$('#import-excel').window('close'); // close the dialog
							//重新加载数据
							$('#llbinfo').datagrid('reload');
						}
					},
					error : function(returnInfo) {
						//上传失败时显示上传失败信息
						document.getElementById('uploadInfo').innerHTML = "<span style='color:Red'>"+ "导入失败" + "</span>";},
					dataType:  'text'   
				}; 
				
				$('#importFileForm').ajaxSubmit(options);
					
					//重新加载数据
					$('#llbinfo').datagrid('reload');
			}else{
				alert("文件类型错误,请重新选择文件");
			}
		});
		
	});
	
	//导出表单
	function exportExcel(){

	        var rows = $('#llbinfo').datagrid('getRows'); //获取当前页的数据行  
	        var total = "";  
	        for (var i = 0; i < rows.length; i++) {  
	            total += rows[i]['applyId']+','; //获取指定列  
	        }  
	        //alert(total);
	        /* var param = "number="+total;
	        $.post(
					"${pageContext.request.contextPath}/entertain/exportExcel",
					param, function(result) {
			
							}); */
			window.open('${pageContext.request.contextPath}/PrivateCar/exportExcel?number='+total);
		
		}
	
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	
	// 操作列
	function operation(value,row,rowIndex){
		//console.info(row.id);
		return '<a class="preScan" href="#" iconCls="icon-search" onclick="detail('+ row.id +','+rowIndex+')">查看明细</a>';
	}
	function checkIn(value,row,rowIndex){
			return '<a class="preScan" href="#" iconCls="icon-search" onclick="check('+ row.applyId +','+rowIndex+')">审核</a>';
		
	}
	function checkOut(value,row,rowIndex){
		return '<a class="preScan" href="#" iconCls="icon-search" onclick="checkBh('+ row.applyId +','+rowIndex+')">驳回</a>';
	}
	function checkBh(id,index){
		var row1 = $('#llbinfo').datagrid('getSelected');
		var row2 = $('#studentInfo').datagrid('getData').rows[index];
		if(confirm('确定要驳回此条记录吗?')){
			$.post(
					"${pageContext.request.contextPath}/privateCar/backPrivateCars",
					{applyid:row2.applyId}, function(result) {
						if(result==1){
							var rows = $("#studentInfo").datagrid("getRows");
							console.log(rows);
							if(rows.length==1){
								$("#dlg").dialog("close");
								$('#llbinfo').datagrid('reload');
							}else{
								$("#studentInfo").datagrid('reload');
							}
						}
			});
		}
	}
	// 显示明细表页面
	function detail(id,index) {
		var row = $('#llbinfo').datagrid('getData').rows[index];
		var playOfpath='${pageContext.request.contextPath}/view/entertain/detail.jsp?number='+row.number;//跳转到明细页面
		window.open(playOfpath,'newwindow','fullscreen=yes,channelmode=yes,resizable=yes,menubar=no,status=no,scrollbars=yes,');
	}
	// 凭证号登记
	function check(id,index) {
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
		$('#dlg').dialog('open').dialog('setTitle', '审核单号'+row.applyId);
		$("#upaidtime").textbox("setText", clock);
		$("#uvouchernum1").textbox("setValue", year);
		$("#uvouchernum2").textbox("setValue", cmonth); 
		
		$("#studentInfo").datagrid({
			url : '${pageContext.request.contextPath}/privateCar/selectByApplyIds?applyid='+row.applyId,
			method : 'post',
			rownumbers : true,
			singleSelect : true,});
	}
	$('#aentertainobject').combobox({
		filter: function(q, row){
			var opts = $(this).combobox('options');
			return row[opts.textField].indexOf(q) == 0;
		}
	});
	//上传文件
	function importExcel() {
		$('#import-excel').dialog('open').dialog('setTitle', '私车公用数据上传');
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
			url : '${pageContext.request.contextPath}/entertain/registerDetail?number='+row.number,
			method : 'post',
			rownumbers : true,
			singleSelect : true,});
	}
</script>
</head>
<body>	<!-- 审核登记对话框 -->
	<div class="mdiv" style="width:100%;height:94%;">
		<table id="llbinfo" class="easyui-datagrid" title="未报销记录"
			style="width: auto; height:100%;">
			<thead>
				<tr>
				 	<!-- <th
						data-options="field:'applyId',width:fixWidth(0.1),align:'center'">审批单号</th> -->
					<th
						data-options="field:'applyTime',width:fixWidth(0.2),align:'center'">提交时间</th>
					<th 
						data-options="field:'applyMan',width:fixWidth(0.2),align:'center'">申请人</th>
					<th 
						data-options="field:'sum',width:fixWidth(0.2),align:'center'">金额</th>
					<th
						data-options="field:'status',width:fixWidth(0.2),align:'center'">状态</th>
					<!-- <th 
						data-options="field:'paidTime',width:fixWidth(0.08),align:'center'">审批时间</th> 
					<th
						data-options="field:'approveMan',width:fixWidth(0.08),align:'center'">审批人</th>
					<th
						data-options="field:'voucherNum',width:fixWidth(0.08),align:'center'">凭单号</th> -->
					<th
						data-options="field:'applyId',width:fixWidth(0.2),formatter:checkIn,align:'center'">操作</th>
				</tr>
			</thead>
		</table>  
	</div>
<!-- 查询信息对话框 -->
	<div id="searchUser" class="easyui-dialog"
		style="width: 400px; height: 320px; padding: 10px 20px" closed="true">
		<form id="sfm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>部门</label></td>
						<td><input id="sdepartment" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/entertain/getDepartment',
							method:'get',
							valueField:'id',
							textField:'departmentName',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>申请人</label></td>
						<td><input id="sapplyman" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>起始时间</label></td>
						<td><input id="sstarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>结束时间</label></td>
						<td><input id="sendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>

				</table>
				<div id="cc" class="easyui-calendar"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="search_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" >查询</a> <a
					href="#" id="search_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" >取消</a>
			</div>
		</form>
	</div>
	<!-- 上传excel文档 -->
	<div id="import-excel" class="easyui-window" title="私车公用数据上传"
		style="width: 380px; height: 190px; padding: 2px;" closed="true">
		<form action="" id="importFileForm" method="post"
			enctype="multipart/form-data">
			
			<table cellPadding="0"
				style="margin-left: 20px; margin-top: 20px; margin-right: auto;">
				<tr>
					<td>请选择xlsx格式的文件</td>
					<td width="10px;"></td>
					<!-- </tr>
				<tr> -->
					<td><input type="file" id="fileImport" name="fileImport"
						style="width: 260px;"></td>
				</tr>
				<tr>
					<td colspan="4"><label id="fileName" /></td>
				</tr>
				<tr>
					<td colspan="4"><label id="uploadInfo" /></td>
				</tr>
			</table>
			<div style="text-align: center; clear: both; margin: 5px;">
				<a id="uploadFileOK" class="easyui-linkbutton"
				data-options="iconCls:'icon-ok'" data-bind="click:importFileClick"
				href="#">上传</a> &nbsp;&nbsp;&nbsp;&nbsp;
				<a id="uploadFileCancel" class="easyui-linkbutton"
				data-options="iconCls:'icon-cancel'"
				data-bind="click:closeImportClick" href="#">关闭</a>
			</div>
		</form>
	</div>
	<div id="dlg" class="easyui-dialog"
		style="width: 60%; height: 350px; padding: 0px 0px" closed="true"
		buttons="#dlg-buttons">
			<table id="studentInfo" class="easyui-datagrid" title="私车使用记录"
			style="width: 100%; height: 230px;">
			<thead>
				<tr>
					<th 
						data-options="field:'userCarTime',width:fixWidth(0.06),align:'center'">用车时间</th>
					<th 
						data-options="field:'beforeDate',width:fixWidth(0.06),align:'center'">实际用车时间</th>
					<th 
						data-options="field:'reason',width:fixWidth(0.12),align:'center'">事由</th>
					<th 
						data-options="field:'beginAddress',width:fixWidth(0.1),align:'center'">出发地</th>
					<th 
						data-options="field:'destination',width:fixWidth(0.1),align:'center'">目的地</th>
					<th 
						data-options="field:'sureLength',width:fixWidth(0.05),align:'center'">核定价格</th>
					<th 
						data-options="field:'ifPass',width:fixWidth(0.04),align:'center'">状态</th>
					<th
						data-options="field:'taskId',width:fixWidth(0.04),hidden:'true'">
																						任务Id</th>
					<th 
						data-options="field:'applyId',width:fixWidth(0.05),formatter:checkOut,align:'center'">操作</th>
				</tr>
			</thead>
		</table>
		<br>
			<div style="text-align: center; margin-top: 20px;">
				<a href="#" id="btn_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">通过</a> 
			</div>
	</div>
</body>
</html>