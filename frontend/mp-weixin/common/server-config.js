var DEFAULT_BASE_URL = 'http://10.143.6.18:8088';

function getBaseUrl() {
  var baseUrl = '';

  try {
    baseUrl = wx.getStorageSync('serverUrl') || '';
  } catch (e) {}

  if (
    baseUrl === 'http://localhost:8080' ||
    baseUrl === 'http://localhost:8088' ||
    baseUrl === 'http://127.0.0.1:8088' ||
    baseUrl === 'http://192.168.31.16:8088' ||
    baseUrl === 'http://172.16.61.184:8088' ||
    baseUrl === 'https://angry-pandas-stick.loca.lt' ||
    baseUrl === 'https://chilly-ducks-double.loca.lt'
  ) {
    baseUrl = '';
  }

  return (baseUrl || DEFAULT_BASE_URL).replace(/\/+$/, '');
}

module.exports = {
  getBaseUrl: getBaseUrl
};
