package com.bwp.data.account;

import com.bwp.data.MapManager;
import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;

public class User extends ConfigObject {

    public String username;
    public String password;
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    private final MapManager<Permission> permissionManager = new MapManager<>(permissions);
    public ConfigMap<Token> tokens = new ConfigMap<>();
    private final MapManager<Token> tokenManager = new MapManager<>(tokens);


    public String username() {
        return username;
    }

    public MapManager<Permission> permissions() {
        return permissionManager;
    }

    public MapManager<Token> tokens() {
        return tokenManager;
    }
}
