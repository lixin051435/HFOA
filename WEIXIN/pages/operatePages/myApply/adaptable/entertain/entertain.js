// pages/entertain/apply/apply.js
const app = getApp();
const util = require('../../../../../utils/util.js');

// 获取审批人
const loadUrl = app.globalData.URL + '/user/getFinder';

// 酒水类型
const wineUrl = app.globalData.URL + '/dictionary/selectWine';

// 招待类型
const zdUrl = app.globalData.URL + '/dictionary/selectDict?type=zd';

// 业务招待修改
const updateEntertainURL = app.globalData.URL + '/entertain/updateEntertain';

// 获取总预算限额
const getLastSumURL = app.globalData.URL + '/entertain/getLastSum';

// 根据openid获取 最大人均预算
const getMaxPerBudgetURL = app.globalData.URL + '/user/getmoney';

// 获取发票出具单位名称
const invoiceUnitURL = getApp().globalData.URL + '/entertain/getInvoiceUnitType';


/**
 * Wine酒类 构造函数
 * @param {string} category 酒种类
 * @param {string} name 酒名
 * @param {string} value 酒数量
 */
function Wine(category, name, value) {
  this.category = category || "请选择酒水类型";
  this.name = name || "";
  this.value = value;
  this.customWineType = "";
  this.customName = "";
}

/**
 * Info 整个表单类 构造函数
 */
function Info() {
  this.applyTime = util.getBeginAndEndDate().endTime;
  this.beforeDate = util.getBeginAndEndDate().endTime;
  this.entertainCategory = "请选择招待类别";
  this.wineType = "请选择酒水类型";
  this.approver = "请选择审批人";
  this.remark = "";
  this.ifWine = "0";
  this.ifBefore = "0";
  this.wines = [];
}

/**
 * 根据真实姓名 返回openId 在抄送人和审批人用到 暂时不考虑重名情况 后期维护者改吧
 * 
 * @param {string} realname 真实姓名
 * @param {list} finderObjectList 对应列表
 */
function getOpenIdByName(realname, finderObjectList) {
  for (let i = 0; i < finderObjectList.length; i++) {
    if (finderObjectList[i].realname == realname) {
      return finderObjectList[i].openid;
    }
  }
  return null;
}



