package com.bwp.utils.secrets;

import com.quiptmc.core.config.ConfigObject;

/**
 * Represents a named capability that can be granted to a user or token.
 * Stored as a ConfigObject and typically referenced by its id (string key).
 */
public class Permission extends ConfigObject {

    /** Optional human-readable description of the permission. */
    public String description;

    /** Default constructor for config frameworks and serialization. */
    public Permission(){

    }

    /**
     * Constructs a Permission with an explicit id and description.
     *
     * @param id          unique string identifier (e.g., "secrets.read")
     * @param description human-readable description of the capability
     */
    public Permission(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Compares by id allowing comparison with either another Permission or a String key.
     *
     * @param obj another Permission or String id
     * @return true if ids match
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Permission other) return id.equals(other.id);
        if (obj instanceof String key) return id.equals(key);
        return false;
    }
}
