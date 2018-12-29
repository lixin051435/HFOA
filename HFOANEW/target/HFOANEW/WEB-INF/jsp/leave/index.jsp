<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>年假执行情况</title>
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

  
    var es = "新建";
    //提交action 的url
    var url = "";
    var idd = 1;

    //返回json对象的长度
    function getJsonObjLength(jsonObj) {
      var Length = 0;
      for (var item in jsonObj) {
        Length++;
      }
      return Length;
    }

    //格式化json
    function formatJson(json) {
      var jsobj = eval(json);
      return '{"total":' + jsobj.bingdings.length + ',"rows":' + json + "}";
    }

    function fixWidth(percent) {
      return ($(".mdiv").width() - 30) * percent;
    }

    function loadGrid(data) {

	var name =  "<%=request.getParameter("user") %>"; 
        
      //汉化 datagrid 翻页
      $("#llbinfo").datagrid({
        url: '${pageContext.request.contextPath}/leavel/searchLearYear?name='+name, 
        method: 'post',
        queryParams: data,
        pageSize: 20,
        rownumbers: true,
        singleSelect: true,
        pagination: true,
        nowrap: false,
        toolbar: [{
          text: '查询',
          iconCls: 'icon-search',
          handler: function () {
            search();
          }
        }, '-', {
          text: '导出表单',
          iconCls: 'icon-llb2',
          handler: function () {
            exportExcel();
          }
        }],
        onLoadSuccess: function (data) {
          $('.preScan').linkbutton({text: '查看详情', plain: true, iconCls: 'icon-search'});
        }
      });
      /* setFirstPage("#llbinfo"); */
      var pager = $('#llbinfo').datagrid().datagrid('getPager');
      pager.pagination({
        beforePageText: "转到",
        afterPageText: "共{pages}页",
        displayMsg: '当前显示从{from}到{to}  共{total}记录',
        onBeforeRefresh: function (pageNumber, pageSize) {
          $(this).pagination('loading');
          $(this).pagination('loaded');
        }
      });
    }  
    	
    $(function () {
    	loadGrid({state: 1});
    	/* $("#hight").hide();  */
    	 $.ajax({  
             type : "POST",  //提交方式  
             url : "${pageContext.request.contextPath}/user/getDepartment",//路径  
             success : function(result) {//返回数据根据结果进行相应的处理  
                 var name =  "<%=request.getParameter("user") %>"; 
                 console.log(name.indexOf('综合办公室'));
                 if(name.indexOf('综合办公室')!=-1){
                	 $("#hight").show(); 	
                 }else{
                	 $("#hight").hide();
                 }
                 
             }  
         });  

    	$("#vehicleCost").click(function(){  
            alert("ok");  
        }); 
      //$(param.llbid) 传递过来得履历本id
      
      //查询用户
      $("#search_ok").click(
          function () {
              
        	var param = {
                      /* department: $("#department").combobox("getText"),
                      applyMan: $("#travelers").combotree("getText").trim(),
                      beginTime: $("#beginTime").textbox("getText") */
            };

        	var department = $("#department").combobox("getText");
        	alert(department);
          	if(department.length!=0){
          		param={department:department}
          	}
          	var travelers = $("#travelers").textbox("getText").trim();
          	if(travelers.length!=0){
          		param = {applyMan:travelers}
          	}
          	var beginTime = $("#beginTime").textbox("getText").trim();
          	if(beginTime.length!=0){
          		param = {beginTime:beginTime}
          	}
            $("#searchUser").dialog("close");
            console.log(param);
            $("#llbinfo").datagrid({
                url: '${pageContext.request.contextPath}/leavel/searchlistLearYear', 
                method: 'post',
                queryParams: param,
                pageSize: 20,
                rownumbers: true,
                singleSelect: true,
                pagination: true,
                nowrap: false,
                toolbar: [{
                  text: '查询',
                  iconCls: 'icon-search',
                  handler: function () {
                    search();
                  }
                }, '-', {
                  text: '导出表单',
                  iconCls: 'icon-llb2',
                  handler: function () {
                    exportExcel();
                  }
                }],
                onLoadSuccess: function (data) {
                  $('.preScan').linkbutton({text: '查看详情', plain: true, iconCls: 'icon-search'});
                }
              });
            var pager = $('#llbinfo').datagrid().datagrid('getPager');
            pager.pagination({
              beforePageText: "转到",
              afterPageText: "共{pages}页",
              displayMsg: '当前显示从{from}到{to}  共{total}记录',
              onBeforeRefresh: function (pageNumber, pageSize) {
                $(this).pagination('loading');
                $(this).pagination('loaded');
              }
            });
            
          }); 
      //查询取消
      $("#search_cancel").click(function () {
        $("#searchUser").dialog("close");

      });
      //登记取消
      $("#btn_cancel").click(function () {
        $('#dlg').dialog('close');
      });
      //单选是否属于试验
      
      //报销总额自动累加和
      $("#vehicleCost").numberbox({onChange: function () {onChangeAdd();}});
      $("#foodAllowance").numberbox({onChange: function () {onChangeAdd();}});
      $("#hotelExpense").numberbox({onChange: function () {onChangeAdd();}});
      $("#parValueAllowance").numberbox({onChange: function () {onChangeAdd();}});
      $("#urbanTraffic").numberbox({onChange: function () {onChangeAdd();}});
      $("#otherCost").numberbox({onChange: function () {onChangeAdd();}});
      $("#inputTax").numberbox({onChange: function () {onChangeAdd();}});
      $("#totalExpenses").numberbox({onChange: function () {onChangeAdd();}});
      $("#repayCost").numberbox({onChange: function () {onChangeAdd();}});
    });


    
    //查询框
    function search() {
      $("#department").combobox('setValue', '');
      $('#searchUser').dialog('open').dialog('setTitle', '查询');
    }

    //导出表单
    function exportExcel() {
    	var rows = $('#llbinfo').datagrid("getRows");
    	if(rows.length==0){
    		alert("没有数据");
    		return;
    	}
    	var url = '${pageContext.request.contextPath}/leavel/exportLeavel?1=1';
    	var department = $("#department").combobox("getText");
    	if(department.length!=0){
    		url+='&department='+department;
    	}
    	var travelers = $("#travelers").textbox("getText").trim();
    	if(travelers.length!=0){
    		url+='&applyMan='+travelers;
    	}
    	var beginTime = $("#beginTime").textbox("getText").trim();
    	if(beginTime.length!=0){
    		url+='&applyTime='+beginTime;
    	}
    	
    	 window.open(url);

    }

    // 操作列
    function operation(value, row, rowIndex) {
      //console.info(row.id);
      return '<a class="preScan" href="#" iconCls="icon-search" onclick="registerApply(' + rowIndex + ')">查看详情</a>';
    }

    

    
    function registerApply(index) {
    	 var row = $('#llbinfo').datagrid('getData').rows[index];
         $('#dlg').dialog('open').dialog('setTitle', '查看详细');
         load(row.id);
    }

    function load(id){
    	$("#studentInfo").datagrid({
            url: '${pageContext.request.contextPath}/leavel/listLeaverTime?id='+id,
            method: 'post'
          });
       

    }
 
  </script>
