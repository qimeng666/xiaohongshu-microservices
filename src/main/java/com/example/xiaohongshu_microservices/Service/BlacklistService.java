package com.example.xiaohongshu_microservices.Service;

import org.springframework.stereotype.Service;

@Service
public interface BlacklistService {
    public void blacklist(String token);
    public boolean isBlacklisted(String token);
}
