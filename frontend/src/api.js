import auth from './auth';

const ALREADY_NOTIFIED = '_ALREADY_NOTIFIED_';

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

  return fetch(`https://pollaroid.club/api/${resource}`, options)
    .then((response) => {
      if (response.status === 204) {
        return response.text();
      }
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
        let message = errors.join(', ');

        if (response.code === 401 && message === 'HTTP 401 Unauthorized') {
          message = 'You don\'t have access to that page. Try logging in?';
        }

        const isFatal = !response.code || response.code >= 400;
        window.notificationSystem.addNotification({
          message,
          level: isFatal ? 'error' : 'success',
        });
        if (isFatal) {
          throw new Error(`${ALREADY_NOTIFIED}${message}`);
        }
      }

      return response;
    })
    .catch((e) => {
      if (e.message && e.message.includes(ALREADY_NOTIFIED)) {
        // Catch the case above where we already sent
        // a notification to the user - just pass the
        // error along
        e.message = e.message.replace(ALREADY_NOTIFIED, '');
      } else {
        window.notificationSystem.addNotification({
          message: 'An unknown API error occurred',
          level: 'error',
        });
      }

      throw e;
    });
};

export default api;
