// pages/operatePages/myApply/confirmVacation/confirmVacation.js
const app = getApp();
const util = require('../../../../utils/util');

// 获取还未休的年假
const unFinishedURL = app.globalData.URL + "/leavel/leaverexecuted";

Page({

  /**
   * 页面的初始数据
   */
  data: {
    icon: app.globalData.URL + '/images/WeiXin/休假.png',
    id: null,
    taskId: null,
    applyMan: null,
    leaverList: null,
    checkedMap: null,
    showErrorMsg: false,
    checkedItem: null
  },

  // 通过复选框选中确认休假项或放弃休假项
  checkboxChange: function (e) {
    this.data.checkedItem = [];
    let indexes = e.detail.value;
    let id = e.currentTarget.dataset.index;
    if (indexes.length > 0) {
      // 选中了当前项,添加到map中,添加选中状态
      let index = parseInt(indexes[0]);
      this.data.checkedMap.set(index, this.data.leaverList[index]);
    } else {
      // 取消选中当前项，从map中删除,取消选中状态
      this.data.checkedMap.delete(id);
    }
    if (this.data.checkedMap.size > 1) {
      util.showError(this, "请逐条确认休假");
    }
    for (let [key, value] of this.data.checkedMap) {
      this.setData({
        checkedItem: value.id
      })
    }
  },

  // 确认休假
  confirmVacation() {
    let that = this;
    if (this.data.checkedMap.size === 0) {
      util.showError(this, "请选择休假信息");
    } else if (this.data.checkedMap.size === 1) {
      let map = that.data.checkedMap;
      let vacation = map.get(0);
      if (vacation == undefined) {
        util.showError(this, "只能休假第一条");
        return;
      } else {
        wx.request({
          url: app.globalData.URL + '/leavel/staffApprove',
          header: {
            'content-type': 'application/json'
          },
          // 传到后台的值
          data: {
            id: vacation.id,
            timeId: vacation.timeId,
            taskId: that.data.taskId,
            leaverStaffResult: 1,
            openId: wx.getStorageSync('openId')
          },
          method: 'GET',
          success: function (res) {
            util.showNetworkError(res, that, "年假确认");
            wx.showToast({
              title: '已确认执行休假',
              icon: 'success',
              duration: 1500
            });
            setTimeout(function () {
              // 获取未休年假个数
              util.request(unFinishedURL, {
                openId: wx.getStorageSync('openId')
              }, function (res) {
                wx.hideLoading();
                let list = res.data.data || [];
                if (list.length > 0) {
                  that.setData({
                    leaverList: list,
                  })
                } else {
                  setTimeout(function () {
                    wx.reLaunch({
                      url: '/pages/mainPages/menu/menu'
                    });
                  }, 500);
                }
              });
              util.showLoading("正在加载...");
            }, 1500);
          },
          fail: function (res) {
            console.log('放弃休假--fail');
            util.showError(that, '网络错误,请稍后重试');
          }
        })
      }
    } else if (this.data.checkedMap.size === 2) {
      util.showError(this, "请逐条确认休假");
    } else if (this.data.checkedMap.size === 3) {
      util.showError(this, "请逐条确认休假");
    }
  },

  // 放弃休假
  giveupVacation() {
    let that = this;
    let map = that.data.checkedMap;
    if (map.size === 0) {
      util.showError(that, "请选择休假信息");
      return false;
    } else if (map.size === 2) {
      util.showError(that, "请逐条放弃休假");
      return false;
    } else if (map.size === 3) {
      util.showError(that, "请逐条放弃休假");
      return false;
    } else if (map.size === 1) {
      let vacation = map.get(0);
      if (vacation == undefined) {
        util.showError(that, "只能放弃第一条");
        return;
      } else {
        wx.showModal({
          title: '提示',
          content: '确定放弃吗?',
          success: function (res) {
            if (res.confirm) {
              wx.request({
                url: app.globalData.URL + '/leavel/staffApprove',
                header: {
                  'content-type': 'application/json'
                },
                // 传到后台的值
                data: {
                  id: vacation.id,
                  timeId: vacation.timeId,
                  taskId: that.data.taskId,
                  leaverStaffResult: 2,
                  openId: wx.getStorageSync('openId')
                },
                method: 'GET',
                success: function (res) {
                  util.showNetworkError(res, that, "年假确认");
                  wx.showToast({
                    title: '已确认放弃休假',
                    icon: 'success',
                    duration: 1500
                  });
                  setTimeout(function () {
                    // 获取未休年假个数
                    util.request(unFinishedURL, {
                      openId: wx.getStorageSync('openId')
                    }, function (res) {
                      let list = res.data.data || [];
                      if (list.length > 0) {
                        that.setData({
                          leaverList: list,
                        })
                      } else {
                        setTimeout(function () {
                          wx.reLaunch({
                            url: '/pages/mainPages/menu/menu'
                          });
                        }, 500);
                      }
                    });
                  }, 1500);
                },
                fail: function (res) {
                  console.log('放弃休假--fail');
                  util.showError(that, '网络错误,请稍后重试');
                }
              })
            }
            if (res.cancel) {
              console.log('用户点击取消')
            }
          }
        })
      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let item = JSON.parse(options.item);
    let showConfirm = options.showConfirm || -1;
    let showCancel = options.showCancel || -1;

    that.setData({
      showConfirm: showConfirm,
      showCancel: showCancel,
      item: item,
      id: item.id,
      taskId: item.taskId,
      applyMan: item.applyMan,
      isConfirm: options.isConfirm,
      checkedMap: new Map()
    })

    if (that.data.showConfirm == 1) {
      wx.setNavigationBarTitle({
        title: '确认休假'
      })
    }
    if (that.data.showCancel == 1) {
      wx.setNavigationBarTitle({
        title: '放弃休假'
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
    let that = this;
    // 搜索待执行年假
    wx.request({
      url: unFinishedURL,
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: res => {
        let leaverList = res.data.data || [];
        that.setData({
          leaverList: leaverList,
        })
        wx.hideLoading();
      },
      fail: () => {
        console.log("获取待执行年假失败");
        util.showError(that, '网络错误,请稍后重试');
      }
    });
    util.showLoading("加载中...");
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