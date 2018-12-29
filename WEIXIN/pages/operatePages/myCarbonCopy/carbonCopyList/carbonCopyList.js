const app = getApp();
const util = require("../../../../utils/util");

// 默认 未查看的URL
const defaultPendingURL = app.globalData.URL + '/applyExpenses/getcCListApplyExpense';
// 默认 已查看的URL
const defaultApprovedURL = app.globalData.URL + '/applyExpenses/getcClistApplyExpenseState';
// 搜索未查看的URL
const searchPendingURL = app.globalData.URL + '/applyExpenses/getcClistApplyExpensesearch';
// 搜索已查看的URL
const searchApprovedURL = app.globalData.URL + '/applyExpenses/getcClistApplyExpensesearch';

// 点击查看URL
const clickURL = app.globalData.URL + '/applyExpenses/ifState';

Page({

  data: {
    icon: app.globalData.URL + '/images/WeiXin/差旅.png',
    // 默认显示第几个菜单导航
    currentTab: 'pending',

    // 每一页显示多少条数据
    pageSize: 5,

    // 待审批和已审批条数
    pendingNumber: 0,
    approvedNumber: 0,

    // 待审批和已审批的ArrayList
    pendingList: [],
    approvedList: [],

    // 待审批的总页数和当前页码
    totalPage: 100,
    pageNumber: 1,

    // 已审批的总页数和当前页码
    approvedPageNumber: 1,
    approvedTotalPage: 100,

    pending_noData: true,
    approved_noData: true,
    showModal: false,
    clickSearch: false
  },
  // 切换顶部tab导航
  switchNav(event) {
    let status = event.currentTarget.dataset.status;
    if (this.data.currentTab === status) {
      return false;
    } else {
      this.setData({
        currentTab: status
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
    // searchInfo.departmentName = "全部部门";
    searchInfo.type = "差旅费";
    searchInfo.typeList = ["差旅费"];
    that.setData({
      searchInfo: searchInfo,
      showModal: false
    });
  },

  // 对话框取消按钮点击事件
  cancel: function () {
    this.hideModal();
  },

  // 点击查询按钮-待我审批的
  search: function () {
    let currentTab = this.data.currentTab;
    let that = this;
    let searchInfo = that.data.searchInfo;
    let data = {
      cCListOpenId: wx.getStorageSync("openId"),
      nowPage: 1,
      pageSize: that.data.pageSize,
      beginTime: searchInfo.beginTime,
      endTime: searchInfo.endTime
      // type: searchInfo.type,
    };
    if (searchInfo.applyMan != null && searchInfo.applyMan != "") {
      data.applyMan = searchInfo.applyMan;
    }
    that.setData({
      searchData: data
    });
    if (currentTab == "pending") {
      data.state = "0";
      util.request(searchPendingURL, data, function (res) {
        that.setData({
          pending_noData: res.data.data.list.length === 0,
          pendingList: res.data.data.list || [],
          totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          pendingNumber: res.data.data.pagesize,
          pageNumber: 1,
          clickSearch: true,
        });
        wx.hideLoading();
      });
      util.showLoading("正在查询");
    } else {
      data.state = "1";
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
      });
      util.showLoading("正在查询");
    }
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

  init: function () {
    let that = this;

    let searchInfo = {
      beginTime: util.getBeginAndEndDate().beginTime,
      endTime: util.getBeginAndEndDate().endTime,
      departmentName: "全部部门",
      type: "差旅费",
      // typeList: ["年假管理", "用印申请", "公车申请", "私车公用", "差旅费", "业务招待"],
      typeList: ["差旅费"],
      IamAverage: false,
      cannotEdit: false
    };
    that.setData({
      searchInfo: searchInfo,
      clickSearch: false,
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.init();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    let that = this;
    if (that.data.pendingList == undefined || that.data.pendingList.length == 0) {
      util.showLoading("正在加载");
    }

    // 加载未查看的
    util.request(defaultPendingURL, {
      nowPage: 1,
      pageSize: that.data.pageSize,
      cCListOpenId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        pending_noData: res.data.data.list.length == 0,
        pendingList: res.data.data.list,
        totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        pendingNumber: res.data.data.pagesize
      });
      wx.hideLoading();
    });

    // 加载已查看的
    util.request(defaultApprovedURL, {
      nowPage: 1,
      pageSize: that.data.pageSize,
      cCListOpenId: wx.getStorageSync('openId')
    }, function (res) {
      that.setData({
        approved_noData: res.data.data.list.length == 0,
        approvedList: res.data.data.list,
        approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        approvedNumber: res.data.data.pagesize
      });
    });

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
    if (that.data.currentTab == "pending") {
      if (that.data.clickSearch == false) {
        let pageNumber = that.data.pageNumber + 1;

        if (pageNumber <= that.data.totalPage) {
          that.setData({
            pageNumber: pageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          util.request(defaultPendingURL, {
            nowPage: that.data.pageNumber,
            pageSize: that.data.pageSize,
            cCListOpenId: wx.getStorageSync('openId')
          }, function (res) {
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
          let data = that.data.searchData;
          data.nowPage = that.data.pageNumber;
          data.pageSize = that.data.pageSize;
          data.cCListOpenId = wx.getStorageSync('openId');
          util.request(searchPendingURL, data, function (res) {
            wx.hideLoading();
            that.setData({
              pendingList: that.data.pendingList.concat(res.data.data.list)
            });
          });
        }
      }
    } else {
      if (that.data.clickSearch == false) {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        if (approvedPageNumber <= that.data.approvedTotalPage) {
          that.setData({
            approvedPageNumber: approvedPageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          util.request(defaultApprovedURL, {
            nowPage: that.data.approvedPageNumber,
            pageSize: that.data.pageSize,
            cCListOpenId: wx.getStorageSync('openId')
          }, function (res) {
            wx.hideLoading();
            that.setData({
              approvedList: that.data.approvedList.concat(res.data.data.list)
            });
          });
        }
      } else {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        if (approvedPageNumber <= that.data.approvedTotalPage) {
          that.setData({
            approvedPageNumber: approvedPageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          let data = that.data.searchData;
          data.nowPage = that.data.approvedPageNumber;
          data.pageSize = that.data.pageSize;
          data.cCListOpenId = wx.getStorageSync('openId');
          util.request(searchApprovedURL, data, function (res) {
            wx.hideLoading();
            that.setData({
              approvedList: that.data.approvedList.concat(res.data.data.list)
            });
          });
        }
      }
    }
  },

  toPendingDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.pendingList[id];
    util.request(clickURL, {
      id: item.id
    }, function (res) {
      util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?item=" + JSON.stringify(item));
    });
    // switch (item.title) {
    //   case '业务招待':
    //     util.goTo("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    //   case '差旅费':
    //     util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    //   case '私车公用':
    //     util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    //   case 3:
    //     util.goTo("/pages/operatePages/myCheck/checkItem/publicItem/publicItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    //   case 4:
    //     util.goTo("/pages/operatePages/myCheck/checkItem/sealItem/sealItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    //   case 5:
    //     util.goTo("/pages/operatePages/myCheck/checkItem/vacationItem/vacationItem?showButton=0&item=" + JSON.stringify(item));
    //     break;
    // }
  },

  toApprovedDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.approvedList[id];
    util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?item=" + JSON.stringify(item));
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})