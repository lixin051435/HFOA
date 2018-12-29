let util = require("../../../../../../utils/util");
const approveURL = getApp().globalData.URL + '/entertain/finaceEntertainApprove';;

Page({

  /**
   * 页面的初始数据
   */
  data: {
    info: {},
    showErrorMsg: false,
    showModal: false
  },
  confirmDisagree: function (e) {
    let that = this;
    let info = this.data.info;

    // 1表示从左边传过来的 0表示右边
    // if (this.data.financeLeft == 1) {
    //   info.canUpdate = 0;
    // } else if (this.data.financeLeft == 0) {
    //   info.canUpdate = 1;
    // }

    info.result = "false";
    info.comment = this.data.rejectReason || "无";
    if (info.hasCommited == undefined) {
      wx.request({
        url: approveURL,
        data: info,
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else {
            util.showError(that, "财务处理的业务招待：服务器内部错误");
            console.log("财务处理的业务招待：服务器内部错误");
            let hasCommited = undefined;
            info.hasCommited = hasCommited;
            that.setData({
              info: info
            });
          }
        },
        fail: function (res) {
          console.log("XHR错误");
        }
      })
      that.hideModal();
      let hasCommited = true;
      info.hasCommited = hasCommited;
      that.setData({
        info: info
      });
    } else {
      util.showError(this, "您已经提交，请勿重复提交");
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
    // 财务处理传过来的参数 1表示从左边传过来的 0表示右边
    let financeLeft = options.financeLeft || -1;
    // 财务处理传过来的参数 1表示显示按钮 否则不显示
    let showButton = options.showButton || -1;
    let item = JSON.parse(options.item)
    let info = {};
    if (item.entertainregisterinfo != null) {
      if (item.entertainregisterinfo.length > 0) {
        info = item.entertainregisterinfo[0];
      }
    } else {
      util.showError(this, "后台数据错误:事后登记信息丢失");
    }
    info.id = item.id;
    info.taskId = item.taskId;
    info.paidTime = util.getBeginAndEndDate().endTime;
    info.voucherNum = item.voucherNum || "";
    this.setData({
      info: info,
      financeLeft: financeLeft,
      showButton: showButton
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

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

  setTime: function (e) {
    let info = this.data.info;
    info.paidTime = e.detail.value;
    if (util.timeHasPassed(e.detail.value)) {
      util.showError(this, "报销时间不能早于今天");
      return false;
    }
    this.setData({
      info: info
    });
  },
  inputedit: function (e) {
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    let info = this.data.info;
    info[dataset.item] = value;
    this.setData({
      info: info
    });
  },
  checkData: function (info) {
    if (util.isEmpty(info.voucherNum)) {
      util.showError(this, "凭证号不能为空");
      return false;
    } else if (!util.isInteger(info.voucherNum)) {
      util.showError(this, "凭证号必须是数字");
      return false;
    }
    return true;
  },
  agree: function () {
    let that = this;
    let info = this.data.info;

    // 1表示从左边传过来的 0表示右边
    // if (this.data.financeLeft == 1) {
    //   info.canUpdate = 0;
    // } else if (this.data.financeLeft == 0) {
    //   info.canUpdate = 1;
    // }
    info.result = "true";
    info.comment = "";
    let flag = this.checkData(info);
    if (flag) {
      if (info.hasCommited == undefined) {
        wx.request({
          url: approveURL,
          data: info,
          method: 'GET',
          success: function (res) {
            util.showNetworkError(res, that, "财务处理的业务招待");
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=2");
            else {
              util.showError(that, "财务处理的业务招待：服务器内部错误");
              console.log("财务处理的业务招待：服务器内部错误");
              let hasCommited = undefined;
              info.hasCommited = hasCommited;
              that.setData({
                info: info
              });
            }
          },
          fail: function (res) {
            console.log("XHR错误");
            let hasCommited = undefined;
            info.hasCommited = hasCommited;
            that.setData({
              info: info
            });
          }
        })
        util.showLoading();
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
      } else {
        util.showError(this, "您已经提交，请勿重复提交");
        return false;
      }
    }

  },
  disagree: function () {
    this.showModal();
  }
})