<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>微信公告列表</title>
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
						deleteNotice();
				}},'-',{
				text : '名称：<input type="text" id="title"/>',
				},'-',{
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					searchNotice();
				}
			}],
			
			onLoadSuccess:function(data){  
				 $('.preScan').linkbutton({text:'修改角色',plain:true,iconCls:'icon-edit'}); 
				 $('.preScan1').linkbutton({text:'上传',plain:true,iconCls:'icon-search'}); 
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
		return '<a class="preScan1" href="#" iconCls="icon-save" onclick="upload('+ row.id +')">上传</a>';
	}
	function upload(noticeid){
		es1="上传图片";
		 $('#dlg1').dialog('open').dialog('setTitle',es1);
	     $('#fm1').form('clear');
	     $('#noticeid').val(noticeid);
	}
	$(function() {
		loadGrid('${pageContext.request.contextPath}/notice/getAllNoticeByPage',null);
		// 确认按钮
		$("#btn_ok").click(function(){
// 			textbox("getText")
				var linkurl = $("#linkurl").val();
        	    var content=$("#content").val();
        	    $.post("${pageContext.request.contextPath}/notice/countAvailable",null, function(data) {
					if(data>0){
						alert("请先关闭可用的！");
					}else{
						if(es=="添加"){
							var param = {content:content,linkurl:linkurl};
							$.post("${pageContext.request.contextPath}/notice/insertNotice",param, function(result) {
								$("#dlg").dialog("close");
								$('#llbinfo').datagrid('reload');
							});
							
						}else{
							var row = $('#llbinfo').datagrid('getSelected');
							var param = {id:row.id,content:content,linkurl:linkurl};
							$.post("${pageContext.request.contextPath}/notice/updateNotice",param, function(result) {
								$("#dlg").dialog("close");
								$('#llbinfo').datagrid('reload');
							});
						}
					}
				});
			
			
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
				url: '${pageContext.request.contextPath}/notice/uploadFile', 
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
		    $("#content").textbox("setText",row.content);
			$("#linkurl").val(row.linkurl);
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     $("#linkurl").val("否");
	}
	function deleteNotice(){
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			var param = {id:row.id};
			$.post("${pageContext.request.contextPath}/notice/deleteNotice",param, function(result) {
				$("#dlg").dialog("close");
				$('#llbinfo').datagrid('reload');
			});
		}else{
			alert("请选择要删除的数据！");
		}
		
	}
	function searchNotice(){
		es = "查询";
		var param = {title:$("#title").val()};
		loadGrid('${pageContext.request.contextPath}/notice/searchNotice',param);
	}

</script>
</head>
<body>	<!-- 公告编辑表 -->
	<div id="dlg" class="easyui-dialog" style="width:400px;height:200px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>描述</label></td>
						<td><input id="content" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>是否可用</label></td>
						<td>
						<select id="linkurl" style="width: 220px;"><option
									value="是">是</option>
								<option value="否" selected>否</option></select></td>
<!-- 						<input id="linkurl" class="easyui-textbox" style="width: 220px;"></input> -->
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
						<input value='' id="noticeid" style="display:none" name="noticeid">
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
		<table id="llbinfo" class="easyui-datagrid"  title="微信公告信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.1),align:'center'" hidden="true">编号</th>
					<th
						data-options="field:'content',width:fixWidth(0.2),align:'center'">描述</th>
				 	<th
						data-options="field:'imgurl',width:fixWidth(0.5),align:'center'">图片链接</th>
						<th
						data-options="field:'linkurl',width:fixWidth(0.1),align:'center'">是否可用</th>
				    <th
					    data-options="field:'forUpload',width:fixWidth(0.2),formatter:uploadImage,align:'center'">上传图片</th>
				</tr>
			</thead>
		</table>  
	</div>
</body>
</html>