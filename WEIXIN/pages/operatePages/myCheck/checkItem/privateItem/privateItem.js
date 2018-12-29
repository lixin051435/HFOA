const app = getApp();
const util = require("../../../../../utils/util");
const root = getApp().globalData.URL;

// 领导审批URL
const approveURL = getApp().globalData.URL + "/privateCar/approvalPrivateCar";

// 员工确认URL
const confirmURL = getApp().globalData.URL + "/privateCar/staffPrivateCarApprove";

// 财务审批URL
const financeConfirmURL = getApp().globalData.URL + "/privateCar/financePrivateCarApprove";

// 员工撤销URL
const revokeURL = getApp().globalData.URL + "/privateCar/deletePrivateCar";

Page({

  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    icon: app.globalData.URL + '/images/WeiXin/私车.png',
    showModal: false
  },

  confirmDisagree: function (e) {
    let that = this;
    let info = this.data.info;
    // 领导的驳回
    if (this.data.showButton == 1) {
      if (info.hasCommited == undefined) {
        wx.request({
          url: approveURL,
          data: {
            applyId: this.data.info.applyId,
            taskId: this.data.info.taskId,
            result: "false",
            comment: that.data.rejectReason || "无"
          },
          method: 'GET',
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/applyback/applyback");
            else
              console.log("出错了");
          },
          fail: function (res) {
            console.log("出错了");
            let hasCommited = undefined;
            info.hasCommited = hasCommited;
            that.setData({
              info: info
            });
          }
        })
        this.hideModal();
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
        util.showLoading();
      } else {
        util.showError(that, "您已经提交，请勿重复提交");
        return false;
      }
    }

    // 财务的驳回
    if (this.data.canFinance == 1) {
      let data = {};
      let info = this.data.info;

      data.taskId = info.taskId;
      data.applyId = info.applyId;
      data.finaceresult = "false";
      data.applyIdinvoice = info.applyIdinvoice || "";
      data.voucherNum = info.voucherNum || "";
      data.paidTime = info.paidTime;
      data.comment = that.data.rejectReason || "无";

      let flag = true;
      if (flag) {
        if (info.hasCommited == undefined) {
          wx.request({
            url: financeConfirmURL,
            method: "GET",
            data: data,
            success: function (res) {
              if (res.data.msg == "OK")
                util.goTo("/pages/publicPages/approveback/approveback?delta=3");
              else
                console.log("server错误");
            },
            fail: function (e) {
              console.log("server错误");
              let hasCommited = undefined;
              info.hasCommited = hasCommited;
              that.setData({
                info: info
              });
            }
          });
          let hasCommited = true;
          info.hasCommited = hasCommited;
          that.setData({
            info: info
          });
          util.showLoading();
        } else {
          util.showError(that, "您已提交过，请不要重复提交");
          return false;
        }

      }
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
  bindData: function (e) {
    let info = this.data.info;
    let value = e.detail.value;
    let item = e.currentTarget.dataset.item;
    if (item == "paidTime") {
      if (util.timeHasPassed(value)) {
        util.showError(this, "时间不能早于今天");
        return false;
      }
    }
    info[item] = value;
    this.setData({
      info: info
    });
  },
  checkData: function (data) {
    if (util.isEmpty(data.voucherNum)) {
      util.showError(this, "凭证号不能为空");
      return false;
    } else if (!util.isInteger(data.voucherNum)) {
      util.showError(this, "凭证号必须是整数");
      return false;
    }
    if (util.isEmpty(data.applyIdinvoice)) {
      util.showError(this, "applyIdinvoice不能为空");
      return false;
    }
    return true;
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let info = JSON.parse(options.item);
    // 领导审批按钮是否显示
    let showButton = options.showButton || -1;
    // 自己修改按钮是否显示
    let canUpdate = options.canUpdate || -1;
    // 员工确认按钮是否显示
    let canConfirm = options.canConfirm || -1;
    // 财务审批按钮是否能显示
    let canFinance = options.canFinance || -1;
    // 员工撤回按钮是否能显示
    let canRevoke = options.canRevoke || -1;
    let notaskid = options.notaskid || -1;
    let canFinanceUpdate = options.canFinanceUpdate || false;
    // 财务审批提交的时候需要
    let applyIdinvoice = options.applyIdinvoice || -1;
    let middleList = JSON.parse(info.passAddress);
    info.paidTime = util.getBeginAndEndDate().endTime;
    info.middleList = middleList;
    info.applyIdinvoice = applyIdinvoice;
    try {
      info.applyTime = info.applyTime.split(" ")[0];
    } catch (error) {
      console.log(error);
    }
    
    this.setData({
      info: info,
      showButton: showButton,
      canUpdate: canUpdate,
      canConfirm: canConfirm,
      canFinance: canFinance,
      canRevoke: canRevoke,
      notaskid: notaskid,
      canFinanceUpdate: canFinanceUpdate
    });
  },
  agree: function (e) {
    let that = this;
    let info = this.data.info;
    if (info.hasCommited == undefined) {
      wx.request({
        url: approveURL,
        data: {
          applyId: this.data.info.applyId,
          taskId: this.data.info.taskId,
          result: "true",
          comment: ""
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("出错了");

        },
        fail: function (res) {
          console.log("出错了");
          let hasCommited = undefined;
          info.hasCommited = hasCommited;
          that.setData({
            info: info
          });
        }
      })
      let hasCommited = true;
      info.hasCommited = hasCommited;
      that.setData({
        info: info
      });
      util.showLoading();
    } else {
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }

  },
  disagree: function (e) {
    this.showModal();


  },
  toUpdate: function (e) {
    util.goTo("/pages/operatePages/myApply/adaptable/private/private?notaskid=" + this.data.notaskid + "&item=" + JSON.stringify(this.data.info));
  },
  relinquish: function (e) {
    let that = this;

    let info = this.data.info;
    if (info.hasCommited == undefined) {
      wx.request({
        url: confirmURL,
        data: {
          applyId: this.data.info.applyId,
          taskId: this.data.info.taskId,
          staffresult: "false"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("出错了");
        },
        fail: function (res) {
          console.log("出错了");
          let hasCommited = undefined;
          info.hasCommited = hasCommited;
          that.setData({
            info: info
          });
        }
      })
      let hasCommited = true;
      info.hasCommited = hasCommited;
      that.setData({
        info: info
      });
      util.showLoading();
    } else {
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }


  },
  // 员工确认
  confirm: function (e) {
    let that = this;
    let info = this.data.info;
    if (info.hasCommited == undefined) {
      wx.request({
        url: confirmURL,
        data: {
          applyId: this.data.info.applyId,
          taskId: this.data.info.taskId,
          staffresult: "true"
        },
        method: 'GET',
        success: function (res) {
          if (res.data.msg == "OK")
            util.goTo("/pages/publicPages/approveback/approveback?delta=2");
          else
            console.log("出错了");
        },
        fail: function (res) {
          console.log("出错了");
          let hasCommited = undefined;
          info.hasCommited = hasCommited;
          that.setData({
            info: info
          });
        }
      })
      let hasCommited = true;
      info.hasCommited = hasCommited;
      that.setData({
        info: info
      });
      util.showLoading();
    } else {
      util.showError(that, "您已经提交，请勿重复提交");
      return false;
    }

  },
  financeRelinquish: function (e) {
    this.showModal();
  },
  revoke: function (e) {
    let that = this;
    let info = this.data.info;

    util.confirmRevoke(function () {
      if (info.hasCommited == undefined) {
        wx.request({
          url: revokeURL,
          data: {
            applyId: that.data.info.applyId,
            taskId: that.data.info.taskId,
            cause: ""
          },
          method: 'GET',
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=2");
            else
              console.log("出错了");

          },
          fail: function (res) {
            console.log("出错了");
            let hasCommited = undefined;
            info.hasCommited = hasCommited;
            that.setData({
              info: info
            });
          }
        })
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
        util.showLoading();
      } else {
        util.showError(that, "您已经提交，请勿重复提交");
        return false;
      }
    });

  },
  financeConfirm: function (e) {
    let that = this;
    let data = {};
    let info = this.data.info;
    data.taskId = info.taskId;
    data.applyId = info.applyId;
    data.finaceresult = "true";
    data.applyIdinvoice = info.applyIdinvoice || "";
    data.voucherNum = info.voucherNum || "";
    data.paidTime = info.paidTime;
    let flag = this.checkData(data);
    if (flag) {
      if (info.hasCommited == undefined) {
        wx.request({
          url: financeConfirmURL,
          method: "GET",
          data: data,
          success: function (res) {
            if (res.data.msg == "OK")
              util.goTo("/pages/publicPages/approveback/approveback?delta=3");
            else
              console.log("server错误");
          },
          fail: function (e) {
            console.log("server错误");
            let hasCommited = undefined;
            info.hasCommited = hasCommited;
            that.setData({
              info: info
            });
          }
        });
        let hasCommited = true;
        info.hasCommited = hasCommited;
        that.setData({
          info: info
        });
        util.showLoading();
      } else {
        util.showError(that, "您已提交过，请不要重复提交");
        return false;
      }

    }
  },
  onUnload: function () {

  }
})