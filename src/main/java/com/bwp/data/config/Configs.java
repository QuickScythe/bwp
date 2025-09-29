package com.bwp.data.config;

import com.bwp.Main;
import com.quiptmc.core.config.Config;
import com.quiptmc.core.config.ConfigManager;

public class Configs {

    public static <T extends Config> T getConfig(Class<T> clazz){
        return ConfigManager.getConfig(Main.INTEGRATION, clazz);
    }

    public static TalentConfig talent(){
        return ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);
    }
}
