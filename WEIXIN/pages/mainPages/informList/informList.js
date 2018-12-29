// pages/mainPages/informList/informList.js
const app = getApp();
const util = require("../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    inform_title: '',
    inform_content: ''
  },

  toInformDetail(e) {
    // 点击某个申请时将该条数据的id传到后台以便获取详情页信息
    let index = parseInt(e.currentTarget.dataset.index);
    let item = parseInt(e.currentTarget.dataset.item);
    // 跳转到详情页
    util.goTo('../informItem/informItem?id=' + this.data.dataList[index].id + '&item=' + JSON.stringify(this.data.dataList[index]));
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    // 获取公告内容
    wx.request({
      url: app.globalData.URL + '/notice/showMessageDetail.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        mainTitle: options.mainTitle
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          dataList: res.data
        })
      },
      fail: function (res) {
        console.log('获取公告信息失败');
        util.showError(that, '网络错误，请稍后重试');
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