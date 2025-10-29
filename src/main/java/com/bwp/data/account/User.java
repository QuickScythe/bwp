package com.bwp.data.account;

import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigMapManager;
import com.quiptmc.core.config.ConfigObject;

public class User extends ConfigObject {

    public String username;
    public String password;
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    private final ConfigMapManager<Permission> permissionManager = new ConfigMapManager<>(permissions);
    public ConfigMap<Token> tokens = new ConfigMap<>();
    private final ConfigMapManager<Token> tokenManager = new ConfigMapManager<>(tokens);


    public String username() {
        return username;
    }

    public ConfigMapManager<Permission> permissions() {
        return permissionManager;
    }

    public ConfigMapManager<Token> tokens() {
        return tokenManager;
    }
}
