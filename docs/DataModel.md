# Data Model

This document describes the main persisted models and their JSON representations as stored by the QuiptMC Config framework in the data directory.

Conventions
- Configs are defined with @ConfigTemplate(name=..., ext=JSON) and stored as <name>.json under the data directory.
- Collections use ConfigMap<T>, persisted as JSON objects keyed by the element id.

User (com.bwp.data.account.User)
- File: users.json (managed by UsersConfig)
- Fields
  - id: String (UUID) – generated when creating a user
  - username: String
  - password: String – encrypted/hashed value; see Security.md
  - permissions: Map<String, Permission> – named permission entries
  - tokens: Map<String, Token> – API tokens bound to the user
- Typical JSON shape
```json
{
  "users": {
    "c0de21f8-9f6a-4afd-b905-4a9efb83b6e1": {
      "id": "c0de21f8-9f6a-4afd-b905-4a9efb83b6e1",
      "username": "admin",
      "password": "<hashed>",
      "permissions": { "ALL": { "id": "ALL" } },
      "tokens": { "<tokenId>": { "id": "<tokenId>", "expiresAt": 1735689600000, "permissions": { "ALL": { "id": "ALL" } } } }
    }
  }
}
```

Token (com.bwp.utils.secrets.Token)
- Stored within User.tokens map keyed by token id.
- Fields
  - id: String – presented by clients
  - expiresAt: long/epoch – expiry time
  - permissions: Map<String, Permission>
- Behavior
  - expired(): returns true if token is past expiry; UsersConfig removes expired tokens upon lookup.

Permission (com.bwp.utils.secrets.Permission)
- Represents a named capability; Permissions.ALL is a wildcard.
- Stored in both User.permissions and Token.permissions.

Talent (com.bwp.data.Talent)
- File: talents.json (managed by TalentConfig)
- Key: Talent.id (TMDB person id)
- Fields (subset, subject to Talent implementation)
  - id: int/String – TMDB person id
  - firstName, lastName, fullName() derived
  - credits: array/map of roles (Talent.Credit)
- Side effects
  - Each talent has a folder under data/actors/<id>/
  - Headshot image saved as headshot.jpg in that folder when synchronized.

DefaultConfig (com.bwp.data.config.DefaultConfig)
- File: config.json
- Fields
  - api_token: String – Bearer token for TMDB HTTP requests.

Filesystem layout recap
- data/
  - config.json (DefaultConfig)
  - users.json (UsersConfig)
  - talents.json (TalentConfig)
  - actors/
    - <tmdb_person_id>/
      - headshot.jpg
  - adminPassword.txt (plaintext bootstrap secret; see Security.md)
