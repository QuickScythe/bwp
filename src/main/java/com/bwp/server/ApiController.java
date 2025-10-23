package com.bwp.server;

import com.bwp.Main;
import com.bwp.data.Actor;
import com.bwp.data.account.User;
import com.bwp.data.config.TalentConfig;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.Utils;
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

    @GetMapping("/talents")
    public List<Actor> listTalents() {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        return new ArrayList<>(config.talents.values());
    }

    @GetMapping("/talents/{id}")
    public Actor getTalent(@PathVariable("id") String id) {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        Actor actor = config.talents.get(id);
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
        }
        return actor;

    }

    @GetMapping()
    public String root() {
        return "BWP Server API is running.";
    }

    @PostMapping("/talents/add")
    public Actor addTalent(@RequestParam("apiId") int apiId, @RequestBody String body) {
        Main.LOGGER.info("Received request to add talent with API ID: {}", apiId);
        if(!body.trim().startsWith("{") || !body.trim().endsWith("}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid talent data format.");

        JSONObject data = new JSONObject(body);
        if(!data.has("token"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required field: token");
        String rawToken = data.getString("token");
        UsersConfig usersConfig = ConfigManager.getConfig(Main.INTEGRATION, UsersConfig.class);
        Optional<User> owner = usersConfig.searchByToken(rawToken);
        if(owner.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found.");
        Token token = owner.get().tokens.get(rawToken);
        if(token.permissions.contains(Permissions.MANAGE_TALENTS) || token.permissions.contains("*")) {
            Main.LOGGER.info("Token validated for user: {}", owner.get().username);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions to add talent.");
        }

        try {
            TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId, t);
        }
    }
}
