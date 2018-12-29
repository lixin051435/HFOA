<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>已审核招待明细列表</title>
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

	//批量登记
	var chk_value =[];
	function batchRegist(){
// 		chk_value =[];
// 		var rows = $('#llbinfo').datagrid('getSelections');
// 		if(rows.length==0){
// 			alert("请选择要报销的申请信息！");
// 		}else{
// 			var applyman=rows[0].applyMan;
// 			var sum=rows[0].sum;
// 			for(var i=0;i<rows.length;i++){
// 				//判断该申请是否已经登记
// 			if(rows[i].ifPerform!="已执行"){
// 					alert("请先修改执行状态为已执行！");
// 					return false;
// 			}else{
// 				if(rows[i].applyMan!=applyman){
// 					alert("请选择相同的申请人！");
// 					return false;
// 				}else{
// 				if(rows[i].sum!=""&&rows[i].sum!=sum){
// 					alert("已经存在报销过的数据");
// 					return false;
// 				}else{
// 					chk_value.push(rows[i].applyId);
// 				}
// 			}
// 			}
// 			}
		chk_value =[];
		var rows = $('#llbinfo').datagrid('getSelections');
		if(rows.length==0){
			alert("请选择要执行的申请信息！");
		}else{
			var applyman=rows[0].applyMan;
			var sum=rows[0].sum;
			var time=rows[0].paidTime;
			var approveman=rows[0].paidMan;
			var number=rows[0].voucherNum;
			for(var i=0;i<rows.length;i++){
				//判断该申请是否已经登记
			if(rows[i].applyMan!=applyman||rows[i].sum!=sum||rows[i].paidTime!=time||rows[i].paidMan!=approveman||rows[i].voucherNum!=number){
					alert("请选择信息一致的申请信息！");
					return false;
			}else{
				if(rows[i].applyMan!=applyman){
					alert("请选择相同的申请人！");
					return false;
				}else{
					$("#performApplyman").val(applyman);
					chk_value.push(rows[i].applyId);
			}
			}
			}
			 $('#form').form('clear');
			$('#save').dialog('open').dialog('setTitle', '审核单号');
			var now = new Date();
	        var nowd = new Date().Format("yyyy-MM-dd");
	        $("#sregisttime").textbox("setText", nowd);
	        var nowd1 = new Date().Format("yyyy-MM-dd hh:mm:ss");
	        $("#performSubtime").textbox("setText", nowd1);
		}
		
	}
	//批量执行
	function batchPerform(){
		chk_value =[];
		var rows = $('#llbinfo').datagrid('getSelections');
		if(rows.length==0){
			alert("请选择要执行的申请信息！");
		}else{
			var applyman=rows[0].applyMan;
			for(var i=0;i<rows.length;i++){
				//判断该申请是否已经登记
			if(rows[i].ifPerform!="待执行"){
					alert("请先修改执行状态为待执行！");
					return false;
			}else{
				if(rows[i].applyMan!=applyman){
					alert("请选择相同的申请人！");
					return false;
				}else{
					$("#performApplyman").val(applyman);
					chk_value.push(rows[i].applyId);
			}
			}
			}
			$('#perform').dialog('open').dialog('setTitle', '事后登记');
			var now = new Date();
	        var nowd1 = new Date().Format("yyyy-MM-dd hh:mm:ss");
	        $("#performSubtime").textbox("setText", nowd1);
		}
	}
	// 给查询框赋初值
	function search() {
		var now = new Date();
        var nowd = new Date().Format("yyyy-MM-dd hh:mm:ss");

        var lastmonth = new Date;
        lastmonth.setDate(now.getDate() - 30);
        var ldate = lastmonth.Format("yyyy-MM-dd hh:mm:ss");
        
		$('#searchUser').dialog('open').dialog('setTitle', '查询');
		$("#sdepartment").combobox("setText", "全部");
		$("#status").val("全部");
		$("#sstarttime").textbox("setText", ldate);
		$("#sendtime").textbox("setText", nowd);
	}
	//删除数据
	function del(){
		var row = $('#llbinfo').datagrid('getSelected');
		if (row) {
			var param = {
					ApplyId : row.applyId
			};
			$.post("${pageContext.request.contextPath}/privateCar/deleteApplyInfo", param,
					function(result) {
				if (result>0) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
					$.messager.show({
						title : 'Message',
						msg : '删除成功'
					});
				} else {
					$.messager.show({
						title : 'Error',
						msg : '删除失败'
					});
				}
					});
		} else {
			alert("请选择要删除的数据！");
		}
	}
	// js格式化Date方法
	      //修改日历框的显示格式
        function formatter(date){
		var now=new Date;
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();
            var hour = now.getHours();
            var minute = now.getMinutes();
            var second = now.getSeconds();
            month = month < 10 ? '0' + month : month;
            day = day < 10 ? '0' + day : day;
            hour = hour < 10 ? '0' + hour : hour;
            minute = minute < 10 ? '0' + minute : minute;
            second = second < 10 ? '0' + second : second;
            return year + "-" + month + "-" + day + " " + hour+":"+minute+":"+second;
        }

        function parser(s){
            var t = Date.parse(s);
            if (!isNaN(t)){
                return new Date(t);
            } else {
                return new Date(s);
            }
        }
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
			singleSelect : false,
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
				}

			},'-',{
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					del();
			}},'-',{
				text : '批量报销',
				iconCls : 'icon-edit',
				handler : function() {
					batchRegist();
			}},'-',{
				text : '查询',
				iconCls : 'icon-search',
				handler : function() {
					search();
			}}],
