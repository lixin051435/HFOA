// pages/operatePages/searchDetail/seal/seal.js
const app = getApp();
const root = getApp().globalData.URL;
const util = require("../../../../utils/util");
const defaultURL = root + "/leavel/searLeaver";
const searchURL = root + "/leavel/searLeaver";


Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 每一页显示多少条数据
    pageSize: 5,
    // 数据列表
    list: [],
    pc: 0,
    days: 0,
    // 总页数
    totalPage: 1,
    // 当前页
    pageNumber: 1,
    // 没有数据时候的标识
    noData: true,
    showModal: false,
    applyMan: wx.getStorageSync("username"),
    clickSearch: false
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
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync("openId"),
      beginTime: searchInfo.beginTime,
      endTime: searchInfo.endTime
    };
    if (searchInfo.applyMan != null && searchInfo.applyMan != "") {
      data.applyMan = searchInfo.applyMan;
    }else{
      data.applyMan = "1";
    }
    if (searchInfo.departmentName != null && searchInfo.departmentName != "全部部门") {
      data.department = searchInfo.departmentName;
    }
    that.setData({
      searchData: data
    });
    util.request(searchURL, data, function (res) {
      that.setData({
        list: res.data.data.list || [],
        noData: res.data.data.length == 0,
        totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        clickSearch: true,
        pageNumber: 1
      });
      wx.hideLoading();
    });
    this.hideModal();
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

  toDetail: function (e) {
    let id = e.currentTarget.dataset.id;
    util.goTo("/pages/operatePages/myCheck/checkItem/vacationItem/vacationItem?item=" + JSON.stringify(this.data.list[id]) + '&showButton=false');
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    if (that.data.list == undefined || that.data.list.length == 0) {
      util.showLoading("正在加载");
    }
    let searchInfo = {
      beginTime: util.getBeginAndEndDate().beginTime,
      endTime: util.getBeginAndEndDate().endTime,
      // department: "全部",
      departmentName: '全部',
      search_department: '',
      applyMan: "",
      openId: wx.getStorageSync("openId")
    };
    that.setData({
      searchInfo: searchInfo
    });

    // 获取部门列表
    util.getDepartmentList(function (data) {
      let searchInfo = that.data.searchInfo;
      searchInfo.departmentNameList = data.departmentNameList;
      searchInfo.departmentObjectList = data.departmentObjectList;
      searchInfo.departmentName = data.departmentNameList[0];
      that.setData({
        searchInfo: searchInfo
      });
    });

    // 获取个人信息 是否是普通员工,真实姓名
    util.getInfo(function (data, realname) {
      let searchInfo = that.data.searchInfo;
      searchInfo.applyMan = realname;
      searchInfo.IamAverage = data;
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

    // 获取今年该用户总览信息
    wx.request({
      url: app.globalData.URL + '/leavel/countLeaver',
      data: {
        openId: wx.getStorageSync("openId")
      },
      method: 'GET',
      dataType: 'json',
      success: res => {
        if (res.data.data === null) {
          that.setData({
            pc: 0,
            days: 0
          })
        } else {
          that.setData({
            pc: res.data.data[0].toatl,
            days: parseInt(res.data.data[0].days) || 0
          })
        }
      },
      fail: () => {
        util.showError(that, '获取今年该用户总览信息失败');
      }
    });

    // 获取所有列表
    util.request(defaultURL, {
      nowPage: 1,
      pageSize: that.data.pageSize,
      openId: wx.getStorageSync("openId")
    }, function (res) {
      let list = res.data.data.list;
      that.setData({
        totalPage: Math.ceil(parseInt(res.data.data.pagesize) / parseInt(that.data.pageSize)),
        list: list,
        noData: list.length == 0
      });
      wx.hideLoading();
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
    
    // that.setData({
    //   pageNumber: 1
    // });
    
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
        wutil.request(defaultURL, {
          nowPage: that.data.pageNumber,
          pageSize: that.data.pageSize,
          openId: wx.getStorageSync('openId')
        }, function (res) {
          wx.hideLoading();
          that.setData({
            list: that.data.list.concat(res.data.data.list)
          });
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
        let data = that.data.searchData;
        data.nowPage = that.data.pageNumber;
        data.pageSize = that.data.pageSize;
        data.openId = wx.getStorageSync('openId');
        util.request(searchURL, data, function (res) {
          wx.hideLoading();
          that.setData({
            list: that.data.list.concat(res.data.data.list)
          });
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