<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>角色列表</title>
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
	var es1="";
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
						deleteRole();
				}},'-',{
				text : '名称：<input type="text" id="title"/>',
				},'-',{
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					searchRole();
				}
			}],
			
			onLoadSuccess:function(data){  
				 $('.preScan').linkbutton({text:'分配权限',plain:true,iconCls:'icon-search'}); 
				 $('.preScan2').linkbutton({text:'分配流程',plain:true,iconCls:'icon-search'}); 
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
	//分配角色
	function editPermission(value,row,index){
		return '<a class="preScan" href="#" iconCls="icon-save" onclick="editPermissions('+ row.id +')">分配权限</a>';
	}
	//分配流程
	function editProcess(value,row,index){
		return '<a class="preScan2" href="#" iconCls="icon-save" onclick="editProcesses('+ row.id +')">分配流程</a>';
	}
	
	function editPermissions(roleid){
		var _tree = $('#t1'),
		roots = _tree.tree('getRoots');
		for ( var i = 0; i < roots.length; i++) {
			var node = _tree.tree('find', roots[i].id);
			_tree.tree('uncheck', node.target);
		}
		es1="分配";
		 $('#dlg1').dialog('open').dialog('setTitle',es1);
	     $('#fm1').form('clear');
	     $("#save_ok").attr("name",roleid); 
	     $.post("${pageContext.request.contextPath}/permission/getPermissionByRole?roleId="+roleid,null, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
				 $(result).each(function(i, obj){
                     var n = $("#t1").tree('find',obj);
                     if(n){
                         $("#t1").tree('check',n.target);
                     }
                 });
		});
	    
	}
	
	function editProcesses(roleid){
		var _tree = $('#t2'),
		roots = _tree.tree('getRoots');
		for ( var i = 0; i < roots.length; i++) {
			var node = _tree.tree('find', roots[i].id);
			_tree.tree('uncheck', node.target);
		}
		es1="分配流程";
		 $('#dlg2').dialog('open').dialog('setTitle',es1);
	     $('#fm2').form('clear');
	     $("#save_ok1").attr("name",roleid); 
	     $.post("${pageContext.request.contextPath}/process/getProcessByRoleId?roleId="+roleid,null, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
				 $(result).each(function(i, obj){
                     var n = $("#t2").tree('find',obj);
                     if(n){
                         $("#t2").tree('check',n.target);
                     }
                 });
		});
	    
	}
	function getTree(){
		$('#t1').tree({
			//发送异步ajax请求，还会携带id的参数
			url:'${pageContext.request.contextPath}/permission/getByNodeType',
			checkbox: true,
			onContextMenu:function(e,node){
				 console.log(node);
				//禁止浏览器的窗口打开
				e.preventDefault();
			},
			//展开所有菜单
			onLoadSuccess : function(node, data) {
		        if (data) {
		            $(data).each(function(index, value) {
		                if (this.state == 'closed') {
		                    $('#t1').tree('expandAll');
		                }
		            });
		        }
		    }
		});
		$('#t2').tree({
			//发送异步ajax请求，还会携带id的参数
			url:'${pageContext.request.contextPath}/process/getByNodeType',
			checkbox: true,
			onContextMenu:function(e,node){
				 console.log(node);
				//禁止浏览器的窗口打开
				e.preventDefault();
			},
			//展开所有菜单
			onLoadSuccess : function(node, data) {
		        if (data) {
		            $(data).each(function(index, value) {
		                if (this.state == 'closed') {
		                    $('#t2').tree('expandAll');
		                }
		            });
		        }
		    }
		});
	}
	$(function() {
		getTree();
		loadGrid('${pageContext.request.contextPath}/role/showAllRole',null);
		// 确认按钮
		$("#btn_ok").click(function(){
				var rolename = $("#rolename").textbox("getText");
        	    var available=$("#avaiable").val();
			if(es=="添加"){
				if(available==""){
					alert("请确定角色是否可用！");
				}else{
					var param = {rolename:rolename,available:available};
					$.post("${pageContext.request.contextPath}/role/insertRole",param, function(result) {
						$("#dlg").dialog("close");
						$('#llbinfo').datagrid('reload');
					});
				}
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				var param = {id:row.id,rolename:rolename,available:available};
				
				$.post("${pageContext.request.contextPath}/role/updateRole",param, function(result) {
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
		$("#btn_cancel2").click(function() {
			$('#dlg2').dialog('close')
		});
		// 上传取消按钮
		$("#uploadFileCancel").click(function() {
			document.getElementById('fileImport').value = null;
			$('#import-excel').window('close')
		});
		//分配角色
		$("#save_ok").click(function(){
			var permissionid = $("#t1").tree("getChecked");;
			var roleid = this.name;
			var permissionids="";
			for(var i=0;i<permissionid.length;i++){
				permissionids+=permissionid[i].id+",";
			}
			$.post("${pageContext.request.contextPath}/role/editMiddlePermission",{permissionids:permissionids,roleid:roleid}, function(result) {
				$("#dlg1").dialog("close");
			});	
		});
		//分配流程
		$("#save_ok1").click(function(){
			var processid = $("#t2").tree("getChecked");;
			var roleid = this.name;
			var processids="";
			for(var i=0;i<processid.length;i++){
				processids+=processid[i].id+",";
			}
			$.post("${pageContext.request.contextPath}/role/editMiddleProcess",{processids:processids,roleid:roleid}, function(result) {
				$("#dlg2").dialog("close");
			});	
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
		    $("#rolename").textbox("setText",row.rolename);
			$("#avaiable").val(row.available);
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     $("#avaiable").val("1");
	}
	function deleteRole(){
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			var param = {id:row.id};
			$.post("${pageContext.request.contextPath}/role/deleteRole",param, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
			});
		}else{
			alert("请选择要删除的数据！");
		}
		
	}
	function searchRole(){
		es = "查询";
		var param = {title:$("#title").val()};
		loadGrid('${pageContext.request.contextPath}/role/searchRole',param);
	}
</script>
</head>
<body>	<!-- 权限编辑表 -->
	<div id="dlg" class="easyui-dialog" style="width:400px;height:200px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>名称</label></td>
						<td><input id="rolename" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>是否可用</label></td>
						<td><select id="avaiable" style="width: 220px;"><option value="1" selected>是</option><option value="0">否</option></select></td>
					</tr>
        		</table>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_ok" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
    <div id="dlg1" class="easyui-dialog" style="width:400px;height:400px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>权限编辑</div>  -->
        <form id="fm1" method="post">
       <div style="text-align:center;">
						<div style="height: 30%;width: 30%;position:absolute;"><label>权限:</label></div>
						<div style="height: 30%;width: 30%;margin-left: 40%"><ul id="t1" class="easyui-tree"></ul></div>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="save_ok" class="easyui-linkbutton" name="aa" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel1" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
    <div id="dlg2" class="easyui-dialog" style="width:400px;height:400px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>流程编辑</div>  -->
        <form id="fm2" method="post">
       <div style="text-align:center;">
						<div style="height: 30%;width: 30%;position:absolute;"><label>流程:</label></div>
						<div style="height: 30%;width: 30%;margin-left: 40%"><ul id="t2" class="easyui-tree"></ul></div>
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="save_ok1" class="easyui-linkbutton" name="bb" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel2" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
	<div class="mdiv" style="width:100%;height:100%;">
		<table id="llbinfo" class="easyui-datagrid"  title="角色信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.1),align:'center'" hidden="true">编号</th>
					<th
						data-options="field:'rolename',width:fixWidth(0.4),align:'center'">名称</th>
				 	<th
						data-options="field:'showAvailable',width:fixWidth(0.2),align:'center'">是否可用</th>
				<th
					    data-options="field:'foreditPermission',width:fixWidth(0.2),formatter:editPermission,align:'center'">操作</th>
					    <th
					    data-options="field:'foreditProcess',width:fixWidth(0.2),formatter:editProcess,align:'center'">分配流程</th>
				</tr>
			</thead>
		</table>  
	</div>
</body>
</html>