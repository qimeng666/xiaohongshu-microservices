package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户基础信息增删改查")
public class userController {
    private final UserService userService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public userController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "创建新用户")
    @PostMapping
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
    public ResponseEntity<User> getById(@PathVariable Long userId) {
        Optional<User> opt = userService.getById(userId);
        return opt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "更新用户信息，可传入任意可修改字段")
    @PutMapping("/{userId}")
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
       try{
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
}
