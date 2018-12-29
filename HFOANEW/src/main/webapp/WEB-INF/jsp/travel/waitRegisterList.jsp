<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>差旅费待登记列表</title>
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

    var taskId;
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
    	alert(json);
      var jsobj = eval(json);
      return '{"total":' + jsobj.bingdings.length + ',"rows":' + json + "}";
    }

    function fixWidth(percent) {
      return ($(".mdiv").width() - 30) * percent;
    }

    function loadGrid(data) {
      //汉化 datagrid 翻页
      $("#llbinfo").datagrid({
        url: '${pageContext.request.contextPath}/applyExpenses/financeApplyExpense',
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
          $('.preScan').linkbutton({text: '登记', plain: true, iconCls: 'icon-save'});
          $('.preScan1').linkbutton({text: '驳回', plain: true, iconCls: 'icon-cancel'});
        }
      });
      setFirstPage("#llbinfo");
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
    	$('#testSite').combobox({
    		prompt:'输入关键字后自动搜索',        
    		required:true,        
    		mode:'remote',
    		url:'${pageContext.request.contextPath}/approveExpenses/getApproveExpenses',        
    		editable:true,        
    		hasDownArrow:false,
    		valueField: "id",
    	    textField: "testSite",
    		onBeforeLoad: function(param){
                console.log("--------------"+param.q);
                if(param == null || param.q == null || param.q.replace(/ /g, '') == ''){
                    var value = $(this).combobox('getValue');
                    if(value){// 修改的时候才会出现q为空而value不为空
                        param.id = value;
                        return true;
                    }
                    return false;
                }
    		}
    	});
    	
    	
    	
      //$(param.llbid) 传递过来得履历本id
      loadGrid({state: 0});
      //查询用户
      $("#search_ok").click(
          function () {
            var param = {
              department: $("#department").combobox("getText"),
              travelers: $("#travelers").combotree("getText").trim(),
              tripDetails: $("#tripDetails").combotree("getText").trim(),
              beginTime: $("#beginTime").textbox("getText"),
              endTime: $("#endTime").textbox("getText"),
              applyMan: $("#applyMan").textbox("getText").trim()
            };
            $("#searchUser").dialog("close");
            loadGrid(param);
          });
      //查询取消
      $("#search_cancel").click(function () {
        $("#searchUser").dialog("close");
      });
      //登记
      $("#btn_ok").click(function () {
        var param ="&voucherNum="+$("#voucherNum").textbox("getText").trim()
        +"&taskId="+taskId+"&testSite="+$("#testSite").textbox("getText").trim()+"&totalExpenses="+$("#totalExpenses").textbox("getText").trim()+
        "&paidTime="+$("#paidTime").datebox("getText")+"&isTestCost="+$("input[name='isTestCost']:checked").val();
        var travelExpenseId = "";
        if($("#travelExpenseId").textbox("getText").length!=0){
        	travelExpenseId = $("#travelExpenseId").textbox("getText");
        	param+="&travelExpenseId="+travelExpenseId;
          }
        var vehicleCost = "";
        if($("#vehicleCost").textbox("getText").length!=0){
        	vehicleCost = $("#vehicleCost").textbox("getText");
        	param+="&vehicleCost="+vehicleCost;
          }
		var foodAllowance = "";
        if($("#foodAllowance").textbox("getText").length!=0){
        	foodAllowance = $("#foodAllowance").textbox("getText");
        	param+="&foodAllowance="+foodAllowance;
        }
		var hotelExpense = "0";
        if($("#hotelExpense").textbox("getText").length!=0){
        	hotelExpense = $("#hotelExpense").textbox("getText");
            param+="&hotelExpense="+hotelExpense;
        }
        var parValueAllowance = "0";
        if($("#parValueAllowance").textbox("getText").length!=0){
        	parValueAllowance = $("#parValueAllowance").textbox("getText");
        	param+="&parValueAllowance="+parValueAllowance;
        }
        var urbanTraffic = "0";
        if($("#urbanTraffic").textbox("getText").length!=0){
        	urbanTraffic = $("#urbanTraffic").textbox("getText");
        	param+="&urbanTraffic="+urbanTraffic;
       	}
       	var otherCost = "0";
       	if($("#otherCost").textbox("getText").length!=0){
       		otherCost = $("#otherCost").textbox("getText");
       		param+="&otherCost="+otherCost;
           	}

       	var repayCost = "";
       	if($("#repayCost").textbox("getText").length!=0){
       		repayCost = $("#repayCost").textbox("getText");
       		param+="&repayCost="+repayCost;
           	}


       	var supplementalCost = "";
       	if($("#supplementalCost").textbox("getText").length!=0){
       		supplementalCost = $("#supplementalCost").textbox("getText");
       		param+="&supplementalCost="+supplementalCost;
	
        }
        var inputTax = "";
        if($("#inputTax").textbox("getText").length!=0){
        	inputTax = $("#inputTax").textbox("getText");
        	param+="&inputTax="+inputTax;
            }
       
        var illustration = "";
        if($("#illustration").textbox("getText").length!=0){
        	illustration = $("#illustration").textbox("getText");
        	param+="&illustration="+illustration; 
            }
             /* "travelExpenseId="+$("#travelExpenseId").textbox("getText").trim()+
            "&vehicleCost="+$("#vehicleCost").textbox("getText").trim()+
            "&foodAllowance="+$("#foodAllowance").textbox("getText").trim()+
            "&hotelExpense="+$("#hotelExpense").textbox("getText").trim()+
            "&parValueAllowance="+$("#parValueAllowance").textbox("getText").trim()+
            "&urbanTraffic="+$("#urbanTraffic").textbox("getText").trim()+
            "&otherCost="+$("#otherCost").textbox("getText").trim()+
            "&repayCost="+$("#repayCost").textbox("getText").trim()+
            "&supplementalCost="+$("#supplementalCost").textbox("getText").trim()+
            "&inputTax="+$("#inputTax").textbox("getText").trim()+
            "&totalExpenses="+$("#totalExpenses").textbox("getText").trim()+
            "&paidTime="+$("#paidTime").datebox("getText")+
            "&voucherNum="+$("#voucherNum").textbox("getText").trim()+
            "&isTestCost="+$("input[name='isTestCost']:checked").val()+
            "&testSite="+$("#testSite").textbox("getText").trim()+
            "&illustration="+$("#illustration").textbox("getText").trim(); */

            console.log(param);

        
        $.post("${pageContext.request.contextPath}/applyExpenses/registerApplyExpense", param, function (result) {
          $("#dlg").dialog("close");
          loadGrid({state: 0});
          if(result)
            alert("插入成功");
          else
            alert("插入失败");
        })
      });
      //登记取消
      $("#btn_cancel").click(function () {
        $('#dlg').dialog('close');
      });
      //单选是否属于试验
      $(":radio[name='isTestCost']").click(function () {
        var index = $(":radio[name='isTestCost']").index($(this));
        if (index == 0)
          $("#showTestSite").show();
        else {
          $("#showTestSite").hide();
          $("#testSite").textbox("setValue", "");
        }
      });
      
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
      window.open('${pageContext.request.contextPath}/applyExpenses/exportApplyExpenses?state=0'
          + '&department=' + $("#department").combobox("getText")
          + '&travelers=' + $("#travelers").combotree("getText").trim()
          + '&tripDetails=' + $("#tripDetails").combotree("getText").trim()
          + '&beginTime=' + $("#beginTime").textbox("getText")
          + '&endTime=' + $("#endTime").textbox("getText")
          + '&applyMan=' + $("#applyMan").textbox("getText").trim());
    }

    // 操作列
    function operation(value, row, rowIndex) {
      //console.info(row.id);
      return '<a class="preScan" href="#" iconCls="icon-save" onclick="registerApply(' + rowIndex + ')">登记</a>'
          + '<a class="preScan1" href="#" iconCls="icon-cancel" onclick="refuseApply(' + rowIndex + ')">驳回</a>';
    }

   
    function registerApply(index) {
      var row = $('#llbinfo').datagrid('getData').rows[index];
      taskId = row.taskId;
      $("#travelExpenseId").textbox("setValue", row.id);
      $("#vehicleCost").textbox("setValue","");
      $("#foodAllowance").textbox("setValue","");
      $("#hotelExpense").textbox("setValue","");
      $("#parValueAllowance").textbox("setValue","");
      $("#urbanTraffic").textbox("setValue","");
      $("#otherCost").textbox("setValue","");
      $("#inputTax").textbox("setValue","");
      $("#totalExpenses").textbox("setValue","");
      $("#repayCost").textbox("setValue","")
      $(":radio[name='isTestCost'][value='否']").prop("checked", "checked");
      $("#showTestSite").hide();
   	  $("#illustration").textbox("setValue","");
      $('#dlg').dialog('open').dialog('setTitle', '登记');
      
    }

    function refuseApply(index) {
      var row = $('#llbinfo').datagrid('getData').rows[index];
      $.messager.confirm('确认对话框','你确定驳回这条记录吗？',function(r){
          if (r){
              $.post('${pageContext.request.contextPath}/applyExpenses/financeReviewFail',"id=" + row.id+"&taskId="+row.taskId,function(result){
                  if (result=="1"){
                	  loadGrid({state: 0});    // reload the user data
                      $.messager.show({
                          title: 'Message',
                          msg: '驳回成功'
                      });
                  } else {
                      $.messager.show({    // show error message
                          title: 'Error',
                          msg: '驳回失败'
                      });
                  }
              },'text');
          }
      });
      /* $.post("", , function (result) {
        if (result) {
          alert("驳回成功");
          loadGrid({state: 0});
        }
        else
          alert("驳回失败");
      }); */
    }

    function onclick(){
    	alert(1);	
    }
    
    function onChangeAdd() {
      var valueArr= $('input[name="needAdd"]');
      var sumValue=0.00;
      var num = $("#repayCost").numberbox("getValue");
      if(valueArr.size()>0||valueArr != null){
        for (var i=0;i<valueArr.size();i++ )
        {
          // alert(valueArr[i].value);
          if(isNaN(valueArr[i].value)||valueArr[i].value==null||valueArr[i].value==""){
            sumValue += 0.00;
          }
          else{
            // alert(valueArr[i].value);
            sumValue += parseFloat(valueArr[i].value);//强制转换为数字
          }
        }
      }
      if(sumValue>0){
        if(isNaN(num) || num == null || num == "")
          $("#supplementalCost").numberbox("setValue", sumValue);
        else
          $("#supplementalCost").numberbox("setValue", sumValue - num);
        $("#totalExpenses").numberbox("setValue", sumValue);
      }
      else{
        if(isNaN(num) || num == null || num == "")
          $("#supplementalCost").numberbox("setValue", 0.00);
        else
          $("#supplementalCost").numberbox("setValue", sumValue - num);
        $("#totalExpenses").numberbox("setValue", 0.00);
      }
    }

    function setFirstPage(ids) {
      var opts = $(ids).datagrid('options');
      var pager = $(ids).datagrid('getPager');
      opts.pageNumber = 1;
      opts.pageSize = opts.pageSize;
      pager.pagination('refresh', {
        pageNumber: 1,
        pageSize: opts.pageSize
      });
    }
    
  </script>
