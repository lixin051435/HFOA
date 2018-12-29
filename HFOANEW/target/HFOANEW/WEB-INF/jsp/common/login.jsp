<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="../../../CSS/easyui.css">
<link rel="stylesheet" type="text/css" href="../../../CSS/icon.css">
<link rel="stylesheet" type="text/css" href="../../../CSS/demo.css">
<link rel="stylesheet" type="text/css" href="../../../CSS/color.css">
<!-- <script type="text/javascript" src="../../../CSS/jquery.min.js"></script> -->
<!-- <script type="text/javascript" src="../../../CSS/jquery.easyui.min.js"></script> -->
<!-- <script type="text/javascript" src="../../../CSS/formValidator-4.1.3.js"></script> -->
<!-- <script type="text/javascript" src="../../../CSS/formValidatorRegex.js" charset="UTF-8"></script> -->
<link rel="stylesheet" type="text/css" href="../../../CSS/loginstyle.css">
<link rel="stylesheet" type="text/css" href="../../../CSS/main.css">
<link rel="stylesheet" type="text/css" href="../../../CSS/login.css">
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
<title>信息化管理系统-登录</title>
<style type="text/css">
body {
	background:
		url(${pageContext.request.contextPath}/img/entertainImg/login.png);
 	background-size: cover;
	background-repeat: no-repeat;
	padding-top: 80px; 
}
.white {
  color:white;}
 
 .position{
  position:fixed;
  left:55%;
  top:28%;
 }
 
 .button{
 position:relative; 
 left:20%;
 }
.btnalink {
	cursor: hand;
	display: block;
	width: 80px;
	height: 29px;
	float: left;
	margin: 12px 28px 12px auto;
	line-height: 22px;
	background:
		url('${pageContext.request.contextPath}/img/entertainImg/btnbg.jpg')
		no-repeat;
	font-size: 14px;
	color: #fff;
	font-weight: bold;
	text-decoration: none;
}


.tittle {
	width: 516px;
	height: 55px;
	font: bold 55px/100% "微软雅黑", "Lucida Grande", "Lucida Sans", Helvetica,
		Arial, Sans; /*设置字体*/
	color: #fff; /*设置文字颜色*/
	text-transform: uppercase;
	text-shadow: black 0 2px 0; /*设置阴影效果*/
	margin: 0 auto; /*设置文字居中显示*/
	letter-spacing: 4px; /*增大文字间间距*/
	position: relative;
	bottom: 100px;
}
</style>

<script type="text/javascript">
	//登录提示方法
	function loginsubmit() {
		var username = document.getElementById("username").value;
		var password = document.getElementById("pwd").value;
		if (username == "") {
			alert("用户名不能为空");
			return false;
		}
		if (password == "") {
			alert("密码不能为空");
			return false;
		}
		//$("#loginform").submit();
		var param = "username=" + username + "&password=" + password;
		$.post(
		"${pageContext.request.contextPath}/login/login", param, function(result) {
			/* if (result == "9") {
				location.href = '${pageContext.request.contextPath}/view/entertain/firstZ.jsp';
			} 
			else if (result == "2") {
				location.href = '${pageContext.request.contextPath}/view/entertain/firstL.jsp';
			} 
			else if(result == "3"){
				location.href = '${pageContext.request.contextPath}/view/entertain/firstG.jsp';	
			}
			else  if(result == "0"){
				$.messager.alert('提示信息', '用户名或密码错误！');
			}
			else{
				location.href = '${pageContext.request.contextPath}/view/entertain/first.jsp';
			} */
			 if(result == "0"){
				$.messager.alert('提示信息', '用户名或密码错误！');
			}else{
				location.href = '${pageContext.request.contextPath}/login/mainPage';
			} 
		});
	}
	
	//判断是否敲击了Enter键 
	$(function() {
		$("#pwd").keydown(function(event) {
			if (event.keyCode == 13) {
				loginsubmit();
			}
		});
	})
</SCRIPT>
</head>
<body>
	<form id="loginform" name="loginform">
			<div class="position">
				<table border="0" cellSpacing="20" cellPadding="8">
					<tbody>
						<tr>
							<td class="white">用&nbsp户</td>
							<td><input type="text" id="username" name="username"
								style=" background-color:transparent;color:white " /></td>
						</tr>
						<tr>
							<td class="white">密&nbsp码</td>
							<td><input type="password" id="pwd" name="password"
								style=" background-color:transparent;color:white" /></td>
						</tr>
			<!-- 		</tbody>
				</table>
				<table>
					<tbody> -->
						<tr >
							<td class="button" colSpan="2" ><input type="button"
								 onclick="loginsubmit()"
								value="登&nbsp;&nbsp;录>>" /></td>
						</tr>
					</tbody>
				</table>
			</div>
	</form>
</body>
</html>