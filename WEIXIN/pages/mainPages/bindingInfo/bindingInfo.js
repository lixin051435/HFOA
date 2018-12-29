// pages/mainPages/bindingInfo/bindingInfo.js
const app = getApp();
const util = require("../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误提示信息显隐
    showErrorMsg: false,
    hasSubmit: false
  },

  formSubmit: function (e) {
    let that = this;
    if (that.data.hasSubmit == true) {
      util.showError(that, "您已提交，请勿重复提交");
      return false;
    } else {
      // 判断输入的数据是否为空
      if (e.detail.value.userName === "" || e.detail.value.password === "") {
        util.showError(this, '请填写所有信息后提交');
        return false;
      }
      // 判断输入的用户名是否合法
      let userName = e.detail.value.userName;
      if (userName == "") {
        util.showError(this, '请输入用户名');
        return false;
      }
      // 传值给后台并跳转
      wx.request({
        url: app.globalData.URL + '/user/updateUserOpenId',
        header: {
          'content-type': 'application/json'
        },
        // 传给后台的值
        data: {
          userName: e.detail.value.userName,
          password: e.detail.value.password,
          openId: wx.getStorageSync("openId")
        },
        method: 'GET',
        success: function (res) {
          if (res.data.data.status === '1') {
            console.log('绑定失败');
            util.showError(that, res.data.data.error);
            return false;
          } else if (res.data.data.status === '0') {
            console.log('绑定成功');
            wx.setStorageSync('authorization', true);
            wx.switchTab({
              url: '../../mainPages/menu/menu'
            })
          }
        },
        fail: function (res) {
          console.log('绑定用户信息--fail');
          util.showError(that, '网络错误,请稍后重试');
          return false;
        }
      })
    }
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
    if (wx.getStorageSync('openId') === "") {
      let that = this;
      wx.login({
        success: function (res) {
          // 1、获取登录凭证
          let code = res.code;
          if (code) {
            // 2、调用获取用户信息接口
            wx.getUserInfo({
              success: function (res) {
                // 3、请求自己的服务器，解密用户信息 获取unionId等加密信息
                wx.request({
                  // 自己的服务接口地址
                  url: app.globalData.URL + '/wXLoginController/decodeUserInfo',
                  method: 'GET',
                  header: {
                    'content-type': 'application/x-www-form-urlencoded'
                  },
                  data: {
                    encryptedData: res.encryptedData,
                    iv: res.iv,
                    code: code
                  },
                  success: function (res) {
                    // 4、解密成功后 获取自己服务器返回的结果
                    if (res.data.status == 1) {
                      let userInfo = res.data.userInfo;
                      // 保存openId
                      wx.setStorageSync('openId', res.data.userInfo.openId);
                      that.setData({
                        userInfo: res.data.userInfo,
                        hasUserInfo: true
                      });
                    } else {
                      console.log('解密失败');
                      util.showError(that, '网络错误,请稍后重试');
                      return false;
                    }
                  },
                  fail: function () {
                    console.log('请求自己的服务器，解密用户信息；获取unionId等加密信息失败');
                    util.showError(that, '网络错误,请稍后重试');
                    return false;
                  }
                })
              },
              fail: function () {
                console.log('获取用户信息失败');
                util.showError(that, '网络错误,请稍后重试');
                return false;
              }
            })
          } else {
            console.log('获取用户登录态失败!');
            util.showError(that, '网络错误,请稍后重试');
            return false;
          }
        },
        fail: function () {
          console.log('绑定失败');
          util.showError(that, '网络错误,请稍后重试');
          return false;
        }
      })
    }
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