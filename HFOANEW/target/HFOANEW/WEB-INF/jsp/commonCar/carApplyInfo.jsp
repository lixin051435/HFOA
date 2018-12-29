<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>公共车申请业务信息列表</title>
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
		$("#adepartment").combobox("setText", "全部");
		$("#astarttime").textbox("setText", ldate);
		$("#aendtime").textbox("setText", nowd);
		$("#acartype").combobox("setText", "全部");
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
				text : '添加',
				iconCls : 'icon-add',
				handler : function() {
					add();
				}
			}, '-', {
				text : '修改',
				iconCls : 'icon-edit',
				handler : function() {
					edit();
				}
			}, '-', {
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					deleteApplyInfo();
				}
			}, '-', {
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					search();
				}
			} , '-', {
				text : '导出表单',
				iconCls : 'icon-llb2',
				handler : function() {
					exportExcel();
			}}]
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
	$(function() {
		//加载所有进行中业务信息
		loadGrid('${pageContext.request.contextPath}/applyCar/getAllRunTimeInfo',null);
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
		
		//查询公车申请信息
		$("#search_ok").click(
		function() {
			var info = $("#acartype").combobox("getText");
			var param = "";
			if(info=="全部"){
				param = {department:$("#adepartment").combobox("getText"),
						applyman:$("#amanager").textbox("getText").trim(),
						starttime:$("#astarttime").textbox("getText"),
						endtime:$("#aendtime").textbox("getText"),
						carinfo:$("#acartype").combobox("getText")};
			}else{
				var carinfo = info.split("   ");
				param = {department:$("#adepartment").combobox("getText"),
						applyman:$("#amanager").textbox("getText").trim(),
						starttime:$("#astarttime").textbox("getText"),
						endtime:$("#aendtime").textbox("getText"),
						carinfo:carinfo[0]};
			}
			$("#searchUser").dialog("close");
			loadGrid('${pageContext.request.contextPath}/applyCar/searchCommonCarInfo',param);
		});
		$("#search_cancel").click(function() {
			$("#searchUser").dialog("close");

		});
	//导出表单
	function exportExcel(){
	        var rows = $('#llbinfo').datagrid('getRows'); //获取当前页的数据行  
	        var total = "";  
	        for (var i = 0; i < rows.length; i++) {  
	            total += rows[i]['applyId']+','; //获取指定列  
	        }  
			window.open('${pageContext.request.contextPath}/applyCar/exportExcel?number='+total);
	}
	//修改信息回显
	function edit(){
		es = "修改";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
		    $("#carCode").textbox("setText",row.carcode);
			$("#carType").textbox("setText",row.cartype);
			$("#carNum").textbox("setText",row.carnum);
			$("#carUnit").textbox("setText",row.carunit);
			$("#carDetailInfo").textbox("setText",row.cardetailinfo);
			$("#carDistance").textbox("setText",row.cardistance);
			$("#peasonNum").textbox("setText",row.peasonnum);
			$("#carInsuranceInfoDetal").textbox("setText",row.carinsuranceinfodetal);
			$("#state").textbox("setText",row.carstate);
			$("#suspendDay").val(row.suspendday);
			$("#cardVale").textbox("setText",row.carLable);
		     
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
})
	
</script>
</head>
<body>	<!-- 审核登记对话框 -->
	<div id="dlg" class="easyui-dialog" style="width:400px;height:500px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
        			<tr>
						<td><label>车名</label></td>
						<td><input id="carCode" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>车型</label></td>
						<td><input id="carType" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>车牌号</label></td>
						<td><input id="carNum" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>公司</label></td>
						<td><input id="carUnit" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>详情</label></td>
						<td><input id="carDetailInfo" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>卡金额</label></td>
						<td><input id="cardVale" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>里程数</label></td>
						<td><input id="carDistance" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>人数</label></td>
						<td><input id="peasonNum" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>保险信息</label></td>
						<td><input id="carInsuranceInfoDetal" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>状态</label></td>
						<td><input id="state" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/dictionary/selectDict.action?type=carinfo',
							method:'get',
							valueField:'info',
							textField:'text',
							editable:false,
							panelHeight:'100'
							" style="width: 220px;"></input></td>
<!-- 						<td><label>状态</label></td> -->
<!-- 						<td><input id="carState" class="easyui-textbox" style="width: 220px;"></input></td> -->
					</tr>
					<tr>
						<td><label>限行日</label></td>
						<td>
						<select id="suspendDay" style="width: 220px;"><option value="星期一" selected>星期一</option>
						<option value="星期二">星期二</option>
						<option value="星期三">星期三</option>
						<option value="星期四">星期四</option>
						<option value="星期五">星期五</option>
						<option value="星期六">星期六</option>
						<option value="星期日">星期日</option></select>
<!-- 						<input id="suspendDay" class="easyui-textbox" style="width: 220px;"></input></td> -->
</td>
					</tr>
        		</table>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_ok" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
	<div class="mdiv" style="width:100%;height:100%;">
		<table id="llbinfo" class="easyui-datagrid" title="公车使用信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
				 	<th
						data-options="field:'applyId',width:fixWidth(0.12),align:'center'">审批单号</th>
					<th 
						data-options="field:'department',width:fixWidth(0.08),align:'center'">部门</th>
					<th
						data-options="field:'applyMan',width:fixWidth(0.06),align:'center'">申请人</th>
					<th
						data-options="field:'approveMan',width:fixWidth(0.06),align:'center'">审批人</th>
					<th 
						data-options="field:'carType',width:fixWidth(0.06),align:'center'">车型</th>
					<th
						data-options="field:'carCode',width:fixWidth(0.08),align:'center'">车牌号</th>
					<th 
						data-options="field:'driver',width:fixWidth(0.06),align:'center'">驾驶人</th> 
					<th 
						data-options="field:'compareManNum',width:fixWidth(0.06),align:'center'">同行人数</th>
					<th 
						data-options="field:'lengthBegin',width:fixWidth(0.06),align:'center'">出车里程</th>
					<th
						data-options="field:'lengthEnd',width:fixWidth(0.06),align:'center'">还车里程</th>
					<th
						data-options="field:'accountLength',width:fixWidth(0.06),align:'center'">计费里程</th>
					<th
						data-options="field:'beginTimePlan',width:fixWidth(0.1),align:'center'">计划借车时间</th>
					<th
						data-options="field:'endTimePlan',width:fixWidth(0.1),align:'center'">计划还车时间</th>
					<th
						data-options="field:'beginTime',width:fixWidth(0.1),align:'center'">出车时间</th>
					<th
						data-options="field:'endTime',width:fixWidth(0.1),align:'center'">还车时间</th>
					<th
						data-options="field:'useCarReason',width:fixWidth(0.1),align:'center'">借车事由</th>
					<th
						data-options="field:'beginPlace',width:fixWidth(0.1),align:'center'">出发地</th>
					<th
						data-options="field:'endPlace',width:fixWidth(0.1),align:'center'">目的地</th>
					<th
						data-options="field:'accountPlanTime',width:fixWidth(0.1),align:'center'">计划用车时间</th>
					<th
						data-options="field:'accountRealTime',width:fixWidth(0.1),align:'center'">实际用车时间</th>
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
						<td><input id="adepartment" class="easyui-combobox" 
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
						<td><input id="amanager" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>车辆</label></td>
						<td><input id="acartype" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/CommonCar/getAllCar',
							method:'get',
							valueField:'id',
							textField:'typenum',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>起始时间</label></td>
						<td><input id="astarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>结束时间</label></td>
						<td><input id="aendtime" class="easyui-datebox"
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