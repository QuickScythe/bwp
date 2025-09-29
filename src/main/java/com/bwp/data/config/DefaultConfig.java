package com.bwp.data.config;

import com.quiptmc.core.QuiptIntegration;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigTemplate;
import com.quiptmc.core.config.ConfigValue;

import java.io.File;

@ConfigTemplate(name="config", ext = ConfigTemplate.Extension.JSON)
public class DefaultConfig extends Config {

    @ConfigValue
    public String api_token = "change this";


    /**
     * Creates a new config file
     *
     * @param file        The file to save to
     * @param name        The name of the config
     * @param extension   The extension of the config
     * @param integration The plugin that owns this config
     */
    public DefaultConfig(File file, String name, ConfigTemplate.Extension extension, QuiptIntegration integration) {
        super(file, name, extension, integration);
    }
}
