package com.bwp.server.api;

import com.bwp.Main;
import com.bwp.data.Talent;
import com.bwp.data.config.TalentConfig;
import com.bwp.server.ApiController;
import com.bwp.utils.Utils;
import com.bwp.utils.secrets.Permissions;
import com.quiptmc.core.config.ConfigManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/talents")
public class TalentApiController extends ApiController {


//25451
    @Override
    public List<Talent> root() {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        return new ArrayList<>(config.talents.values());
    }

    @GetMapping("/{id}")
    public Talent get(@PathVariable("id") String id) {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        Talent talent = config.talents.get(id);
        if (talent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
        }
        return talent;

    }

    @PostMapping("/add")
    public Talent add(@RequestParam("apiId") int apiId, @RequestBody String tokenContainer) {
        Main.LOGGER.info("Received request to add talent with API ID: {}", apiId);
        if(validateToken(tokenContainer, Permissions.MANAGE_TALENTS) == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There was an error validating your token.");
        try {
            TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId, t);
        }
    }
}