</head>
<body>
<!-- 审核登记对话框 -->
<div id="dlg" class="easyui-dialog"
     style="width: 350px; height: 580px; padding: 0px 0px" closed="true"
     buttons="#dlg-buttons">
  <table align="center" style="margin-top:20px;">
    <tr>
      <td><label>审批单号</label></td>
      <td><input id="travelExpenseId" class="easyui-textbox" style="width: 160px;" disabled/></td>
    </tr>
    <tr>
      <td><label>交通工具金额</label></td>
      <td><input id="vehicleCost"  name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;"/></td>
    </tr>
    <tr>
      <td><label>伙食费补助</label></td>
      <td><input id="foodAllowance" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>住宿费</label></td>
      <td><input id="hotelExpense" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>票面补助</label></td>
      <td><input id="parValueAllowance" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" onclick="this.value='';focus()" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>市内交通</label></td>
      <td><input id="urbanTraffic" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;"/></td>
    </tr>
    <tr>
      <td><label>其他费用</label></td>
      <td><input id="otherCost" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>进项税额</label></td>
      <td><input id="inputTax" name="needAdd" class="easyui-numberbox"
                 precision="2" value="" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>报销总额</label></td>
      <td><input id="totalExpenses" class="easyui-numberbox"
                 precision="2"  type="text" style="width: 160px;" disabled required="true"/></td>
    </tr>
    <tr>
      <td><label>还款金额</label></td>
      <td><input id="repayCost" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;"/></td>
    </tr>
    <tr>
      <td><label>补领金额</label></td>
      <td><input id="supplementalCost" class="easyui-numberbox"
                 precision="2"  type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>报销时间</label></td>
      <td><input id="paidTime" class="easyui-datebox" data-options="sharedCalendar:'#cc'" style="width: 160px;" required="true"/></td>
    </tr>
    <!-- <tr>
      <td><label>经费来源</label></td>
      <td><input id="fundSource" class="easyui-textbox" style="width: 160px;"/></td>
    </tr> -->
    <tr>
      <td><label>凭证号</label></td>
      <td><input id="voucherNum" class="easyui-textbox" style="width: 160px;" required="true"/>
      </td>
    </tr>
    <tr>
      <td><label>是否为试验费</label></td>
      <td>
        <span class="radioSpan">
          <input type="radio" name="isTestCost" value="是"/>是
          <input type="radio" name="isTestCost" value="否" checked="true"/>否
        </span>
      </td>
    </tr>
    <tr id="showTestSite" style="display: none;">
      <td><label>试验现场代号</label></td>
      <td><input id="testSite" class="easyui-combobox" style="width: 160px;" data-options="editable:false,valueField:'id', textField:'testSite'" required="true"/></td>
    </tr>
    <tr>
      <td><label>说明</label></td>
      <td><input id="illustration" class="easyui-textbox" style="width: 160px;"/></td>
    </tr>
  </table>
  <div style="text-align: center; bottom: 5px; margin-top: 20px;">
    <a href="#" id="btn_ok" class="easyui-linkbutton"
       data-options="iconCls:'icon-save'" style="width: 20%;">确定</a>
    <a href="#" id="btn_cancel" class="easyui-linkbutton"
       data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
  </div>
