import React, { Component } from 'react';
import './Home.css';

class Home extends Component {
  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>BiPoller</h1>
        <p className='lead'>A powerful tool for public officials and their campaigns<br /> to connect and survey the voters they represent. </p>
      </div>
    </div>;
  }
}

export default Home;
