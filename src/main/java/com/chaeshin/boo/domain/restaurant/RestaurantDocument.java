package com.chaeshin.boo.domain.restaurant;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "restaurant")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantDocument {

    @Id @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    public static RestaurantDocument from (Restaurant restaurant) {
        return RestaurantDocument.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .build();
    }
}
