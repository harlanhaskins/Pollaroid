import React from 'react';

export const INPUT_CLASS = 'form-control';

export const Label = ({htmlFor, children}) => <label className='col-sm-2 control-label' htmlFor={htmlFor}>{children}</label>;
export const FormGroup = ({children}) => <div className='form-group'>{children}</div>;
export const InputWrapper = ({children}) => <div className='col-sm-10'>{children}</div>;
