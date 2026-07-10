var app = getApp();
Page({
  data: {
    seatInfo: null,
    errorMsg: '',
    loading: true,
    diningSessionId: null,
    seatId: null
  },

  onLoad: function (options) {
    // 解析扫码参数 scene
    var scene = options.scene || '';
    console.log('扫码scene参数:', scene);

    if (scene) {
      this.parseScene(scene);
    } else {
      // 没有scene参数，尝试从storage获取
      this.setData({ loading: false, errorMsg: '未检测到座位二维码，请重新扫码' });
    }
  },

  onShow: function () {
    // 每次显示页面时检查是否有有效的session
    var diningSessionId = wx.getStorageSync('diningSessionId');
    var seatId = wx.getStorageSync('seatId');
    if (diningSessionId && seatId) {
      this.setData({
        diningSessionId: diningSessionId,
        seatId: seatId
      });
    }
  },

  // 解析scene参数: seatCode:qrVersion:sign
  parseScene: function (scene) {
    var that = this;
    var parts = scene.split(':');

    if (parts.length !== 3) {
      this.setData({ loading: false, errorMsg: '二维码格式无效，请联系店员' });
      return;
    }

    var seatCode = parts[0];
    var qrVersion = parseInt(parts[1]);
    var sign = parts[2];

    // 调用后端接口校验
    wx.request({
      url: app.globalData.baseUrl + '/user/seat/scan',
      method: 'POST',
      data: scene,
      header: {
        'authentication': wx.getStorageSync('token') || ''
      },
      success: function (res) {
        if (res.data.success || (res.data.code === 1 && res.data.data.success)) {
          var data = res.data.data;
          that.setData({
            loading: false,
            seatInfo: {
              seatCode: data.seatCode,
              seatName: data.seatName,
              areaName: data.areaName
            },
            diningSessionId: data.diningSessionId,
            seatId: data.seatId,
            errorMsg: ''
          });
          // 保存到storage
          wx.setStorageSync('diningSessionId', data.diningSessionId);
          wx.setStorageSync('seatId', data.seatId);
          wx.setStorageSync('seatCode', data.seatCode);
        } else {
          that.setData({
            loading: false,
            errorMsg: res.data.data.message || '扫码失败，请联系店员'
          });
        }
      },
      fail: function () {
        that.setData({
          loading: false,
          errorMsg: '网络连接失败，请检查网络'
        });
      }
    });
  },

  // 开始点餐
  goToOrder: function () {
    wx.switchTab({
      url: '/pages/index/index'
    });
  },

  // 重新扫码
  reScan: function () {
    wx.scanCode({
      success: function (res) {
        console.log('扫码结果:', res);
        var scene = res.result;
        that.parseScene(scene);
      }
    });
  }
});
