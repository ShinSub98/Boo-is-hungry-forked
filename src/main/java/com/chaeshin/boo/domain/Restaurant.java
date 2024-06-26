package com.chaeshin.boo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Getter;

@Entity
@Getter
public class Restaurant {
    @Id @GeneratedValue
    @Column(name = "restaurant_id")
    private Long id;

    private String name;

    private String imageUrl;

    private String latitude;

    private String longitude;

    private String openAt;

    private String address;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String phone;

    private int reviewCount;

    private int scoreAccum;

    @Column(precision = 2, scale = 1)
    private BigDecimal scoreAvg;

}
