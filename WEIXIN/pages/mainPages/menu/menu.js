// pages/mainPages/menu/menu.js
const app = getApp();
const util = require("../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误信息显隐
    showErrorMsg: false,
    // 公告内容
    mainTitle: "",
    // 用来判断是否已申请年假
    leaver: null
  },
  toMine() {
    let errorMsg = this.data.errorMsg;
    if (errorMsg == '尚未绑定或信息丢失,请到“我的”页面重新授权') {
      wx.switchTab({
        url: '/pages/mainPages/mine/mine'
      })
    }
  },

  // 公告页
  toInformList() {
    util.goTo("../../mainPages/informList/informList?mainTitle=" + this.data.mainTitle);
  },
  // 用印申请页
  toSealApply() {
    util.goTo("../../modulePages/seal/sealApply/sealApply");
  },
  // 休假申请页
  toVacationApply() {
    let that = this;
    if (this.data.leaver === null) {
      util.goTo("../../modulePages/vacation/vacationApply/vacationApply");
    } else {
      util.showError(this, '您已提交年假，请勿重复申请');
      return false;
    }
  },
  // 公车申请页
  toPublicApply() {
    util.goTo("../../modulePages/public/publicStatus/publicStatus");
  },
  // 差旅申请页
  toTravelApply() {
    util.goTo("../../modulePages/travel/travelApply/travelApply");
  },
  // 私车申请页
  toPrivateApply() {
    util.goTo("../../modulePages/private/privateApply/privateApply");
  },
  // 招待申请页
  toEntertainApply() {
    util.goTo("../../modulePages/entertain/entertainApply/entertainApply");
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    
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
    let that = this;
    let status = 0;
    // 已授权
    wx.request({
      url: app.globalData.URL + '/user/getopenId',
      header: {
        'content-type': 'application/json'
      },
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: function (res) {
        util.showNetworkError(res,that);
        status = res.data.data.status;
        if (status == '0') {
          that.setData({
            menuList: []
          });
          util.showError(that, '尚未绑定或信息丢失,请到“我的”页面重新授权');
          return false;
        } else {
          wx.request({
            url: app.globalData.URL + '/notice/showImage.action',
            header: {
              'content-type': 'application/json'
            },
            method: 'GET',
            success: function (res) {
              that.setData({
                banner: res.data[0].imgurl
              })
            },
            fail: function (res) {
              console.log('获取首页顶部图片失败');
              util.showError(that, '网络错误，请稍后重试');
            }
          })

          // 获取公告标题
          wx.request({
            url: app.globalData.URL + '/notice/showMessage.action',
            header: {
              'content-type': 'application/json'
            },
            method: 'GET',
            success: function (res) {
              util.showNetworkError(res,that);
              if (res.data.length < 50) {
                that.setData({
                  mainTitle: res.data
                })
              } else {
                that.setData({
                  mainTitle: ''
                })
              }
            },
            fail: function (res) {
              console.log('获取公告信息失败');
              util.showError(that, '网络错误，请稍后重试');
            }
          })

          // 获取功能模块
          wx.request({
            url: app.globalData.URL + '/user/toLogin.action',
            header: {
              'content-type': 'application/json'
            },
            data: {
              openId: wx.getStorageSync('openId')
            },
            method: 'GET',
            success: function (res) {
              util.showNetworkError(res,that);
              let user = res.data.data.userEntity;
              wx.setStorageSync("username", user.realname);
              wx.setStorageSync("departmentName", user.departmentname);
              that.setData({
                menuList: res.data.data.firstPermission,
                // 用来判断是否已提交年假申请,null-未申请
                leaver: res.data.data.leaver
              });
            },
            fail: function (res) {
              console.log('获取首页菜单失败');
              util.showError(that, '网络错误，请稍后重试');
            }
          })
        }
      },
      fail: function (res) {
        console.log('获取首页顶部图片失败');
        util.showError(that, '网络错误，请稍后重试');
      }
    })
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
    // 显示转发菜单
    wx.showShareMenu({
      withShareTicket: true
    })
  }
})