package com.chaeshin.boo.domain.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class ReviewImage {
    @Id @GeneratedValue
    @Column(name = "id")
    private Long id; // 왜 primitive type이 아닌 wrapper class type 을 사용할까?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
    private String imageUrl;
}
