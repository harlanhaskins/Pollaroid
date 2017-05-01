import React, { Component } from 'react';
import PollForm from './forms/PollForm.js';

class CreatePoll extends Component {
  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>Create poll</h1>
        <br />
        <PollForm />
      </div>
    </div>;
  }
}

export default CreatePoll;
