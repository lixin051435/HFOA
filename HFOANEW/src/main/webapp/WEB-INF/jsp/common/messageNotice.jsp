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
<link rel="stylesheet" href="${pageContext.request.contextPath}/kindeditor/themes/default/default.css" />
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/jquery.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/jquery.easyui.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/CSS/easyui-lang-zh_CN.js"></script>
	<script type="text/javascript"
	src="${pageContext.request.contextPath}/kindeditor/kindeditor-all.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/kindeditor/kindeditor-all-min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/kindeditor/lang/zh_CN.js"></script>
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
				}}
// 				,'-',{
// 				text : '名称：<input type="text" id="title"/>',
// 				}
// 				,'-',{
// 				text : '查询',
// 				iconCls : 'icon-search',
// 				handler : function() {
// 					searchNotice();
// 				}
// 			}
			],
			
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
	//介绍富文本编辑器
    KindEditor.ready(function(K) {
        introEditor = K.create("#content", {
            width : 100,
            minHeight : '300px',
            uploadJson : "${pageContext.request.contextPath}/notice/kindeditorUploadImg",
            items : [ 'source', '|', 'undo', 'redo', '|', 'preview', 'print',
                    'template', 'code', 'cut', 'copy', 'paste', 'plainpaste',
                    'wordpaste', '|', 'justifyleft', 'justifycenter',
                    'justifyright', 'justifyfull', 'insertorderedlist',
                    'insertunorderedlist', 'indent', 'outdent', 'subscript',
                    'superscript', 'clearhtml', 'quickformat', 'selectall',
                    '|', 'formatblock', 'fontname', 'fontsize', '|',
                    'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
                    'strikethrough', 'lineheight', 'removeformat', '|',
                    'image', 'flash', 'media', 'insertfile', 'table', 'hr',
                    'pagebreak', 'anchor', 'link', 'unlink', '|', 'about',
                    'fullscreen' ],
        });
    });
	$(function() {
		loadGrid('${pageContext.request.contextPath}/notice/getAllMessageNoticeByPage',null);
		// 确认按钮
		$("#btn_ok").click(function(){
				var mainTitle = $("#mainTitle").textbox("getText");
        	    var contentTitle=$("#contentTitle").textbox("getText");
        	    var content=introEditor.html();
			if(es=="添加"){
				var param = {maintitle:mainTitle,contenttitle:contentTitle,content:content};
				$.post("${pageContext.request.contextPath}/notice/insertMessagenotice",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
				
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				var param = {id:row.id,maintitle:mainTitle,contenttitle:contentTitle,content:content};
				$.post("${pageContext.request.contextPath}/notice/updateMessagenotice",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
			}
			
		});
		// 取消按钮
		$("#btn_cancel").click(function() {
			$('#dlg').dialog('close')
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
		    $("#content").val(row.content);
			$("#mainTitle").textbox("setText",row.maintitle);
			$("#contentTitle").textbox("setText",row.contenttitle);
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     //获取添加的主标题
// 	     $.post("${pageContext.request.contextPath}/notice/getMainTitle",param, function(result) {
// 				if(result!=null){
// 					$("#mainTitle").textbox("setText",result);
// 				}
// 		 });
	}
	function deleteNotice(){
		es = "删除";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			var param = {id:row.id};
			$.post("${pageContext.request.contextPath}/notice/deleteMessagenotice",param, function(result) {
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
	<div id="dlg" class="easyui-dialog" style="width:800px;height:600px;padding:0px 0px;"closed="true" buttons="#dlg-buttons">
        <!--<div>用户编辑</div>  -->
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin:auto;" cellspacing="10">
					<tr>
						<td><label>主标题</label></td>
						<td><input id="mainTitle" class="easyui-textbox" style="width: 300px;"></input></td>
					</tr>
					<tr>
						<td><label>内容标题</label></td>
						<td><input id="contentTitle" class="easyui-textbox" style="width: 300px;"></input></td>
					</tr>
					<tr>
						<td><label>内容</label></td>
						<td><textarea rows="20" style="width:400px;" id="content" name="content" class="easyui-validatebox" data-options="validType:'length[1,1000000]'" invalidMessage="最大长度不能超过1000000""></textarea></td>
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
		<table id="llbinfo" class="easyui-datagrid"  title="微信公告信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
					<th
						data-options="field:'id',width:fixWidth(0.1),align:'center'" hidden="true">编号</th>
					<th
						data-options="field:'maintitle',width:fixWidth(0.3),align:'center'">主标题</th>
				 	<th
						data-options="field:'contenttitle',width:fixWidth(0.3),align:'center'">内容标题</th>
				    <th
						data-options="field:'usertime',width:fixWidth(0.4),align:'center'">开始时间</th>
				</tr>
			</thead>
		</table>  
	</div>
</body>
</html>