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
      districtsLoading: true,
      districts: [],
      houseChosen: null,
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

  loadDistricts() {
    api('districts')
      .then((data) => {
        this.setState({
          districtsLoading: false,
          districts: data,
        });
      });
  }

  componentDidMount() {
    this.loadDistricts();
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
            <Control.select className={INPUT_CLASS} model='.houseDistrictID' disabled={this.state.districtsLoading} title={this.state.districtsLoading ? 'Loading districts, please wait...' : ''}>
              <option>Select one...</option>
              { !this.state.districtsLoading && this.state.districts.filter((district) => district.house).map((district) => <option key={district.id} value={district.id}>{`${district.stateCode} ${district.congressionalBody} District ${district.number}`}</option>) }
            </Control.select>
          </InputWrapper>
        </FormGroup>
        <FormGroup>
          <Label htmlFor='senateDistrictID'>Senate District</Label>
          <InputWrapper>
            <Control.select className={INPUT_CLASS} model='.senateDistrictID' disabled={this.state.districtsLoading} title={this.state.districtsLoading ? 'Loading districts, please wait...' : ''}>
              <option>Select one...</option>
              { !this.state.districtsLoading && this.state.districts.filter((district) => district.senate).map((district) => <option key={district.id} value={district.id}>{`${district.stateCode} ${district.congressionalBody} District ${district.number}`}</option>) }
            </Control.select>
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
          <div className='col-sm-offset-3 col-sm-9 col-md-offset-2 col-md-10'>
            <button type='submit' className='btn btn-primary' disabled={this.state.loading}>Sign Up</button>
          </div>
        </FormGroup>
        { this.state.loading && <Spinner /> }
      </LocalForm>
    );
  }
}
