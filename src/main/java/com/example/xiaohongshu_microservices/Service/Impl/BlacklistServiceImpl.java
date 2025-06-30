package com.example.xiaohongshu_microservices.Service.Impl;

import com.example.xiaohongshu_microservices.Service.BlacklistService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BlacklistServiceImpl implements BlacklistService {
    private final ConcurrentHashMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    @Override
    public void blacklist(String token) {
        if (token != null) {
            blacklist.put(token, true);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}
