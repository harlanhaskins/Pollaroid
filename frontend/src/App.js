import React, { Component } from 'react';
import {
  BrowserRouter as Router,
  Route
} from 'react-router-dom';
import NotificationSystem from 'react-notification-system';

import Navbar from './Navbar.js';
import Home from './Home.js';
import Vote from './Vote.js';
import Explore from './Explore.js';
import Signup from './Signup.js';
import Login from './Login.js';
import Poll from './Poll.js';

class App extends Component {
  componentDidMount() {
    window.notificationSystem = this.refs.notificationSystem;
  }

  componentWillUnmount() {
    window.notificationSystem = null;
  }

  render() {
    return <div>
      <Router>
        <div>
          <Navbar />
          <Route exact path='/' component={Home} />
          <Route exact path='/vote' component={Vote} />
          <Route exact path='/explore' component={Explore} />
          <Route exact path='/signup' component={Signup} />
          <Route exact path='/login' component={Login} />
          <Route path='/vote/:pollId' component={Poll} />
        </div>
      </Router>
      <NotificationSystem ref='notificationSystem' />
    </div>;
  }
}

export default App;
