package com.arpit.stashly;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Store {

    private final Map<String, String> data = new ConcurrentHashMap<>();

    public void set(String key, String value) {
        data.put(key, value);
    }

    //Returns Null is the key does not exist
    public String get(String key) {
        return data.get(key);
    }

    //Return true if something was deleted
    public boolean delete(String key) {
        return data.remove(key) != null;
    }

    public boolean exists(String key) {
        return data.containsKey(key);
    }
}