// 			,'-',{
// 				text : '批量执行',
// 				iconCls : 'icon-edit',
// 				handler : function() {
// 					batchPerform();
// 			}}
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
		//切换状态切换添加页面
		$("#statuss").change(function(){
			if($("#statuss").val()=="已通过"){
				$("#approveT1").show();
				$("#approveT2").show();
				$("#performstatus1").show();
				$("#performstatus2").show();
				$("#pass1").hide();
				$("#pass2").hide();
				$("#sum1").hide();
				$("#sum2").hide();
				$("#register1").hide();
				$("#register2").hide();
				$("#regman1").hide();
				$("#regman2").hide();
				$("#voucher1").hide();
				$("#voucher2").hide();
				$("#subtime1").hide();
				$("#subtime2").hide();
			}else if($("#statuss").val()=="待审批"){
				$("#approveT1").hide();
				$("#approveT2").hide();
				$("#performstatus1").hide();
				$("#performstatus2").hide();
				$("#sum1").hide();
				$("#sum2").hide();
				$("#register1").hide();
				$("#register2").hide();
				$("#regman1").hide();
				$("#regman2").hide();
				$("#voucher1").hide();
				$("#voucher2").hide();
				$("#pass1").hide();
				$("#pass2").hide();
				$("#subtime1").hide();
				$("#subtime2").hide();
			}else if($("#statuss").val()=="被否决"){
				$("#performstatus1").hide();
				$("#performstatus2").hide();
				$("#approveT1").show();
				$("#approveT2").show();
				$("#sum1").hide();
				$("#sum2").hide();
				$("#register1").hide();
				$("#register2").hide();
				$("#regman1").hide();
				$("#regman2").hide();
				$("#voucher1").hide();
				$("#voucher2").hide();
				$("#pass1").hide();
				$("#pass2").hide();
				$("#subtime1").hide();
				$("#subtime2").hide();
			}else if($("#statuss").val()=="已完成"){
				$("#approveT1").show();
				$("#approveT2").show();
				$("#performstatus1").show();
				$("#performstatus2").show();
				$("#pass1").show();
				$("#pass2").show();
				$("#sum1").show();
				$("#sum2").show();
				$("#register1").show();
				$("#register2").show();
				$("#regman1").show();
				$("#regman2").show();
				$("#voucher1").show();
				$("#voucher2").show();
				$("#subtime1").show();
				$("#subtime2").show(); 
			}
			
		});
		$("#ifpass").change(function(){
			if("已报销"==$(this).val()){
				$("#register1").show();
				$("#register2").show();
				$("#regman1").show();
				$("#regman2").show();
				$("#voucher1").show();
				$("#voucher2").show();
				$("#sum1").show();
				$("#sum2").show();
			}else{
				$("#register1").hide();
				$("#register2").hide();
				$("#regman1").hide();
				$("#regman2").hide();
				$("#voucher1").hide();
				$("#voucher2").hide();
				$("#sum1").show();
				$("#sum2").show();
			}
		});
		//是否执行切换
		$("#ifperform").change(function(){
			if($("#ifperform").val()=="待执行"){
				$("#pass1").hide();
				$("#pass2").hide();
				$("#sum1").hide();
				$("#sum2").hide();
				$("#register1").hide();
				$("#register2").hide();
				$("#regman1").hide();
				$("#regman2").hide();
				$("#voucher1").hide();
				$("#voucher2").hide();
				$("#pass1").hide();
				$("#pass2").hide();
				$("#subtime1").hide();
				$("#subtime2").hide();
			}else if($("#ifperform").val()=="已执行"){
				$("#pass1").show();
				$("#pass2").show();
				$("#sum1").show();
				$("#sum2").show();
				$("#subtime1").show();
				$("#subtime2").show();
			}
		})
		//路线规划切换
		$("#waymodel").change(function(){
			if($("#waymodel").val()=="自定义线路"){
				$("#passAddress").show();
				$("#way").hide();
				 $("#beginaddress").textbox("setText","");
				 $("#destination").textbox("setText","");
				 $("#endLength").textbox("setText","");
			}else if($("#waymodel").val()=="预置线路"){
				$("#passAddress").hide();
				$("#way").show();
			}
		})
		//预置路线切换
		$("#waydetail").combobox({
		onChange: function (n,o) {
			 $("#beginaddress").textbox("setText",$("#waydetail").textbox("getText").split("-")[0]);
			 $("#destination").textbox("setText",$("#waydetail").textbox("getText").split("-")[1]);
			 $("#endLength").textbox("setText",$("#waydetail").textbox("getValue").split("-")[1]);
			 $("#realwaydetail").val($("#waydetail").textbox("getValue").split("-")[0]);
		}})
		loadGrid('${pageContext.request.contextPath}/privateCar/displayAll',null);
		// 确认按钮
		$("#btn_ok").click(function(){
			//待审批
				var department = $("#department").textbox("getText");
				var applyMan = $("#applyman").textbox("getText");
				var approveMan = $("#approveman").textbox("getText");
				var userCarTime = $("#usercartime").textbox("getText");
				var reason = $("#reason").textbox("getText");
				var beginAddress = $("#beginaddress").textbox("getText");
				
				var address = "";
				$("input[name='passes']").each(function(){
					address += $(this).val()+",";
				});
				var length="";
				$("input[name='endlengthes']").each(function(){
					length += $(this).val()+",";
				});
				var passAddress = address+"-"+length;
				var destination = $("#destination").textbox("getText");
				var singleLength = $("#singlelength").textbox("getText");
				var sureLength = $("#surelength").textbox("getText");
				var countLength = $("#countlength").textbox("getText");
				var wayModel = $("#waymodel").val();
				var wayDetail = $("#realwaydetail").val();
				var doubleLength = $("#doublelength").val();
				var endLength = $("#endLength").textbox("getText");
				var ifBefore = $("#ifbefore").val();
				var status=$("#statuss").val();
				var applyTime=$("#applytime").textbox("getText");
			//被否决
				var approveTime=$("#approvetime").textbox("getText");//审批时间
			//已通过未执行
			    var ifPerform=$("#ifperform").val();//是否执行
			//已通过已执行
			    var ifPass = $("#ifpass").val();//是否报销
			    var submitTime=$("#subtime").textbox("getText");
			//已执行未报销
			var sum=$("#sum").textbox("getText");//报销金额
			//已执行已报销
			var paidMan=$("#approveman2").textbox("getText");//登记人
			var paidTime=$("#paidtime").textbox("getText");//登记时间
			var voucherNum=$("#vouchernum").textbox("getText");//凭单号
			if(es=="添加"){
// 				alert(status+".."+department+".."+applyMan+".."+approveMan+".."+userCarTime+".."+reason+".."+beginAddress+".."+passAddress+".."+destination+".."+singleLength+".."+sureLength+".."+countLength+".."+wayModel+".."+wayDetail+".."+doubleLength+".."+endLength+".."+ifBefore+".."+applyTime+".."+approveTime+".."+ifPerform+".."+ifPass+".."+sum+".."+paidMan+".."+paidTime+".."+voucherNum+".."+submitTime)
				var param = {status:status,department:department,applyMan:applyMan,approveMan:approveMan,userCarTime:userCarTime,
						reason:reason,beginAddress:beginAddress,passAddress:passAddress,
						destination:destination,singleLength:singleLength,sureLength:sureLength,countLength:countLength,
						wayModel:wayModel,wayDetail:wayDetail,doubleLength:doubleLength,endLength:endLength,ifBefore:ifBefore,applyTime:applyTime,approveTime:approveTime,ifPerform:ifPerform,ifPass:ifPass,sum:sum,paidMan:paidMan,paidTime:paidTime,voucherNum:voucherNum,submitTime:submitTime};

					$.post("${pageContext.request.contextPath}/privateCar/SaveNew",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
				
			}else{
				var row = $('#llbinfo').datagrid('getSelected');
				
				var param = {applyId:row.applyId,status:status,department:department,applyMan:applyMan,approveMan:approveMan,userCarTime:userCarTime,
						reason:reason,beginAddress:beginAddress,passAddress:passAddress,
						destination:destination,singleLength:singleLength,sureLength:sureLength,countLength:countLength,
						wayModel:wayModel,wayDetail:wayDetail,doubleLength:doubleLength,endLength:endLength,ifBefore:ifBefore,applyTime:applyTime,approveTime:approveTime,ifPerform:ifPerform,ifPass:ifPass,sum:sum,paidMan:paidMan,paidTime:paidTime,voucherNum:voucherNum,submitTime:submitTime};
				
				$.post("${pageContext.request.contextPath}/privateCar/updateNew",param, function(result) {
					$("#dlg").dialog("close");
					$('#llbinfo').datagrid('reload');
				});
			}
			
			
			
		});
		//查询私车公用申请信息
		$("#search_ok").click(
			function() {
				var param = {department:$("#sdepartment").combobox("getText"),
						     applyMan:$("#smanager").textbox("getText").trim(),
							 beingTime:$("#sstarttime").textbox("getText"),
							 endTime:$("#sendtime").textbox("getText"),
							 status:$("#status").val()};
				$("#searchUser").dialog("close");
				loadGrid('${pageContext.request.contextPath}/privateCar/displaySearch',param);
			});
		//登记审核单号
		$("#regist_ok").click(
			function() {
				 var applyids=chk_value.toString(); 
				 var param = {
							ApplyIds : applyids,
							registtime:$("#sregisttime").textbox("getText"),
							registman:$("#sapproveman2").textbox("getText"),
							vouchernum:$("#svouchernum").textbox("getText"),
							sum:$("#invoMoney").textbox("getText"),
							subtime:$("#performSubtime").textbox("getText"),
							applyman:$("#performApplyman").val()
					};
				
				$.post("${pageContext.request.contextPath}/privateCar/registApplyInfo", param,
						function(result) {
// 					if (result>0) {
						$("#save").dialog("close");
						$('#llbinfo').datagrid('reload');
// 						$.messager.show({
// 							title : 'Message',
// 							msg : '登记成功'
// 						});
// 					} else {
// 						$.messager.show({
// 							title : 'Error',
// 							msg : '登记失败'
// 						});
// 					}
				});
			});
		//批量执行事后登记
		$("#perform_ok").click(
				function() {
					 var applyids=chk_value.toString(); 
					 var param = {
								ApplyIds : applyids,
								subtime:$("#performSubtime").textbox("getText"),
								sum:$("#performMoney").textbox("getText"),
								applyman:$("#performApplyman").val()
						};
					$.post("${pageContext.request.contextPath}/privateCar/performApplyInfo", param,
							function(result) {
//	 					if (result>0) {
							$("#perform").dialog("close");
							$('#llbinfo').datagrid('reload');
//	 						$.messager.show({
//	 							title : 'Message',
//	 							msg : '登记成功'
//	 						});
//	 					} else {
//	 						$.messager.show({
//	 							title : 'Error',
//	 							msg : '登记失败'
//	 						});
//	 					}
					});
				});
		//增加途经地
		$("#btn_addaddress").click(function() {
			$('#addr').append('<div name="passdiv"><label>途径地</label><input name="passes" style="width: 220px;"></input><label>行驶里程</label><input name="endlengthes" class="easyui-datebox" style="width: 220px;"></input></td><button type="button" class="close" onclick="DeleteDiv(this)">×</button></div>');
		});
		// 取消按钮
		$("#btn_cancel").click(function() {
			$('#dlg').dialog('close')
		});
		$("#search_cancel").click(function() {
			$('#searchUser').dialog('close')
		});
		$("#regist_cancel").click(function() {
			$('#save').dialog('close')
		});
		$("#perform_cancel").click(function() {
			$('#perform').dialog('close')
		});
		
	})	
	
	function fixWidth(percent) {
		return ($(".mdiv").width() - 30) * percent;
	}
	//移除途径地
	function DeleteDiv(obj){
		$(obj).parent().remove();
	}
	function edit(){
		$('#addr').empty();
		es = "修改";
		var row = $('#llbinfo').datagrid('getSelected');
		if (row){
			 $('#dlg').dialog('open').dialog('setTitle',es);
		     $('#fm').form('clear');
		     var now = new Date();
	        var nowd = new Date().Format("yyyy-MM-dd");
	        var nowd1 = new Date().Format("yyyy-MM-dd hh:mm:ss");
		     
		     $("#subtime1").show();
			$("#subtime2").show();
		     $("#approveT1").show();
			$("#approveT2").show();
			$("#performstatus1").show();
			$("#performstatus2").show();
			$("#pass1").show();
			$("#pass2").show();
			$("#sum1").show();
			$("#sum2").show();
			$("#register1").show();
			$("#register2").show();
			$("#regman1").show();
			$("#regman2").show();
			$("#voucher1").show();
			$("#voucher2").show();
		    $("#department").textbox("setText",row.department);
			$("#applyman").textbox("setText",row.applyMan);
			$("#approveman").textbox("setText",row.approveMan);
			if(row.userCarTime!=""&&row.userCarTime!=null){
				$("#usercartime").textbox("setText",row.userCarTime);
			}else{
				$("#usercartime").textbox("setText", nowd);
			}
			
			$("#reason").textbox("setText",row.reason);
			$("#beginaddress").textbox("setText",row.beginAddress);
			$("#passaddress").textbox("setText",row.passAddress);
			$("#destination").textbox("setText",row.destination);
			$("#singlelength").textbox("setText",row.singleLength);
			$("#surelength").textbox("setText",row.sureLength);
			$("#countlength").textbox("setText",row.countLength);
			$("#waymodel").val(row.wayModel);
			//预置路线
			if(row.wayDetail!=""){
// 				$("#waydetail").show()
				$.post("${pageContext.request.contextPath}/privateCar/getWayByDict?id="+row.wayDetail, null,
						function(result) {
					$("#waydetail").textbox("setText",result);
				});
			}else{
				$("#waydetail").textbox("setText","");
				$("#waydetail").hide();
			}
			
			$("#endLength").textbox("setText",row.endLength);
			$("#ifpass").val(row.ifPass);
			$("#ifperform").val(row.ifPerform);
			if(row.approveTime!=null&&row.approveTime!=""){
				$("#approvetime").textbox("setText",row.approveTime);
			}else{
				 $("#approvetime").textbox("setText", nowd1);
			}
			if(row.applyTime!=null&&row.applyTime!=""){
				$("#applytime").textbox("setText",row.applyTime);
			}else{
				$("#applytime").textbox("setText", nowd1);
			}
			if(row.subTime!=null&&row.subTime!=""){
				$("#subtime").textbox("setText",row.subTime);
			}else{
				 $("#subtime").textbox("setText", nowd1);
			}
			if(row.ifBefore=="否"){
				$("#ifbefore").val("0");
			}else if(row.ifBefore=="是"){
				$("#ifbefore").val("1");
			}
			if(row.status=="待修改"){
				$("#statuss").val("被否决");
			}else{
				$("#statuss").val(row.status);
			}
			$("#doublelength").val(row.doubleLength);
			$("#sum").textbox("setText",row.sum);
			if(row.paidTime!=null&&row.paidTime!=""){
				$("#paidtime").textbox("setText",row.paidTime);
			}else{
				$("#paidtime").textbox("setText", nowd);
			}
			
			$("#approveman2").textbox("setText",row.paidMan);
			$("#vouchernum").textbox("setText",row.voucherNum);
			//途径地
			if(row.passAddress!="[]"){
			var obj = JSON.parse(row.passAddress);
			for(var i=0;i<obj.length;i++){
				$('#addr').append('<div name="passdiv"><label>途径地</label><input name="passes" style="width: 220px;" value='+obj[i].addressName+'></input><label>行驶里程</label><input name="endlengthes" class="easyui-datebox" style="width: 220px;" value='+obj[i].addressValue+'></input></td><button type="button" class="close" onclick="DeleteDiv(this)">×</button></div>');
			}
			}
			
		}else{
	    	alert("请选择要修改的数据！");
	    }
	}
	function add(){
		 es = "添加";
		 var now = new Date();
        var nowd = new Date().Format("yyyy-MM-dd");
        var nowd1 = new Date().Format("yyyy-MM-dd hh:mm:ss");
        $('#addr').empty();
		 $('#dlg').dialog('open').dialog('setTitle',es);
	     $('#fm').form('clear');
	     $("#usercartime").textbox("setText", nowd);
	     $("#paidtime").textbox("setText", nowd);
	     $("#approvetime").textbox("setText", nowd1);
	     $("#applytime").textbox("setText", nowd1);
	     $("#subtime").textbox("setText", nowd1);
	     $("#statuss").val("待审批");
	     $("#waymodel").val("自定义线路");
	     $("#ifbefore").val("0");
	     $("#ifperform").val("待执行");
	     $("#ifpass").val("未报销");
	     $("#approveT1").hide();
		$("#approveT2").hide();
		$("#performstatus1").hide();
		$("#performstatus2").hide();
		$("#sum1").hide();
		$("#sum2").hide();
		$("#register1").hide();
		$("#register2").hide();
		$("#regman1").hide();
		$("#regman2").hide();
		$("#voucher1").hide();
		$("#voucher2").hide();
		$("#pass1").hide();
		$("#pass2").hide();
		$("#subtime1").hide();
		$("#subtime2").hide();
	}
	
