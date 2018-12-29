<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户管理</title>
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
document.oncontextmenu=function(e){return false;};
	$(function (){
		getTree();
		//取消按钮
		$('#cancelbtn').click(function(){
			$('#info').dialog('close');
			$('#myform').form('clear');
		});
	});
	function getTree(){
		$('#t1').tree({
			//发送异步ajax请求，还会携带id的参数
			url:'${pageContext.request.contextPath}/dictionary/getByNodeType',
			 onContextMenu:function(e,node){
				 console.log(node);
				//禁止浏览器的窗口打开
				e.preventDefault();
				//右键选中节点
				$(this).tree('select',node.target);
				$('#mm').menu('show',{
					left:e.pageX,
					top:e.pageY
				});
			}
		});
	}
	function append(){
		flag='add';
		$('#info').dialog('open').dialog('setTitle','增加');
		$('#myform').form('clear');
	}
	
	function editor(){
		flag='edit';
		//清空表单数据
		$('#myform').form('clear');
		//清空表单数据库，重新填充选中的节点里的id，name，url属性
		var node=$('#t1').tree('getSelected');
		$('#myform').form('load',{
			id:node.id,
			name:node.text,
			value:node.info
// 			mark:node.attributes.url
		});
		//打开dialog
		$('#info').dialog('open').dialog('setTitle','修改');
	}
	function saveType(){
		//对必填项进行判空
		var typeName=$('#typeName').val();
		if(typeName==''){
			alert("请输入名称");
			return;
			}
		if(flag=='add'){
			//1做前台更新
			//（1）获取所选中的节点，也就是父节点
			var node=$('#t1').tree('getSelected');
			//2后台同步更新
			$.ajax({
				type:'post',
				url:'${pageContext.request.contextPath}/dictionary/saveDict',
				cache:false,
				data:{
					id:node.id,
					text:$('#myform').find('input[name=name]').val(),
					info:$('#myform').find('input[name=value]').val(),
					parentId:node.id
				},
				dataType:'json',
				success:function(result){
					if(result=='1'){
						//重新加载
						var parent=$('#t1').tree('getParent',node.target);
						if(parent==null || parent==undefined){
							//重新加载一个树形结构
							getTree();
						}else{
							$('#t1').tree('reload',parent.target);
						}
						
						$.messager.show({
							title:'提示信息',
							msg:'添加成功！'
						});
					}else{
						$.messager.show({ // show error message
							title : 'Error',
							msg : result.msg
						});
					}
				}
			});
			//3关闭dialog
			$('#info').dialog('close');
			$('#myform').form('clear');
		}else{
			$.ajax({
				type:'post',
				url:'${pageContext.request.contextPath}/dictionary/updateDict',
				cache:false,
				data:{
					id:$('#myform').find('input[name=id]').val(),
					text:$('#myform').find('input[name=name]').val(),
					info:$('#myform').find('input[name=value]').val()
// 					url:$('#myform').find('input[name=mark]').val()
				},
				dataType:'json',
				success:function(result){
					if(result=='1'){
						//刷新节点，刷新选中节点的父亲
						var node=$('#t1').tree('getSelected');
						var parent=$('#t1').tree('getParent',node.target);
						$('#t1').tree('reload',parent.target);
						//提示信息
						$.messager.show({
							title:'提示信息',
							msg:'修改成功！'
						});
					}else{
						$.messager.show({ // show error message
							title : 'Error',
							msg : result.msg
						});
					}
				}
			});
		}
		//3关闭dialog
		$('#info').dialog('close');
		$('#myform').form('clear');
	}
	function removetree(){
		//前台删除
		var node=$('#t1').tree('getSelected');
		if(node){
			
			$.messager.confirm('确认对话框','你确定删除这条记录吗？',function(r){
			       if (r){
						$.post('${pageContext.request.contextPath}/dictionary/deleteDict',{id:node.id},function(result){
							//给出提示信息
							if(result=='1'){
								$('#t1').tree('remove',node.target);
								$.messager.show({
									title:'提示信息',
									msg:'删除成功！'
								});
							}else{
								//result==0判定该节点是否为根节点，若为根节点 ，无法删除
								$.messager.show({
									title:'提示信息',
									msg:'该节点为根目录，无法删除'
								});
							}
						},'text');
					}
				});
		}
	}
</script>
</head>
<body>
	<div class="mdiv" style="width:20%;height:94%;text-align:left;">
		<div data-options="iconCls:'icon-llb',heightStyle:'content'" style="overflow:auto;"> <!-- overflow:auto高度随着页面的高底而延伸 -->
			<ul id="t1" class="easyui-tree" style="width: 100%;"> 
				
			</ul>
		</div>
	</div>
	<div id="info" class="easyui-dialog" style="width:400px;height: 180px;" closed=true>
		<form id="myform" method="post">
			<input type="hidden" name="id" value="">
			<table style="margin: auto;" cellspacing="10">
				<tr>
					<td>名称</td>
					<td><input class="easyui-textbox" id="typeName" name="name" value="" data-options="required:true"></td>
				</tr>
				<tr>
					<td>值</td>
					<td><input class="easyui-textbox" id="typeValue" name="value" value="" ></td>
				</tr>
			</table>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a id="savebtn" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" onclick="saveType()">保存</a> <a
					id="cancelbtn" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'">取消</a>
			</div>
		</form>
	</div>
	<div id="mm" class="easyui-menu" style="width:130px;">
		<div data-options="iconCls:'icon-add'" onclick="append()">添 加</div>
		<div data-options="iconCls:'icon-edit'" onclick="editor()">修 改</div>
		<div data-options="iconCls:'icon-remove'" onclick="removetree()">删 除</div>
		<div class="menu-sep"></div> 
		<div  data-options="iconCls:'icon-undo'" onclick="exit()">退出</div>
	</div>
</body>
</html>