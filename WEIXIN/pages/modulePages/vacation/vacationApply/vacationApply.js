// pages/modulePages/vacation/vacationApply/vacationApply.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误提示信息显隐
    showErrorMsg: false,
    // 请假时间占位符,默认显示占位符,点击后隐藏并显示日期
    showBeginTime1: true,
    showEndTime1: true,
    showBeginTime2: true,
    showEndTime2: true,
    showBeginTime3: true,
    showEndTime3: true,
    showTime2: false,
    showTime3: false,
    // 请假天数
    beginTime1: null,
    beginTime2: null,
    beginTime3: null,
    endTime1: null,
    endTime2: null,
    endTime3: null,
    offdays: 0,
    offdays1: 0,
    offdays2: 0,
    offdays3: 0,
    // 审批人占位符,默认显示占位符,点击后隐藏并显示审批人
    leaderIndex: 0,
    // 显示提交按钮
    showSubmit: true,
    applyMan: wx.getStorageSync("username"),
    department: wx.getStorageSync("departmentName")
  },

  split(list) {
    return list.join(",");
  },

  // 休假次数单选按钮
  radioChange: function (e) {
    if (e.detail.value === '1') {
      this.setData({
        showTime2: false,
        showTime3: false
      })
    }
    if (e.detail.value === '2') {
      this.setData({
        showTime2: true,
        showTime3: false
      })
    }
    if (e.detail.value === '3') {
      this.setData({
        showTime2: true,
        showTime3: true
      })
    }
  },

  // 申请时间picker1
  bindBeginTimeChange1: function (e) {
    let beginTime1 = e.detail.value;
    let endTime1 = this.data.endTime1;

    if (endTime1 != null) {
      if (util.afterToday(beginTime1)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }

      if (new Date(beginTime1).getTime() > new Date(endTime1).getTime()) {
        util.showError(this, "开始时间应当早于结束时间");
        return false;
      } else {
        this.setData({
          showBeginTime1: false,
          beginTime1: beginTime1,
          offdays1: util.getDaysByPickers(beginTime1, endTime1)
        });
      }

    } else {
      if (util.afterToday(beginTime1)) {
        util.showError(this, "时间不能早于今天");
        return false;
      } else {
        this.setData({
          showBeginTime1: false,
          beginTime1: beginTime1
        })
      }
    }

  },
  bindEndTimeChange1: function (e) {
    let endTime1 = e.detail.value;
    let beginTime1 = this.data.beginTime1;

    // 计算已选天数
    if (beginTime1 === null) {
      util.showError(this, '请先选择开始时间');
      return false;
    } else {
      if (util.afterToday(endTime1)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }
      if (new Date(beginTime1).getTime() > new Date(endTime1).getTime()) {
        util.showError(this, '开始时间应当早于结束时间');
        return false;
      } else {
        this.setData({
          showEndTime1: false,
          endTime1: endTime1,
          offdays1: util.getDaysByPickers(beginTime1, endTime1)
        })
      }
    }
  },

  // 申请时间picker2
  bindBeginTimeChange2: function (e) {
    let beginTime2 = e.detail.value;
    let endTime2 = this.data.endTime2;

    if (this.data.endTime1 == null || this.data.beginTime1 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }

    if (new Date(beginTime2).getTime() <= new Date(this.data.endTime1).getTime()) {
      util.showError(this, "开始时间必须晚于上次休假结束时间");
      return false;
    }

    if (endTime2 != null) {
      if (util.afterToday(beginTime2)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }

      if (new Date(beginTime2).getTime() > new Date(endTime2).getTime()) {
        util.showError(this, "开始时间应当早于结束时间");
        return false;
      } else {
        this.setData({
          showBeginTime2: false,
          beginTime2: beginTime2,
          offdays2: util.getDaysByPickers(beginTime2, endTime2)
        });
      }

    } else {
      if (util.afterToday(beginTime2)) {
        util.showError(this, "时间不能早于今天");
        return false;
      } else {
        this.setData({
          showBeginTime2: false,
          beginTime2: beginTime2
        })
      }
    }

  },
  bindEndTimeChange2: function (e) {
    let beginTime2 = this.data.beginTime2;
    let endTime2 = e.detail.value;
    if (this.data.endTime1 == null || this.data.beginTime1 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }


    // 计算已选天数
    if (beginTime2 === null) {
      util.showError(this, '请先选择开始时间');
      return false;
    } else {
      if (util.afterToday(endTime2)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }

      if (new Date(beginTime2).getTime() > new Date(endTime2).getTime()) {
        util.showError(this, '开始时间应当早于结束时间');
        return false;
      } else {
        this.setData({
          showEndTime2: false,
          endTime2: endTime2,
          offdays2: util.getDaysByPickers(beginTime2, endTime2)
        })
      }
    }
  },

  // 申请时间picker3
  bindBeginTimeChange3: function (e) {
    let beginTime3 = e.detail.value;
    let endTime3 = this.data.endTime3;
    if (this.data.endTime2 == null || this.data.beginTime2 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }
    if (this.data.endTime2 == null || this.data.beginTime2 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }

    if (new Date(beginTime3).getTime() <= new Date(this.data.endTime2).getTime()) {
      util.showError(this, "开始时间必须晚于上次休假结束时间");
      return false;
    }

    if (endTime3 != null) {
      if (util.afterToday(beginTime3)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }

      if (new Date(beginTime3).getTime() > new Date(endTime3).getTime()) {
        util.showError(this, "开始时间应当早于结束时间");
        return false;
      } else {
        this.setData({
          showBeginTime3: false,
          beginTime3: beginTime3,
          offdays3: util.getDaysByPickers(beginTime3, endTime3)
        });
      }

    } else {
      if (util.afterToday(beginTime3)) {
        util.showError(this, "时间不能早于今天");
        return false;
      } else {
        this.setData({
          showBeginTime3: false,
          beginTime3: beginTime3,
        })
      }
    }
  },
  bindEndTimeChange3: function (e) {
    let endTime3 = e.detail.value;
    let beginTime3 = this.data.beginTime3;
    if (this.data.endTime2 == null || this.data.beginTime2 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }
    if (this.data.endTime2 == null || this.data.beginTime2 == null) {
      util.showError(this, "请先选择上一次请假信息");
      return false;
    }
    // 计算已选天数
    if (beginTime3 == null) {
      util.showError(this, '请先选择开始时间');
      return false;
    } else {
      if (new Date(beginTime3).getTime() > new Date(endTime3).getTime()) {
        util.showError(this, '开始时间应当早于结束时间');
        return false;
      } else {
        this.setData({
          showEndTime3: false,
          endTime3: endTime3,
          offdays3: util.getDaysByPickers(beginTime3, endTime3)
        })
      }
    }
  },

  // 审批人picker
  bindLeaderChange: function (e) {
    this.setData({
      leaderIndex: e.detail.value
    })
  },

  checkData(data) {
    if (this.data.days == 0 || this.data.days == "") {
      util.showError(this, "您已经没有可休天数了");
      return false;
    }
    if (data.beginTime.indexOf("请选择") > -1) {
      util.showError(this, "请补全所有信息后提交");
      return false;
    }
    if (data.endTime.indexOf("请选择") > -1) {
      util.showError(this, "请补全所有信息后提交");
      return false;
    }

    return true;
  },

  // 提交申请
  formSubmit: function (e) {
    let that = this;
    let data = this.data;
    let submitValue = e.detail.value;

    // 传给后台的开始时间，结束时间，休假天数
    let beginTime, endTime, offdays;
    // 判断是否重复提交
    if (this.data.hasCommited === true) {
      util.showError(that, "您已提交，请勿重复提交");
    } else {
      // 休假一次
      if (submitValue.frequency == 1) {
        beginTime = data.beginTime1;
        endTime = data.endTime1;
        offdays = data.offdays1;
        data.offdays = data.offdays1;
        // 判断已选休假总天数与应休天数是否相符
        if (data.offdays != submitValue.days) {
          util.showError(this, '已选休假总天数与应休天数不符');
          return false;
        }
      }
      // 休假两次
      else if (submitValue.frequency == 2) {

        beginTime = that.split([data.beginTime1, data.beginTime2]);
        endTime = that.split([data.endTime1, data.endTime2]);
        offdays = that.split([data.offdays1, data.offdays2]);
        data.offdays = data.offdays1 + data.offdays2;
        // 判断已选休假总天数与应休天数是否相符
        if (data.offdays != submitValue.days) {
          util.showError(this, '已选休假总天数与应休天数不符');
          return false;
        }
      }
      // 休假三次
      else if (submitValue.frequency == 3) {
        // 传数据到后台时，将开始时间、结束时间、休假天数分别组成字符串传过去
        beginTime = that.split([data.beginTime1, data.beginTime2, data.beginTime3]);
        endTime = that.split([data.endTime1, data.endTime2, data.endTime3]);
        offdays = that.split([data.offdays1, data.offdays2, data.offdays3]);
        data.offdays = data.offdays1 + data.offdays2 + data.offdays3;
        // 判断已选休假总天数与应休天数是否相符
        if (data.offdays != submitValue.days) {
          util.showError(this, '已选休假总天数与应休天数不符');
          return false;
        }
      }
      // 判断提交的数据是否为空
      if (submitValue.applyMan == "" || submitValue.department == "" || submitValue.approveMan == null) {
        util.showError(this, '请填写所有信息后提交');
        return false;
      }
      // 符合提交要求
      else {
        let data = {
          applyTime: that.data.applyTime,
          formId: e.detail.formId,
          user_id: this.data.user_id,
          openId: wx.getStorageSync("openId"),
          applyMan: submitValue.applyMan,
          department: submitValue.department,
          frequency: submitValue.frequency,
          beginTime: beginTime,
          endTime: endTime,
          offdays: offdays,
          approveMan: submitValue.approveMan,
          approveManOpenId: submitValue.openId
        };
        if (data.approveManOpenId == null || data.approveManOpenId == "") {
          util.showError(this, "该审批人没有绑定");
          return false;
        }
        let flag = that.checkData(data);
        if (flag) {
          wx.request({
            url: app.globalData.URL + '/leavel/insertLeavel',
            header: {
              'content-type': 'application/json'
            },
            data: data,
            method: 'GET',
            success: function (res) {
              util.showNetworkError(res, that, "年假申请");
              that.setData({
                hasCommited: true,
                showSubmit: false
              })
              util.goTo("../../../publicPages/applyback/applyback");
            },
            fail: function (res) {
              that.setData({
                hasCommited: false,
                showSubmit: true
              })
              console.log('提交年假申请--fail');
              util.showError(that, '网络错误,请稍后重试');
            }
          })
          that.setData({
            hasCommited: true
          })
          util.showLoading();
        }
      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {


    let that = this;
    let applyTime = util.getBeginAndEndDate().endTime;
    that.setData({
      applyTime: applyTime
    });
    // 获取成功后传值给后台，以便获取对应人员信息，显示到页面
    wx.request({
      url: app.globalData.URL + '/user/toLogin',
      header: {
        'content-type': 'application/json'
      },
      // 传给后台的值
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      // 从后台获取数据显示到申请页面
      success: function (res) {
        // 单独取出可休天数days，用来在填写表单时做校验

        that.setData({
          user_id: res.data.data.userEntity.id,
          applyMan: res.data.data.userEntity.username,
          department: res.data.data.userEntity.departmentname,
          days: res.data.data.days,
          leaderList: res.data.data.leader
        });

        if (res.data.data.days == "") {
          that.setData({
            days: 0
          });
        }
      },
      fail: function (res) {
        console.log('获取年假申请默认数据信息--失败');
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