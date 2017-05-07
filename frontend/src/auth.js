import Cookie from 'js-cookie';

const COOKIE_KEY = 'pollaroid_auth_info';
const globalAuth = {
  data: null,
  listeners: [],
};

const addListener = (fn) => {
  globalAuth.listeners.push(fn);
};

const isLoggedIn = () => !!(globalAuth.data && globalAuth.data.uuid);

const isRepresentative = () => isLoggedIn() && !!(globalAuth.data.voter && globalAuth.data.voter.representingDistrict);

const notifyListeners = () => {
  const loggedIn = isLoggedIn();
  globalAuth.listeners.forEach((l) => l(loggedIn));
};

const login = (data) => {
  globalAuth.data = data;
  Cookie.set(COOKIE_KEY, data, { expires: 365 });
  notifyListeners();
};

const logout = () => {
  globalAuth.data = null;
  Cookie.remove(COOKIE_KEY);
  notifyListeners();
};

const token = () => {
  if (!isLoggedIn()) {
    return '';
  }

  return globalAuth.data.uuid;
};

const getData = () => {
  if (!isLoggedIn()) {
    return;
  }

  return globalAuth.data;
};

const initialize = () => {
  const cookieData = Cookie.getJSON(COOKIE_KEY);
  if (cookieData) {
    login(cookieData);
  }
};

initialize();

export default {
  login,
  logout,
  token,
  isLoggedIn,
  isRepresentative,
  addListener,
  getData,
};
