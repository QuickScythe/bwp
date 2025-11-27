# Architecture

Overview
- BWP is a Spring Boot web application packaged as a WAR for deployment on a servlet container (e.g., Apache Tomcat).
- UI is rendered with Thymeleaf templates under src/main/resources/templates.
- Configuration and data are file-based using the QuiptMC Config framework (com.quiptmc.core.config).
- The application depends on an external API (TheMovieDB) to fetch talent profile data and images.

Key components
- com.bwp.Main
  - Spring Boot bootstrap class and WAR initializer (extends SpringBootServletInitializer).
  - Nested Integration extends QuiptIntegration and resolves the application data directory.
  - Logs and ensures the data folder exists on startup.
- com.bwp.server.PageController
  - MVC controller for HTML pages: 
    - "/" → home page
    - "/talent" → renders talent listing using the TalentConfig.
- com.bwp.server.ApiController
  - REST API controller under "/api":
    - GET /api → simple readiness message
    - GET /api/health → returns status and timestamp
  - Includes token validation utilities used by secured endpoints (none published by default in this repo).
- com.bwp.data.config.* (DefaultConfig, TalentConfig, UsersConfig)
  - Config classes backed by JSON files in the data directory.
  - TalentConfig manages Talent objects and downloads headshots from TMDB.
  - UsersConfig persists users, permissions, and tokens.
  - DefaultConfig stores api_token for TMDB.
- com.bwp.data.account.User
  - Represents an admin/user entity with permissions and API tokens, stored via QuiptMC ConfigMap.
- com.bwp.utils.Utils
  - Initializes ConfigManager factories and registers config classes.
  - Bootstraps a default admin user and token when users.json is empty, and creates adminPassword.txt with a generated value.
  - Provides HTTP configuration helper for external calls (Utils.GET()).
- com.bwp.components.ResourceComponent
  - Utility for resolving filesystem paths for external resources (e.g., actors image storage).

Runtime flow (high level)
1) Application starts (WAR or bootRun): Spring creates a context for com.bwp.Main.
2) Main.Integration.resolveDataFolder determines the data directory using, in order: system property, environment variable, catalina.base, user.home, current working directory.
3) Utils.init should be invoked by integration code to register config classes and perform initial bootstrap (users, tokens, etc.).
4) PageController renders Thymeleaf views; TalentConfig reads/writes talents.json, manages per-actor folders in data/actors.
5) ApiController exposes minimal health and readiness; it can leverage UsersConfig for token validation.

Data directory layout (typical)
- data/
  - config.json           (DefaultConfig)
  - talents.json          (TalentConfig)
  - users.json            (UsersConfig)
  - actors/               (per-talent subfolders, headshot.jpg, etc.)
  - adminPassword.txt     (generated once to bootstrap admin)

External integrations
- TheMovieDB API: requires an API token in DefaultConfig.config.json (api_token).
- QuiptMC core libraries: configuration framework, HTTP utils, hashing utilities.

Security considerations
- The first run auto-creates an admin with ALL permissions and a token with ALL permissions. Rotate credentials immediately in any non-demo environment. See docs/Security.md.
