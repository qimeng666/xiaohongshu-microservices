package com.example.xiaohongshu_microservices.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 发起关注
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // 被关注者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    public Follow() {
    }

    public Long getId() {
        return this.id;
    }

    public User getFollower() {
        return this.follower;
    }

    public User getFollowee() {
        return this.followee;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Follow)) return false;
        final Follow other = (Follow) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$follower = this.getFollower();
        final Object other$follower = other.getFollower();
        if (this$follower == null ? other$follower != null : !this$follower.equals(other$follower)) return false;
        final Object this$followee = this.getFollowee();
        final Object other$followee = other.getFollowee();
        if (this$followee == null ? other$followee != null : !this$followee.equals(other$followee)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Follow;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $follower = this.getFollower();
        result = result * PRIME + ($follower == null ? 43 : $follower.hashCode());
        final Object $followee = this.getFollowee();
        result = result * PRIME + ($followee == null ? 43 : $followee.hashCode());
        return result;
    }

    public String toString() {
        return "Follow(id=" + this.getId() + ", follower=" + this.getFollower() + ", followee=" + this.getFollowee() + ")";
    }
}
