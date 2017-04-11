import React, { Component } from 'react';
import DataLoader from './DataLoader';
import ApiTable from './ApiTable';
import api from './api';

class Poll extends Component {
  constructor() {
    super();

    this.state = {
      data: [],
      loaded: false,
    };
  }

  componentDidMount() {
    api(`polls/${this.props.match.params.pollId}/responses`)
      .then((data) => {
        this.setState({
          data,
          loaded: true,
        });
      });
  }


  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>Poll.</h1>
        <DataLoader loaded={this.state.loaded}>
          <ApiTable data={this.state.data} />
        </DataLoader>
      </div>
    </div>;
  }
}

export default Poll;
