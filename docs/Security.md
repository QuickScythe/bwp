# Security

Overview
- This project includes bootstrap mechanisms intended for development/demo use. Harden before production.

Accounts and bootstrap admin
- On first initialization (when users.json is empty), Utils.init will:
  - Generate a random value, write it to adminPassword.txt (plaintext) in the data directory.
  - Derive an encrypted password from that value and create an "admin" user with ALL permissions.
  - Create a Token for admin with ALL permissions.
- Action items after first run (non-demo environments):
  - Delete adminPassword.txt or rotate credentials; do not keep plaintext secrets in place.
  - Change the admin password to a strong, unique password.
  - Create named, least-privileged accounts and tokens for automation.
  - Revoke the default admin token if not required.

Passwords and hashing
- Passwords are hashed using utilities from com.quiptmc.core.utils.HashUtils. In Utils.init a double-hash occurs; treat this as an implementation detail.
- Do not store plaintext passwords in users.json.

Tokens and permissions
- Tokens are opaque identifiers stored under User.tokens. Present them via a JSON body containing {"token":"<id>"}.
- Tokens carry their own permissions set; a Token with Permissions.ALL bypasses granular checks.
- Tokens can expire (field expiresAt). Expired tokens are pruned during lookup.

TMDB API token
- The Movie DB api_token is stored in config.json. Treat it as sensitive.
- Do not commit real api_token values to version control. Use environment-specific copies of data/config.json.

Filesystem and backups
- Protect the data directory with appropriate OS-level permissions.
- Backups should be encrypted and access-controlled. Avoid storing adminPassword.txt in long-term backups.

Secure deployment checklist
- Set -Dbwp.dataDir to a directory owned by the service user with least privileges.
- Provide a sanitized config.json with a valid TMDB api_token via secure secret management.
- Remove bootstrap artifacts (adminPassword.txt) and rotate default admin credentials.
- Enable HTTPS/SSL termination at the proxy or container level.
- Monitor /api/health and logs for anomalies.
