// pages/modulePages/public/publicStatus/publicStatus.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showSubmit: false
  },

  toBorrowDetail(e) {
    let index = e.currentTarget.dataset.index;
    util.goTo("../borrowDetail/borrowDetail?id=" + index);
  },

  toPublicApply() {
    util.goTo("../publicApply/publicApply");
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/applyCar/carInfo.action',
      header: {
        'content-type': 'application/json'
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          publicCar: res.data
        })
      },
      fail: function (res) {
        console.log('获取公车借用状态信息--fail');
        util.showError(that, '网络错误,请稍后重试');
      }
    })

    // 判断是否可以申请公车
    wx.request({
      url: app.globalData.URL + '/applyCar/getApproval.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: function (res) {
        if (res.data.ifOperation === true) {
          that.setData({
            showSubmit: true
          })
        } else if (res.data.ifOperation === false) {
          that.setData({
            showSubmit: false
          })
        }
      },
      fail: function (res) {
        console.log('获取公车申请默认数据信息--失败');
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