import React, { Component } from 'react';
import { Link } from 'react-router-dom';

const TableHeaders = ({headers, detailLink}) => {
  return <thead>
    <tr>
      { headers.map((header) => <th key={header}>{header}</th>) }
      { detailLink && <th key='link' /> }
    </tr>
  </thead>;
};
TableHeaders.propTypes = {
  headers: React.PropTypes.arrayOf(React.PropTypes.string),
  detailLink: React.PropTypes.bool,
};

const TableBody = ({headers, data, detailLink}) => {
  return <tbody>
    {data.map((item, index) => {
      return <tr key={index}>
        { headers.map((header) => <td key={header}>{item[header] + ''}</td>) }
        { detailLink && <td key='link'><Link to={`/vote/${item.id}`} className='btn btn-info btn-xs'>Details<span className='fa fa-chevron-right icon-space-l' /></Link></td> }
      </tr>;
    })}
  </tbody>;
};
TableBody.propTypes = {
  headers: React.PropTypes.arrayOf(React.PropTypes.string),
  data: React.PropTypes.arrayOf(React.PropTypes.object),
  detailLink: React.PropTypes.bool,
};

class ApiTable extends Component {
  render() {
    if (this.props.data.length === 0) {
      return <div>No data.</div>;
    }

    const headers = Object.keys(this.props.data[0]);
    return <table className='table table-striped'>
      <TableHeaders headers={headers} detailLink={this.props.detailLink} />
      <TableBody headers={headers} data={this.props.data} detailLink={this.props.detailLink} />
    </table>;
  }
}
ApiTable.propTypes = {
  data: React.PropTypes.arrayOf(React.PropTypes.object),
  detailLink: React.PropTypes.bool,
};

export default ApiTable;
