import React from 'react';
import { Redirect } from 'react-router-dom';
import { LocalForm, Control } from 'react-redux-form';

import { Label, FormGroup, InputWrapper, INPUT_CLASS } from './helpers';
import Spinner from '../Spinner';
import api from '../api';

export default class PollForm extends React.Component {
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

    const valueData = {
      title: values.title,
      options: (values.options || '').split(','),
    };

    api('polls', valueData)
      .then((data) => {
        this.setState({
          loading: false,
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
          <Label htmlFor='title'>Title</Label>
          <InputWrapper>
            <Control.text className={INPUT_CLASS} model='.title' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='options'>Options</Label>
          <InputWrapper>
            <Control type='options' className={INPUT_CLASS} model='.options' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <div className='col-sm-offset-2 col-sm-10'>
            <button type='submit' className='btn btn-primary' disabled={this.state.loading}>Create Poll</button>
          </div>
        </FormGroup>
        { this.state.loading && <Spinner /> }
      </LocalForm>
    );
  }
}
