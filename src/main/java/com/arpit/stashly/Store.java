package com.arpit.stashly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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

    // Adds delts to the number Stored at key. A missing key counts as 0.
    // Throws NumberFormateException if the value is not a number,
    //and ArithmeticException if result is too big.
    public long incrBy(String key, long delta) {
        long[] result = new long[1];
        data.compute(key, (k, old) -> {
            long current = (old==null) ? 0 : Long.parseLong(old);
            result[0] = Math.addExact(current, delta);
            return String.valueOf(result[0]);
        });
        return result[0];
    }

    public int size() {
        return data.size();
    }

    public void clear() {
        data.clear();
    }

    // * means "anything", ? means "any one letter"
    public List<String> keys(String pattern) {
        Pattern regex = Pattern.compile(globToRegex(pattern));
        List<String> result = new ArrayList<>();
        for(String key : data.keySet()) {
            if(regex.matcher(key).matches()) {
                result.add(key);
            }
        }
        Collections.sort(result); // same order everytime
        return result;
    }

    private static String globToRegex(String glob) {
        StringBuilder sb = new StringBuilder();
        for(char c : glob.toCharArray()) {
            if(c == '*') {
                sb.append(".*");
            } else if(c == '?') {
                sb.append(".");
            } else {
                sb.append(Pattern.quote(String.valueOf(c)));
            }
        }
        return sb.toString();
    }
}
