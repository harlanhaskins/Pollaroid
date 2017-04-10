import auth from './auth';

const api = (resource, body) => {
  const options = {
    method: body ? 'POST' : 'GET',
    headers: {
      'X-API-KEY': auth.token(),
    },
  };

  if (body) {
    options.body = JSON.stringify(body);
    options.headers['Content-Type'] = 'application/json';
  }

  return fetch(`http://localhost:8080/api/${resource}`, options)
    .then((response) => {
      return response.json();
    })
    .then((response) => {
      let errors = [];
      if (response.errors) {
        errors = response.errors;
      }
      if (response.message) {
        errors = [response.message];
      }

      if (errors.length > 0) {
        window.notificationSystem.addNotification({
          message: errors.join(', '),
          level: response.code < 400 ? 'success' : 'error',
        });
      }

      return response;
    });
};

export default api;
