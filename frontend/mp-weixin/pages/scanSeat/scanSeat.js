var serverConfig = require('../../common/server-config.js');
var seatScene = require('../../common/seat-scene.js');
Page({
  data: {
    seatInfo: null,
    errorMsg: '',
    loading: true,
    confirming: false,
    full: false,
    scene: ''
  },

  onLoad: function (options) {
    var scene = options.scene || '';
    console.log('扫码scene参数:', scene);

    if (scene) {
      this.parseScene(scene);
    } else {
      this.setData({
        loading: false,
        errorMsg: '未检测到座位二维码，请重新扫码',
        scene: scene
      });
    }
  },

  parseScene: function (scene) {
    var that = this;
    var normalizedScene = seatScene.normalizeSeatScene(scene);
    var parts = normalizedScene ? normalizedScene.split(':') : [];

    if (
      parts.length !== 3 ||
      !parts[0] ||
      !parts[1] ||
      !parts[2] ||
      isNaN(parseInt(parts[1], 10))
    ) {
      this.setData({
        seatInfo: null,
        errorMsg: '二维码格式无效，请联系店员',
        loading: false,
        full: false,
        scene: normalizedScene
      });
      return;
    }

    this.setData({
      seatInfo: null,
      errorMsg: '',
      loading: true,
      full: false,
      scene: normalizedScene
    });

    wx.request({
      url: serverConfig.getBaseUrl() + '/user/seat/scan',
      method: 'POST',
      data: normalizedScene,
      header: {
        'content-type': 'text/plain;charset=UTF-8',
        'authentication': wx.getStorageSync('token') || ''
      },
      success: function (res) {
        if (res.data && res.data.code === 1 && res.data.data && res.data.data.success) {
          var data = res.data.data;
          var full = data.full === true;
          that.setData({
            seatInfo: {
              seatId: data.seatId,
              seatCode: data.seatCode,
              seatName: data.seatName,
              areaName: data.areaName,
              locationName: data.seatName.indexOf(data.areaName) === 0
                ? data.seatName
                : data.areaName + ' ' + data.seatName,
              capacity: data.capacity,
              participantCount: data.participantCount,
              joined: data.joined
            },
            full: full,
            errorMsg: full ? '该座位已被占用，请联系店员' : '',
            loading: false,
            scene: normalizedScene
          });
        } else {
          that.setData({
            loading: false,
            errorMsg: (res.data && res.data.msg) ||
              (res.data && res.data.data && res.data.data.message) ||
              '扫码失败，请联系店员'
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

  confirmStartOrder: function () {
    var that = this;
    var seatInfo = this.data.seatInfo;

    if (!seatInfo || this.data.confirming || this.data.full) {
      return;
    }

    this.setData({
      confirming: true,
      errorMsg: ''
    });

    wx.request({
      url: serverConfig.getBaseUrl() + '/user/seat/session/confirm?seatId=' + encodeURIComponent(seatInfo.seatId),
      method: 'POST',
      header: {
        'authentication': wx.getStorageSync('token') || ''
      },
      success: function (res) {
        if (res.data && res.data.code === 1 && res.data.data && res.data.data.success) {
          var data = res.data.data;
          wx.setStorageSync('diningSessionId', data.diningSessionId);
          wx.setStorageSync('seatId', data.seatId);
          wx.setStorageSync('seatCode', data.seatCode);
          wx.redirectTo({
            url: '/pages/index/index'
          });
          return;
        }

        var message = (res.data && res.data.msg) ||
          (res.data && res.data.data && res.data.data.message) ||
          '确认座位失败，请重试';
        that.setData({
          errorMsg: message,
          full: message === '该座位已被占用，请联系店员'
        });
      },
      fail: function () {
        that.setData({
          errorMsg: '网络连接失败，请检查网络'
        });
      },
      complete: function () {
        that.setData({
          confirming: false
        });
      }
    });
  },

  reScan: function () {
    var that = this;
    wx.scanCode({
      success: function (res) {
        console.log('扫码结果:', res);
        that.parseScene(res.result);
      }
    });
  }
});
