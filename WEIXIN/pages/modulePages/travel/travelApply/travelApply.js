const app = getApp();

// 全局工具模块
const util = require('../../../../utils/util.js');

// 本页面用到的工具模块
const apply_util = require('./util');

// 插入url
const url = app.globalData.URL + '/applyExpenses/insertApply';

// 获取审批人
const finderURL = app.globalData.URL + '/user/getFinder';

// 获取抄送人
const ccurl = app.globalData.URL + '/user/getCcList';

// 获取出行方式
const travelModeUrl = app.globalData.URL + '/dictionary/selectTravel';

// 获取默认抄送人
const defaultccURL = app.globalData.URL + '/user/defaultgetCcList';

/**
 * 
 * @param {string} from 出差地1 
 * @param {string} to 出差地2
 * @param {string} mode 出行方式
 */
function Detail(from, to, mode) {
  this.from = from || "";
  this.to = to || "";
  this.mode = mode || "请选择出行方式";
}

// 试验类型对象
function TestObject(name = "", checked = false) {
  this.name = name;
  this.checked = checked;
}

Detail.prototype.toString = function () {
  return this.from + "," + this.to + "," + this.mode;
}

function Info() {
  this.beginTime = util.getBeginAndEndDate().endTime;
  this.endTime = "请选择返回日期";
  this.applyTime = util.getBeginAndEndDate().endTime;
  this.goTravelMode = "请选择出行方式";
  this.backTravelMode = "请选择出行方式";
  // 试验类型 如果没选就是否 如果选了就是试验类型
  this.trialType = "否";
  this.applyMan = wx.getStorageSync("username");
  this.department = wx.getStorageSync("departmentName");
}

