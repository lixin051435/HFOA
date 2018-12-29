const app = getApp();
const util = require("../../../../../utils/util");
const root = getApp().globalData.URL;

// 领导审批URL
const approveURL = getApp().globalData.URL + '/entertain/approveEntetain';

// 财务审批URL
const financeConfirmURL = getApp().globalData.URL + "/privateCar/staffPrivateCarApprove";

// 员工撤销URL
const revokeURL = getApp().globalData.URL + "/entertain/deleteEntertain";
Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsh: false,
    icon: app.globalData.URL + '/images/WeiXin/招待.png',
    showModal: false

  },
  confirmDisagree: function (e) {
    let that = this;
    // 有领导按钮
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
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("出错了");
        },
        fail: function (res) {
          console.log("XHR出错了");
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
    let entertainregisterinfo = null;
    if (info.entertainregisterinfo != null) {
      if (info.entertainregisterinfo.length > 0) {
        entertainregisterinfo = info.entertainregisterinfo[0];
      }
    }

    // 领导审批按钮是否显示
    let showButton = options.showButton || -1;

    // 自己修改按钮是否显示
    let canUpdate = options.canUpdate || -1;

    // 员工确认按钮是否显示
    let canConfirm = options.canConfirm || -1;

    // 财务审批按钮是否能显示
    let canFinance = options.canFinance || -1;

    // 事后登记按钮是否能显示 
    let canReimburse = options.canReimburse || -1;

    // 员工撤回按钮是否能显示
    let canRevoke = options.canRevoke || -1;

    // 给下一个页面要不要穿taskid
    let notaskid = options.notaskid || -1;

    // 财务处理传过来的参数 1表示从左边传过来的 0表示右边
    let financeLeft = options.financeLeft || -1;

    let wines;
    try {
      wines = JSON.parse(info.wineType)
    } catch (error) {
      wines = info.wineType;
    }
    // let wines = JSON.parse(info.wineType) || info.wineType;
    info.wines = wines;

    this.setData({
      info: info,
      showButton: showButton,
      canUpdate: canUpdate,
      canConfirm: canConfirm,
      canFinance: canFinance,
      canReimburse: canReimburse,
      canRevoke: canRevoke,
      notaskid: notaskid,
      entertainregisterinfo: entertainregisterinfo

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
            console.log("出错了");
        },
        fail: function (res) {
          console.log("XHR出错了");
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
  },
  disagree: function (e) {
    this.showModal();
  },
  toUpdate: function (e) {
    util.goTo("/pages/operatePages/myApply/adaptable/entertain/entertain?notaskid=" + this.data.notaskid + "&item=" + JSON.stringify(this.data.info));
  },
  toReimburse: function (e) {
    util.goTo("/pages/operatePages/myApply/adaptable/after/after?item=" + JSON.stringify(this.data.info));
  },
  confirm: function (e) {

  },
  relinquish: function (e) {

  },
  // 原来的财务驳回 现在没用了
  financeRelinquish: function (e) {

  },
  revoke: function (e) {
    let that = this;
    util.confirmRevoke(function () {
      if (that.data.hasCommited == undefined) {
        wx.request({
          url: revokeURL,
          data: {
            id: that.data.info.id,
            taskId: that.data.info.taskId
          },
          method: 'GET',
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/undoback/undoback");
            else
              console.log("出错了");
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
        util.showError(that, "您已经提交，请勿重复提交");
        return false;
      }
    });

  },
  financeConfirm: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: financeConfirmURL,
        data: {
          applyId: this.data.info.applyId,
          taskId: this.data.info.taskId,
          staffresult: "true"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("出错了");

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
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }

  }

})