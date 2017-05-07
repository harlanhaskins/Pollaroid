import React from 'react';
import { Redirect } from 'react-router-dom';
import { LocalForm, Control } from 'react-redux-form';

import { Label, FormGroup, InputWrapper, INPUT_CLASS } from './helpers';
import Spinner from '../Spinner';
import api from '../api';
import auth from '../auth';

export default class SignupForm extends React.Component {
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

    api('login', values)
      .then((data) => {
        this.setState({
          loading: false,
        });

        if (data.uuid) {
          auth.login(data);
          this.setState({
            redirectToVote: true,
          });
        }
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
          <Label htmlFor='email'>Email</Label>
          <InputWrapper>
            <Control.text className={INPUT_CLASS} model='.email' type='email' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='password'>Password</Label>
          <InputWrapper>
            <Control type='password' className={INPUT_CLASS} model='.password' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <div className='col-sm-offset-3 col-sm-9 col-md-offset-2 col-md-10'>
            <button type='submit' className='btn btn-primary' disabled={this.state.loading}>Sign In</button>
          </div>
        </FormGroup>
        { this.state.loading && <Spinner /> }
      </LocalForm>
    );
  }
}