</div>
<div class="mdiv" style="width:100%;height:94%;">
  <table id="llbinfo" class="easyui-datagrid" title="待登记列表"
         style="width: auto; height:100%;">
    <thead>
    <tr>
      <th
          data-options="field:'id',width:fixWidth(0.15),align:'center'">审批单号
      </th>
      <th
          data-options="field:'department',width:fixWidth(0.08),align:'center'">部门
      </th>
      <th
          data-options="field:'travelers',width:fixWidth(0.11),align:'center'">出差人
      </th>
      <th
          data-options="field:'cause',width:fixWidth(0.11),align:'center'">事由
      </th>
      <th
          data-options="field:'tripDetails',width:fixWidth(0.30),align:'center'">出差地及方式
      </th>
      <th
          data-options="field:'beginTime',width:fixWidth(0.08),align:'center'">出发日期
      </th>
      <th
          data-options="field:'endTime',width:fixWidth(0.08),align:'center'">返回日期
      </th>
      <th
          data-options="field:'travelDays',width:fixWidth(0.06),align:'center'">出差天数
      </th>
      <th
          data-options="field:'totalBudget',width:fixWidth(0.06),align:'center'">总预算
      </th>
      <th
          data-options="field:'isTest',width:fixWidth(0.08),align:'center'">是否属于试验
      </th>
      <th
          data-options="field:'applyTime',width:fixWidth(0.08),align:'center'">申请时间
      </th>
      <th
          data-options="field:'applyMan',width:fixWidth(0.08),align:'center'">申请人
      </th>
      <th
          data-options="field:'approveMan',width:fixWidth(0.08),align:'center'">审批人
      </th>
      <th
          data-options="field:'remarks',width:fixWidth(0.1),align:'center'">备注
      </th>
      <th
			data-options="field:'taskId',width:fixWidth(0.06),hidden:'true'">任务id</th>
      <th
          data-options="field:'operation',width:fixWidth(0.16),formatter:operation,align:'center'">操作
      </th>
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
          <td><label>出差人</label></td>
          <td><input id="travelers" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>出差地</label></td>
          <td><input id="tripDetails" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>出发日期</label></td>
          <td><input id="beginTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>返回日期</label></td>
          <td><input id="endTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>申请人</label></td>
          <td><input id="applyMan" class="easyui-textbox" style="width: 220px;"/></td>
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