// pages/operatePages/myCheck/publicOut/publicOut.js
const app = getApp();
const util = require('../../../../utils/util');
const dTPicker = require('../../../../utils/dateTimePicker');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    showSubmit: true,
    user_icon: app.globalData.URL + '/images/WeiXin/公车.png',
    showBorrowOutTime: true
  },

  bindBorrowOutTime(e) {
    this.setData({
      showBorrowOutTime: false,
      borrowOutTime: e.detail.value
    });
  },

  formSubmit(e) {
    let that = this;
    // 将入库时间转换为字符串格式
    e.detail.value.borrowOutTime = this.data.borrowOutTimeArray[0][e.detail.value.borrowOutTime[0]] + '-' + this.data.borrowOutTimeArray[1][e.detail.value.borrowOutTime[1]] + '-' + this.data.borrowOutTimeArray[2][e.detail.value.borrowOutTime[2]] + ' ' + this.data.borrowOutTimeArray[3][e.detail.value.borrowOutTime[3]] + ':' + this.data.borrowOutTimeArray[4][e.detail.value.borrowOutTime[4]] + ':' + this.data.borrowOutTimeArray[5][e.detail.value.borrowOutTime[5]];
    // 发送数据到后端
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        status: 4,
        lengthBegin: e.detail.value.lengthBegin,
        beginTime: e.detail.value.borrowOutTime,
        remarks: e.detail.value.remarks,
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("提交出库--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.onLoad_time = new Date();
    // 预借时间、预还时间picker默认选中值
    // 获取完整的年月日、时分秒
    let obj = dTPicker.dateTimePicker(this.data.startYear, this.data.endYear);
    // 精确到分的处理，将数组的秒去掉
    // let lastArray = obj1.dateTimeArray.pop();
    // let lastTime = obj1.dateTime.pop();
    // 页面传值处理
    let item = JSON.parse(options.item);
    // let showButton = options.showButton;
    // let canUpdate = options.canUpdate;
    let showbutton = true;
    let canupdate = true;
    // 默认显示的数据
    this.setData({
      borrowOutTimeArray: obj.dateTimeArray,
      borrowOutTime: obj.dateTime,
      item: item,
      url: item.url,
      lengthBegin: item.lengthBegin,
      taskId: item.taskId,
      showbutton: showbutton,
      canupdate: canupdate
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