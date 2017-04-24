import React, { Component } from 'react';
import SignupForm from './forms/SignupForm.js';

class Signup extends Component {
  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>Sign up.</h1>
        <br />
        <SignupForm />
      </div>
    </div>;
  }
}

export default Signup;
