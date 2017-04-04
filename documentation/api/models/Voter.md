# Voter Object

A Voter object contains the information about a registered voter in our system.
It contains the following keys:

| Key | Type | Description |
| --- | ---- | ----------- |
| `id` | Int | The database identifier. |
| `name` | Int | The voter's full name. |
| `email` | String | The voter's email address. |
| `address` | Boolean | The voter's address. |
| `phoneNumber` | String | The user's phone number. |
| `houseDistrict` | District | The House District in which this voter resides. |
| `senateDistrict` | District | The Senate district in which this voter resides. |
| `representingDistrict` | District? | The District in which this voter is a representative. Optional; will only be provided if the voter is a Representative. |