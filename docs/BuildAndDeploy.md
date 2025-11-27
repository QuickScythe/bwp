# Build and Deploy

Prerequisites
- JDK 17+
- Gradle 8.x (or use the included gradlew/gradlew.bat wrapper)

Build artifacts
- The project is configured to produce a WAR suitable for Tomcat.
- Main class: com.bwp.Main (configured in build.gradle)

Build commands
- Clean and build WAR:
  - Windows: gradlew.bat clean bootWar
  - Unix: ./gradlew clean bootWar
- Run tests:
  - Windows: gradlew.bat test
  - Unix: ./gradlew test
- Developer run (no WAR):
  - Windows: gradlew.bat bootRun
  - Unix: ./gradlew bootRun

Data directory
- Set the data directory via one of:
  - Java property: -Dbwp.dataDir=C:\\path\\to\\bwp-data
  - Env var (Windows): set BWP_DATA_DIR=C:\\path\\to\\bwp-data
  - Env var (Unix): export BWP_DATA_DIR=/path/to/bwp-data

Deploy to Tomcat
1) Copy build/libs/ROOT.war to TOMCAT_HOME/webapps/ to deploy at root context (/).
2) Ensure a writable data directory is configured on the server (e.g., -Dbwp.dataDir=/var/lib/bwp-data).
3) Verify logs and /api/health after startup.

Logging and monitoring
- See Operations.md for log locations and health checks.

CI/CD
- If using automation, ensure the pipeline supplies sanitized data and excludes any sensitive files from the repo (see Security.md).
