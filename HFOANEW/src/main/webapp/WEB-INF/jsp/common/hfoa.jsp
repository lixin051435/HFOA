<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
   <%--  <% 
    String username=(String)session.getAttribute("username");
    %> --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>信息化管理系统-首页</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/entertainImg/favicon.ico">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/jqueryui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/jqueryui/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/CSS/jqueryui/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/CSS/jqueryui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/CSS/jqueryui/locale/easyui-lang-zh_CN.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/style.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/main.css">

<script type="text/javascript">
/* var username=document.cookie.split("=")[0];
document.getElementById("username").innerHTML=username; */
//禁用鼠标右键
document.oncontextmenu=function(e){return false;};
//验证身份，未登录退回
// if('${username}'==''){
// 	location.href='${pageContext.request.contextPath}/login/loginPage';
// }
			//日期显示函数
	        function getweek(w) {
		        switch(w)
		        {
		        	case 0:
			        	return "星期日";
			        	break;
		        	case 1:
			        	return "星期一";
			        	break;
		        	case 2:
			        	return "星期二";
			        	break;
		        	case 3:
			        	return "星期三";
			        	break;
		        	case 4:
			        	return "星期四";
			        	break;
		        	case 5:
			        	return "星期五";
			        	break;
		        	case 6:
			        	return "星期六";
			        	break;
			        default:
				        return "日期错误";
			        	break;
		        }
		    }
	        //移除tabs
		     function removePanel(){ 
		            var tab = $('#tt').tabs('getSelected'); 
		            if (tab){ 
		                var index = $('#tt').tabs('getTabIndex', tab); 
		                $('#tt').tabs('close', index); 
		            } 
		            
		        } 				
	        //增加tabs	
			 function addPanel(url,title){ 
// 	        	url = "${pageContext.request.contextPath}"+url;
// 	        	console.log(url);'+url+'
					url="${pageContext.request.contextPath}/"+url;
				 	if($("#tt").is(":visible")){	
					 }
				 	else{
				 		$("#tt").css("display","block");
					 }
		            if(!$('#tt').tabs('exists', title)){ 
		                $('#tt').tabs('add',{ 
		                    title: title, 
		                    content: '<iframe scrolling="auto" src="'+url+'" frameBorder="0" border="0"  style="width: 100%; height: 100%;"/>', 
		                    closable: true 
		                }); 
		            }else{ 
		                $('#tt').tabs('select', title); 
		            } 
		           
		            
		        } 
			//构造菜单
		    function getTree(){
		    	 $.ajax({
                     type: 'get',
                     //尼玛 幸福来得太突然了。注意jquery ajax 请求路径问题
                     url: '/AirRecord3/getMenu',
                     cache: false,
                     success: function (data) {
                    	$('#llb').tree({   
                        checkbox: false,
                        data:eval(data),               
                        onClick:function(node){
                             var state=node.state;
                             //id：节点的 id
                             //text：节点的文字
                             //checked：节点是否被选中
                             //attributes：节点自定义属性
                             //target：被点击目标的 DOM 对象
                             //top 属性 自定义  定义树的结果级别
                             var content=node.text;
                             var len=content.length;
                             var level=node.top;
                             var key=node.key;
							 if(level=="3"){
								//最下一级的子菜单 飞机系统
								addPanel("LLBManage/llbsystem.jsp?planeSystem="+key,content);	
							 }
							 //飞机编号
							 else if(level=="2"){
								addPanel("LLBManage/llbplane.jsp?planeNum="+key,content);
							 }
							 //飞机类型动态
							 else{
								//顶级菜单加不加载再说
							 }
                        }
                    	});
                     },
                     
                 });
			}


		
			$(function(){
				//加载menu
				$.ajax({
                    type: 'post',
                    url: '${pageContext.request.contextPath}/login/showMenu',
                    cache: false,
                    success: function (result) {
//                     	var west="'west'";
//                     	var title="'导航菜单'";
//                     	var str='<div data-options="region:'+west+',split:true,title:'+title+'" style="width: 200px;"><div id="nav" class="easyui-accordion"style="width: auto; height: 100%;">';
                    	var lable="'icon-rz'";
                    	var str='';
    					if(result!=null){
    						for(var i=0;i<result.length;i++) {
    							 if(result[i].urlclass=='3'||result[i].urlclass=='1'){
    								 if(result[i].permissiontype=='fatherLevel'&&result[i].available=='1'){
    									 str+='<div title="'+result[i].permissionname+'" data-options="iconCls:'+lable+'" style="overflow:auto;"><ul id="" class="easyui-tree" style="width: 100%;height:100%;">';
    									 if(result[i].parentid==result[i].id&&result[i].urlclass=='1'){
    										 str+='<li><a href=javascript:addPanel("'+result[i].permissionurl+'","'+result[i].permissionname+'")>'+result[i].permissionname+'</a></li>'; 
    									 }
    									 str+='</ul></div>';
    								 }
    							 }
    						 }
//     						str+='</div></div>';
//     						alert(str)
    					$("#nav").append(str);
//     					$.parser.parse("#nav");

                    }
                    }
                });
				//登录页暂时I先这样  先弄好主页 
				var mydate=new Date();
				var week=mydate.getDay();
				var str=" "+mydate.getFullYear()+"年"+(mydate.getMonth()+1)+"月"+mydate.getDate()+"日";
				$("#timee").text(str+","+getweek(week));
				$("#user").text('${sessionScope.username}');
// 				$("#user").text('${username}');
			    //加载动态菜单列表
// 				getTree();
				//监听选项卡关闭事件 如果没有tabs 隐藏tab控件
				$("#tt").tabs({
					heightStyle : "auto",
					onClose:function(title,index){
						//如果没有tabs 隐藏tabs	
						if(!$('#tt').tabs('exists', 0)){
							 $("#tt").css("display","none");
						}
						else{
						  
						}
					},
					onAdd:function(title,index){
					}
				});
				   
				 
				//注销按钮
				$("#logout").click(function (){
					var result=confirm('您确定要注销吗?');
					if (result==true){
						location.href='${pageContext.request.contextPath}/login/loginPage';
					}else{}
				});
				
			});
			function logout(){
				var result=confirm('您确定要退出本系统吗?');
						if (result==true){
							location.href='${pageContext.request.contextPath}/login/loginPage';
						}else{
							//仍在该页面
						}
			}
		/* 	function exit(){
				var result=confirm('您确定要注销吗?');
				if (result==true){
					location.href='elogin.jsp';
				}else{
					//仍在该页面
				} */
	/* } */
			
	</script>
