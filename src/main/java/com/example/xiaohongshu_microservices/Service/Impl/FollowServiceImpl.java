package com.example.xiaohongshu_microservices.Service.Impl;

import com.example.xiaohongshu_microservices.Entity.Follow;
import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Repository.FollowRepository;
import com.example.xiaohongshu_microservices.Repository.UserRepository;
import com.example.xiaohongshu_microservices.Service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private FollowRepository followRepo;
    @Autowired
    private UserRepository userRepo;
    private User loadUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: " + id));
    }
    @Override
    @Transactional
    public void follow(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能关注自己");
        }
        User follower = loadUser(userId);
        User followee = loadUser(targetUserId);
        if (followRepo.existsByFollowerAndFollowee(follower, followee)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "已关注");
        }
        followRepo.save(new Follow(follower, followee));
    }

    @Override
    @Transactional
    public void unfollow(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能取消关注自己");
        }
        User follower = loadUser(userId);
        User followee = loadUser(targetUserId);
        if (!followRepo.existsByFollowerAndFollowee(follower, followee)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "还没有关注该用户");
        }
        followRepo.deleteByFollowerAndFollowee(follower, followee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowing(Long userId) {
        User follower = loadUser(userId);
        return followRepo.findByFollower(follower).stream().map(Follow::getFollowee).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowers(Long userId) {
        User followee = loadUser(userId);
        return followRepo.findByFollowee(followee).stream().map(Follow::getFollower).collect(Collectors.toList());
    }
}
