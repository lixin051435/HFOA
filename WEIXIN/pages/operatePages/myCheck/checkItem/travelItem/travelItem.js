const app = getApp();
const util = require("../../../../../utils/util");
const root = getApp().globalData.URL;
// 领导审批URL
const approveURL = getApp().globalData.URL + "/applyExpenses/approveApplyExpense";
// 员工确认URL
const confirmURL = getApp().globalData.URL + "/applyExpenses/confirmApplyExpense";
// 员工撤销URL
const revokeURL = getApp().globalData.URL + "/applyExpenses/deleteApplyExpense";

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    icon: app.globalData.URL + '/images/WeiXin/差旅.png',
    showModal: false
  },
  confirmDisagree: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: approveURL,
        data: {
          id: this.data.info.id,
          taskId: this.data.info.taskId,
          result: "false",
          comment: that.data.rejectReason || "无"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/checkback/checkback");
          else
            console.log("Server 错误")
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
  },
  setRejectReason: function (e) {
    let value = e.detail.value;
    this.setData({
      rejectReason: value
    });
  },
  /**
   * 弹窗
   */
  showDialogBtn: function () {
    this.setData({
      showModal: true
    })
  },

  // 弹出框蒙层截断touchmove事件
  preventTouchMove: function () {

  },
  showModal: function () {
    this.setData({
      showModal: true
    });
  },

  // 隐藏模态对话框
  hideModal: function () {
    this.setData({
      showModal: false
    });
  },
  cancel: function () {
    this.setData({
      showModal: false,
      rejectReason: ""
    });

  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let info = JSON.parse(options.item);
    // 领导审批按钮是否显示
    let showButton = options.showButton || -1;
    // 自己修改按钮是否显示
    let canUpdate = options.canUpdate || -1;
    // 员工确认按钮是否显示
    let canConfirm = options.canConfirm || -1;
    // 员工撤回按钮是否能显示
    let canRevoke = options.canRevoke || -1;

    // 给下一个页面要不要穿taskid
    let notaskid = options.notaskid || -1;

    let tripDetails = JSON.parse(info.tripDetails);
    let len = tripDetails.length;

    info.startAddress = tripDetails[0].beginAddress;
    info.middAddress = tripDetails[0].endAddress;
    info.goTravelMode = tripDetails[0].vehicle;

    info.lastPlace = tripDetails[len - 1].beginAddress;
    info.endAddress = tripDetails[len - 1].endAddress;
    info.backTravelMode = tripDetails[len - 1].vehicle;
    info.cCListOpenIdName = info.cCListOpenIdName || "无";

    if (tripDetails.length < 3) {
      info.middleList = [];
    } else {
      let middleList = [];
      for (let i = 1; i < tripDetails.length - 1; i++) {
        middleList.push(tripDetails[i]);
      }
      info.middleList = middleList;
    }
    this.setData({
      info: info,
      showButton: showButton,
      canUpdate: canUpdate,
      canConfirm: canConfirm,
      canRevoke: canRevoke,
      notaskid: notaskid
    });
  },
  agree: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: approveURL,
        data: {
          id: this.data.info.id,
          taskId: this.data.info.taskId,
          result: "true",
          comment: ""
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("Server 错误")
        },
        fail: function (res) {
          console.log("出错了");
          let hasCommited = undefined;
          that.setData({
            hasCommited: hasCommited
          });
        }
      });
      this.hideModal();
      let hasCommited = true;
      that.setData({
        hasCommited: hasCommited
      });
      util.showLoading();
    } else {
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }

  },
  disagree: function (e) {
    this.showDialogBtn();
  },
  toUpdate: function (e) {
    util.goTo("/pages/operatePages/myApply/adaptable/travel/travel?notaskid=" + this.data.notaskid + "&item=" + JSON.stringify(this.data.info));
  },
  confirm: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: confirmURL,
        data: {
          id: this.data.info.id,
          taskId: this.data.info.taskId,
          cofirm: "true"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("Server 错误")
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

  },
  revoke: function (e) {
    let that = this;

    util.confirmRevoke(function () {
      if (that.data.hasCommited == undefined) {
        wx.request({
          url: revokeURL,
          data: {
            id: that.data.info.id,
            taskId: that.data.info.taskId,
          },
          method: 'GET',
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=2");
            else
              console.log("Server 错误");
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
    });


  },
  relinquish: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: confirmURL,
        data: {
          id: this.data.info.id,
          taskId: this.data.info.taskId,
          cofirm: "false"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("Server 错误")
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

  },

})