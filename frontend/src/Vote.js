import React, { Component } from 'react';
import DataLoader from './DataLoader';
import ApiTable from './ApiTable';
import api from './api';
import auth from './auth';

class Vote extends Component {
  constructor() {
    super();

    this.state = {
      data: [],
      loaded: false,
    };
  }

  componentDidMount() {
    if (!auth.isLoggedIn()) {
      return;
    }

    api('polls').then((data) => {
      // TODO: not this.
      data.forEach((item) => {
        item.district = `${item.district.stateCode} ${item.district.congressionalBody}`;
        item.submitter = item.submitter.name;
        item.options = item.options.map((option) => option.text).join(', ');
      });

      this.setState({
        data,
        loaded: true,
      });
    });
  }

  render() {
    return <div className='container'>
      <div className='starter-template'>
        <h1>Vote.</h1>
        { auth.isLoggedIn()
          ? <DataLoader loaded={this.state.loaded}>
            <ApiTable data={this.state.data} detailLink />
          </DataLoader>
          : <p>Please log in.</p> }
      </div>
    </div>;
  }
}

export default Vote;
