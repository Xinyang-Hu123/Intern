const assert = require('assert');
const fs = require('fs');
const path = require('path');

const mapperPath = path.resolve(
  __dirname,
  '../../../backend/sky-take-out/sky-server/src/main/resources/mapper/ShoppingCartMapper.xml'
);
const vendorPath = path.resolve(__dirname, '../common/vendor.js');

const mapper = fs.readFileSync(mapperPath, 'utf8');
const vendor = fs.readFileSync(vendorPath, 'utf8');

assert(
  mapper.includes(
    'shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time, dining_session_id)'
  ),
  '新增购物车 SQL 必须写入 dining_session_id，避免字段和值数量不一致'
);

assert(
  mapper.includes('set number = #{number}'),
  '购物车数量更新必须更新 number 字段'
);

assert(
  vendor.includes("uni.setStorageSync('token', success.data.token);"),
  '微信登录成功后必须将 token 保存到本地，扫码确认座位才能携带身份'
);

assert(
  vendor.includes('_this.loginSync().then(function (jsCode) {'),
  '用户登录必须先获取微信 code，再请求登录接口'
);

assert(
  vendor.includes("title: '请先授权微信登录后再点餐'"),
  '未登录时点击加入购物车必须给出明确提示'
);

assert(
  vendor.includes("title: '加入购物车失败，请重试'"),
  '加入购物车失败时不能静默吞掉错误'
);

console.log('shopping-cart-and-login tests passed');
