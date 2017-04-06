import React from 'react';
import { Nav, Navbar } from 'react-bootstrap';
import {
  Route,
  Link
} from 'react-router-dom';

const BootstrapLink = ({ label, to, activeOnlyWhenExact }) => (
  <Route path={to} exact={activeOnlyWhenExact} children={({ match }) => (
    <li className={match ? 'active' : ''}>
      <Link to={to}>{label}</Link>
    </li>
  )} />
);

export default () => {
  return <Navbar collapseOnSelect fixedTop>
    <Navbar.Header>
      <Navbar.Brand>
        <Link to='/'>BiPoller</Link>
      </Navbar.Brand>
      <Navbar.Toggle />
    </Navbar.Header>
    <Navbar.Collapse>
      <Nav>
        <BootstrapLink to='/vote' label='Vote' />
        <BootstrapLink to='/explore'label='Explore' />
      </Nav>
      <Nav pullRight>
        <BootstrapLink to='/signup' label='Sign Up' />
        <BootstrapLink to='/login' label='Log In' />
      </Nav>
    </Navbar.Collapse>
  </Navbar>;
};
