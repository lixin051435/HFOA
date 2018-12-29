// pages/operatePages/myCheck/publicIn/publicIn.js
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
    showBorrowOutTime: true,
    useCarTime: 0,
    beginTime: 0,
    lengthBegin: null
  },

  bindBorrowOutTime(e) {
    // 将入库时间转换为字符串格式
    e.detail.value.endTime = this.data.borrowOutTimeArray[0][e.detail.value[0]] + '-' + this.data.borrowOutTimeArray[1][e.detail.value[1]] + '-' + this.data.borrowOutTimeArray[2][e.detail.value[2]] + ' ' + this.data.borrowOutTimeArray[3][e.detail.value[3]] + ':' + this.data.borrowOutTimeArray[4][e.detail.value[4]] + ':' + this.data.borrowOutTimeArray[5][e.detail.value[5]];

    // 计算借车时长,不足1小时为0
    //时间差的毫秒数
    let date = new Date(e.detail.value.endTime).getTime() - new Date(this.data.beginTime).getTime();
    //计算出相差天数
    let days = Math.floor(date / (24 * 3600 * 1000));
    //计算出小时数
    let leave1 = date % (24 * 3600 * 1000);
    //计算天数后剩余的毫秒数
    let hours = Math.floor(leave1 / (3600 * 1000));
    //计算相差分钟数
    let leave2 = leave1 % (3600 * 1000);
    //计算小时数后剩余的毫秒数
    let minutes = Math.floor(leave2 / (60 * 1000));
    //计算相差秒数  
    let leave3 = leave2 % (60 * 1000);
    //计算分钟数后剩余的毫秒数
    let seconds = Math.round(leave3 / 1000);
    this.setData({
      showBorrowOutTime: false,
      borrowOutTime: e.detail.value,
      useCarTime: days * 24 + hours
    });
  },

  formSubmit(e) {
    let that = this;
    let thatValue = that.data;
    let submitValue = e.detail.value;

    // 将入库时间转换为字符串格式
    submitValue.endTime = thatValue.borrowOutTimeArray[0][submitValue.endTime[0]] + '-' + thatValue.borrowOutTimeArray[1][submitValue.endTime[1]] + '-' + thatValue.borrowOutTimeArray[2][submitValue.endTime[2]] + ' ' + thatValue.borrowOutTimeArray[3][submitValue.endTime[3]] + ':' + thatValue.borrowOutTimeArray[4][submitValue.endTime[4]] + ':' + thatValue.borrowOutTimeArray[5][submitValue.endTime[5]];
    if (submitValue.endLength === '') {
      util.showError(that, '请输入行驶里程');
      return false;
    } else if (parseInt(submitValue.endLength) < parseInt(that.data.lengthBegin)) {
      util.showError(that, '入库里程小于出库里程，请重新输入');
      return false;
    } else {
      // 发送数据到后端
      wx.request({
        url: app.globalData.URL + '/' + that.data.url,
        data: {
          taskId: that.data.taskId,
          status: 5,
          endLength: submitValue.endLength,
          endTime: submitValue.endTime,
          useCarTime: submitValue.useCarTime,
          openId: wx.getStorageSync('openId')
        },
        method: 'GET',
        success: res => {
          util.goTo('../../../publicPages/checkback/checkback');
        },
        fail: () => {
          console.log("提交入库--fail");
          util.showError(that, '网络错误,请稍后重试');
        }
      });
    }
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
      taskId: item.taskId,
      lengthBegin: item.lengthBegin,
      beginTime: item.beginTime,
      showbutton: showbutton,
      canupdate: canupdate,
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