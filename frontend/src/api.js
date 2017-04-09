const api = (resource) => {
  return fetch(`http://localhost:8080/api/${resource}`, {
    headers: {
      'X-API-KEY': 'e8e649d6-14f8-4ffb-a9ec-d43de72f0ab6',
    }})
    .then((response) => {
      return response.json();
    });
};

export default api;
