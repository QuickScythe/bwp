package com.bwp.utils.secrets;


import com.bwp.Main;
import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigMapManager;
import com.quiptmc.core.config.ConfigObject;

import static com.bwp.utils.secrets.TokenUtils.*;

/**
 * Represents an API token bound to a User. Tokens carry their own permission set
 * and an issue timestamp used for expiry checks.
 */
public class Token extends ConfigObject {

    /** Id of the owning user (UUID string). */
    public String userId;
    /** Milliseconds since epoch when the token was created. */
    public long created;
    /** Permission map specific to this token. */
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    private final ConfigMapManager<Permission> permissionManager = new ConfigMapManager<>(permissions);

    /**
     * Constructs a new token with a random id and current timestamp.
     * Intended for deserialization or manual association later.
     */
    public Token() {
        this.id = generateSecret();
        created = System.currentTimeMillis();

    }

    /**
     * Constructs and immediately associates a new token to the given user.
     *
     * @param user the owning user; token id is generated and added to user.tokens
     */
    public Token(User user) {
        this.id = generateSecret();
        this.userId = user.id;
        created = System.currentTimeMillis();
        user.tokens.put(this);
    }

    /**
     * Returns a manager for manipulating this token's permission map.
     */
    public ConfigMapManager<Permission> permissions(){
        return permissionManager;
    }

    /**
     * Generates a random token string using the configured alphabet and length.
     */
    private String generateSecret() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    /**
     * Determines whether this token has expired based on its creation time.
     *
     * @return true if older than TokenUtils.TOKEN_EXPIRATION_TIME
     */
    public boolean expired() {
        long currentTime = System.currentTimeMillis();
        // Token is considered expired if it was created more than 24 hours ago
        return (currentTime - created) > TokenUtils.TOKEN_EXPIRATION_TIME;
    }

    /**
     * Resolves and returns the User who owns this token.
     *
     * @return the User from UsersConfig, or null if not found
     */
    public User user() {
        UsersConfig config = ConfigManager.getConfig(Main.INTEGRATION, UsersConfig.class);
        return config.get(userId);
    }
}
