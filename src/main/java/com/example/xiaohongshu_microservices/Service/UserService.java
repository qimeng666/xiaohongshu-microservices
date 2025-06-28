package com.example.xiaohongshu_microservices.Service;

import com.example.xiaohongshu_microservices.Entity.User;

import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> getById(Long id);
    User update(User user);
    Optional<User> findByEmail(String email);
}
