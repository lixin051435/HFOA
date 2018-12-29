// pages/operatePages/myApply/adaptable/after/after.js

let util = require("../../../../../utils/util");

const url = getApp().globalData.URL + '/entertain/postregistration';

// 获取发票出具单位名称
const invoiceUnitURL = getApp().globalData.URL + '/entertain/getInvoiceUnitType';

function Info() {
  this.invoiceDate = util.getBeginAndEndDate().endTime;
  this.accuracy = 1;
}

Page({


  data: {
    showErrorMsg: false,

    // 能匹配到的招待单位列表
    bindSource: [],

    // 是否显示招待单位匹配列表
    showInputView: false,
    isShowCompanyMask: false,
    info: {}
  },
  // 点击匹配项的遮罩层时隐藏遮罩层，隐藏匹配框
  hideCompanyMask: function () {
    this.setData({
      showInputView: false,
      isShowCompanyMask: false
    })
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    let item = null;
    try {
      item = JSON.parse(options.item);
      let infos = item.entertainregisterinfo;
      
      let info = new Info();
      if (infos.length > 0) {
        // 如果之前有过事后登记，则回显
        info = infos[infos.length - 1];
      } else {
        // 没有事后登记，则是第一次，初始化
        info = new Info();
        info.invoiceSum = 0;
        info.wineSum = 0;
      }
      info.personNum = parseInt(item.entertainNum) + parseInt(item.accompanyNum);
      info.number = item.number;
      info.taskId = item.taskId;
      info.registerMan = item.manager;
      info.enterSum = parseFloat(info.invoiceSum) + parseFloat(info.wineSum);
      that.setData({
        info: info,
        item: item
      });
    } catch (error) {
      console.log(error);
    }

    // 获取发票出具单位名称
    util.request(invoiceUnitURL, {}, function (res) {
      util.showNetworkError(res, that, "事后登记");

      // 所有单位名称
      let unitNames = [];
      try {
        res.data.forEach(element => {
          if (!element.invoiceUnit) {
            // element == null undefined "" NaN
          } else {
            unitNames.push(element.invoiceUnit);
          }
        });
      } catch (error) {
        console.log(error);
      } finally {
        that.setData({
          unitNames: unitNames
        });
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

  },
  setEntertainObject: function (e) {
    let info = this.data.info;
    info.invoiceUnit = e.currentTarget.dataset.value;
    this.setData({
      info: info,
      showInputView: false,
      isShowCompanyMask: false
    });
  },
  inputedit: function (e) {
    // input 和 info 双向数据绑定
    let that = this;
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    let info = this.data.info;
    info[dataset.item] = value;
    this.setData({
      info: info
    });

    let item = dataset.item;
    switch (item) {
      case "invoiceSum":
      case "wineSum":
        if (util.isNumber(info.invoiceSum) && util.isNumber(info.wineSum)) {
          let enterSum = (parseFloat(info.invoiceSum) + parseFloat(info.wineSum)).toFixed(info.accuracy);
          let personSum = (enterSum / info.personNum).toFixed(info.accuracy);
          if (!isNaN(enterSum)) {
            info.enterSum = enterSum;
          } else {
            info.enterSum = 0;
          }
          if (!isNaN(personSum)) {
            info.personSum = personSum;
          } else {
            info.personSum = 0;
          }
        } else {
          info.enterSum = 0;
          info.personSum = 0;
        }
        break;
      case "invoiceUnit":
        try {
          if (value == "") {
            that.setData({
              showInputView: false,
              isShowCompanyMask: false
            });
            return false;
          }
          that.setData({
            bindSource: [],
            showInputView: false,
            isShowCompanyMask: false
          });
          let bindSource = that.data.bindSource;
          let unitNames = that.data.unitNames;
          unitNames.forEach(function (e) {
            if (e.indexOf(value) != -1) {
              bindSource.push(e)
            }
          });
          that.setData({
            bindSource: bindSource || [],
            showInputView: bindSource.length > 0,
            isShowCompanyMask: bindSource.length > 0,
          });
        } catch (error) {
          console.log(error);
        }

        break;
    }

    this.setData({
      info: info
    });
  },
  /**
   * 非补录的发票日期不能早于申请，补录的发票日期不早于实际招待日期
   */
  setTime: function (e) {
    let value = e.detail.value;
    let dataset = e.currentTarget.dataset;
    let info = this.data.info;
    let applyTime = this.data.item.applyTime;
    let beforeTime = this.data.item.beforeDate;
    if (beforeTime != "请选择实际时间") {
      // 说明beforeTime 是实际招待日期 事前是补录的
      if (new Date(beforeTime).getTime() > new Date(value).getTime()) {
        util.showError(this, "开票时间不能早于实际招待时间");
        return false;
      }
      if (new Date(value).getTime() > new Date().getTime()) {
        util.showError(this, "开票时间不能晚于今天");
        return false;
      }
    } else {
      // 说明applyTime 是实际招待日期 事前是非补录的
      if (new Date(applyTime).getTime() > new Date(value).getTime()) {
        util.showError(this, "开票时间不能早于实际招待时间");
        return false;
      }
      if (new Date(value).getTime() > new Date().getTime()) {
        util.showError(this, "开票时间不能晚于今天");
        return false;
      }
    }
    info[dataset.item] = value;
    this.setData({
      info: info
    });
  },
  checkData: function (info) {
    if (util.isEmpty(info.number)) {
      util.showError(this, "审批编号不能为空");
      return false;
    }
    if (util.isEmpty(info.invoiceDate)) {
      util.showError(this, "开票日期不能为空");
      return false;
    }
    if (util.isEmpty(info.invoiceContent)) {
      util.showError(this, "开票内容不能为空");
      return false;
    }
    if (util.isEmpty(info.invoiceNum)) {
      util.showError(this, "发票张数不能为空");
      return false;
    } else if (!util.isInteger(info.invoiceNum)) {
      util.showError(this, "发票张数必须是整数");
      return false;
    }
    if (util.isEmpty(info.invoiceUnit)) {
      util.showError(this, "开票单位不能为空");
      return false;
    }
    if (util.isEmpty(info.registerMan)) {
      util.showError(this, "登记人不能为空");
      return false;
    }
    if (util.isEmpty(info.invoiceNumber)) {
      util.showError(this, "发票号不能为空");
      return false;
    }
    if (util.isEmpty(info.invoiceSum)) {
      util.showError(this, "发票金额不能为空");
      return false;
    } else if (!util.isNumber(info.invoiceSum)) {
      util.showError(this, "发票金额必须是数字");
      return false;
    } else {
      if (info.invoiceSum.split(".").length > 1) {
        if (info.invoiceSum.split(".")[1].length > 1) {
          util.showError(this, "发票金额最多一位小数");
          return false;
        }
      }
    }

    if (util.isEmpty(info.wineSum)) {
      if (info.wineSum === 0) {

      } else {
        util.showError(this, "酒水金额不能为空");
        return false;
      }

    } else if (!util.isNumber(info.wineSum)) {
      util.showError(this, "酒水金额必须是数字");
      return false;
    } else {
      if (info.wineSum.split(".").length > 1) {
        if (info.wineSum.split(".")[1].length > 1) {
          util.showError(this, "酒水金额最多一位小数");
          return false;
        }
      }
    }

    if (util.isEmpty(info.enterSum)) {
      util.showError(this, "总金额不能为空");
      return false;
    } else if (!util.isNumber(info.enterSum)) {
      util.showError(this, "总金额必须是数字");
      return false;
    }
    if (util.isEmpty(info.personSum)) {
      console.log("数据错误，没有计算出来人均金额");
      return false;
    }


    return true;
  },
  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    // 事前登记的总预算
    let masterBudget = parseFloat(this.data.item.masterBudget);
    if (info.enterSum > masterBudget) {
      util.showError(that, "总金额超过了事前登记预算");
      return false;
    }
    let flag = this.checkData(info);
    if (flag) {
      info.wineSum = info.wineSum || 0;
      info.remark = info.remark || "";
      // 防止表单重复提交
      if (info.hasCommited == undefined) {
        wx.request({
          url: url,
          method: "GET",
          data: info,
          success: function (res) {
            if (res.data.msg == "OK") {
              util.goTo("/pages/publicPages/approveback/approveback?delta=3");
            } else {
              util.showError(that, "事后登记：服务器内部错误");
              console.log("事后登记：服务器内部错误");
              let hasCommited = undefined;
              info.hasCommited = hasCommited;
              that.setData({
                info: info
              });
            }
          },
          fail: function (res) {
            console.log("出错了");
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
        util.showError(taht, "您已经提交，请勿重复提交");
        return false;
      }
    }
  }
})