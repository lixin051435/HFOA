// pages/mainPages/mine/mine.js
const app = getApp();
const util = require("../../../utils/util");


Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误信息显隐
    showErrorMsg: false,
    // 授权绑定
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    hasUserInfo: false,
    userInfo: null,
    RESuserInfo: null,
    // 新消息徽章提示
    showNewApplyNum: false,
    showNewCheckNum: false,
    newApplyNum: 0,
    newCheckNum: 0,
    authorization: null,
    newccNum: 0,
    newfinNum: 0,
    newApplyNum: 0,
    newCheckNum: 0,
    newCarNum: 0,
    newSealNum: 0,
    pageSize: 5,
    hasClickedBinding: false
  },

  // 解绑
  toRemoveBinding() {
    let that = this;
    wx.showModal({
      title: '提示',
      content: '确认解绑吗？',
      showCancel: true, //是否显示取消按钮
      cancelText: "否", //默认是“取消”
      confirmText: "是", //默认是“确定”
      success: function (res) {
        if (res.cancel) {
          //点击取消,默认隐藏弹框
        } else {
          //点击确定
          util.request(app.globalData.URL + '/user/untieOpenIdUser', {
            openId: wx.getStorageSync("openId")
          }, function (res) {
            wx.removeStorageSync("openId");
            wx.removeStorageSync("authorization");
            wx.removeStorageSync("departmentName");
            wx.removeStorageSync("username");
            console.log('解绑成功');
            wx.switchTab({
              url: '/pages/mainPages/menu/menu'
            })
          });
        }
      },
      fail: function (res) {}, //接口调用失败的回调函数
      complete: function (res) {}, //接口调用结束的回调函数（调用成功、失败都会执行）
    })

  },

  // 我发起的
  toMyApply() {
    util.goTo('../../operatePages/myApply/applyList/applyList');
  },

  // 我审批的
  toMyCheck() {
    util.goTo('../../operatePages/myCheck/checkList/checkList');
  },

  // 抄送我的
  toCarbonCopy() {
    util.goTo('../../operatePages/myCarbonCopy/carbonCopyList/carbonCopyList');
  },

  // 公车处理
  toPublicHandle() {
    util.goTo('../../operatePages/publicHandle/publicList/publicList');
  },

  // 用印处理
  toSealHandle() {
    util.goTo('../../operatePages/sealHandle/sealList/sealList');
  },

  // 财务处理
  toFinanceHandle() {
    util.goTo('../../operatePages/financeHandle/financeHandle');
  },

  // 信息查询
  toSearchDetail() {
    util.goTo('/pages/operatePages/searchDetail/index/index');
    // util.goTo("/pages/operatePages/myApply/adaptable/after/after");
  },

  // 未授权，点击授权按钮进行授权登录
  getUserInfo: function () {
    let that = this;
    util.showLoading("正在加载");
    if (that.data.hasClickedBinding == true) {
      util.showError(that, "正在处理中，请稍候");
      return false;
    } else {
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
                      // 保存openId
                      wx.setStorageSync('openId', res.data.userInfo.openId);
                      // 保存获取到的用户信息
                      that.data.RESuserInfo = res.data.userInfo;
                      // 判断后台是否有openId,有-直接显示用户信息不跳转；无-获取用户信息并跳转绑定
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
                          // statsu-0-尚未绑定；status-1-已绑定
                          if (res.data.data.status === "0") {
                            that.setData({
                              authorization: false,
                              userInfo: null,
                              hasUserInfo: false
                            });
                            wx.setStorageSync('authorization', false);
                            util.goTo('../bindingInfo/bindingInfo');
                          } else if (res.data.data.status === "1") {
                            that.setData({
                              authorization: true,
                              userInfo: that.data.RESuserInfo,
                              hasUserInfo: true
                            });
                            wx.setStorageSync('authorization', true);

                            // 获取功能模块
                            wx.request({
                              url: app.globalData.URL + '/user/toLogin.action',
                              header: {
                                'content-type': 'application/json'
                              },
                              // 传给后台的值
                              data: {
                                openId: wx.getStorageSync('openId')
                              },
                              method: 'GET',
                              success: function (res) {
                                that.setData({
                                  mineList: res.data.data.myPermission
                                });
                              },
                              fail: function (res) {
                                console.log('获取用户信息--失败');
                                util.showError(that, '网络错误,请稍后重试');
                                that.setData({
                                  hasClickedBinding: false
                                });
                              }
                            })
                            that.getBadge();
                          }
                        },
                        fail: function (res) {
                          console.log('判断后台是否有openId--fail');
                          util.showError(that, '网络错误，请稍后重试');
                          that.setData({
                            hasClickedBinding: false
                          });
                        }
                      })
                    } else {
                      console.log('解密失败');
                      that.setData({
                        hasClickedBinding: false
                      });
                    }
                  },
                  fail: function () {
                    console.log('请求自己的服务器，解密用户信息；获取unionId等加密信息失败');
                    util.showError(that, '网络错误,请稍后重试');
                    that.setData({
                      hasClickedBinding: false
                    });
                  }
                })
              },
              fail: function () {
                console.log('获取用户信息失败');
                util.showError(that, '网络错误,请稍后重试');
                that.setData({
                  hasClickedBinding: false
                });
              }
            })
          } else {
            console.log('获取用户登录态失败!');
            util.showError(that, '网络错误,请稍后重试');
            that.setData({
              hasClickedBinding: false
            });
          }
        },
        fail: function () {
          console.log('绑定失败');
          util.showError(that, '网络错误,请稍后重试');
          that.setData({
            hasClickedBinding: false
          });
        }
      })
      that.setData({
        hasClickedBinding: true
      });
    }
  },

  getBadge: function () {
    let that = this;
    // 财务处理徽章
    util.request(app.globalData.URL + '/main/thirdMainapprove', {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        newfinNum: res.data.data.pagesize
      });
    });

    // 抄送处理徽章
    util.request(app.globalData.URL + '/applyExpenses/getcCListApplyExpense', {
      nowPage: 1,
      pageSize: that.data.pageSize,
      cCListOpenId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        newccNum: res.data.data.pagesize
      });
    });

    // 我发起的徽章
    util.request(app.globalData.URL + '/main/mainApply', {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        newApplyNum: res.data.data.pagesize
      });
    });

    // 我处理的徽章
    util.request(app.globalData.URL + '/main/mainApprove.action', {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        newCheckNum: res.data.data.pagesize
      });
    });

    // 用印处理徽章
    wx.request({
      url: app.globalData.URL + '/print/toTreatHandleMessage',
      header: {
        'content-type': 'application/json'
      },
      data: {
        nowPage: 1,
        pageSize: 4,
        openId: wx.getStorageSync('openId'),
        department: '全部',
        gzkind: '全部',
        starttime: null,
        endtime: null,
        applyman: null
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          newSealNum: res.data
        })
      },
      fail: function (res) {
        console.log('获取用户信息失败');
        util.showError(that, '网络错误，请稍后重试');
      }
    })

    // 公车处理徽章
    wx.request({
      url: app.globalData.URL + '/applyCar/outInHandleMessage',
      header: {
        'content-type': 'application/json'
      },
      data: {
        nowPage: 1,
        pageSize: 4,
        openId: wx.getStorageSync('openId'),
        department: '全部',
        carinfo: '全部',
        starttime: null,
        endtime: null,
        applyman: null
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          newCarNum: res.data
        })
      },
      fail: function (res) {
        console.log('获取用户信息失败');
        util.showError(that, '网络错误，请稍后重试');
      }
    })

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

    that.setData({
      authorization: wx.getStorageSync('authorization'),
      hasClickedBinding: false
    })

    // 判断是否授权
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo'] && wx.getStorageSync('openId') !== "") {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称
          wx.getUserInfo({
            success: function (res) {
              that.setData({
                userInfo: res.userInfo,
                hasUserInfo: true
              });
            }
          })
        } else {
          that.setData({
            hasUserInfo: false
          });
          console.log('尚未授权');
        }
      }
    })

    // 已授权
    if (wx.getStorageSync('openId') !== "" && wx.getStorageSync('authorization') === true) {
      // 获取功能模块
      wx.request({
        url: app.globalData.URL + '/user/toLogin.action',
        header: {
          'content-type': 'application/json'
        },
        // 传给后台的值
        data: {
          openId: wx.getStorageSync('openId')
        },
        method: 'GET',
        success: function (res) {
          let user = res.data.data.userEntity;
          wx.setStorageSync("username", user.realname);
          wx.setStorageSync("departmentName", user.departmentname);
          that.setData({
            mineList: res.data.data.myPermission
          });
        },
        fail: function (res) {
          console.log('获取用户信息--失败');
          util.showError(that, '网络错误,请稍后重试');
        }
      });

      this.getBadge();
    } else {
      that.setData({
        mineList: [],
        userInfo: null,
        hasUserInfo: false
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