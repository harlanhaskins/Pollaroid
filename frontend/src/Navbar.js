import React from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import {
  Route,
  Link
} from 'react-router-dom';

import auth from './auth';

const BootstrapLink = ({ label, to, activeOnlyWhenExact }) => (
  <Route path={to} exact={activeOnlyWhenExact} children={({ match }) => (
    <li className={match ? 'active' : ''}>
      <Link to={to}>{label}</Link>
    </li>
  )} />
);

export default ({ loggedIn }) => {
  return <Navbar collapseOnSelect fixedTop>
    <Navbar.Header>
      <Navbar.Brand>
        <Link to='/'>Pollaroid</Link>
      </Navbar.Brand>
      <Navbar.Toggle />
    </Navbar.Header>
    <Navbar.Collapse>
      <Nav>
        <BootstrapLink to='/polls' label='Polls' />
        { loggedIn && auth.isRepresentative() && <BootstrapLink to='/voters' label='Voters' /> }
      </Nav>
      { loggedIn && <Nav pullRight>
        <Navbar.Text>
          Welcome back, {auth.getData().voter.name}!
        </Navbar.Text>
        <li>
          <a onClick={() => auth.logout()} href='#'>Log Out</a>
        </li>
      </Nav> }
      { !loggedIn && <Nav pullRight>
        <BootstrapLink to='/signup' label='Sign Up' />
        <BootstrapLink to='/login' label='Sign In' />
      </Nav> }
    </Navbar.Collapse>
  </Navbar>;
};
