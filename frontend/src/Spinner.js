import React, { Component } from 'react';

class Spinner extends Component {
  shouldComponentUpdate() {
    return false;
  }

  render() {
    return <div className='spinner' />;
  }
}

export default Spinner;
