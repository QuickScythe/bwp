package com.bwp.utils;

import com.bwp.Main;
import com.bwp.data.Actor;
import com.bwp.data.account.User;
import com.bwp.data.config.DefaultConfig;
import com.bwp.data.config.TalentConfig;
import com.bwp.data.config.UsersConfig;
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
import java.util.UUID;

public class Utils {

    private static Main.Integration integration;

    public static void init(Main.Integration integration) {
        Utils.integration = integration;
        ConfigManager.registerFactory(new GenericFactory<>(Actor.class));
        ConfigManager.registerFactory(new GenericFactory<>(Actor.Credit.class));
        ConfigManager.registerFactory(new GenericFactory<>(User.class));


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
        }
    }

    public static Main.Integration integration() {
        return integration;
    }

    public static HttpConfig GET() {
        String token = ConfigManager.getConfig(Main.INTEGRATION, DefaultConfig.class).api_token;
        return HttpConfig.config(Duration.ofSeconds(10), 10, false, true, "application/json", "UTF-8", HttpHeaders.AUTHORIZATION_BEARER(token));
    }

    public static <T extends Config> T getConfig(Class<T> clazz) {
        return ConfigManager.getConfig(Main.INTEGRATION, clazz);
    }
}
