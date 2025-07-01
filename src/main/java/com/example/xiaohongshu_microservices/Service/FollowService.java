package com.example.xiaohongshu_microservices.Service;

import com.example.xiaohongshu_microservices.Entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FollowService {
    void follow(Long userId, Long targetUserId);

    void unfollow(Long userId, Long targetUserId);

    List<User> getFollowing(Long userId);

    List<User> getFollowers(Long userId);
}
