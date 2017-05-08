import React, { Component } from 'react';
import { Link } from 'react-router-dom';

const TableHeaders = ({headers, detailLink, miscColumn}) => {
  return <thead>
    <tr>
      { headers.map((header) => <th key={header}>{header}</th>) }
      { !!miscColumn && <th key='misc' /> }
      { detailLink && <th key='link' /> }
    </tr>
  </thead>;
};
TableHeaders.propTypes = {
  headers: React.PropTypes.arrayOf(React.PropTypes.string),
  detailLink: React.PropTypes.bool,
  miscColumn: React.PropTypes.func,
};

const TableBody = ({headers, data, detailLink, miscColumn}) => {
  return <tbody>
    {data.map((item, index) => {
      return <tr key={index}>
        { headers.map((header) => <td key={header}>{item[header] + ''}</td>) }
        { !!miscColumn && <td key='misc'>{miscColumn(item)}</td> }
        { detailLink && <td key='link'><Link to={`/polls/${item.id}`} className='btn btn-info btn-xs'>Details<span className='fa fa-chevron-right icon-space-l' /></Link></td> }
      </tr>;
    })}
  </tbody>;
};
TableBody.propTypes = {
  headers: React.PropTypes.arrayOf(React.PropTypes.string),
  data: React.PropTypes.arrayOf(React.PropTypes.object),
  detailLink: React.PropTypes.bool,
  miscColumn: React.PropTypes.func,
};

class ApiTable extends Component {
  render() {
    if (this.props.data.length === 0) {
      return <div>No data.</div>;
    }

    const headers = this.props.headers || Object.keys(this.props.data[0]);
    return <table className='table table-striped'>
      <TableHeaders headers={headers} detailLink={this.props.detailLink} miscColumn={this.props.miscColumn} />
      <TableBody headers={headers} data={this.props.data} detailLink={this.props.detailLink} miscColumn={this.props.miscColumn} />
    </table>;
  }
}
ApiTable.propTypes = {
  data: React.PropTypes.arrayOf(React.PropTypes.object),
  detailLink: React.PropTypes.bool,
  miscColumn: React.PropTypes.func,
  headers: React.PropTypes.arrayOf(React.PropTypes.string),
};

export default ApiTable;
