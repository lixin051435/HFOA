<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门列表</title>
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
	var rolelable="";
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
						deleteDepartment();
				}},'-',{
				text : '名称：<input type="text" id="title"/>',
				},'-',{
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					searchDepartment();
				}
			}],
			
			onLoadSuccess:function(data){  
				 $('.preScan').linkbutton({text:'修改角色',plain:true,iconCls:'icon-edit'});
				 $('.preScan2').linkbutton({text:'选择抄送部门',plain:true,iconCls:'icon-edit'});
				 $('.preScan1').linkbutton({text:'查看角色',plain:true,iconCls:'icon-search'}); 
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
	//查看部门的所有角色
	 function viewRole(value,row,index){
		return '<a class="preScan1" href="#" iconCls="icon-save" onclick="viewRoles('+ row.id +')">查看</a>';
	}
	function viewRoles(departmentid){
		rolelable = "查看";
		 $('#role').dialog('open').dialog('setTitle',rolelable);
		 $('#editRole').hide();
		//加载所有角色
		 $("#llbinfo1").datagrid({
		 	idField : 'id',
			url:"${pageContext.request.contextPath}/role/getAllRoleByDepartId.action?departmentid="+departmentid,
			method : 'post',
			rownumbers : true,
			singleSelect : false,
			onLoadSuccess:function(data){
				$('#llbinfo1').datagrid('selectAll');
				} 
		});
		
	}
	//给部门分配角色
	 function editRole(value,row,index){
		return '<a class="preScan" href="#" iconCls="icon-save" onclick="editRoles('+ row.id +')">查看</a>';
	}
	function editRoles(departmentid){
		rolelable = "修改";
		 $('#role').dialog('open').dialog('setTitle',rolelable);
		 $('#editRole').show();
		 $('#llbinfo1').datagrid('unselectAll');
		//加载所有角色
		 $("#llbinfo1").datagrid({
		 	idField : 'id',
			url:"${pageContext.request.contextPath}/role/getAllRole.action",
			method : 'post',
			rownumbers : true,
			singleSelect : false,
			onLoadSuccess:function(data){
				$.post("${pageContext.request.contextPath}/role/getRoleByDepartmentId?departmentid="+departmentid,null, function(result) {
			             var funId = result.split(',');
			             var rowData = data.rows;
						    $.each(rowData, function (idx, val) {
						    	for(var i=0;i<funId.length;i++){
						    		if (val.id==funId[i]) {
							            $("#llbinfo1").datagrid("selectRow", idx);
							        }
						    	}
						    }); 
	                    
				});
				 
				} 
		});
	}

	//选择抄送部门
	 function copyDepart(value,row,index){
		return '<a class="preScan2" href="#" iconCls="icon-save" onclick="copyDeparts('+ row.id +')">选择</a>';
	}
	function copyDeparts(departmentId){
		rolelable = "查看";
		 $('#copy').dialog('open').dialog('setTitle',rolelable);
		 $('#selectDepart').show();
		 $('#llbinfo2').datagrid('unselectAll');
		//加载所有角色
		 $("#llbinfo2").datagrid({
		 	idField : 'id',
			url:"${pageContext.request.contextPath}/department/getAllAvalibleDepart.action",
			method : 'post',
			rownumbers : true,
			singleSelect : false,
			onLoadSuccess:function(data){
			$.post("${pageContext.request.contextPath}/department/getAlldepartByDId?departmentid="+departmentId,null, function(result) {
		             var funId = result.split(',');
		             var rowData = data.rows;
					    $.each(rowData, function (idx, val) {
					    	for(var i=0;i<funId.length;i++){
					    		if (val.id==funId[i]) {
						            $("#llbinfo2").datagrid("selectRow", idx);
						        }
					    	}
					    }); 
                    
			});
			 
			} 
		});
	}
	$(function() {
		loadGrid('${pageContext.request.contextPath}/department/getAllDepartmentByPage',null);
		// 确认按钮
		$("#btn_ok").click(function(){
				var departmentname = $("#departmentname").textbox("getText");
        	    var available=$("#available").val();
			if(es=="添加"){
				var param = {departmentname:departmentname,available:available};
				$.post("${pageContext.request.contextPath}/department/insertDepartment",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				var param = {id:row.id,departmentname:departmentname,available:available};
				$.post("${pageContext.request.contextPath}/department/updateDepartment",param, function(result) {
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
		//分配角色
		$("#save_ok").click(function(){
			var departmentid = $('#llbinfo').datagrid('getSelected').id;
			var roleids = "";
			var rows = $('#llbinfo1').datagrid('getSelections');  
			for(var i=0; i<rows.length; i++){  
			    if(i==rows.length-1){
			    	roleids += rows[i].id;
			    }else{
			    	roleids += rows[i].id+",";
			    }
			}
			if(roleids!=""){
				$.post('${pageContext.request.contextPath}/department/updateMiddleDepartment',{roleids:roleids,departmentid:departmentid},function(result){
					if (result=="1"){
		                $('#dlg1').dialog('close');        // close the dialog
		                $('#llbinfo1').datagrid('reload');    // reload the user data
		                $.messager.show({
		                    title: 'Message',
		                    msg: '操作成功!'
		                });
		            } else {
		                $.messager.show({
		                    title: 'Error',
		                    msg: '操作失败!'
		                });
		            }
	            },'text');
			}else{
				$.messager.show({
	                title: 'Message',
	                msg: '请选择!'
	            });
			}
		});
		//选择抄送部门
		//分配角色
		$("#select_ok").click(function(){
			var departmentid = null;
			if($('#llbinfo').datagrid('getSelected')!=null){
				departmentid = $('#llbinfo').datagrid('getSelected').id;
			}
			var departmentIds = "";
			var rows = $('#llbinfo2').datagrid('getSelections');  
			for(var i=0; i<rows.length; i++){  
			    if(i==rows.length-1){
			    	departmentIds += rows[i].id;
			    }else{
			    	departmentIds += rows[i].id+",";
			    }
			}
			if(departmentIds!=""){
				$.post('${pageContext.request.contextPath}/department/updateCopyDepartment',{departmentIds:departmentIds,departmentid:departmentid},function(result){
					if (result=="1"){
		                $('#copy').dialog('close');        // close the dialog
		                $('#llbinfo2').datagrid('reload');    // reload the user data
		                $.messager.show({
		                    title: 'Message',
		                    msg: '操作成功!'
		                });
		            } else {
		                $.messager.show({
		                    title: 'Error',
		                    msg: '操作失败!'
		                });
		            }
	            },'text');
			}else{
				$.messager.show({
	                title: 'Message',
	                msg: '请选择!'
	            });
			}
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
		    $("#departmentname").textbox("setText",row.departmentname);
			$("#available").val(row.available);
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     $("#available").val("0");
	}
	function deleteDepartment(){
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			var param = {id:row.id};
			$.post("${pageContext.request.contextPath}/department/deleteDepartment",param, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
			});
		}else{
			alert("请选择要删除的数据！");
		}
		
	}
	function searchDepartment(){
		es = "查询";
		var param = {title:$("#title").val()};
		loadGrid('${pageContext.request.contextPath}/department/searchDepartment',param);
	}

</script>
</head>
<body>	<!-- 部门编辑表 -->
	<div id="dlg" class="easyui-dialog" style="width:400px;height:200px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>部门名称</label></td>
						<td><input id="departmentname" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>是否可用</label></td>
						<td><select id="available" style="width: 220px;"><option value="0" selected>否</option><option value="1">是</option></select></td>
					</tr>
        		</table>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_ok" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
<!--     角色编辑表 -->
    <div id="role" class="easyui-dialog" style="width:30%;height:500px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <table id="llbinfo1" class="easyui-datagrid" title="角色列表"
			style="width: auto; height:90%;">
			<thead>
				<tr>
					<th field="id" checkbox="true" id="all"></th>
					<th
						data-options="field:'rolename',width:fixWidth(0.25),align:'left'">角色名称</th>
				</tr>
			</thead>
		</table>
		<br>
			<div style="text-align: center; margin-top: 10px; display:none" id="editRole">
				<a href="#" id="save_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">保存</a> 
			</div>
    </div>
<!--     选择抄送单位 -->
   <div id="copy" class="easyui-dialog" style="width:30%;height:500px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <table id="llbinfo2" class="easyui-datagrid" title="部门列表"
			style="width: auto; height:90%;">
			<thead>
				<tr>
					<th field="id" checkbox="true" id="allDepart"></th>
					<th
						data-options="field:'departmentname',width:fixWidth(0.25),align:'left'">部门名称</th>
				</tr>
			</thead>
		</table>
		<br>
			<div style="text-align: center; margin-top: 10px; display:none" id="selectDepart">
				<a href="#" id="select_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">确定</a> 
			</div>
    </div>
	<div class="mdiv" style="width:100%;height:100%;">
		<table id="llbinfo" class="easyui-datagrid"  title="部门信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.1),align:'center'" hidden="true">编号</th>
					<th
						data-options="field:'departmentname',width:fixWidth(0.3),align:'center'">部门名称</th>
				 	<th
						data-options="field:'showAvailable',width:fixWidth(0.1),align:'center'">是否可用</th>
					<th
					    data-options="field:'forShowRole',width:fixWidth(0.2),formatter:viewRole,align:'center'">查看</th>
				    <th
					    data-options="field:'foreditRole',width:fixWidth(0.2),formatter:editRole,align:'center'">操作</th>
					<th
					    data-options="field:'forCopyDepart',width:fixWidth(0.2),formatter:copyDepart,align:'center'">抄送部门</th>
				</tr>
			</thead>
		</table>  
	</div>
</body>
</html>