import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import DataLoader from './DataLoader';
import ApiTable from './ApiTable';
import api from './api';
import auth from './auth';

const miscColumn = (item) => <Link to={`/polls/${item.id}/vote`} className='btn btn-info btn-xs'><i className='fa fa-archive icon-space-r' />Vote</Link>;

class Polls extends Component {
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
        item.district = `${item.district.stateCode} ${item.district.congressionalBody} District ${item.district.number}`;
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
        <h1>Polls</h1>
        { auth.isRepresentative() &&
          <p>
            <Link to={`/polls/create`} className='btn btn-info btn-sm'>
              <i className='fa fa-plus icon-space-r' />Create Poll
            </Link>
          </p>
        }
        { auth.isLoggedIn()
          ? <DataLoader loaded={this.state.loaded}>
            <ApiTable data={this.state.data} detailLink={auth.isRepresentative()} miscColumn={miscColumn} />
          </DataLoader>
          : <p>Please log in.</p> }
      </div>
    </div>;
  }
}

export default Polls;
