package com.example.xiaohongshu_microservices.Service.Impl;

import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Repository.UserRepository;
import com.example.xiaohongshu_microservices.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private  static final String USER_CACHE_KEY_PREFIX = "user:";
    private static final long USER_CACHE_EXPIRATION = 60; // 1 hour in seconds
    
    public UserServiceImpl(UserRepository userRepository, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getById(Long id) {
        String cacheKey = USER_CACHE_KEY_PREFIX + id;
        String userJson = null;
        try {
            userJson = redisTemplate.opsForValue().get(cacheKey);
            // 从缓存获取
            if(userJson != null) {
                System.out.println("【cache hit】userId=" + id);
                // 清理可能的控制字符
                userJson = userJson.replaceAll("[\\x00-\\x1F\\x7F]", "");
                User user = objectMapper.readValue(userJson, User.class);
                return Optional.of(user);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize cached user for id={}, clearing cache: {}", id, e.getMessage());
            log.debug("Problematic JSON: {}", userJson);
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.warn("Failed to get cached user for id={}, clearing cache: {}", id, e.getMessage());
            redisTemplate.delete(cacheKey);
        }
        
        System.out.println("【cache miss】userId={}, query DB" + id);
        // 从数据库获取
        Optional<User> userOpt = userRepository.findById(id);
        userOpt.ifPresent(user -> {
            try {
                String jsonString = objectMapper.writeValueAsString(user);
                redisTemplate.opsForValue().set(cacheKey, jsonString, 60);
                log.debug("Cached user JSON for id={}: {}", id, jsonString);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize user for caching, id={}: {}", id, e.getMessage());
            }
        });
        return userOpt;
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public Optional<User> findByName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Long findIdByName(String username) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .map(Long::valueOf)
                .orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        String cacheKey = "userdetails:" + username;
        
        try {
            String userDetailsJson = redisTemplate.opsForValue().get(cacheKey);
            if (userDetailsJson != null) {
                // 清理可能的控制字符和空字节
                userDetailsJson = userDetailsJson.replaceAll("[\\x00-\\x1F\\x7F]", "");
                log.debug("【cache hit】UserDetails for username={}", username);
                // 从缓存获取简化的用户信息并重建UserDetails
                var simpleUserInfo = objectMapper.readValue(userDetailsJson, java.util.Map.class);
                return org.springframework.security.core.userdetails.User
                        .withUsername((String) simpleUserInfo.get("username"))
                        .password((String) simpleUserInfo.get("password"))
                        .authorities(Collections.emptyList())
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to get cached UserDetails for username={}, clearing cache: {}", username, e.getMessage());
            redisTemplate.delete(cacheKey);
        }
        
        log.debug("【cache miss】UserDetails for username={}, query DB", username);
        // 从数据库获取
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
        
        // 缓存UserDetails - 使用简化的JSON格式
        try {
            // 创建一个简化的用户信息对象，避免Spring Security UserDetails的序列化问题
            var simpleUserInfo = new java.util.HashMap<String, Object>();
            simpleUserInfo.put("username", user.getUsername());
            simpleUserInfo.put("password", user.getPassword());
            simpleUserInfo.put("authorities", Collections.emptyList());
            simpleUserInfo.put("enabled", true);
            simpleUserInfo.put("accountNonExpired", true);
            simpleUserInfo.put("accountNonLocked", true);
            simpleUserInfo.put("credentialsNonExpired", true);
            
            String userDetailsJson = objectMapper.writeValueAsString(simpleUserInfo);
            redisTemplate.opsForValue().set(cacheKey, userDetailsJson, 300); // 5分钟缓存
            log.debug("Cached UserDetails for username={}", username);
        } catch (Exception e) {
            log.warn("Failed to cache UserDetails for username={}: {}", username, e.getMessage());
        }
        
        return userDetails;
    }

    @Override
    public String login(String username, String password) {
        return "";
    }
    
    /**
     * 清除所有用户缓存
     */
    public void clearAllUserCache() {
        try {
            // 清除用户信息缓存
            Set<String> userKeys = redisTemplate.keys(USER_CACHE_KEY_PREFIX + "*");
            if (userKeys != null && !userKeys.isEmpty()) {
                redisTemplate.delete(userKeys);
                log.info("Cleared {} user cache entries", userKeys.size());
            }
            
            // 清除UserDetails缓存
            Set<String> userDetailsKeys = redisTemplate.keys("userdetails:*");
            if (userDetailsKeys != null && !userDetailsKeys.isEmpty()) {
                redisTemplate.delete(userDetailsKeys);
                log.info("Cleared {} UserDetails cache entries", userDetailsKeys.size());
            }
        } catch (Exception e) {
            log.error("Failed to clear user cache: {}", e.getMessage());
        }
    }
    
    /**
     * 清除指定用户的UserDetails缓存
     */
    public void clearUserDetailsCache(String username) {
        try {
            String cacheKey = "userdetails:" + username;
            redisTemplate.delete(cacheKey);
            log.info("Cleared UserDetails cache for username={}", username);
        } catch (Exception e) {
            log.error("Failed to clear UserDetails cache for username={}: {}", username, e.getMessage());
        }
    }
    
    /**
     * 清除所有损坏的UserDetails缓存
     */
    public void clearAllUserDetailsCache() {
        try {
            Set<String> userDetailsKeys = redisTemplate.keys("userdetails:*");
            if (userDetailsKeys != null && !userDetailsKeys.isEmpty()) {
                redisTemplate.delete(userDetailsKeys);
                log.info("Cleared all UserDetails cache entries");
            }
        } catch (Exception e) {
            log.error("Failed to clear all UserDetails cache: {}", e.getMessage());
        }
    }
}
