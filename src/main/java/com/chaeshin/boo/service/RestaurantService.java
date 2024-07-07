package com.chaeshin.boo.service;

import com.chaeshin.boo.domain.restaurant.Category;
import com.chaeshin.boo.domain.restaurant.Restaurant;
import com.chaeshin.boo.domain.restaurant.RestaurantDocument;
import com.chaeshin.boo.repository.restaurant.RestaurantDocumentRepository;
import com.chaeshin.boo.repository.restaurant.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDocumentRepository documentRepository;

    @Transactional
    public Long save(String name) {
        Restaurant restaurant = new Restaurant().builder().name(name).latitude("위도").longitude("경도")
                .category(Category.KOREAN).build();

        restaurantRepository.save(restaurant);
        documentRepository.save(RestaurantDocument.from(restaurant));
        return restaurant.getId();
    }

    public List<RestaurantDocument> searchRestaurant(String keyword) {
        return documentRepository.findByNameContaining(keyword);
    }
}
