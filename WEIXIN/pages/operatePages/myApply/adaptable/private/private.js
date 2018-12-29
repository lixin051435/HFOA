const app = getApp();
const util = require('../../../../../utils/util.js');
const root = getApp().globalData.URL;
const url1 = root + '/applyExpenses/insertApply';

// 获取审批人
const loadUrl = app.globalData.URL + '/user/getFinder';

// 路线规划
const zdUrl = app.globalData.URL + '/dictionary/selectDict?type=local';

// 里程单价
const priceURL = app.globalData.URL + '/dictionary/selectDict?type=bt';

// 修改
const updateURL = app.globalData.URL + '/privateCar/updatePrivatecar';


/**
 * Detail类 构造函数
 * @param {string} addressName 途经地
 * @param {string} addressValue 里程数
 */
function Detail(addressName, addressValue) {
  this.addressName = addressName;
  this.addressValue = addressValue;
}

/**
 * Info类 构造函数
 */
function Info() {
  this.applyTime = util.getBeginAndEndDate().endTime;
  this.userCarTime = util.getBeginAndEndDate().endTime;
  this.wayModel = "请选择路线规划";
  this.approveMan = "请选择审批人";
  this.ifBefore = "0";
  this.details = [];
  // 浮点数精度
  this.accuracy = 1;
  // 路线规划类别
  this.wayModels = ["自定义路线", "预定路线"];
  this.wayModel = "自定义路线";
  this.isWangFan = 0;
}

/**
 * 根据真实姓名 返回openId 在抄送人和审批人用到 暂时不考虑重名情况 后期维护者改吧
 * 
 * @param {string} realname 真实姓名
 * @param {list} finderObjectList 对应列表
 */
function getOpenIdByName(realname, finderObjectList) {
  for (let i = 0; i < finderObjectList.length; i++) {
    if (finderObjectList[i].realname == realname) {
      return finderObjectList[i].openid;
    }
  }
  return null;
}

