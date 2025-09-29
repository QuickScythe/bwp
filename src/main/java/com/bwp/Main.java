package com.bwp;


import com.bwp.utils.Utils;
import com.bwp.utils.queue.QueueManager;
import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.utils.TaskScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.util.concurrent.TimeUnit;

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

    public static void main(String[] args) throws Exception {
        new Main().configure(new SpringApplicationBuilder(Main.class)).run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Keep initializer lean for WAR deployment; defer heavy initialization to ApplicationReadyEvent
        return application.sources(Main.class);
    }

    public static class Integration extends QuiptIntegration {

        File dataFolder;

        public Integration() {
            dataFolder = new File("data");
        }

        public Integration(String dataFolderPath) {
            dataFolder = new File(dataFolderPath);
        }

        @Override
        public void enable() {
            if (!dataFolder.exists()) {
                boolean created = dataFolder.mkdirs();
                if (!created) {
                    LOGGER.error("Failed to create data folder");
                }
            }
        }

        @Override
        public File dataFolder() {
            return dataFolder;
        }

        @Override
        public String name() {
            return "bwp";
        }

        @Override
        public String version() {
            return "0.0.1";
        }
    }
}