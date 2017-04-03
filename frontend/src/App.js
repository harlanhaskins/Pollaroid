import React, { Component } from 'react';
import {
  BrowserRouter as Router,
  Route
} from 'react-router-dom';

import Navbar from './Navbar.js';
import Home from './Home.js';
import Vote from './Vote.js';
import Explore from './Explore.js';
import Signup from './Signup.js';
import Login from './Login.js';

class App extends Component {
  render() {
    return <Router>
      <div>
        <Navbar />
        <Route exact path='/' component={Home} />
        <Route exact path='/vote' component={Vote} />
        <Route exact path='/explore' component={Explore} />
        <Route exact path='/signup' component={Signup} />
        <Route exact path='/login' component={Login} />
      </div>
    </Router>;
  }
}

export default App;
