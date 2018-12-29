<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>差旅费已登记列表</title>
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
      //汉化 datagrid 翻页
      $("#llbinfo").datagrid({
        url: '${pageContext.request.contextPath}/approveExpenses/searApplyExpenseApprove',
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
          $('.preScan').linkbutton({text: '查看', plain: true, iconCls: 'icon-search'});
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
    	
    	$("#vehicleCost").click(function(){  
            alert("ok");  
        }); 
    	
    	
    	$('#addtestSite').combobox({
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
      loadGrid({state: 1});
      //查询用户
      $("#search_ok").click(
          function () {
            var param = {
              department: $("#department").combobox("getText"),
              travelers: $("#travelers").combotree("getText").trim(),
              tripDetails: $("#tripDetails").combotree("getText").trim(),
              beginTime: $("#beginTime").textbox("getText"),
              endTime: $("#addendTime").textbox("getText"),
              applyMan: $("#applyMan").textbox("getText").trim(),
              starTime:$("#starTime").textbox("getText"),
              searendTime:$("#searendTime").textbox("getText"),
              voucherNum:$("#searvoucherNum").textbox("getText")
            };
            $("#searchUser").dialog("close");
            console.log(param);
            loadGrid(param);
          });

     /*  $("#sonSearch_ok").click(
              function () {
                var param = {
                  searchBeginTime:$("#searchBeginTime").datebox("getValue"),
                  searchEndTime:$("#searchEndTime").datebox("getValue"),
                  voucherNum: $("#voucherNum").combotree("getText").trim(),
                  testSite: $("#testSite").textbox("getText")
                };
                console.log(param);
                $("#sonSearchUser").dialog("close");
                $("#studentInfo").datagrid({
                    url: '${pageContext.request.contextPath}/approveExpenses/searchApproveExpenses?travelExpenseId='+travelExpenseId,
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
                        	sonSearch();
                        }
                      },'-',{
                          text: '添加',
                          iconCls: 'icon-add',
                          handler: function () {
                             add();
                          }
                        },'-', {
                            text: '修改',
                            iconCls: 'icon-edit',
                            handler: function () {
                               edit();
                            }
                          },'-', {
                              text: '删除',
                              iconCls: 'icon-remove',
                              handler: function () {
                                 remove();
                              }
                            }]
                  });
              }); */
      //查询取消
      $("#search_cancel").click(function () {
        $("#searchUser").dialog("close");

      });
      $("#sonSearch_cancel").click(function () {
          $("#sonSearchUser").dialog("close");

        });
      //登记
      $("#btn_ok").click(function () {
        $('#numberbox').validatebox({required:true});
        if(i==0){
        	var param = "travelExpenseId="+$("#travelExpenseId").textbox("getText").trim()+"&result=true"
        	 "&isTestCost="+$("input[name='isTestCost']:checked").val();
            /* "&vehicleCost="+vehicleCost+
            "&foodAllowance="+foodAllowance+
            "&hotelExpense="+hotelExpense+
            "&parValueAllowance="+parValueAllowance+
            "&urbanTraffic="+urbanTraffic+
            "&otherCost="+otherCost+
            "&repayCost="+repayCost+
            "&supplementalCost="+supplementalCost+
            "&inputTax="+inputTax+
            "&totalExpenses="+totalExpenses+
            "&paidTime="+addpaidTime+
            "&voucherNum="+addvoucherNum+
            "&isTestCost="+$("input[name='isTestCost']:checked").val()+
            "&testSite="+addtestSite+
            "&illustration="+illustration */;
        	var vehicleCost = "";
        	if($("#vehicleCost").textbox("getText").length!=0){
        		vehicleCost = $("#vehicleCost").textbox("getText")
        		param+="&vehicleCost="+vehicleCost;
        	}
        	var foodAllowance = "";
        	if($("#foodAllowance").textbox("getText").trim().length!=0){
        		foodAllowance = $("#foodAllowance").textbox("getText").trim();
        		param+="&foodAllowance="+foodAllowance;
        	}
        	var hotelExpense = "";
        	if($("#hotelExpense").textbox("getText").trim().length!=0){
        		hotelExpense = $("#hotelExpense").textbox("getText").trim()
        		param+="&hotelExpense="+hotelExpense;
        	}
        	var parValueAllowance = "";
        	if($("#parValueAllowance").textbox("getText").trim().length!=0){
        		parValueAllowance = $("#parValueAllowance").textbox("getText").trim()
        		param+="&parValueAllowance="+parValueAllowance;
        	}
        	var urbanTraffic = "";
        	if($("#urbanTraffic").textbox("getText").length!=0){
        		urbanTraffic = $("#urbanTraffic").textbox("getText").trim()
        		param+="&urbanTraffic="+urbanTraffic;
        	}
        	var otherCost = "";
        	if($("#otherCost").textbox("getText").trim().length!=0){
        		otherCost = $("#otherCost").textbox("getText").trim()
        		param+="&otherCost="+otherCost;
        	}
        	var repayCost="";
        	if($("#repayCost").textbox("getText").trim().length!=0){
        		repayCost = $("#repayCost").textbox("getText").trim()
        		param+="&repayCost="+repayCost;
        	}
        	var supplementalCost ="";
        	if($("#supplementalCost").textbox("getText").trim().length!=0){
        		supplementalCost = $("#supplementalCost").textbox("getText").trim();
        		param+="&supplementalCost="+supplementalCost;
        	}
        	var inputTax = "";
        	if($("#inputTax").textbox("getText").trim().length!=0){
        		inputTax = $("#inputTax").textbox("getText").trim();
        		param+="&inputTax="+inputTax;
        	}
        	var totalExpenses = "";
        	if($("#totalExpenses").textbox("getText").trim().length!=0){
        		totalExpenses = $("#totalExpenses").textbox("getText").trim();
        		param+="&totalExpenses="+totalExpenses;
        	}
        	var addpaidTime= "";
        	if($("#addpaidTime").textbox("getText").length!=0){
        		addpaidTime = $("#addpaidTime").textbox("getText");
        		param+="&paidTime="+addpaidTime;
        	}
        	var addvoucherNum = "";
        	if($("#addvoucherNum").textbox("getText").trim().length!=0){
        		addvoucherNum = $("#addvoucherNum").textbox("getText").trim();
        		param+="&voucherNum="+addvoucherNum;
        	}
        	var addtestSite = "";
        	if($("#addtestSite").textbox("getText").trim().length!=0){
        		addtestSite = $("#addtestSite").textbox("getText").trim()
        		param+="&testSite="+addtestSite;
        	}
        	var illustration = "";
        	if($("#illustration").textbox("getText").trim().length!=0	){
        		illustration = $("#illustration").textbox("getText").trim()
        		param+="&illustration="+illustration;
        	}     	
        	console.log(param);
        	$.post("${pageContext.request.contextPath}/approveExpenses/insertFinanceReview", param, function (result) {
                if(result){
                    alert("插入成功");
                    $('#dlg').dialog('close');
                   console.log(result.id);
                    load(travelExpenseId);
                }else{
                    $("#dlg").dialog("close");
                    alert("插入失败");
                }
              })
        }else{
        	var param = "id="+$("#id").val()+"&travelExpenseId="+$("#travelExpenseId").textbox("getText")+
        	 "&isTestCost="+$("input[name='isTestCost']:checked").val();
            /* "&vehicleCost="+$("#vehicleCost").textbox("getText")+
            "&foodAllowance="+$("#foodAllowance").textbox("getText")+
            "&hotelExpense="+$("#hotelExpense").textbox("getText")+
            "&parValueAllowance="+$("#parValueAllowance").textbox("getText")+
            "&urbanTraffic="+$("#urbanTraffic").textbox("getText")+
            "&otherCost="+$("#otherCost").textbox("getText")+
            "&repayCost="+$("#repayCost").textbox("getText")+
            "&supplementalCost="+$("#supplementalCost").textbox("getText")+
            "&inputTax="+$("#inputTax").textbox("getText")+
            "&totalExpenses="+$("#totalExpenses").textbox("getText")+
            "&paidTime="+$("#addpaidTime").textbox("getText")+
            "&voucherNum="+$("#addvoucherNum").textbox("getText")+
            "&isTestCost="+$("input[name='isTestCost']:checked").val()+
            "&testSite="+$("#addtestSite").textbox("getText")+
            "&illustration="+$("#illustration").textbox("getText"); */
        	if($("#vehicleCost").textbox("getText").length!=0){
        		vehicleCost = $("#vehicleCost").textbox("getText")
        		param+="&vehicleCost="+vehicleCost;
        	}
        	var foodAllowance = "";
        	if($("#foodAllowance").textbox("getText").length!=0){
        		foodAllowance = $("#foodAllowance").textbox("getText");
        		param+="&foodAllowance="+foodAllowance;
        	}
        	var hotelExpense = "";
        	if($("#hotelExpense").textbox("getText").length!=0){
        		hotelExpense = $("#hotelExpense").textbox("getText");
        		param+="&hotelExpense="+hotelExpense;
        	}
        	var parValueAllowance = "";
        	if($("#parValueAllowance").textbox("getText").length!=0){
        		parValueAllowance = $("#parValueAllowance").textbox("getText");
        		param+="&parValueAllowance="+parValueAllowance;
        	}
        	var urbanTraffic = "";
        	if($("#urbanTraffic").textbox("getText").length!=0){
        		urbanTraffic = $("#urbanTraffic").textbox("getText");
        		param+="&urbanTraffic="+urbanTraffic;
        	}
        	var otherCost = "";
        	if($("#otherCost").textbox("getText").length!=0){
        		otherCost = $("#otherCost").textbox("getText");
        		param+="&otherCost="+otherCost;
        	}
        	var repayCost="";
        	if($("#repayCost").textbox("getText").length!=0){
        		repayCost = $("#repayCost").textbox("getText");
        		param+="&repayCost="+repayCost;
        	}
        	var supplementalCost ="";
        	if($("#supplementalCost").textbox("getText").length!=0){
        		supplementalCost = $("#supplementalCost").textbox("getText").trim();
        		param+="&supplementalCost="+supplementalCost;
        	}
        	var inputTax = "";
        	if($("#inputTax").textbox("getText").length!=0){
        		inputTax = $("#inputTax").textbox("getText");
        		param+="&inputTax="+inputTax;
        	}
        	var totalExpenses = "";
        	if($("#totalExpenses").textbox("getText").trim().length!=0){
        		totalExpenses = $("#totalExpenses").textbox("getText").trim();
        		param+="&totalExpenses="+totalExpenses;
        	}
        	var addpaidTime= "";
        	if($("#addpaidTime").textbox("getText").length!=0){
        		addpaidTime = $("#addpaidTime").textbox("getText");
        		param+="&paidTime="+addpaidTime;
        	}
        	var addvoucherNum = "";
        	if($("#addvoucherNum").textbox("getText").trim().length!=0){
        		addvoucherNum = $("#addvoucherNum").textbox("getText").trim();
        		param+="&voucherNum="+addvoucherNum;
        	}
        	var addtestSite = "";
        	if($("#addtestSite").textbox("getText").trim().length!=0){
        		addtestSite = $("#addtestSite").textbox("getText").trim()
        		param+="&testSite="+addtestSite;
        	}
        	var illustration = "";
        	if($("#illustration").textbox("getText").trim().length!=0	){
        		illustration = $("#illustration").textbox("getText").trim()
        		param+="&illustration="+illustration;
        	}       	
            console.log(param);
        	$.post("${pageContext.request.contextPath}/approveExpenses/modifyFinanceReview", param, function (result) {
                if(result){
                    alert("更新成功");
                    $('#dlg').dialog('close');
                   console.log(result.id);
                    load(travelExpenseId);
                }else{
                    $("#dlg").dialog("close");
                    alert("插入失败");
                }
              })
        }   
      });
      //登记取消
      $("#btn_cancel").click(function () {
        $('#dlg').dialog('close')
      });
      //单选是否属于试验
      $(":radio[name='isTestCost']").click(function () {
        var index = $(":radio[name='isTestCost']").index($(this));
        if (index == 0)
          $("#showTestSite").show();
        else
          $("#showTestSite").hide();
        $("#addtestSite").textbox("setValue", "");
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

    
    //查询框
    function search() {
      $("#department").combobox('setValue', '');
      $('#searchUser').dialog('open').dialog('setTitle', '查询');
    }
    //子页面查询框
    function sonSearch(){
    	$("#department").combobox('setValue', '');
    	$("input[name='sonisTestCost']:eq(0)").attr('checked',false);
    	$("input[name='sonisTestCost']:eq(1)").attr('checked',false);
         $('#sonSearchUser').dialog('open').dialog('setTitle', '查询');
    }

    //导出表单
    function exportExcel() {
    	var rows = $('#llbinfo').datagrid("getRows");
    	if(rows.length==0){
    		alert("没有数据");
    		return;
    	}
    	var department = $("#department").combobox("getText");
    	var url = '${pageContext.request.contextPath}/approveExpenses/exportApproveExpenses?1=1';
    	if(department.length!=0){
    		url+='&department='+department;
    	}
    	var travelers = $("#travelers").textbox("getText").trim();
    	if(travelers.length!=0){
    		url+='&travelers='+travelers;
    	}
    	var tripDetails = $("#tripDetails").textbox("getText").trim();
    	if(tripDetails.length!=0){
    		url+='&tripDetails='+tripDetails;
    	}
    	var beginTime = $("#beginTime").textbox("getText");
    	if(beginTime.length!=0){
    		url+='&beginTime='+beginTime;
    	}
    	var endTime = $("#addendTime").textbox("getText");
    	if(endTime.length!=0){
    		url+='&endTime='+endTime;
    	}
    	var applyMan = $("#applyMan").textbox("getText").trim();
    	if(applyMan.length!=0){
    		url+='&applyMan='+applyMan;
    	}
    	var starTime = $("#starTime").textbox("getText");
    	if(starTime.length!=0){
    		url+='&starTime='+starTime;
    	}
    	var searendTime = $("#searendTime").textbox("getText");
    	if(searendTime.length!=0){
    		url+='&searendTime='+searendTime;
    	}
    	var voucherNum = $("#searvoucherNum").textbox("getText");
    	if(voucherNum.length!=0){
    		url+='&voucherNum='+voucherNum;
    	}
    	 window.open(url);

    }

    // 操作列
    function operation(value, row, rowIndex) {
      //console.info(row.id);
      return '<a class="preScan" href="#" iconCls="icon-search" onclick="registerApply(' + rowIndex + ')">查看</a>';
     
    }


    function editApply(index){
        var row = $("#studentInfo").datagrid('getData').rows[index];
        $("#travelExpenseId").textbox("setValue", travelExpenseId);
        $('#dlg').dialog('open').dialog('setTitle', '修改');

    }
    
    function refuseApply(index) {
        var row = $('#llbinfo').datagrid('getData').rows[index];
        $.post("${pageContext.request.contextPath}/applyExpenses/financeReviewFail", "id=" + row.id, function (result) {
          if (result) {
            alert("驳回成功");
            loadGrid({state: 1});
          }
          else
            alert("驳回失败");
        });
      }

    var travelExpenseId = null;
    function registerApply(index) {
    	 var row = $('#llbinfo').datagrid('getData').rows[index];
    	 travelExpenseId = row.id;
    	 console.log(row);
         $('#dlg1').dialog('open').dialog('setTitle', '查看详细');
         load(row.id);
    }



    function load(id){
    	$("#studentInfo").datagrid({
            url: '${pageContext.request.contextPath}/approveExpenses/getroveExpenses?travelExpenseId='+id,
            method: 'post',
            pageSize: 20,
            rownumbers: true,
            singleSelect: true,
            pagination: true,
            nowrap: false,
            toolbar: [/* {
                text: '查询',
                iconCls: 'icon-search',
                handler: function () {
                	sonSearch();
                }
              },'-', */{
                  text: '添加',
                  iconCls: 'icon-add',
                  handler: function () {
                     add();
                  }
                },'-', {
                    text: '修改',
                    iconCls: 'icon-edit',
                    handler: function () {
                       edit();
                    }
                  },'-', {
                      text: '删除',
                      iconCls: 'icon-remove',
                      handler: function () {
                         remove();
                      }
                    }]
          });
       

    }
    
    //删除
    function  remove(){
        var row = $('#studentInfo').datagrid('getSelected');
        if (row){
            $.messager.confirm('确认对话框','你确定删除这条记录吗？',function(r){
            	console.log(row.id);
                    $.post('${pageContext.request.contextPath}/approveExpenses/deleteapproveExpenses',{Id:row.id},function(result){
                        if (result=="1"){
                            $('#llbinfo').datagrid('reload');    // reload the user data
                            $.messager.show({
                                title: 'Message',
                                msg: '删除成功'
                            });
                            load(travelExpenseId);
                        } else {
                            $.messager.show({    // show error message
                                title: 'Error',
                                msg: '删除失败'
                            });
                        }
                    },'text');
            });
        }else{
        	alert("请选择要删除的数据！");
        }
    }

    //变量俩区分是修改还是增加0是增加1是修改
    var i = 0;

    function edit(){
        i =1;
    	 es="编辑";
    	 var row = $('#studentInfo').datagrid('getSelected');
         if (row){
        
             $('#dlg').dialog('open').dialog('setTitle', '修改');
    		 //点击edit有字段的话必须 控件必须和字段名称一样 本次不一样 需要手动赋值
           	$("#travelExpenseId").textbox("setValue", row.travelExpenseId);
           	$("#vehicleCost").textbox("setValue",row.vehicleCost);
           	$("#foodAllowance").textbox("setValue",row.foodAllowance);
           	$("#hotelExpense").textbox("setValue",row.hotelExpense);
           	$("#parValueAllowance").textbox("setValue",row.parValueAllowance);
           	$("#urbanTraffic").textbox("setValue",row.urbanTraffic);
           	$("#otherCost").textbox("setValue",row.otherCost);
           	$("#inputTax").textbox("setValue",row.inputTax);
           	$("#repayCost").textbox("setValue",row.repayCost);
           	$("#addpaidTime").datebox("setValue",row.paidTime);
           	$("#addvoucherNum").textbox("setValue",row.voucherNum);
            $("#id").val(row.id);
           	if(row.isTestCost=="否"){
               	$(":radio[name='isTestCost'][value='" + row.isTestCost + "']").prop("checked", "checked");
               	$("#showTestSite").hide();

            }else{
            	$(":radio[name='isTestCost'][value='" + row.isTestCost + "']").prop("checked", "checked");
            	$("#showTestSite").show();
            	$("#addtestSite").textbox("setValue",row.testSite);
           }
           	
    	}else{
    		alert("请选择要修改的数据！");
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

    function add(){
        i=0;
    	$("#travelExpenseId").textbox("setValue", travelExpenseId);
    	$("#vehicleCost").textbox("setValue","");
       	$("#foodAllowance").textbox("setValue","");
       	$("#hotelExpense").textbox("setValue","");
       	$("#parValueAllowance").textbox("setValue","");
       	$("#urbanTraffic").textbox("setValue","");
       	$("#otherCost").textbox("setValue","");
       	$("#inputTax").textbox("setValue","");
       	$("#repayCost").textbox("setValue","");
       	$("#addpaidTime").datebox("setValue","");
       	$("#totalExpenses").textbox("setValue","");
       	$("#supplementalCost").textbox("setValue","");
       	$("#addvoucherNum").textbox("setValue","");
       	$("#addtestSite").textbox("setValue","");
        var row = $('#studentInfo').datagrid('getSelected');
        if(row){
        	if(row.isTestCost=="否"){
               	$(":radio[name='isTestCost'][value='" + row.isTestCost + "']").prop("checked", "checked");
               	$("#showTestSite").hide();

            }else{
            	$(":radio[name='isTestCost'][value='" + row.isTestCost + "']").prop("checked", "checked");
            	$("#showTestSite").show();
            	$("#addtestSite").textbox("setValue",row.testSite);
           }
        }else{
        	alert("请选择要关联的数据！");
        	return;
        }
       /* 	$(":radio[name='isTestCost'][value='否']").prop("checked", "checked");
       	$("#showTestSite").hide(); */
       	$("#illustration").textbox("setValue","");
        $('#dlg').dialog('open').dialog('setTitle', '添加');
    }


    
  </script>
</head>
<body>
<div class="mdiv" style="width:100%;height:94%;">
  <table id="llbinfo" class="easyui-datagrid" title="已登记列表"
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
          data-options="field:'operation',width:fixWidth(0.16),formatter:operation,align:'center'">操作
      </th>
    </tr>
    </thead>
  </table>
</div>

<!-- 添加修改对话框 -->

<div id="dlg1" class="easyui-dialog"
		style="width: 70%; height: 420px; padding: 0px 0px" closed="true"
		buttons="#dlg-buttons">
			<table id="studentInfo" class="easyui-datagrid" title="差旅费报销"
			style="width: 100%; height: 380px;"> 
			
			<thead>
				<tr>
					<th 
						data-options="field:'travelExpenseId',width:fixWidth(0.05),align:'center'">审批单号</th>
					<th 
						data-options="field:'vehicleCost',width:fixWidth(0.05),align:'center'">交通工具金额</th>
					<th 
						data-options="field:'foodAllowance',width:fixWidth(0.04),align:'center'">伙食费补助</th>
					<th 
						data-options="field:'hotelExpense',width:fixWidth(0.04),align:'center'">住宿费</th>
					<th 
						data-options="field:'parValueAllowance',width:fixWidth(0.04),align:'center'">票面补助</th>
					<th 
						data-options="field:'urbanTraffic',width:fixWidth(0.04),align:'center'">市内交通</th>
					<th 
						data-options="field:'otherCost',width:fixWidth(0.04),align:'center'">其他费用</th>
					<th 
						data-options="field:'repayCost',width:fixWidth(0.04),align:'center'">还款金额</th>
						<th 
						data-options="field:'supplementalCost',width:fixWidth(0.04),align:'center'">补领金额</th>
						<th 
						data-options="field:'inputTax',width:fixWidth(0.04),align:'center'">进项金额</th>
						<th 
						data-options="field:'totalExpenses',width:fixWidth(0.04),align:'center'">报销总额</th>
						<th 
						data-options="field:'paidTime',width:fixWidth(0.04),align:'center'">报销时间</th>
					<!-- 	<th 
						data-options="field:'fundSource',width:fixWidth(0.04),align:'center'">经费来源</th> -->
						<th 
						data-options="field:'voucherNum',width:fixWidth(0.03),align:'center'">凭证号</th>
						<th 
						data-options="field:'isTestCost',width:fixWidth(0.08),align:'center'">是否为试验费</th>
						<th 
						data-options="field:'testSite',width:fixWidth(0.08),align:'center'">试验现场代号</th>
						<th 
						data-options="field:'illustration',width:fixWidth(0.03),align:'center'">备注</th>
						<th  hidden="true" data-options="field:'id',align:'center'">id
						</th>
				</tr>
				
			</thead>
		</table>
	</div>


<!-- 查询信息对话框 -->
<div id="searchUser" class="easyui-dialog"
     style="width: 400px; height: 420px; padding: 10px 20px" closed="true">
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
          <td><input id="addendTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>申请人</label></td>
          <td><input id="applyMan" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>开始报销日期</label></td>
          <td><input id="starTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>结束报销日期</label></td>
          <td><input id="searendTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/></td>
        </tr>
        <tr>
          <td><label>试验现场代号</label></td>
          <td><input id="searvoucherNum" class="easyui-textbox"
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
<!-- 子页面查询对话框 -->
<div id="sonSearchUser" class="easyui-dialog"
     style="width: 400px; height: 320px; padding: 10px 20px" closed="true">
  <form id="sfm" method="post">
    <div style="text-align: center;">
      <table style="margin: auto;" cellspacing="10">
        <tr>
          <td><label>开始时间</label></td>
          <td><input id="searchBeginTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/>
                     </td>
        </tr>
        <tr>
          <td><label>结束时间</label></td>
          <td><input id="searchEndTime" class="easyui-datebox"
                     data-options="sharedCalendar:'#cc'" style="width: 220px;"/>
                     </td>
        </tr>
        <!-- <tr>
          <td><label>经费来源</label></td>
          <td><input id="fundSource" class="easyui-textbox" style="width: 220px;"/></td>
        </tr> -->
        <tr>
          <td><label>凭证号</label></td>
          <td><input id="voucherNum" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
       <!--  <tr>
          <td><label>是否为试验费</label></td>
          <td>
        <span class="radioSpan">
          <input type="radio" name="sonisTestCost" value="是"/>是
          <input type="radio" name="sonisTestCost" value="否"/>否
        </span>
      </td>
        </tr> -->
        <tr>
          <td><label>试验现场代号</label></td>
          <td><input id="testSite" class="easyui-textbox" style="width: 220px;"/></td>
        </tr>
      </table>
      <div id="cc" class="easyui-calendar"></div>
    </div>
    <div style="text-align: center; bottom: 15px; margin-top: 20px;">
      <a href="#" id="sonSearch_ok" class="easyui-linkbutton"
         data-options="iconCls:'icon-search'" style="width: 20%;">查询</a> <a
        href="#" id="sonSearch_cancel" class="easyui-linkbutton"
        data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
    </div>
  </form>
</div>

<!-- 审核登记对话框 -->
<div id="dlg" class="easyui-dialog"
     style="width: 700px; height: 600px; padding: 0px 0px" closed="true"
     buttons="#dlg-buttons">
  <table align="center" style="margin-top:20px;">
    <tr>
      <td><label>审批单号</label></td>
      <td><input id="travelExpenseId" class="easyui-textbox" style="width: 160px;" disabled/></td>
    </tr>
    <tr>
      <td><label>交通工具金额</label></td>
      <td><input id="vehicleCost"  name="needAdd" class="easyui-numberbox"
                 min="0.01" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>伙食费补助</label></td>
      <td><input id="foodAllowance" name="needAdd" class="easyui-numberbox"
                 min="0.01" precision="2" type="text" style="width: 160px;"/></td>
    </tr>
    <tr>
      <td><label>住宿费</label></td>
      <td><input id="hotelExpense" name="needAdd" class="easyui-numberbox"
                 min="0.01" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>票面补助</label></td>
      <td><input id="parValueAllowance" name="needAdd" class="easyui-numberbox"
                 min="0.01" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>市内交通</label></td>
      <td><input id="urbanTraffic" name="needAdd" class="easyui-numberbox"
                 min="0.00" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>其他费用</label></td>
      <td><input id="otherCost" name="needAdd" class="easyui-numberbox"
                 precision="2" value="0.00" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>进项税额</label></td>
      <td><input id="inputTax" name="needAdd" class="easyui-numberbox"
                 min="0.00" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>报销总额</label></td>
      <td><input id="totalExpenses" class="easyui-numberbox"
                 min="0.00" precision="2" type="text" style="width: 160px;" disabled /></td>
    </tr>
    <tr>
      <td><label>还款金额</label></td>
      <td><input id="repayCost" class="easyui-numberbox"
                 min="0.00" precision="2" type="text" style="width: 160px;" /></td>
    </tr>
    <tr>
      <td><label>补领金额</label></td>
      <td><input id="supplementalCost" class="easyui-numberbox"
                 min="0.00" precision="2" type="text" style="width: 160px;" disabled/></td>
    </tr>
    <tr>
      <td><label>报销时间</label></td>
      <td><input id="addpaidTime" class="easyui-datebox" data-options="sharedCalendar:'#cc'" style="width: 160px;" required="true"/></td>
    </tr>
    <!-- <tr>
      <td><label>经费来源</label></td>
      <td><input id="addfundSource" class="easyui-textbox" style="width: 160px;" required="true"/></td>
    </tr> -->
    <tr>
      <td><label>凭证号</label></td>
      <td><input id="addvoucherNum" class="easyui-textbox" style="width: 160px;" required="true"/></td>
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
      <td><input id="addtestSite" class="easyui-combobox"  data-options="editable:false,valueField:'id', textField:'testSite'" style="width: 160px;" required="true"/></td>
    </tr>
    <tr>
      <td><label>说明</label></td>
      <td><input id="illustration" class="easyui-textbox" style="width: 160px;"/>
      	  <input id="id"  type="hidden">
      </td>
    </tr>
  </table>
  <div style="text-align: center; bottom: 5px; margin-top: 20px;">
    <a href="#" id="btn_ok" class="easyui-linkbutton"
       data-options="iconCls:'icon-save'" style="width: 20%;">确定</a>
    <a href="#" id="btn_cancel" class="easyui-linkbutton"
       data-options="iconCls:'icon-cancel'" style="width: 20%;">取消</a>
  </div>
</div>


</body>
</html>