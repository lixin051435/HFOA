// pages/modulePages/seal/sealApply/sealApply.js
const app = getApp();
const util = require("../../../../../utils/util");

Page({

  /**
   * 页面的初始数据
   */
  data: {
    // 错误提示信息显隐
    showErrorMsg: false,
    showLeader3: false,
    showMoreInfo: false,
    // 公章类型
    showSealType: true,
    // 用印类型遮罩层
    showModal: false,
    // 已选公章id
    checkedSealId: [],
    // 已选公章name
    checkedSealName: [],
    // 已选公章grade
    checkedSealGrade: [],
    gzkind: '',
    gzkindName: '',
    // 获取选中项中的最大值
    max: 0,
    // 发往单位自动匹配
    //点击结果项之后替换到文本框的值
    toCompany: '',
    //本地匹配源
    adapterSource: [],
    //绑定到页面的数据，根据用户输入动态变化
    bindSource: [],
    isShowCompanyMask: false,
    showInputView: false,
    // 如果选择了合同专用章，显示合同金额输入框
    showContractAmount: false,
    // 合同金额大于50万，显示合同类型单选按钮
    showContractType: false,
    // 受托开始时间占位符
    showBeginTime: true,
    // 受托结束时间占位符
    showEndTime: true,
    // 受托期限
    timeLimit: 0,
    // 审批人下标
    leaderDIndex: 0,
    leaderBIndex: 0,
    leaderGIndex: 0,
    // 是否显示提交-点击后隐藏
    showSubmit: true,
    applyMan: wx.getStorageSync("username"),
    department: wx.getStorageSync("departmentName"),
    // 是否是身份证,身份证与护照的校验不同
    isSFZ: true,
    contractAmount: 0,
    contractType: "",
    isApprovedManEqual: false
  },

  // 文件密级Picker
  bindClassifiedChange: function (e) {
    this.setData({
      showClassified: false,
      classifiedListIndex: e.detail.value
    })
  },

  //当键盘输入时，触发input事件
  isInput: function (e) {
    // 输入内容为空时，不显示匹配框
    if (e.detail.value === "") {
      this.setData({
        showInputView: false,
        isShowCompanyMask: false
      })
      return false;
    }
    let prefix = e.detail.value //用户实时输入值
    let newSource = [] //匹配的结果
    if (prefix != "") {
      this.data.adapterSource.forEach(function (e) {
        if (e.indexOf(prefix) != -1) {
          newSource.push(e)
        }
      })
    }
    // 如果有匹配项，显示匹配框，否则不显示
    if (newSource.length != 0) {
      this.setData({
        bindSource: newSource,
        showInputView: true,
        isShowCompanyMask: true
      })
    } else {
      this.setData({
        bindSource: [],
        showInputView: false,
        isShowCompanyMask: false
      })
    }
  },

  // 选中资源项时，使用当前项作为输入值
  itemtap: function (e) {
    this.setData({
      toCompany: e.target.id,
      bindSource: [],
      showInputView: false,
      isShowCompanyMask: false
    })
  },

  // 点击匹配项的遮罩层时隐藏遮罩层，隐藏匹配框
  hideCompanyMask: function () {
    this.setData({
      showInputView: false,
      isShowCompanyMask: false
    })
  },

  // 受托人证件类型picker
  bindCertificateTypeChange: function (e) {
    if (e.detail.value === "护照") {
      this.setData({
        isSFZ: false,
        certificateTypeListIndex: e.detail.value
      })
    } else if (e.detail.value === "身份证") {
      this.setData({
        isSFZ: true,
        certificateTypeListIndex: e.detail.value
      })
    }
  },

  // 受托开始时间picker
  bindBeginTimeChange: function (e) {
    if (util.timeHasPassed(e.detail.value)) {
      util.showError(this, "时间不能早于今天");
      return false;
    } else {
      this.setData({
        showBeginTime: false,
        beginTime: e.detail.value
      })
      // 取到开始休假时间，以便后面判断提交的数据是否符合要求
      this.data.beginTime = e.detail.value;
    }
  },

  // 受托结束时间picker
  bindEndTimeChange: function (e) {
    // 计算已选天数
    if (this.data.beginTime === null || this.data.beginTime === "" || this.data.beginTime === undefined) {
      util.showError(this, '请先选择开始时间');
      return false;
    } else {
      this.data.timeLimit = util.getDaysByPickers(this.data.beginTime, e.detail.value);
      if (this.data.timeLimit <= 0) {
        util.showError(this, '开始时间应当早于结束时间');
        return false;
      } else {
        this.setData({
          showEndTime: false,
          endTime: e.detail.value,
          timeLimit: this.data.timeLimit
        })
      }
    }
  },

  // 借出时间picker
  bindBeginTimeChange2: function (e) {
    if (util.timeHasPassed(e.detail.value)) {
      util.showError(this, "时间不能早于今天");
      return false;
    } else {
      this.setData({
        borrowTime: e.detail.value
      })
      // 取到开始休假时间，以便后面判断提交的数据是否符合要求
      this.data.beginTime2 = e.detail.value;
    }
  },

  // 归还时间picker
  bindEndTimeChange2: function (e) {
    // 计算已选天数
    if (this.data.beginTime2 === null) {
      util.showError(this, '请先选择开始时间');
      return false;
    } else {
      this.data.offdays2 = util.getDaysByPickers(this.data.beginTime2, e.detail.value);
      if (this.data.offdays2 <= 0) {
        util.showError(this, '结束时间应当晚于开始时间');
        return false;
      } else {
        this.setData({
          returnTime: e.detail.value
        })
      }
    }
  },
  // 提交申请
  formSubmit: function (e) {
    let that = this;
    let thatValue = that.data;
    let submitValue = e.detail.value;

    // 去除字符串前后空格后再进行校验
    submitValue.toCompany = submitValue.toCompany.trim();
    submitValue.useSealNumber = submitValue.useSealNumber.trim();
    submitValue.useSealReason = submitValue.useSealReason.trim();

    if (submitValue.contractAmount !== undefined) {
      submitValue.contractAmount = submitValue.contractAmount.trim();
    }
    if (submitValue.trustee !== undefined) {
      submitValue.trustee = submitValue.trustee.trim();
    }
    if (submitValue.trusteeDuty !== undefined) {
      submitValue.trusteeDuty = submitValue.trusteeDuty.trim();
    }
    if (submitValue.entrustedcardnum !== undefined) {
      submitValue.entrustedcardnum = submitValue.entrustedcardnum.trim();
    }
    if (submitValue.entrustedMatters !== undefined) {
      submitValue.entrustedMatters = submitValue.entrustedMatters.trim();
    }


    // 先进行一定会有的三项值的非空校验->三项的正则校验
    if (submitValue.useSealNumber === "" || submitValue.toCompany === "" || submitValue.useSealReason === "") {
      util.showError(that, '请填写所有信息后提交');
    } else {
      if (submitValue.toCompany === "") {
        util.showError(that, '请输入发往单位');
        return false;
      }
      // 用印份数规范性判断
      submitValue.useSealNumber = parseInt(submitValue.useSealNumber);
      if (!util.isInteger(submitValue.useSealNumber)) {
        util.showError(that, '用印份数应为数字');
        return false;
      } else if (submitValue.useSealNumber === 0) {
        util.showError(that, '用印份数不能为0');
        return false;
      } else if (submitValue.useSealNumber > 0) {
        submitValue.useSealNumber = parseInt(submitValue.useSealNumber);
      }

      // 申请事由规范性判断
      if (submitValue.useSealReason.length < 5 || submitValue.useSealReason.length > 20) {
        util.showError(that, '申请事由请输入5-20个字符');
        return false;
      }

      // 所选公章级别最大为3
      if (that.data.max === 3) {
        // 进行更多信息的非空校验
        if (submitValue.trustee === "" || submitValue.trusteeDuty === "" || submitValue.entrustedcardnum === "" || submitValue.entrustedMatters === "" || submitValue.beginTime === null || submitValue.endTime === null || submitValue.timeLimit === 0) {
          util.showError(that, '请填写所有信息后提交');
          return false;
        }
        // 受托人证件号判断
        if (submitValue.certificateType === "身份证") {
          let regIdNo1 = /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/;
          if (!regIdNo1.test(submitValue.entrustedcardnum)) {
            util.showError(that, '身份证号码格式有误');
            return false;
          }
        } else if (submitValue.certificateType === "护照") {
          let regIdNo2 = /^((1[45]\d{7})|(G\d{8})|(P\d{7})|(S\d{7,8}))?$/;
          if (!regIdNo2.test(submitValue.entrustedcardnum)) {
            util.showError(that, '护照号码格式有误');
            return false;
          }
        }
      }

      // 判断是否重复提交
      if (that.data.hasCommited === true) {
        util.showError(that, "您已提交，请勿重复提交");
        return false;
      } else {
        // 发送申请信息到后台
        if (that.data.needTaskId == 1) {
          if (this.data.maxGrade == 1) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                taskId: that.data.taskId,
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          } else if (this.data.maxGrade == 2) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                taskId: that.data.taskId,
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          } else if (this.data.maxGrade == 3) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                taskId: that.data.taskId,
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                entrustedman: e.detail.value.trustee || "",
                entrustedpost: e.detail.value.trusteeDuty || "",
                entrustedcardtype: e.detail.value.certificateType || "",
                entrustedcardnum: e.detail.value.entrustedcardnum || "",
                entrustedmatter: e.detail.value.entrustedMatters || "",
                entrustedpermission: e.detail.value.timeLimit || "",
                entrustedstarttime: e.detail.value.beginTime || "",
                entrustedendtime: e.detail.value.endTime || "",
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          }
        } else if (that.data.needTaskId == 0) {
          if (this.data.maxGrade == 1) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          } else if (this.data.maxGrade == 2) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          } else if (this.data.maxGrade == 3) {
            wx.request({
              url: app.globalData.URL + '/print/reStartApply.action',
              header: {
                'content-type': 'application/json'
              },
              data: {
                id: that.data.id,
                applyusername: e.detail.value.applyMan,
                department: e.detail.value.department,
                departmentid: e.detail.value.department_id,
                gzId: that.data.sealTypeId,
                copies: e.detail.value.useSealNumber,
                sendto: e.detail.value.toCompany,
                contracAmount: submitValue.contractAmount || 0,
                contractType: submitValue.contractType || "",
                issecret: e.detail.value.classified,
                reason: e.detail.value.useSealReason,
                entrustedman: e.detail.value.trustee,
                entrustedpost: e.detail.value.trusteeDuty,
                entrustedcardtype: e.detail.value.certificateType,
                entrustedcardnum: e.detail.value.entrustedcardnum,
                entrustedmatter: e.detail.value.entrustedMatters,
                entrustedpermission: e.detail.value.timeLimit,
                entrustedstarttime: e.detail.value.beginTime,
                entrustedendtime: e.detail.value.endTime,
                approveman: e.detail.value.approveman || "",
                businessManager: e.detail.value.businessManager || "",
                confirmman: e.detail.value.confirmman || "",
                maxgarde: that.data.maxGrade,
                approvalLable: that.data.item.approvalLable,
                businessLabel: that.data.item.businessLabel,
                confirmLabel: that.data.item.confirmLabel,
                gzkind: that.data.item.gzkind,
                status: that.data.item.status,
                sorttime: that.data.item.sorttime
              },
              method: 'GET',
              success: function (res) {
                util.goTo("../../../../publicPages/applyback/applyback");
              },
              fail: function (res) {
                that.setData({
                  hasCommited: false,
                  showSubmit: true
                })
                console.log('发送用印申请数据信息--失败');
                util.showError(that, '网络错误,请稍后重试');
              }
            })
          }
        }
      }
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let item = JSON.parse(options.item);
    this.setData({
      item: item
    })
    let that = this;
    wx.request({
      url: app.globalData.URL + '/print/getApproval.action',
      header: {
        'content-type': 'application/json'
      },
      data: {
        openId: wx.getStorageSync('openId')
      },
      method: 'GET',
      success: function (res) {
        // 文件是否涉密
        if (item.issecret === "是") {
          that.setData({
            isBM: true
          })
        } else {
          that.setData({
            isBM: false
          })
        }

        // 证件类型
        if (item.contractType === "身份证") {
          that.setData({
            isSFZ: true
          })
        } else if (item.contractType === "护照") {
          that.setData({
            isSFZ: false
          })
        }

        // 如果为合同专用章，显示合同金额
        if (item.gzId === 3) {
          that.setData({
            contracAmount: item.contracAmount,
            showContractAmount: true
          })
        } else {
          that.setData({
            showContractAmount: false
          })
        }

        // 如果合同金额大于50万元，显示合同类型：甲方->甲方；null || ""->乙方
        if (item.contracAmount > 50) {
          if (item.contractType == '甲方') {
            that.setData({
              showContractType: true,
              contractType: item.contractType
            })
          } else if (item.contractType == null || item.contractType == "" || item.contractType == "乙方") {
            item.contractType = '乙方';
            that.setData({
              showContractType: true,
              contractType: item.contractType
            })
          }
        } else {
          that.setData({
            showContractType: false
          })
        }

        if (item.gzId === 8 || item.gzId === 9) {
          that.setData({
            showTime: true,
            borrowTime: item.borrowTime,
            returnTime: item.returnTime
          })
        } else {
          that.setData({
            showTime: false
          })
        }

        // 不论公章等级为1、2、3都需要的内容
        that.setData({
          adapterSource: res.data.unit,
          needTaskId: options.needTaskId,
          user_id: res.data.user.id,
          applyTime: item.applytime,
          // 修改前的数据回显
          taskId: item.taskId,
          id: item.id,
          sealTypeId: item.gzId,
          maxGrade: item.maxgarde,
          applyMan: item.applyusername,
          department: item.department,
          department_id: item.departmentid,
          sealType: item.gzkind,
          useSealNumber: item.copies,
          toCompany: item.sendto,
          useSealReason: item.reason,
          approveman: item.approveman,
          businessManager: item.businessManager || "",
          confirmman: item.confirmman || ""
        })
        if (item.maxgarde === 2) {
          that.setData({
            businessManager: item.businessManager,
            confirmman: item.confirmman
          })
        }
        if (item.maxgarde === 3) {
          if (item.entrustedcardtype === "身份证") {
            that.setData({
              isSFZ: true
            })
          } else {
            that.setData({
              isSFZ: false
            })
          }
          that.setData({
            showMoreInfo: true,
            trustee: item.entrustedman,
            trusteeDuty: item.entrustedpost,
            entrustedcardnum: item.entrustedcardnum,
            entrustedMatters: item.entrustedmatter,
            timeLimit: item.entrustedpermission,
            beginTime: item.entrustedstarttime,
            endTime: item.entrustedendtime,
            businessManager: item.businessManager,
            confirmman: item.confirmman
          })
        }

      },
      fail: function (res) {
        console.log('获取用印申请默认数据信息--失败');
        util.showError(that, '网络错误,请稍后重试');
      }
    });

    that.setData({
      item: item
    })

    // 如果二级审批人和三级审批人一样，只显示一个
    if (item.businessManager == item.confirmman) {
      that.setData({
        showLeader3: false,
        isApprovedManEqual: true
      })
    } else {
      that.setData({
        showLeader3: true,
        isApprovedManEqual: false
      })
    }
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

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})