Page({
  data: {
    info: {},
    showErrorMsg: false,
    showWangFan: false
  },
  
  init: function (options) {
    let info = JSON.parse(options.item);
    info.approveManOpenId = info.approveOpenId;
    info.isWangFan = info.doubleLength;
    let notaskid = options.notaskid || -1;
    let passAddress = JSON.parse(info.passAddress);
    info.details = passAddress;
    info.wayModels = ["自定义路线", "预定路线"];
    if (info.wayModel == "预定路线") {
      info.isPreDetail = true;
      info.preDetail = info.beginAddress + "-" + info.destination;
    } else {
      info.isPreDetail = false;
    }
    
    try {
      info.applyTime = info.applyTime.split(" ")[0];
    } catch (error) {
      console.log(error);
    }

    let that = this;
    this.setData({
      info: info,
      wineIndex: [0, 0],
      notaskid: notaskid
    });
    // 获取单价
    wx.request({
      url: priceURL,
      method: "GET",
      success: function (res) {
        let price = parseFloat(res.data[0].info);
        let info = that.data.info;
        info.price = price;
        that.setData({
          info: info
        });
      }
    });
    // 获取路线规划
    wx.request({
      url: zdUrl,
      method: 'GET',
      success: function (res) {
        let preDetails = [];
        let data = res.data;
        let info = that.data.info;
        data.forEach(e => {
          preDetails.push(e.text);
        });
        info.preDetailObjects = res.data;
        info.preDetails = preDetails;
        // info.preDetail = preDetails[0];
        info.wayDetail = info.preDetailObjects[0].id;
        that.setData({
          info: info
        });
      }
    });
    // 获取审批人 
    wx.request({
      url: loadUrl,
      method: 'GET',
      data: {
        openId: wx.getStorageSync('openId'),
        moduleNum: 5
      },
      success: function (res) {
        let realname = res.data.data.user.realname;
        let department = res.data.data.user.departmentname;
        let finders = res.data.data.finder;
        let finderList = [];
        for (let i = 0; i < finders.length; i++) {
          finderList.push(finders[i].realname);
        }
        that.setData({
          finders: finderList,
          finderObjectList: finders,
        });
        let info = that.data.info;
        info.department = department;
        // info.applyMan = realname;
        that.setData({
          info: info,
          approvers: finderList
        });
      }
    });
  },

  onLoad: function (options) {
    this.init(options);
  },


  setPreDetail: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    info.preDetail = info.preDetails[value];
    info.beginAddress = info.preDetailObjects[value].text.split("-")[0];
    info.destination = info.preDetailObjects[value].text.split("-")[1];
    info.endLength = info.preDetailObjects[value].info;
    info.wayDetail = info.preDetailObjects[value].id;
    this.cal();
    this.setData({
      info: info
    });
  },
  setWayModel: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    let showWangFan = false;
    info.wayModel = info.wayModels[value];
    if (value == "1") {
     // 选择了预置路线
     info.isPreDetail = true;
     let preDetail = info.preDetail;
     info.preDetailObjects.forEach(function (e) {
       if (e.text == preDetail) {
         info.beginAddress = e.text.split("-")[0];
         info.destination = e.text.split("-")[1];
         info.endLength = e.info;
       }
     });

     info.details = [];
     showWangFan = true;
     this.cal();
    } else {
      info.preDetail = info.preDetailObjects[0].text;
      info.isPreDetail = false;
      info.beginAddress = "";
      info.destination = "";
      info.endLength = "";
      info.singleLength = "";
      info.countLength = "";
      info.sureLength = "";
      showWangFan = false;
      this.cal();
    }
    this.setData({
      info: info,
      showWangFan: showWangFan
    });
  },

  setApproveMan: function (e) {
    let index = e.detail.value;
    let info = this.data.info;
    info.approveMan = this.data.finders[index];
    info.approveManOpenId = getOpenIdByName(this.data.finders[index], this.data.finderObjectList);
    this.setData({
      info: info
    });
  },

  bindUserCarTime: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    info.applyTime = value;
    this.setData({
      info: info
    });
  },

  setRealTime: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    if (info.ifBefore == 1) {
      // 选了补录
      if (new Date(value).getTime() > new Date(util.getNowTimeDate()).getTime()) {
        util.showError(this, "时间不能晚于今天");
        return false;
      }

    } else {
      // 没选补录
      if (new Date(value).getTime() < new Date(util.getNowTimeDate()).getTime()) {
        util.showError(this, "时间不能早于今天");
        return false;
      }
    }

    info.userCarTime = value;
    this.setData({
      info: info
    });
  },

  radioChange: function (e) {
    let id = e.currentTarget.id;
    let item = e.target.dataset.item;
    let value = e.detail.value;
    let info = this.data.info;
    info[item] = value;
    this.setData({
      info: info
    });
  },

  setIsWangFan: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    let isWangFan = 0;
    if (value.length > 0) {
      isWangFan = 1;
    }
    info.isWangFan = isWangFan;
    this.cal();
    this.setData({
      info: info
    });
  },

  inputedit: function (e) {
    // 1. input 和 info 双向数据绑定
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    let info = this.data.info;
    this.data[dataset.obj][dataset.item] = value;
    this.setData({
      info: this.data[dataset.obj]
    });
    if (dataset.item == "endLength") {
      this.cal();
    }

    switch (dataset.item) {
      case "beginAddress":
      case "destination":
        if (this.data.info.beginAddress == this.data.info.destination) {
          let isWangFan = 0;
          // info.isWangFan = isWangFan;
          this.setData({
            info: info
          });
          this.cal();
        } else {
          this.setData({});
          this.cal();
        }
        break;

    }
  },

  addItem: function (e) {
    let info = this.data.info;
    info.details.push(new Detail());
    this.cal();
    this.setData({
      info: info
    });
  },

  removeItem: function (e) {
    let info = this.data.info;
    info.details.pop();

    this.cal();
    this.setData({
      info: info
    });
  },

  setPlace: function (e) {
    let index = parseInt(e.currentTarget.id.replace("place-", ""));
    let place = e.detail.value;
    let info = this.data.info;
    info.details[index].addressName = place;
    this.setData({
      info: info
    });
  },

  setNumber: function (e) {
    let index = parseInt(e.currentTarget.id.replace("number-", ""));
    let number = e.detail.value;
    let info = this.data.info;
    info.details[index].addressValue = number;
    this.cal();
    this.setData({
      info: info
    });
  },

  // 算单程里程 计价里程 核定价格
  cal: function () {
    let info = this.data.info;
    let singleLength = parseFloat(this.data.info.endLength);
    this.data.info.details.forEach(element => {
      singleLength += parseFloat(element.addressValue);
    });
    info.singleLength = singleLength.toFixed(info.accuracy);
    if (info.isWangFan == 1) {
      info.countLength = singleLength * 2;
    } else {
      info.countLength = singleLength;
    }
    info.countLength = info.countLength.toFixed(info.accuracy);
    info.sureLength = (info.countLength * info.price).toFixed(info.accuracy);
    if (isNaN(info.singleLength)) {
      info.singleLength = "";
    }
    if (isNaN(info.countLength)) {
      info.countLength = "";
    }
    if (isNaN(info.sureLength)) {
      info.sureLength = "";
    }
    this.setData({
      info: info
    });
  },

  checkData: function (data) {
    if (util.isEmpty(data.department)) {
      util.showError(this, "部门不能为空");
      return false;
    }
    if (util.isEmpty(data.reason)) {
      util.showError(this, "事由不能为空");
      return false;
    }
    if (util.isEmpty(data.beginAddress)) {
      util.showError(this, "出发地不能为空");
      return false;
    }
    if (util.isEmpty(data.destination)) {
      util.showError(this, "目的地不能为空");
      return false;
    }
    if (util.isEmpty(data.endLength)) {
      util.showError(this, "里程数不能为空");
      return false;
    } else if (!util.isNumber(data.endLength)) {
      util.showError(this, "里程数必须是数字");
      return false;
    }
    if (util.isEmpty(data.userCarTime) || data.userCarTime.indexOf("请选择") != -1) {
      util.showError(this, "请选择用车时间");
      return false;
    }
    if (util.isEmpty(data.applyMan)) {
      util.showError(this, "申请人不能为空");
      return false;
    }
    if (util.isEmpty(data.approveMan) || data.approveMan.indexOf("请选择") != -1) {
      util.showError(this, "请选择审批人");
      return false;
    }
    if (data.details.length > 0) {
      for (let i = 0; i < data.details.length; i++) {
        let e = data.details[i];
        if (util.isEmpty(e.addressValue) || util.isEmpty(e.addressName)) {
          util.showError(this, "途经地或里程数输入有误");
          return false;
        }
        if (!util.isNumber(e.addressValue)) {
          util.showError(this, "里程数必须是数字");
          return false;
        }
      }
    }
    if (data.userCarTime.indexOf("请选择") > -1) {
      util.showError(this, "请选择用车时间");
      return false;
    }
    if (data.ifBefore == 1) {
      // 补录
      if (new Date(util.getNowTimeDate()).getTime() < new Date(data.userCarTime).getTime()) {
        util.showError(this, "补录用车时间必须早于当前时间");
        return false;
      }
    } else {
      // 非补录
      if (new Date(util.getNowTimeDate()).getTime() > new Date(data.userCarTime).getTime()) {
        util.showError(this, "非补录用车时间必须晚于当前时间");
        return false;
      }
    }
    return true;
  },

  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    info.department = info.department;
    info.applyTime = info.applyTime;
    info.reason = info.reason;
    info.beginAddress = info.beginAddress;
    info.passAddress = JSON.stringify(info.details);
    info.destination = info.destination;
    info.countLength = parseFloat(info.countLength);
    info.singleLength = parseFloat(info.singleLength);
    info.sureLength = parseFloat(info.sureLength);
    info.applyMan = info.applyMan;
    info.approveMan = info.approveMan;
    info.wayModel = info.wayModel;
    info.ifBefore = info.ifBefore;
    info.userCarTime = info.userCarTime;
    info.endLength = info.endLength;
    info.details = info.details;
    info.openId = wx.getStorageSync('openId');
    info.approveManOpenId = info.approveManOpenId || "这个审批人没有openId";
    info.wayDetail = info.wayDetail;
    info.doubleLength = info.isWangFan;
    info.beforeDate = info.userCarTime;

    if (this.data.notaskid != -1) {
      // 是从右边进来的
      info.canUpdate = 1;
    } else {
      // 是从左边进来的（待处理这边进来的）
      info.canUpdate = 0;
    }
    let flag = this.checkData(info);
    if (flag) {
      if (that.data.hasCommited == undefined) {
        wx.request({
          url: updateURL,
          data: info,
          success: function (res) {
            if (res.data.msg == "OK") {
              util.goTo("/pages/publicPages/approveback/approveback?delta=3");

            } else {
              util.showError(that, "私车修改：服务器内部错误");
              console.log("私车修改：服务器内部错误");
              let hasCommited = undefined;
              that.setData({
                hasCommited: hasCommited
              });
            }
          },
          fail: function () {
            console.log("XHR失败");
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
        util.showError(that, "您已经提交，请勿重复提交");
        return false;
      }

    }
  },
})