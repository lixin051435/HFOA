<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>公车基本信息列表</title>
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
	var es = "";
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
			toolbar : [  {
				text : '添加',
				iconCls : 'icon-add',
				handler : function() {
					add();
				}},'-',{
				text : '修改',
				iconCls : 'icon-edit',
				handler : function() {
					edit();
				}
// 				},'-',{
// 					text : '上传图片',
// 					iconCls : 'icon-upload',
// 					handler : function() {
// 						upload();
// 					}

			}],
			
			onLoadSuccess:function(data){  
				 $('.preScan').linkbutton({text:'上传',plain:true,iconCls:'icon-search'}); 
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
	}
	//上传图片
	function uploadImage(value,row,index){
		return '<a class="preScan" href="#" iconCls="icon-save" onclick="upload('+ row.id +')">上传</a>';
	}
	function upload(carId){
		es1="上传图片";
		 $('#dlg1').dialog('open').dialog('setTitle',es1);
	     $('#fm1').form('clear');
	     $('#carId').val(carId);
	}
	$(function() {
		//$(param.llbid) 传递过来得履历本id
		
		loadGrid('${pageContext.request.contextPath}/CommonCar/showAllCarBaseInfo',null);
		// 确认按钮
		$("#btn_ok").click(function(){
			//
			
				var carcode = $("#carCode").textbox("getText");
				var cartype = $("#carType").textbox("getText");
				var carnum = $("#carNum").textbox("getText");
				var carunit = $("#carUnit").textbox("getText");
				var cardetailinfo = $("#carDetailInfo").textbox("getText");
				var cardistance = $("#carDistance").textbox("getText");
				var peasonnum = $("#peasonNum").textbox("getText");
				var carinsuranceinfodetal = $("#carInsuranceInfoDetal").textbox("getText");
				var carstate = $("#state").textbox("getText");
				var suspendday = $("#suspendDay").val();
				var cardvale = $("#cardVale").textbox("getText");
				var carLable= $("#state").textbox("getValue");
			
			if(es=="添加"){
				if(carstate=="全部"){
					alert("请选择车辆状态！");
				}else{
					var param = {carcode:carcode,cartype:cartype,carnum:carnum,carunit:carunit,
							cardetailinfo:cardetailinfo,cardistance:cardistance,peasonnum:peasonnum,
							carinsuranceinfodetal:carinsuranceinfodetal,carstate:carstate,suspendday:suspendday,cardvale:cardvale,carLable:carLable};
					$.post("${pageContext.request.contextPath}/CommonCar/insertCommonCar",param, function(result) {
						$("#dlg").dialog("close");
						$('#llbinfo').datagrid('reload');
					});
				}
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				
				var param = {id:row.id,carcode:carcode,cartype:cartype,carnum:carnum,carunit:carunit,
						cardetailinfo:cardetailinfo,cardistance:cardistance,peasonnum:peasonnum,
						carinsuranceinfodetal:carinsuranceinfodetal,carstate:carstate,suspendday:suspendday,cardvale:cardvale};
				
				$.post("${pageContext.request.contextPath}/CommonCar/updateCommonCar",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
			}
			
			
			
		});
		// 取消按钮
		$("#btn_cancel").click(function() {
			$('#dlg').dialog('close')
		});
		$("#btn_cancel1").click(function() {
			$('#dlg1').dialog('close')
		});
		//上传图片
		$("#btn_upload").click(function() {
			$("#fm1").form("submit", {    
				type: 'post',    
				url: '${pageContext.request.contextPath}/CommonCar/uploadFile', 
				success: function (data) {
					 if(data>0){
						 $('#dlg1').dialog('close');        // close the dialog
		                 $('#llbinfo').datagrid('reload');
						alert("上传成功！");
					 }
			}});
		});
		
	})	
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	
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
// 			$("#state").textbox("setValue",row.carLable);
			$("#suspendDay").val(row.suspendday);
			$("#cardVale").textbox("setText",row.carLable);
		     
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     $("#state").combobox("setText", "全部");
	}
	
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
    <div id="dlg1" class="easyui-dialog" style="width:400px;height:200px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm1" method="post" enctype="multipart/form-data">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>上传图片</label></td>
						<td>  
						<input value='' id="carId" style="display:none" name="carId">
						<input class="easyui-filebox" data-options="prompt:'浏览'" id="upload" name="upload" style="width:100%"> </td>
					</tr>
        		</table>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_upload" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel1" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
	<div class="mdiv" style="width:100%;height:100%;">
		<table id="llbinfo" class="easyui-datagrid"  title="公车基本信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.05),align:'center'" hidden="true">编号</th>
					<th
						data-options="field:'carcode',width:fixWidth(0.08),align:'center'">车名</th>
				 	<th
						data-options="field:'cartype',width:fixWidth(0.08),align:'center'">车型</th>
					<th 
						data-options="field:'carnum',width:fixWidth(0.1),align:'center'">车牌号</th>
					<th
						data-options="field:'carunit',width:fixWidth(0.1),align:'center'">公司</th>
					<th
						data-options="field:'cardetailinfo',width:fixWidth(0.1),align:'center'">详情</th>
					<th
						data-options="field:'cardvale',width:fixWidth(0.08),align:'center'">卡金额</th>
					<th 
						data-options="field:'cardistance',width:fixWidth(0.08),align:'center'">里程数</th>
					<th 
						data-options="field:'peasonnum',width:fixWidth(0.05),align:'center'">人数</th>
					<th 
						data-options="field:'carinsuranceinfodetal',width:fixWidth(0.15),align:'center'">保险信息</th>
					<th 
						data-options="field:'carstate',width:fixWidth(0.08),align:'center'">状态</th>
					<th
						data-options="field:'suspendday',width:fixWidth(0.08),align:'center'">限行日</th>
				    <th
					    data-options="field:'forUpload',width:fixWidth(0.2),formatter:uploadImage,align:'center'">上传图片</th>
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
						<td><label>车名</label></td>
						<td><input id="astarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>车型</label></td>
						<td><input id="amanager" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>车牌号</label></td>
						<td><input id="astarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>公司</label></td>
						<td><input id="astarttime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>购买时间</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>详情</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>里程数</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>人数</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>保险信息</label></td>
						<td><input id="aendtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>状态</label></td>
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