Page({
  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    // 中间过程 ArrayList<Detail>
    lists: [],

    // 试验类型picker列表
    testList: [],
    info: {},
    // 是否勾选了多程(往返还是多程)
    isMulti: false,
    // 是否勾选了试验
    isTrial: false,
    isOverproof: false
  },

  checkData: function (data) {
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

    if (data.approveManOpenId.indexOf("审批人") > -1) {
      util.showError(this, "该审批人尚未绑定，请重新选择");
      return false;
    }
    return true;
  },
  /**
   * 判断出行方式是否超标
   * @param {string} travelType 出行方式
   */
  isOverproof(travelType) {
    let isOverproof = false;
    switch (travelType) {
      case "高铁一等座":
      case "轮船二等舱":
      case "动车一等座":
      case "城际列车一等座":
        isOverproof = true;
        break;
    }
    return isOverproof;
  },


  // 判断是否点击了 试验
  handleIsTrial: function (e) {
    let that = this;
    let info = this.data.info;
    let testList = this.data.testList;
    switch (e.detail.value) {
      case "1":
        // 不属于试验
        info.trialType = '否';
        that.setData({
          isTrial: false,
          info: info
        });
        break;
      case "2":
        // 属于试验
        info.trialType = '是';
        testList.forEach(function (e) {
          e.checked = false;
        });
        that.setData({
          isTrial: true,
          testList: testList,
          info: info
        });
        break;
    }
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

  // 绑定试验类型
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

  // 出差日期 返回日期 申请时间 绑定事件
  bindPickerTime: function (e) {
    let that = this;
    let item = e.currentTarget.dataset.item;
    let info = this.data.info;
    let value = e.detail.value;
    switch (item) {
      case "beginTime":
        if (info.endTime != "请选择返回时间") {
          let endTime = new Date(info.endTime).getTime();
          let beginTime = new Date(value).getTime();
          if (endTime < beginTime) {
            util.showError(this, "结束时间不能早于出行时间");
            return false;
          } else {
            info[item] = value;
            let travelDays = util.getDaysByPickers(value, info.endTime);
            info.travelDays = travelDays;
          }
        }
        info[item] = value;
        break;
      case "endTime":
        // if (util.timeHasPassed(value)) {
        //   util.showError(this, "时间不能早于今天");
        //   return false;
        // }
        let endTime = new Date(value).getTime();
        let beginTime = new Date(info.beginTime).getTime();
        if (endTime < beginTime) {
          util.showError(this, "结束时间不能早于出行时间");
          return false;
        } else {
          info[item] = value;
          let travelDays = util.getDaysByPickers(info.beginTime, value);
          info.travelDays = travelDays;
        }
        break;
      case "applyTime":
        info[item] = value;
        break;
    }
    this.setData({
      info: info
    });

  },

  // 选择行程：往返还是多程
  radioChange: function (e) {
    let that = this;
    let info = this.data.info;
    switch (e.detail.value) {
      case "1":
        // 单程
        // 清空ArrayList<Detail>

        info.startAddress = info.endAddress;
        info.middAddress = info.lastPlace;
        that.setData({
          isMulti: false,
          info: info,
          lists: []
        });
        break;
      case "2":
        that.setData({
          isMulti: true
        });
        break;
    }
  },

  // 所有文本框绑定事件
  bindValue: function (e) {
    let item = e.currentTarget.dataset.item;
    let info = this.data.info;
    let value = e.detail.value;
    info[item] = value;
    switch (item) {
      case "startAddress":
      case "endAddress":
        if (this.data.isMulti == false) {
          info.endAddress = value;
          info.startAddress = value;
        }
        break;
      case "middAddress":
      case "lastPlace":
        if (this.data.isMulti == false) {
          info.middAddress = value;
          info.lastPlace = value;
        }
        break;
    }
    this.setData({
      info: info
    });

  },

  // 绑定审批人
  bindApproveMan: function (e) {
    let index = e.detail.value;
    let info = this.data.info;
    info.approveMan = this.data.finders[index];
    info.approveManOpenId = apply_util.getOpenIdByName(this.data.finders[index], this.data.finderObjectList);
    this.setData({
      info: info
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

  // 绑定去的出行方式 和 回来的出行方式
  bindTravelMode: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    let info = this.data.info;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    let multiArray = [];
    multiArray.push(this.data.categories);
    multiArray.push(this.data.names[value[0]]);
    let item = e.currentTarget.dataset.item;
    switch (item) {
      case "goTravelMode":
        info.goModes = util.deepClone(multiArray);
        info[item] = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");
        break;
      case "backTravelMode":
        info.backModes = util.deepClone(multiArray);
        info[item] = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");
        break;
    }
    // 判断选择的出行方式是否超标
    // if (this.isOverproof(info[item])) {
    //   this.setData({
    //     isOverproof: true
    //   });
    // } else {
    //   this.setData({
    //     isOverproof: false
    //   });
    // }

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

  checkboxChange: function (e) {
    let indexes = e.detail.value;
    let ccObjectList = this.data.ccObjectList;
    for (let i = 0; i < ccObjectList.length; i++) {
      ccObjectList[i].checked = false;
    }
    for (let i = 0; i < indexes.length; i++) {
      indexes[i] = parseInt(indexes[i]);
      ccObjectList[indexes[i]].checked = true;
    }
    // ccnames 选中的人
    // ccopenids 选中的人的openid
    let ccnames = [];
    let ccopenids = [];
    for (let i = 0; i < ccObjectList.length; i++) {
      let e = ccObjectList[i];
      if (e.checked == true) {
        ccopenids.push(e.openid);
        ccnames.push(e.realname);
      }
    }
    this.setData({
      ccnames: ccnames,
      ccopenids: ccopenids,
      ccObjectList: ccObjectList
    });
  },

  // 对话框取消按钮点击事件
  onCancel: function () {
    // 真正的抄送人
    let cCListOpenIdName = this.data.cCListOpenIdName || "";
    let ccObjectList = this.data.ccObjectList;
    if (cCListOpenIdName == "") {
      for (let j = 0; j < ccObjectList.length; j++) {
        let e = ccObjectList[j];
        e.checked = false;
      }
    } else {
      cCListOpenIdName = cCListOpenIdName.split(",");
      for (let i = 0; i < ccObjectList.length; i++) {
        ccObjectList[i].checked = false;
      }
      for (let i = 0; i < cCListOpenIdName.length; i++) {
        for (let j = 0; j < ccObjectList.length; j++) {
          let e = ccObjectList[j];
          if (e.realname == cCListOpenIdName[i]) {
            e.checked = true;
          }
        }
      }
    }
    this.setData({
      ccObjectList: ccObjectList
    });
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
    let index = parseInt(e.currentTarget.id.replace("src-", ""));
    let value = e.detail.value;
    let list = this.data.lists;
    list[index].from = value;
    this.setData({
      lists: list
    });
  },

  setTo: function (e) {
    let index = parseInt(e.currentTarget.id.replace("dest-", ""));
    let value = e.detail.value;
    let list = this.data.lists;
    list[index].to = value;
    this.setData({
      lists: list
    });
  },

  // 绑定中间过程的出行方式
  bindPickerValues: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    let index = parseInt(e.currentTarget['id'].split('-')[1]); //得到index整数
    let mode = this.data.categories[value[0]] + (this.data.names[value[0]][value[1]] || "");
    // 更新lists
    let list = this.data.lists;
    list[index].mode = mode;
    this.setData({
      lists: list,
    });

  },

  // ArrayList<Detail>.add(Detail)
  addItem: function () {
    let items = this.data.lists;
    let detail = new Detail();
    detail.modes = util.deepClone(this.data.multiArray);
    items.push(detail);
    this.setData({
      lists: items,
    });
  },

  // ArrayList<Detail>.remove(Detail)
  removeItem: function () {
    let items = this.data.lists;
    items.pop();
    this.setData({
      lists: items
    });
  },



  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let info = new Info();




    // 初始化试验类型列表
    let testList = [];
    let obj = new TestObject("陆上试验");
    testList.push(obj);
    obj = new TestObject("系泊航行试验");
    testList.push(obj);
    obj = new TestObject("出海航行试验");
    testList.push(obj);

    this.setData({
      info: info,
      testList: testList
    });
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
        let info = that.data.info;
        info.department = user.departmentname;
        info.applyMan = user.realname;
        if (finders.length > 0) {
          info.approveMan = finders[0].realname;
          info.approveManOpenId = apply_util.getOpenIdByName(finders[0].realname, finders);
        } else {
          info.approveMan = "请选择审批人";
          info.approveManOpenId = "";
        }
        that.setData({
          finders: finderList,
          finderObjectList: finders,
          info: info
        });
      }
    });
    // 获取抄送人
    wx.request({
      url: ccurl,
      method: "GET",
      data: {
        openId: wx.getStorageSync("openId"),
        moduleNum: 5
      },
      success: function (res) {
        if (res.statusCode >= 400 && res.statusCode <= 499) {
          util.showError(that, "获取抄送人：服务器参数错误");
        } else if (res.statusCode >= 500 && res.statusCode <= 599) {
          util.showError(that, "获取抄送人：服务器内部错误");
        } else {
          // 后台传过来的值
          let ccObjectList = res.data.data;
          // 抄送人对象列表
          let carbonCopyList = [];
          let cCListOpenIdName = [];
          let ccopenids = [];

          // 过滤非法数据
          for (let i = 0; i < ccObjectList.length; i++) {
            if (ccObjectList[i] != null) {
              carbonCopyList.push(ccObjectList[i]);
              ccObjectList[i].checked = true;
            }
          }

          carbonCopyList.forEach(function (e) {
            e.checked = true;
            cCListOpenIdName.push(e.realname);
            ccopenids.push(e.openid);
          });

          that.setData({
            ccObjectList: carbonCopyList,
            cCListOpenIdName: cCListOpenIdName.join(","),
            ccopenids: ccopenids.join(",")
          });
        }

      }
    });
    // 获取出行方式
    wx.request({
      url: travelModeUrl,
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

        // multiArray 是用来显示在picker的range
        let info = that.data.info;
        let multiArray = [];
        multiArray.push(categories);
        multiArray.push(names[0]);

        info.goModes = util.deepClone(multiArray);
        info.backModes = util.deepClone(multiArray);
        that.setData({
          categories: categories,
          names: names,
          multiArray: multiArray,
          info: info
        });


      }
    });


    // 获取超标抄送人
    util.request(defaultccURL, {
      openId: wx.getStorageSync("openId"),
      moduleNum: 5
    }, function (res) {
      let defaultccName = [];
      if (res.data.data != null) {
        if (res.data.data.length > 0) {
          res.data.data.forEach(function (e) {
            defaultccName.push(e.realname);
          });
        }
      }
      defaultccName = defaultccName.join(",");
      that.setData({
        defaultccName: defaultccName
      });
    });
  },



  onShow: function () {},

  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    let values = e.detail.value;
    let lists = this.data.lists;
    let data = {};
    let travelDatails = "";
    if (lists.length == 0) {
      // 没有添加出差地  补全travelDatails
      travelDatails = apply_util.generateTravelObject(info["startAddress"], info["middAddress"], info["goTravelMode"]) + "*" + apply_util.generateTravelObject(info["lastPlace"], info["endAddress"], info["backTravelMode"]);
    } else {
      let modes = this.data.pickerValue;
      travelDatails = apply_util.generateTravelObject(info["startAddress"], info["middAddress"], info["goTravelMode"]) + "*";
      for (let i = 0; i < lists.length; i++) {
        travelDatails += lists[i].toString() + "*";
      }
      travelDatails += apply_util.generateTravelObject(info["lastPlace"], info["endAddress"], info["backTravelMode"]);
    }



    // 判断是否试验
    let isTest = that.split(that.data.testList);
    if (info.trialType == "否") {
      isTest = "否";
    } else {
      if (isTest == "") {
        isTest = "否";
      }
    }

    // 生成后台需要的json
    data.department = info.department;
    data.travelers = info.travelers;
    data.beginTime = info.beginTime;
    data.endTime = info.endTime;
    data.cause = info.cause;
    data.travelDays = info.travelDays;
    data.totalBudget = info.totalBudget || 0;
    data.applyTime = info.applyTime;
    data.applyMan = info.applyMan;
    data.approveMan = info.approveMan;
    data.remarks = info.remarks || "";
    data.isTest = isTest;
    // 往返1 还是 多程0
    data.tripMode = this.data.isMulti ? "0" : "1";
    data.lastPlace = info.lastPlace;
    data.startAddress = info.startAddress;
    data.endAddress = info.endAddress;
    data.middAddress = info.middAddress;
    data.goTravelMode = info.goTravelMode;
    data.backTravelMode = info.backTravelMode;
    data.tripDetails = travelDatails;
    data.approveManOpenId = info.approveManOpenId || "该审批人没有openId";
    data.openId = wx.getStorageSync('openId');
    data.approveMan = info.approveMan;
    data.cCListOpenId = this.data.ccopenids || "";
    data.cCListOpenIdName = this.data.cCListOpenIdName || "";
    // 前端校验
    let flag = this.checkData(data);
    // util.goTo("/pages/operatePages/myApply/adaptable/travel/travel?item=" + JSON.stringify(data));
    if (flag) {
      // 防止表单重复提交
      if (that.data.hasCommited == undefined) {
        wx.request({
          url: url,
          data: data,
          header: {},
          method: 'GET',
          success: function (res) {
            let resultdata = res.data.data;
            if (resultdata == "success") {
              util.goTo("/pages/publicPages/applyback/applyback");
            } else {
              util.showError(that, "差旅申请：服务器内部错误");
              console.log("差旅申请：服务器内部错误");
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
        })
        let hasCommited = true;
        that.setData({
          hasCommited: hasCommited
        });
        util.showLoading();
      } else {
        util.showError(that, "您已经提交，请勿重复提交");
        return false;
      }

    }
  }
})