</head>
<body class="easyui-layout" id="body">
	<!-- 标题头部 -->
	<div data-options="region:'north',border:false" style="height: 120px;">
		<div class="bodyTop">
			<div>
				<div></div>
			</div>
		</div>
		<!-- 导航菜单 -->
		<div>
			<!-- 获取session -->
			<table style="width: 100%;">
				<tr>
					<%-- <td>欢迎您 <!--<s:property value=''  />--> ${activeUser.username}
						,今天是<label id="timee"></label>
					</td> --%>
					<td>欢迎您&nbsp&nbsp<lable id="user"></lable>&nbsp&nbsp&nbsp今天是<label id="timee"></label></td>
					<td style="text-align: right;"> 
						<a href="#" id="logout" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-logout'">注销</a>
						&nbsp;&nbsp; 
						<!-- <a href="javascript:logout()" id="logout" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-no'">退出</a></td> -->
				<a href="javascript:logout()" id="logout" class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-no'">退出</a></td>
				</tr>
			</table>
		</div> 
	</div>
	<!--导航菜单 -->
	<div data-options="region:'west',split:true,title:'导航菜单'"
		style="width: 200px;">
<!-- 		<!-- 树状菜单 -->  

		<div id="nav" class="easyui-accordion"
			style="width: auto; height: 100%;">
		<c:forEach items="${sessionScope.titles }" var="title">
			<c:if test="${title.urlclass=='3'||title.urlclass=='1'}">
			<c:if test="${title.permissiontype=='fatherLevel'&&title.available=='1'}">
				<div title="${title.permissionname }" data-options="iconCls:'icon-rz'" style="overflow:auto;">
					<ul id="" class="easyui-tree" style="width: 100%;height:100%;">
						<c:forEach items="${sessionScope.menus }" var="menu">
							<c:if test="${menu.parentid==title.id&&menu.urlclass=='1'}">
								<li><a href=javascript:addPanel("${menu.permissionurl }","${menu.permissionname }")>${menu.permissionname }</a>
								</li>
							</c:if>
						</c:forEach>
					</ul>
				</div>
			</c:if>
			</c:if>
		</c:forEach>
		</div>
	</div>
	<div id="info" class="easyui-dialog" style="width:400px;height: 180px;" closed=true>
		<form id="myform" method="post">
			<input type="hidden" name="id" value="">
			<table style="margin: auto;" cellspacing="10">
				<tr>
					<td>型号名称</td>
					<td><input class="easyui-textbox" name="name" value="" data-options="required:true"></td>
				</tr>
				<tr>
					<td>备注：</td>
					<td><input class="easyui-textbox" name="mark" value=""></td>
				</tr>
			</table>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a id="savebtn" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" style="width: 20%;">保存</a> <a
					id="cancelbtn" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
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
	

	<!-- 主要内容 -->
	<div id="funcarea" data-options="region:'center',title:'功能区'"
		style="height: 100%; width: 100%;">
		<div id="tt" data-options="fit:true,border:false,plain:true"
			class="easyui-tabs" style="display: none;"></div>
	</div>
	<!-- 底部 -->
	<div data-options="region:'south',border:false"
		style="height: 20px; background: #031652; text-align: center; margin-top: 2px;color:white;">
		海丰通航科技有限公司©2016All right reserved</div>

</body>
</html>