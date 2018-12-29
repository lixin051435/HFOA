//app.js
App({
  onLaunch: function () {

  },
  globalData: {
    // 124戴志华，122王志新
    userInfo: null,
    URL: "https://gongche.hfga.com.cn/HFOANEW",
    // URL: "https://gongche.hfga.com.cn:444/HFOANEWTEST"
    // URL: "http://192.168.4.157:8080/HFOANEW",
    // URL: "http://192.168.4.122:9988/HFOANEW"
    // URL: "http://192.168.4.122:8080/HFOANEW"
  },

  "networkTimeout": {
    "request": 15000,
    "connectSocket": 15000,
    "uploadFile": 15000,
    "downloadFile": 15000
  }
})