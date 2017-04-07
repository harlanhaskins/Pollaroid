# Poll Record Object

A Poll Record is a recorded vote on a poll for a given voter.

It shall have the following keys:

| Key | Type | Description |
| --- | ---- | ----------- |
| `id` | Int | The database identifier. |
| `voter` | Voter | The voter who cast this vote. |
| `poll` | Poll | The poll for which this vote was cast. |
| `choice` | PollOption | The option chosen for this poll. |