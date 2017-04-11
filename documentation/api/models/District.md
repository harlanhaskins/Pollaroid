# District Object

A District object contains the information about a registered district in our system.
It contains the following keys:

| Key | Type | Description |
| --- | ---- | ----------- |
| `id` | Int | The database identifier. |
| `number` | Int | The state-wide district number. |
| `stateName` | String | A human-readable name of this District's state. |
| `stateCode` | String | A two-letter code of this District's state. |
| `house` | Boolean | Whether this district is a House district. |
| `senate` | Boolean | Whether this district is a Senate district. |