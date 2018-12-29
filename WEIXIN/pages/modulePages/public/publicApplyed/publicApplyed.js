// pages/modulePages/public/publicApplyed/publicApplyed.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/applyCar/appForm.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        appId: options.appId
      },
      method: 'GET',
      success: function (res) {
        that.data.applyTime = res.data.applyTime.split(" ")[0];
        that.setData({
          applyMan: res.data.applyMan,
          publicType: res.data.carTypeNum,
          driver: res.data.driver,
          applyTime: that.data.applyTime,
          beginPlace: res.data.beginPlace,
          endPlace: res.data.endPlace,
          companyNo: res.data.compareManNum,
          borrowTime: res.data.beginTimePlan,
          returnTime: res.data.endTimePlan,
          reason: res.data.useCarReason,
          approveMan: res.data.approveMan
        })
      },
      fail: function (res) {
        console.log('获取公车详细信息--失败');
        util.showError(that, '网络错误,请稍后重试');
      }
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