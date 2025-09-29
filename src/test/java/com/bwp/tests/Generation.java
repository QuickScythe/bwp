package com.bwp.tests;

import com.bwp.Main;
import com.bwp.data.config.Configs;
import com.bwp.data.config.TalentConfig;
import com.bwp.utils.Utils;
import org.junit.jupiter.api.Test;

public class Generation {

    @Test
    public void generate() {
        Main.Integration integration = new Main.Integration();
        Utils.init(integration);
        TalentConfig talentConfig = Configs.talent();
        talentConfig.actor(20819);
        talentConfig.actor(154182);
        talentConfig.actor(1214974);
        talentConfig.actor(15516);
        talentConfig.actor(224526);
        talentConfig.actor(1230724);
        talentConfig.actor(29479);
        talentConfig.actor(100085);
        talentConfig.actor(223050);
        talentConfig.actor(4164165);
        talentConfig.save();
    }
}
