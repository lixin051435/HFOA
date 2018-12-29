/**
 * 对wx.navigateTo方法进行封装，用于路由
 * @param {string} url 页面跳转url
 * @author lixin
 */
function goTo(url) {
  wx.navigateTo({
    url: url,
  })
}

/**
 * 错误信息提示-顶部
 * @param {object} that 当前Page的this指针
 * @param {string} msg 提示信息
 * @author lixin
 */
function showError(that, msg) {
  // 错误信息隐藏函数
  function ohShitfadeOut(which) {
    let fadeOutTimeout = setTimeout(() => {
      which.setData({
        showErrorMsg: false,
        errorMsg: ""
      });
      clearTimeout(fadeOutTimeout);
    }, 3000);
  }
  that.setData({
    showErrorMsg: true,
    errorMsg: msg
  });
  ohShitfadeOut(that);
  return false;
}

/**
 * 通过modal方式显示错误提示信息
 * @param {string} content 错误提示信息
 * @author lixin
 */
function showErrorByModal(content) {
  wx.showModal({
    title: '',
    content: content,
    showCancel: false,
    success: function () {}
  })
}
/**
 * 是否确认撤回 提示框  
 * @param {Function} callback 点击modal确定的回调函数
 * @author lixin
 */
function confirmRevoke(callback) {
  wx.showModal({
    title: "提示",
    content: "是否确认撤回?",
    showCancel: true,
    success: function (res) {
      if (res.confirm) {
        callback();
      } else if (res.cancel) {
        // console.log('用户点击取消')
      }
    }
  });
}
/**
 * 获取当前时间的年月日
 */
function getNowTimeDate() {
  let now = new Date();
  let year = now.getFullYear();
  let month = now.getMonth() + 1;
  let date = now.getDate();
  return year + "-" + month + "-" + date;
}

// 获取当前年-月-日 时：分：秒
function getNowFormatDate() {
  let date = new Date();
  let seperator1 = "-";
  let seperator2 = ":";
  let month = date.getMonth() + 1;
  let strDate = date.getDate();
  let strHours = date.getHours();
  let strMinutes = date.getMinutes();
  let strSeconds = date.getSeconds();
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }
  if (strDate >= 0 && strDate <= 9) {
    strDate = "0" + strDate;
  }
  if (strHours >= 0 && strHours <= 9) {
    strHours = "0" + strHours;
  }
  if (strMinutes >= 0 && strMinutes <= 9) {
    strMinutes = "0" + strMinutes;
  }
  if (strSeconds >= 0 && strSeconds <= 9) {
    strSeconds = "0" + strSeconds;
  }
  let currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate + " " + strHours + seperator2 + strMinutes + seperator2 + strSeconds;
  return currentdate;
}

/**
 * 判断data是否为null undefined ""
 * @param {object} data 要判断的对象
 */
function isEmpty(data) {
  if (data == null || data == "" || data == undefined) {
    return true;
  }
  if (typeof (data) == "string") {
    if (data.trim() == "") {
      return true;
    }
  }
  return false;
}

/**
 * 判断str是否是数字,而且是大于零的数字
 * @param {string} str 
 * @author lixin
 */
function isNumber(str) {
  let re = /^[0-9]+\.?[0-9]*$/;
  if (re.test(str))
    return true;
  else
    return false;
}

/**
 * 判断str是否是大于等于零的整数
 * @param {string} str 
 * @author lixin
 */
function isInteger(str) {
  let re = /^[0-9]*$/;
  if (re.test(str))
    return true;
  else
    return false;
}

/**
 * 判断str是否是汉字
 * @param {string} str 
 * @author lixin
 * 注：正则可能有问题，尚未确认
 */
function isChinese(str) {
  let re = /^[\u4e00-\u9fa5]+$/;
  if (re.test(str))
    return true;
  else
    return false;
}

// 判断选择的两个时间是否交叉
function isOverLapping(beginTime1, endTime1, beginTime2, endTime2) {
  let beginTime2Int = new Date(beginTime2).getTime();
  let endTime1Int = new Date(endTime1).getTime();
  if (beginTime2Int <= endTime1Int) {
    return true;
  } else {
    return false;
  }
}

// 判断选择的日期是否在今天之前
function timeHasPassed(selectedTime) {
  let date = new Date();
  let Y = date.getFullYear();
  let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
  let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
  let nowDate = Y + "-" + M + "-" + D;
  if (new Date(selectedTime).getTime() < new Date(nowDate).getTime()) {
    return true;
  } else {
    return false;
  }
}

