package com.bwp.data.config;

import com.bwp.Main;
import com.bwp.components.ResourceComponent;
import com.bwp.data.Talent;
import com.bwp.utils.Utils;
import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.*;
import com.quiptmc.core.utils.net.NetworkUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.http.HttpResponse;

@ConfigTemplate(name = "talents", ext = ConfigTemplate.Extension.JSON)
public class TalentConfig extends Config {

    @ConfigValue
    public ConfigMap<Talent> talents = new ConfigMap<>();


    /**
     * Creates a new config file
     *
     * @param file        The file to save to
     * @param name        The name of the config
     * @param extension   The extension of the config
     * @param integration The plugin that owns this config
     */
    public TalentConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }

    public Talent actor(int apiId) {
        Talent talent = new Talent(apiId);
        talent.sync();
        actor(talent);
        return talent;
    }

    public void actor(Talent talent) {
        ResourceComponent.ResourceHandlerRecord record = ResourceComponent.ExternalFile.ACTORS.getHandlerRecord();
        File file = record.file();
        File actorFolder = new File(file, talent.id());
        if (!actorFolder.exists()) {
            boolean success = actorFolder.mkdir();
            if (!success) {
                Main.LOGGER.error("Can't create actor folder for {}.", talent.fullName());
                return;
            }
            Main.LOGGER.info("Successfully created actor folder for {}.", talent.fullName());
        }
        String apiUrl = "https://api.themoviedb.org/3/person/" + talent.id;
        HttpResponse<String> apiResponse = NetworkUtils.get(Utils.GET(), apiUrl, HttpResponse.BodyHandlers.ofString());
        JSONObject data = new JSONObject(apiResponse.body());
        String imagePath = data.optString("profile_path", null);
        String imageUrl = "https://image.tmdb.org/t/p/original" + imagePath;
        HttpResponse<InputStream> imageResponse = NetworkUtils.get(Utils.GET(), imageUrl, HttpResponse.BodyHandlers.ofInputStream());
        NetworkUtils.save(imageResponse, new File(actorFolder, "headshot.jpg"));
        talents.put(talent);
        save();
    }
}
