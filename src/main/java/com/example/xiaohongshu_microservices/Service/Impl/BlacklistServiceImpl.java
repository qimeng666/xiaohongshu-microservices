package com.example.xiaohongshu_microservices.Service.Impl;

import com.example.xiaohongshu_microservices.Service.BlacklistService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlacklistServiceImpl implements BlacklistService {
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();
    @Override
    public void blacklist(String token) {
        blacklist.add(token);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
