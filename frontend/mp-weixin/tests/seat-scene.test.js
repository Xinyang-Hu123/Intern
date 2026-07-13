const assert = require('assert');
const path = require('path');

const seatScene = require(path.resolve(__dirname, '../common/seat-scene.js'));

const signedScene = 'A01:3:2ee22c43048294996c20689f57842761';

assert.strictEqual(
  seatScene.normalizeSeatScene(encodeURIComponent(signedScene)),
  signedScene,
  '扫码页应还原跳转时编码过的二维码内容'
);

assert.strictEqual(
  seatScene.normalizeSeatScene('  ' + signedScene + '  '),
  signedScene,
  '扫码页应忽略二维码内容前后的空白'
);

console.log('seat-scene tests passed');
