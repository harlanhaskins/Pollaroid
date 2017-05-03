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

    api('signup', values)
      .then((data) => {
        this.setState({
          loading: false,
        });

        if (data.uuid) {
          auth.login(data);
          this.setState({
            redirectToVote: true,
          });
          window.notificationSystem.addNotification({
            message: `Welcome to BiPoller, ${data.voter.name}!`,
            level: 'success',
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
          <Label htmlFor='name'>Name</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.name' />
          </InputWrapper>
        </FormGroup>
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
          <Label htmlFor='address'>Address</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.address' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='houseDistrictID'>House District</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.houseDistrictID' type='number' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='senateDistrictID'>Senate District</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.senateDistrictID' type='number' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='phoneNumber'>Phone Number</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.phoneNumber' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='representingDistrictID'>Representing District ID (optional)</Label>
          <InputWrapper>
            <Control className={INPUT_CLASS} model='.representingDistrictID' type='number' />
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <div className='col-sm-offset-2 col-sm-10'>
            <button type='submit' className='btn btn-primary' disabled={this.state.loading}>Sign Up</button>
          </div>
        </FormGroup>
        { this.state.loading && <Spinner /> }
      </LocalForm>
    );
  }
}
