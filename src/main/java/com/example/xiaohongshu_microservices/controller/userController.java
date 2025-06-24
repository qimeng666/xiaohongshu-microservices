package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.domain.Users;
import com.example.xiaohongshu_microservices.mapper.UsersMapper;
import com.example.xiaohongshu_microservices.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户基础信息增删改查")
public class userController {
    private final UsersService usersService;
    private final UsersMapper usersMapper;

    public userController(UsersService userService, UsersMapper usersMapper) {
        this.usersService = userService;
        this.usersMapper = usersMapper;
    }

    @PostMapping("/createUser")
    @Operation(summary = "创建新用户")
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        usersService.save(user);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{userId}")
    @Operation(summary = "根据用户ID查询用户信息")
    public ResponseEntity<Users> getById(@PathVariable Long userId) {
        Users user = usersService.getById(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{userId}")
    @Operation(summary = "更新用户邮箱")
    public ResponseEntity<Users> updateEmail(@PathVariable Long userId, @RequestParam String newEmail) {
        Users user = usersService.getById(userId);
        if (user != null) {
            user.setEmail(newEmail);
            usersService.updateById(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
