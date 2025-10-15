package com.bwp.data.config;

import com.bwp.data.account.User;
import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

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

    public User get(String id){
        return users.get(id);
    }

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
}
