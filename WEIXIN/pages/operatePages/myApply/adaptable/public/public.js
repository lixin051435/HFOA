// pages/modulePages/public/publicApply/publicApply.js
const app = getApp();
const util = require("../../../../../utils/util");
const dTPicker = require('../../../../../utils/dateTimePicker.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    companyNo: 0,
    showPublicType: true,
    showBorrowTime: true,
    showReturnTime: true,
    approveMan: null,
    approveManIndex: 0,
    showSubmit: true,
    taskId: null,
    needTaskId: null,
    hasCommited: null,
    changedComanyNum: 0,
    hasCommited: null,
    hasBorrowTimeChanged: false,
    hasReturnTimeChanged: false,
    real_borrowTime: '',
    real_returnTime: ''
  },

  // 车牌型号picker
  bindPublicTypeChange(e) {
    this.setData({
      showPublicType: false,
      publicTypeIndex: e.detail.value
    })
  },

  // 预借时间
  bindBorrowTime(e) {
    let value = e.detail.value;
    this.setData({
      showBorrowTime: false,
      borrowTime: value,
      isBorrowTimeChanged: true
    });
    let returnTime = [value[0], value[1], value[2], 17, 0, 0];
    let YY = value[0] + 2018;
    let MM = value[1] + 1;
    let DD = value[2] + 1;
    MM = MM < 10 ? '0' + MM : MM;
    DD = DD < 10 ? '0' + DD : DD;
    let today_offtime = `${YY}-${MM}-${DD} 17:00:00`;
    this.setData({
      returnTime: returnTime,
      today_offtime: today_offtime,
      hasBorrowTimeChanged: true,
      hasReturnTimeChanged: true
    })
  },

  // 预还时间
  bindReturnTime(e) {
    let value = e.detail.value;
    this.setData({
      showReturnTime: false,
      returnTime: value
    });
    let returnTime = [value[0], value[1], value[2], value[3], value[4], value[5]];
    let YY = value[0] + 2018;
    let MM = value[1] + 1;
    let DD = value[2] + 1;
    MM = MM < 10 ? '0' + MM : MM;
    DD = DD < 10 ? '0' + DD : DD;
    value[3] = value[3] < 10 ? '0' + value[3] : value[3];
    value[4] = value[4] < 10 ? '0' + value[4] : value[4];
    value[5] = value[5] < 10 ? '0' + value[5] : value[5];
    let today_offtime = `${YY}-${MM}-${DD} ${value[3]}:${value[4]}:${value[5]}`;
    this.setData({
      returnTime: returnTime,
      today_offtime: today_offtime,
      hasReturnTimeChanged: true
    })
  },

  // 审批人picker
  bindApproveManChange: function (e) {
    this.setData({
      approveManIndex: e.detail.value
    })
  },

  // 提交申请
  formSubmit: function (e) {
    let that = this;
    let thatValue = this.data;
    let submitValue = e.detail.value;

    // 去除字符串前后空格后再进行校验
    submitValue.driver = submitValue.driver.trim();
    submitValue.beginPlace = submitValue.beginPlace.trim();
    submitValue.endPlace = submitValue.endPlace.trim();
    submitValue.reason = submitValue.reason.trim();
    submitValue.companyNo = submitValue.companyNo.trim();

    // 判断是否重复提交
    if (thatValue.hasCommited === true) {
      util.showError(that, "您已提交，请勿重复提交");
    } else {
      // 输入框部分非空校验
      if (submitValue.driver === "" || submitValue.beginPlace === "" || submitValue.endPlace === "" || submitValue.reason === "") {
        util.showError(this, '请输入全部信息后提交');
        return false;
      } else {
        // 将预借时间转换为字符串格式
        submitValue.borrowTime = thatValue.borrowTimeArray[0][submitValue.borrowTime[0]] + '-' + thatValue.borrowTimeArray[1][submitValue.borrowTime[1]] + '-' + thatValue.borrowTimeArray[2][submitValue.borrowTime[2]] + ' ' + thatValue.borrowTimeArray[3][submitValue.borrowTime[3]] + ':' + thatValue.borrowTimeArray[4][submitValue.borrowTime[4]] + ':' + thatValue.borrowTimeArray[5][submitValue.borrowTime[5]];
        // 将预还时间转换为字符串格式
        submitValue.returnTime = thatValue.returnTimeArray[0][submitValue.returnTime[0]] + '-' + thatValue.returnTimeArray[1][submitValue.returnTime[1]] + '-' + thatValue.returnTimeArray[2][submitValue.returnTime[2]] + ' ' + thatValue.returnTimeArray[3][submitValue.returnTime[3]] + ':' + thatValue.returnTimeArray[4][submitValue.returnTime[4]] + ':' + thatValue.returnTimeArray[5][submitValue.returnTime[5]];

        // 判断是否选择车牌型号
        if (submitValue.publicType === null) {
          util.showError(this, '请选择车牌型号');
          return false;
        }

        // 用车事由大于5个字
        let reason = submitValue.reason;
        if (reason.length < 5) {
          util.showError(that, '申请事由请填写5个以上的字符');
          return false;
        }
        // 判断同行人数
        if (submitValue.companyNo === "") {
          util.showError(that, '请输入同行人数');
          return false;
        }
        // 判断输入的同行人数是否合法
        let companyNo = Number(submitValue.companyNo);
        let companyNoPatrn = /^[0-9]{1}$/;
        // 判断输入的同行人数是否是数字
        if (!companyNoPatrn.test(companyNo)) {
          util.showError(that, '同行人数应为数字且不超过最大载客量');
          return false;
        }
        // 判断输入的同行人数是否超过最大载客量
        submitValue.passengerMax = submitValue.passengerMax || thatValue.changedComanyNum;
        if (submitValue.companyNo > submitValue.passengerMax - 1) {
          util.showError(that, '总人数超过最大载客量');
          return false;
        }

        // 将车牌型号字符串拆分成车牌和型号两部分传到后台
        let publicType = submitValue.publicType;
        let arr = publicType.split(',');
        submitValue.carCode = arr[0];
        submitValue.carType = arr[1];

        let hasBorrowTimeChanged = that.data.hasBorrowTimeChanged;
        let hasReturnTimeChanged = that.data.hasReturnTimeChanged;

        if (hasBorrowTimeChanged === false && hasReturnTimeChanged === false) {
          console.log("预借时间、预还时间都没改");
          // 预借时间不能早于当前时间
          that.setData({
            real_borrowTime: that.data.apply_borrowTime,
            real_returnTime: that.data.today_offtime
          })

          that.data.real_borrowTime = ((that.data.real_borrowTime).replace(/-/g, '/'));
          that.data.real_returnTime = ((that.data.real_returnTime).replace(/-/g, '/'));
          that.data.onLoad_time = ((that.data.onLoad_time).replace(/-/g, '/'));

          if (new Date(that.data.real_borrowTime).getTime() < new Date(that.data.onLoad_time).getTime()) {
            util.showError(that, "预借时间不能早于当前时间");
            return false;
          }
          // 预还时间不能早于预借时间
          if (util.compare(that.data.real_borrowTime, that.data.real_returnTime)) {
            util.showError(that, "预还时间不能早于预借时间");
            return false;
          }
        } else if (hasBorrowTimeChanged === true && hasReturnTimeChanged === true) {
          console.log("预借时间、预还时间都改了");
          that.setData({
            real_borrowTime: submitValue.borrowTime,
            real_returnTime: submitValue.returnTime
          })
          // 预借时间不能早于当前时间
          if (new Date(that.data.real_borrowTime).getTime() < new Date(that.data.onLoad_time).getTime()) {
            util.showError(that, "预借时间不能早于当前时间");
            return false;
          }
          // 预还时间不能早于预借时间
          if (util.compare(that.data.real_borrowTime, that.data.real_returnTime)) {
            util.showError(that, "预还时间不能早于预借时间");
            return false;
          }
        } else if (hasBorrowTimeChanged === true && hasReturnTimeChanged === false) {
          console.log("预借时间改了，预还时间没改");
          that.setData({
            real_borrowTime: submitValue.borrowTime,
            real_returnTime: that.data.today_offtime
          })
          // 预借时间不能早于当前时间
          if (new Date(that.data.real_borrowTime).getTime() < new Date(that.data.onLoad_time).getTime()) {
            util.showError(that, "预借时间不能早于当前时间");
            return false;
          }
          // 预还时间不能早于预借时间
          if (util.compare(that.data.real_borrowTime, that.data.real_returnTime)) {
            util.showError(that, "预还时间不能早于预借时间");
            return false;
          }
        } else if (hasBorrowTimeChanged === false && hasReturnTimeChanged === true) {
          console.log("预借时间没改，预还时间改了");
          that.setData({
            real_borrowTime: that.data.apply_borrowTime,
            real_returnTime: submitValue.returnTime
          })
          // 预借时间不能早于当前时间
          if (new Date(that.data.real_borrowTime).getTime() < new Date(that.data.onLoad_time).getTime()) {
            util.showError(that, "预借时间不能早于当前时间");
            return false;
          }
          // 预还时间不能早于预借时间
          if (util.compare(that.data.real_borrowTime, that.data.real_returnTime)) {
            util.showError(that, "预还时间不能早于预借时间");
            return false;
          }
        }

        // 再将-连接的时间传回去
        that.data.real_borrowTime = ((that.data.real_borrowTime).replace(/\//g, '-'));
        that.data.real_returnTime = ((that.data.real_returnTime).replace(/\//g, '-'));
        that.data.onLoad_time = ((that.data.onLoad_time).replace(/\//g, '-'));

        wx.request({
          url: app.globalData.URL + '/applyCar/canApply',
          header: {
            'content-type': 'application/json'
          },
          data: {
            ID: that.data.ID,
            CarCode: submitValue.carCode,
            BeginTimePlan: that.data.real_borrowTime,
            EndTimePlan: that.data.real_returnTime
          },
          method: 'GET',
          success: res => {
            // 非限行日
            if (res.data === 'ok') {
              // 将申请公车表单信息发送给后端
              if (that.data.needTaskId === '1') {
                wx.request({
                  url: app.globalData.URL + '/applyCar/reStartApply.action',
                  header: {
                    'content-type': 'application/json'
                  },
                  // 传到后台的值
                  data: {
                    taskId: that.data.taskId,
                    ID: that.data.ID,
                    ApplyUserName: that.data.userCode,
                    ApplyMan: submitValue.applyMan,
                    Driver: submitValue.driver,
                    CarCode: submitValue.carCode,
                    CarId: submitValue.carId,
                    CarType: submitValue.carType,
                    BeginPlace: submitValue.beginPlace,
                    EndPlace: submitValue.endPlace,
                    CompareManNum: submitValue.companyNo,
                    BeginTimePlan: that.data.real_borrowTime,
                    EndTimePlan: that.data.real_returnTime,
                    UseCarReason: submitValue.reason,
                    ApproveMan: submitValue.approveMan,
                    realApproveMan: submitValue.realApproveMan,
                    applyId: that.data.item.applyId,
                    approvalUserId: that.data.item.approvalUserId,
                    state: that.data.item.state,
                    status: that.data.item.status,
                    department: that.data.item.department,
                    departmentId: that.data.item.departmentId,
                    sorttime: that.data.item.sorttime
                  },
                  method: 'GET',
                  success: function (res) {
                    // 隐藏提交按钮
                    that.setData({
                      showSubmit: false
                    })
                    util.goTo('../../../../publicPages/applyback/applyback');
                  },
                  fail: function (res) {
                    that.setData({
                      hasCommited: false,
                      showSubmit: true
                    })
                    console.log('提交申请公车表单信息--fail');
                    util.showError(that, '网络错误,请稍后重试');
                  }
                })
                util.showLoading();
              } else if (that.data.needTaskId === '0') {
                wx.request({
                  url: app.globalData.URL + '/applyCar/reStartApply.action',
                  header: {
                    'content-type': 'application/json'
                  },
                  // 传到后台的值
                  data: {
                    ID: that.data.ID,
                    ApplyUserName: that.data.userCode,
                    ApplyMan: submitValue.applyMan,
                    Driver: submitValue.driver,
                    CarCode: submitValue.carCode,
                    CarId: submitValue.carId,
                    CarType: submitValue.carType,
                    BeginPlace: submitValue.beginPlace,
                    EndPlace: submitValue.endPlace,
                    CompareManNum: submitValue.companyNo,
                    BeginTimePlan: that.data.real_borrowTime,
                    EndTimePlan: that.data.real_returnTime,
                    UseCarReason: submitValue.reason,
                    ApproveMan: submitValue.approveMan,
                    realApproveMan: submitValue.realApproveMan,
                    applyId: that.data.item.applyId,
                    approvalUserId: that.data.item.approvalUserId,
                    state: that.data.item.state,
                    status: that.data.item.status,
                    department: that.data.item.department,
                    departmentId: that.data.item.departmentId,
                    sorttime: that.data.item.sorttime
                  },
                  method: 'GET',
                  success: function (res) {
                    // 隐藏提交按钮
                    that.setData({
                      hasCommited: false,
                      showSubmit: true
                    })
                    util.goTo('../../../../publicPages/applyback/applyback');
                  },
                  fail: function (res) {
                    console.log('提交申请公车表单信息--fail');
                    util.showError(that, '网络错误,请稍后重试');
                  }
                })
                util.showLoading();
              }
            } else {
              util.showError(that, res.data);
            }
          },
          fail: res => {}
        });
      }
    }

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;

    let item = JSON.parse(options.item);

    // 获取当前时间
    var now = new Date();
    var year = now.getFullYear(); //得到年份
    var month = now.getMonth(); //得到月份
    var date = now.getDate(); //得到日期
    var day = now.getDay(); //得到周几
    var hour = now.getHours(); //得到小时
    var minu = now.getMinutes(); //得到分钟
    var sec = now.getSeconds(); //得到秒
    month = month + 1;
    if (month < 10) month = "0" + month;
    if (date < 10) date = "0" + date;
    if (hour < 10) hour = "0" + hour;
    if (minu < 10) minu = "0" + minu;
    if (sec < 10) sec = "0" + sec;
    var time = year + "/" + month + "/" + date + " " + hour + ":" + minu + ":" + sec;
    this.data.onLoad_time = time;

    // 预借时间、预还时间picker默认选中值
    // 获取完整的年月日、时分秒
    let obj = dTPicker.dateTimePicker(this.data.startYear, this.data.endYear);
    // 精确到分的处理，将数组的秒去掉
    // let lastArray = obj1.dateTimeArray.pop();
    // let lastTime = obj1.dateTime.pop();
    // 默认显示的数据
    this.setData({
      borrowTimeArray: obj.dateTimeArray,
      returnTimeArray: obj.dateTimeArray,
      borrowTime: obj.dateTime,
      returnTime: obj.dateTime
    });

    // 默认预还时间为当天时间的下班时间
    // 获取到今天的日期，加下班时间17:00:00
    let today = new Date();
    let YY = today.getFullYear();
    let MM = today.getMonth() + 1;
    let DD = today.getDate();
    let returnTime = [YY - 2018, MM - 1, DD - 1, 17, 0, 0];
    MM = MM < 10 ? '0' + MM : MM;
    DD = DD < 10 ? '0' + DD : DD;
    let today_offtime = `${YY}-${MM}-${DD} 17:00:00`;
    that.setData({
      returnTime: returnTime,
      today_offtime: today_offtime
    })

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
        that.data.applyTime = item.applyTime.split(" ")[0];
        that.setData({
          needTaskId: options.needTaskId,
          userCode: res.data.user.code,
          applyMan: res.data.user.realname,
          publicType: res.data.car,
          approveManList: res.data.approvalMan,
          // 修改前的数据回显
          applyTime: that.data.applyTime,
          driver: item.driver,
          beginPlace: item.beginPlace,
          endPlace: item.endPlace,
          companyNo: item.compareManNum,
          reason: item.useCarReason,
          approveMan: item.approveMan,
          taskId: item.taskId,
          ID: item.id,
          apply_typenum: `${item.carCode},${item.carType}`,
          apply_borrowTime: item.beginTimePlan,
          today_offtime: item.endTimePlan,
          changedComanyNum: item.carryNum,
          carId: item.carId
        })
      },
      fail: function (res) {
        console.log('获取公车申请默认数据信息--失败');
        util.showError(that, '网络错误,请稍后重试');
      }
    })

    that.setData({
      item: item
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