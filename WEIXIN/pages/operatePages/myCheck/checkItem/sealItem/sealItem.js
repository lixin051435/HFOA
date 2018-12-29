// pages/operatePages/myCheck/sealItem/sealItem.js
const app = getApp();
const util = require('../../../../../utils/util');
const dTPicker = require('../../../../../utils/dateTimePicker');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    icon: app.globalData.URL + '/images/WeiXin/用印.png',
    showMore: false,
    userName: wx.getStorageSync('username'),
    url: null,
    appId: null,
    taskid: null,
    status: null,
    showButton: false,
    canUpdate: false,
    canUseSeal: false,
    showTime: false,
    showBeginTime: true,
    showEndTime: true,
    needTaskId: null,
    showModal: false,
    showRejucted: false,
    rejectReason: '',
    showContractAmount: false,
    showContractType: false,
    showSubmit: 0,
    showLeader3: false
  },

  // 点击驳回时输入驳回理由，若不填则为空
  confirmDisagree: function (e) {
    let that = this;
    if (that.data.hasCommited == undefined) {
      that.data.rejectReason = that.data.rejectReason.trim();
      if (that.data.rejectReason === "") {
        that.setData({
          rejectReason: '无'
        })
      } else {
        that.setData({
          rejectReason: that.data.rejectReason.trim()
        })
      }
      wx.request({
        url: app.globalData.URL + '/' + that.data.url,
        data: {
          taskId: that.data.taskId,
          status: wx.getStorageSync('username') + '否决',
          result: false,
          comment: that.data.rejectReason
        },
        method: 'GET',
        success: res => {
          util.goTo('../../../../publicPages/checkback/checkback');
        },
        fail: () => {
          console.log("驳回审批--fail");
          util.showError(that, '网络错误,请稍后重试');
        }
      });
      let hasCommited = true;
      that.setData({
        hasCommited: hasCommited
      });
      util.showLoading();
    } else {
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }
  },

  setRejectReason: function (e) {
    let value = e.detail.value;
    this.setData({
      rejectReason: value
    });
  },
  /**
   * 弹窗
   */
  showDialogBtn: function () {
    this.setData({
      showModal: true
    })
  },

  // 弹出框蒙层截断touchmove事件
  preventTouchMove: function () {

  },
  showModal: function () {
    this.setData({
      showModal: true
    });
  },

  // 隐藏模态对话框
  hideModal: function () {
    this.setData({
      showModal: false
    });
  },
  cancel: function () {
    this.setData({
      showModal: false,
      rejectReason: ""
    });
  },

  // 同意
  agree() {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        status: wx.getStorageSync('username') + '通过',
        result: true
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("通过审批--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  // 不同意
  disagree() {
    this.showModal();
  },

  // 营业执照的同意，多传开始时间和结束时间给后台
  submit: function (e) {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        status: that.data.userName + '通过',
        result: true,
        borrowTime: that.data.item.borrowTime,
        returnTime: that.data.item.returnTime
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("通过审批--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  // 修改
  toUpdate() {
    util.goTo('../../../myApply/adaptable/seal/seal?item=' + JSON.stringify(this.data.item) + "&needTaskId=" + this.data.needTaskId);
  },

  // 用印确认
  toHandle() {
    let that = this;
    wx.request({
      url: app.globalData.URL + '/' + that.data.url,
      data: {
        taskId: that.data.taskId,
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        util.goTo('../../../../publicPages/checkback/checkback');
      },
      fail: () => {
        console.log("用印确认--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
  },

  // 撤回
  toRevoke() {
    let that = this;
    util.confirmRevoke(function () {
      wx.request({
        url: app.globalData.URL + '/print/revokeApply',
        data: {
          taskId: that.data.taskId,
          appId: that.data.appId
        },
        method: 'GET',
        success: res => {
          util.goTo('../../../../publicPages/undoback/undoback');
        },
        fail: () => {
          console.log("用印确认--fail");
          util.showError(that, '网络错误,请稍后重试');
        }
      });
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
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

    let item = JSON.parse(options.item);
    item.applytime = item.applytime.split(" ")[0];

    that.setData({
      isLeaderCheck: options.isLeaderCheck || false,
      applytime: item.applytime
    })

    if (item.canUpdate === null) {
      item.canUpdate = false;
    }
    if (item.canRevoke === null) {
      item.canRevoke = false;
    }
    if (item.needTaskId === null) {
      item.needTaskId = false;
    }

    if (item.comment == null || item.comment == undefined || item.comment == "") {
      this.setData({
        showRejucted: false
      })
    } else {
      this.setData({
        showRejucted: true
      })
    }

    // 如果是合同专用章，显示合同金额
    if (item.gzId === 3) {
      that.setData({
        item: item,
        contracAmount: item.contracAmount,
        showContractAmount: true
      })
    } else {
      that.setData({
        showContractAmount: false
      })
    }

    // 如果合同金额大于50万元，显示合同类型：甲方->甲方；null || ""->乙方
    if (item.gzId === 3) {
      if (item.contracAmount > 50) {
        if (item.contractType == '甲方') {
          that.setData({
            showContractType: true
          })
        } else if (item.contractType == null || item.contractType == "") {
          item.contractType = '乙方';
          that.setData({
            showContractType: true,
            [item.contractType]: item.contractType
          })
        }
      } else {
        that.setData({
          showContractType: false
        })
      }
    }

    // 如果是营业执照原件，且到了第三个审批人审批，显示借出时间和归还时间
    let displayTime = item.displayTime;
    if (displayTime == 1) {
      this.setData({
        showTime: true,
        showSubmit: 1
      })
    } else {
      this.setData({
        showTime: false,
        showSubmit: 0
      })
    }

    if (item.status === "通过") {
      that.setData({
        item: item,
        url: item.url,
        appId: item.id,
        taskId: item.taskId,
        status: item.status,
        showButton: false,
        canUseSeal: item.canUseSeal,
        canUpdate: item.canUpdate,
        canRevoke: item.canRevoke,
        needTaskId: options.needTaskId
      });
    } else {
      that.setData({
        item: item,
        url: item.url,
        appId: item.id,
        taskId: item.taskId,
        status: item.status,
        showButton: options.showButton,
        canUseSeal: item.canUseSeal,
        canUpdate: item.canUpdate,
        canRevoke: item.canRevoke,
        needTaskId: options.needTaskId
      });
    }

    // 是否显示更多内容判断
    if (item.entrustedcardnum === null && item.entrustedcardtype === null && item.entrustedendtime === null && item.entrustedman === null && item.entrustedmatter === null && item.entrustedpermission === null && item.entrustedpost === null && item.entrustedstarttime === null) {
      that.setData({
        showMore: false
      });
    } else {
      that.setData({
        showMore: true
      });
    }

    if (item.businessManager == item.confirmman) {
      that.setData({
        showLeader3: false
      })
    } else {
      that.setData({
        showLeader3: true
      })
    }

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