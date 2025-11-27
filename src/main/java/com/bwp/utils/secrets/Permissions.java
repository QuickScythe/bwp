package com.bwp.utils.secrets;

import com.quiptmc.core.data.registries.Registries;
import com.quiptmc.core.data.registries.Registry;

import java.util.Optional;

/**
 * Static registry of well-known Permission instances and lookup helpers.
 * Use Permissions.of("key") to resolve a permission by id at runtime.
 */
public class Permissions {

    private static final Registry<Permission> PERMISSIONS = Registries.register("permissions", ()->null);

    /** Wildcard permission that implies all others. */
    public static final Permission ALL = register("*", "Grants all permissions" );
    /** Allows reading of secrets. */
    public static final Permission READ_SECRETS = register("secrets.read", "Allows reading of secrets");
    /** Allows writing of secrets. */
    public static final Permission WRITE_SECRETS = register("secrets.write", "Allows writing of secrets");
    /** Allows deletion of secrets. */
    public static final Permission DELETE_SECRETS = register("secrets.delete", "Allows deletion of secrets");
    /** Allows management of tokens. */
    public static final Permission MANAGE_TOKENS = register("tokens.manage", "Allows management of tokens");
    /** Allows management of users. */
    public static final Permission MANAGE_USERS = register("users.manage", "Allows management of users");
    /** Allows viewing of audit logs. */
    public static final Permission VIEW_AUDIT_LOGS = register("auditlogs.view", "Allows viewing of audit logs");
    /** Allows management of permissions. */
    public static final Permission MANAGE_PERMISSIONS = register("permissions.manage", "Allows management of permissions");
    /** Allows management of talents. */
    public static final Permission MANAGE_TALENTS = register("talents.manage", "Allows management of talents");
    /** Represents no permission granted. */
    public static final Permission NONE = register ("none", "No permissions granted");

    /** Registers a Permission in the static registry. */
    private static Permission register(String id, String description) {
        Permission permission = new Permission(id, description);
        PERMISSIONS.register(id, permission);
        return permission;
    }

    /**
     * Looks up a Permission by its id.
     *
     * @param permission the string key (e.g., "secrets.read")
     * @return Optional with the Permission if registered
     */
    public static Optional<Permission> of(String permission) {
        return PERMISSIONS.get(permission);
    }
}
