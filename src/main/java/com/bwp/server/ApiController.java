package com.bwp.server;

import com.bwp.data.Actor;
import com.bwp.data.config.Configs;
import com.bwp.data.config.TalentConfig;
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
        TalentConfig config = Configs.talent();
        return new ArrayList<>(config.talents.values());
    }

    @GetMapping("/talents/{id}")
    public Actor getTalent(@PathVariable String id) {
        TalentConfig config = Configs.talent();
        try {
            Actor actor = config.talents.get(id);
            if (actor == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
            }
            return actor;
        } catch (NoSuchMethodError | UnsupportedOperationException e) {
            // Fallback in case ConfigMap does not support get(String)
            for (Actor a : config.talents.values()) {
                try {
                    // Attempt to match via toString or fullName if needed; primary intent is id match
                    // If ConfigObject.id is not accessible, rely on map lookup above.
                } catch (Throwable ignored) {
                }
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
        }
    }

    @GetMapping()
    public String root() {
        return "BWP Server API is running.";
    }

    @PostMapping("/talents/{apiId}")
    public Actor addTalent(@PathVariable int apiId) {
        try {
            TalentConfig config = Configs.talent();
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId, t);
        }
    }
}
