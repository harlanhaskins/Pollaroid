# Pollaroid
[![CircleCI](https://circleci.com/gh/harlanhaskins/Pollaroid.svg?style=svg&circle-token=e813a3ac50a90a344f7601d537115f954efa34f6)](https://circleci.com/gh/harlanhaskins/Pollaroid)

| Leader | Design Engineer | Test Engineer | UI Engineer |
|--------|-----------------|---------------|-------------|
| Harlan Haskins | Joshua Robbins | Lucas Shadler | Stuart Olivera |

Domain: https://pollaroid.club

---

Pollaroid is a powerful tool for public officials and their campaigns to connect and survey the voters they represent. Using Pollaroid, officials will have access to voter registration data, including the contact information, of every eligible voter in their district. When an official signs up, their team gains access to a number of tools: canvassing organization tools that help them manage who calls whom and keep track of their results, survey creation tools that enable broad-sampling of public opinion, and direct connections with specific voters who register in the system.

Voters will also interact with the Pollaroid system. Voters will be able to create accounts for themselves, where they will be able to vote on the surveys created by their representatives. This will allow the tool to aggregate data and display the concerns of the people on a visual map of the area. Public officials can then use the broad opinion information to inform their messaging and voting behavior.

Users, once they sign up for Pollaroid, will also be able to directly connect to their representatives. This will give public officials unprecedented transparency and trust with their constituents. We’ll display a public voting record next to the officials’ profiles that can be cross-referenced in messages to constituents.

Pollaroid is a tool for both voters and the people who represent them. Voters get to have their voices heard through polls and messages, and representatives get valuable feedback from for their campaigns. It will help foster a connection and transparency between the two groups that is so often nonexistent, which we hope will positively affect political process and inspire more people to be active in their government.

## Instructions

### Website 

The simplest way to play with Pollaroid is by logging onto the website at
https://pollaroid.club. If you'd prefer to build the project, it's simple
enough to build.

### Building

Pollaroid is built with the `gradle` build system. To run the server, first
you'll want to populate it with dummy data by running this command:

```bash
./gradlew populate
```

This will create hundreds of representatives, districts, voters, and polls
for the database. To run the server, just run:

```bash
./gradlew run
```

This will run the server locally on port 8080.

### Building the Web Frontend

The web frontend is a React web application that's built using `webpack`. To
install all the dependencies necessary for the frontend, ensure you have `npm`
installed and available in your PATH, then navigate to the
`frontend` directory and run:

```bash
npm install
npm run build
```

Then you should be able to open `frontend/build/index.html` and poke around the
frontend.
