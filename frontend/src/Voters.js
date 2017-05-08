import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import DataLoader from './DataLoader';
import ApiTable from './ApiTable';
import api from './api';
import auth from './auth';

const downloadData = (filename, data) => {
  // Source: http://stackoverflow.com/questions/3665115/create-a-file-in-memory-for-user-to-download-not-through-server
  const blob = new window.Blob([JSON.stringify(data)], {type: 'text/json'});
  if (window.navigator.msSaveOrOpenBlob) {
    window.navigator.msSaveBlob(blob, filename);
  } else {
    var elem = window.document.createElement('a');
    elem.href = window.URL.createObjectURL(blob);
    elem.download = filename;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
  }
};

class Voters extends Component {
  constructor() {
    super();

    this.state = {
      data: [],
      loaded: false,
    };
  }

  componentDidMount() {
    if (!auth.isRepresentative()) {
      return;
    }

    api('voters').then((data) => {
      data.forEach((item) => {
        item.houseDistrict = item.houseDistrict ? item.houseDistrict.id : '';
        item.senateDistrict = item.senateDistrict ? item.senateDistrict.id : '';
        item.representingDistrict = item.representingDistrict ? item.representingDistrict.id : '';
      });

      this.setState({
        data,
        loaded: true,
      });
    });
  }

  render() {
    const headers = auth.isRepresentative() ? ['name', 'email', 'address', 'phoneNumber', 'houseDistrict', 'senateDistrict', 'representingDistrict'] : ['title'];
    return <div className='container'>
      <div className='starter-template'>
        <h1>Voters</h1>
        { auth.isRepresentative()
          ? <DataLoader loaded={this.state.loaded}>
            <div>
              <p><button className='btn btn-info btn-sm' onClick={() => downloadData('voters.json', this.state.data)}><span className='fa fa-download icon-space-r' />Download data</button></p>
              <ApiTable data={this.state.data} detailLink={false} headers={headers} />
            </div>
          </DataLoader>
          : <p>You must be a representative to use this page.</p> }
      </div>
    </div>;
  }
}

export default Voters;
