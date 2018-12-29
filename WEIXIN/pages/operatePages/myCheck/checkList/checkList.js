const app = getApp();
const util = require("../../../../utils/util");

// 待审批的 URL 
const defaultApprovingURL = app.globalData.URL + '/main/mainApprove.action';
// 我已经审批的URL
const defaultApprovedURL = app.globalData.URL + '/main/mainApproved.action'
// 搜索我已经审批的URL
const searchApprovedURL = app.globalData.URL + "/main/mainapplyedSearch";
// 搜索待我审批的URL
const searchApprovingURL = app.globalData.URL + "/main/mainApproveScreen";
// 部门列表
const getDepartmentListURL = app.globalData.URL + "/user/departmentopenId";

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
    pending_noData: false,
    approved_noData: false,
    // 默认用户头像
    iconSeal: app.globalData.URL + '/images/WeiXin/用印.png',
    iconVacation: app.globalData.URL + '/images/WeiXin/休假.png',
    iconPrivate: app.globalData.URL + '/images/WeiXin/私车.png',
    iconEntertain: app.globalData.URL + '/images/WeiXin/招待.png',
    iconPublic: app.globalData.URL + '/images/WeiXin/公车.png',
    iconTravel: app.globalData.URL + '/images/WeiXin/差旅.png',
    iconFinacePrivate: app.globalData.URL + '/images/WeiXin/私车.png',
    iconFinaceEntertain: app.globalData.URL + '/images/WeiXin/招待.png',
    pendingNumber: 0,
    approvedNumber: 0,
    // 默认显示第几个菜单导航
    currentTab: 'pending',
    // 每一页显示多少条数据
    pageSize: 5,
    // 待审批列表
    pendingList: [],
    // 已审批列表
    approvedList: [],
    // 待我审批的总页数和当前页数
    totalPage: 100,
    pageNumber: 1,
    // 已审批的当前页和总页数
    approvedPageNumber: 1,
    approvedTotalPage: 100,
    showModal: false,
    clickSearch: false

  },

  // 切换顶部tab导航
  switchNav(event) {
    let status = event.currentTarget.dataset.status;
    // if (status == "pending") {
    //   this.setData({
    //     pageNumber: 1,
    //     pendingList: []
    //   });
    // } else if (status == "approved") {
    //   this.setData({
    //     approvedPageNumber: 1,
    //     approvedList: []
    //   });
    // }
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
    searchInfo.type = "全部";
    searchInfo.typeList = ["全部", "年假管理", "用印申请", "公车申请", "私车公用", "差旅费", "业务招待"];
    searchInfo.applyMan = "";
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
      openId: wx.getStorageSync("openId"),
      nowPage: 1,
      pageSize: that.data.pageSize,
      beginTime: searchInfo.beginTime,
      endTime: searchInfo.endTime,
      departmentId: searchInfo.departmentName,
      type: searchInfo.type,
    };
    if (searchInfo.applyMan != null && searchInfo.applyMan != "") {
      data.applyMan = searchInfo.applyMan;
    }
    that.setData({
      searchData: data
    });
    if (currentTab == "pending") {
      wx.request({
        url: searchApprovingURL,
        data: data,
        method: "GET",
        success: function (res) {
          util.showNetworkError(res, that);
          that.setData({
            pending_noData: res.data.data.list.length === 0,
            pendingList: res.data.data.list || [],
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
            pendingNumber: res.data.data.pagesize,
            pageNumber: 1,
            clickSearch: true
          });
          wx.hideLoading();
        },
        fail: function (res) {
          console.log("XHR失败");
        }
      });
      util.showLoading("正在查询");
    } else if (currentTab == "approved") {
      wx.request({
        url: searchApprovedURL,
        data: data,
        method: "GET",
        success: function (res) {
          util.showNetworkError(res, that);
          that.setData({
            approved_noData: res.data.data.list.length === 0,
            approvedList: res.data.data.list || [],
            approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
            approvedNumber: res.data.data.pagesize,
            approvedPageNumber: 1,
            clickSearch: true
          });
          wx.hideLoading();
        },
        fail: function (res) {
          console.log("XHR失败");
        }
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

  toPendingDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.pendingList[id];
    switch (item.title) {
      case '业务招待':
        util.goTo(item.url + "?showButton=" + item.showButton + "&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?showButton=1&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?showButton=1&item=" + JSON.stringify(item));
        break;
      case '财务审批私车':
        util.goTo("/pages/operatePages/myCheck/checkItem/finance/sublist/sublist?item=" + JSON.stringify(item));
        break;
      case '公车申请':
        // 当前状态为3-出库
        if (item.status === 3) {
          util.goTo("../publicOut/publicOut?showButton=1&item=" + JSON.stringify(item) + '&needTaskId=0');
          break;
        }
        // 当前状态为4-入库
        else if (item.status === 4) {
          util.goTo("../publicIn/publicIn?showButton=1&item=" + JSON.stringify(item) + '&needTaskId=0');
          break;
        } {
          util.goTo("../checkItem/publicItem/publicItem?showButton=1&item=" + JSON.stringify(item) + '&needTaskId=0');
          break;
        }
      case '用印申请':
        util.goTo("../checkItem/sealItem/sealItem?showButton=1&item=" + JSON.stringify(item) + '&needTaskId=0&isLeaderCheck=1');
        break;
      case '年假管理':
        util.goTo("../checkItem/vacationItem/vacationItem?showToast=1&showButton=1&item=" + JSON.stringify(item) + '&needTaskId=0');
        break;
    }
  },

  toApprovedDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.approvedList[id];
    switch (item.title) {
      case '业务招待':
        util.goTo("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem?showButton=0&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?showButton=0&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?showButton=0&item=" + JSON.stringify(item));
        break;
      case '公车申请':
        util.goTo("/pages/operatePages/myCheck/checkItem/publicItem/publicItem?showButton=0&item=" + JSON.stringify(item) + '&needTaskId=0');
        break;
      case '用印申请':
        util.goTo("/pages/operatePages/myCheck/checkItem/sealItem/sealItem?showButton=0&item=" + JSON.stringify(item) + '&needTaskId=0=0&isLeaderCheck=1');
        break;
      case '年假管理':
        util.goTo("/pages/operatePages/myCheck/checkItem/vacationItem/vacationItem?showButton=0&item=" + JSON.stringify(item) + '&needTaskId=0');
        break;
    }
  },

  // 一键同意 方便测试用 前端页面不显示这个东西
  agreeAll: function () {
    let that = this;
    let pendingList = this.data.pendingList;

    let entertainApproveURL = getApp().globalData.URL + '/entertain/approveEntetain';
    let privateApproveURL = getApp().globalData.URL + "/privateCar/approvalPrivateCar";
    let vacationApproveURL = getApp().globalData.URL + '/leavel/leaverApprove';
    let travelApproveURL = getApp().globalData.URL + "/applyExpenses/approveApplyExpense";
    let sealApproveURL = "";
    let publicApproveURL = "";
    for (let i = 0; i < pendingList.length; i++) {
      let element = pendingList[i];
      if (true) {
        if (element.title == "私车公用") {
          util.request(privateApproveURL, {
            applyId: element.applyId,
            taskId: element.taskId,
            result: "true",
            comment: ""
          }, function (res) {
            util.showNetworkError(res, that);
            if (res.statusCode == 200) {
              console.log("第" + i + "条数据已经审批通过");
            }
          });
        } else if (element.title == "差旅费") {
          util.request(travelApproveURL, {
            id: element.id,
            taskId: element.taskId,
            result: "true",
            comment: ""
          }, function (res) {
            util.showNetworkError(res, that);
            if (res.statusCode == 200) {
              console.log("第" + i + "条数据已经审批通过");
            }
          });
        } else if (element.title == "业务招待") {
          util.request(entertainApproveURL, {
            id: element.id,
            taskId: element.taskId,
            result: "true",
            comment: ""
          }, function (res) {
            util.showNetworkError(res, that);
            if (res.statusCode == 200) {
              console.log("第" + i + "条数据已经审批通过");
            }
          });

        } else if (element.title == "年假管理") {
          util.request(vacationApproveURL, {
            id: element.id,
            taskId: element.taskId,
            result: 1
          }, function (res) {
            util.showNetworkError(res, that);
            if (res.statusCode == 200) {
              console.log("第" + i + "条数据已经审批通过");
            }
          });
        } else if (element.title == "用印申请") {
          console.log("第" + i + "条" + "用印暂未审批");
          // util.request(sealApproveURL, {
          //   taskId: element.taskId,
          //   status: wx.getStorageSync('username') + '通过',
          //   result: true
          // }, function (res) {
          //   util.showNetworkError(res, that);
          //   if (res.statusCode == 200) {
          //     console.log("第" + i + "条数据已经审批通过");
          //   }
          // });
        } else if (element.title.indexOf("公车") > -1) {
          console.log("第" + i + "条" + "公车暂未审批");
          // util.request(publicApproveURL, {
          //   id: element.id,
          //   taskId: element.taskId,
          //   result: 1
          // }, function (res) {
          //   util.showNetworkError(res, that);
          //   if (res.statusCode == 200) {
          //     console.log("第" + i + "条数据已经审批通过");
          //   }
          // });
        }
      }

    }

  },

  init: function () {
    let that = this;
    if (that.data.pendingList == undefined || that.data.pendingList.length == 0) {
      util.showLoading("正在加载");
    }

    this.setData({
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    });

    // 待我审批的-URL
    wx.request({
      url: defaultApprovingURL,
      data: {
        nowPage: 1,
        pageSize: that.data.pageSize,
        openId: wx.getStorageSync('openId')
      },
      method: "GET",
      success: function (res) {
        that.setData({
          pending_noData: res.data.data.list.length === 0,
          pendingList: res.data.data.list || [],
          totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          pendingNumber: res.data.data.pagesize
        });
        wx.hideLoading();
      },
      fail: function (res) {
        console.log("获取待审批列表--fail");
      }
    });

    // 我已审批的-URL
    wx.request({
      url: defaultApprovedURL,
      data: {
        nowPage: 1,
        pageSize: that.data.pageSize,
        openId: wx.getStorageSync('openId')
      },
      method: "GET",
      success: function (res) {
        that.setData({
          approved_noData: res.data.data.list.length === 0,
          approvedList: res.data.data.list || [],
          approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          approvedNumber: res.data.data.pagesize,
        });
      },
      fail: function (res) {
        console.log("获取已审批列表--fail");
        util.showError(that, '网络错误,请稍后重试');
      }
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
      // departmentName: "全部部门",
      type: "全部",
      typeList: ["全部", "年假管理", "用印申请", "公车申请", "私车公用", "差旅费", "业务招待"]
    };
    that.setData({
      searchInfo: searchInfo
    });

    // 获取部门列表
    wx.request({
      url: getDepartmentListURL,
      data: {
        openId: wx.getStorageSync("openId")
      },
      method: "GET",
      success: function (res) {
        let list = res.data.data || [];
        let departmentNameList = [];
        list.forEach(element => {
          departmentNameList.push(element.departmentname);
        });
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
    console.log("checklist触发了上拉刷新事件");
    let that = this;
    let currentTab = that.data.currentTab;
    if (currentTab == "pending") {
      if (that.data.clickSearch == false) {
        let pageNumber = that.data.pageNumber + 1;
        // that.setData({
        //   pageNumber: pageNumber
        // });
       
        if (pageNumber <= that.data.totalPage) {
          that.setData({
            pageNumber: pageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          wx.request({
            url: defaultApprovingURL,
            data: {
              nowPage: that.data.pageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync('openId')
            },
            method: "GET",
            success: function (res) {
              wx.hideLoading();
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              });
            },
            fail: function (res) {
              wx.hideLoading();
            }
          });
        }
      } else {
        let pageNumber = that.data.pageNumber + 1;
        // that.setData({
        //   pageNumber: pageNumber
        // });
       
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

          wx.request({
            url: searchApprovingURL,
            data: data,
            method: "GET",
            success: function (res) {
              wx.hideLoading();
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              });
            },
            fail: function (res) {
              wx.hideLoading();
            }
          });
        }
      }
    } else if (currentTab == "approved") {
      if (that.data.clickSearch == false) {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        let approvedTotalPage = that.data.approvedTotalPage;
        // that.setData({
        //   approvedPageNumber: approvedPageNumber
        // });
        
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
          wx.request({
            url: defaultApprovedURL,
            data: data,
            method: "GET",
            success: function (res) {
              wx.hideLoading();
              that.setData({
                approvedList: that.data.approvedList.concat(res.data.data.list)
              });
            },
            fail: function (res) {
              wx.hideLoading();
            }
          });
        }
      } else {
        let approvedPageNumber = that.data.approvedPageNumber + 1;
        let approvedTotalPage = that.data.approvedTotalPage;
        // that.setData({
        //   approvedPageNumber: approvedPageNumber
        // });
        
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
          wx.request({
            url: searchApprovedURL,
            data: data,
            method: "GET",
            success: function (res) {
              wx.hideLoading();
              that.setData({
                approvedList: that.data.approvedList.concat(res.data.data.list)
              });
            },
            fail: function (res) {
              wx.hideLoading();
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