// 判断选择的日期是否在今天之前
function timeHasPassed1(selectedTime) {
  let date = new Date();
  if (new Date(selectedTime).getTime() < new Date(date).getTime()) {
    return true;
  } else {
    return false;
  }
}

// 判断选择的日期是否在今天之前,今天也不可以
function afterToday(selectedTime) {
  let date = new Date();
  let Y = date.getFullYear();
  let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
  let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
  let nowDate = Y + "-" + M + "-" + D;
  if (new Date(selectedTime).getTime() <= new Date(nowDate).getTime()) {
    return true;
  } else {
    return false;
  }
}

/**
 * 
 * @param {string} time1 2018-10-30
 * @param {string} time2 2015-20-05
 * @return time1 > time2 ? true : false
 * @author lixin
 */
function compare(time1, time2) {
  let time1Date = new Date(time1).getTime();
  let time2Date = new Date(time2).getTime();
  return time1Date > time2Date;
}

function compareToday(time1, time2) {
  let time1Date = new Date(time1).getTime();
  let time2Date = new Date(time2).getTime();
  return time1Date >= time2Date;
}

/**
 * 计算begin到end的天数，包括begin本身
 * @param {string} begin YYYY-MM-DD 开始时间  
 * @param {string} end YYYY-MM-DD 结束时间
 * @author lixin
 */
function getDaysByPickers(begin, end) {
  return (new Date(end).getTime() - new Date(begin).getTime()) / (24 * 60 * 60 * 1000) + 1;
}

/**
 * @returns {beginTime,endTime} 本年的一月一号和今天 YYYY-MM-DD格式
 * @author lixin
 */
function getBeginAndEndDate() {
  let date = new Date();
  let Y = date.getFullYear();
  let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1);
  let D = date.getDate() < 10 ? '0' + date.getDate() : date.getDate();
  return {
    beginTime: Y + '-01-01',
    endTime: Y + "-" + M + "-" + D
  };
}
/**
 * 加载框
 * @param {string} title loading框title
 * @param {int} milliseconds loading框持续时间
 * @author lixin
 */
function showLoading(title, milliseconds) {
  milliseconds = milliseconds || 10000;
  title = title || "正在提交...";
  wx.showLoading({
    title: title,
  })
  setTimeout(function () {
    wx.hideLoading();
  }, milliseconds)
}

/**
 * 公共模块：根据openId获取可查询的部门列表
 * @param {function} callback 回调函数
 * @author lixin
 */
function getDepartmentList(callback) {
  wx.request({
    url: getApp().globalData.URL + "/user/departmentopenId",
    method: "GET",
    data: {
      "openId": wx.getStorageSync('openId')
    },
    success: function (res) {
      // List<Object>
      let list = res.data.data || [];
      // List<String>
      let departmentNameList = [];
      list.forEach(element => {
        departmentNameList.push(element.departmentname);
      });
      let data = {
        departmentNameList: departmentNameList,
        departmentObjectList: list
      };
      callback(data);
    },
    fail: function (res) {
      console.log("获取已审批列表--fail");
    }
  });
}

/**
 * 公共模块：获取个人信息
 * @param {function} callback 回调函数
 * @author lixin
 */
function getInfo(callback) {
  wx.request({
    url: getApp().globalData.URL + "/user/toLogin.action",
    method: "GET",
    data: {
      openId: wx.getStorageSync('openId')
    },
    success: function (res) {
      //  0是普通员工 data表示是否是普通员工
      let data = res.data.data.userEntity.duty == "0";
      let realname = res.data.data.userEntity.realname;
      callback(data, realname);
    },
    fail: function (res) {
      console.log("获取已审批列表--fail");
    }
  });
}

/**
 * 公共模块：获取用印信息
 * @param {function} callback 回调函数
 * @author lixin
 */
function getSealTypes(callback) {
  wx.request({
    url: getApp().globalData.URL + "/print/getPrintingType",
    method: "GET",
    success: function (res) {
      let objectList = res.data || [];
      let list = [];
      objectList.forEach(element => {
        list.push(element.gzkind);
      });
      let data = {
        objectList: objectList,
        nameList: list
      };
      callback(data);
    },
    fail: function (res) {
      console.log("获取已审批列表--fail");
    }
  });

}
/**
 * 公共模块：获取公车车辆信息
 * @param {function} callback 回调函数
 * @author lixin
 */
