package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户基础信息增删改查")
@RequiredArgsConstructor
public class userController {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Operation(summary = "创建新用户")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        User saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "根据用户ID查询用户信息")
    @GetMapping("/{userId}")
    public ResponseEntity<User> getById(@PathVariable Long userId) {
        return userService.getById(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "更新用户邮箱")
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateEmail(
            @PathVariable Long userId,
            @RequestParam String newEmail) {
        return userService.getById(userId)
                .map(user -> {
                    user.setEmail(newEmail);
                    return ResponseEntity.ok(userService.update(user));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "根据邮箱精确查询用户信息")
    @GetMapping(params = "email")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
