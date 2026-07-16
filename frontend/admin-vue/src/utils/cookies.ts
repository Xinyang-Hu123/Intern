import Cookies from 'js-cookie';

const cookieAttributes: Cookies.CookieAttributes = {
  path: '/',
  sameSite: 'strict',
  secure: process.env.NODE_ENV === 'production'
};

const writeCookie = (name: string, value: string) => Cookies.set(name, value, cookieAttributes);
const removeCookie = (name: string) => Cookies.remove(name, { path: '/' });

// App
const sidebarStatusKey = 'sidebar_status';
export const getSidebarStatus = () => Cookies.get(sidebarStatusKey);
export const setSidebarStatus = (sidebarStatus: string) => writeCookie(sidebarStatusKey, sidebarStatus);

// User
const storeId = 'storeId';
export const getStoreId = () => Cookies.get(storeId);
export const setStoreId = (id: string) => writeCookie(storeId, id);
export const removeStoreId = () => removeCookie(storeId);

// User
const tokenKey = 'token';
export const getToken = () => Cookies.get(tokenKey);
export const setToken = (token: string) => writeCookie(tokenKey, token);
export const removeToken = () => removeCookie(tokenKey);

// userInfo

const userInfoKey = 'user_info';
export const getUserInfo = () => Cookies.get(userInfoKey);
export const setUserInfo = (userInfo: Object) => writeCookie(userInfoKey, JSON.stringify(userInfo));
export const removeUserInfo = () => removeCookie(userInfoKey);

const usernameKey = 'username';
export const getUsername = () => Cookies.get(usernameKey);
export const setUsername = (username: string) => writeCookie(usernameKey, username);
export const removeUsername = () => removeCookie(usernameKey);

// printinfo

const printKey = 'print';
export const getPrint = () => Cookies.get(printKey);
export const setPrint = (printInfo: Object) => writeCookie(printKey, JSON.stringify(printInfo));
export const removePrint = () => removeCookie(printKey);

// 获取消息
const newData = 'new';
export const getNewData = () => Cookies.get(newData);
export const setNewData = (val: Object) => writeCookie(newData, JSON.stringify(val));
