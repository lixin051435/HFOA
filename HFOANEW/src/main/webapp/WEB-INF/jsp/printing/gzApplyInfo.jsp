<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用印申请列表</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/entertainImg/favicon.ico">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/icon.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/demo/demo.css">
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
		$("#department").combobox("setText", "全部");
		$("#sgztype").combobox("setText", "全部");
		$("#sstatus").val("全部");
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
		//获取所有数据
		loadGrid('${pageContext.request.contextPath}/print/getAllRunTimeApplyInfo',null);
		// 确认按钮
		$("#btn_ok").click(
			function() {});
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
				if("全部"==$("#sgztype").combobox("getText")){
					$("#sgztype").combobox("setValue",0);
				}
				var param = {department:$("#department").combobox("getText"),
						applyusername:$("#smanager").textbox("getText").trim(),
							 starttime:$("#sstarttime").textbox("getText"),
							 endtime:$("#sendtime").textbox("getText"),
							 status:$("#sstatus").val(),
							 gzId:$("#sgztype").combobox("getValue"),
							 gzkind:$("#sgztype").combobox("getText")};
				$("#searchUser").dialog("close");
				loadGrid('${pageContext.request.contextPath}/print/displayRunTimeSearch',param);
			});
		$("#search_cancel").click(function() {
			$("#searchUser").dialog("close");

		});

	});
	
	function loadGrid(url,data){
		//汉化 datagrid 翻页
		$("#llbinfo").datagrid({
			url : url,
			method : 'post',
			queryParams:data,
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
	}
	//导出表单
	function exportExcel(){
        var rows = $('#llbinfo').datagrid('getRows'); //获取当前页的数据行  
        var total = "";  
        for (var i = 0; i < rows.length; i++) {  
            total += rows[i]['id']+','; //获取指定列  
        }  
		window.open('${pageContext.request.contextPath}/print/exportRunTimeExcel?number='+total);
	}
	
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	
</script>
</head>
<body>
	<div class="mdiv" style="width:100%;height:94%;">
		<table id="llbinfo" class="easyui-datagrid" title="用印申请信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
				 	<th
						data-options="field:'approvalid',width:fixWidth(0.15),align:'center'">审批单号</th>
					<th 
						data-options="field:'gzkind',width:fixWidth(0.15),align:'center'">公章类型</th> 
					<th
						data-options="field:'applyusername',width:fixWidth(0.1),align:'center'">申请人</th>
					<th 
						data-options="field:'department',width:fixWidth(0.2),align:'center'">部门</th>
					<th 
						data-options="field:'applytime',width:fixWidth(0.15),align:'center'">申请时间</th>
					<th 
						data-options="field:'status',width:fixWidth(0.1),align:'center'">审批状态</th>
					<th
						data-options="field:'reason',width:fixWidth(0.2),align:'center'">申请事由</th>
					<th
						data-options="field:'sendto',width:fixWidth(0.2),align:'center'">发往单位</th>
					<th 
						data-options="field:'copies',width:fixWidth(0.1),align:'center'">用印份数</th>
					<th 
						data-options="field:'issecret',width:fixWidth(0.1),align:'center'">是否涉密</th>
					<th
						data-options="field:'approveman',width:fixWidth(0.1),align:'center'">审批人</th>
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
						<td><input id="department" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/department/getAllDepartment',
							method:'get',
							valueField:'id',
							textField:'departmentname',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>申请人</label></td>
						<td><input id="smanager" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>公章类型</label></td>
						<td><input id="sgztype" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/print/getPrintingType',
							method:'get',
							valueField:'id',
							textField:'gzkind',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>审批状态</label></td>
						<td><select id="sstatus" style="width: 220px;">
						<option value="全部">全部</option>
						<option value="待审批">待审批</option>
						<option value="否决">被否决</option>
						<option value="通过">已通过</option>
						</select></td>
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
					data-options="iconCls:'icon-search'" style="width: 20%;">查询</a> <a
					href="#" id="search_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
			</div>
		</form>
	</div>
</body>
</html>