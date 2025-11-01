package com.bwp.data.account;

import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigMapManager;
import com.quiptmc.core.config.ConfigObject;

/**
 * Represents an application user persisted via QuiptMC Config.
 * Contains username, hashed password, permissions, and issued API tokens.
 */
public class User extends ConfigObject {

    public String username;
    public String password;
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    private final ConfigMapManager<Permission> permissionManager = new ConfigMapManager<>(permissions);
    public ConfigMap<Token> tokens = new ConfigMap<>();
    private final ConfigMapManager<Token> tokenManager = new ConfigMapManager<>(tokens);


    /**
     * Returns the username for this user.
     *
     * @return username
     */
    public String username() {
        return username;
    }

    /**
     * Accessor for managing the user's permissions map.
     *
     * @return a ConfigMapManager bound to the permissions map
     */
    public ConfigMapManager<Permission> permissions() {
        return permissionManager;
    }

    /**
     * Accessor for managing the user's token map.
     *
     * @return a ConfigMapManager bound to the tokens map
     */
    public ConfigMapManager<Token> tokens() {
        return tokenManager;
    }
}
