# Pollaroid API Reference

Pollaroid is structured as a RESTful JSON API that supports secure authentication and signup.
All `GET`/`HEAD`/`DELETE` requests shall pass their parameters via form-encoded URL parameters.
All `POST` routes shall pass their parameters via a JSON-encoded `POST` body.

The API is broken up into the following routes:

## POST `/api/login`

Pollaroid uses BCrypt to hash and salt passwords, and stores the hashed and salted passwords in
the database. On `/api/login`, Pollaroid will return a JSON object with the following keys.

| Key | Type | Description |
| --- | ---- | ----------- |
| `uuid` | String | A unique key that's used to identify this user as an authorized voter. This is the API key that will be passed to routes that declare they require authentication. |
| `expiration` | UnixTimestamp | A decimal timestamp that represents the exact timestamp this access token will expire from the database. All requests that authenticate with this timestamp will extend its lifetime by an unspecified amount that is subject to change. |

### Example

#### `GET /api/login`
Request:

```json
{
    "email": "foo@bar.rip",
    "password": "p@ssword"
}
```

Response:

```json
{
    "uuid": "a0180894-f9af-48ea-bbea-80d038ffb636",
    "expiration": 1491497247.219000000
}
```

## POST `/api/signup`

A POST to `/api/signup` signs up a voter or representative with the system. It does not require authorization.
It requires the following parameters:

| Key | Type | Description |
| --- | ---- | ----------- |
| `name` | String | The user's full name. |
| `email` | String | The user's email. This will be used to authenticate the user. |
| `password` | String | The user's password. This will be used to authenticate the user, but will not be stored directly. |
| `address` | String | The user's full mailing address. |
| `houseDistrictID` | Int | The ID of the House district in which this user resides. |
| `senateDistrictID` | Int | The ID of the Senate district in which this user resides. |
| `phoneNumber` | String | The user's phone number. |
| `representingDistrictID` | Int? | The ID of the district this user represents. Optional. |

It will return an AccessToken object with a fully-formed Voter object representing the added user.

### Example

Request:

```json
{
  "name": "Harlan Haskins",
  "email": "harlan@csh.rit.edu",
  "password": "garbage",
  "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
  "houseDistrictID": 1,
  "senateDistrictID": 2,
  "phoneNumber": "8649189255",
  "representingDistrictID": 2
}
```

Response:

```json
{
  "uuid": "a0180894-f9af-48ea-bbea-80d038ffb636",
  "expiration": 1491497247.219000000,
  "voter": {
    "id": 1,
    "name": "Harlan Haskins",
    "houseDistrict": {
      "id": 1,
      "number": 1,
      "state": "New York",
      "house": false,
      "senate": true
    },
    "senateDistrict": {
      "id": 2,
      "number": 2,
      "state": "New York",
      "house": true,
      "senate": false
    },
    "phoneNumber": "8649189255",
    "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
    "email": "harlan@csh.rit.edu",
    "representingDistrict": {
      "id": 2,
      "number": 2,
      "state": "New York",
      "house": true,
      "senate": false
    }
  }
}
```

## GET `/api/voters`

A GET to `/api/voters` will produce a JSON array of all voters currently registered in the database. This is temporary, and will be removed.
It requires no parameters, and returns a JSON array of voters.

### Example:

Response:

```json
[
  {
    "id": 1,
    "name": "Harlan Haskins",
    "houseDistrict": {
      "id": 1,
      "number": 1,
      "state": "New York",
      "house": false,
      "senate": true
    },
    "senateDistrict": {
      "id": 2,
      "number": 2,
      "state": "New York",
      "house": true,
      "senate": false
    },
    "phoneNumber": "8649189255",
    "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
    "email": "harlan@csh.rit.edu",
    "representingDistrict": {
      "id": 2,
      "number": 2,
      "state": "New York",
      "house": true,
      "senate": false
    }
  },
  {
    "id": 2,
    "name": "Joshua Robbins",
    "houseDistrict": {
      "id": 1,
      "number": 1,
      "state": "New York",
      "house": false,
      "senate": true
    },
    "senateDistrict": {
      "id": 2,
      "number": 2,
      "state": "New York",
      "house": true,
      "senate": false
    },
    "phoneNumber": "5555555555",
    "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
    "email": "iconmaster@csh.rit.edu",
    "representingDistrict": null
  }
]
```

