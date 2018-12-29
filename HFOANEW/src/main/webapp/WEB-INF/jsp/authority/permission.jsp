<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>权限列表</title>
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
				}},'-',{
					text : '删除',
					iconCls : 'icon-remove',
					handler : function() {
						deletePermission();
				}},'-',{
				text : '名称：<input type="text" id="title"/>',
				},'-',{
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					searchPermission();
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
	}
	$(function() {
		$("#urlclass").combobox(
		{
			onSelect : function(record) {
				var info = $("#urlclass").combobox('getValue');
				if(info==2){
					$("#weixin").show();
					$("#Icon").show();
				}else{
					$("#weixin").hide();
					$("#Icon").hide();
				}
				if(info==1||info==3){
					$("#grade").show();
				}else{
					$("#grade").hide();
				}
			}
		})
		loadGrid('${pageContext.request.contextPath}/permission/showAllPermission',null);
		// 确认按钮
		$("#btn_ok").click(function(){
				var permissionname = $("#permissionname").textbox("getText");
				var permissionurl = $("#permissionurl").textbox("getText");
				var urlclass = $("#urlclass").textbox("getValue");
				var showUrlClass = $("#urlclass").textbox("getText");
				var permissionsort = $("#permissionsort").textbox("getText");
        	    var available=$("#avaiable").val();
				var parentid = $("#parentid").textbox("getText");
				var permissionlable=$("#weixin1").val();
				var permissiontype=$("#grade1").val();
			if(es=="添加"){
				if(urlclass==""){
					alert("请选择路径类型！");
				}else{
					var param = {permissionname:permissionname,permissionurl:permissionurl,urlclass:urlclass,permissionsort:permissionsort,
							available:available,parentid:parentid,permissionlable:permissionlable,permissiontype:permissiontype};
					$.post("${pageContext.request.contextPath}/permission/insertPermission",param, function(result) {
						$("#pId").val(result);
						 if($("#file").val()!=""){
							 $("#myform").form("submit", {    
								type: 'post',    
								url: '${pageContext.request.contextPath}/permission/uploadFile', 
								success: function (data) {
									 if(data>0){
										alert("上传成功！");
									 }
							}});
						 }
						$("#dlg").dialog("close");
						$('#llbinfo').datagrid('reload');
					});
				}
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				var param = {id:row.id,permissionname:permissionname,permissionurl:permissionurl,showUrlClass:showUrlClass,permissionsort:permissionsort,
						available:available,parentid:parentid,permissionlable:permissionlable,permissiontype:permissiontype};
				
				$.post("${pageContext.request.contextPath}/permission/updatePermission",param, function(result) {
					$("#pId").val(row.id);
					 if($("#file").val()!=""){
						 $("#myform").form("submit", {    
							type: 'post',    
							url: '${pageContext.request.contextPath}/permission/uploadFile', 
							success: function (data) {
								 if(data>0){
									alert("上传成功！");
								 }
						}});
					 }
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
			}
			
			
			
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
		    $("#permissionname").textbox("setText",row.permissionname);
			$("#permissionurl").textbox("setText",row.permissionurl);
			$("#urlclass").textbox("setText",row.showUrlClass);
			$("#permissionsort").textbox("setText",row.permissionsort);
			$("#avaiable").val(row.available);
			$("#parentid").textbox("setText",row.parentid);
			$("#grade1").val(row.permissiontype);
			$("#wexin1").val(row.permissionlable);
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	}
	function deletePermission(){
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			var param = {id:row.id};
			$.post("${pageContext.request.contextPath}/permission/deletePermission",param, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
			});
		}else{
			alert("请选择要删除的数据！");
		}
		
	}
	function searchPermission(){
		es = "查询";
		var param = {title:$("#title").val()};
		loadGrid('${pageContext.request.contextPath}/permission/searchPermission',param);
	}
</script>
</head>
<body>	<!-- 权限编辑表 -->
	<div id="dlg" class="easyui-dialog" style="width:400px;height:300px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>名称</label></td>
						<td><input id="permissionname" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>路径</label></td>
						<td><input id="permissionurl" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>路径类别</label></td>
<!-- 						<td><input id="urlclass" class="easyui-textbox" style="width: 220px;"></input></td> -->
						<td><input id="urlclass" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/dictionary/selectDict.action?type=url',
							method:'get',
							valueField:'info',
							textField:'text',
							editable:false,
							panelHeight:'70'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>排序</label></td>
						<td><input id="permissionsort" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>是否可用</label></td>
						<td><select id="avaiable" style="width: 220px;"><option value="1" selected>是</option><option value="0">否</option></select></td>
					</tr>
					<tr>
						<td><label>父级编号</label></td>
						<td><input id="parentid" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr id="grade" style="display:none">
						<td><label>菜单级别</label></td>
						<td><select id="grade1" style="width: 220px;"><option value="fatherLevel" selected>父级菜单</option><option value="childLevel">子级菜单</option></select></td>
					</tr>
					<tr id="weixin" style="display:none">
						<td><label>微信标识</label></td>
						<td><select id="weixin1" style="width: 220px;"><option value="my" selected>我的界面</option><option value="first">首界面</option></select></td>
					</tr>
        		</table>
        		
        		 </form>
        		  <div id="Icon" style="display:none">
					<div style="width: 20%;height: 10%;margin-left: 10%"><label>微信图标</label></div>
					<form  id="myform" method="POST" enctype="multipart/form-data"accept="image/gif, image/jpeg,image/jpg, image/png">
					<div style="width: 80%;height: 10%;margin-left: 30%">
					<input value="" id="pId" style="display:none" name="pId">
					 <input  id="file" type="file" name="icon"/></div></form>
		       </div>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_ok" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
       
       
    </div>
	<div class="mdiv" style="width:100%;height:100%;">
		<table id="llbinfo" class="easyui-datagrid"  title="权限信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.1),align:'center'">编号</th>
					<th
						data-options="field:'permissionname',width:fixWidth(0.2),align:'center'">名称</th>
				 	<th
						data-options="field:'permissionurl',width:fixWidth(0.3),align:'center'">路径</th>
					<th 
						data-options="field:'showUrlClass',width:fixWidth(0.2),align:'center'">路径类别</th>
					<th
						data-options="field:'permissionsort',width:fixWidth(0.1),align:'center'">排序</th>
					<th
						data-options="field:'showAvailable',width:fixWidth(0.1),align:'center'">是否可用</th>
				</tr>
			</thead>
		</table>  
	</div>
</body>
</html>