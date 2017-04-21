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
import auth from './auth.js';

class App extends Component {
  constructor() {
    super();

    this.state = {
      loggedIn: false,
    };
  }

  componentDidMount() {
    window.notificationSystem = this.refs.notificationSystem;
    auth.addListener(this.loginStateChanged.bind(this));
  }

  loginStateChanged(loggedIn) {
    this.setState({
      loggedIn,
    });
  }

  componentWillUnmount() {
    window.notificationSystem = null;
  }

  render() {
    return <div>
      <Router>
        <div>
          <Navbar loggedIn={this.state.loggedIn} />
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