Page({
  /**
   * 页面的初始数据
   */
  data: {
    showErrorMsg: false,
    info: {},

    // 能匹配到的招待单位列表
    bindSource: [],

    // 是否显示招待单位匹配列表
    showInputView: false,

    // 能匹配到的发票单位列表
    bindSource1: [],

    // 是否显示招待发票匹配列表
    showInputView1: false,
    isShowCompanyMask1: false

  },
  /**
   * 前端验证
   * @param {string}} data 
   */
  checkData(data) {
    if (util.isEmpty(data.department)) {
      util.showError(this, "请填写所在部门");
      return false;
    }
    if (util.isEmpty(data.applyTime) || data.applyTime.indexOf("请选择") > -1) {
      util.showError(this, "请选择申请日期");
      return false;
    }
    if (util.isEmpty(data.entertainObject)) {
      util.showError(this, "请填写招待单位");
      return false;
    }
    if (util.isEmpty(data.entertainReason)) {
      util.showError(this, "请填写事由");
      return false;
    }
    if (util.isEmpty(data.entertainNum)) {
      util.showError(this, "请填写接待人数");
      return false;
    } else if (!util.isInteger(data.entertainNum)) {
      util.showError(this, "接待人数必须是整数");
      return false;
    } else if (!util.isNumber(data.entertainNum)) {
      util.showError(this, "接待人数必须是数字");
      return false;
    } else {
      let entertainNum = parseInt(data.entertainNum);
      if (entertainNum <= 0) {
        util.showError(this, "接待人数必须大于0");
        return false;
      }
    }
    if (util.isEmpty(data.accompanyNum)) {
      util.showError(this, "请填写陪同人数");
      return false;
    } else if (!util.isInteger(data.accompanyNum)) {
      util.showError(this, "陪同人数必须是整数");
      return false;
    } else if (!util.isNumber(data.accompanyNum)) {
      util.showError(this, "陪同人数必须是数字");
      return false;
    } else {
      // 陪同人数不能超过招待人数三分之一
      let accompanyNum = parseInt(data.accompanyNum);
      if (accompanyNum <= 0) {
        util.showError(this, "陪同人数必须大于0");
        return false;
      }
      // 合理的陪同人数
      if (parseInt(data.entertainNum) < 10) {
        if (accompanyNum > 3) {
          util.showError(this, "陪同人数最多3人");
          return false;
        }
      } else {
        let entertainOKNum = Math.floor(parseInt(data.entertainNum) / 3);
        if (accompanyNum > entertainOKNum) {
          util.showError(this, "陪同人数不得超过招待人数三分之一");
          return false;
        }
      }
    }
    if (util.isEmpty(data.perBudget)) {
      util.showError(this, "请填写人均预算");
      return false;
    } else if (!util.isNumber(data.perBudget)) {
      util.showError(this, "人均预算必须是数字");
      return false;
    } else {
      if (data.perBudget.split(".").length > 1) {
        if (data.perBudget.split(".")[1].length > 1) {
          util.showError(this, "人均预算最多输入一位小数");
          return false;
        }
      }
      let perBudget = parseFloat(data.perBudget);
      if (perBudget <= 0) {
        util.showError(this, "人均预算必须大于0");
        return false;
      }
      // let maxPerMoney = data.maxPerMoney;
      // if (perBudget > maxPerMoney) {
      //   util.showError(this, "人均预算超过最大预算" + maxPerMoney + "元");
      //   return false;
      // }
    }
    if (util.isEmpty(data.masterBudget)) {
      console.log("总预算没有算出来...前端js错误");
      return false;
    }
    if (util.isEmpty(data.manager)) {
      util.showError(this, "请填写接待人");
      return false;
    }
    if (util.isEmpty(data.approver) || data.approver.indexOf("请选择") > -1) {
      util.showError(this, "请选择审批人");
      return false;
    }
    if (util.isEmpty(data.entertainCategory)) {
      util.showError(this, "请选择接待类型");
      return false;
    }
    if (util.isEmpty(data.ifWine)) {
      util.showError(this, "请选择是否需要酒水");
      return false;
    }
    if (util.isEmpty(data.ifBefore)) {
      util.showError(this, "请选择是否是补录");
      return false;
    }
    if (util.isEmpty(data.entertainCategory) || data.entertainCategory.indexOf("请选择") > -1) {
      util.showError(this, "请选择招待类别");
      return false;
    }
    if (data.beforeDate.indexOf("请选择") > -1) {
      util.showError(this, "请选择用车时间");
      return false;
    }
    if (data.ifBefore == 1) {
      // 补录
      if (new Date(util.getNowTimeDate()).getTime() < new Date(data.beforeDate).getTime()) {
        util.showError(this, "补录招待时间必须早于当前时间");
        return false;
      }
    } else {
      // 非补录
      if (new Date(util.getNowTimeDate()).getTime() > new Date(data.beforeDate).getTime()) {
        util.showError(this, "非补录招待时间必须晚于当前时间");
        return false;
      }
    }
    return true;
  },

  onLoad: function (options) {
    this.init(options);
  },

  // 页面加载时需要请求后台一些内容
  init: function (options) {
    let that = this;

    let info = JSON.parse(options.item);
    // entertainregisterinfo 事后登记对象并赋初值
    let entertainregisterinfo = null;
    if (info.entertainregisterinfo != null) {
      if (info.entertainregisterinfo.length > 0) {
        entertainregisterinfo = info.entertainregisterinfo[0];
      }
    }
    if (entertainregisterinfo != null) {
      entertainregisterinfo.personNum = parseInt(info.entertainNum) + parseInt(info.accompanyNum);
      entertainregisterinfo.number = info.number;
      entertainregisterinfo.taskId = info.taskId;
      entertainregisterinfo.registerMan = info.manager;
      entertainregisterinfo.wineSum = entertainregisterinfo.wineSum || 0;
      entertainregisterinfo.enterSum = parseFloat(entertainregisterinfo.invoiceNum) + parseFloat(entertainregisterinfo.wineSum);
      entertainregisterinfo.accuracy = 1;
      entertainregisterinfo.enterSum = parseFloat(entertainregisterinfo.wineSum) + parseFloat(entertainregisterinfo.invoiceSum);
    } else {
      // util.showError(that,"事后登记信息获取失败");
      console.log("事后登记信息获取失败");
    }

    let wines = JSON.parse(info.wineType);
    let notaskid = options.notaskid || -1;
    info.wines = wines || [];
    info.beforeDate = util.getBeginAndEndDate().endTime;

    // 修正wines
    for (let i = 0; i < wines.length; i++) {
      let wine = wines[i];
      // 自定义酒水类别
      if (wine.category == wine.customWineType) {
        wine.category = "其他";
        // wine.name = "";
      }

      // 自定义酒水名称
      if (wine.name == wine.customName) {
        if (wine.category == "红酒") {
          wine.name = "红酒";
        } else if (wine.category == "白酒") {
          wine.name = "其他";
        }

      }
    }
    this.setData({
      info: info,
      wineIndex: [0, 0],
      notaskid: notaskid,
      entertainregisterinfo: entertainregisterinfo
    });

    // 1.获取最大人均预算
    // wx.request({
    //   url: getMaxPerBudgetURL,
    //   data: {
    //     openId: wx.getStorageSync('openId')
    //   },
    //   method: "GET",
    //   data: {
    //     openId: wx.getStorageSync("openId")
    //   },
    //   success: function (res) {
    //     let money = parseFloat(res.data.data);
    //     let info = that.data.info;
    //     info.maxPerMoney = money;
    //     that.setData({
    //       info: info
    //     });
    //   },
    //   fail: function (e) {
    //     console.log("XHR失败");
    //   }
    // });
    // 2. 获取酒水类型
    wx.request({
      url: wineUrl,
      method: "GET",
      data: {
        openId: wx.getStorageSync("openId")
      },
      success: function (res) {
        let categories = [];
        let names = [];
        res.data.forEach(e => {
          categories.push(e.dict.text);
          let temp = [];
          e.listDict.forEach(ele => {
            temp.push(ele.text);
          });
          names.push(temp);
        });
        let wineTypes = [];
        let info = that.data.info;

        wineTypes.push(categories);
        wineTypes.push(names[0]);
        for (let i = 0; i < info.wines.length; i++) {
          info.wines[i].wineTypes = util.deepClone(wineTypes);
        }
        that.setData({
          categories: categories,
          names: names,
          wineTypes: wineTypes,
          info: info
        });
      },
      fail: function (e) {
        console.log("XHR失败");
      }
    });
    // 3. 获取招待类型
    wx.request({
      url: zdUrl,
      method: "GET",
      success: function (res) {
        let list = res.data;
        let entertainCategories = [];
        list.forEach(element => {
          entertainCategories.push(element.text);
        });
        that.setData({
          entertainCategories: entertainCategories
        });
      },
      fail: function (e) {
        console.log("XHR失败");
      }
    });
    // 4. 获取审批人 
    wx.request({
      url: loadUrl,
      method: 'GET',
      data: {
        openId: wx.getStorageSync('openId'),
        moduleNum: 5
      },
      success: function (res) {
        let realname = res.data.data.user.realname;
        let department = res.data.data.user.departmentname;
        let finders = res.data.data.finder;
        let finderList = [];
        for (let i = 0; i < finders.length; i++) {
          finderList.push(finders[i].realname);
        }
        that.setData({
          finders: finderList,
          finderObjectList: finders,
        });
        let info = that.data.info;
        info.department = department;
        info.manager = realname;
        that.setData({
          info: info,
          approvers: finderList
        });

        // 必须获取到department之后 才能 获取总预算 所以写到回调函数里面
        // 4.1 获取总预算
        wx.request({
          url: getLastSumURL,
          method: 'GET',
          data: {
            department: department
          },
          success: function (res) {
            let budgetTotal = parseFloat(res.data.budget) || 0;
            that.setData({
              budgetTotal: budgetTotal
            });
          },
          fail: function (e) {
            console.log("XHR失败");
          }
        });

      },
      fail: function (e) {
        console.log("XHR失败");
      }
    });

    // 5. 获取业务招待单位名称
    util.getEntertainTypes(function (data) {
      let unitNames = data.unitNames;
      that.setData({
        unitNames: unitNames
      });
    });

    // 6. 获取发票出具单位名称
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
          unitNames1: unitNames
        });
      }
    });
  },

  setEntertainCategory: function (e) {
    let info = this.data.info;
    info.entertainCategory = this.data.entertainCategories[e.detail.value];
    this.setData({
      info: info
    });
  },

  setApproveMan: function (e) {
    let index = e.detail.value;
    let info = this.data.info;
    info.approver = this.data.finders[index];
    info.approveManOpenId = getOpenIdByName(this.data.finders[index], this.data.finderObjectList);
    this.setData({
      info: info
    });
  },

  addItem: function (e) {
    let info = this.data.info;
    let wine = new Wine();
    wine.wineTypes = util.deepClone(this.data.wineTypes);
    info.wines.push(wine);
    this.setData({
      info: info
    });
  },

  removeItem: function (e) {
    let info = this.data.info;
    info.wines.pop();
    this.setData({
      info: info
    });
  },

  setWineNumber: function (e) {
    let index = parseInt(e.currentTarget.id.replace("wineNumber-", ""));
    let number = e.detail.value;
    let info = this.data.info;
    info.wines[index].value = number;
    this.setData({
      info: info
    });
  },
  setCustomWineName: function (e) {
    let index = parseInt(e.currentTarget.id.replace("wineNumber-", ""));
    let name = e.detail.value;
    let info = this.data.info;
    info.wines[index].customName = name;
    this.setData({
      info: info
    });
  },

  radioChange: function (e) {
    let id = e.currentTarget.id;
    let item = e.target.dataset.item;
    let value = e.detail.value;
    let info = this.data.info;
    if (id == "ifWine" && value == "0") {
      info.wines = [];
    }

    info[item] = value;
    this.setData({
      info: info
    });
  },

  bindApplyTime: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    info.applyTime = value;
    this.setData({
      info: info
    });
  },
  bindBeforeTime: function (e) {
    let value = e.detail.value;
    let info = this.data.info;
    if (info.ifBefore == 1) {
      // 选了补录
      if (new Date(value).getTime() > new Date(util.getNowTimeDate()).getTime()) {
        util.showError(this, "时间不能晚于今天");
        return false;
      }

    } else {
      // 没选补录
      if (new Date(value).getTime() < new Date(util.getNowTimeDate()).getTime()) {
        util.showError(this, "时间不能早于今天");
        return false;
      }
    }

    info.beforeDate = value;
    this.setData({
      info: info
    });
  },

  setWineCategory: function (e) {
    // 修正value 如果只选了第一个第二个没选会是null
    let value = e.detail.value;
    value[0] = value[0] || 0;
    value[1] = value[1] || 0;
    // 赋值到每一个item
    let index = parseInt(e.currentTarget.id.replace("wineType-", ""));
    let model = {
      category: this.data.categories[value[0]],
      name: this.data.names[value[0]][value[1]]
    }
    let info = this.data.info;
    info.wines[index].category = model.category;
    info.wines[index].name = model.name || "";
    // 更新info和wineTypes(wineTypes 得复原)
    let wineTypes = [];
    wineTypes.push(this.data.categories);
    wineTypes.push(this.data.names[0]);
    this.setData({
      info: info,
      wineTypes: wineTypes
    });
  },

  updateWineColumn: function (e) {
    let column = e.detail.column;
    let value = e.detail.value;
    let info = this.data.info;
    let index = parseInt(e.currentTarget.id.replace("wineType-", ""));
    // 如果改变第一列 则实时改变第二列的值
    if (column == 0) {
      let wineTypes = [];
      wineTypes.push(this.data.categories);
      wineTypes.push(this.data.names[value]);
      info.wines[index].wineTypes = util.deepClone(wineTypes);

      this.setData({
        info: info,
      });

    }
  },
  setEntertainObject: function (e) {
    let info = this.data.info;
    info.entertainObject = e.currentTarget.dataset.value;
    this.setData({
      info: info,
      showInputView: false
    });
  },

  inputedit: function (e) {
    // 1. input 和 info 双向数据绑定
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    this.data[dataset.obj][dataset.item] = value;
    this.setData({
      obj: this.data[dataset.obj]
    });
    // 2. 实时计算总预算
    let perBudget = this.data.info.perBudget;
    let accompanyNum = this.data.info.accompanyNum;
    let entertainNum = this.data.info.entertainNum;
    let temp = this.data.info;
    switch (dataset.item) {
      case "entertainNum":
      case "accompanyNum":
      case "perBudget":
        if (util.isInteger(accompanyNum) && util.isInteger(entertainNum) && util.isNumber(perBudget)) {
          let budgetTotal = this.data.budgetTotal;
          let masterBudget = parseFloat(perBudget) * (parseInt(entertainNum) + parseInt(accompanyNum));
          if (masterBudget > budgetTotal) {
            util.showError(this, "已经超过总预算了");
            temp.perBudget = "";
            temp.masterBudget = 0;
            this.setData({
              info: temp
            });
            return false;
          } else {
            temp.masterBudget = masterBudget;
          }

        } else {
          temp.masterBudget = 0;
        }
        this.setData({
          info: temp
        });
        break;
      case "entertainObject":
        if (value == "") {
          this.setData({
            showInputView: false
          });
        }
        this.setData({
          bindSource: [],
        });
        let bindSource = this.data.bindSource;
        let unitNames = this.data.unitNames;
        unitNames.forEach(function (e) {
          if (e.indexOf(value) != -1) {
            bindSource.push(e)
          }
        });
        this.setData({
          bindSource: bindSource || [],
          showInputView: bindSource.length > 0
        });
        break;
    }
  },
  // 失去焦点触发
  notFocus: function (e) {
    this.setData({
      showInputView: false
    })
  },

  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    let wines = info.wines;
    // 修正酒水类别，如果是其他，则修正
    for (let i = 0; i < wines.length; i++) {
      let wine = wines[i];
      if (wine.name == "其他" || wine.name == "红酒") {
        wine.name = wine.customName;
      }
    }
    let wineNum = 0;
    info.wines.forEach(e => {
      wineNum += parseInt(e.value);
    });
    // 校验wines
    for (let i = 0; i < info.wines.length; i++) {
      let e = info.wines[i];
      if (e.value == undefined || e.value == 0) {
        util.showError(that, "请填写酒水数量");
        return false;
      } else if (!util.isInteger(e.value)) {
        util.showError(that, "酒水数量必须是数字");
        return false;
      }
      if (util.isEmpty(e.name) || e.name.indexOf("请选择") > -1 || e.name == "") {
        util.showError(that, "请选择酒水类型");
        return false;
      }
      if (util.isEmpty(e.category) || e.category.indexOf("请选择") > -1) {
        util.showError(that, "请选择酒水类型");
        return false;
      }
    }
    let data = {};

    /**
     */
    // 拼数据
    data.department = info.department,
      data.applyTime = info.applyTime,
      data.entertainObject = info.entertainObject,
      data.entertainReason = info.entertainReason,
      data.entertainNum = info.entertainNum,
      data.accompanyNum = info.accompanyNum,
      data.perBudget = info.perBudget,
      data.masterBudget = info.masterBudget,
      data.entertainCategory = info.entertainCategory,
      data.manager = info.manager,
      data.approver = info.approver,
      data.approverOpenid = info.approverOpenid || "该审批人没有openid",
      data.remark = info.remark || "",
      data.ifWine = info.ifWine,
      data.wineType = JSON.stringify(info.wines),
      data.wineNum = wineNum,
      data.ifBefore = info.ifBefore,
      data.beforeDate = info.beforeDate,
      data.openId = wx.getStorageSync('openId')
    data.id = info.id;
    data.number = info.number;
    data.maxPerMoney = info.maxPerMoney;
    data.taskId = info.taskId;
    if (this.data.notaskid == -1) {
      // 从左边进入
      data.canUpdate = 0;
    } else {
      // 从右边进入
      data.canUpdate = 1;
    }
    // 前端校验
    let flag = that.checkData(data);
    // 发请求
    if (flag) {
      util.request(getMaxPerBudgetURL, {
        openId: wx.getStorageSync("openId"),
        entertainCategory: data.entertainCategory,
        perBudget: data.perBudget
      }, function (res) {
        if (res.data.data == false) {
          util.showError(that, "超过人均预算");
          return false;
        } else if (res.data.data == true) {
          if (that.data.hasCommited == undefined) {

            wx.request({
              url: updateEntertainURL,
              method: "GET",
              data: data,
              success: function (res) {
                let resultdata = res.data.msg;
                if (resultdata == "OK") {
                  util.goTo("/pages/publicPages/applyback/applyback");
                } else {
                  console.log("招待修改：服务器内部错误");
                  util.showError(that, "招待修改：服务器内部错误");
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
                that.setData({
                  hasCommited: hasCommited
                });
              }
            });
            let hasCommited = true;
            that.setData({
              hasCommited: hasCommited
            });
            util.showLoading();
          }
        } else {
          util.showError(that, "您已经提交，请勿重复提交");
          return false;
        }
      });

    }
  },










  // 事后登记的函数

  setEntertainObject1: function (e) {
    let entertainregisterinfo = this.data.entertainregisterinfo;
    entertainregisterinfo.invoiceUnit = e.currentTarget.dataset.value;
    this.setData({
      entertainregisterinfo: entertainregisterinfo,
      showInputView1: false,
      isShowCompanyMask1: false
    });
  },
  inputedit1: function (e) {
    let that = this;
    // input 和 info 双向数据绑定
    let dataset = e.currentTarget.dataset;
    let value = e.detail.value;
    let entertainregisterinfo = this.data.entertainregisterinfo;
    entertainregisterinfo[dataset.item] = value;
    this.setData({
      entertainregisterinfo: entertainregisterinfo
    });

    let item = dataset.item;
    switch (item) {
      case "invoiceSum":
      case "wineSum":
        if (util.isNumber(entertainregisterinfo.invoiceSum) && util.isNumber(entertainregisterinfo.wineSum)) {
          let enterSum = (parseFloat(entertainregisterinfo.invoiceSum) + parseFloat(entertainregisterinfo.wineSum)).toFixed(entertainregisterinfo.accuracy);
          let personSum = (enterSum / entertainregisterinfo.personNum).toFixed(entertainregisterinfo.accuracy);
          if (!isNaN(enterSum)) {
            entertainregisterinfo.enterSum = enterSum;
          } else {
            entertainregisterinfo.enterSum = 0;
          }
          if (!isNaN(personSum)) {
            entertainregisterinfo.personSum = personSum;
          } else {
            entertainregisterinfo.personSum = 0;
          }
        } else {
          entertainregisterinfo.enterSum = 0;
          entertainregisterinfo.personSum = 0;
        }
        break;
      case "invoiceUnit":
        try {
          if (value == "") {
            that.setData({
              showInputView1: false,
              isShowCompanyMask1: false
            });
            return false;
          }
          that.setData({
            bindSource1: [],
            showInputView1: false,
            isShowCompanyMask1: false
          });
          let bindSource1 = that.data.bindSource1;
          let unitNames = that.data.unitNames1;
          unitNames.forEach(function (e) {
            if (e.indexOf(value) != -1) {
              bindSource1.push(e)
            }
          });
          that.setData({
            bindSource1: bindSource1 || [],
            showInputView1: bindSource1.length > 0,
            isShowCompanyMask1: bindSource1.length > 0,
          });
        } catch (error) {
          console.log(error);
        }

        break;
    }

    this.setData({
      entertainregisterinfo: entertainregisterinfo
    });
  },
  setTime1: function (e) {
    let value = e.detail.value;
    let dataset = e.currentTarget.dataset;
    let entertainregisterinfo = this.data.entertainregisterinfo;
    let applyTime = this.data.info.applyTime;
    let beforeTime = this.data.info.beforeDate;
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
    entertainregisterinfo[dataset.item] = value;
    this.setData({
      entertainregisterinfo: entertainregisterinfo
    });
  },
  checkData1: function (entertainregisterinfo) {
    if (util.isEmpty(entertainregisterinfo.number)) {
      util.showError(this, "审批编号不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceDate)) {
      util.showError(this, "开票日期不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceContent)) {
      util.showError(this, "开票内容不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceNum)) {
      util.showError(this, "发票张数不能为空");
      return false;
    } else if (!util.isInteger(entertainregisterinfo.invoiceNum)) {
      util.showError(this, "发票张数必须是整数");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceUnit)) {
      util.showError(this, "开票单位不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.registerMan)) {
      util.showError(this, "登记人不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceNumber)) {
      util.showError(this, "发票号不能为空");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.invoiceSum)) {
      util.showError(this, "发票金额不能为空");
      return false;
    } else if (!util.isNumber(entertainregisterinfo.invoiceSum)) {
      util.showError(this, "发票金额必须是数字");
      return false;
    } else {
      if (entertainregisterinfo.invoiceSum.split(".").length > 1) {
        if (nfo.invoiceSum.split(".")[1].length > 1) {
          util.showError(this, "发票金额最多一位小数");
          return false;
        }
      }
    }

    if (util.isEmpty(entertainregisterinfo.wineSum)) {
      if (entertainregisterinfo.wineSum === 0) {

      } else {
        util.showError(this, "酒水金额不能为空");
        return false;
      }

    } else if (!util.isNumber(entertainregisterinfo.wineSum)) {
      util.showError(this, "酒水金额必须是数字");
      return false;
    } else {
      if (entertainregisterinfo.wineSum.split(".").length > 1) {
        if (entertainregisterinfo.wineSum.split(".")[1].length > 1) {
          util.showError(this, "酒水金额最多一位小数");
          return false;
        }
      }
    }

    if (util.isEmpty(entertainregisterinfo.enterSum)) {
      util.showError(this, "总金额不能为空");
      return false;
    } else if (!util.isNumber(entertainregisterinfo.enterSum)) {
      util.showError(this, "总金额必须是数字");
      return false;
    }
    if (util.isEmpty(entertainregisterinfo.personSum)) {
      console.log("数据错误，没有计算出来人均金额");
      return false;
    }


    return true;
  },
  formSubmit1: function (e) {
    let that = this;
    let info = this.data.info;
    let entertainregisterinfo = this.data.entertainregisterinfo;
    // 事前登记的总预算
    let masterBudget = parseFloat(info.masterBudget);
    if (entertainregisterinfo.enterSum > masterBudget) {
      util.showError(that, "总金额超过了事前登记预算");
      return false;
    }
    if (this.data.notaskid == -1) {
      // 从左边进入
      entertainregisterinfo.canUpdate = 0;
      delete entertainregisterinfo["registerId"];
    } else {
      // 从右边进入
      entertainregisterinfo.canUpdate = 1;
      delete entertainregisterinfo["id"];
    }

    let flag = this.checkData1(entertainregisterinfo);

    if (flag) {
      entertainregisterinfo.wineSum = entertainregisterinfo.wineSum || 0;
      entertainregisterinfo.remark = entertainregisterinfo.remark || "";
      // 防止表单重复提交
      if (entertainregisterinfo.hasCommited == undefined) {
        wx.request({
          url: updateEntertainURL,
          method: "GET",
          data: entertainregisterinfo,
          success: function (res) {
            if (res.data.msg == "OK") {
              util.goTo("/pages/publicPages/approveback/approveback?delta=3");
            } else {
              console.log("招待修改：服务器内部错误");
              util.showError(that, "招待修改：服务器内部错误");
              let hasCommited = undefined;
              entertainregisterinfo.hasCommited = hasCommited;
              that.setData({
                entertainregisterinfo: entertainregisterinfo
              });
            }
          },
          fail: function (res) {
            console.log("出错了");
            let hasCommited = undefined;
            entertainregisterinfo.hasCommited = hasCommited;
            that.setData({
              entertainregisterinfo: entertainregisterinfo
            });
          }
        });
        let hasCommited = true;
        entertainregisterinfo.hasCommited = hasCommited;
        that.setData({
          entertainregisterinfo: entertainregisterinfo
        });
        util.showLoading();
      } else {
        util.showError(taht, "您已经提交，请勿重复提交");
        return false;
      }
    }
  },
  update(e) {
    let that = this;
    let info = this.data.info;
    let wines = info.wines;
    // 修正酒水类别，如果是其他，则修正
    for (let i = 0; i < wines.length; i++) {
      let wine = wines[i];
      if (wine.name == "其他" || wine.name == "红酒") {
        wine.name = wine.customName;
      }
    }
    let wineNum = 0;
    info.wines.forEach(e => {
      wineNum += parseInt(e.value);
    });
    // 校验wines
    for (let i = 0; i < info.wines.length; i++) {
      let e = info.wines[i];
      if (e.value == undefined || e.value == 0) {
        util.showError(that, "请填写酒水数量");
        return false;
      } else if (!util.isInteger(e.value)) {
        util.showError(that, "酒水数量必须是数字");
        return false;
      }
      if (util.isEmpty(e.name) || e.name.indexOf("请选择") > -1 || e.name == "") {
        util.showError(that, "请选择酒水类型");
        return false;
      }
      if (util.isEmpty(e.category) || e.category.indexOf("请选择") > -1) {
        util.showError(that, "请选择酒水类型");
        return false;
      }
    }
    let data = {};

    /**
     */
    // 拼数据
    data.department = info.department,
      data.applyTime = info.applyTime,
      data.entertainObject = info.entertainObject,
      data.entertainReason = info.entertainReason,
      data.entertainNum = info.entertainNum,
      data.accompanyNum = info.accompanyNum,
      data.perBudget = info.perBudget,
      data.masterBudget = info.masterBudget,
      data.entertainCategory = info.entertainCategory,
      data.manager = info.manager,
      data.approver = info.approver,
      data.approverOpenid = info.approverOpenid || "该审批人没有openid",
      data.remark = info.remark || "",
      data.ifWine = info.ifWine,
      data.wineType = JSON.stringify(info.wines),
      data.wineNum = wineNum,
      data.ifBefore = info.ifBefore,
      data.beforeDate = info.beforeDate,
      data.openId = wx.getStorageSync('openId')
    data.id = info.id;
    data.number = info.number;
    data.maxPerMoney = info.maxPerMoney;
    data.taskId = info.taskId;
    if (this.data.notaskid == -1) {
      // 从左边进入
      data.canUpdate = 0;
    } else {
      // 从右边进入
      data.canUpdate = 1;
    }
    let flag1 = this.checkData(data);
    if (flag1 == false) {
      return false;
    } else {
      // 事前登记前端校验成功

      let flag2 = true;
      let entertainregisterinfo = this.data.entertainregisterinfo;
      if (entertainregisterinfo != null) {
        // 事前登记的总预算
        let masterBudget = parseFloat(info.masterBudget);
        if (entertainregisterinfo.enterSum > masterBudget) {
          util.showError(that, "总金额超过了事前登记预算");
          return false;
        }
        if (this.data.notaskid == -1) {
          // 从左边进入
          delete entertainregisterinfo["registerId"];
        } else {
          // 从右边进入
          entertainregisterinfo.registerId = entertainregisterinfo.id;
          delete entertainregisterinfo["id"];

        }
        flag2 = this.checkData1(entertainregisterinfo);
      }

      // 事后登记修改校验
      if (flag2 == false) {
        return false;
      } else {
        // 事后登记前端校验成功
        for (let key in entertainregisterinfo) {
          data[key] = entertainregisterinfo[key];
        }

        // 发请求
        util.request(getMaxPerBudgetURL, {
          openId: wx.getStorageSync("openId"),
          entertainCategory: data.entertainCategory,
          perBudget: data.perBudget
        }, function (res) {
          if (res.data.data == false) {
            util.showError(that, "人均预算超过最大限额");
            return false;
          } else if (res.data.data == true) {
            if (that.data.hasCommited == undefined) {

              wx.request({
                url: updateEntertainURL,
                method: "GET",
                data: data,
                success: function (res) {
                  let resultdata = res.data.msg;
                  if (resultdata == "OK") {
                    util.goTo("/pages/publicPages/applyback/applyback");
                  } else {
                    console.log("招待修改：服务器内部错误");
                    util.showError(that, "招待修改：服务器内部错误");
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
                  that.setData({
                    hasCommited: hasCommited
                  });
                }
              });
              let hasCommited = true;
              that.setData({
                hasCommited: hasCommited
              });
              util.showLoading();
            }
          } else {
            util.showError(that, "您已经提交，请勿重复提交");
            return false;
          }
        });

      }
    }








  }

})