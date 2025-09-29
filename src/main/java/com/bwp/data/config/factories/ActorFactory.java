package com.bwp.data.config.factories;

import com.bwp.data.Actor;
import com.quiptmc.core.config.ConfigObject;

public class ActorFactory implements ConfigObject.Factory<Actor> {


    @Override
    public String getClassName() {
        return Actor.class.getName();
    }
}
