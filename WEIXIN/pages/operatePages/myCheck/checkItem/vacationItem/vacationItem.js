// pages/operatePages/myCheck/vacationItem/vacationItem.js
const app = getApp();
const util = require("../../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    icon: app.globalData.URL + '/images/WeiXin/休假.png',
    showButton: null,
    canUpdate: null,
    canConfirm: null,
    canRevoke: null,
    sfyc: null,
    id: null,
    taskId: null,
    nowTime: null,
    showModal: false,
    pending_vacation: null
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
      url: app.globalData.URL + '/leavel/leaverApprove',
      data: {
        id: that.data.id,
        taskId: that.data.taskId,
        result: 1
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

  // 驳回
  disagree() {
    this.showModal();
  },

  confirmDisagree: function (e) {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/leavel/leaverApprove',
      data: {
        id: that.data.id,
        taskId: that.data.taskId,
        result: 2,
        comment: that.data.rejectReason || "无"
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
    this.hideModal();
    util.showLoading();
  },

  setRejectReason: function (e) {
    let value = e.detail.value;
    this.setData({
      rejectReason: value
    });
  },

  // 转到第二年
  nextYear() {
    let that = this;
    wx.showModal({
      title: '提示',
      content: '确定转接到第二年吗?',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: app.globalData.URL + '/leavel/leaverApprove',
            data: {
              id: that.data.id,
              taskId: that.data.taskId,
              result: 3
            },
            method: 'GET',
            success: res => {
              util.goTo('../../../../publicPages/checkback/checkback');
            },
            fail: () => {
              console.log("转到第二年--fail");
              util.showError(that, '网络错误,请稍后重试');
            }
          })
        }
        if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },

  // 现金补偿
  cash() {
    let that = this;
    wx.showModal({
      title: '提示',
      content: '确定现金补偿吗?',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: app.globalData.URL + '/leavel/leaverApprove',
            data: {
              id: that.data.id,
              taskId: that.data.taskId,
              result: 4
            },
            method: 'GET',
            success: res => {
              util.goTo('../../../../publicPages/checkback/checkback');
            },
            fail: () => {
              console.log("现金补偿--fail");
              util.showError(that, '网络错误,请稍后重试');
            }
          })
        }
        if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },

  // 异常-转到第二年
  nextYearYC() {
    let that = this;
    wx.showModal({
      title: '提示',
      content: '确定转接到第二年吗?',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: app.globalData.URL + '/leavel/leaverApprove',
            data: {
              id: that.data.id,
              taskId: that.data.taskId,
              result: 5
            },
            method: 'GET',
            success: res => {
              util.goTo('../../../../publicPages/checkback/checkback');
            },
            fail: () => {
              console.log("异常-转到第二年--fail");
              util.showError(that, '网络错误,请稍后重试');
            }
          })
        }
        if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },

  // 异常-放弃
  giveupYC() {
    let that = this;
    wx.showModal({
      title: '提示',
      content: '确定放弃吗?',
      success: function (res) {
        if (res.confirm) {
          wx.request({
            url: app.globalData.URL + '/leavel/leaverApprove',
            data: {
              id: that.data.id,
              taskId: that.data.taskId,
              result: 6
            },
            method: 'GET',
            success: res => {
              util.goTo('../../../../publicPages/checkback/checkback');
            },
            fail: () => {
              console.log("异常-放弃--fail");
              util.showError(that, '网络错误,请稍后重试');
            }
          });
        }
        if (res.cancel) {
          console.log('用户点击取消')
        }
      }
    })
  },

  // 修改
  toUpdate(e) {
    if (util.afterToday(this.data.pending_vacation[0].beingTime)) {
      util.showError(this, "修改日期应在休假日期之前,已不能修改");
    } else {
      util.goTo('../../../myApply/adaptable/vacation/vacation?item=' + JSON.stringify(this.data.item));
    }
  },

  // 确认休假
  confirmVacation() {
    util.goTo('../../../myApply/confirmVacation/confirmVacation?showConfirm=1&item=' + JSON.stringify(this.data.item) + '&isConfirm=1');
  },

  // 放弃休假
  giveupVacation() {
    util.goTo('../../../myApply/confirmVacation/confirmVacation?showCancel=1&item=' + JSON.stringify(this.data.item) + '&isConfirm=0');
  },

  // 撤回
  toRevoke(e) {
    let that = this;
    util.confirmRevoke(function () {
      wx.request({
        url: app.globalData.URL + '/leavel/deleteLeaver',
        data: {
          id: that.data.id,
          taskId: that.data.taskId,
        },
        method: 'GET',
        success: res => {
          util.goTo('../../../../publicPages/undoback/undoback');
        },
        fail: () => {
          console.log("撤回--fail");
          util.showError(that, '网络错误,请稍后重试');
        }
      });
    });

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let item = JSON.parse(options.item);
    let timeId = item.id;
    let showToast = options.showToast || -1;
    if (showToast != -1) {
      wx.showModal({
        title: '提示',
        content: "请核实病事假天数",
        showCancel: false,
        success: function () {}
      })
    }
    this.setData({
      item: item,
      sfyc: item.sfyc,
      id: item.id,
      taskId: item.taskId,
      showButton: options.showButton,
      canUpdate: item.conUpdate,
      canConfirm: item.conConfirm,
      canRevoke: item.canRevoke,
      nowTime: util.getNowFormatDate(),
      timeId: timeId,
      showToast: showToast
    })

    // 搜索待执行年假
    wx.request({
      url: app.globalData.URL + "/leavel/leaverexecuted",
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        that.setData({
          pending_vacation: res.data.data
        })
      },
      fail: () => {
        console.log("获取待执行年假失败");
        util.showError(that, '网络错误,请稍后重试');
      }
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