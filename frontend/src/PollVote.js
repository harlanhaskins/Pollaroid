import React, { Component } from 'react';
import api from './api';
import auth from './auth';
import VoteForm from './forms/VoteForm';
import DataLoader from './DataLoader';
import './Home.css';

class PollVote extends Component {
  constructor() {
    super();

    this.state = {
      poll: null,
      loaded: false,
    };
  }

  componentDidMount() {
    if (!auth.isLoggedIn()) {
      return;
    }

    api('polls').then((data) => {
      const poll = data.find((item) => {
        return parseInt(this.props.match.params.pollId, 10) === item.id;
      });

      this.setState({
        poll,
        loaded: true,
      });
    });
  }

  render() {
    return <div className='container'>
      <div className='starter-template'>
        { auth.isLoggedIn()
          ? <DataLoader loaded={this.state.loaded}>
            <VoteForm poll={this.state.poll} />
          </DataLoader>
          : <p>Please log in.</p> }
      </div>
    </div>;
  }
}

export default PollVote;
