import React, { Component } from 'react';
import Navbar from './Navbar.js';
import './App.css';

class App extends Component {
  render() {
    return (
      <div>
        { Navbar }
        <div className="container">
          <div className="starter-template">
            <h1>BiPoller</h1>
            <p className="lead">A powerful tool for public officials and their campaigns<br /> to connect and survey the voters they represent. </p>
          </div>
        </div>
      </div>
    );
  }
}

export default App;
