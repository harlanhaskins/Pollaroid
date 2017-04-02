import React from 'react';
import { Nav, Navbar, NavDropdown, NavItem, MenuItem } from 'react-bootstrap';

export default (
  <Navbar inverse collapseOnSelect fixedTop>
    <Navbar.Header>
      <Navbar.Brand>
        <a href="/">BiPoller</a>
      </Navbar.Brand>
      <Navbar.Toggle />
    </Navbar.Header>
    <Navbar.Collapse>
      <Nav>
        <NavItem eventKey={1} href="#">Vote</NavItem>
        <NavItem eventKey={2} href="#">Explore</NavItem>
      </Nav>
      <Nav pullRight>
        <NavItem eventKey={1} href="#">Sign up</NavItem>
        <NavItem eventKey={2} href="#">Log In</NavItem>
      </Nav>
    </Navbar.Collapse>
  </Navbar>
);
