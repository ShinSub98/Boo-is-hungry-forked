package com.chaeshin.boo.domain;

import com.chaeshin.boo.domain.review.Review;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String googleId; // 구글 PK
    private String nickname;
    private String memberLang; // 유저 선호 언어

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    public Member() {}

    @Builder
    public Member(String googleId, String nickname, String memberLang) {
        this.googleId = googleId;
        this.nickname = nickname;
        this.memberLang = memberLang;
    }
}
