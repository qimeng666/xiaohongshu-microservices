package com.example.xiaohongshu_microservices.Service;

import com.example.xiaohongshu_microservices.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    User save(User user);
    Optional<User> getById(Long id);
    User update(User user);
    Optional<User> findByEmail(String email);

    Optional<User> findByName(String username);
    Long findIdByName(String username);

    UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException;
    String login(String username, String password);
}
