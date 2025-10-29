package com.bwp.server;

import com.bwp.Main;
import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Permissions;
import com.bwp.utils.secrets.Token;
import com.quiptmc.core.config.ConfigManager;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "OK");
        status.put("timestamp", OffsetDateTime.now().toString());
        return status;
    }


    @GetMapping()
    public Object root() {
        return "BWP Server API is running.";
    }


    public Token validateToken(String tokenContainer, Permission... permissions) {
        String tokenId = extractTokenID(tokenContainer);
        UsersConfig usersConfig = ConfigManager.getConfig(Main.INTEGRATION, UsersConfig.class);
        Optional<User> owner = usersConfig.searchByToken(tokenId);
        if (owner.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found.");
        Token token = owner.get().tokens.get(tokenId);
        if (token.permissions.containsAll(permissions) || token.permissions.contains(Permissions.ALL)) {
            Main.LOGGER.info("Token validated for user: {}", owner.get().username);
            return token;
        }
        return null;
    }



    private String extractTokenID(String tokenContainer) {
        if (!tokenContainer.trim().startsWith("{") || !tokenContainer.trim().endsWith("}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid talent data format.");

        JSONObject data = new JSONObject(tokenContainer);
        if (!data.has("token"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field: token");
        return data.getString("token");
    }
}
