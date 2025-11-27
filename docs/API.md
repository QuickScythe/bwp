# API

Base path: /api

Endpoints
- GET /api
  - Returns: plain text "BWP Server API is running."
  - Use: Basic readiness check.
- GET /api/health
  - Returns: JSON with status and timestamp
  - Example:
    {
      "status": "OK",
      "timestamp": "2025-11-01T02:50:00Z"
    }

Token validation (internal helper)
- ApiController.validateToken(tokenContainerJson, requiredPermissions...)
  - tokenContainerJson must be a JSON string with a "token" field, e.g., {"token":"<tokenId>"}
  - Looks up the token in UsersConfig; rejects if not found or expired.
  - Returns the Token if it carries ALL required permissions or the ALL wildcard.
  - Throws 400 if the container format is invalid; 401 if token not found.

Common patterns for secured endpoints
- Accept a JSON body or header containing the presented token id.
- Parse into a token container JSON string and call validateToken(..., Permissions.SOME_PERMISSION).
- Perform action only if a valid token with sufficient permissions is returned.

Notes
- No mutating API endpoints are published in this repository by default.
- Add new endpoints under /api with appropriate @PostMapping/@PutMapping as needed, and reuse validateToken for authorization.
