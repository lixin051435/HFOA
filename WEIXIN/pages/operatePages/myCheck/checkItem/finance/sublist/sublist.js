let util = require("../../../../../../utils/util");

// 根据列表项 获取Object
const URL = getApp().globalData.URL + "/privateCar/financePrivatreCartaskId";

// 财务审批
const financeConfirmURL = getApp().globalData.URL + "/privateCar/financePrivateCarApprove";


Page({

  /**
   * 页面的初始数据
   */
  data: {
    icon: getApp().globalData.URL + '/images/WeiXin/私车.png',
    showErrorMsg: false
  },

  toDetail: function (e) {
    let id = e.currentTarget.id;
    let item = this.data.info.items[id];
    let applyIdinvoice = this.data.info.applyIdinvoice;
    util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?canUpdate=1&notaskid=1&applyIdinvoice=" + applyIdinvoice + "&canFinance=1&item=" + JSON.stringify(item));
  },

  bindData: function (e) {
    let info = this.data.info;
    let value = e.detail.value;
    let item = e.currentTarget.dataset.item;
    if (item == "paidTime") {
      if (util.timeHasPassed(value)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }
    }
    info[item] = value;
    this.setData({
      info: info
    });
  },

  checkData: function (data) {
    if (util.isEmpty(data.voucherNum)) {
      util.showError(this, "凭证号不能为空");
      return false;
    } else if (!util.isInteger(data.voucherNum)) {
      util.showError(this, "凭证号必须是整数");
      return false;
    }
    if (util.isEmpty(data.applyIdinvoice)) {
      util.showError(this, "applyIdinvoice不能为空");
      return false;
    }
    return true;

  },

  financeConfirm: function (e) {
    let that = this;
    let data = {};
    let info = this.data.info;
    data.taskId = info.taskIds.join(",");
    data.applyId = info.applyIds.join(",");
    data.finaceresult = "true";
    data.applyIdinvoice = info.applyIdinvoice || "";
    data.voucherNum = info.voucherNum || "";
    data.paidTime = info.paidTime;

    let flag = this.checkData(data);
    if (flag) {
      if (info.hasCommited == undefined) {
        wx.request({
          url: financeConfirmURL,
          method: "GET",
          data: data,
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=2");
            else
              console.log("server错误");
          },
          fail: function (e) {
            console.log("server错误");
          }
        });
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
      } else {
        util.showError(this, "您已提交过，请不要重复提交");
        return false;
      }
    }
  },
  financeReject: function (e) {
    let that = this;
    let data = {};
    let info = this.data.info;
    data.taskId = info.taskIds.join(",");
    data.applyId = info.applyIds.join(",");
    data.finaceresult = "false";
    data.applyIdinvoice = info.applyIdinvoice || "";
    data.voucherNum = info.voucherNum || "";
    data.paidTime = info.paidTime;
    let flag = true;
    if (flag) {
      if (info.hasCommited == undefined) {
        wx.request({
          url: financeConfirmURL,
          method: "GET",
          data: data,
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=2");
            else
              console.log("server错误");
          },
          fail: function (e) {
            console.log("server错误");
          }
        });
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
      } else {
        util.showError(this, "您已提交过，请不要重复提交");
        return false;
      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    // 财务处理传过来的参数 1表示从左边传过来的 0表示右边
    let financeLeft = options.financeLeft || -1;

    // 财务处理传过来的参数 1表示能显示按钮 否则不显示
    let showButton = options.showButton || -1;

    let info = {};
    if (options != null) {
      info = JSON.parse(options.item);
    }
    info.applyIdinvoice = info.applyId;
    info.paidTime = util.getBeginAndEndDate().endTime;
    that.setData({
      info: info,
      financeLeft: financeLeft,
      showButton: showButton
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {},

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    let that = this;
    let info = that.data.info;
    let canUpdate = 0;

    // 1表示从左边传过来的 0表示右边
    if (this.data.financeLeft == 1) {
      canUpdate = 0;
    } else if (this.data.financeLeft == 0) {
      canUpdate = 1;
    }
    wx.request({
      url: URL,
      data: {
        openId: wx.getStorageSync('openId'),
        applyIds: info.applyIds,
        canUpdate: canUpdate
      },
      method: "GET",
      success: function (res) {
        let items = res.data.data;
        let applyIds = [];
        let taskIds = [];
        items.forEach(item => {
          applyIds.push(item.applyId);
          taskIds.push(item.taskId);
        });
        info.applyIds = applyIds;
        info.taskIds = taskIds;
        info.items = items;
        if (that.data.financeLeft == 1) {
          if (items == null || items.length == 0) {
            util.showError(that, "财务处理：私车列表数据错误");
          }
        }

        that.setData({
          info: info
        });
      },
      fail: function (e) {
        console.log("XHR错误");
      }
    });
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})