# Poll Object

A Poll object contains the information about a poll in our system.
It contains the following keys:

| Key | Type | Description |
| --- | ---- | ----------- |
| `id` | Int | The database identifier. |
| `title` | String | The title of the poll. |
| `submitter` | Voter | The Representative who submitted the poll. |
| `options` | [PollOption] | The available options for the poll. This will not be empty. |

Example:

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
     "pollID": 1,
     "text": "Yes"
   },
   {
     "id": 2,
     "pollID": 1,
     "text": "No"
   },
   {
     "id": 3,
     "pollID": 1,
     "text": "Abstain"
   }
 ]
}
```