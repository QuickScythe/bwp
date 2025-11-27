# Development

Project setup
- Requirements: JDK 17+, Git, Gradle (or use the wrapper).
- Clone and open in IntelliJ IDEA (recommended) or your IDE of choice.

Build & run
- Run tests: gradlew.bat test (Windows) or ./gradlew test (Unix)
- Developer run: gradlew.bat bootRun or ./gradlew bootRun
- Build WAR: gradlew.bat clean bootWar or ./gradlew clean bootWar

Data directory for local runs
- Create a sanitized copy of data/ to avoid using any sensitive files.
- Point the app to it via one of:
  - Env var: BWP_DATA_DIR=C:\\tmp\\bwp-data (Windows) or /tmp/bwp-data (Unix)
  - Java prop: -Dbwp.dataDir=C:\\tmp\\bwp-data

Code organization
- com.bwp.Main: entry point and integration, resolves data dir.
- com.bwp.server: MVC and REST controllers (PageController, ApiController).
- com.bwp.data: domain and config classes (Talent, UsersConfig, TalentConfig, DefaultConfig).
- com.bwp.utils: utility functions and initialization helpers (Utils).
- com.bwp.components: resource/file helpers.
- src/main/resources/templates: Thymeleaf pages (home, talent, fragments).
- src/test/java: tests and helpers.

Adding an API endpoint
- Create a method in ApiController (or a new @RestController) with appropriate @GetMapping/@PostMapping.
- For secured operations, accept a token container JSON (e.g., {"token":"..."}) and call validateToken with required permissions.

Working with talents
- Ensure config.json->api_token is valid.
- To sync a new talent programmatically, use TalentConfig.actor(id). It will create data/actors/<id>/ and fetch the headshot.

Testing
- Keep tests deterministic and avoid real network calls; mock or provide stub data when possible.
- Use [DEBUG_LOG] prefixes in test logs when troubleshooting, as suggested in build tooling.

Coding standards
- Use consistent formatting and Java 17 language features when appropriate.
- Keep controllers thin; push logic into config/services/util classes.

Troubleshooting
- Data path issues: verify -Dbwp.dataDir or BWP_DATA_DIR and that the directory is writable.
- TMDB failures: check api_token in config.json and network reachability.
- Permission errors: ensure tokens carry required permissions; check users.json contents.
