package com.bwp.server;

import com.bwp.Main;
import com.bwp.data.Actor;
import com.bwp.data.config.TalentConfig;
import com.bwp.utils.Utils;
import com.quiptmc.core.config.ConfigManager;
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
    public Actor addTalent(@RequestParam("apiId") int apiId) {
        System.out.println("Received request to add talent with API ID: " + apiId);
        try {
            TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId, t);
        }
    }
}
