package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Service.FollowService;
import com.example.xiaohongshu_microservices.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/users")
@Tag(name = "内部用户管理", description = "内部用户基础信息增删改查")
public class internalController {

    private final UserService userService;
    private final FollowService followService;

    public internalController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @Operation(summary = "内部_获取指定用户粉丝列表")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<Long>> interGetFollower(
            @PathVariable Long userId) {

        List<Long> followers = followService.getFollowers(userId)
                .stream()
                .map(f -> f.getId())   // 或者 f.getId()
                .collect(Collectors.toList());
        return ResponseEntity.ok(followers);
    }
}
