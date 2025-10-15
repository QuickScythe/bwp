package com.bwp.data.account;

import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User extends ConfigObject {

    private final PermissionManager permissionManager = new PermissionManager();
    public String username;
    public String password;
    public ConfigMap<Permission> permissions;

    public String username() {
        return username;
    }

    public static class Permission extends ConfigObject {

        public String description;


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Permission other)
                return id.equals(other.id);
            if (obj instanceof String key)
                return id.equals(key);
            return false;
        }
    }

    public class PermissionManager {

        Map<String, Permission> cache = new HashMap<>();

        private Permission get(String permissionName) {
            if (cache.containsKey(permissionName)) return cache.get(permissionName);
            Permission permission = new Permission();
            permission.id = permissionName;
            permission.description = "No description provided";
            cache.put(permissionName, permission);
            return permission;
        }

        public boolean has(String permission) {
            Permission perm = get(permission);
            return permissions.contains(perm.id);
        }

        public void add(String permission) {
            Permission perm = get(permission);
            if (!permissions.contains(perm.id)) {
                permissions.put(perm);
            }
        }

        public void remove(String permission) {
            Permission perm = get(permission);
            permissions.remove(perm);
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
