# Configuration

Data directory resolution
The application stores its JSON config and external resources in a single "data directory". The directory is resolved in this order (first non-empty wins):
1) Java system property: -Dbwp.dataDir=... 
2) Environment variable: BWP_DATA_DIR=...
3) If running on Tomcat: ${catalina.base}/bwp-data
4) ${user.home}/.bwp/data
5) ./data (current working directory)

Initial setup
- On first run, the Integration component ensures the data directory exists.
- Utils.init registers config classes and bootstraps default data. If users.json is empty, it will:
  - Create adminPassword.txt with a random value (stored in plaintext) in the data directory.
  - Hash the value twice and create an admin user with that password and ALL permissions.
  - Create an API token for the admin with ALL permissions.

Config files (JSON)
- config.json (DefaultConfig)
  - api_token: String, TheMovieDB (TMDB) API token used for fetching profiles and images.
- talents.json (TalentConfig)
  - Map of Talent objects keyed by their id (TMDB person id). See DataModel.md.
- users.json (UsersConfig)
  - Map of User objects keyed by UUID id. See DataModel.md.

Environment variables and properties
- bwp.dataDir (system property) → absolute path to data directory
- BWP_DATA_DIR (environment) → absolute path to data directory

Logging
- Logback configuration is in src/main/resources/logback.xml.
- Logs are emitted under logs/ when running locally, or Tomcat’s logs when deployed.

TMDB networking
- Utils.GET() builds HttpConfig using api_token from config.json as a Bearer token header.
- Ensure config.json contains a valid token before calling endpoints that fetch remote data (e.g., adding a new Talent).
