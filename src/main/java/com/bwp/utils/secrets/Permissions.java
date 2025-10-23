package com.bwp.utils.secrets;

import com.quiptmc.core.data.registries.Registries;
import com.quiptmc.core.data.registries.Registry;

import java.util.Optional;

public class Permissions {

    private static final Registry<Permission> PERMISSIONS = Registries.register("permissions", ()->null);

    public static final Permission ALL = register("*", "Grants all permissions" );
    public static final Permission READ_SECRETS = register("secrets.read", "Allows reading of secrets");
    public static final Permission WRITE_SECRETS = register("secrets.write", "Allows writing of secrets");
    public static final Permission DELETE_SECRETS = register("secrets.delete", "Allows deletion of secrets");
    public static final Permission MANAGE_TOKENS = register("tokens.manage", "Allows management of tokens");
    public static final Permission MANAGE_USERS = register("users.manage", "Allows management of users");
    public static final Permission VIEW_AUDIT_LOGS = register("auditlogs.view", "Allows viewing of audit logs");
    public static final Permission MANAGE_PERMISSIONS = register("permissions.manage", "Allows management of permissions");
    public static final Permission MANAGE_TALENTS = register("talents.manage", "Allows management of talents");



    private static Permission register(String id, String description) {
        Permission permission = new Permission(id, description);
        PERMISSIONS.register(id, permission);
        return permission;
    }

    public static Optional<Permission> get(String permission) {
        return PERMISSIONS.get(permission);
    }
}
