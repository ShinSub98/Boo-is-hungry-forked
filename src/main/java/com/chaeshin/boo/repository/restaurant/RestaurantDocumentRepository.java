package com.chaeshin.boo.repository.restaurant;

import com.chaeshin.boo.domain.restaurant.RestaurantDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface RestaurantDocumentRepository extends ElasticsearchRepository<RestaurantDocument, Long> {

    List<RestaurantDocument> findByNameContaining(String name);
}
