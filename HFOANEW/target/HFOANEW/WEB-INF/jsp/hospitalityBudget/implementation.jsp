<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>业务招待费年度预算</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/img/entertainImg/favicon.ico">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/jqueryui/themes/icon.css">
<!-- <link rel="stylesheet" type="text/css" -->
<%-- 	href="${pageContext.request.contextPath}/CSS/jqueryui/demo/demo.css"> --%>
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
		$("#aentertainobject").combobox("setText", "全部");
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
	
	$(function() {
		//$(param.llbid) 传递过来得履历本id
		//汉化 datagrid 翻页
		$("#llbinfo").datagrid({
			url : '${pageContext.request.contextPath}/entertain/wGetNowAnnual',
			method : 'post',
			pageSize : 20,
			rownumbers : true,
			singleSelect : true,
			fit: true, //datagrid自适应宽度
            fitColumn: false, //列自适应宽度
			toolbar : "#tb",
			
			onLoadSuccess:function(data){ 
			 //初始值
				//console.log(data);
				 //初始值
				 var a1= parseFloat(data.rows[0].budgetSum0);
				 var a2= parseFloat(data.rows[1].budgetSum0);
				 var a3= parseFloat(data.rows[2].budgetSum0);
				 var a4= parseFloat(data.rows[3].budgetSum0);
				 var a5= parseFloat(data.rows[4].budgetSum0);
				 var a6= parseFloat(data.rows[5].budgetSum0);
				 var a7= parseFloat(data.rows[6].budgetSum0);
				 var a8= parseFloat(data.rows[7].budgetSum0);
				 var a9= parseFloat(data.rows[8].budgetSum0);
				 var a10=parseFloat(data.rows[9].budgetSum0);
				 var a11=parseFloat(data.rows[10].budgetSum0);
				 
				 //第1次调整
				 if(data.rows[0].budgetSum1==""){var b1=0;}
				 else{var b1= parseFloat(data.rows[0].budgetSum1);}
				 if(data.rows[1].budgetSum1==""){var b2=0;}
				 else{var b2= parseFloat(data.rows[1].budgetSum1);}
				 if(data.rows[2].budgetSum1==""){var b3=0;}
				 else{var b3= parseFloat(data.rows[2].budgetSum1);}
				 if(data.rows[3].budgetSum1==""){var b4=0;}
				 else{var b4= parseFloat(data.rows[3].budgetSum1);}
				 if(data.rows[4].budgetSum1==""){var b5=0;}
				 else{var b5= parseFloat(data.rows[4].budgetSum1);}
				 if(data.rows[5].budgetSum1==""){var b6=0;}
				 else{var b6= parseFloat(data.rows[5].budgetSum1);}
				 if(data.rows[6].budgetSum1==""){var b7=0;}
				 else{var b7= parseFloat(data.rows[6].budgetSum1);}
				 if(data.rows[7].budgetSum1==""){var b8=0;}
				 else{var b8= parseFloat(data.rows[7].budgetSum1);}
				 if(data.rows[8].budgetSum1==""){var b9=0;}
				 else{var b9= parseFloat(data.rows[8].budgetSum1);}
				 if(data.rows[9].budgetSum1==""){var b10=0;}
				 else{var b10=parseFloat(data.rows[9].budgetSum1);}
				 if(data.rows[10].budgetSum1==""){var b11=0;}
				 else{var b11=parseFloat(data.rows[10].budgetSum1);}
				 
				 //第2次调整
				 if(data.rows[0].budgetSum2==""){var c1=0;}
				 else{var c1= parseFloat(data.rows[0].budgetSum2);}
				 if(data.rows[1].budgetSum2==""){var c2=0;}
				 else{var c2= parseFloat(data.rows[1].budgetSum2);}
				 if(data.rows[2].budgetSum2==""){var c3=0;}
				 else{var c3= parseFloat(data.rows[2].budgetSum2);}
				 if(data.rows[3].budgetSum2==""){var c4=0;}
				 else{var c4= parseFloat(data.rows[3].budgetSum2);}
				 if(data.rows[4].budgetSum2==""){var c5=0;}
				 else{var c5= parseFloat(data.rows[4].budgetSum2);}
				 if(data.rows[5].budgetSum2==""){var c6=0;}
				 else{var c6= parseFloat(data.rows[5].budgetSum2);}
				 if(data.rows[6].budgetSum2==""){var c7=0;}
				 else{var c7= parseFloat(data.rows[6].budgetSum2);}
				 if(data.rows[7].budgetSum2==""){var c8=0;}
				 else{var c8= parseFloat(data.rows[7].budgetSum2);}
				 if(data.rows[8].budgetSum2==""){var c9=0;}
				 else{var c9= parseFloat(data.rows[8].budgetSum2);}
				 if(data.rows[9].budgetSum2==""){var c10=0;}
				 else{var c10= parseFloat(data.rows[9].budgetSum2);}
				 if(data.rows[10].budgetSum2==""){var c11=0;}
				 else{var c11= parseFloat(data.rows[10].budgetSum2);}
				 
				 //第3次调整
				 if(data.rows[0].budgetSum3==""){var d1=0;}
				 else{var d1= parseFloat(data.rows[0].budgetSum3);}
				 if(data.rows[1].budgetSum3==""){var d2=0;}
				 else{var d2= parseFloat(data.rows[1].budgetSum3);}
				 if(data.rows[2].budgetSum3==""){var d3=0;}
				 else{var d3= parseFloat(data.rows[2].budgetSum3);}
				 if(data.rows[3].budgetSum3==""){var d4=0;}
				 else{var d4= parseFloat(data.rows[3].budgetSum3);}
				 if(data.rows[4].budgetSum3==""){var d5=0;}
				 else{var d5= parseFloat(data.rows[4].budgetSum3);}
				 if(data.rows[5].budgetSum3==""){var d6=0;}
				 else{var d6= parseFloat(data.rows[5].budgetSum3);}
				 if(data.rows[6].budgetSum3==""){var d7=0;}
				 else{var d7= parseFloat(data.rows[6].budgetSum3);}
				 if(data.rows[7].budgetSum3==""){var d8=0;}
				 else{var d8= parseFloat(data.rows[7].budgetSum3);}
				 if(data.rows[8].budgetSum3==""){var d9=0;}
				 else{var d9= parseFloat(data.rows[8].budgetSum3);}
				 if(data.rows[9].budgetSum3==""){var d10=0;}
				 else{var d10= parseFloat(data.rows[9].budgetSum3);}
				 if(data.rows[10].budgetSum3==""){var d11=0;}
				 else{var d11= parseFloat(data.rows[10].budgetSum3);}
				 
				 //第4次调整
				 if(data.rows[0].budgetSum4==""){var e1=0;}
				 else{var e1= parseFloat(data.rows[0].budgetSum4);}
				 if(data.rows[1].budgetSum4==""){var e2=0;}
				 else{var e2= parseFloat(data.rows[1].budgetSum4);}
				 if(data.rows[2].budgetSum4==""){var e3=0;}
				 else{var e3= parseFloat(data.rows[2].budgetSum4);}
				 if(data.rows[3].budgetSum4==""){var e4=0;}
				 else{var e4= parseFloat(data.rows[3].budgetSum4);}
				 if(data.rows[4].budgetSum4==""){var e5=0;}
				 else{var e5= parseFloat(data.rows[4].budgetSum4);}
				 if(data.rows[5].budgetSum4==""){var e6=0;}
				 else{var e6= parseFloat(data.rows[5].budgetSum4);}
				 if(data.rows[6].budgetSum4==""){var e7=0;}
				 else{var e7= parseFloat(data.rows[6].budgetSum4);}
				 if(data.rows[7].budgetSum4==""){var e8=0;}
				 else{var e8= parseFloat(data.rows[7].budgetSum4);}
				 if(data.rows[8].budgetSum4==""){var e9=0;}
				 else{var e9= parseFloat(data.rows[8].budgetSum4);}
				 if(data.rows[9].budgetSum4==""){var e10=0;}
				 else{var e10= parseFloat(data.rows[9].budgetSum4);}
				 if(data.rows[10].budgetSum4==""){var e11=0;}
				 else{var e11= parseFloat(data.rows[10].budgetSum4);}
				 
				 //第5次调整
				 if(data.rows[0].budgetSum5==""){var f1=0;}
				 else{var f1= parseFloat(data.rows[0].budgetSum5);}
				 if(data.rows[1].budgetSum5==""){var f2=0;}
				 else{var f2= parseFloat(data.rows[1].budgetSum5);}
				 if(data.rows[2].budgetSum5==""){var f3=0;}
				 else{var f3= parseFloat(data.rows[2].budgetSum5);}
				 if(data.rows[3].budgetSum5==""){var f4=0;}
				 else{var f4= parseFloat(data.rows[3].budgetSum5);}
				 if(data.rows[4].budgetSum5==""){var f5=0;}
				 else{var f5= parseFloat(data.rows[4].budgetSum5);}
				 if(data.rows[5].budgetSum5==""){var f6=0;}
				 else{var f6= parseFloat(data.rows[5].budgetSum5);}
				 if(data.rows[6].budgetSum5==""){var f7=0;}
				 else{var f7= parseFloat(data.rows[6].budgetSum5);}
				 if(data.rows[7].budgetSum5==""){var f8=0;}
				 else{var f8= parseFloat(data.rows[7].budgetSum5);}
				 if(data.rows[8].budgetSum5==""){var f9=0;}
				 else{var f9= parseFloat(data.rows[8].budgetSum5);}
				 if(data.rows[9].budgetSum5==""){var f10=0;}
				 else{var f10= parseFloat(data.rows[9].budgetSum5);}
				 if(data.rows[10].budgetSum5==""){var f11=0;}
				 else{var f11= parseFloat(data.rows[10].budgetSum5);}
			
				 var s1 = a1+b1+c1+d1+e1+f1;
				 var s2 = a2+b2+c2+d2+e2+f2;
				 var s3 = a3+b3+c3+d3+e3+f3;
				 var s4 = a4+b4+c4+d4+e4+f4;
				 var s5 = a5+b5+c5+d5+e5+f5;
				 var s6 = a6+b6+c6+d6+e6+f6;
				 var s7 = a7+b7+c7+d7+e7+f7;
				 var s8 = a8+b8+c8+d8+e8+f8;
				 var s9 = a9+b9+c9+d9+e9+f9;
				 var s10=a10+b10+c10+d10+e10+f10;
				 var s11=a11+b11+c11+d11+e11+f11;

				 console.log(s1);
				 
				 // 调整后预算
				 $("#a0").html(s1);
				 $("#a1").html(s2);
				 $("#a2").html(s3);
				 $("#a3").html(s4);
				 $("#a4").html(s5);
				 $("#a5").html(s6);
				 $("#a6").html(s7);
				 $("#a7").html(s8);
				 $("#a8").html(s9);
				 $("#a9").html(s10);
				 $("#a10").html(s11);
				 
				 // 发生额
				 //$.post("${pageContext.request.contextPath}/entertain/wGetAllUsedNow", // 获得每个部门事后登记表的总金额
				$.post("${pageContext.request.contextPath}/entertain/wGetSelectedUsed",{year:$('#annual').combobox("getText")}, // 获得每个部门事后登记表的总金额
					function(result) {
					console.log(result);
				 var u1= parseFloat(result[data.rows[0].department])/10000;
			     var u2= parseFloat(result[data.rows[1].department])/10000;
				 var u3= parseFloat(result[data.rows[2].department])/10000;
				 var u4= parseFloat(result[data.rows[3].department])/10000;
				 var u5= parseFloat(result[data.rows[4].department])/10000;
				 var u6= parseFloat(result[data.rows[5].department])/10000;
				 var u7= parseFloat(result[data.rows[6].department])/10000;
				 var u8= parseFloat(result[data.rows[7].department])/10000;
				 var u9= parseFloat(result[data.rows[8].department])/10000;
				 var u10=parseFloat(result[data.rows[9].department])/10000;
				 var u11=parseFloat(result[data.rows[10].department])/10000;
									
				$("#u0").html(fomatFloat1(u1,1));
				$("#u1").html(fomatFloat1(u2,1));
				$("#u2").html(fomatFloat1(u3,1));
				$("#u3").html(fomatFloat1(u4,1));
				$("#u4").html(fomatFloat1(u5,1));
				$("#u5").html(fomatFloat1(u6,1));
				$("#u6").html(fomatFloat1(u7,1));
				$("#u7").html(fomatFloat1(u8,1));
				$("#u8").html(fomatFloat1(u9,1));
				$("#u9").html(fomatFloat1(u10,1));
				$("#u10").html(fomatFloat1(u11,1));
				// 执行率
			 	var p1 = toPercent(u1/s1);
			 	var p2 = toPercent(u2/s2);
			 	var p3 = toPercent(u3/s3);
			 	var p4 = toPercent(u4/s4);
			 	var p5 = toPercent(u5/s5);
			 	var p6 = toPercent(u6/s6);
			 	var p7 = toPercent(u7/s7);
			 	var p8 = toPercent(u8/s8);
			 	var p9 = toPercent(u9/s9);
			 	var p10 = toPercent(u10/s10);
			 	var p11 = toPercent(u11/s11);
			 	
				$("#p0").html(p1);
				$("#p1").html(p2);
				$("#p2").html(p3);
				$("#p3").html(p4);
				$("#p4").html(p5);
				$("#p5").html(p6);
				$("#p6").html(p7);
				$("#p7").html(p8);
				$("#p8").html(p9);
				$("#p9").html(p10);
				$("#p10").html(p11);
								
				});
			 
			 
			}
		});

		// 给combobox赋初值
		$("#annual").combobox({
			onLoadSuccess: function(){
				var now = new Date();
       			var nowd = new Date().Format("yyyy-MM-dd");
       			var ny = nowd.substring(0,4);
				$("#annual").combobox('setValue',ny);
			}
		});

		
		$("#annual").combobox({
			onSelect: function(){
							var year = $("#annual").combobox('getText'); 
							//alert(year);
							var param = "year="+year;
							console.log("year="+year);
							$("#llbinfo").datagrid({
								url : '${pageContext.request.contextPath}/entertain/wGetNowAnnual1?year='+year,
								method : 'post',
								pageSize : 20,
								rownumbers : true,
								singleSelect : true,
								fit: true, //datagrid自适应宽度
					            fitColumn: false, //列自适应宽度
								toolbar : "#tb",
								
								onLoadSuccess:function(data){ 
									 //初始值
									 var a1= parseFloat(data.rows[0].budgetSum0);
									 var a2= parseFloat(data.rows[1].budgetSum0);
									 var a3= parseFloat(data.rows[2].budgetSum0);
									 var a4= parseFloat(data.rows[3].budgetSum0);
									 var a5= parseFloat(data.rows[4].budgetSum0);
									 var a6= parseFloat(data.rows[5].budgetSum0);
									 var a7= parseFloat(data.rows[6].budgetSum0);
									 var a8= parseFloat(data.rows[7].budgetSum0);
									 var a9= parseFloat(data.rows[8].budgetSum0);
									 var a10=parseFloat(data.rows[9].budgetSum0);
									 var a11=parseFloat(data.rows[10].budgetSum0);
									 
									 //第1次调整
									 if(data.rows[0].budgetSum1==""){var b1=0;}
									 else{var b1= parseFloat(data.rows[0].budgetSum1);}
									 if(data.rows[1].budgetSum1==""){var b2=0;}
									 else{var b2= parseFloat(data.rows[1].budgetSum1);}
									 if(data.rows[2].budgetSum1==""){var b3=0;}
									 else{var b3= parseFloat(data.rows[2].budgetSum1);}
									 if(data.rows[3].budgetSum1==""){var b4=0;}
									 else{var b4= parseFloat(data.rows[3].budgetSum1);}
									 if(data.rows[4].budgetSum1==""){var b5=0;}
									 else{var b5= parseFloat(data.rows[4].budgetSum1);}
									 if(data.rows[5].budgetSum1==""){var b6=0;}
									 else{var b6= parseFloat(data.rows[5].budgetSum1);}
									 if(data.rows[6].budgetSum1==""){var b7=0;}
									 else{var b7= parseFloat(data.rows[6].budgetSum1);}
									 if(data.rows[7].budgetSum1==""){var b8=0;}
									 else{var b8= parseFloat(data.rows[7].budgetSum1);}
									 if(data.rows[8].budgetSum1==""){var b9=0;}
									 else{var b9= parseFloat(data.rows[8].budgetSum1);}
									 if(data.rows[9].budgetSum1==""){var b10=0;}
									 else{var b10=parseFloat(data.rows[9].budgetSum1);}
									 if(data.rows[10].budgetSum1==""){var b11=0;}
									 else{var b11=parseFloat(data.rows[10].budgetSum1);}
									 
									 //第2次调整
									 if(data.rows[0].budgetSum2==""){var c1=0;}
									 else{var c1= parseFloat(data.rows[0].budgetSum2);}
									 if(data.rows[1].budgetSum2==""){var c2=0;}
									 else{var c2= parseFloat(data.rows[1].budgetSum2);}
									 if(data.rows[2].budgetSum2==""){var c3=0;}
									 else{var c3= parseFloat(data.rows[2].budgetSum2);}
									 if(data.rows[3].budgetSum2==""){var c4=0;}
									 else{var c4= parseFloat(data.rows[3].budgetSum2);}
									 if(data.rows[4].budgetSum2==""){var c5=0;}
									 else{var c5= parseFloat(data.rows[4].budgetSum2);}
									 if(data.rows[5].budgetSum2==""){var c6=0;}
									 else{var c6= parseFloat(data.rows[5].budgetSum2);}
									 if(data.rows[6].budgetSum2==""){var c7=0;}
									 else{var c7= parseFloat(data.rows[6].budgetSum2);}
									 if(data.rows[7].budgetSum2==""){var c8=0;}
									 else{var c8= parseFloat(data.rows[7].budgetSum2);}
									 if(data.rows[8].budgetSum2==""){var c9=0;}
									 else{var c9= parseFloat(data.rows[8].budgetSum2);}
									 if(data.rows[9].budgetSum2==""){var c10=0;}
									 else{var c10= parseFloat(data.rows[9].budgetSum2);}
									 if(data.rows[10].budgetSum2==""){var c11=0;}
									 else{var c11= parseFloat(data.rows[10].budgetSum2);}
									 
									 //第3次调整
									 if(data.rows[0].budgetSum3==""){var d1=0;}
									 else{var d1= parseFloat(data.rows[0].budgetSum3);}
									 if(data.rows[1].budgetSum3==""){var d2=0;}
									 else{var d2= parseFloat(data.rows[1].budgetSum3);}
									 if(data.rows[2].budgetSum3==""){var d3=0;}
									 else{var d3= parseFloat(data.rows[2].budgetSum3);}
									 if(data.rows[3].budgetSum3==""){var d4=0;}
									 else{var d4= parseFloat(data.rows[3].budgetSum3);}
									 if(data.rows[4].budgetSum3==""){var d5=0;}
									 else{var d5= parseFloat(data.rows[4].budgetSum3);}
									 if(data.rows[5].budgetSum3==""){var d6=0;}
									 else{var d6= parseFloat(data.rows[5].budgetSum3);}
									 if(data.rows[6].budgetSum3==""){var d7=0;}
									 else{var d7= parseFloat(data.rows[6].budgetSum3);}
									 if(data.rows[7].budgetSum3==""){var d8=0;}
									 else{var d8= parseFloat(data.rows[7].budgetSum3);}
									 if(data.rows[8].budgetSum3==""){var d9=0;}
									 else{var d9= parseFloat(data.rows[8].budgetSum3);}
									 if(data.rows[9].budgetSum3==""){var d10=0;}
									 else{var d10= parseFloat(data.rows[9].budgetSum3);}
									 if(data.rows[10].budgetSum3==""){var d11=0;}
									 else{var d11= parseFloat(data.rows[10].budgetSum3);}
									 
									 //第4次调整
									 if(data.rows[0].budgetSum4==""){var e1=0;}
									 else{var e1= parseFloat(data.rows[0].budgetSum4);}
									 if(data.rows[1].budgetSum4==""){var e2=0;}
									 else{var e2= parseFloat(data.rows[1].budgetSum4);}
									 if(data.rows[2].budgetSum4==""){var e3=0;}
									 else{var e3= parseFloat(data.rows[2].budgetSum4);}
									 if(data.rows[3].budgetSum4==""){var e4=0;}
									 else{var e4= parseFloat(data.rows[3].budgetSum4);}
									 if(data.rows[4].budgetSum4==""){var e5=0;}
									 else{var e5= parseFloat(data.rows[4].budgetSum4);}
									 if(data.rows[5].budgetSum4==""){var e6=0;}
									 else{var e6= parseFloat(data.rows[5].budgetSum4);}
									 if(data.rows[6].budgetSum4==""){var e7=0;}
									 else{var e7= parseFloat(data.rows[6].budgetSum4);}
									 if(data.rows[7].budgetSum4==""){var e8=0;}
									 else{var e8= parseFloat(data.rows[7].budgetSum4);}
									 if(data.rows[8].budgetSum4==""){var e9=0;}
									 else{var e9= parseFloat(data.rows[8].budgetSum4);}
									 if(data.rows[9].budgetSum4==""){var e10=0;}
									 else{var e10= parseFloat(data.rows[9].budgetSum4);}
									 if(data.rows[10].budgetSum4==""){var e11=0;}
									 else{var e11= parseFloat(data.rows[10].budgetSum4);}
									 
									 //第5次调整
									 if(data.rows[0].budgetSum5==""){var f1=0;}
									 else{var f1= parseFloat(data.rows[0].budgetSum5);}
									 if(data.rows[1].budgetSum5==""){var f2=0;}
									 else{var f2= parseFloat(data.rows[1].budgetSum5);}
									 if(data.rows[2].budgetSum5==""){var f3=0;}
									 else{var f3= parseFloat(data.rows[2].budgetSum5);}
									 if(data.rows[3].budgetSum5==""){var f4=0;}
									 else{var f4= parseFloat(data.rows[3].budgetSum5);}
									 if(data.rows[4].budgetSum5==""){var f5=0;}
									 else{var f5= parseFloat(data.rows[4].budgetSum5);}
									 if(data.rows[5].budgetSum5==""){var f6=0;}
									 else{var f6= parseFloat(data.rows[5].budgetSum5);}
									 if(data.rows[6].budgetSum5==""){var f7=0;}
									 else{var f7= parseFloat(data.rows[6].budgetSum5);}
									 if(data.rows[7].budgetSum5==""){var f8=0;}
									 else{var f8= parseFloat(data.rows[7].budgetSum5);}
									 if(data.rows[8].budgetSum5==""){var f9=0;}
									 else{var f9= parseFloat(data.rows[8].budgetSum5);}
									 if(data.rows[9].budgetSum5==""){var f10=0;}
									 else{var f10= parseFloat(data.rows[9].budgetSum5);}
									 if(data.rows[10].budgetSum5==""){var f11=0;}
									 else{var f11= parseFloat(data.rows[10].budgetSum5);}
								
									 var s1 = a1+b1+c1+d1+e1+f1;
									 var s2 = a2+b2+c2+d2+e2+f2;
									 var s3 = a3+b3+c3+d3+e3+f3;
									 var s4 = a4+b4+c4+d4+e4+f4;
									 var s5 = a5+b5+c5+d5+e5+f5;
									 var s6 = a6+b6+c6+d6+e6+f6;
									 var s7 = a7+b7+c7+d7+e7+f7;
									 var s8 = a8+b8+c8+d8+e8+f8;
									 var s9 = a9+b9+c9+d9+e9+f9;
									 var s10=a10+b10+c10+d10+e10+f10;
									 var s11=a11+b11+c11+d11+e11+f11;
									 
// 									 alert("a6="+a6);
// 									 alert("s6="+s6);
// 									 alert("b6="+b6);
// 									 alert("c6="+c6);
// 									 alert("d6="+d6);
// 									 alert("e10="+e10);
// 									 alert("f10="+f10);
// 									 alert("s10="+s10);
									 
									 // 调整后预算
									 $("#a0").html(s1);
									 $("#a1").html(s2);
									 $("#a2").html(s3);
									 $("#a3").html(s4);
									 $("#a4").html(s5);
									 $("#a5").html(s6);
									 $("#a6").html(s7);
									 $("#a7").html(s8);
									 $("#a8").html(s9);
									 $("#a9").html(s10);
									 $("#a10").html(s11);
									 
									 // 发生额
									 //$.post("${pageContext.request.contextPath}/entertain/wGetAllUsedNow", // 获得每个部门事后登记表的总金额
									$.post("${pageContext.request.contextPath}/entertain/wGetSelectedUsed",{year:$('#annual').combobox("getText")}, // 获得每个部门事后登记表的总金额
										function(result) {
										console.log(result);
									 var u1= parseFloat(result[data.rows[0].department])/10000;
								     var u2= parseFloat(result[data.rows[1].department])/10000;
									 var u3= parseFloat(result[data.rows[2].department])/10000;
									 var u4= parseFloat(result[data.rows[3].department])/10000;
									 var u5= parseFloat(result[data.rows[4].department])/10000;
									 var u6= parseFloat(result[data.rows[5].department])/10000;
									 var u7= parseFloat(result[data.rows[6].department])/10000;
									 var u8= parseFloat(result[data.rows[7].department])/10000;
									 var u9= parseFloat(result[data.rows[8].department])/10000;
									 var u10=parseFloat(result[data.rows[9].department])/10000;
									 var u11=parseFloat(result[data.rows[10].department])/10000;
																			 
														
									$("#u0").html(fomatFloat1(u1,1));
									$("#u1").html(fomatFloat1(u2,1));
									$("#u2").html(fomatFloat1(u3,1));
									$("#u3").html(fomatFloat1(u4,1));
									$("#u4").html(fomatFloat1(u5,1));
									$("#u5").html(fomatFloat1(u6,1));
									$("#u6").html(fomatFloat1(u7,1));
									$("#u7").html(fomatFloat1(u8,1));
									$("#u8").html(fomatFloat1(u9,1));
									$("#u9").html(fomatFloat1(u10,1));
									$("#u10").html(fomatFloat1(u11,1));
									console.log("------------------------------");
									console.log(fomatFloat1(u1,1));
									console.log(fomatFloat1(u2,1));
									console.log(fomatFloat1(u3,1));
									console.log(fomatFloat1(u4,1));
									console.log(fomatFloat1(u5,1));
									console.log(fomatFloat1(u6,1));
									console.log(fomatFloat1(u7,1));
									console.log(fomatFloat1(u8,1));
									console.log(fomatFloat1(u9,1));
									console.log(fomatFloat1(u10,1));
									console.log(fomatFloat1(u11,1));
									console.log("------------------------------");
									// 执行率
								 	var p1 = toPercent(u1/s1);
								 	var p2 = toPercent(u2/s2);
								 	var p3 = toPercent(u3/s3);
								 	var p4 = toPercent(u4/s4);
								 	var p5 = toPercent(u5/s5);
								 	var p6 = toPercent(u6/s6);
								 	var p7 = toPercent(u7/s7);
								 	var p8 = toPercent(u8/s8);
								 	var p9 = toPercent(u9/s9);
								 	var p10 = toPercent(u10/s10);
								 	var p11 = toPercent(u11/s11);
								 	console.log("p11");
								 	console.log(p11);
									$("#p0").html(p1);
									$("#p1").html(p2);
									$("#p2").html(p3);
									$("#p3").html(p4);
									$("#p4").html(p5);
									$("#p5").html(p6);
									$("#p6").html(p7);
									$("#p7").html(p8);
									$("#p8").html(p9);
									$("#p9").html(p10);	
									$("#p10").html(p11);
									});
								}
							});
						}
					});

				});
	function fomatFloat1(src,pos){   
	       return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);   
	    }
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	
	$('#aentertainobject').combobox({
		filter: function(q, row){
			var opts = $(this).combobox('options');
			return row[opts.textField].indexOf(q) == 0;
		}
	});
	
	/* function percentNum(num,num2) {
		return (Math.round(num / num2 * 10000) / 100.00 + "%"); //小数点后两位百分比
		}
	 */
	// 小数转百分数（保留两位小数）
	function toPercent(point){
// 	    var str=Number(point*100).toFixed(4);
		var str= fomatFloat1(point*100,1);
	    str+="%";
	    return str;
	}
	//这块ID不唯一所以有问题
 	// 调整后预算
	function adjustSumOper(value,row,rowIndex){
		return '<span id="a'+rowIndex+'"></span>';
	}  
 	// 发生额
	function usedSumOper(value,row,rowIndex){
		return '<span id="u'+rowIndex+'"></span>';
	}  
	// 执行率
	function percentOper(value,row,rowIndex){
		return '<span id="p'+rowIndex+'"></span>';
	}  
	
