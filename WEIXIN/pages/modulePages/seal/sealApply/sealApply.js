// pages/modulePages/seal/sealApply/sealApply.js
const app = getApp();
const util = require("../../../../utils/util");

Page({

	/**
	 * 页面的初始数据
	 */
	data: {

		// 错误提示信息显隐
		showErrorMsg: false,
		showMoreLeader: false,
		showLeader2: false,
		showLeader3: false,
		showMoreInfo: false,
		// 公章类型
		sealType: '',
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
		contractTypeA: true,
		contractAmountValue: null,
		contractTypeValue: "甲方",
		ifSelect: false,
		showTime: false,
		showBeginTime2: true,
		showEndTime2: true,
		isApprovedManEqual: false,
		approvalMan0: null,
		approvalMan1: null,
		count: 0,
		isOnlyFYJ: false
	},

	/**
	 * 弹窗
	 */
	showDialogBtn: function () {
		this.setData({
			showModal: true
		})
	},

	// 隐藏模态对话框
	hideModal: function () {
		this.setData({
			showModal: false
		});
	},

	// 对话框取消按钮点击事件
	onCancel: function () {
		let that = this;
		let thatValue = that.data;
		let loadInMax = thatValue.max;

		let sealTypeList = this.data.sealTypeList;
		let gzkind = this.data.sealType || "";
		sealTypeList.forEach(function (e) {
			e.checked = false;
		});
		if (gzkind !== "") {
			gzkind = gzkind.split(",");
			for (let i = 0; i < gzkind.length; i++) {
				for (let j = 0; j < sealTypeList.length; j++) {
					let e = sealTypeList[j];
					if (e.gzkind == gzkind[i]) {
						e.checked = true;
					}
				}
			}
		}

		// 判断显示的页面内容
		switch (loadInMax) {
			case 1:
				this.setData({
					showMoreLeader: false,
					showMoreInfo: false
				})
				break;
			case 2:
				this.setData({
					showMoreLeader: true,
					showMoreInfo: false
				})
				break;
			case 3:
				this.setData({
					showMoreLeader: true,
					showMoreInfo: true
				})
				break;
		}
		this.setData({
			sealTypeList: sealTypeList
		});
		console.log(this.data.sealType);
		this.hideModal();
	},

	// 对话框确认按钮点击事件
	onConfirm: function () {
		// console.log("onConfirm");
		// console.log(this.data.sealTypeList);
		let that = this;
		let thatValue = that.data;
		// 如果选择了合同专用章，显示合同金额输入框
		that.setData({
			showContractAmount: false,
			showTime: false
		})
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 3) {
				that.setData({
					showContractAmount: true
				})
				break;
			}
		}

		// 针对薪酬会计，只有申请公章时才可以选择xx为审批人，其余情况不可以
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 1) {
				if (thatValue.checkedSealId.length == 1) {
					// 只选择了一个公章，显示审批人为approvalMan0
					that.setData({
						departmentManagerList: that.data.approvalMan0
					})
				} else {
					// 不符合条件，显示审批人为approvalMan1
					that.setData({
						departmentManagerList: that.data.approvalMan1
					})
					that.data.leaderDIndex = parseInt(that.data.leaderDIndex);
					if (that.data.leaderDIndex >= that.data.departmentManagerList.length) {
						that.data.leaderDIndex = 0;
						that.setData({
							leaderDIndex: that.data.leaderDIndex
						})
					}
				}
			}
		}

		// 如果只选择了合同专用章且为普通员工，审批人除了部门经理还有滕沫
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 3) {
				// 只选择了一个合同专用章
				if (thatValue.checkedSealId.length == 1) {
					// 只选择了一个合同专用章，显示审批人多一个滕沫
					wx.request({
						url: app.globalData.URL + '/print/getApprovalmanForHT',
						header: {
							'Content-Type': 'application/json'
						},
						data: {
							openId: wx.getStorageSync('openId')
						},
						success: function (res) {
							that.setData({
								departmentManagerList: res.data.approvalMan1
							})
						}
					})
					break;
				} else {
					// 不只选择了一个合同专用章，再把审批人都换成原来的
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
							that.setData({
								departmentManagerList: res.data.approvalMan1,
								businessExecutivesList: res.data.approvalMan2 || "",
								generalManagerList: res.data.approvalMan3 || "",
								approvalMan0: res.data.approvalMan0 || "",
								approvalMan1: res.data.approvalMan1,
								// 审批人下标
								leaderDIndex: 0,
								leaderBIndex: 0,
								leaderGIndex: 0,
							})
							// 每一次进入用印申请页面时，默认没有选择公章
							// for (let i = 0; i < that.data.sealTypeList.length; i++) {
							// 	that.data.sealTypeList[i].checked = false;
							// }
							if (res.data.ifSelect == true) {
								// 获取下一个审批人
								if (that.data.departmentManagerList[that.data.leaderDIndex].id != 3) {
									wx.request({
										url: app.globalData.URL + '/print/getNextAppriovalMan.action',
										header: {
											'content-type': 'application/json'
										},
										data: {
											userId: that.data.departmentManagerList[that.data.leaderDIndex].id
										},
										method: 'GET',
										success: function (res) {
											that.setData({
												businessExecutivesList: res.data.approvalMan2 || "",
												generalManagerList: res.data.approvalMan3 || "",
											})
										},
										fail: function (res) {
											console.log('获取下一个审批人数据信息--失败');
											util.showError(that, '网络错误,请稍后重试');
										}
									})
								}
							} else {
								console.log("不触发获取下一个审批人事件");
							}
							// 判断最多有几个审批人
							if (res.data.approvalCount === 1) {
								that.setData({
									showLeader2: false,
									showLeader3: false
								})
							} else if (res.data.approvalCount === 2) {
								that.setData({
									showLeader2: true,
									showLeader3: false
								})
							} else if (res.data.approvalCount === 3) {
								that.setData({
									showLeader2: true,
									showLeader3: true
								})
							}
						},
						fail: function (res) {
							console.log('获取用印申请默认数据信息--失败');
							util.showError(that, '网络错误,请稍后重试');
						}
					})
				}
			}
		}

		// 如果选择了营业执照原件或者公章外带，显示借出时间和归还时间
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 8 || thatValue.checkedSealId[i] === 9) {
				that.setData({
					showTime: true
				})
				break;
			}
		}

		// 只选择了法人章时，审批人直接为王总
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 5) {
				if (thatValue.checkedSealId.length == 1) {
					// 只选择了法人章，审批人直接为王总
					wx.request({
						url: app.globalData.URL + '/print/getManager',
						header: {
							'Content-Type': 'application/json'
						},
						success: function (res) {
							that.setData({
								departmentManagerList: res.data.approvalMan1,
								businessExecutivesList: [],
								generalManagerList: [],
								showMoreLeader: false
							})
						}
					})
				} else {
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
							wx.showModal({
								title: '提示',
								content: '法人章审批人为总经理',
								showCancel: false,
								confirmText: '确定'
							});
							that.setData({
								departmentManagerList: res.data.approvalMan1,
								businessExecutivesList: res.data.approvalMan2 || "",
								generalManagerList: res.data.approvalMan3 || "",
								approvalMan0: res.data.approvalMan0 || "",
								approvalMan1: res.data.approvalMan1,
								// 审批人下标
								leaderDIndex: 0,
								leaderBIndex: 0,
								leaderGIndex: 0,
								showMoreLeader: true
							})
							// 每一次进入用印申请页面时，默认没有选择公章
							// for (let i = 0; i < that.data.sealTypeList.length; i++) {
							// 	that.data.sealTypeList[i].checked = false;
							// }
							if (res.data.ifSelect == true) {
								// 获取下一个审批人
								if (that.data.departmentManagerList[that.data.leaderDIndex].id != 3) {
									wx.request({
										url: app.globalData.URL + '/print/getNextAppriovalMan.action',
										header: {
											'content-type': 'application/json'
										},
										data: {
											userId: that.data.departmentManagerList[that.data.leaderDIndex].id
										},
										method: 'GET',
										success: function (res) {
											that.setData({
												businessExecutivesList: res.data.approvalMan2 || "",
												generalManagerList: res.data.approvalMan3 || "",
											})
										},
										fail: function (res) {
											console.log('获取下一个审批人数据信息--失败');
											util.showError(that, '网络错误,请稍后重试');
										}
									})
								}
							} else {
								console.log("不触发获取下一个审批人事件");
							}
							// 判断最多有几个审批人
							if (res.data.approvalCount === 1) {
								that.setData({
									showLeader2: false,
									showLeader3: false
								})
							} else if (res.data.approvalCount === 2) {
								that.setData({
									showLeader2: true,
									showLeader3: false
								})
							} else if (res.data.approvalCount === 3) {
								that.setData({
									showLeader2: true,
									showLeader3: true
								})
							}
						},
						fail: function (res) {
							console.log('获取用印申请默认数据信息--失败');
							util.showError(that, '网络错误,请稍后重试');
						}
					})
				}
			}
		}

		// 获取选择的最大的grade,即max,用以判断显示页面数据
		thatValue.max = 0;
		let count = 0;
		for (let i = 0; i < thatValue.checkedSealGrade.length; i++) {
			if (thatValue.max < thatValue.checkedSealGrade[i]) {
				thatValue.max = thatValue.checkedSealGrade[i]
			}
			if (thatValue.checkedSealGrade[i] == 2) {
				count++;
			}
		}
		that.setData({
			count: count
		})

		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 6) {
				if (thatValue.checkedSealId.length != 1) {
					wx.showModal({
						title: '提示',
						content: '法人身份证复印件二级审批人为综合办公室主任',
						showCancel: false,
						confirmText: '确定'
					});
				}
			}
		}

		// 如果选择了法人身份证复印件，审批人为邓主任
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 6 && thatValue.max == 2 && thatValue.count == 1 && thatValue.contractAmountValue < 50) {
				// 选择了法人身份证复印件，审批人为邓主任
				wx.request({
					url: app.globalData.URL + '/print/getComOfficeLeader',
					header: {
						'Content-Type': 'application/json'
					},
					success: function (res) {
						that.setData({
							departmentManagerList: that.data.approvalMan1,
							businessExecutivesList: res.data.approvalMan2 || "",
							generalManagerList: res.data.approvalMan3 || "",
						})
					}
				})
				break;
			} else {
				if (that.data.departmentManagerList[that.data.leaderDIndex].id != 3) {
					if (that.data.ifSelect == true) {
						wx.request({
							url: app.globalData.URL + '/print/getNextAppriovalMan.action',
							header: {
								'content-type': 'application/json'
							},
							data: {
								userId: that.data.departmentManagerList[that.data.leaderDIndex].id
							},
							method: 'GET',
							success: function (res) {
								that.setData({
									departmentManagerList: that.data.approvalMan1,
									businessExecutivesList: res.data.approvalMan2 || "",
									generalManagerList: res.data.approvalMan3 || "",
								})
							},
							fail: function (res) {
								console.log('获取下一个审批人数据信息--失败');
								util.showError(that, '网络错误,请稍后重试');
							}
						})
					} else {
						console.log('不触发获取下一个审批人事件');
					}
				}
			}
		}

		// 判断显示的页面内容
		switch (thatValue.max) {
			case 1:
				this.setData({
					showMoreLeader: false,
					showMoreInfo: false
				})
				break;
			case 2:
				this.setData({
					showMoreLeader: true,
					showMoreInfo: false
				})
				break;
			case 3:
				this.setData({
					showMoreLeader: true,
					showMoreInfo: true
				})
				break;
		}
		let seals = [];
		let sealTypeList = this.data.sealTypeList;
		sealTypeList.forEach(function (e) {
			if (e.checked) {
				seals.push(e.gzkind);
			}
		});
		this.setData({
			sealType: seals.join(",")
		})
		// console.log(this.data.sealType);
		this.hideModal();
	},

	// 合同金额大于50万，审批人到第三级
	contractAmountInput: function (e) {
		let that = this;
		let thatValue = that.data;
		let contractAmountValue = parseInt(e.detail.value.trim());

		// 因为默认选中了合同类型为甲方，但是会根据金额等条件判断是否显示合同类型和多个审批人，所以在此处加一层判断用来回显正确的合同类型，进而显示正确的审批人
		if (that.data.contractTypeValue == "甲方") {
			that.setData({
				contractTypeA: true
			})
		} else {
			that.setData({
				contractTypeA: false
			})
		}

		// 合同金额大于50时，显示合同类型
		if (contractAmountValue >= 50) {
			this.setData({
				showMoreLeader: true,
				showContractType: true,
				contractAmount: contractAmountValue,
				contractAmountValue: contractAmountValue
			})
		} else {
			this.setData({
				showMoreLeader: false,
				showContractType: false,
				contractAmount: contractAmountValue,
				contractAmountValue: contractAmountValue
			})
		}

		// 合同金额大于50并且合同类型为甲方时，显示多个审批人
		if (that.data.contractAmountValue >= 50 && that.data.contractTypeValue == "甲方") {
			if (that.data.departmentManagerList[that.data.leaderDIndex].id != 3) {
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
						that.setData({
							departmentManagerList: res.data.approvalMan1,
							businessExecutivesList: res.data.approvalMan2 || "",
							generalManagerList: res.data.approvalMan3 || "",
							approvalMan0: res.data.approvalMan0 || "",
							approvalMan1: res.data.approvalMan1,
							// 审批人下标
							leaderDIndex: 0,
							leaderBIndex: 0,
							leaderGIndex: 0,
						})
						// 每一次进入用印申请页面时，默认没有选择公章
						// for (let i = 0; i < that.data.sealTypeList.length; i++) {
						// 	that.data.sealTypeList[i].checked = false;
						// }
						if (res.data.ifSelect == true) {
							// 获取下一个审批人
							if (that.data.departmentManagerList[that.data.leaderDIndex].id != 3) {
								wx.request({
									url: app.globalData.URL + '/print/getNextAppriovalMan.action',
									header: {
										'content-type': 'application/json'
									},
									data: {
										userId: that.data.departmentManagerList[that.data.leaderDIndex].id
									},
									method: 'GET',
									success: function (res) {
										that.setData({
											businessExecutivesList: res.data.approvalMan2 || "",
											generalManagerList: res.data.approvalMan3 || "",
										})
									},
									fail: function (res) {
										console.log('获取下一个审批人数据信息--失败');
										util.showError(that, '网络错误,请稍后重试');
									}
								})
							}
						} else {
							console.log("不触发获取下一个审批人事件");
						}
						// 判断最多有几个审批人
						if (res.data.approvalCount === 1) {
							that.setData({
								showLeader2: false,
								showLeader3: false
							})
						} else if (res.data.approvalCount === 2) {
							that.setData({
								showLeader2: true,
								showLeader3: false
							})
						} else if (res.data.approvalCount === 3) {
							that.setData({
								showLeader2: true,
								showLeader3: true
							})
						}
					},
					fail: function (res) {
						console.log('获取用印申请默认数据信息--失败');
						util.showError(that, '网络错误,请稍后重试');
					}
				})
			}
		} else {
			for (let i = 0; i < thatValue.checkedSealId.length; i++) {
				if (thatValue.checkedSealId[i] == 6 && thatValue.max == 2 && thatValue.count == 1 && thatValue.contractAmountValue < 50) {
					wx.request({
						url: app.globalData.URL + '/print/getComOfficeLeader',
						header: {
							'Content-Type': 'application/json'
						},
						success: function (res) {
							that.setData({
								businessExecutivesList: res.data.approvalMan2 || "",
								generalManagerList: res.data.approvalMan3 || "",
							})
						}
					})
					break;
				}
			}
			that.setData({
				showMoreLeader: false
			})
		}

		// 获取选择的最大的grade,即max,用以判断显示页面数据
		thatValue.max = 0;
		for (let i = 0; i < thatValue.checkedSealGrade.length; i++) {
			if (thatValue.max < thatValue.checkedSealGrade[i]) {
				thatValue.max = thatValue.checkedSealGrade[i]
			}
		}
		if (thatValue.max > 1) {
			// 判断显示的页面内容
			switch (thatValue.max) {
				case 1:
					this.setData({
						showMoreLeader: false,
						showMoreInfo: false
					})
					break;
				case 2:
					this.setData({
						showMoreLeader: true,
						showMoreInfo: false
					})
					break;
				case 3:
					this.setData({
						showMoreLeader: true,
						showMoreInfo: true
					})
					break;
			}
		}
	},

	// 公章类型多选
	checkboxChange: function (e) {
		let that = this;
		let thatValue = that.data;
		let submitValue = e.detail.value;

		// 每一次进入用印申请页面时，默认没有选择公章
		for (let i = 0; i < that.data.sealTypeList.length; i++) {
			that.data.sealTypeList[i].checked = false;
		}

		// 若之前已选择公章，将其回显
		let indexes = e.detail.value;
		let sealTypeList = this.data.sealTypeList;
		
		for (let i = 0; i < indexes.length; i++) {
			indexes[i] = parseInt(indexes[i]);
			sealTypeList[indexes[i] - 1].checked = true;
		}
		// 直接将整个list赋值回去
		this.setData({
			sealTypeList: sealTypeList
		})
		// 清空已选数组
		this.setData({
			checkedSealId: [],
			checkedSealName: [],
			checkedSealGrade: []
		})

		// 得到选择的公章name和grade
		for (let i = 0; i < submitValue.length; i++) {
			thatValue.checkedSealId.push(thatValue.sealTypeList[submitValue[i] - 1].id);
			thatValue.checkedSealName.push(thatValue.sealTypeList[submitValue[i] - 1].gzkind);
			thatValue.checkedSealGrade.push(thatValue.sealTypeList[submitValue[i] - 1].grade);
		}
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] != 3) {
				that.setData({
					showContractType: false
				})
			}
		}
		// 将name数组转换成字符串
		thatValue.gzkind = thatValue.checkedSealId.join(","); // 应该写到onConfirm事件中
		thatValue.gzkindName = thatValue.checkedSealName.join(","); // 实际没用
		thatValue.gzkindGrade = thatValue.checkedSealGrade.join(",");// 实际没用到

		// console.log("==");
		// console.log(this.data.sealTypeList);
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

	// 合同类型picker
	bindContractTypeChange: function (e) {
		let that = this;
		let thatValue = that.data;
		this.setData({
			contractTypeListIndex: e.detail.value,
			contractTypeValue: e.detail.value
		})
		if (that.data.contractAmountValue >= 50 && that.data.contractTypeValue == "甲方") {
			that.setData({
				showMoreLeader: true
			})
		} else {
			that.setData({
				showMoreLeader: false
			})
		}

		// 获取选择的最大的grade,即max,用以判断显示页面数据
		thatValue.max = 0;
		for (let i = 0; i < thatValue.checkedSealGrade.length; i++) {
			if (thatValue.max < thatValue.checkedSealGrade[i]) {
				thatValue.max = thatValue.checkedSealGrade[i]
			}
		}
		if (thatValue.max > 1) {
			// 判断显示的页面内容
			switch (thatValue.max) {
				case 1:
					this.setData({
						showMoreLeader: false,
						showMoreInfo: false
					})
					break;
				case 2:
					this.setData({
						showMoreLeader: true,
						showMoreInfo: false
					})
					break;
				case 3:
					this.setData({
						showMoreLeader: true,
						showMoreInfo: true
					})
					break;
			}
		}
	},

	// 文件密级Picker
	bindClassifiedChange: function (e) {
		this.setData({
			classifiedListIndex: e.detail.value
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
		let beginTime = e.detail.value;
		let endTime = this.data.endTime;
		if (endTime != null) {
			if (util.timeHasPassed(beginTime)) {
				util.showError(this, "时间不能早于今天");
				return false;
			}
			if (new Date(beginTime).getTime() > new Date(endTime).getTime()) {
				util.showError(this, "开始时间应当早于结束时间");
				return false;
			} else {
				this.setData({
					showBeginTime: false,
					beginTime: beginTime,
					timeLimit: util.getDaysByPickers(beginTime, endTime)
				});
			}
		} else {
			if (util.timeHasPassed(beginTime)) {
				util.showError(this, "时间不能早于今天");
				return false;
			} else {
				this.setData({
					showBeginTime: false,
					beginTime: beginTime
				})
			}
		}
	},

	// 受托结束时间picker
	bindEndTimeChange: function (e) {
		let endTime = e.detail.value;
		let beginTime = this.data.beginTime;
		// 计算已选天数
		if (beginTime == null || beginTime == "") {
			util.showError(this, '请先选择开始时间');
			return false;
		} else {
			if (util.timeHasPassed(endTime)) {
				util.showError(this, "时间不能早于今天");
				return false;
			}
			if (new Date(beginTime).getTime() > new Date(endTime).getTime()) {
				util.showError(this, '开始时间应当早于结束时间');
				return false;
			} else {
				this.setData({
					showEndTime: false,
					endTime: endTime,
					timeLimit: util.getDaysByPickers(beginTime, endTime)
				})
			}
		}
	},

	// 借出时间picker
	bindBeginTimeChange2: function (e) {
		let beginTime2 = e.detail.value;
		let endTime2 = this.data.endTime2;
		if (endTime2 != null) {
			if (util.timeHasPassed(beginTime2)) {
				util.showError(this, "时间不能早于今天");
				return false;
			}
			if (new Date(beginTime2).getTime() > new Date(endTime2).getTime()) {
				util.showError(this, "开始时间应当早于结束时间");
				return false;
			} else {
				this.setData({
					showBeginTime2: false,
					beginTime2: beginTime2
				});
			}
		} else {
			if (util.timeHasPassed(beginTime2)) {
				util.showError(this, "时间不能早于今天");
				return false;
			} else {
				this.setData({
					showBeginTime2: false,
					beginTime2: beginTime2
				})
			}
		}
	},

	// 归还时间picker
	bindEndTimeChange2: function (e) {
		let endTime2 = e.detail.value;
		let beginTime2 = this.data.beginTime2;
		// 计算已选天数
		if (beginTime2 == null || beginTime2 == "") {
			util.showError(this, '请先选择接触时间');
			return false;
		} else {
			if (util.timeHasPassed(endTime2)) {
				util.showError(this, "时间不能早于今天");
				return false;
			}
			if (new Date(beginTime2).getTime() > new Date(endTime2).getTime()) {
				util.showError(this, '开始时间应当早于结束时间');
				return false;
			} else {
				this.setData({
					showEndTime2: false,
					endTime2: endTime2
				})
			}
		}
	},

	// 部门经理picker
	bindDepartmentManagerChange: function (e) {
		let that = this;
		let thatValue = that.data;
		this.setData({
			showDepartmentManager: false,
			leaderDIndex: e.detail.value
		})

		// 触发获取下一审批人方法的条件：ifSelct为true;当前不只选择了一个法人身份证复印件
		for (let i = 0; i < thatValue.checkedSealId.length; i++) {
			if (thatValue.checkedSealId[i] === 5) {
				if (thatValue.checkedSealId.length == 1) {
					that.setData({
						isOnlyFYJ: true
					})
				} else {
					that.setData({
						isOnlyFYJ: false
					})
				}
			}
		}

		if (that.data.ifSelect == true && that.data.isOnlyFYJ == true) {
			console.log('触发获取下一审批人方法');
			// 获取下一个审批人
			wx.request({
				url: app.globalData.URL + '/print/getNextAppriovalMan.action',
				header: {
					'content-type': 'application/json'
				},
				data: {
					userId: this.data.departmentManagerList[this.data.leaderDIndex].id
				},
				method: 'GET',
				success: function (res) {
					that.setData({
						businessExecutivesList: res.data.approvalMan2 || "",
						generalManagerList: res.data.approvalMan3 || "",
					})
				},
				fail: function (res) {
					console.log('获取下一个审批人数据信息--失败');
					util.showError(that, '网络错误,请稍后重试');
				}
			})
		} else {
			console.log("不触发获取下一个审批人事件");
		}
		if (that.data.leaderDIndex > that.data.departmentManagerList.length) {
			that.setData({
				leaderDIndex: 0
			})
		}
	},

	// 业务主管领导picker
	bindBusinessExecutivesChange: function (e) {
		let that = this;
		this.setData({
			showBusinessExecutives: false,
			leaderBIndex: e.detail.value,
			businessExecutives: that.data.businessExecutivesList[e.detail.value].realname
		})
	},

	// 总经理picker
	bindGeneralManagerChange: function (e) {
		let that = this;
		that.setData({
			showGeneralManager: false,
			leaderGIndex: e.detail.value,
			generalManager: that.data.generalManagerList[e.detail.value].realname
		})
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
		if (submitValue.certificateNo !== undefined) {
			submitValue.certificateNo = submitValue.certificateNo.trim();
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
				if (submitValue.trustee === "" || submitValue.trusteeDuty === "" || submitValue.certificateNo === "" || submitValue.entrustedMatters === "" || submitValue.beginTime === null || submitValue.endTime === null || submitValue.timeLimit === 0) {
					util.showError(that, '请填写所有信息后提交');
					return false;
				}
				// 受托人证件号判断
				if (submitValue.certificateType === "身份证") {
					let regIdNo1 = /(^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)|(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}$)/;
					if (!regIdNo1.test(submitValue.certificateNo)) {
						util.showError(that, '身份证号码格式有误');
						return false;
					}
				} else if (submitValue.certificateType === "护照") {
					let regIdNo2 = /^((1[45]\d{7})|(G\d{8})|(P\d{7})|(S\d{7,8}))?$/;
					if (!regIdNo2.test(submitValue.certificateNo)) {
						util.showError(that, '护照号码格式有误');
						return false;
					}
				}
				// 预借时间不能早于当前时间
				if (util.timeHasPassed(e.detail.value)) {
					util.showError(that, "开始时间不能早于当前时间");
					return false;
				}
				// 预还时间不能早于预借时间
				if (util.compare(submitValue.beginTime, submitValue.endTime)) {
					util.showError(that, "结束时间不能早于开始时间");
					return false;
				}
			}

			// 判断是否重复提交
			if (that.data.hasCommited === true) {
				util.showError(that, "您已提交，请勿重复提交");
				return false;
			} else {
				// 发送申请信息到后台
				for (let i = 0; i < thatValue.gzkind.length; i++) {
					if (thatValue.gzkind[i] == 3) {
						submitValue.useSealNumber = parseInt(submitValue.useSealNumber);
						if (submitValue.contractAmount <= 0) {
							util.showError(that, "合同金额应大于0");
							return false;
						}
						break;
					}
				}

				switch (that.data.max) {
					case 1:
						if (submitValue.generalManager == undefined || submitValue.generalManager == "" || submitValue.generalManager == null) {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.businessExecutives || ""
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
							break;
						} else {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.generalManager || ""
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
							break;
						}
					case 2:
						if (submitValue.generalManager == undefined || submitValue.generalManager == "" || submitValue.generalManager == null) {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.businessExecutives || "",
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
						} else {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.generalManager || ""
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
						}
						break;
					case 3:
						if (submitValue.generalManager == undefined || submitValue.generalManager == "" || submitValue.generalManager == null) {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									entrustedman: submitValue.trustee || "",
									entrustedpost: submitValue.trusteeDuty || "",
									entrustedcardtype: submitValue.certificateType || "",
									entrustedcardnum: submitValue.certificateNo || "",
									entrustedmatter: submitValue.entrustedMatters || "",
									entrustedpermission: submitValue.timeLimit || "",
									entrustedstarttime: submitValue.beginTime || "",
									entrustedendtime: submitValue.endTime || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.businessExecutives || ""
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
						} else {
							wx.request({
								url: app.globalData.URL + '/print/startApply.action',
								header: {
									'content-type': 'application/json'
								},
								data: {
									applytime: submitValue.applyTime,
									applyusername: submitValue.applyMan,
									department: submitValue.department,
									departmentid: submitValue.department_id,
									gzkind: that.data.gzkind,
									copies: submitValue.useSealNumber,
									sendto: submitValue.toCompany,
									contracAmount: submitValue.contractAmount || 0,
									contractType: submitValue.contractType || "",
									issecret: submitValue.classified,
									reason: submitValue.useSealReason,
									borrowTime: submitValue.beginTime2 || "",
									returnTime: submitValue.endTime2 || "",
									entrustedman: submitValue.trustee || "",
									entrustedpost: submitValue.trusteeDuty || "",
									entrustedcardtype: submitValue.certificateType || "",
									entrustedcardnum: submitValue.certificateNo || "",
									entrustedmatter: submitValue.entrustedMatters || "",
									entrustedpermission: submitValue.timeLimit || "",
									entrustedstarttime: submitValue.beginTime || "",
									entrustedendtime: submitValue.endTime || "",
									approveman: submitValue.departmentManager || "",
									BusinessManager: submitValue.businessExecutives || "",
									confirmman: submitValue.generalManager || ""
								},
								method: 'GET',
								success: function (res) {
									that.setData({
										hasCommited: true,
										showSubmit: false
									})
									util.goTo("../../../publicPages/applyback/applyback");
								},
								fail: function (res) {
									that.setData({
										showSubmit: true
									})
									console.log('发送用印申请数据信息--失败');
									util.showError(that, '网络错误,请稍后重试');
								}
							})
							that.setData({
								hasCommited: true
							})
							util.showLoading();
						}
						break;
				}
			}
		}
	},

	/**
	 * 生命周期函数--监听页面加载
	 */
	onLoad: function (options) {
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
				that.setData({
					adapterSource: res.data.unit,
					grade: res.data.grade,
					user_id: res.data.user.id,
					applyTime: util.getBeginAndEndDate().endTime,
					applyMan: res.data.user.code,
					department: res.data.user.departmentname,
					department_id: res.data.user.departmentid,
					sealTypeList: res.data.gzkind,
					departmentManagerList: res.data.approvalMan1,
					businessExecutivesList: res.data.approvalMan2 || "",
					generalManagerList: res.data.approvalMan3 || "",
					ifSelect: res.data.ifSelect,
					approvalMan0: res.data.approvalMan0 || "",
					approvalMan1: res.data.approvalMan1
				})
				// 每一次进入用印申请页面时，默认没有选择公章
				for (let i = 0; i < that.data.sealTypeList.length; i++) {
					that.data.sealTypeList[i].checked = false;
				}
				if (res.data.ifSelect == true) {
					// 获取下一个审批人
					wx.request({
						url: app.globalData.URL + '/print/getNextAppriovalMan.action',
						header: {
							'content-type': 'application/json'
						},
						data: {
							userId: that.data.departmentManagerList[that.data.leaderDIndex].id
						},
						method: 'GET',
						success: function (res) {
							that.setData({
								businessExecutivesList: res.data.approvalMan2 || "",
								generalManagerList: res.data.approvalMan3 || "",
							})
						},
						fail: function (res) {
							console.log('获取下一个审批人数据信息--失败');
							util.showError(that, '网络错误,请稍后重试');
						}
					})
				} else {
					console.log("不触发获取下一个审批人事件");
				}

				// 判断最多有几个审批人
				if (res.data.approvalCount === 1) {
					that.setData({
						showLeader2: false,
						showLeader3: false
					})
				} else if (res.data.approvalCount === 2) {
					that.setData({
						showLeader2: true,
						showLeader3: false
					})
				} else if (res.data.approvalCount === 3) {
					that.setData({
						showLeader2: true,
						showLeader3: true
					})
				}

			},
			fail: function (res) {
				console.log('获取用印申请默认数据信息--失败');
				util.showError(that, '网络错误,请稍后重试');
			}
		})

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