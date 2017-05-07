import React from 'react';
import { Redirect } from 'react-router-dom';
import { LocalForm } from 'react-redux-form';

import { FormGroup, Radio } from './helpers';
import Spinner from '../Spinner';
import api from '../api';

export default class VoteForm extends React.Component {
  constructor() {
    super();

    this.state = {
      loading: false,
      redirectToVote: false,
    };
  }

  handleSubmit(values) {
    this.setState({
      loading: true,
    });

    api(`polls/${this.props.poll.id}/responses`, values)
      .then((data) => {
        this.setState({
          loading: false,
        });

        window.notificationSystem.addNotification({
          message: `Your vote has been cast!`,
          level: 'success',
        });

        this.setState({
          redirectToVote: true,
        });
      })
      .catch((e) => {
        this.setState({
          loading: false,
        });
        throw e;
      });
  }

  render() {
    if (this.state.redirectToVote) {
      return <Redirect to={'/polls'} />;
    }

    return (
      <LocalForm
        onSubmit={(values) => this.handleSubmit(values)}
        className='form-horizontal'
      >
        <FormGroup>
          <div className='col-xs-12'><h3>{this.props.poll.title}</h3></div>
        </FormGroup>
        <FormGroup>
          <div className='col-xs-12'>
            { this.props.poll.options.map((option) => <Radio model='.optionID' value={option.id} key={option.id}>{option.text}</Radio>) }
          </div>
        </FormGroup>
        <FormGroup>
          <div className='col-xs-12'>
            <button type='submit' className='btn btn-primary' disabled={this.state.loading}>Vote!</button>
          </div>
        </FormGroup>
        { this.state.loading && <Spinner /> }
      </LocalForm>
    );
  }
}
