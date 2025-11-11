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

/**
 * Talent REST API providing read and administrative operations for talents.
 *
 * Base path: /api/talents
 *
 * Endpoints:
 * - GET /api/talents           → List all talents (overrides ApiController.root())
 * - GET /api/talents/{id}      → Get a single talent by id
 * - POST /api/talents/add      → Add/sync a talent by TMDB person id (requires MANAGE_TALENTS via token)
 */
@RestController
@RequestMapping("/api/talents")
public class TalentApiController extends ApiController {


//25451
    /**
     * Lists all talents in the system.
     * Overrides ApiController.root() to return a JSON array instead of a readiness string.
     *
     * @return list of Talent objects
     */
    @Override
    public List<Talent> root() {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        return new ArrayList<>(config.talents.values());
    }

    /**
     * Retrieves a single talent by id.
     *
     * @param id the talent id (TMDB person id) as stored in talents.json
     * @return the Talent if found
     * @throws ResponseStatusException 404 if the talent does not exist
     */
    @GetMapping("/{id}")
    public Talent get(@PathVariable("id") String id) {
        TalentConfig config = Utils.getConfig(TalentConfig.class);
        Talent talent = config.talents.get(id);
        if (talent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Talent not found: " + id);
        }
        return talent;

    }

    /**
     * Adds (synchronizes) a talent by TMDB person id and persists it.
     * Requires a token with MANAGE_TALENTS permission.
     *
     * @param apiId TMDB person id to add
     * @param tokenContainer JSON body containing {"token":"<id>", "permissions":[...]}
     * @return the created Talent
     * @throws ResponseStatusException 403 if token is invalid or lacks permission; 500 if sync fails
     */
    @PostMapping("/add")
    public Talent add(@RequestParam("apiId") int apiId, @RequestBody String tokenContainer) {
        Main.LOGGER.info("Received request to add talent with API ID: {}", apiId);
        if(validateToken(tokenContainer, Permissions.MANAGE_TALENTS) == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There was an error validating your token.");
        try {
            TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
            return config.actor(apiId);
        } catch (Throwable t) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add talent: " + apiId + ". " + t.getMessage(), t);
        }
    }
}
