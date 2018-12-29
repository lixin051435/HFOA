const app = getApp();

// 全局工具模块
const util = require('../../../../../utils/util');

// 本页面用到的工具模块
const apply_util = require('./util');

const root = getApp().globalData.URL;

// 修改URL
const url = app.globalData.URL + '/applyExpenses/updateApplyExpense';

// 获取审批人
const finderURL = app.globalData.URL + '/user/getFinder';

// 获取抄送人
const ccurl = app.globalData.URL + '/user/getCcList';

// 获取出行方式
const travelModeURL = app.globalData.URL + '/dictionary/selectTravel';

/**
 * 
 * @param {string} from 出差地1 
 * @param {string} to 出差地2
 * @param {string} mode 出行方式
 */
function Detail(beginAddress, endAddress, vehicle) {
  this.beginAddress = beginAddress || "";
  this.endAddress = endAddress || "";
  this.vehicle = vehicle || "请选择出行方式";
}

// 试验类型对象
function TestObject(name = "", checked = false) {
  this.name = name;
  this.checked = checked;
}

function parseTripDetails(tripDetails) {
  let tripDetailsArray = [];
  let arrays = tripDetails.split("*");
  arrays.forEach(function (e) {
    let beginAddress = e.split(",")[0];
    let endAddress = e.split(",")[1];
    let vehicle = e.split(",")[2];

    let detail = new Detail(beginAddress, endAddress, vehicle);
    tripDetailsArray.push(detail);
  });
  return tripDetailsArray;
}

Detail.prototype.toString = function () {
  return this.beginAddress + "," + this.endAddress + "," + this.vehicle;
}

