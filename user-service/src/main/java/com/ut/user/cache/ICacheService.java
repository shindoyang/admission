package com.ut.user.cache;

public interface ICacheService {
    String setex(String key, String value);

    String setex(String key, int seconds, String value);

    String get(String key);

}
