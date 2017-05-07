import React, { Component } from 'react';
import { BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';
import api from './api';

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
        this.setState({
          data,
        });
      });
  }

  render() {
    return <BarChart width={600} height={400} data={this.state.data}>
      <CartesianGrid stroke='#ccc' />
      <XAxis dataKey='title' />
      <YAxis />
      <Tooltip />
      <Bar dataKey='numberOfVotes' fill='#8884d8' />
    </BarChart>;
  }
}