</head>
<body>
<div class="mdiv" style="width:100%;height:94%;">
  <table id="llbinfo" class="easyui-datagrid" title="年假执行查看"
         style="width: auto; height:100%;">
    <thead>
    <tr>
      <th
          data-options="field:'department',width:fixWidth(0.15),align:'center'">部门
      </th>
      <th
          data-options="field:'applyMan',width:fixWidth(0.08),align:'center'">申请人
      </th>
      <th
          data-options="field:'applyTime',width:fixWidth(0.11),align:'center'">申请时间
      </th>
      <th
          data-options="field:'frequency',width:fixWidth(0.11),align:'center'">休假次数
      </th>
      <th
          data-options="field:'approveMan',width:fixWidth(0.11),align:'center'">审批人
      </th>
      <th
          data-options="field:'sfyc',width:fixWidth(0.08),align:'center'">是否异常
      </th>
      <th
      	  data-options="field:'status',width:fixWidth(0.15),align:'center'">是否完结
      </th>
      <th
          data-options="field:'operation',width:fixWidth(0.16),formatter:operation,align:'center'">操作
      </th>
    </tr>
    </thead>
  </table>
</div>



<!-- 查询信息对话框 -->
<div id="searchUser" class="easyui-dialog"
     style="width: 400px; height: 220px; padding: 10px 20px" closed="true">
  <form id="sfm" method="post">
    <div style="text-align: center;">
      <table style="margin: auto;" cellspacing="10">
        <tr id="hight">
          <td><label>部门</label></td>
          <td> <input id="department" class="easyui-combobox"
                     data-options="
                     		url:'${pageContext.request.contextPath}/user/getDepartment',
							valueField:'id',
							textField:'departmentName',
							editable:false,
							panelHeight:'200'
							" style="width: 220px;"/> 
		</td>
        </tr>
        <tr>
          <td><label>申请人</label></td>
          <td><input id="travelers" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>申请时间</label></td>
          <td><input id="beginTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
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

<div id="dlg" class="easyui-dialog"
     style="width: 700px; height: 400px; padding: 0px 0px" closed="true"
     buttons="#dlg-buttons">
  <table id ="studentInfo" align="center" class="easyui-datagrid" style=" height:300px;">
    <thead>
    <tr>
      <th
          data-options="field:'beingTime',width:fixWidth(0.08),align:'center'">开始时间
      </th>
      <th
          data-options="field:'endTime',width:fixWidth(0.08),align:'center'">结束时间
      </th>
      <th
          data-options="field:'days',width:fixWidth(0.08),align:'center'">天数
      </th>
      <th
          data-options="field:'state',width:fixWidth(0.08),align:'center'">是否休假
      </th>
      <th
          data-options="field:'sfyc',width:fixWidth(0.08),align:'center'">是否异常
      </th>
    </tr>
    </thead>
  </table>
  <div style="text-align: center; bottom: 5px; margin-top: 20px;">
    <a href="#" id="btn_cancel" class="easyui-linkbutton"
       data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
  </div>
</div>


</body>
</html>