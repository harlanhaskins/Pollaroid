import React, { Component } from 'react';
import { BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';
import api from './api';

const votesFormatter = (val, _, etc) => {
  return `${val}. "${etc.payload.mostPopularOption}" had the most votes.`;
};

export default class TopPolls extends Component {
  constructor() {
    super();

    this.state = {
      data: null,
    };
  }

  componentDidMount() {
    this.getData();
  }

  getData() {
    api('polls/top')
      .then((data) => {
        data.forEach((item) => {
          item.Votes = item.poll ? item.poll.numberOfVotes : 0;
        });
        this.setState({
          data,
        });
      });
  }

  render() {
    return <BarChart width={600} height={400} data={this.state.data}>
      <CartesianGrid stroke='#ccc' />
      <XAxis dataKey='poll.title' />
      <YAxis />
      <Tooltip formatter={votesFormatter} />
      <Bar dataKey='Votes' fill='#8884d8' />
    </BarChart>;
  }
}
