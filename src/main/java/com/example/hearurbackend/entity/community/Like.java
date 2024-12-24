package com.example.hearurbackend.entity.community;

import com.example.hearurbackend.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "post"})
})
public class Like {
    @Id
    @Column(name = "like_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long like_no;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post", nullable = false)
    private Post post;

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}