Page({
  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    approveMan: "请选择审批人",
    // ArrayList<Detail>
    lists: [],
    // 试验类型
    trailType: "陆上试验",
    // 是否属于试验
    isTest: "否",
    index: 0,
  },
  split(testList) {

    let list = [];
    testList.forEach(function (e) {
      if (e.checked == true) {
        list.push(e.name);
      }
    });

    return list.join(",");


  },

  bindApproveMan: function (e) {
    let index = e.detail.value;
    let info = this.data.info;
    info.approveMan = this.data.finders[index];
    info.approveManOpenId = apply_util.getOpenIdByName(this.data.finders[index], this.data.finderObjectList);
    this.setData({
      info: info
    });
  },
  // 弹出框蒙层截断touchmove事件
  preventTouchMove: function () {},

  // 隐藏模态对话框
  hideModal: function () {
    this.setData({
      showModal: false
    });
  },

  // 对话框取消按钮点击事件
  onCancel: function () {
    // 清空已选数组
    this.setData({
      showMoreInfo: false,
      cCListOpenIdName: "",
      ccnames: [],
      ccopenids: []
    })
    this.hideModal();
  },

  // 对话框确认按钮点击事件
  onConfirm: function () {
    // let cCListOpenIdName = apply_util.arrayToString(this.data.ccnames);
    // this.setData({
    //   cCListOpenIdName: cCListOpenIdName
    // });

    this.hideModal();
  },
  showDialogBtn: function () {
    this.setData({
      showModal: true
    })
  },

  setFrom: function (e) {
    let info = this.data.info;
    let index = parseInt(e.currentTarget.id.replace("src-", ""));
    let value = e.detail.value;
    let list = info.lists;
    list[index].beginAddress = value;
    this.setData({
      lists: list
    });
  },

  setTo: function (e) {
    let info = this.data.info;
    let index = parseInt(e.currentTarget.id.replace("dest-", ""));
    let value = e.detail.value;
    let list = info.lists;
    list[index].endAddress = value;
    this.setData({
      lists: list
    });
  },

  bindPickerValues: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    let index = parseInt(e.currentTarget['id'].split('-')[1]); //得到index整数
    let mode = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");

    // 更新lists
    let info = this.data.info;
    let list = info.lists;
    list[index].vehicle = mode;

    // 复原 multiArray
    let multiArray = [];
    multiArray.push(this.data.categories);
    multiArray.push(this.data.names[0]);
    this.setData({
      info: info,
      multiArray: multiArray
    });
  },

  addItem: function () {
    let info = this.data.info;
    let items = info.lists;
    let detail = new Detail();
    detail.modes = util.deepClone(this.data.multiArray);
    items.push(detail);
    this.setData({
      info: info
    });
  },

  removeItem: function () {
    let info = this.data.info;
    let items = info.lists;
    items.pop();
    this.setData({
      info: info
    });
  },

  // 去的出行方式
  bindMultiPickerChange: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    let info = this.data.info;
    info.goTravelMode = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");

    // 复原 multiArray
    let multiArray = [];
    multiArray.push(this.data.categories);
    multiArray.push(this.data.names[0]);
    this.setData({
      info: info,
      multiArray: multiArray
    });
  },

  // 回的出行方式
  setBackTravelMode: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    let info = this.data.info;
    info.backTravelMode = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");

    // 复原 multiArray
    let multiArray = [];
    multiArray.push(this.data.categories);
    multiArray.push(this.data.names[0]);
    this.setData({
      info: info,
      multiArray: multiArray
    });
  },

  // 出行方式改变事件
  bindMultiPickerColumnChange: function (e) {
    let column = e.detail.column;
    let value = e.detail.value;
    let info = this.data.info;
    // 如果改变第一列 则实时改变第二列的值
    if (column == 0) {
      let multiArray = [];
      multiArray.push(this.data.categories);
      multiArray.push(this.data.names[value]);
      let item = e.currentTarget.dataset.item;
      switch (item) {
        case "goTravelMode":
          info.goModes = util.deepClone(multiArray);
          break;
        case "backTravelMode":
          info.backModes = util.deepClone(multiArray);
          break;
        default:
          let index = parseInt(item.replace("picker-", ""));
          let lists = this.data.lists;
          lists[index].modes = util.deepClone(multiArray);
          this.setData({
            lists: lists
          });
          break;
      }
      this.setData({
        info: info
      });
    }
  },

  handleIsTrial: function (e) {
    let that = this;
    let info = this.data.info;
    let testList = this.data.testList;
    switch (e.detail.value) {
      case "1":
        testList.forEach(function (e) {
          e.checked = false;
        });
        info.isTrial = false;
        break;
      case "2":
        info.isTrial = true;
        break;
    }
    that.setData({
      info: info,
      testList: testList
    });
  },

  handleTrialType: function (e) {
    let that = this;
    let indexes = e.detail.value;
    let testList = that.data.testList;

    // 重置checked属性
    testList.forEach(function (e) {
      e.checked = false;
    })

    // 把选中的item赋值checked = true
    indexes.forEach(function (e) {
      testList[e].checked = true;
    });

    that.setData({
      testList: testList
    });
  },

  // 绑定申请时间
  bindApplyTime: function (e) {
    let info = this.data.info;
    if (util.timeHasPassed(e.detail.value)) {
      util.showError(this, "时间不能早于今天");
      return false;
    } else {
      info.applyTime = e.detail.value;
      this.setData({
        info: info
      });
    }
  },

  // 选择行程：往返还是多程
  radioChange: function (e) {
    let that = this;
    let info = that.data.info;
    let value = e.detail.value;
    switch (value) {
      case "1":
        // 单程
        // 取消多程添加和删除的input框
        info.tripMode = value;
        info.isMulti = false;
        info.startAddress = info.endAddress;
        info.middAddress = info.lastPlace;
        info.lists = []
        break;
      case "2":
        info.isMulti = true;
        info.tripMode = value;
        break;
    }
    that.setData({
      info: info
    });
  },

  bindBeginTime: function (e) {
    let that = this;
    let info = this.data.info;
    // if (util.timeHasPassed(e.detail.value)) {
    //   util.showError(that, "时间不能早于今天");
    //   return false;
    // } else {
    //   let endTime = new Date(this.data.info.endTime).getTime();
    //   let startTime = new Date(e.detail.value).getTime();
    //   if (endTime < startTime) {
    //     util.showError(that, "结束时间不能早于出行时间");
    //     return false;
    //   } else {
    //     info.beginTime = e.detail.value;
    //     this.setData({
    //       info: info
    //     });
    //   }
    // }

    let endTime = new Date(this.data.info.endTime).getTime();
    let startTime = new Date(e.detail.value).getTime();
    if (endTime < startTime) {
      util.showError(that, "结束时间不能早于出行时间");
      return false;
    } else {
      info.beginTime = e.detail.value;
      this.setData({
        info: info
      });
    }


    if (info.beginTime != "请选择出差日期" && info.endTime != "请选择返回日期") {
      info.travelDays = util.getDaysByPickers(info.beginTime, info.endTime);
      this.setData({
        info: info
      });
    }
  },

  bindEndTime: function (e) {
    let that = this;
    let info = this.data.info;
    let startTime = new Date(this.data.info.beginTime).getTime();
    let endTime = new Date(e.detail.value).getTime();
    // if (util.timeHasPassed(e.detail.value)) {
    //   util.showError(that, "时间不能早于今天");
    //   return;
    // }
    if (endTime < startTime) {
      util.showError(that, "结束时间不能早于出行时间");
      return false;
    } else {
      info.endTime = e.detail.value;
      this.setData({
        info: info
      });

    }
    if (info.beginTime != "请选择出差日期" && info.endTime != "请选择返回日期") {
      info.travelDays = util.getDaysByPickers(info.beginTime, info.endTime);
      this.setData({
        info: info
      });
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

    // 初始化试验类型列表
    let testList = [];
    let obj = new TestObject("陆上试验");
    testList.push(obj);
    obj = new TestObject("系泊航行试验");
    testList.push(obj);
    obj = new TestObject("出海航行试验");
    testList.push(obj);
    this.setData({
      testList: testList
    });

    let info = JSON.parse(options.item);
    let notaskid = options.notaskid || -1;

    // 设置中间过程 和 设置是否多途和往返
    // let tripDetailList = parseTripDetails(info.tripDetails);
    let tripDetailList = JSON.parse(info.tripDetails);
    let length = tripDetailList.length;

    // tripMode == 1表示往返，2表示多程
    if (info.tripMode == "1") {
      info.isMulti = false;
    } else if (info.tripMode == "2") {
      info.isMulti = true;
    }

    // 中间过程对象列表  并赋值
    let lists = [];
    for (let i = 1; i < length - 1; i++) {
      lists.push(tripDetailList[i]);
      // lists[i].modes = util.deepClone(this.data.multiArray);
    }
    info.lists = lists;

    // 回显试验类型
    if (info.isTest == "否") {
      info.isTrial = false;
    } else {
      info.isTrial = true;
      let testnames = info.isTest.split(",");
      let testList = this.data.testList;
      for (let i = 0; i < testnames.length; i++) {
        for (let j = 0; j < testList.length; j++) {
          if (testnames[i] == testList[j].name) {
            testList[j].checked = true;
          }
        }
      }

      this.setData({
        testList: testList
      });
    }
    this.setData({
      info: info,
      notaskid: notaskid
    });


    // 初始化页面 设置时间相关
    let that = this;
    // 获取审批人 
    wx.request({
      url: finderURL,
      method: 'GET',
      data: {
        openId: wx.getStorageSync('openId'),
        moduleNum: 5
      },
      success: function (res) {
        let user = res.data.data.user;
        let finders = res.data.data.finder;
        let finderList = [];
        for (let i = 0; i < finders.length; i++) {
          finderList.push(finders[i].realname);
        }
        that.setData({
          department: user.departmentname,
          finders: finderList,
          finderObjectList: finders,
          applyMan: user.realname
        });
      }
    });


    // 获取抄送人  基本没用
    wx.request({
      url: ccurl,
      method: "GET",
      data: {
        openId: wx.getStorageSync("openId"),
        moduleNum: 5
      },
      success: function (res) {
        // 所有抄送人对象列表
        let ccObjectList = res.data.data;

        // 所有抄送人姓名列表
        let ccNameList = [];
        for (let i = 0; i < ccObjectList.length; i++) {
          ccNameList.push(ccObjectList[i].realname);
        }

        // 之前已经选过的的抄送人姓名列表
        let ccnames = info.cCListOpenIdName.split(",");
        // 之前已经选过的抄送人openid列表
        let ccopenids = info.cCListOpenId.split(",")



        // 遍历原有抄送人列表 如果有后台传过来的ccnames中的人，将该对象设置成checked = true
        for (let i = 0; i < ccObjectList.length; i++) {
          for (let j = 0; j < ccnames.length; j++) {
            if (ccObjectList[i].realname == ccnames[j]) {
              ccObjectList[i].checked = "true";
            }
          }
        }

        that.setData({
          ccObjectList: ccObjectList,
          ccNameList: ccNameList,
          info: info
        });
      }
    });
    // 获取出行方式
    wx.request({
      url: travelModeURL,
      method: "GET",
      data: {
        openId: wx.getStorageSync("openId")
      },
      success: function (res) {
        // categories 是大类
        let categories = [];
        // names 是大类中对应的具体方式
        let names = [];
        res.data.data.forEach(e => {
          categories.push(e.dict.text);
          let temp = [];
          e.listDict.forEach(ele => {
            temp.push(ele.text);
          });
          names.push(temp);
        });

        let info = that.data.info;
        let multiArray = [];
        multiArray.push(categories);
        multiArray.push(names[0]);

        info.goModes = util.deepClone(multiArray);
        info.backModes = util.deepClone(multiArray);

        // 给每个Detail添加自己的出行方式
        let lists = info.lists;
        // console.log(lists);
        for (let i = 0; i < lists.length; i++) {
          // lists.push(tripDetailList[i]);
          lists[i].modes = util.deepClone(multiArray);
        }
        info.lists = lists;

        that.setData({
          categories: categories,
          names: names,
          multiArray: multiArray,
          info: info
        });
      }
    });
  },

  checkboxChange: function (e) {
    // 获取选中的索引列表
    let indexes = e.detail.value;
    for (let i = 0; i < indexes.length; i++) {
      indexes[i] = parseInt(indexes[i]);
    }

    // 选中checkbox
    let ccObjectList = this.data.ccObjectList;
    for (let i = 0; i < ccObjectList.length; i++) {
      ccObjectList[i].checked = false;
    }
    for (let i = 0; i < indexes.length; i++) {
      indexes[i] = parseInt(indexes[i]);
      ccObjectList[indexes[i]].checked = true;
    }


    // 根据name 获取 openid
    let ccnames = [];
    let ccopenids = [];
    for (let i = 0; i < indexes.length; i++) {
      ccopenids.push(apply_util.getOpenIdByName(this.data.ccNameList[indexes[i]], this.data.ccObjectList));
      ccnames.push(this.data.ccNameList[indexes[i]]);
    }

    let info = this.data.info;
    info.ccnames = ccnames;
    info.ccopenids = ccopenids;
    this.setData({
      info: info,
      ccObjectList: ccObjectList
    });
  },
  inputedit: function (e) {
    let item = e.currentTarget.dataset.item;
    let info = this.data.info;
    let value = e.detail.value;
    info[item] = value;
    switch (item) {
      case "startAddress":
      case "endAddress":
        if (info.isMulti == false) {
          info.endAddress = value;
          info.startAddress = value;
        }
        break;
      case "middAddress":
      case "lastPlace":
        if (info.isMulti == false) {
          info.middAddress = value;
          info.lastPlace = value;
        }
        break;
    }
    this.setData({
      info: info
    });

  },

  /**
   * 
   * @param {JSON} data
   * 判断提交给后台的数据是否合法 
   */
  checkData(data) {
    if (util.isEmpty(data.department)) {
      util.showError(this, "请填写所在部门");
      return false;
    }
    if (util.isEmpty(data.travelers)) {
      util.showError(this, "请填写出差人");
      return false;
    }
    if (util.isEmpty(data.cause)) {
      util.showError(this, "请填写出差事由");
      return false;
    }
    if (util.isEmpty(data.startAddress)) {
      util.showError(this, "请填写起点");
      return false;
    }
    if (util.isEmpty(data.middAddress)) {
      util.showError(this, "请填写出差地");
      return false;
    }
    if (util.isEmpty(data.goTravelMode) || data.goTravelMode.indexOf("请选择") > -1) {
      util.showError(this, "请选择出行方式");
      return false;
    }
    if (util.isEmpty(data.lastPlace)) {
      util.showError(this, "请填写出差地");
      return false;
    }
    if (util.isEmpty(data.endAddress)) {
      util.showError(this, "请填写终点");
      return false;
    }
    if (util.isEmpty(data.backTravelMode) || data.backTravelMode.indexOf("请选择") > -1) {
      util.showError(this, "请选择出行方式");
      return false;
    }
    if (util.isEmpty(data.beginTime) || data.beginTime.indexOf("请选择") > -1) {
      util.showError(this, "请填写出行日期");
      return false;
    }
    if (util.isEmpty(data.endTime) || data.endTime.indexOf("请选择") > -1) {
      util.showError(this, "请填写返回日期");
      return false;
    }
    if (util.isEmpty(data.applyMan)) {
      util.showError(this, "请填写申请人");
      return false;
    }
    if (util.isEmpty(data.applyTime) || data.applyTime.indexOf("请选择") > -1) {
      util.showError(this, "请选择申请时间");
      return false;
    }
    if (util.isEmpty(data.approveMan) || data.approveMan.indexOf("请选择") > -1) {
      util.showError(this, "请选择审批人");
      return false;
    }
    if (util.isEmpty(data.tripDetails) || data.tripDetails.indexOf(",,") > -1 || data.tripDetails.indexOf("*,") > -1 || data.tripDetails.indexOf("请选择") > -1) {
      util.showError(this, "请补全出行过程");
      return false;
    }
    if (util.isEmpty(data.totalBudget)) {

    } else if (isNaN(data.totalBudget)) {
      util.showError(this, "总预算必须是数字");
      return false;
    }

    // 如果抄送人列表不为空 则校验抄送人
    if (this.data.ccObjectList != null && this.data.ccObjectList.length > 0) {
      if (util.isEmpty(data.cCListOpenIdName) || util.isEmpty(data.cCListOpenId)) {
        util.showError(this, "请选择抄送人");
        return false;
      }
    }
    return true;
  },
  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    let lists = info.lists;
    let travelDatails = "";
    let detail = null;
    if (lists.length == 0) {
      // 没有添加出差地  补全travelDatails
      travelDatails = apply_util.generateTravelObject(info.startAddress, info.middAddress, info.goTravelMode) + "*" + apply_util.generateTravelObject(info.lastPlace, info.endAddress, info.backTravelMode);
    } else {
      let modes = this.data.pickerValue;
      travelDatails = apply_util.generateTravelObject(info.startAddress, info.middAddress, info.goTravelMode) + "*";
      for (let i = 0; i < lists.length; i++) {
        detail = new Detail(lists[i].beginAddress, lists[i].endAddress, lists[i].vehicle);
        travelDatails += detail.toString() + "*";
      }
      travelDatails += apply_util.generateTravelObject(info.lastPlace, info.endAddress, info.backTravelMode);
    }


    // 生成试验类型
    let isTest = that.split(that.data.testList);
    if (info.isTrial == false) {
      isTest = "否";
    } else {
      if (isTest == "") {
        isTest = "否";
      }
    }


    // 生成后台需要的json
    let data = {};
    data.department = info.department;
    data.travelers = info.travelers;
    data.beginTime = info.beginTime;
    data.endTime = info.endTime;
    data.cause = info.cause;
    data.travelDays = info.travelDays;
    data.totalBudget = info.totalBudget || 0;
    data.isTest = info.isTest;
    data.applyTime = info.applyTime;
    data.applyMan = info.applyMan;
    data.approveMan = info.approveMan;
    data.remarks = info.remarks;
    data.tripMode = info.isMulti == true ? "2" : "1";
    data.lastPlace = info.lastPlace;
    data.startAddress = info.startAddress;
    data.endAddress = info.endAddress;
    data.middAddress = info.middAddress;
    data.goTravelMode = info.goTravelMode;
    data.backTravelMode = info.backTravelMode;
    data.tripDetails = travelDatails;
    data.approveManOpenId = info.approveManOpenId;
    data.openId = wx.getStorageSync('openId');
    data.approveMan = info.approveMan;
    data.isTest = isTest;
    data.taskId = info.taskId;

    // if (this.data.notaskid == -1) {
    //   data.taskId = info.taskId;
    // } else {
    //   delete data.taskId;
    // }
    data.id = info.id;
    data.cCListOpenId = info.cCListOpenId;
    data.cCListOpenIdName = info.cCListOpenIdName;
    // 前端校验
    let flag = that.checkData(data);
    if (flag) {
      if (that.data.hasCommited == undefined) {
        wx.request({
          url: url,
          data: data,
          method: 'GET',
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=3");
            else {
              console.log("差旅修改：服务器内部错误");
              util.showError(that, "差旅修改：服务器内部错误");
              let hasCommited = undefined;
              that.setData({
                hasCommited: hasCommited
              });
            }

          },
          fail: function (res) {
            console.log("出错了");
            let hasCommited = undefined;
            that.setData({
              hasCommited: hasCommited
            });
          }
        });
        let hasCommited = true;
        that.setData({
          hasCommited: hasCommited
        });
        util.showLoading();
      } else {
        util.showError(this, "您已经提交，请勿重复提交");
        return false;
      }

    }
  }
})