package com.bwp.data.config;

import com.bwp.data.account.User;
import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;
import org.json.JSONObject;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * Configuration class that persists application users, their permissions, and API tokens.
 * <p>
 * Backed by users.json in the resolved data directory. Uses ConfigMap<User> for storage.
 */
@ConfigTemplate(name="users", ext = ConfigTemplate.Extension.JSON)
public class UsersConfig extends Config {

    @ConfigValue
    public ConfigMap<User> users = new ConfigMap<>();


    /**
     * Creates a new config file
     *
     * @param file        The file to save to
     * @param name        The name of the config
     * @param extension   The extension of the config
     * @param integration The plugin that owns this config
     */
    public UsersConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }

    /**
     * Retrieves a user by id.
     *
     * @param id the UUID string of the user
     * @return the User instance or null if not present
     */
    public User get(String id){
        return users.get(id);
    }

    /**
     * Creates and persists a new user with the given username and encrypted password.
     * Generates a unique UUID and saves the configuration.
     *
     * @param username          the username to assign
     * @param encryptedPassword the already-encrypted/hashed password
     * @return the newly created User
     */
    public User create(String username, String encryptedPassword) {
        UUID uuid = UUID.randomUUID();
        while(users.contains(uuid.toString()))
            uuid = UUID.randomUUID();

        JSONObject userData = new JSONObject()
                .put("username", username)
                .put("id", uuid.toString())
                .put("password", encryptedPassword);
        User user = new User();
        user.fromJson(userData);
        users.put(user);
        save();
        return user;
    }

    /**
     * Searches all users for a token with the provided id.
     * If a matching token is found but is expired, it is removed and empty is returned.
     *
     * @param rawToken the presented token id
     * @return Optional user who owns the valid (non-expired) token
     */
    public Optional<User> searchByToken(String rawToken) {
        for (User user : users.values()) {
            for (var token : user.tokens.values()) {
                if (token.id.equals(rawToken)) {
                    if (token.expired()) {
                        user.tokens.remove(token);
                        save();
                        return Optional.empty();
                    }
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}
