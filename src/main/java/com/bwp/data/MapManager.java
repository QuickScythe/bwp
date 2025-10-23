package com.bwp.data;

import com.quiptmc.core.config.ConfigMap;
import com.quiptmc.core.config.ConfigObject;

public record MapManager<T extends ConfigObject>(ConfigMap<T> map) {

    public T get(String id) {
        return map.get(id);
    }

    public boolean has(String id) {
        return map.contains(id);
    }

    public boolean has(T obj) {
        return map.contains(obj);
    }

    public void add(T obj) {
        if (!map.contains(obj)) {
            map.put(obj);
        }
    }
}
