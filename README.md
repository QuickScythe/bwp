# BWP (Beast Within Productions)

## Project intent (important)

This repository contains the source code for a client project. The code is published publicly to allow others to read, learn from, and reference implementation patterns. However, this project is *not intended* to be built, deployed, or used by third parties for their own purposes. The primary audience is the client and authorized maintainers.

If you are browsing this repository to learn, feel free to read the code, review patterns, and run safe read-only operations (for example, run unit tests or inspect templates). Do not deploy this service against production systems or real client data, and do not use any credentials or files found in the repo (they may be placeholders or client-specific). If you need to run the app locally for learning, follow the "For maintainers" instructions below and use only sanitized configuration and test data.

---

A Spring Boot web application for managing and displaying talent/actor data, packaged as a deployable WAR for Tomcat. The project leverages Thymeleaf for templating, supports scheduled tasks, and integrates with the QuiptMC core library.

## Features
- Home and talent web pages (Thymeleaf-based)
- Loads and displays talent/actor data from configuration files
- Queue management and scheduled background tasks
- Logging to both console and rolling file (Tomcat-compatible)
- WAR packaging for easy deployment to Tomcat

## Directory Structure
```
├── build.gradle           # Gradle build file
├── data/                  # Application data and configuration
│   ├── config.json
│   ├── talents.json
│   └── actors/            # Per-actor data folders
├── src/
│   ├── main/
│   │   ├── java/com/bwp/  # Main Java source code
│   │   │   ├── Main.java
│   │   │   ├── server/PageController.java
│   │   │   ├── data/      # Data models and config
│   │   │   ├── components/
│   │   │   ├── utils/
│   │   │   └── ...
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── templates/ # Thymeleaf templates
│   └── test/              # Unit tests
├── logs/                  # Application logs
└── ...
```

## Build & Run

> NOTE: The instructions below are intended for maintainers or authorized developers only. This repository is published for learning/reference; it is not intended as a consumer-buildable distribution.

### Prerequisites
- Java 17+
- Gradle (or use the provided `gradlew` wrapper)

### Build WAR (maintainer)
```
./gradlew clean bootWar
```
The WAR will be output to `build/libs/ROOT.war` (for ROOT context deployment).

### Deploy to Tomcat (maintainer)
1. Copy `ROOT.war` to your Tomcat `webapps/` directory (deploys at `/`), or use the provided GitHub Actions workflow.
2. Ensure your Tomcat user has the `manager-script` role if using the Manager API.

### Configuration
- Application data directory (where `config.json`, `talents.json`, and `actors/` live) is configurable via:
  - System property: `-Dbwp.dataDir=/absolute/path/to/bwp-data`
  - Environment variable: `BWP_DATA_DIR=/absolute/path/to/bwp-data`
  - Defaults to `${catalina.base}/bwp-data` on Tomcat, then `${user.home}/.bwp/data`, then `./data`.
- Logging is configured via `src/main/resources/logback.xml`.

## Endpoints
- `/` — Home page
- `/talent` — Talent listing page (shows all actors from configuration)

## Development
- Main entry point: `com.bwp.Main`
- Main controller: `com.bwp.server.PageController`
- Queue management: `com.bwp.utils.queue.QueueManager`

## Quick exploration (safe/read-only)
If you want to explore the project without building or deploying:
- Read template files in `src/main/resources/templates` to see UI structure.
- Inspect `src/main/java` for controller and service patterns.
- Run unit tests only (see below) and do not point tests to production systems or real client data.

## Testing
Run unit tests with:
```
./gradlew test
```

## Security & sensitive data
This repository may contain configuration placeholders and client-specific files. Do not assume any credentials in the repo are safe to use. If you discover secrets or sensitive data, contact the repository maintainer or client and do not share the data publicly.

## Contributing
The source is public to help others learn. External contributions are welcome as improvements or fixes, but keep in mind:
- Pull requests will be reviewed by maintainers; acceptance is at their discretion.
- Do not submit changes that include credentials or client data.
- For major changes, open an issue first and coordinate with maintainers/clients.

## License
Specify your license here (e.g., MIT, Apache 2.0, etc.)

---

## Thymeleaf: iterating a Map from the Model
If a controller adds a `Map<String,Object>` to the model (for example `model.addAttribute("myMap", myMap);`), you can iterate its keys and values in Thymeleaf like this:

Inline example (entrySet):

```html
<ul>
  <li th:each="entry : ${myMap.entrySet()}">
    <strong th:text="${entry.key}">key</strong>: <span th:text="${entry.value}">value</span>
  </li>
</ul>
```

Or using Thymeleaf's maps utility:

```html
<table>
  <tr th:each="kv : ${#maps.entries(myMap)}">
    <td th:text="${kv.key}">key</td>
    <td th:text="${kv.value}">value</td>
  </tr>
</table>
```

Tip: use a `LinkedHashMap` if you want to preserve insertion order when iterating.

## Running locally with file-based data (maintainers)
This project is moving to local file storage. To run the app locally against a sanitized copy of the `data/` folder, follow these steps. Examples below include Windows `cmd.exe` and Unix shells.

1) Create a sanitized copy of `data/` (only keep non-sensitive test data). Do *not* copy production or client-only files.

Windows (cmd.exe):
```cmd
mkdir C:\tmp\bwp-data
xcopy /E /I data C:\tmp\bwp-data
rem Manually remove or replace any sensitive files in C:\tmp\bwp-data
```

Unix (bash):
```bash
mkdir -p /tmp/bwp-data
cp -r data/* /tmp/bwp-data/
# Manually remove or replace any sensitive files in /tmp/bwp-data
```

2) Point the app to the sanitized data directory and run (use `bootRun` for a quick developer start or `bootWar` to build the WAR):

Windows (cmd.exe):
```cmd
set BWP_DATA_DIR=C:\tmp\bwp-data
gradlew.bat bootRun
```

Unix (bash):
```bash
export BWP_DATA_DIR=/tmp/bwp-data
./gradlew bootRun
```

If you prefer to build the WAR instead of running the app directly:

Windows (cmd.exe):
```cmd
set BWP_DATA_DIR=C:\tmp\bwp-data
gradlew.bat clean bootWar
```

Unix (bash):
```bash
export BWP_DATA_DIR=/tmp/bwp-data
./gradlew clean bootWar
```

3) Open the web UI (by default) at http://localhost:8080/ after `bootRun` completes.

Notes and safety
- Always use sanitized, non-production data when running locally.
- Do not commit sanitized copies that contain client or secret data into the repository.
- If your local environment uses a different port or additional JVM options, pass them via `--args` or `JAVA_OPTS` as needed.