</script>
</head>
<body>
	<div class="mdiv" style="width:100%;height:400px;">
		<table id="llbinfo" class="easyui-datagrid" title="业务招待费年度预算"
			 style="width: auto; height:100%;">
			<thead>
				<tr>
				 	<th 
						data-options="field:'department',width:fixWidth(0.1),align:'center'">部门</th>
					<th 
						data-options="field:'adjustSum',width:fixWidth(0.1),formatter:adjustSumOper,align:'center'">当前预算（万元）</th>
					<th 
						data-options="field:'order',hidden:true">当前预算（万元）</th>
					<th
						data-options="field:'usedSum',width:fixWidth(0.09),formatter:usedSumOper,align:'center'">发生额（万元）</th>
					<th
						data-options="field:'percent',width:fixWidth(0.08),formatter:percentOper,align:'center'">执行率（%）</th>
					<th
						data-options="field:'budgetSum0',width:fixWidth(0.06),align:'center'">初始预算</th>
					<th
						data-options="field:'budgetSum1',width:fixWidth(0.07),align:'center'">第1次调整</th>
					<th 
						data-options="field:'copileTime1',width:fixWidth(0.08),align:'center'">调整日期</th>
					<th 
						data-options="field:'budgetSum2',width:fixWidth(0.07),align:'center'">第2次调整</th>
					<th
						data-options="field:'copileTime2',width:fixWidth(0.08),align:'center'">调整日期</th>
					<th
						data-options="field:'budgetSum3',width:fixWidth(0.07),align:'center'">第3次调整</th>
					<th
						data-options="field:'copileTime3',width:fixWidth(0.08),align:'center'">调整日期</th>
					<th
						data-options="field:'budgetSum4',width:fixWidth(0.07),align:'center'">第4次调整</th>
					<th
						data-options="field:'copileTime4',width:fixWidth(0.08),align:'center'">调整日期</th>
					<th
						data-options="field:'budgetSum5',width:fixWidth(0.07),align:'center'">第5次调整</th>
					<th
						data-options="field:'copileTime5',width:fixWidth(0.08),align:'center'">调整日期</th>
					
				</tr>
			</thead>
		</table> 
	</div>
	
	<div id="tb" style="padding:5px;height:auto">
<div style="margin-bottom:5px">
<lable>年度</lable>
<input id="annual" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/entertain/getYear',
							method:'get',
							valueField:'id',
							textField:'year',
							editable:false,
							panelHeight:'60'
							" style="width: 60px;"></input>
</div>
</div>

</body>
</html>