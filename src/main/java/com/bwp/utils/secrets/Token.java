package com.bwp.utils.secrets;


import com.bwp.Main;
import com.bwp.data.MapManager;
import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;

import static com.bwp.utils.secrets.TokenUtils.*;

public class Token extends ConfigObject {




    public String userId;
    public long created;
    public ConfigMap<Permission> permissions = new ConfigMap<>();
    private final MapManager<Permission> permissionManager = new MapManager<>(permissions);

    public Token() {
        this.id = generateSecret();
        created = System.currentTimeMillis();

    }


    public Token(User user) {
        this.id = generateSecret();
        this.userId = user.id;
        created = System.currentTimeMillis();
    }

    public MapManager<Permission> permissions(){
        return permissionManager;
    }

    private String generateSecret() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }

    public boolean expired() {
        long currentTime = System.currentTimeMillis();
        // Token is considered expired if it was created more than 24 hours ago
        return (currentTime - created) > TokenUtils.TOKEN_EXPIRATION_TIME;
    }

    public User user() {
        UsersConfig config = ConfigManager.getConfig(Main.INTEGRATION, UsersConfig.class);
        return config.get(userId);
    }
}