</script>
</head>
<body>
<!-- 批量报销 -->
	<div id="save" class="easyui-dialog"
		style="width: 400px; height: 320px; padding: 10px 20px" closed="true">
		<form id="form" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10" >
					<tr>
						<td><label>登记人</label></td>
						<td><input id="sapproveman2" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>登记时间</label></td>
						<td><input id="sregisttime" class="easyui-datebox"
						style="width: 220px;"></input></td>
					</tr>
					<tr>
					<td><label>提交时间</label></td>
						<td><input id="performSubtime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>
					<tr>
						<td><label>凭单号</label></td>
						<td><input id="svouchernum" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>报销金额</label></td>
						<td><input id="invoMoney" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<input id="performApplyman" style="width: 220px;display:none"></input>
				</table>
				<div id="cc" class="easyui-calendar"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="regist_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" >凭证号登记</a> <a
					href="#" id="regist_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" >取消</a>
			</div>
		</form>
	</div>
	<!-- 批量执行 -->
	<div id="perform" class="easyui-dialog"
		style="width: 400px; height: 220px; padding: 10px 20px" closed="true">
		<form id="performForm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10" >
<!-- 					<tr> -->
<!-- 						<td><label>提交时间</label></td> -->
<!-- 						<td><input id="performSubtime" class="easyui-datebox" -->
<!-- 						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td> -->
<!-- 					</tr> -->
<!-- 						<td><label>报销金额</label></td> -->
<!-- 						<td><input id="performMoney" class="easyui-textbox" style="width: 220px;"></input></td> -->
<!-- 					</tr> -->
<!-- 					<input id="performApplyman" style="width: 220px;display:none"></input> -->
				</table>
				<div id="cc" class="easyui-calendar"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="perform_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-save'" >事后登记</a> <a
					href="#" id="perform_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" >取消</a>
			</div>
		</form>
	</div>
	<!-- 添加修改框 -->
	<div id="dlg" class="easyui-dialog" style="width:700px;height:550px;padding:0px 0px"closed="true" buttons="#dlg-buttons">
        <form id="fm" method="post">
       <div style="text-align:center;">
        		<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>申请人</label></td>
						<td><input id="applyman" class="easyui-textbox"
							style="width: 220px;"></input></td>
							<td><label>部门</label></td>
						<td><input id="department" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/department/getAllDepartment',
							method:'get',
							valueField:'id',
							textField:'departmentname',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
					<td><label>申请状态</label></td>
						<td><select id="statuss" style="width:100%">
						<option value="待审批">待审批</option>
						<option value="已通过">已通过</option>
						<option value="被否决">被否决</option>
						</select></td>
						<td><label>使用事由</label></td>
						<td><input id="reason" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>审批人</label></td>
						<td><input id="approveman" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>路线规划</label></td>
						<td><select id="waymodel" style="width: 220px;"><option
									value="预置线路" selected>预置线路</option>
								<option value="自定义线路">自定义线路</option></select></td>
					</tr>
					<tr id="way" style="display:none">
						<td><label>预置路线</label></td>
						<td><input id="waydetail" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/privateCar/getway',
							method:'get',
							valueField:'id',
							textField:'text',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
							<input  style="display:none" id="realwaydetail"></input>
					</tr>
					<tr>
						<td><label>出发地</label></td>
						<td><input id="beginaddress" class="easyui-textbox"
							style="width: 220px;"></input></td>
							<td><label>目的地</label></td>
						<td><input id="destination" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					<tr>
							<td><label>行驶里程</label></td>
						<td><input id="endLength" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>单程里程</label></td>
						<td><input id="singlelength" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
					
					<tr>
					<td><label>计价里程</label></td>
						<td><input id="countlength" class="easyui-textbox"
							style="width: 220px;"></input></td>
						<td><label>核定价格</label></td>
						<td><input id="surelength" class="easyui-textbox"
							style="width: 220px;"></input></td>
						
					</tr>
					<tr>
						<td><label>是否往返</label></td>
						<td><select id="doublelength" style="width: 220px;"><option
									value="1" selected>是</option>
								<option value="0">否</option></select></td>
						<td><label>是否补录</label></td>
						<td><select id="ifbefore" style="width: 220px;"><option
									value="1" selected>是</option>
								<option value="0">否</option></select></td>
					</tr>
					<tr>
						<td><label>申请时间</label></td>
						<td><input id="applytime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>
						<td><label>用车时间</label></td>
						<td><input id="usercartime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td id="performstatus1"  style="display: none"><label>执行状态</label></td>
						<td id="performstatus2"  style="display: none"><select id="ifperform" style="width: 220px;"><option
									value="待执行" selected>待执行</option>
								<option value="已执行">已执行</option></select></td>
						<td id="approveT1" style="display: none"><label>审批时间</label></td>
						<td id="approveT2" style="display: none"><input id="approvetime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>		
					</tr>
					<tr>
						<td id="subtime1" style="display: none"><label>提交时间</label></td>
						<td id="subtime2" style="display: none"><input id="subtime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>
						<td id="pass1" style="display: none"><label>是否报销</label></td>
						<td id="pass2" style="display: none"><select id="ifpass" style="width: 220px;"><option
									value="未报销" selected>未报销</option>
								<option value="已报销">已报销</option></select></td>
					</tr>
					<tr>
					<td id="sum1" style="display: none"><label>报销金额</label></td>
						<td id="sum2" style="display: none"><input id="sum" class="easyui-textbox"
							style="width: 220px;"></input></td>
					<td id="register1" style="display: none"><label>登记时间</label></td>
						<td id="register2" style="display: none"><input id="paidtime" class="easyui-datebox"
						data-options="sharedCalendar:'#cc'" style="width: 220px;"></input></td>
					</tr>
					<tr>
					<td id="regman1" style="display: none"><label>登记人</label></td>
						<td id="regman2" style="display: none"><input id="approveman2" class="easyui-textbox"
							style="width: 220px;"></input></td>
					<td id="voucher1" style="display: none"><label>凭单号</label></td>
					<td id="voucher2" style="display: none"><input id="vouchernum" class="easyui-textbox"
							style="width: 220px;"></input></td>
					</tr>
				</table>
        	</div>
        	<div id="passAddress" style="margin-left: 8%">
        	 <a href="#" id="btn_addaddress" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">增加途经地</a></br>
        	  <div id="addr"></div>    
        	</div>
  			<div style="text-align:center;bottom:15px;margin-top:20px;">
        		<a href="#" id="btn_ok" class="easyui-linkbutton" data-options="iconCls:'icon-save'" style="width:20%;">确定</a>
				<a href="#" id="btn_cancel" class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" style="width:20%;">取消</a>
        	</div>
        </form>
    </div>
	<div class="mdiv" style="width:100%;height:94%;">
		<table id="llbinfo" class="easyui-datagrid" title="私车公用所有申请信息"
			style="width: auto; height:100%;">
			<thead>
				<tr>
				 	<th
					data-options="field:'',width:fixWidth(0.1),align:'center',checkbox:'true'">选项</th>
					<th
					data-options="field:'applyId',width:fixWidth(0.1),align:'center'" hidden="true">审批单号</th>
					<th 
					data-options="field:'applyMan',width:fixWidth(0.1),align:'center'">申请人</th>
					<th
					data-options="field:'applyTime',width:fixWidth(0.2),align:'center'">申请时间</th>
					<th
					data-options="field:'department',width:fixWidth(0.1),align:'center'">所属部门</th>
					<th
					data-options="field:'sureLength',width:fixWidth(0.1),align:'center'">核定价格</th>
					<th
					data-options="field:'ifBefore',width:fixWidth(0.1),align:'center'">是否补录</th>
					<th
					data-options="field:'userCarTime',width:fixWidth(0.2),align:'center'">用车时间</th>
					<th
					data-options="field:'status',width:fixWidth(0.1),align:'center'">审批状态</th>
					<th
					data-options="field:'approveMan',width:fixWidth(0.1),align:'center'">审批人</th>
					<th 
					data-options="field:'approveTime',width:fixWidth(0.2),align:'center'">审批时间</th> 
					<th 
					data-options="field:'ifPerform',width:fixWidth(0.1),align:'center'">是否执行</th> 
					<th 
					data-options="field:'submitTime',width:fixWidth(0.2),align:'center'">提交时间</th>
					<th 
					data-options="field:'ifPass',width:fixWidth(0.1),align:'center'">是否报销</th> 
					<th
					data-options="field:'sum',width:fixWidth(0.1),align:'center'">报销金额</th>
					<th 
					data-options="field:'paidTime',width:fixWidth(0.2),align:'center'">登记时间</th> 
					<th
					data-options="field:'paidMan',width:fixWidth(0.1),align:'center'">登记人</th>
					<th
					data-options="field:'voucherNum',width:fixWidth(0.1),align:'center'">凭单号</th>
				</tr>
			</thead>
		</table>  
	</div>
