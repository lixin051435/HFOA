// pages/operatePages/publicHandle/publicList/publicList.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    // 默认模块头像
    iconPublic: app.globalData.URL + '/images/WeiXin/公车.png',
    // 待审批和已审批条数
    pendingOutNumber: 0,
    pendingInNumber: 0,
    // 默认显示第几个菜单导航
    currentTab: 'pending',
    // 每一页显示多少条数据
    pageSize: 5,
    // 待审批和已审批的ArrayList
    publicOutList: [],
    publicInList: [],
    // 待审批的总页数和当前页码
    totalPage: 100,
    pageNumber: 1,
    // 已审批的总页数和当前页码
    approvedPageNumber: 1,
    approvedTotalPage: 100,
    // 无数据标识
    pending_noData: true,
    approved_noData: true,
    showModal: false,
    applyMan: wx.getStorageSync("username"),
    clickSearch: false
  },

  // 切换顶部tab导航
  switchNav(event) {
    let status = event.currentTarget.dataset.status;
    // if (status == "pending") {
    //   this.setData({
    //     pageNumber: 1
    //   });
    // } else if (status == "approved") {
    //   this.setData({
    //     approvedPageNumber: 1
    //   });
    // }
    if (this.data.currentTab === status) {
      return false;
    } else {
      this.setData({
        currentTab: status,
        clickSearch: false
      })
    }
  },

  init: function () {
    let that = this;
    that.setData({
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    });
    util.showLoading("正在加载");
    // 出库
    wx.request({
      url: app.globalData.URL + '/applyCar/getOutTask',
      data: {
        nowPage: 1,
        pageSize: that.data.pageSize,
        openId: wx.getStorageSync('openId'),
        department: '全部',
        carinfo: '全部',
        starttime: null,
        endtime: null,
        applyman: null
      },
      method: "GET",
      success: function (res) {
        if (res.data.data.list.length === 0) {
          that.setData({
            pending_noData: true,
            pendingOutNumber: 0,
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          });
        } else {
          that.setData({
            pending_noData: false,
            publicOutList: res.data.data.list || [],
            pendingOutNumber: res.data.data.pagesize,
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          });
        }
        wx.hideLoading();
      },
      fail: function (res) {
        console.log("获取出库列表失败");
        util.showError(that, '获取出库列表失败');
      }
    });

    // 入库
    wx.request({
      url: app.globalData.URL + '/applyCar/getInTask',
      data: {
        nowPage: 1,
        pageSize: that.data.pageSize,
        openId: wx.getStorageSync('openId'),
        department: '全部',
        carinfo: '全部',
        starttime: null,
        endtime: null,
        applyman: null
      },
      method: "GET",
      success: function (res) {
        if (res.data.data.list.length === 0) {
          that.setData({
            approved_noData: true,
            pendingInNumber: 0,
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          });
        } else {
          that.setData({
            approved_noData: false,
            publicInList: res.data.data.list || [],
            pendingInNumber: res.data.data.pagesize,
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          });
        }
      },
      fail: function (res) {
        console.log("获取入库列表失败");
        util.showError(that, '获取入库列表失败');
      }
    });
  },

  toPublicOutItem: function (e) {
    let id = e.currentTarget.id;
    let item = this.data.publicOutList[id];
    util.goTo("../publicOut/publicOut?showButton=1&item=" + JSON.stringify(item) + "&showButton=false&needTaskId=0&showRejucted=false");
  },

  toPublicInItem: function (e) {
    let id = e.currentTarget.id;
    let item = this.data.publicInList[id];
    if (item.status === 4) {
      util.goTo("../publicIn/publicIn?showButton=1&item=" + JSON.stringify(item) + "&showButton=false&needTaskId=0&showRejucted=false");
    }
  },

  /**
   * 搜索相关函数
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
    let that = this;
    let searchInfo = that.data.searchInfo;
    searchInfo.beginTime = util.getBeginAndEndDate().beginTime;
    searchInfo.endTime = util.getBeginAndEndDate().endTime;
    this.setData({
      searchInfo: searchInfo,
      showModal: false
    });
  },

  // 对话框取消按钮点击事件
  cancel: function () {
    this.hideModal();
  },

  setApplyMan: function (e) {
    let value = e.detail.value;
    let searchInfo = this.data.searchInfo;
    searchInfo.applyMan = value;
    this.setData({
      searchInfo: searchInfo
    });
  },

  setDepartment: function (e) {
    let value = e.detail.value;
    let searchInfo = this.data.searchInfo;
    let name = this.data.searchInfo.departmentNameList[value];
    searchInfo.departmentName = name;
    this.setData({
      searchInfo: searchInfo
    });
  },

  setType: function (e) {
    let value = e.detail.value;
    let searchInfo = this.data.searchInfo;
    let type = this.data.searchInfo.typeList[value];
    searchInfo.type = type;
    this.setData({
      searchInfo: searchInfo
    });
  },

  bindTime: function (e) {
    let value = e.detail.value;
    let item = e.currentTarget.dataset.item;
    let searchInfo = this.data.searchInfo;
    switch (item) {
      case "beginTime":
        if (new Date(value).getTime() > new Date(searchInfo.endTime).getTime()) {
          util.showError(this, "结束时间不能早于开始时间");
          return false;
        }
        break;
      case "endTime":
        if (new Date(searchInfo.beginTime).getTime() > new Date(value).getTime()) {
          util.showError(this, "结束时间不能早于开始时间");
          return false;
        }
        break;
    }
    searchInfo[item] = value;
    this.setData({
      searchInfo: searchInfo
    });
  },

  setCarName: function (e) {
    let value = e.detail.value;
    let searchInfo = this.data.searchInfo;
    searchInfo.carName = searchInfo.carNames[value];
    this.setData({
      searchInfo: searchInfo
    });
  },

  // 点击查询按钮
  search: function () {
    let that = this;
    let searchInfo = that.data.searchInfo;
    let data = {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId'),
      department: searchInfo.departmentName,
      carinfo: searchInfo.carName,
      starttime: searchInfo.beginTime,
      endtime: searchInfo.endTime,
      applyman: searchInfo.applyMan || "null"
    };
    if (that.data.currentTab === "pending") {
      wx.request({
        url: app.globalData.URL + '/applyCar/getOutTask',
        data: data,
        method: "GET",
        success: function (res) {
          that.setData({
            pending_noData: res.data.data.list.length === 0,
            publicOutList: res.data.data.list || [],
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
            pendingOutNumber: res.data.data.pagesize,
            pageNumber: 1,
            clickSearch: true
          });
          that.hideModal();
          wx.hideLoading();
        },
        fail: function (res) {
          console.log("出库信息查询失败");
        }
      });
      util.showLoading("正在查询");
    } else if (that.data.currentTab === "approved") {
      wx.request({
        url: app.globalData.URL + '/applyCar/getInTask',
        data: data,
        method: "GET",
        success: function (res) {
          that.setData({
            approved_noData: res.data.data.list.length === 0,
            publicInList: res.data.data.list || [],
            approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
            pendingInNumber: res.data.data.pagesize,
            approvedPageNumber: 1,
            clickSearch: true
          });
          that.hideModal();
          wx.hideLoading();
        },
        fail: function (res) {
          console.log("入库信息查询失败");
        }
      });
      util.showLoading("正在查询");
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let searchInfo = {
      beginTime: util.getBeginAndEndDate().beginTime,
      endTime: util.getBeginAndEndDate().endTime,
      departmentName: '全部',
      search_department: '',
      applyMan: "",
      openId: wx.getStorageSync("openId")
    };
    that.setData({
      searchInfo: searchInfo
    });

    // 获取所有部门列表
    util.request(app.globalData.URL + "/department/getAllDepartment", {}, function (res) {
      let searchInfo = that.data.searchInfo;
      let departmentObjectList = res.data || [];
      let departmentNameList = [];
      departmentObjectList.forEach(element => {
        departmentNameList.push(element.departmentname);
      });
      departmentNameList.unshift("全部");
      searchInfo.departmentNameList = departmentNameList;
      searchInfo.departmentObjectList = departmentObjectList;
      searchInfo.departmentName = departmentNameList[0];
      that.setData({
        searchInfo: searchInfo
      });
    });


    // 获取个人信息 是否是普通员工,真实姓名
    util.getInfo(function (data, realname) {
      let searchInfo = that.data.searchInfo;
      searchInfo.applyMan = '';
      searchInfo.IamAverage = data;
      that.setData({
        searchInfo: searchInfo
      });
    });

    util.getCarInfo(function (data) {
      let searchInfo = that.data.searchInfo;
      searchInfo.carNames = data.carNames;
      searchInfo.carNames.unshift("全部");
      searchInfo.carName = data.carNames[0] || "请选择车牌号";
      that.setData({
        searchInfo: searchInfo
      });
    });

    // 获取当前登录用户的所属部门
    wx.request({
      url: app.globalData.URL + '/user/toLogin',
      data: {
        openId: wx.getStorageSync("openId")
      },
      method: 'GET',
      dataType: 'json',
      success: res => {
        let searchInfo = that.data.searchInfo;
        searchInfo.search_department = res.data.data.userEntity.departmentname;
        that.setData({
          searchInfo: searchInfo,
          applyMan: res.data.data.userEntity.realname
        });
      },
      fail: () => {
        util.showError(that, '获取部门信息失败');
      }
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
    this.init();
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
    let that = this;
    if (that.data.currentTab === "pending") {
      if (that.data.clickSearch == false) {
        let pageNumber = that.data.pageNumber + 1;
        
        if (pageNumber <= that.data.totalPage) {
          that.setData({
            pageNumber: pageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          wx.request({
            url: app.globalData.URL + '/applyCar/getOutTask',
            data: {
              starttime: that.data.searchInfo.beginTime,
              endtime: that.data.searchInfo.endTime,
              department: that.data.searchInfo.departmentName,
              carinfo: that.data.searchInfo.carName,
              applyman: null,
              nowPage: that.data.pageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync("openId")
            },
            method: "GET",
            success: function (res) {
              that.setData({
                publicOutList: that.data.publicOutList.concat(res.data.data.list)
              });
              that.hideModal();
              wx.hideLoading();
            },
            fail: function (res) {
              console.log("获取下一页出库列表失败");
            }
          });
        }
      } else {
        let pageNumber = that.data.pageNumber + 1;
       
        if (pageNumber <= that.data.totalPage) {
          that.setData({
            pageNumber: pageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          wx.request({
            url: app.globalData.URL + '/applyCar/getOutTask',
            data: {
              starttime: that.data.searchInfo.beginTime,
              endtime: that.data.searchInfo.endTime,
              department: that.data.searchInfo.departmentName,
              carinfo: that.data.searchInfo.carName,
              applyman: that.data.searchInfo.applyMan,
              nowPage: that.data.pageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync("openId")
            },
            method: "GET",
            success: function (res) {
              that.setData({
                publicOutList: that.data.publicOutList.concat(res.data.data.list)
              });
              that.hideModal();
              wx.hideLoading();
            },
            fail: function (res) {
              console.log("获取下一页出库列表失败");
            }
          });
        }
      }
    } else if (that.data.currentTab === "approved") {
      if (that.data.clickSearch == false) {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        that.setData({
          approvedPageNumber: approvedPageNumber
        });
        if (approvedPageNumber <= that.data.approvedTotalPage) {
          wx.showLoading({
            title: "加载中"
          });
          wx.request({
            url: app.globalData.URL + '/applyCar/getInTask',
            data: {
              starttime: that.data.searchInfo.beginTime,
              endtime: that.data.searchInfo.endTime,
              department: that.data.searchInfo.departmentName,
              carinfo: that.data.searchInfo.carName,
              applyman: null,
              nowPage: that.data.approvedPageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync("openId")
            },
            method: "GET",
            success: function (res) {
              that.setData({
                publicInList: that.data.publicInList.concat(res.data.data.list)
              });
              that.hideModal();
              wx.hideLoading();
            },
            fail: function (res) {
              console.log("获取下一页出库列表失败");
            }
          });
        }
      } else {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        that.setData({
          approvedPageNumber: approvedPageNumber
        });
        if (approvedPageNumber <= that.data.approvedTotalPage) {
          wx.showLoading({
            title: "加载中"
          });
          that.data.searchInfo.nowPage = that.data.approvedPageNumber;
          that.data.searchInfo.pageSize = that.data.pageSize;
          wx.request({
            url: app.globalData.URL + '/applyCar/getInTask',
            data: {
              starttime: that.data.searchInfo.beginTime,
              endtime: that.data.searchInfo.endTime,
              department: that.data.searchInfo.departmentName,
              carinfo: that.data.searchInfo.carName,
              applyman: that.data.searchInfo.applyMan,
              nowPage: that.data.approvedPageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync("openId")
            },
            method: "GET",
            success: function (res) {
              that.setData({
                publicInList: that.data.publicInList.concat(res.data.data.list)
              });
              that.hideModal();
              wx.hideLoading();
            },
            fail: function (res) {
              console.log("获取下一页出库列表失败");
            }
          });
        }
      }
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})