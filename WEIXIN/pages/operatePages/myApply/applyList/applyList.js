const app = getApp();
const util = require("../../../../utils/util");

// 未完成的
const defaultPendingURL = app.globalData.URL + '/main/mainApply';
// 审批中的
const defaultApprovedURL = app.globalData.URL + '/main/mainRuntimeHistory';
//  搜索用
const searchPendingURL = app.globalData.URL + '/main/mainApplyScreen';
//  搜索用
const searchApprovedURL = app.globalData.URL + '/main/mainRuntimeHistorySearch';
// 凭票报销URL
const reimburseURL = app.globalData.URL + "/privateCar/reimbursementPrivateCarApprove";
// 部门列表
const getDepartmentListURL = app.globalData.URL + "/user/departmentopenId";

Page({

  data: {
    pending_noData: false,
    approved_noData: false,
    // 默认显示第几个菜单导航
    currentTab: 'pending',
    showFinance: false,
    // 每一页显示多少条数据
    pageSize: 5,
    // 总页数
    totalPage: 100,
    approvedTotalPage: 100,
    // 当前页
    pageNumber: 1,
    approvedPageNumber: 1,
    // 有多少个待处理
    pendingNumber: 0,
    pendingList: [],
    // 有多少个审批中
    approvedNumber: 0,
    approvedList: [],
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
        currentTab: status
      })
    }
  },

  // 初始化
  init: function () {
    let that = this;
    if (that.data.pendingList == undefined || that.data.pendingList.length == 0) {
      util.showLoading("正在加载");
    }
    // finance_privateMap key存储的是下标 选中的下标 value 是选中的对象
    let finance_privateMap = new Map();
    that.setData({
      finance_privateMap: finance_privateMap,
      showFinance: finance_privateMap.size > 0 ? true : false,
      sum: "",
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    })

    // 待处理
    wx.request({
      url: defaultPendingURL,
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
          pendingNumber: res.data.data.pagesize,
        });
        let list = res.data.data.list || [];
        for (let i = 0; i < list.length; i++) {
          list[i].checked = false;
        }
        that.setData({
          pendingList: list
        });
        wx.hideLoading();
      },
      fail: function (res) {
        console.log("XHR失败");
      }
    });

    // 审批中
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
          approvedNumber: res.data.data.pagesize,
          approvedTotalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
        });
      },
      fail: function (res) {
        console.log("XHR失败");
      }
    });
  },

  // 待处理-跳转
  toPendingDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let item = that.data.pendingList[id];

    switch (item.title) {
      case '业务招待':
        util.goTo(item.url + "?canRevoke=1&canConfirm=" + item.canConfirm + "&canReimburse=" + item.canReimburse + "&canUpdate=" + item.canUpdate + "&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        console.log(item);
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?canRevoke=1&canConfirm=" + item.canConfirm + "&canUpdate=1&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo(item.url + "?canRevoke=1&canConfirm=" + item.canConfirm + "&canUpdate=" + item.canUpdate + "&item=" + JSON.stringify(item));
        break;
        // case '私车公用凭票报销':
        //   util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?item=" + JSON.stringify(item));
        //   break;
      case '公车申请':
        util.goTo('../../myCheck/checkItem/publicItem/publicItem' + "?item=" + JSON.stringify(item) + "&showButton=0&canUpdate=1&canRevoke=1&needTaskId=1");
        break;
      case '用印申请':
        util.goTo('../../myCheck/checkItem/sealItem/sealItem' + "?item=" + JSON.stringify(item) + "&showButton=0&canUpdate=1&canRevoke=1&needTaskId=1&isLeaderCheck=0");
        break;
      case '年假管理':
        util.goTo('../../myCheck/checkItem/vacationItem/vacationItem' + "?item=" + JSON.stringify(item) + "&showButton=0&conUpdate=1&conConfirm=1");
        break;
    }
  },

  // 进行中-跳转
  toDetailItem: function (e) {
    let that = this;
    let id = e.currentTarget.id;
    let param = e.currentTarget.dataset.param;
    let item = {};
    if (param == 0) {
      item = that.data.completeList[id];
    } else if (param == 1) {
      item = that.data.approvedList[id];
    }
    switch (item.title) {
      case '业务招待':
        util.goTo("/pages/operatePages/myCheck/checkItem/entertainItem/entertainItem?canUpdate=1&canRevoke=1&notaskid=1&item=" + JSON.stringify(item));
        break;
      case '差旅费':
        util.goTo("/pages/operatePages/myCheck/checkItem/travelItem/travelItem?canUpdate=1&canRevoke=1&notaskid=1&item=" + JSON.stringify(item));
        break;
      case '私车公用':
        util.goTo("/pages/operatePages/myCheck/checkItem/privateItem/privateItem?canUpdate=1&canRevoke=1&notaskid=1&item=" + JSON.stringify(item));
        break;
      case '公车申请':
        util.goTo('../../myCheck/checkItem/publicItem/publicItem' + "?item=" + JSON.stringify(item) + "&showButton=0&canUpdate=1&canRevoke=1&needTaskId=0");
        break;
      case '用印申请':
        util.goTo('../../myCheck/checkItem/sealItem/sealItem' + "?item=" + JSON.stringify(item) + "&showButton=0&canUpdate=1&canRevoke=1&needTaskId=0&isLeaderCheck=0");
        break;
      case '年假管理':
        util.goTo('../../myCheck/checkItem/vacationItem/vacationItem' + "?item=" + JSON.stringify(item) + "&showButton=0&canUpdate=1&canConfirm=0");
        break;
    }
  },

  /**
   * 财务相关函数
   */
  inputedit: function (e) {
    let value = e.detail.value;
    this.setData({
      sum: value
    });
  },

  checkboxChange: function (e) {
    let finance_privateMap = this.data.finance_privateMap;
    let pendingList = this.data.pendingList;
    let indexes = e.detail.value;
    let id = e.currentTarget.dataset.index;
    if (indexes.length > 0) {
      let index = parseInt(indexes[0]);
      finance_privateMap.set(index, pendingList[index]);
      pendingList[id].checked = true;
    } else {
      finance_privateMap.delete(id);
      pendingList[id].checked = false;
    }
    let showFinance = finance_privateMap.size > 0 ? true : false;

    let sum = 0;
    for (let [key, value] of finance_privateMap) {
      sum += value.sureLength;
    }
    let placeholder = "最大金额：" + sum + "元";

    this.setData({
      showFinance: showFinance,
      placeholder: placeholder,
      finance_privateMap: finance_privateMap,
      pendingList: pendingList
    });
  },

  submit: function (e) {
    let that = this;
    let sum = 0;
    let data = {};
    let applyIdList = [];
    let taskIdList = [];
    let finance_privateMap = this.data.finance_privateMap;
    for (let [key, value] of finance_privateMap) {
      sum += value.sureLength;
      data.applyMan = value.applyMan || "";
      applyIdList.push(value.applyId);
      taskIdList.push(value.taskId);
    }
    data.applyIds = applyIdList.join(",");
    data.taskIds = taskIdList.join(",");
    if (util.isEmpty(this.data.sum)) {
      util.showError(that, "请输入凭单金额");
      return false;
    } else if (!util.isNumber(this.data.sum)) {
      util.showError(that, "金额必须是数字");
      return false;
    } else if (util.isNumber(this.data.sum)) {
      if (parseFloat(this.data.sum) > sum) {
        util.showError(that, "超过了最大金额");
        return false;
      } else {
        data.sum = this.data.sum;
      }
    }
    if (that.data.hasCommited == undefined) {
      wx.request({
        url: reimburseURL,
        data: data,
        method: 'GET',
        success: function (res) {
          let resultdata = res.data.msg;
          if (resultdata == "OK") {
            util.goTo("/pages/publicPages/approveback/approveback?delta=1");
          } else {
            console.log("出错了");
          }
        },
        fail: function (res) {
          console.log("XHR出错了");
          let hasCommited = true;
          that.setData({
            hasCommited: hasCommited
          });
        }
      });
      util.showLoading();
    } else {
      util.showError(this, "您已提交，请勿重复提交");
      return false;
    }
  },

  /**
   * 搜索弹窗相关函数
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

    // 把搜索参数存储起来 用来分页用
    this.setData({
      searchData: data
    });

    if (currentTab == "pending") {
      wx.request({
        url: searchPendingURL,
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

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let searchInfo = {
      beginTime: util.getBeginAndEndDate().beginTime,
      endTime: util.getBeginAndEndDate().endTime,
      departmentName: "全部部门",
      type: "全部",
      typeList: ["全部", "年假管理", "用印申请", "公车申请", "私车公用", "差旅费", "业务招待"],
      cannotEdit: true
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
        // console.log(res);
        let list = res.data.data || [];
        let departmentNameList = [];
        list.forEach(element => {
          departmentNameList.push(element.departmentname);
        });
        let searchInfo = that.data.searchInfo;
        searchInfo.departmentNameList = departmentNameList;
        searchInfo.departmentObjectList = list;
        that.setData({
          searchInfo: searchInfo
        });
      },
      fail: function (res) {
        console.log("获取已审批列表--fail");
      }
    });

    wx.request({
      url: getApp().globalData.URL + "/user/toLogin.action",
      method: "GET",
      data: {
        openId: wx.getStorageSync('openId')
      },
      success: function (res) {
        let searchInfo = that.data.searchInfo;
        let departmentName = res.data.data.userEntity.departmentname;
        let realname = res.data.data.userEntity.realname;
        searchInfo.departmentName = departmentName;
        searchInfo.applyMan = realname;
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
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    this.init();
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
    console.log("applylist触发了上拉刷新事件");
    let that = this;
    let currentTab = that.data.currentTab;
    if (currentTab == "pending") {
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
            url: defaultPendingURL,
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
            url: searchPendingURL,
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


        if (approvedPageNumber <= approvedTotalPage) {
          that.setData({
            approvedPageNumber: approvedPageNumber
          });
          wx.showLoading({
            title: "加载中"
          });
          wx.request({
            url: defaultApprovedURL,
            data: {
              nowPage: that.data.approvedPageNumber,
              pageSize: that.data.pageSize,
              openId: wx.getStorageSync('openId')
            },
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