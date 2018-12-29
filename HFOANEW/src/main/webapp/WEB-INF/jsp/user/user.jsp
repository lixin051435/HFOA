<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户列表</title>
<link rel="shortcut icon"
	href="${pageContext.request.contextPath}/img/entertainImg/favicon.ico">
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

	Date.prototype.Format = function(fmt) { //author: meizz 
		var o = {
			"M+" : this.getMonth() + 1, //月份 
			"d+" : this.getDate(), //日 
			"h+" : this.getHours(), //小时 
			"m+" : this.getMinutes(), //分 
			"s+" : this.getSeconds(), //秒 
			"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
			"S" : this.getMilliseconds()
		//毫秒 
		};
		if (/(y+)/.test(fmt))
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		for ( var k in o)
			if (new RegExp("(" + k + ")").test(fmt))
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
						: (("00" + o[k]).substr(("" + o[k]).length)));
		return fmt;
	}

	function loadGrid(url, data) {
		//汉化 datagrid 翻页
		$("#llbinfo").datagrid({
			url : url,
			method : 'post',
			queryParams : data,
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
			},'-', {
				text : '分配角色',
				iconCls : 'icon-edit',
				handler : function() {
					distributRole();
				}
			}, '-', {
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					deleteUser();
				}
			}, '-', {
				text : '姓名：<input type="text" id="title"/>',
			}, '-', {
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					searchUser();
				}
			} ],

			onLoadSuccess : function(data) {
				$('.preScan').linkbutton({
					text : '查看明细',
					plain : true,
					iconCls : 'icon-search'
				});
				$('.preScan1').linkbutton({
					text : '凭证号登记',
					plain : true,
					iconCls : 'icon-save'
				});
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
		//动态加载部门角色
// 		$("#department").combobox(
// 		{
// 			onSelect : function(record) {
// 				$("#rolebox").empty();
// 				var departmentid = $("#department").combobox(
// 						'getValue');
// 				$.ajax({
// 					type : "POST",
// 					url : '${pageContext.request.contextPath}/role/getRoleByDepartment',
// 					dataType : "json",
// 					data : {
// 						"departmentid" : departmentid
// 					},
// 					success : function(data) {
// 						if (data != null
// 								&& data.length > 0) {
// 							var result = "";
// 							for (var i = 0; i < data.length; i++) {
// 								result = '<label"><input type="checkbox" value='+data[i].id+' name="role"/>'
// 										+ data[i].rolename
// 										+ '</label>';
// 								$("#rolebox").append(result);
// 							}
// 						}
// 					}
// 				});
// 			}
// 		})
		loadGrid('${pageContext.request.contextPath}/user/showAllUser', null);
		// 确认按钮
		$("#btn_ok").click(
		function() {
			var realname = $("#realname").textbox("getText");
			var companyname = $("#companyname").textbox("getText");
			var code = $("#code").textbox("getText");
			var userpassword = $("#userpassword").textbox("getText");
			var telephone = $("#telephone").textbox("getText");
			var mobile = $("#mobile").textbox("getText");
			var ipaddress = $("#ipaddress").textbox("getText");
			var macaddress = $("#macaddress").textbox("getText");
			var officeaddr = $("#officeaddr").textbox("getText");
			var homeaddress = $("#homeaddress").textbox("getText");
			var departmentname = $("#department").textbox("getText");
			var departmentid = $("#department").textbox("getValue");
			var gender = $("#gender").val();
			var auditstatus = $("#auditstatus").val();
			var status = $("#status").val();
			var createby=$("#createby").val();
			obj = document.getElementsByName("role");
			var duty = "";
			for (k in obj) {
				if (obj[k].checked)
					duty += obj[k].value + ",";
			}
			var leaderId = $("#leader").textbox("getValue");
			var leadername = $("#leader").textbox("getText");
			if (es == "添加") {
				if (departmentname == "全部") {
					alert("请确定部门！");
				} else {
					var param = {
						realname : realname,
						companyname : companyname,
						code : code,
						username:code,
						userpassword : userpassword,
						telephone : telephone,
						mobile : mobile,
						ipaddress : ipaddress,
						macaddress : macaddress,
						officeaddr : officeaddr,
						homeaddress : homeaddress,
						departmentname : departmentname,
						departmentid : departmentid,
						gender : gender,
						auditstatus : auditstatus,
						status : status,
// 						duty : duty,
						leadername : leadername,
						workgroupid : leaderId,
						createby:createby
					};
					$.post("${pageContext.request.contextPath}/user/insertUser",param, function(result) {
						if(result==1){
							$("#dlg").dialog("close");
							$('#llbinfo').datagrid('reload');
							$.messager.show({
			                    title: 'Message',
			                    msg: '添加成功!'
			                });
						}else{
							$.messager.show({
			                    title: 'Message',
			                    msg: '该账号已存在！'
			                });
						}
						
					});
				}
			} else {
				var row = $('#llbinfo').datagrid('getSelected');
				var param = {
					id : row.id,
					realname : realname,
					companyname : companyname,
					code : code,
					username:code,
					userpassword : userpassword,
					telephone : telephone,
					mobile : mobile,
					ipaddress : ipaddress,
					macaddress : macaddress,
					officeaddr : officeaddr,
					homeaddress : homeaddress,
					departmentname : departmentname,
					gender : gender,
					auditstatus : auditstatus,
					status : status,
// 					duty : duty,
					leadername : leadername,
					createby:createby
				};

				$.post("${pageContext.request.contextPath}/user/updateUser",param, function(result) {
					if(result==1){
						$("#dlg").dialog("close");
						$('#llbinfo').datagrid('reload');
						$.messager.show({
		                    title: 'Message',
		                    msg: '修改成功!'
		                });
					}else{
						$.messager.show({
		                    title: 'Message',
		                    msg: '该账号已存在！'
		                });
					}
				});
			}

		});
		//分配角色
		$("#save_ok").click(function(){
			var userId = $('#llbinfo').datagrid('getSelected').id;
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
				$.post('${pageContext.request.contextPath}/user/updateRoleByUserId',{roleids:roleids,userId:userId},function(result){
// 					if (result=="1"){
		                $('#role').dialog('close');        // close the dialog
		                $('#llbinfo1').datagrid('reload');    // reload the user data
// 		                $.messager.show({
// 		                    title: 'Message',
// 		                    msg: '操作成功!'
// 		                });
// 		            } else {
// 		                $.messager.show({
// 		                    title: 'Error',
// 		                    msg: '操作失败!'
// 		                });
// 		            }
	            },'text');
			}else{
				$.messager.show({
	                title: 'Message',
	                msg: '请选择!'
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

	//分配角色
	function distributRole(){
		$('#llbinfo1').datagrid('unselectAll');
		var row = $('#llbinfo').datagrid('getSelected');
		es = "修改";
		if(row){
			 $('#role').dialog('open').dialog('setTitle',es);
			 $('#editRole').show();
			 var userId= $('#llbinfo').datagrid('getSelected').id;
			var departmentid= $('#llbinfo').datagrid('getSelected').departmentid;
			//加载所有角色
			 $("#llbinfo1").datagrid({
			 	idField : 'id',
				url:"${pageContext.request.contextPath}/role/getRoleByDepartment.action?departmentid="+departmentid,
				method : 'post',
				rownumbers : true,
				singleSelect : false,
				onLoadSuccess:function(data){
					$.post("${pageContext.request.contextPath}/user/getRoleByUserId?userId="+userId,null, function(result) {
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
					 
					} });
		}else{
			alert("请选择要分配角色的用户信息！");
		}

	}
	function edit() {
		$("#rolebox").empty();
		es = "修改";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row) {
			$('#dlg').dialog('open').dialog('setTitle', es);
			$('#fm').form('clear');
			$("#realname").textbox("setText", row.realname);
			$("#companyname").textbox("setText", row.companyname);
			$("#code").textbox("setText", row.code);
// 			$("#userpassword").textbox("setText", row.userpassword);
			$("#telephone").textbox("setText", row.telephone);
			$("#mobile").textbox("setText", row.mobile);
			$("#ipaddress").textbox("setText", row.ipaddress);
			$("#macaddress").textbox("setText", row.macaddress);
			$("#officeaddr").textbox("setText", row.officeaddr);
			$("#homeaddress").textbox("setText", row.homeaddress);
			$("#gender").val(row.gender);
			$("#auditstatus").val(row.auditstatus);
			$("#status").val(row.status);
			$("#department").textbox("setText", row.departmentname);
			$("#leader").textbox("setText", row.leadername);
			$("#createby").textbox("setText", row.createby);
			
			//动态回显自己的角色
// 			var departmentid = row.departmentid;
// 			$.post("${pageContext.request.contextPath}/role/getRoleByDepartment?departmentid="+ departmentid,null,
// 			function(data) {
// 				var roleids=[];
// 				var rolename=[];
// 				var index=[];
// 				var oriIndex=[];
// 				var result = "";
// 				var role = row.duty.split(",");
// 				if (data != null && data.length > 0) {
// 					for (var i = 0; i < data.length; i++) {
// 						oriIndex.push(i);
// 						for (var j = 0; j < role.length; j++) {
// 							if (role[j] == data[i].id) {
// 								roleids.push(data[i].id);
// 								rolename.push(data[i].rolename);
// 								index.push(i);
// 							}
							
// 						}
// 					}
// 					if(index.length!=0&&oriIndex.length!=0){
// 						for(var m=0;m<oriIndex.length;m++){
// 							for(var n=0;n<index.length;n++){
// 								if(index[n]==oriIndex[m]){
// 									oriIndex.splice(m,1)
// 								}
// 							}
// 						}
// 					}
// 					for (var i = 0; i < oriIndex.length; i++) {
// 						var a=oriIndex[i];
// 						result= '<label"><input type="checkbox" value='+data[a].id+' name="role"/>'
// 								+ data[a].rolename
// 								+ '</label>';
// 						$("#rolebox").append(result);
// 					}
					
// 					for(var k=0;k<roleids.length;k++){
// 						result= '<label"><input type="checkbox" value='+roleids[k]+' name="role" checked="true"/>'
// 						+ rolename[k]
// 						+ '</label>';
// 						$("#rolebox").append(result);
// 					}
					
// 				}
				
// 			});
		} else {
			alert("请选择要修改的数据！");
		}
	}
	function add() {
		es = "添加";
		$('#dlg').dialog('open').dialog('setTitle', es);
		$('#fm').form('clear');
		$("#adepartment").combobox("setText", "全部");
		$("#gender").val("男");
		$("#auditstatus").val("在职");
		$("#status").val("0");
		$("#rolebox").empty();

	}
	function deleteUser() {
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row) {
			$.messager.confirm('Confirm', '确定要删除该用户?', function(r){
				if (r){
		 			var param = {
	 				id : row.id
	 			};
	 			$.post("${pageContext.request.contextPath}/user/deleteUser", param,
	 					function(result) {
	 				if(result==1){
						$('#llbinfo').datagrid('reload');
						$.messager.show({
		                    title: 'Message',
		                    msg: '删除成功!'
		                });
					}else{
						$.messager.show({
		                    title: 'Message',
		                    msg: '删除失败！'
		                });
					}
	 					});
				}
			});

		} else {
			alert("请选择要删除的数据！");
		}
		

	}
	function searchUser() {
		es = "查询";
		var param = {
			title : $("#title").val()
		};
		loadGrid('${pageContext.request.contextPath}/user/searchUser', param);
	}
</script>
</head>
<body>
<!-- 分配角色 -->
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
	<!-- 用户编辑表 -->
	<div id="dlg" class="easyui-dialog"
		style="width: 600px; height: 500px; padding: 0px 0px;" closed="true"
		buttons="#dlg-buttons">
		<!--<div>用户编辑</div>  -->
		<form id="fm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>姓名</label></td>
						<td><input id="realname" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>企业名称</label></td>
						<td><input id="companyname" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>账号</label></td>
						<td><input id="code" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>密码</label></td>
						<td><input id="userpassword" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>电话</label></td>
						<td><input id="telephone" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>手机</label></td>
						<td><input id="mobile" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>IP地址</label></td>
						<td><input id="ipaddress" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>mac地址</label></td>
						<td><input id="macaddress" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>办公室地址</label></td>
						<td><input id="officeaddr" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>家庭住址</label></td>
						<td><input id="homeaddress" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>性别</label></td>
						<td><select id="gender" style="width: 220px;"><option
									value="男" selected>男</option>
								<option value="女">女</option></select></td>
						<td><label>状态</label></td>
						<td><select id="auditstatus" style="width: 220px;"><option
									value="在职" selected>在职</option>
								<option value="离职">离职</option></select></td>
					</tr>
					<tr>
						<td><label>所在部门</label></td>
						<td><input id="department" class="easyui-combobox"
							data-options="
							url:'${pageContext.request.contextPath}/department/getAllDepartment',
							method:'get',
							valueField:'id',
							textField:'departmentname',
							editable:false,
							panelHeight:'200'
							"
							style="width: 220px;"></input></td>
						<td><label>是否禁用</label></td>
						<td><select id="status" style="width: 220px;"><option
									value="0" selected>否</option>
								<option value="1">是</option></select></td>
					</tr>
					<tr>
						<td><label>审核人</label></td>
						<td><input id="leader" class="easyui-combobox"
							data-options="
							url:'${pageContext.request.contextPath}/user/getAllLeader',
							method:'get',
							valueField:'id',
							textField:'code',
							editable:false,
							panelHeight:'200'
							"
							style="width: 220px;"></input></td>
							<td><label>年假天数</label></td>
						<td><input id="createby" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
				</table>
<!-- 				<div -->
<!-- 					style="height: 50px; width: 20%; position: absolute; margin-left: -5%"> -->
<!-- 					<label>角色</label> -->
<!-- 				</div> -->
				<div id="rolebox" style="height: 50px; width: 90%; margin-left: 10%"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="btn_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">确定</a> <a
					href="#" id="btn_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
			</div>
		</form>
	</div>
	<div class="mdiv" style="width: 100%; height: 100%;">
		<table id="llbinfo" class="easyui-datagrid" title="用户信息"
			style="width: auto; height: 100%;">
			<thead>
				<tr>
				<th data-options="field:'departmentid',width:fixWidth(0.1),align:'center'"
						hidden="true">部门编号</th>
					<th data-options="field:'id',width:fixWidth(0.1),align:'center'"
						hidden="true">编号</th>
					<th data-options="field:'createby',width:fixWidth(0.1),align:'center'"
						hidden="true">年假天数</th>
					<th
						data-options="field:'realname',width:fixWidth(0.1),align:'center'">姓名</th>
					<th
						data-options="field:'companyname',width:fixWidth(0.2),align:'center'">企业名称</th>
					<th
						data-options="field:'departmentname',width:fixWidth(0.1),align:'center'">部门名称</th>
					<!-- 					<th -->
					<!-- 						data-options="field:'gender',width:fixWidth(0.5),align:'center'">性别</th> -->
					<!-- 					<th -->
					<!-- 						data-options="field:'mobile',width:fixWidth(0.5),align:'center'">手机号</th> -->
					<!-- 					<th -->
					<!-- 						data-options="field:'telephone',width:fixWidth(0.5),align:'center'">电话号</th> -->
					<th
						data-options="field:'createon',width:fixWidth(0.1),align:'center'">创建时间</th>
					<th
						data-options="field:'auditstatus',width:fixWidth(0.1),align:'center'">状态</th>
					<th
						data-options="field:'ipaddress',width:fixWidth(0.2),align:'center'">IP地址</th>
					<th
						data-options="field:'macaddress',width:fixWidth(0.2),align:'center'">mac地址</th>
					<!-- 					<th -->
					<!-- 						data-options="field:'homeaddress',width:fixWidth(0.5),align:'center'">家庭住址</th> -->
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>