<!-- 查询信息对话框 -->
	<div id="searchUser" class="easyui-dialog"
		style="width: 400px; height: 320px; padding: 10px 20px" closed="true">
		<form id="sfm" method="post">
			<div style="text-align: center;">
				<table style="margin: auto;" cellspacing="10">
					<tr>
						<td><label>部门</label></td>
						<td><input id="sdepartment" class="easyui-combobox" 
						data-options="
							url:'${pageContext.request.contextPath}/department/getAllDepartment',
							method:'get',
							valueField:'id',
							textField:'departmentname',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>申请人</label></td>
						<td><input id="smanager" class="easyui-textbox" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>申请状态</label></td>
						<td><select id="status" style="width:100%">
						<option value="全部">全部</option>
						<option value="待审批">待审批</option>
						<option value="已通过">已通过</option>
						<option value="待修改">待修改</option>
						</select></td>
					</tr>
					<tr>
						<td><label>起始时间</label></td>
						<td><input id="sstarttime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>
					</tr>
					<tr>
						<td><label>结束时间</label></td>
						<td><input id="sendtime" class="easyui-datebox"
						data-options="formatter:formatter,parser:parser" style="width: 220px;"></input></td>
					</tr>


				</table>
				<div id="cc" class="easyui-calendar"></div>
			</div>
			<div style="text-align: center; bottom: 15px; margin-top: 20px;">
				<a href="#" id="search_ok" class="easyui-linkbutton"
					data-options="iconCls:'icon-search'" style="width: 20%;">查询</a> <a
					href="#" id="search_cancel" class="easyui-linkbutton"
					data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
			</div>
		</form>
	</div>
</body>
</html>