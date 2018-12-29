// pages/modulePages/public/publicApply/publicApply.js
const app = getApp();
const util = require("../../../../utils/util");
const dTPicker = require('../../../../utils/dateTimePicker.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误信息提示
    showErrorMsg: false,
    // 是否显示车牌类型占位符
    showPublicType: true,
    publicTypeIndex: null,
    // 预借时间占位符
    showBorrowTime: true,
    // 预还时间占位符
    showReturnTime: true,
    // 默认显示第0个审批人
    approveManIndex: 0,
    // 是否显示提交-点击后隐藏
    showSubmit: false,
    changedComanyNum: 0,
    hasCommited: null,
    applyMan: wx.getStorageSync("username"),
    driver: wx.getStorageSync("username")
  },

  // 车牌型号picker
  bindPublicTypeChange(e) {
    let value = e.detail.value;
    let car = this.data.publicType[value];
    let carId = this.data.carId;
    let changedComanyNum = this.data.changedComanyNum;
    // 修改选中的car 和 carId
    value = car.carNum + "," + car.carType;
    carId = car.id;
    this.setData({
      showPublicType: false,
      publicTypeIndex: e.detail.value,
      value: value,
      carId: carId,
      changedComanyNum: changedComanyNum
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
      today_offtime: today_offtime
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
      today_offtime: today_offtime
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
      submitValue.publicType = submitValue.publicType || thatValue.orinial;
      // 输入框部分非空校验
      if (submitValue.driver === "" || submitValue.beginPlace === "" || submitValue.endPlace === "" || submitValue.reason === "") {
        util.showError(that, '请输入全部信息后提交');
        return false;
      } else {
        // 将预借时间转换为字符串格式
        submitValue.borrowTime = thatValue.borrowTimeArray[0][submitValue.borrowTime[0]] + '-' + thatValue.borrowTimeArray[1][submitValue.borrowTime[1]] + '-' + thatValue.borrowTimeArray[2][submitValue.borrowTime[2]] + ' ' + thatValue.borrowTimeArray[3][submitValue.borrowTime[3]] + ':' + thatValue.borrowTimeArray[4][submitValue.borrowTime[4]] + ':' + thatValue.borrowTimeArray[5][submitValue.borrowTime[5]];
        // 将预还时间转换为字符串格式
        submitValue.returnTime = thatValue.returnTimeArray[0][submitValue.returnTime[0]] + '-' + thatValue.returnTimeArray[1][submitValue.returnTime[1]] + '-' + thatValue.returnTimeArray[2][submitValue.returnTime[2]] + ' ' + thatValue.returnTimeArray[3][submitValue.returnTime[3]] + ':' + thatValue.returnTimeArray[4][submitValue.returnTime[4]] + ':' + thatValue.returnTimeArray[5][submitValue.returnTime[5]];

        // 判断是否选择了车牌类型
        let car = thatValue.value;
        if (car == "请选择车牌类型") {
          util.showError(that, "请选择车牌类型");
          return false;
        } else {
          that.data.carCode = car.split(",")[0];
          that.data.carType = car.split(",")[1];
        }

        // 预借时间不能早于当前时间
        submitValue.borrowTime = ((submitValue.borrowTime).replace(/-/g, '/'));
        that.data.onLoad_time = ((that.data.onLoad_time).replace(/-/g, '/'));

        if (new Date(submitValue.borrowTime).getTime() < new Date(that.data.onLoad_time).getTime()) {
          util.showError(that, "预借时间不能早于当前时间");
          return false;
        }

        // 预还时间不能早于预借时间
        if (util.compare(submitValue.borrowTime, submitValue.returnTime)) {
          util.showError(that, "预还时间不能早于预借时间");
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
        
        submitValue.borrowTime = ((submitValue.borrowTime).replace(/\//g, '-'));
        that.data.onLoad_time = ((that.data.onLoad_time).replace(/\//g, '-'));

        let data = {
          ApplyUserName: thatValue.userCode,
          ApplyMan: submitValue.applyMan,
          Driver: submitValue.driver,
          CarCode: thatValue.carCode,
          CarId: thatValue.carId + "",
          CarType: thatValue.carType,
          BeginPlace: submitValue.beginPlace,
          EndPlace: submitValue.endPlace,
          CompareManNum: submitValue.companyNo,
          BeginTimePlan: submitValue.borrowTime,
          EndTimePlan: submitValue.returnTime,
          UseCarReason: submitValue.reason,
          ApproveMan: submitValue.approveMan,
          realApproveMan: submitValue.realApproveMan,
        };
        if (true) {
          wx.request({
            url: app.globalData.URL + '/applyCar/canApply',
            header: {
              'content-type': 'application/json'
            },
            data: {
              CarCode: thatValue.carCode,
              BeginTimePlan: submitValue.borrowTime,
              EndTimePlan: submitValue.returnTime,
              ID: 0
            },
            method: 'GET',
            success: res => {
              // 非限行日
              if (res.data === 'ok') {
                wx.request({
                  url: app.globalData.URL + '/applyCar/startApply.action',
                  header: {
                    'content-type': 'application/json'
                  },
                  data: data,
                  method: 'GET',
                  success: function (res) {
                    that.setData({
                      hasCommited: true,
                      showSubmit: false
                    })
                    util.goTo('../../../publicPages/applyback/applyback');
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
                that.setData({
                  hasCommited: true
                })
                util.showLoading();
              } else {
                util.showError(that, res.data);
                that.setData({
                  hasCommited: false
                })
                return false;
              }
            },
            fail: res => {
              util.showNetworkError(res, that);
            }
          });
        }

      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
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
    var time = year + "-" + month + "-" + date + " " + hour + ":" + minu + ":" + sec;
    this.data.onLoad_time = time;

    // 预借时间、预还时间picker默认选中值
    // 获取完整的年月日、时分秒
    let obj = dTPicker.dateTimePicker(this.data.startYear, this.data.endYear);
    // 精确到分的处理，将数组的秒去掉
    // let lastArray = obj1.dateTimeArray.pop();
    // let lastTime = obj1.dateTime.pop();
    // 默认显示的数据
    that.setData({
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

    // 车牌类型
    let cartype = options.cartype || "";
    let carnum = options.carnum || "";
    let value = "";
    cartype == "" || carnum == "" ? value = "请选择车牌类型" : value = carnum + "," + cartype;
    that.setData({
      value: value
    });
    if (options.cartype != null) {
      that.setData({
        carId: options.carId,
        changedComanyNum: options.peasonnum,
        showPublicType: false
      });
    }

    // 获取默认显示信息
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
            userCode: res.data.user.code,
            applyMan: res.data.user.realname,
            driver: res.data.user.realname,
            publicType: res.data.car,
            approveManList: res.data.approvalMan,
            applyTime: util.getBeginAndEndDate().endTime,
            showSubmit: true
          })
        } else if (res.data.ifOperation === false) {
          that.setData({
            userCode: res.data.user.code,
            applyMan: res.data.user.realname,
            driver: res.data.user.realname,
            publicType: res.data.car,
            approveManList: res.data.approvalMan,
            applyTime: util.getBeginAndEndDate().endTime,
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