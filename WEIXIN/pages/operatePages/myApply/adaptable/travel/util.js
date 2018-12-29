const util = require("../../../../../utils/util.js");

/**
 * 
 * @param {抄送人list} arr 
 * 数组转字符串，用逗号隔开，抄送人用到
 */
function arrayToString(arr) {
  let msg = "";
  if (Array.isArray(arr)) {
    if (arr.length > 0) {
      msg = arr[0];
      for (let i = 1; i < arr.length; i++) {
        msg = msg + "," + arr[i];
      }
    }
  }
  return msg;
}

/**
 * 用于格式化出行细节，给后台
 * @param {出发地} start 
 * @param {目的地} end 
 * @param {出行方式} model 
 */
function generateTravelObject(start, end, model) {
  return start + "," + end + "," + model;
}

// 根据真实姓名 返回openId 在抄送人和审批人用到
function getOpenIdByName(realname, finderObjectList) {
  for (let i = 0; i < finderObjectList.length; i++) {
    if (finderObjectList[i].realname == realname) {
      return finderObjectList[i].openid;
    }
  }
  return null;
}

module.exports = {
  getOpenIdByName: getOpenIdByName,
  generateTravelObject: generateTravelObject,
  arrayToString: arrayToString,
}