## GET `/api/polls`

This route gets all the polls for the logged-in voter's districts.
It accepts no parameters, and returns a JSON array of `Poll` objects.

### Example

Response:
```json
[
  {
    "id": 1,
    "submitter": {
      "id": 1,
      "name": "Harlan Haskins",
      "phoneNumber": "8649189255",
      "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
      "email": "harlan@csh.rit.edu",
      "houseDistrict": {
        "id": 1,
        "number": 1,
        "state": "NEW_YORK",
        "congressionalBody": "SENATE",
        "house": false,
        "senate": true
      },
      "senateDistrict": {
        "id": 2,
        "number": 2,
        "state": "NEW_YORK",
        "congressionalBody": "HOUSE",
        "house": true,
        "senate": false
      },
      "representingDistrict": {
        "id": 2,
        "number": 2,
        "state": "NEW_YORK",
        "congressionalBody": "HOUSE",
        "house": true,
        "senate": false
      }
    },
    "district": {
      "id": 2,
      "number": 2,
      "state": "NEW_YORK",
      "congressionalBody": "HOUSE",
      "house": true,
      "senate": false
    },
    "title": "How should I vote on HR1136?",
    "options": [
      {
        "id": 1,
        "text": "Yes"
      },
      {
        "id": 2,
        "text": "No"
      },
      {
        "id": 3,
        "text": "Abstain"
      }
    ]
  }
]
```

## POST `/api/polls`

This will create a poll in the representative's district with the following parameters:

| Key | Type | Description |
| --- | ---- | ----------- |
| `title` | String | The title of the poll |
| `options` | [String] | The possible options for this poll. |

It will return a full Poll object back.

### Example

Request:

```json
{
  "title": "How should I vote on HR1136?",
  "options": [
    "Yes", "No", "Abstain"
  ]
}
```

Response:

```json
{
  "id": 1,
  "submitter": {
    "id": 1,
    "name": "Harlan Haskins",
    "phoneNumber": "8649189255",
    "address": "1 Lomb Memorial Drive, Rochester, NY 14623",
    "email": "harlan@csh.rit.edu",
    "houseDistrict": {
      "id": 1,
      "number": 1,
      "state": "NEW_YORK",
      "congressionalBody": "SENATE",
      "house": false,
      "senate": true
    },
    "senateDistrict": {
      "id": 2,
      "number": 2,
      "state": "NEW_YORK",
      "congressionalBody": "HOUSE",
      "house": true,
      "senate": false
    },
    "representingDistrict": {
      "id": 2,
      "number": 2,
      "state": "NEW_YORK",
      "congressionalBody": "HOUSE",
      "house": true,
      "senate": false
    }
  },
  "district": {
    "id": 2,
    "number": 2,
    "state": "NEW_YORK",
    "congressionalBody": "HOUSE",
    "house": true,
    "senate": false
  },
  "title": "How should I vote on HR1136?",
  "options": [
    {
      "id": 1,
      "text": "Yes"
    },
    {
      "id": 2,
      "text": "No"
    },
    {
      "id": 3,
      "text": "Abstain"
    }
  ]
}
```

## POST `/api/polls/:id/responses`

This will record a response to the provided Poll.

This will fail if:
    - The Poll with the provided ID does not exist.
    - The voter is not logged in
    - The voter casting the vote does not reside in the district the
      poll was created in.
      
This route will accept the following parameters:

| Key | Type | Description |
|-----|------|-------------|
| `optionID` | Int | The unique ID of the specific poll option being chosen. |
      
If successful, this will return a 204 Success No Data response.

### Example

Request:

```json
{
  "optionID": 4
}
```

Response:

```
HTTP 204 Success No Data
```

## GET `/api/polls/:id/responses`

This will return a list of all responses to a provided poll.
Note: This will return a 401 Unauthorized response if the current user
is not the representative who submitted the poll. It does not expect any
parameters.

This will return a JSON array of `PollRecord` objects, corresponding to the
votes cast.