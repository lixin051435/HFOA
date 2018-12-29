// pages/operatePages/myCheck/publicItem/publicItem.js
const app = getApp();
const util = require("../../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    icon: app.globalData.URL + '/images/WeiXin/公车.png',
    isCheckOk: false,
    showButton: false,
    canUpdate: false,
    canRevoke: false,
    needTaskId: null,
    showModal: false,
    showRejucted: false,
    rejectReason: ''
  },

  // 点击驳回时输入驳回理由，若不填则为空
  confirmDisagree: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      that.data.rejectReason = that.data.rejectReason.trim();
      if (that.data.rejectReason === "") {
        this.setData({
          rejectReason: '无'
        })
      }else{
        this.setData({
          rejectReason: that.data.rejectReason.trim()
        })
      }
      wx.request({
        url: app.globalData.URL + '/' + that.data.url,
        data: {
          taskId: that.data.taskId,
          result: false,
          status: 1,
          openId: wx.getStorageSync('openId'),
          comment: that.data.rejectReason
        },
        method: 'GET',
        success: res => {
          util.goTo('../../../../publicPages/checkback/checkback');
        },
        fail: () => {
          console.log("驳回审批--fail");
          util.showError(that, '网络错误,请稍后重试');
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

  // 同意
  agree() {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        result: true,
        status: 3,
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("通过审批--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  // 不同意
  disagree() {
    this.showModal();
  },

  // 修改申请
  toUpdate() {
    util.goTo('../../../myApply/adaptable/public/public?item=' + JSON.stringify(this.data.item) + "&needTaskId=" + this.data.needTaskId);
  },

  // 撤回申请
  toRevoke() {
    let that = this;
    util.confirmRevoke(function () {
      wx.request({
        url: app.globalData.URL + '/applyCar/revokeApply.action',
        header: {
          'content-type': 'application/json'
        },
        // 传到后台的值
        data: {
          appId: that.data.appId,
          taskId: that.data.taskId
        },
        method: 'GET',
        success: function (res) {
          util.goTo('../../../../publicPages/undoback/undoback');
        },
        fail: function (res) {
          console.log('撤回公车申请--fail');
          util.showError(that, '网络错误,请稍后重试');
        }
      })
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let item = JSON.parse(options.item);
    // 如果传过来的是null,设置为“无”
    if (item.comment == null || item.comment == undefined || item.comment == "") {
      this.setData({
        showRejucted: false
      })
    } else {
      this.setData({
        showRejucted: true
      })
    }
    item.applyTime = item.applyTime.split(" ")[0];
    if (item.canUpdate === null) {
      item.canUpdate = false;
    }
    if (item.canRevoke === null) {
      item.canRevoke = false;
    }
    if (item.needTaskId === null) {
      item.needTaskId = false;
    }
    
    this.setData({
      item: item,
      url: item.url,
      appId: item.id,
      taskId: item.taskId,
      showButton: options.showButton,
      canUpdate: item.canUpdate,
      canRevoke: item.canRevoke,
      needTaskId: options.needTaskId,
      applyTime: item.applyTime
    });

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