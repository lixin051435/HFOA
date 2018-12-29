// pages/operatePages/sealHandle/useSeal/useSeal.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    icon: app.globalData.URL + '/images/WeiXin/用印.png',
  },

  // 用印确认
  toHandle() {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("用印确认--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let item = JSON.parse(options.item);

    item.applytime = item.applytime.split(" ")[0];
    that.setData({
      [item.applytime]:item.applytime
    })
    
    if (item.status === "通过") {
      that.setData({
        item: item,
        url: item.url,
        appId: item.id,
        taskId: item.taskId,
        status: item.status,
        canUseSeal: item.canUseSeal
      });
    }

    // 是否显示更多内容判断
    if (item.entrustedcardnum === null && item.entrustedcardtype === null && item.entrustedendtime === null && item.entrustedman === null && item.entrustedmatter === null && item.entrustedpermission === null && item.entrustedpost === null && item.entrustedstarttime === null) {
      that.setData({
        showMore: false
      });
    } else {
      that.setData({
        showMore: true
      });
    }
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