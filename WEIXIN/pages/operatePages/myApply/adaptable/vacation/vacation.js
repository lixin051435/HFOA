// pages/modulePages/vacation/vacationApply/vacationApply.js
const app = getApp();
const util = require("../../../../../utils/util");

// 获取这个人的年假对象列表，包括已经休的 和 未休的
const URL = app.globalData.URL + "/leavel/listupdateLeaver";


Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 用于radio根据后台传输的frequency进行选择
    isOneTimes: 0,
    isTwoTimes: 0,
    isThreeTimes: 0,
    // 是否还能申请一次，两次，三次
    oneTimeCanUse: true,
    twoTimeCanUse: true,
    threeTimeCanUse: true,
    item: null,
    taskId: null,
    id: null
  },
  split(list) {
    return list.join(",");
  },
  // value 是不是在beginTime和endTime之间，包括等号
  isOverlap(value, beginTime, endTime) {
    let valueTime = new Date(value).getTime();
    beginTime = new Date(beginTime).getTime();
    endTime = new Date(endTime).getTime();
    if (valueTime >= beginTime && valueTime <= endTime) {
      return true;
    }
    return false;
  },

  /**
   * 
   * @param {yyyy-mm-dd} value 时间字符串
   * @param {list} finishedVacationList 以休年假对象列表
   * @return {boolean} 判断value这个时间是否在以休年假的时间段内
   */
  overlapList(value, finishedVacationList) {
    let that = this;
    let flag = false;
    finishedVacationList.forEach(function (e) {
      if (that.isOverlap(value, e.beingTime, e.endTime)) {
        flag = true;
        return flag;
      }
    });
    return flag;
  },

  setTime(e) {
    let that = this;
    let value = e.detail.value;
    let item = e.currentTarget.dataset.item;
    let info = this.data.info;

    let finishedVacationList = info.finishedVacationList;


    switch (item) {
      case "beginTime1":
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (!info.endTime1.indexOf("请选择") > -1) {
          if (new Date(info.endTime1).getTime() < new Date(value).getTime()) {
            util.showError(that, "开始时间不能晚于结束时间");
            return false;
          }
        }
        info[item] = value;
        info.offdays1 = util.getDaysByPickers(value, info.endTime1);
        break;
      case "endTime1":
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (!info.beginTime1.indexOf("请选择") > -1) {
          if (new Date(info.beginTime1).getTime() > new Date(value).getTime()) {
            util.showError(that, "开始时间不能晚于结束时间");
            return false;
          }
        }
        info[item] = value;
        info.offdays1 = util.getDaysByPickers(info.beginTime1, value);
        break;
      case "beginTime2":
        if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (that.isOverlap(value, info.beginTime1, info.endTime1)) {
          util.showError(that, "休假时间不能交叉");
          return false;
        }
        if (!info.endTime2.indexOf("请选择") > -1) {
          if (new Date(info.endTime2).getTime() < new Date(value).getTime()) {
            util.showError(that, "开始时间不能晚于结束时间");
            return false;
          }
        }

        info[item] = value;
        info.offdays2 = util.getDaysByPickers(value, info.endTime2);
        break;
      case "endTime2":
        if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (that.isOverlap(value, info.beginTime1, info.endTime1)) {
          util.showError(that, "休假时间不能交叉");
          return false;
        }
        if (!info.beginTime2.indexOf("请选择") > -1) {
          if (new Date(info.beginTime2).getTime() > new Date(value).getTime()) {
            util.showError(that, "时间不能早于开始时间");
            return false;
          }
        }

        info[item] = value;
        info.offdays2 = util.getDaysByPickers(info.beginTime2, value);
        break;
      case "beginTime3":
        if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (info.beginTime2.indexOf("请选择") > -1 || info.endTime2.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (that.isOverlap(value, info.beginTime1, info.endTime1) || that.isOverlap(value, info.beginTime2, info.endTime2)) {
          util.showError(that, "休假时间不能交叉");
          return false;
        }
        if (!info.endTime3.indexOf("请选择") > -1) {
          if (new Date(info.endTime3).getTime() < new Date(value).getTime()) {
            util.showError(that, "开始时间不能晚于结束时间");
            return false;
          }
        }
        info[item] = value;
        info.offdays3 = util.getDaysByPickers(value, info.endTime3);
        break;
      case "endTime3":
        if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (info.beginTime2.indexOf("请选择") > -1 || info.endTime2.indexOf("请选择") > -1) {
          util.showError(that, "请先补全上一次休假时间");
          return false;
        }
        if (that.overlapList(value, finishedVacationList)) {
          util.showError(that, "时间与已休年假时间交叉");
          return false;
        }
        if (new Date().getTime() > new Date(value).getTime()) {
          util.showError(that, "时间不能早于今天");
          return false;
        }
        if (that.isOverlap(value, info.beginTime1, info.endTime1) || that.isOverlap(value, info.beginTime2, info.endTime2)) {
          util.showError(that, "休假时间不能交叉");
          return false;
        }
        if (!info.beginTime3.indexOf("请选择") > -1) {
          if (new Date(info.beginTime3).getTime() > new Date(value).getTime()) {
            util.showError(that, "时间不能早于开始时间");
            return false;
          }
        }
        info[item] = value;
        info.offdays3 = util.getDaysByPickers(info.beginTime3, value);
        break;

    }
    info[item] = value;
    this.setData({
      info: info
    });
  },

  // 跳转到首页
  toMenu() {
    wx.reLaunch({
      url: '../../../../mainPages/menu/menu'
    })
  },

  // 休假次数单选按钮
  radioChange: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    info.frequency = value;
    this.setData({
      info: info
    });
  },


  // 审批人picker
  bindLeaderChange(e) {
    let value = this.data.leaderList[e.detail.value];
    let info = this.data.info;
    info.approveMan = value.realname;
    info.approveManOpenId = value.openid;
    this.setData({
      info: info
    });
    wx.setStorageSync('leaderIndex', this.data.leaderIndex);
  },

  checkData(info) {
    let restDays = parseInt(this.data.info.restDays);
    if (restDays == 0) {
      util.showError(this, "您已经没有可休天数了");
      return false;
    }
    let days = 0;
    info.offdays = info.offdays.toString();
    if (info.offdays.indexOf(",") > -1) {
      let offdays = info.offdays.split(",");
      offdays.forEach(function (e) {
        e = parseInt(e);
        days += e;
      });
    } else {
      days = parseInt(info.offdays);
    }

    if (days != restDays) {
      util.showError(this, "休假天数和已休天数不符");
      return false;
    }
    if (util.isEmpty(info.approveMan)) {
      util.showError(this, "审批人不能为空");
      return false;
    }
    return true;
  },

  // 提交调整的数据
  formSubmit(e) {
    let that = this;
    let info = this.data.info;

    if (info.frequency == 1) {
      if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      info.beginTime = info.beginTime1;
      info.endTime = info.endTime1;
      info.offdays = info.offdays1;
    } else if (info.frequency == 2) {
      if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      if (info.beginTime2.indexOf("请选择") > -1 || info.endTime2.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      info.beginTime = that.split([info.beginTime1, info.beginTime2]);
      info.endTime = that.split([info.endTime1, info.endTime2]);
      info.offdays = that.split([info.offdays1, info.offdays2]);
      if (new Date(info.beginTime2).getTime() <= new Date(info.endTime1).getTime()) {
        util.showError(that, "开始时间必须晚于上次结束时间");
        return;
      }
    } else if (info.frequency == 3) {
      if (info.beginTime1.indexOf("请选择") > -1 || info.endTime1.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      if (info.beginTime2.indexOf("请选择") > -1 || info.endTime2.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      if (info.beginTime3.indexOf("请选择") > -1 || info.endTime3.indexOf("请选择") > -1) {
        util.showError(that, "请选择开始时间和结束时间");
        return;
      }
      info.beginTime = that.split([info.beginTime1, info.beginTime2, info.beginTime3]);
      info.endTime = that.split([info.endTime1, info.endTime2, info.endTime3]);
      info.offdays = that.split([info.offdays1, info.offdays2, info.offdays3]);
      if (new Date(info.beginTime2).getTime() <= new Date(info.endTime1).getTime()) {
        util.showError(that, "开始时间必须晚于上次结束时间");
        return;
      }
      if (new Date(info.beginTime3).getTime() <= new Date(info.endTime2).getTime()) {
        util.showError(that, "开始时间必须晚于上次结束时间");
        return;
      }
    }


    let data = {
      taskId: info.taskId,
      id: info.id,
      formId: "",
      openId: wx.getStorageSync('openId'),
      applyMan: info.applyMan,
      department: info.department,
      frequency: info.frequency,
      beginTime: info.beginTime,
      endTime: info.endTime,
      offdays: info.offdays,
      approveMan: info.approveMan,
      approveManOpenId: info.approveManOpenId
    };
    let flag = that.checkData(data);

    if (flag) {
      if (that.data.hasCommited == undefined) {
        // 将调整年假表单信息发送给后端
        wx.request({
          url: app.globalData.URL + '/leavel/update',
          header: {
            'content-type': 'application/json'
          },
          data: data,
          method: 'GET',
          success: function (res) {
            util.goTo('../../../../publicPages/applyback/applyback');
          },
          fail: function (res) {
            console.log('年假修改提交-fail');
            util.showError(that, '网络错误,请稍后重试');
            that.setData({
              hasCommited: undefined
            });
          }
        })
        util.showLoading();
        that.setData({
          hasCommited: true
        });

      } else {
        util.showError(that, "您已提交，请勿重复提交");
      }

    }


  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let item = JSON.parse(options.item);
    let info = util.deepClone(item);
    that.setData({
      info: info,
      item: item
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
    let that = this;
    let info = that.data.info;

    util.request(URL, {
      openId: wx.getStorageSync("openId")
    }, function (res) {
      util.showNetworkError(that, res, "年假修改");
      let list = res.data.data.leaver || [];
      // 将年假对象分割成 已休年假列表和未休年假列表
      let vacationList = [];
      let finishedVacationList = [];
      list.forEach(e => {
        if (e.state == "1") {
          finishedVacationList.push(e);
        } else {
          vacationList.push(e);
        }
      });
      info.vacationList = vacationList;
      info.finishedVacationList = finishedVacationList;

      // maxTimes 最多还能修几次假
      let maxTimes = 3 - info.finishedVacationList.length;

      info.frequency = info.vacationList.length;

      // 给开始时间，结束时间，休假天数赋初值
      for (let i = 1; i < 4; i++) {
        info["beginTime" + i] = "请选择开始时间";
        info["endTime" + i] = "请选择结束时间";
        info["offdays" + i] = 0;
      }

      // 计算已经休假天数
      info.finishedDays = 0;
      info.finishedVacationList.forEach(e => {
        info.finishedDays += parseInt(e.days);
      });

      // 给开始时间，结束时间，休假天数赋值，用于回显
      info.vacationList.forEach(function (e, index) {
        let id = index + 1;
        info["beginTime" + id] = e.beingTime;
        info["endTime" + id] = e.endTime;
        info["offdays" + id] = e.days;
      });

      // 获取审批人列表
      util.request(app.globalData.URL + '/user/toLogin', {
        openId: wx.getStorageSync('openId')
      }, function (res) {
        wx.setStorageSync('totalRestDays', res.data.data.days);
        // 计算还剩多少天假可以休
        info.restDays = parseInt(wx.getStorageSync('totalRestDays')) - info.finishedDays;
        that.setData({
          leaderList: res.data.data.leader,
          info: info
        })
        wx.hideLoading();
      });
      util.showLoading("加载中...");


      that.setData({
        info: info,
        maxTimes: maxTimes
      });

    });
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