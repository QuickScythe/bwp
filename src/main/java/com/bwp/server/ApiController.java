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

/**
 * REST API endpoints for health checks and utility operations.
 * <p>
 * Base path: /api
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * Health check endpoint.
     *
     * @return a map containing service status and current timestamp
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "OK");
        status.put("timestamp", OffsetDateTime.now().toString());
        return status;
    }


    /**
     * Readiness endpoint at /api.
     *
     * @return a short message indicating the API is running
     */
    @GetMapping()
    public Object root() {
        return "BWP Server API is running.";
    }


    /**
         * Validates a presented token against stored users and required permissions.
         *
         * @param tokenContainer JSON string containing a field "token" with the token id
         * @param permissions    the required permissions to check
         * @return the matching Token if valid and authorized
         * @throws ResponseStatusException 401 if token not found; may throw 400 for invalid input
         */
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



    /**
         * Extracts the token id from a JSON container string.
         * The expected shape is: {"token":"<id>"}
         *
         * @param tokenContainer the raw JSON string containing the token field
         * @return the token id string
         * @throws ResponseStatusException 400 if format is invalid or token field is missing
         */
        private String extractTokenID(String tokenContainer) {
        if (!tokenContainer.trim().startsWith("{") || !tokenContainer.trim().endsWith("}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid talent data format.");

        JSONObject data = new JSONObject(tokenContainer);
        if (!data.has("token"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field: token");
        return data.getString("token");
    }
}
