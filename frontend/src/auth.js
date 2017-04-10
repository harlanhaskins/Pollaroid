const globalAuth = {
  data: null,
};

const login = (data) => {
  globalAuth.data = data;
};

const isLoggedIn = () => globalAuth.data && globalAuth.data.uuid;

const token = () => {
  if (!isLoggedIn()) {
    return '';
  }

  return globalAuth.data.uuid;
};

export default {
  login,
  token,
  isLoggedIn,
};