function getCarInfo(callback) {
  wx.request({
    url: getApp().globalData.URL + "/CommonCar/getAllCar",
    method: "GET",
    success: function (res) {
      let carObjectList = res.data || [];
      let carNames = [];
      carObjectList.forEach(element => {
        carNames.push(element.carNum);
      });
      let data = {
        carObjectList: carObjectList,
        carNames: carNames
      };
      callback(data);

    },
    fail: function (res) {
      console.log("获取已审批列表--fail");
    }
  });
}

/**
 * 公共模块：获取招待单位
 * @param {function} callback 回调函数
 * @author lixin
 */
function getEntertainTypes(callback) {
  wx.request({
    url: getApp().globalData.URL + "/entertain/getType",
    method: "GET",
    success: function (res) {
      let list = res.data;
      let unitNames = [];
      list.forEach(element => {
        unitNames.push(element.oName);
      });
      let data = {
        unitObjectList: list,
        unitNames: unitNames
      };
      callback(data);
    },
    fail: function (res) {
      console.log("获取已审批列表--fail");
    }
  });
}

/**
 * 对象的深拷贝
 * @param {object} obj 要拷贝的对象
 * @returns {object} obj的深拷贝对象
 * @author lixin
 */
function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

/**
 * 封装wx.request get请求
 * @param {string} url 请求的URL
 * @param {Object}}} data 请求参数
 * @param {Function} successCallback 成功的回调函数
 * @param {Function} failCallback 失败的回调函数
 * @author lixin
 */
function request(url, data, successCallback, failCallback) {
  data = data || [];
  wx.request({
    url: url,
    data: data,
    method: "GET",
    success: function (res) {
      successCallback(res);
    },
    fail: function () {
      if (failCallback != undefined) {
        failCallback();
      } else {
        console.log("XHR错误");
      }
    }
  });
}

/**
 * 对get请求的封装
 * @param {string} url url
 * @param {string} data data
 * @param {Function} successCallback 成功回调函数
 * @param {Function} failCallback 失败回调函数
 */
function get(url, data, successCallback, failCallback) {
  wx.request({
    url: url,
    data: data,
    method: "GET",
    success: function (res) {
      successCallback(res);
    },
    fail: function () {
      if (failCallback != null) {
        failCallback();
      } else {
        console.log("XHR错误");
      }
    }
  });
}

function post(url, data, successCallback, failCallback) {
  // 模拟器默认是content-type
  // 真机默认是Content-Type

  for (let key in data) {
    data[key] = JSON.stringify(data[key]);
  }
  wx.request({
    url: url,
    data: data,
    header: {
      "Content-Type": "application/x-www-form-urlencoded"
    },
    method: "POST",
    success: function (res) {
      successCallback(res);
    },
    fail: function () {
      if (failCallback != null) {
        failCallback();
      } else {
        console.log("XHR错误");
      }
    }
  });
}

/**
 * 根据http状态码显示错误信息
 * @param {object} res 网络请求返回值
 * @param {object} that page指针
 * @param {string} msg 显示页面信息
 * @author lixin
 */
function showNetworkError(res, that, msg = "") {
  if (msg == "") {
    if (res.statusCode >= 400 && res.statusCode <= 499) {
      showError(that, "请求参数错误");
    } else if (res.statusCode >= 500 && res.statusCode <= 599) {
      showError(that, "服务器内部错误");
    }
  } else {
    if (res.statusCode >= 400 && res.statusCode <= 499) {
      showError(that, msg + ":请求参数错误");
    } else if (res.statusCode >= 500 && res.statusCode <= 599) {
      showError(that, msg + ":服务器内部错误");
    }
  }

}


module.exports = {
  goTo: goTo,
  showError: showError,
  showErrorByModal: showErrorByModal,
  getNowFormatDate: getNowFormatDate,
  isEmpty: isEmpty,
  isNumber: isNumber,
  isInteger: isInteger,
  isChinese: isChinese,
  isOverLapping: isOverLapping,
  timeHasPassed: timeHasPassed,
  afterToday: afterToday,
  getDaysByPickers: getDaysByPickers,
  getBeginAndEndDate: getBeginAndEndDate,
  compare: compare,
  compareToday: compareToday,
  showLoading: showLoading,
  timeHasPassed1: timeHasPassed1,
  getDepartmentList: getDepartmentList,
  getInfo: getInfo,
  getSealTypes: getSealTypes,
  getCarInfo: getCarInfo,
  getEntertainTypes: getEntertainTypes,
  deepClone: deepClone,
  request: request,
  confirmRevoke: confirmRevoke,
  get: get,
  post: post,
  getNowTimeDate: getNowTimeDate,
  showNetworkError: showNetworkError
}