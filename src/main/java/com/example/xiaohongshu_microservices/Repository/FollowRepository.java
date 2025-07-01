package com.example.xiaohongshu_microservices.Repository;

import com.example.xiaohongshu_microservices.Entity.Follow;
import com.example.xiaohongshu_microservices.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowee(User follower, User followee);

    void deleteByFollowerAndFollowee(User follower, User followee);

    List<Follow> findByFollower(User follower);

    List<Follow> findByFollowee(User followee);
}
