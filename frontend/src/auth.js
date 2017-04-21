const globalAuth = {
  data: null,
  listeners: [],
};

const addListener = (fn) => {
  globalAuth.listeners.push(fn);
};

const isLoggedIn = () => globalAuth.data && globalAuth.data.uuid;

const notifyListeners = () => {
  const loggedIn = isLoggedIn();
  globalAuth.listeners.forEach((l) => l(loggedIn));
};

const login = (data) => {
  globalAuth.data = data;
  notifyListeners();
};

const logout = () => {
  globalAuth.data = null;
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

export default {
  login,
  logout,
  token,
  isLoggedIn,
  addListener,
  getData,
};
