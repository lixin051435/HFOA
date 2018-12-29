const util = require("../../../../utils/util");
const root = getApp().globalData.URL;

Page({

  /**
   * 页面的初始数据
   */
  data: {

  },
  toSearchDetail:function(e){
    let item = e.currentTarget.dataset.item;
    if(item.indexOf("公车") > -1){
      util.goTo("/pages/operatePages/searchDetail/public/public");
    }else if(item.indexOf("用印") > -1){
      util.goTo("/pages/operatePages/searchDetail/seal/seal");
    }else if(item.indexOf("休假") > -1){
      util.goTo("/pages/operatePages/searchDetail/vacation/vacation");
    }else if(item.indexOf("差旅") > -1){
      util.goTo("/pages/operatePages/searchDetail/travel/travel");
    }else if(item.indexOf("私车") > -1){
      util.goTo("/pages/operatePages/searchDetail/private/private");
    }else if(item.indexOf("招待") > -1){
      util.goTo("/pages/operatePages/searchDetail/entertain/entertain");
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    wx.request({
      url: root + '/user/toLogin.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          menuList: res.data.data.search,
          // 用来判断是否已提交年假申请,null-未申请
          leaver: res.data.data.leaver
        });
      },
      fail: function (res) {
        console.log('获取首页菜单失败');
        util.showError(that, '获取首页菜单失败');
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