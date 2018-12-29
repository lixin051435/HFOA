// pages/modulePages/public/borrowDetail/borrowDetail.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    noData: false,
    icon: app.globalData.URL + '/images/WeiXin/公车.png',
    showSubmit: false
  },

  toApplyInfo(e) {
    let index = e.currentTarget.dataset.index;
    util.goTo('../publicApplyed/publicApplyed?appId=' + index);
  },

  toPublicApply() {
    util.goTo('../publicApply/publicApply?carnum=' + this.data.carNum + "&cartype=" + this.data.carType + "&peasonnum=" + this.data.personnum + "&carId=" +  this.data.carId);
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/applyCar/carSendDetail.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        carNum: options.id
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          carUrl: res.data.carInfo.carUrl,
          carType: res.data.carInfo.carType,
          carNum: res.data.carInfo.carNum,
          personnum:res.data.carInfo.peasonnum,
          carId:res.data.carInfo.id,
        })
        if (res.data.applyInfo.length === 0) {
          that.setData({
            noData: true
          })
        } else {
          that.setData({
            noData: false,
            borrowList: res.data.applyInfo
          })
        }
      },
      fail: function (res) {
        console.log('获取公车借用详情--失败');
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