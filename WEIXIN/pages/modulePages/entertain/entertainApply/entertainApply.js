// pages/entertain/apply/apply.js
const app = getApp();
const util = require('../../../../utils/util.js');
const root = getApp().globalData.URL;
const url1 = root + '/applyExpenses/insertApply';

// 获取审批人 旧接口
// const loadUrl = app.globalData.URL + '/user/getFinder';

// 获取审批人 新接口
const getFinderURL = app.globalData.URL + '/user/getEntertainFinder';

// 获取酒水类型
const getWineURL = app.globalData.URL + '/dictionary/selectWine';

// 获取招待类型
const getZDURL = app.globalData.URL + '/dictionary/selectDict?type=zd';

// 增加一条招待信息
const addEntertainURL = app.globalData.URL + '/entertain/insertEntertain';

// 根据openid获取 最大人均预算
const getMaxPerBudgetURL = app.globalData.URL + '/user/getmoney';

// 获取总预算限额
const getLastSumURL = app.globalData.URL + '/entertain/getLastSum';

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
  this.beforeDate = "请选择招待日期";
  this.entertainCategory = "请选择招待类别";
  this.wineType = "请选择酒水类型";
  this.remark = "";
  this.ifWine = "0";
  this.ifBefore = "0";
  this.totalBudget = 0;
  this.wines = [];
  this.manager = wx.getStorageSync("username");
  this.department = wx.getStorageSync("departmentName");
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
    isShowCompanyMask: false,
    bindSource1: '11111<span class="red">hello,world</span>22222'
  },

  checkData: function (data) {
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
      // 人均预算 交给后台校验
      // let maxPerMoney = data.maxPerMoney;
      // if (perBudget > maxPerMoney) {
      //   util.showError(this, "人均预算超过最大预算" + maxPerMoney + "元");
      //   return false;
      // }
    }
    if (util.isEmpty(data.masterBudget)) {
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
    if (util.isEmpty(data.entertainCategory) || data.entertainCategory.indexOf("请选择") > -1) {
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
      util.showError(this, "请选择招待时间");
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

    if (data.approverOpenid == null || data.approverOpenid == "") {
      util.showError(this, "该审批人尚未绑定，请重新选择");
      return false;
    }
    return true;
  },

  onLoad: function (options) {
    this.init();
  },

  // 页面加载时需要请求后台一些内容
  init: function () {
    let that = this;
    this.setData({
      info: new Info(),
      wineIndex: [0, 0]
    });
    // 1.获取最大人均预算
    // wx.request({
    //   url: getMaxPerBudgetURL,
    //   data:{
    //     openId:wx.getStorageSync('openId')
    //   },
    //   method: "GET",
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
      url: getWineURL,
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
        wineTypes.push(categories);
        wineTypes.push(names[0]);
        that.setData({
          categories: categories,
          names: names,
          wineTypes: wineTypes
        });
      },
      fail: function (e) {
        console.log("XHR失败");
      }
    });
    // 3. 获取招待类型
    wx.request({
      url: getZDURL,
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
      url: getFinderURL,
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
        info.approver = finders[0].realname;
        info.approveManOpenId = finders[0].openid;
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
            openId: wx.getStorageSync("openId"),
            department: department
          },
          success: function (res) {
            let budgetTotal = parseFloat(res.data.budget) || 0;
            let lastBudget = parseFloat(res.data.lastBudget) || 0;
            that.setData({
              budgetTotal: budgetTotal,
              lastBudget: lastBudget
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
    // 5. 联想
    util.getEntertainTypes(function (data) {
      let unitNames = data.unitNames;
      that.setData({
        unitNames: unitNames
      });
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
    if (util.timeHasPassed(value)) {
      util.showError(this, "时间不能早于今天");
      return false;
    }
    let info = this.data.info;
    info.applyTime = value;
    this.setData({
      info: info
    });
  },
  // 绑定实际时间
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

  // 当酒水类别改变的时候 第二列也改变
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
      showInputView: false,
      isShowCompanyMask: false
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
    let lastBudget = this.data.lastBudget;
    let temp = this.data.info;
    switch (dataset.item) {
      case "entertainNum":
      case "accompanyNum":
      case "perBudget":
        if (util.isInteger(accompanyNum) && util.isInteger(entertainNum) && util.isNumber(perBudget)) {
          let budgetTotal = this.data.budgetTotal;
          let masterBudget = parseFloat(perBudget) * (parseInt(entertainNum) + parseInt(accompanyNum))
          masterBudget = masterBudget.toFixed(1);
          if (masterBudget > budgetTotal) {
            util.showError(this, "已经超过总预算了");
            temp.perBudget = "";
            temp.masterBudget = 0;
            this.setData({
              info: temp
            });
            return false;
          } else if (masterBudget > lastBudget) {
            util.showError(this, "已经超过剩余预算了");
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
        // 输入自动补全
        if (value == "") {
          this.setData({
            showInputView: false,
            isShowCompanyMask: false
          });
          return false;
        }
        this.setData({
          bindSource: [],
          showInputView: false,
          isShowCompanyMask: false
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
          showInputView: bindSource.length > 0,
          isShowCompanyMask: bindSource.length > 0,
        });
    }
  },

  // 点击匹配项的遮罩层时隐藏遮罩层，隐藏匹配框
  hideCompanyMask: function () {
    this.setData({
      showInputView: false,
      isShowCompanyMask: false
    })
  },

  formSubmit: function (e) {
    let that = this;
    let info = this.data.info;
    let wines = info.wines;
    // 修正酒水类别，如果是其他，则修正
    for (let i = 0; i < wines.length; i++) {
      let wine = wines[i];
      // if (wine.category == "其他") {
      //   wine.name = wine.customWineType;
      // }
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
      if (util.isEmpty(e.name) || e.name.indexOf("请选择") > -1) {
        util.showError(that, "请选择酒水类型");
        return false;
      }
      if (util.isEmpty(e.category) || e.category.indexOf("请选择") > -1) {
        util.showError(that, "请选择酒水类型");
        return false;
      }
    }
    // 拼数据
    let obj = {
      number: "",
      department: info.department,
      applyTime: info.applyTime,
      entertainObject: info.entertainObject,
      entertainReason: info.entertainReason,
      entertainNum: info.entertainNum,
      accompanyNum: info.accompanyNum,
      perBudget: info.perBudget,
      masterBudget: info.masterBudget,
      entertainCategory: info.entertainCategory,
      manager: info.manager,
      approver: info.approver,
      approverOpenid: info.approveManOpenId,
      remark: info.remark,
      ifWine: info.ifWine,
      wineType: JSON.stringify(info.wines),
      wineNum: wineNum,
      ifBefore: info.ifBefore,
      beforeDate: info.beforeDate,
      openId: wx.getStorageSync('openId'),
      maxPerMoney: info.maxPerMoney
    };
    // util.goTo("/pages/operatePages/myApply/adaptable/entertain/entertain?item="+JSON.stringify(obj));
    // 前端校验
    let flag = this.checkData(obj);
    console.log("招待申请，点击了提交");

    // 发请求
    if (flag) {
      util.request(getMaxPerBudgetURL, {
        openId: wx.getStorageSync("openId"),
        entertainCategory: obj.entertainCategory,
        perBudget: obj.perBudget
      }, function (res) {
        if (res.data.data == false) {
          util.showError(that, "人均预算超过最大限额");
          return false;
        } else if (res.data.data == true) {
          // 防止表单重复提交
          if (info.hasCommited == undefined) {
            wx.request({
              url: addEntertainURL,
              method: "GET",
              data: obj,
              success: function (res) {
                let resultdata = res.data.msg;
                if (resultdata == "OK") {
                  util.goTo("/pages/publicPages/applyback/applyback");
                } else {
                  util.showError(that, "招待申请：服务器内部错误");
                  console.log("招待申请：服务器内部错误");
                  let hasCommited = undefined;
                  info.hasCommited = hasCommited;
                  that.setData({
                    info: info
                  });
                }
              },
              fail: function (res) {
                console.log("服务器连接失败");
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
          }
        } else {
          util.showError(that, "您已经提交，请勿重复提交");
          return false;
        }
      });





    }
  }
})