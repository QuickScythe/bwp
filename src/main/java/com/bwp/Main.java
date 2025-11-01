package com.bwp;


import com.quiptmc.core.QuiptIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

/**
 * Main application class that initializes and configures the Spring Boot application.
 * This class extends SpringBootServletInitializer to support WAR deployment and
 * enables scheduling capabilities through @EnableScheduling.
 */
@SpringBootApplication
@EnableScheduling
public class Main extends SpringBootServletInitializer {

    public static final Integration INTEGRATION = new Integration();

    public static final Logger LOGGER = LoggerFactory.getLogger("BWP");

    /**
     * Bootstraps the Spring Boot application.
     *
     * @param args application arguments
     * @throws Exception if startup fails
     */
    public static void main(String[] args) throws Exception {
        new Main().configure(new SpringApplicationBuilder(Main.class)).run(args);
    }

    /**
     * Configures the application when deployed as a WAR.
     * Keeps the initializer lean for container startup.
     *
     * @param application the SpringApplicationBuilder to configure
     * @return the configured builder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Keep initializer lean for WAR deployment; defer heavy initialization to ApplicationReadyEvent
        return application.sources(Main.class);
    }

    /**
             * Integration with the QuiptMC framework providing application identity and
             * data directory resolution/ownership for configuration files.
             */
            public static class Integration extends QuiptIntegration {

        File dataFolder;

        /**
             * Creates an Integration using the resolved data directory.
             */
        public Integration() {
            this(resolveDataFolder());
        }

        /**
             * Creates an Integration with an explicit data directory path.
             *
             * @param dataFolderPath absolute or relative path to the data directory
             */
        public Integration(String dataFolderPath) {
            dataFolder = new File(dataFolderPath).getAbsoluteFile();
        }

        /**
             * Resolves the application data directory path using the following precedence:
             * 1) Java system property -Dbwp.dataDir
             * 2) Environment variable BWP_DATA_DIR
             * 3) Tomcat's catalina.base/bwp-data
             * 4) ${user.home}/.bwp/data
             * 5) ./data (working directory)
             *
             * @return an absolute path to the chosen data directory
             */
        private static String resolveDataFolder() {
            // 1) System property overrides
            String prop = System.getProperty("bwp.dataDir");
            if (prop != null && !prop.isBlank()) return prop;
            // 2) Environment variable
            String env = System.getenv("BWP_DATA_DIR");
            if (env != null && !env.isBlank()) return env;
            // 3) Tomcat catalina.base
            String catalinaBase = System.getProperty("catalina.base");
            if (catalinaBase != null && !catalinaBase.isBlank()) {
                return new File(catalinaBase, "bwp-data").getAbsolutePath();
            }
            // 4) User home fallback
            String userHome = System.getProperty("user.home");
            if (userHome != null && !userHome.isBlank()) {
                return new File(new File(userHome, ".bwp"), "data").getAbsolutePath();
            }
            // 5) Current working directory fallback
            return new File("data").getAbsolutePath();
        }

        /**
             * Ensures the data directory exists and logs its status during startup.
             */
        @Override
        public void enable() {
            if (!dataFolder.exists()) {
                boolean created = dataFolder.mkdirs();
                if (!created) {
                    LOGGER.error("Failed to create data folder at {}", dataFolder.getAbsolutePath());
                } else {
                    LOGGER.info("Created data folder at {}", dataFolder.getAbsolutePath());
                }
            } else {
                LOGGER.info("Using data folder at {}", dataFolder.getAbsolutePath());
            }
        }

        /**
             * Returns the resolved data directory used by the application.
             *
             * @return absolute File path to the data directory
             */
        @Override
        public File dataFolder() {
            return dataFolder;
        }

        /**
             * Returns the integration name used by the QuiptMC framework.
             */
        @Override
        public String name() {
            return "bwp";
        }

        /**
             * Returns the current version string of the application.
             */
        @Override
        public String version() {
            return "0.0.1";
        }
    }
}