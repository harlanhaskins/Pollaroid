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
      options: [1, 2],
      nextOption: 3,
    };
  }

  handleSubmit(values) {
    this.setState({
      loading: true,
    });

    const valueData = {
      title: values.title,
      options: (this.state.options || []).map((number) => values.options[number]),
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

  addOption() {
    const optionsCopy = this.state.options.slice();
    optionsCopy.push(this.state.nextOption);
    this.setState({
      options: optionsCopy,
      nextOption: this.state.nextOption + 1,
    });
  }

  removeOption(number) {
    const optionsCopy = this.state.options.slice();
    const index = this.state.options.indexOf(number);
    optionsCopy.splice(index, 1);
    this.setState({
      options: optionsCopy,
      nextOption: this.state.nextOption + 1,
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
            { this.state.options.map((number) => <p key={number}>
              <div className='input-group'>
                <Control type='options' className={INPUT_CLASS} model={`.options[${number}]`} />
                <span className='input-group-btn'>
                  <button className='btn btn-default' type='button' onClick={() => this.removeOption(number)} tabIndex='-1'><span className='fa fa-close' /></button>
                </span>
              </div>
            </p>) }
            <p><button type='button' onClick={() => this.addOption()} className='btn btn-info btn-sm'><span className='fa fa-plus icon-space-r' />Add Option</button></p>
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
