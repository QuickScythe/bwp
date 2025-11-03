# Operations

Health and readiness
- GET /api/health returns a JSON object with status and timestamp. Use for uptime checks.
- GET /api returns plain text and can serve as a minimal readiness probe.

Logs
- Logback configuration is in src/main/resources/logback.xml.
- Local development: logs/ directory under the project root (or console) depending on config.
- Tomcat: application logs are typically under TOMCAT_HOME/logs; ensure the app has permission to write if configured that way.

Data backups
- The application is stateful via the data directory. Back up the entire data directory regularly.
- Minimum set to back up:
  - config.json, users.json, talents.json
  - actors/ subfolders
  - Consider excluding adminPassword.txt from backups or securing it separately, as it is a bootstrap artifact.

Restore procedures
- Stop the application.
- Restore the backed-up data directory to the configured path.
- Start the application and verify /api/health and UI pages.

Rotations and maintenance
- TMDB token rotation: update config.json->api_token and restart the app if necessary.
- Admin bootstrap rotation: see Security.md for guidance on regenerating admin and revoking tokens.
- Talent images: re-sync a talent by using the TalentConfig actor(...) flow in code, or by implementing an administrative endpoint invoking TalentConfig.actor(id).

Monitoring
- Track response times and error rates on:
  - GET / and GET /talent (UI)
  - GET /api/health (API)
- Watch disk usage of data directory; image storage under actors/ can grow.
