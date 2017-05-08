import React, { Component } from 'react';
import { BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';

export default class PollResults extends Component {
  render() {
    const options = {
      anonymous: 0,
    };

    this.props.data.forEach((item) => {
      if (!item.choice) {
        options.anonymous += 1;
        return;
      }
      options[item.choice] = options[item.choice] || 0;
      options[item.choice] += 1;
    });

    let data = [];
    Object.keys(options).forEach((key) => {
      data.push({
        option: key,
        votes: options[key],
      });
    });

    return <BarChart width={600} height={400} data={data}>
      <CartesianGrid stroke='#ccc' />
      <XAxis dataKey='option' />
      <YAxis />
      <Tooltip />
      <Bar dataKey='votes' fill='#8884d8' />
    </BarChart>;
  }
}
