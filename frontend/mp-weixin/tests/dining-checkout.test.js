const assert = require('assert');
const fs = require('fs');
const path = require('path');

const vendorPath = path.resolve(__dirname, '../common/vendor.js');
const payPagePath = path.resolve(__dirname, '../pages/pay/index.js');
const vendor = fs.readFileSync(vendorPath, 'utf8');
const payPage = fs.readFileSync(payPagePath, 'utf8');

assert(
  vendor.includes("uni.getStorageSync('diningSessionId')"),
  '去结算时必须读取已确认的堂食会话'
);

assert(
  vendor.includes("orderType: 3") &&
    vendor.includes("deliveryMethod: 'DINING'") &&
    vendor.includes('diningSessionId: diningSessionId'),
  '扫码堂食结算必须提交堂食订单、座位和用餐会话'
);

assert(
  vendor.includes("url: '/pages/pay/index?orderId=' + res.data.id"),
  '堂食订单创建成功后必须直接跳转支付页'
);

assert(
  vendor.includes("url: '/pages/order/index'"),
  '未扫码时必须保留原来的外卖地址结算页'
);

assert(
  payPage.includes("wx.removeStorageSync('diningSessionId');") &&
    payPage.includes("wx.removeStorageSync('seatId');") &&
    payPage.includes("wx.removeStorageSync('seatCode');"),
  '堂食支付成功后必须清除座位会话，下一次未扫码时恢复外卖流程'
);

console.log('dining-checkout tests passed');
