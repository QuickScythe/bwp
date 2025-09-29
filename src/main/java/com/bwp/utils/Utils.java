package com.bwp.utils;

import com.bwp.Main;
import com.bwp.data.Actor;
import com.bwp.data.config.Configs;
import com.bwp.data.config.DefaultConfig;
import com.bwp.data.config.TalentConfig;
import com.bwp.data.config.factories.ActorFactory;
import com.quiptmc.core.config.ConfigManager;
import com.quiptmc.core.utils.net.HttpConfig;
import com.quiptmc.core.utils.net.HttpHeaders;

import java.time.Duration;

public class Utils {

    private static Main.Integration integration;

    public static void init(Main.Integration integration) {
        Utils.integration = integration;
        ConfigManager.registerFactory(new ActorFactory());
        ConfigManager.registerFactory(new Actor.Credit.Factory());
        TalentConfig talentConfig = ConfigManager.registerConfig(integration, TalentConfig.class);
        DefaultConfig config = ConfigManager.registerConfig(integration, DefaultConfig.class);
    }

    public static Main.Integration integration() {
        return integration;
    }

    public static HttpConfig GET() {
        String token = ConfigManager.getConfig(Main.INTEGRATION, DefaultConfig.class).api_token;
        return HttpConfig.config(
                Duration.ofSeconds(10),
                10,
                false,
                true,
                "application/json",
                "UTF-8",
                HttpHeaders.AUTHORIZATION_BEARER(token)
        );
    }
}
