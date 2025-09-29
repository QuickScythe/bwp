package com.bwp.registries;

import com.bwp.utils.JsonSerializable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registries {

    private final static Map<RegistryKey, Registry<?>> registries = new HashMap<>();
    private final static Map<String, RegistryKey> keys = new HashMap<>();

    public static <T> Registry<T> register(String key, Supplier<T> defaultSupplier) {
        if (keys.containsKey(key)) throw new IllegalArgumentException("Key already registered: " + key);
        RegistryKey registryKey = new RegistryKey(key);
        keys.put(key, registryKey);

        T defaultValue = defaultSupplier.get();
        Registry<T> registry = new Registry<>(registryKey);
        if (!(defaultValue == null)) registry.register("0", defaultSupplier.get());
        registries.put(registryKey, registry);
        return registry;
    }


    public static RegistryKey key(String key) {

        if (!keys.containsKey(key)) throw new IllegalArgumentException("Key not registered: " + key);
        return keys.get(key);
    }

    @Deprecated
    public static Registry<?> get(String key) {
        return registries.get(key(key));
    }

    @Deprecated
    public static <T> Registry<T> get(String key, Class<T> type) {
        return get(key(key), type);
    }


    public static <T> Registry<T> get(RegistryKey key, Class<T> type) throws ClassCastException {
        return (Registry<T>) registries.get(key);
    }


    public static Registry<?> get(RegistryKey key) {
        return registries.get(key);
    }

    public static void reset() {
        for (Registry<?> registry : registries.values()) {
            registry.clear();
        }
        keys.clear();
    }

    public static JSONObject dump() {
        JSONObject root = new JSONObject();
        for (Map.Entry<String, RegistryKey> keyEntry : keys.entrySet()) {
            String skey = keyEntry.getKey();
            RegistryKey key = keyEntry.getValue();
            Registry<?> registry = registries.get(key);
            JSONObject json = new JSONObject();
            for (Map.Entry<String, ?> entry : registry.toMap().entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof JsonSerializable jsonSerializable) {
                    json.put(name, jsonSerializable.json());
                } else {
                    json.put(name, value);
                }
            }
//            registry.forEach(json::put);
            root.put(skey, json);
        }
        return root;
    }

}