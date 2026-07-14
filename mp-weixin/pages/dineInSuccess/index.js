Page({
  data: {
    seatNumber: '',
    orderNumber: ''
  },

  onLoad(options) {
    this.setData({
      seatNumber: decodeURIComponent(options.seatNumber || wx.getStorageSync('seatNumber') || ''),
      orderNumber: decodeURIComponent(options.orderNumber || '')
    })
  },

  viewOrders() {
    wx.reLaunch({ url: '/pages/historyOrder/historyOrder' })
  },

  continueOrdering() {
    wx.reLaunch({ url: '/pages/index/index' })
  }
})
