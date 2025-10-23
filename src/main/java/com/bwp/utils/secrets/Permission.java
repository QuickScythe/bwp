package com.bwp.utils.secrets;

import com.quiptmc.core.config.ConfigObject;

public class Permission extends ConfigObject {

    public String description;


    public Permission(){

    }

    public Permission(String id, String description) {
        this.id = id;
        this.description = description;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Permission other) return id.equals(other.id);
        if (obj instanceof String key) return id.equals(key);
        return false;
    }
}
