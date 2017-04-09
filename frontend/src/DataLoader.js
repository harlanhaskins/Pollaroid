import React from 'react';
import Spinner from './Spinner';

const DataLoader = ({loaded, children}) => {
  if (!loaded) {
    return <div><Spinner /></div>;
  }

  return children;
};

export default DataLoader;
