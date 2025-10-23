package com.bwp.data.account;

import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Permissions;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User extends ConfigObject {

    private final PermissionManager permissionManager = new PermissionManager();
    private final TokenManager tokenManager = new TokenManager();
    public String username;
    public String password;
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    public ConfigMap<Token> tokens = new ConfigMap<>();

    public String username() {
        return username;
    }

    public PermissionManager permissions(){
        return permissionManager;
    }

    public TokenManager tokens() {
        return tokenManager;
    }


    public class TokenManager {
        public void add(Token token) {
            tokens.put(token);
        }

        public void remove(Token token) {
            tokens.remove(token);
        }

        public boolean validate(Token token){
            Token storedToken = tokens.get(token.id);
            if (storedToken == null) {
                return false;
            }
            if (storedToken.expired()) {
                tokens.remove(storedToken);
                return false;
            }
            return true;
        }

        public Collection<Token> list() {
            return tokens.values();
        }
    }

    public class PermissionManager {

//        Map<String, Permission> cache = new HashMap<>();

        private Permission get(String permission) {
            return Permissions.get(permission).orElseThrow(()->new IllegalStateException("Permission not found: " + permission));
        }

        public boolean has(String permission) {
            Permission perm = get(permission);
            return has(perm);
        }

        public boolean has(Permission permission){
            return permissions.contains(permission);
        }

        public void add(String permission) {
            add(get(permission));
        }

        public void add(Permission permission){
            if (!permissions.contains(permission)) {
                permissions.put(permission);
            }
        }

        public void remove(String permission) {
            remove(get(permission));
        }

        public void remove(Permission permission){
            permissions.remove(permission);
        }

        public Collection<Permission> list() {
            return permissions.values();
        }

        @Override
        public String toString() {
            return json().toString(2); // Pretty print with an indent of 4 spaces
        }
    }
}
