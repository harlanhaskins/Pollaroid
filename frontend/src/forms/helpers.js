import React from 'react';
import { Control } from 'react-redux-form';

export const INPUT_CLASS = 'form-control';

export const Label = ({htmlFor, children}) => <label className='col-sm-3 col-md-2 control-label' htmlFor={htmlFor}>{children}</label>;
export const FormGroup = ({children}) => <div className='form-group'>{children}</div>;
export const InputWrapper = ({children}) => <div className='col-sm-9 col-md-10'>{children}</div>;
export const Radio = ({children, model, value}) => <div className='radio disabled'><label><Control.radio model={model} name={model} value={value} />{children}</label></div>;
