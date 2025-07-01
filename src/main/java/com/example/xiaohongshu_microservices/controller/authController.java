package com.example.xiaohongshu_microservices.controller;

import com.example.xiaohongshu_microservices.Entity.AuthRequest;
import com.example.xiaohongshu_microservices.Entity.User;
import com.example.xiaohongshu_microservices.Repository.UserRepository;
import com.example.xiaohongshu_microservices.Service.BlacklistService;
import com.example.xiaohongshu_microservices.Service.UserService;
import com.example.xiaohongshu_microservices.Utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "认证管理", description = "后台用户管理")
@RequestMapping("/auth")
public class authController {
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userDetailsService;
    private final BlacklistService blacklistService;
    public authController(AuthenticationManager authManager, JwtUtil jwtUtil,
                          UserService uds, BlacklistService bls) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
        this.blacklistService = bls;
    }

    @Operation(summary = "登录以后返回token")
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try{
            var auth = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
            authManager.authenticate(auth);
            var userDetails = userDetailsService.loadUserByUsername(req.getUsername());
            Long userId = userDetailsService.findIdByName(req.getUsername());
            String token = jwtUtil.generateToken(userDetails, userId);
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("accessToken", token);
            return ResponseEntity.ok(tokenMap);
        }
        catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }

    }
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if(header == null || !header.startsWith("Bearer ")){
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "缺少或无效的 Authorization 头"));
        }
        String token = header.substring(7);
        blacklistService.blacklist(token);
        return ResponseEntity.noContent().build();
    }
}
