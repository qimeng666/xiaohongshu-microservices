package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Service.FollowService;
import com.example.xiaohongshu_microservices.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户基础信息增删改查")
public class userController {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final FollowService followService;

    public userController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @Operation(summary = "创建新用户")
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            userService.save(user);
            return ResponseEntity.ok("创建成功");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建失败");
        }
    }

    @Operation(summary = "根据用户ID查询用户信息")
    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.details or hasRole('ADMIN')")
    public ResponseEntity<User> getById(@PathVariable Long userId) {
        Optional<User> opt = userService.getById(userId);
        return opt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "更新用户信息，可传入任意可修改字段")
    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.details or hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(
            @PathVariable Long userId,
            @RequestBody User updates) {
        Optional<User> cur = userService.getById(userId);
        if (cur.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("用户未找到，更新失败");
        }

        User existing = cur.get();
        if (updates.getEmail() != null) {
            existing.setEmail(updates.getEmail());
        }
        if (updates.getUsername() != null) {
            existing.setUsername(updates.getUsername());
        }
        // update password
        // if (updates.getPassword() != null) {
        //     existing.setPassword(encoder.encode(updates.getPassword()));
        // }

        try {
            userService.update(existing);
            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新失败");
        }
    }

    @Operation(summary = "根据邮箱精确查询用户信息")
    @GetMapping(params = "email")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        Optional<User> opt = userService.findByEmail(email);
        return opt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.findByName(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            if (encoder.matches(password, user.getPassword())) {
                return ResponseEntity.ok("登录成功");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("登录失败");
        }
    }

    @Operation(summary = "关注用户")
    @PostMapping("/{userId}/follow/{targetUserId}")
    public ResponseEntity<String> follow(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {

        followService.follow(userId, targetUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body("关注成功");
    }

    @Operation(summary = "取消关注用户")
    @DeleteMapping("/{userId}/unfollow/{targetUserId}")
    public ResponseEntity<String> unfollow(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {

        followService.unfollow(userId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取指定用户关注列表")
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(
            @PathVariable Long userId) {

        List<User> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }
    @Operation(summary = "获取指定用户粉丝列表")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollower(
            @PathVariable Long userId) {

        List<User> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }
    
    @Operation(summary = "清除所有用户缓存")
    @DeleteMapping("/cache/clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearUserCache() {
        try {
            // 需要强制转换为 UserServiceImpl 来访问 clearAllUserCache 方法
            if (userService instanceof com.example.xiaohongshu_microservices.Service.Impl.UserServiceImpl) {
                ((com.example.xiaohongshu_microservices.Service.Impl.UserServiceImpl) userService).clearAllUserCache();
                return ResponseEntity.ok("用户缓存清除成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("无法访问缓存服务");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("清除缓存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "清除所有UserDetails缓存")
    @DeleteMapping("/cache/clear-userdetails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> clearUserDetailsCache() {
        try {
            if (userService instanceof com.example.xiaohongshu_microservices.Service.Impl.UserServiceImpl) {
                ((com.example.xiaohongshu_microservices.Service.Impl.UserServiceImpl) userService).clearAllUserDetailsCache();
                return ResponseEntity.ok("UserDetails缓存清除成功");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("无法访问缓存服务");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("清除UserDetails缓存失败: " + e.getMessage());
        }
    }

}
