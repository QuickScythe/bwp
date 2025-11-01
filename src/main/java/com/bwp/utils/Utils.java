package com.bwp.utils;

import com.bwp.Main;
import com.bwp.data.Talent;
import com.bwp.data.account.User;
import com.bwp.data.config.DefaultConfig;
import com.bwp.data.config.TalentConfig;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Permissions;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.config.factories.GenericFactory;
import com.quiptmc.core.utils.HashUtils;
import com.quiptmc.core.utils.net.HttpConfig;
import com.quiptmc.core.utils.net.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Random;

/**
 * Utility helpers for application-wide configuration and HTTP access.
 * <p>
 * Responsibilities:
 * - Hold a reference to the Main.Integration instance.
 * - Register QuiptMC config factories and configuration classes.
 * - Bootstrap a default admin user and token when users configuration is empty.
 * - Provide a preconfigured HttpConfig for external API calls (e.g., TMDB).
 */
public class Utils {

    private static Main.Integration integration;

    /**
         * Initializes utility state and config system for the application.
         * <p>
         * Registers Config factories and configuration classes, and bootstraps a default
         * admin user and token when the users configuration is empty.
         *
         * @param integration the running Main.Integration instance
         */
        public static void init(Main.Integration integration) {
        Utils.integration = integration;
        ConfigManager.registerFactory(new GenericFactory<>(Talent.class));
        ConfigManager.registerFactory(new GenericFactory<>(Talent.Credit.class));
        ConfigManager.registerFactory(new GenericFactory<>(User.class));
        ConfigManager.registerFactory(new GenericFactory<>(Permission.class));
        ConfigManager.registerFactory(new GenericFactory<>(Token.class));


        ConfigManager.registerConfig(integration, TalentConfig.class);
        ConfigManager.registerConfig(integration, DefaultConfig.class);
        UsersConfig usersConfig = ConfigManager.registerConfig(integration, UsersConfig.class);
        if (usersConfig.users.isEmpty()) {
            File adminFile = new File(integration.dataFolder(), "adminPassword.txt");
            String password; // This should be replaced with a proper encryption method
            String encryptedPassword;
            try {
                if (!adminFile.exists()) {
                    Utils.integration().logger().log("Creating adminPassword.txt file to store the admin password. ({})", adminFile.createNewFile() ? adminFile.getAbsolutePath() : "Failed");
                    password = HashUtils.sha256(System.currentTimeMillis() + new Random().nextInt(100000) + ""); // Generate a random password
                    encryptedPassword = HashUtils.sha256(password);
                    Files.write(adminFile.toPath(), password.getBytes()); // Save the encrypted password to the file

                } else {
                    password = HashUtils.sha256(Files.readString(adminFile.toPath()).trim()); // Read the encrypted password from the file
                    encryptedPassword = HashUtils.sha256(password);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            User admin = usersConfig.create("admin", encryptedPassword);
            admin.permissions().add(Permissions.ALL);
            Token token = new Token(admin);
            token.permissions().add(Permissions.ALL);

            usersConfig.save();
        }
    }

    /**
     * Returns the active integration instance associated with the application.
     *
     * @return the Main.Integration instance set during initialization
     */
    public static Main.Integration integration() {
        return integration;
    }

    /**
     * Builds a reusable HTTP configuration for external API calls.
     * <p>
     * Uses the Bearer token from DefaultConfig.api_token and sets reasonable
     * timeouts and headers for JSON communication.
     *
     * @return a configured HttpConfig instance
     */
    public static HttpConfig GET() {
        String token = ConfigManager.getConfig(Main.INTEGRATION, DefaultConfig.class).api_token;
        return HttpConfig.config(Duration.ofSeconds(10), 10, false, true, "application/json", "UTF-8", HttpHeaders.AUTHORIZATION_BEARER(token));
    }

    /**
     * Convenience wrapper around ConfigManager.getConfig bound to the application's integration.
     *
     * @param clazz the config class to retrieve
     * @param <T>   the type of Config
     * @return the loaded configuration instance for the given class
     */
    public static <T extends Config> T getConfig(Class<T> clazz) {
        return ConfigManager.getConfig(Main.INTEGRATION, clazz);
    }
}
