import React, { Component } from 'react';
import SigninForm from './forms/SigninForm.js';

class Login extends Component {
  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>Log in.</h1>
        <br />
        <SigninForm />
      </div>
    </div>;
  }
}

export default Login;
