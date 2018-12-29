const app = getApp();
const util = require("../../../utils/util.js");

// 搜索待我审批的URL 带分页
const searchApprovingURL = app.globalData.URL + "/main/thirdMainapproveSearch";
// 搜索我已审批的URL 带分页
const searchApprovedURL = app.globalData.URL + "/main/thirdMainapprovedSerach";

// 部门列表  没有全部
const getDepartmentListURL = app.globalData.URL + "/department/getAllDepartment";

// 默认待我审批 URL
const defaultApprovingURL = app.globalData.URL + '/main/thirdMainapprove';
// 默认我已经审批 URL
const defaultApprovedURL = app.globalData.URL + '/main/thirdMainapproved';

/**
 * 
 * @param {string} departmentName 部门名称
 * @param {list} departmentObjectList 部门对象列表
 * @return 返回departmentid 
 */
function getDepartmentId(departmentName, departmentObjectList) {
  for (let i = 0; i < departmentObjectList.length; i++) {
    if (departmentObjectList[i].departmentname == departmentName) {
      return departmentObjectList[i].id;
    }
  }
  return -1;
}

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 默认用户头像
    iconSeal: app.globalData.URL + '/images/WeiXin/用印.png',
    iconVacation: app.globalData.URL + '/images/WeiXin/年假.png',
    iconPrivate: app.globalData.URL + '/images/WeiXin/私车.png',
    iconEntertain: app.globalData.URL + '/images/WeiXin/招待.png',
    iconPublic: app.globalData.URL + '/images/WeiXin/公车.png',
    iconTravel: app.globalData.URL + '/images/WeiXin/差旅.png',
    iconFinacePrivate: app.globalData.URL + '/images/WeiXin/私车.png',
    iconFinaceEntertain: app.globalData.URL + '/images/WeiXin/招待.png',
    // 待审批和已审批条数
    pendingNumber: 0,
    approvedNumber: 0,

    // 默认显示第几个菜单导航
    currentTab: 'pending',

    // 每一页显示多少条数据
    pageSize: 5,

    // 待审批和已审批的ArrayList
    pendingList: [],
    approvedList: [],

    // 待审批的总页数和当前页码
    totalPage: 100,
    pageNumber: 1,

    // 已审批的总页数和当前页码
    approvedPageNumber: 1,
    approvedTotalPage: 100,

    pending_noData: false,
    approved_noData: false,
    showModal: false
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

  // 隐藏模态对话框
  hideModal: function () {
    this.setData({
      showModal: false
    });
  },
  showModal: function () {
    this.setData({
      showModal: true
    });
  },

  // 对话框取消按钮点击事件
  cancel: function () {
    this.hideModal();
  },

  checkData: function (data) {

  },
  // 点击查询按钮
  search: function () {
    let that = this;
    let searchInfo = that.data.searchInfo;
    let data = {
      openId: wx.getStorageSync("openId"),
      nowPage: 1,
      pageSize: that.data.pageSize,
      beginTime: searchInfo.beginTime,
      endTime: searchInfo.endTime,
      departmentId: searchInfo.departmentName,
      type: searchInfo.type,
    };
    if (data.type == "私车公用") {
      data.type = "财务审批私车";
    }
    if (searchInfo.applyMan != null && searchInfo.applyMan != "") {
      data.applyMan = searchInfo.applyMan;
    }
    that.setData({
      searchData: data
    });
    if (that.data.currentTab == "pending") {
      util.request(searchApprovingURL, data, function (res) {
        that.setData({
          pending_noData: res.data.data.list.length === 0,
          pendingList: res.data.data.list || [],
          totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          pendingNumber: res.data.data.pagesize,
          pageNumber: 1,
          clickSearch: true
        });
        wx.hideLoading();
        that.hideModal();
      });
      util.showLoading("正在查询");
    } else {
      util.request(searchApprovedURL, data, function (res) {
        that.setData({
          approved_noData: res.data.data.list.length === 0,
          approvedList: res.data.data.list || [],
          approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          approvedNumber: res.data.data.pagesize,
          approvedPageNumber: 1,
          clickSearch: true
        });
        wx.hideLoading();
        that.hideModal();
      });
      util.showLoading("正在查询");
    }


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
    // searchInfo.departmentId = getDepartmentId(name, this.data.searchInfo.departmentObjectList);
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
    let that = this;
    let value = e.detail.value;
    let item = e.currentTarget.dataset.item;
    let searchInfo = this.data.searchInfo;
    switch (item) {
      case "beginTime":
        if (new Date(value).getTime() > new Date(searchInfo.endTime).getTime()) {
          util.showError(that, "结束时间不能早于开始时间");
          return false;
        }
        break;
      case "endTime":
        if (new Date(searchInfo.beginTime).getTime() > new Date(value).getTime()) {
          util.showError(that, "结束时间不能早于开始时间");
          return false;
        }
        break;
    }
    searchInfo[item] = value;
    this.setData({
      searchInfo: searchInfo
    });
  },

  toPendingDetailItem: function (e) {
    // 后台要求，从左边进入 提交canUpdate = 0
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.pendingList[id];
    switch (item.title) {
      case '业务招待':
        util.goTo(item.url + "?financeLeft=1&showButton=1&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?financeLeft=1&showButton=1&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?financeLeft=1&showButton=1&item=" + JSON.stringify(item));
        break;
      case '财务审批私车':
        util.goTo("/pages/operatePages/myCheck/checkItem/finance/sublist/sublist?financeLeft=1&showButton=1&item=" + JSON.stringify(item));
        break;
      case '公车申请':
        // 当前状态为3-出库
        if (item.status === 3) {
          util.goTo("../publicOut/publicOut?showButton=1&item=" + JSON.stringify(item));
          break;
        }
        // 当前状态为4-入库
        else if (item.status === 4) {
          util.goTo("../publicIn/publicIn?showButton=1&item=" + JSON.stringify(item));
          break;
        } {
          util.goTo("../checkItem/publicItem/publicItem?showButton=1&item=" + JSON.stringify(item));
          break;
        }
      case '用印申请':
        util.goTo("../checkItem/sealItem/sealItem?showButton=1&item=" + JSON.stringify(item));
        break;
      case '年假管理':
        util.goTo("../checkItem/vacationItem/vacationItem?showButton=1&item=" + JSON.stringify(item));
        break;
    }
  },

  toApprovedDetailItem: function (e) {
    // 后台要求，从左边进入 提交canUpdate = 1
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.approvedList[id];
    switch (item.title) {
      case '业务招待':
        util.goTo("/pages/operatePages/myCheck/checkItem/finance/entertainItem/entertainItem?financeLeft=0&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?financeLeft=0&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?financeLeft=0&item=" + JSON.stringify(item));
        break;
      case '财务审批私车':
        util.goTo("/pages/operatePages/myCheck/checkItem/finance/sublist/sublist?financeLeft=0&item=" + JSON.stringify(item));
        break;

    }
  },

  init: function () {
    let that = this;
    if (that.data.pendingList == undefined || that.data.pendingList.length == 0) {
      util.showLoading("正在加载");
    }
    that.setData({
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    });

    // 待我审批的-URL
    util.request(defaultApprovingURL, {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        pendingList: res.data.data.list || [],
        pending_noData: res.data.data.list.length == 0,
        totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        pendingNumber: res.data.data.pagesize
      });
      wx.hideLoading();
    });

    // 我已审批的-URL
    util.request(defaultApprovedURL, {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        approvedList: res.data.data.list || [],
        approved_noData: res.data.data.list.length == 0,
        approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        approvedNumber: res.data.data.pagesize
      });
    });

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let searchInfo = {
      beginTime: util.getBeginAndEndDate().beginTime,
      endTime: util.getBeginAndEndDate().endTime,
      departmentName: "全部部门",
      type: "私车公用",
      typeList: ["私车公用", "业务招待"]
    };
    that.setData({
      searchInfo: searchInfo,
    });

    // 获取部门列表
    wx.request({
      url: getDepartmentListURL,
      method: "GET",
      success: function (res) {
        let list = res.data || [];
        let departmentNameList = [];
        list.forEach(element => {
          departmentNameList.push(element.departmentname);
        });
        departmentNameList.unshift("全部部门");
        let searchInfo = that.data.searchInfo;
        searchInfo.departmentNameList = departmentNameList;
        searchInfo.departmentObjectList = list;
        searchInfo.departmentName = departmentNameList[0];
        that.setData({
          searchInfo: searchInfo
        });
      },
      fail: function (res) {
        console.log("获取已审批列表--fail");
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

    let currentTab = that.data.currentTab;
    if (currentTab == "pending") {
      if (that.data.clickSearch == true) {
        let pageNumber = that.data.pageNumber + 1;

        if (pageNumber <= that.data.totalPage) {
          that.setData({
            pageNumber: pageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          let data = that.data.searchData;
          data.nowPage = that.data.pageNumber;
          data.pageSize = that.data.pageSize;
          data.openId = wx.getStorageSync('openId');

          util.request(searchApprovingURL, data, function (res) {
            wx.hideLoading();
            that.setData({
              pendingList: that.data.pendingList.concat(res.data.data.list)
            });
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

          util.request(defaultApprovingURL, {
            nowPage: that.data.pageNumber,
            pageSize: that.data.pageSize,
            openId: wx.getStorageSync('openId')
          }, function (res) {
            wx.hideLoading();
            that.setData({
              pendingList: that.data.pendingList.concat(res.data.data.list)
            });
          });
        }
      }

    } else if (currentTab == "approved") {
      if (that.data.clickSearch == true) {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        let approvedTotalPage = that.data.approvedTotalPage;

        if (approvedPageNumber <= approvedTotalPage) {
          that.setData({
            approvedPageNumber: approvedPageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          let data = that.data.searchData;
          data.nowPage = that.data.approvedPageNumber;
          data.pageSize = that.data.pageSize;
          data.openId = wx.getStorageSync('openId');

          util.request(searchApprovedURL, data, function (res) {
            wx.hideLoading();
            that.setData({
              approvedList: that.data.approvedList.concat(res.data.data.list)
            });
          });
        }
      } else {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        let approvedTotalPage = that.data.approvedTotalPage;

        if (approvedPageNumber <= approvedTotalPage) {
          that.setData({
            approvedPageNumber: approvedPageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          let data = {};
          data.nowPage = that.data.approvedPageNumber;
          data.pageSize = that.data.pageSize;
          data.openId = wx.getStorageSync('openId');

          util.request(defaultApprovedURL, data, function (res) {
            wx.hideLoading();
            that.setData({
              approvedList: that.data.approvedList.concat(res.data.data.list)
            });
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