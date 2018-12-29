// pages/operatePages/sealHandle/sealList/sealList.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    icon: app.globalData.URL + '/images/WeiXin/用印.png',
    showMore: false,
    userName: null,
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
    noData: false,
    // 每一页显示多少条数据
    pageSize: 5,
    // 待审批的总页数和当前页码
    totalPage: 100,
    pageNumber: 1,
    // 已审批的总页数和当前页码
    approvedPageNumber: 1,
    approvedTotalPage: 100,
    showModal: false,
    applyMan: wx.getStorageSync("username"),
    clickSearch: false
  },

  toUseSeal(e) {
    let id = e.currentTarget.id;
    let item = this.data.pendingList[id];
    util.goTo("../useSeal/useSeal?showButton=1&canUseSeal=1&item=" + JSON.stringify(item));
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
    this.setData({
      showModal: false
    });
  },

  // 对话框取消按钮点击事件
  cancel: function () {
    this.hideModal();
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
      gzId: searchInfo.gzId,
      starttime: searchInfo.beginTime,
      endtime: searchInfo.endTime,
      applyusername: searchInfo.applyMan || "null"
    };
    that.setData({
      searchData: data
    });
    wx.request({
      url: app.globalData.URL + '/print/getTobetreatedTask',
      data: data,
      method: "GET",
      success: function (res) {
        that.setData({
          noData: res.data.data.list.length === 0,
          pendingList: res.data.data.list || [],
          totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
          pendingOutNumber: res.data.data.pagesize,
          pageNumber: 1,
          clickSearch: true
        });
        that.hideModal();
        wx.hideLoading();
      },
      fail: function (res) {
        console.log("用印信息查询失败");
      }
    });
    util.showLoading("正在查询");
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

  setSealName: function (e) {
    let value = e.detail.value;
    let searchInfo = this.data.searchInfo;
    let seal = this.data.searchInfo.objectList[value];
    searchInfo.sealName = seal.gzkind;
    searchInfo.gzId = seal.id;
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

    // 获取用印类型
    util.getSealTypes(function (data) {
      let searchInfo = that.data.searchInfo;
      searchInfo.objectList = data.objectList;
      searchInfo.nameList = data.nameList;
      searchInfo.nameList.unshift("全部");
      searchInfo.sealName = data.nameList[0] || "请选择用印类型";
      // 用印查询改为传公章的id
      let seal = {
        id:0,
        gzkind:"全部"
      };
      searchInfo.objectList.unshift(seal);
      searchInfo.gzId = 0;
      searchInfo.sealName = "全部";
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
    // 获取数据
    let that = this;
    that.setData({
      pageNumber: 1,
      approvedPageNumber: 1,
      clickSearch: false
    });
    util.showLoading("正在加载");

    wx.request({
      url: app.globalData.URL + '/print/getTobetreatedTask',
      data: {
        nowPage: 1,
        pageSize: that.data.pageSize,
        openId: wx.getStorageSync('openId'),
        department: '全部',
        gzId: 0,
        starttime: null,
        endtime: null,
        applyman: null
      },
      method: "GET",
      success: function (res) {
        if (res.data.data.list.length === 0) {
          that.setData({
            noData: true,
            pendingList: [],
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          })
        } else {
          that.setData({
            noData: false,
            pendingList: res.data.data.list,
            totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize))
          })
        }
        wx.hideLoading();
      },
      fail: function (res) {
        console.log("获取用印列表失败");
        util.showError(that, '获取用印列表失败');
      }
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
    let that = this;
    if (that.data.clickSearch == false) {
      let pageNumber = that.data.pageNumber + 1;
      that.setData({
        pageNumber: pageNumber
      });
      if (pageNumber <= that.data.totalPage) {
        wx.showLoading({
          title: "加载中"
        });
        wx.request({
          url: app.globalData.URL + '/print/getTobetreatedTask',
          data: {
            nowPage: that.data.pageNumber,
            pageSize: that.data.pageSize,
            openId: wx.getStorageSync('openId'),
            department: '全部',
            gzId: 0,
            starttime: null,
            endtime: null,
            applyman: null
          },
          method: "GET",
          success: function (res) {
            if (res.data.data.list.length === 0) {
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              })
            } else {
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              })
            }
            that.hideModal();
            wx.hideLoading();
          },
          fail: function (res) {
            console.log("获取用印列表失败");
            util.showError(that, '获取用印列表失败');
          }
        });
      }
    } else {
      let pageNumber = that.data.pageNumber + 1;
      that.setData({
        pageNumber: pageNumber
      });
      if (pageNumber <= that.data.totalPage) {
        wx.showLoading({
          title: "加载中"
        });
        that.data.searchData.nowPage = that.data.pageNumber;
        that.data.searchData.pageSize = that.data.pageSize;
        wx.request({
          url: app.globalData.URL + '/print/getTobetreatedTask',
          data: that.data.searchData,
          method: "GET",
          success: function (res) {
            if (res.data.data.list.length === 0) {
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              })
            } else {
              that.setData({
                pendingList: that.data.pendingList.concat(res.data.data.list)
              })
            }
            that.hideModal();
            wx.hideLoading();
          },
          fail: function (res) {
            console.log("获取用印列表失败");
            util.showError(that, '获取用印列表失败');
          }
        });
      }
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})