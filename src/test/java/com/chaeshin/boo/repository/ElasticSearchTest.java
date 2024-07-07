package com.chaeshin.boo.repository;

import com.chaeshin.boo.config.ElasticSearchConfig;
import com.chaeshin.boo.domain.restaurant.Category;
import com.chaeshin.boo.domain.restaurant.Restaurant;
import com.chaeshin.boo.domain.restaurant.RestaurantDocument;
import com.chaeshin.boo.repository.restaurant.RestaurantRepository;
import com.chaeshin.boo.repository.restaurant.RestaurantDocumentRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@DataElasticsearchTest
@Testcontainers
@Import(ElasticSearchConfig.class)
@ComponentScan(basePackages = "com.chaeshin.boo.repository.restaurant")
public class ElasticSearchTest {

    @Container
    @ServiceConnection
    public static ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.5.3");

    @Autowired private ElasticsearchOperations elasticsearchOperations;

    @Autowired private RestaurantDocumentRepository restaurantDocumentRepository;
    @Autowired private RestaurantRepository restaurantRepository;

    @BeforeEach
    public void setUp() {
        await().untilAsserted(() ->  {
            Assert.assertEquals(elasticsearchContainer.isRunning(), true);
        });
    }

    @Test
    public void ES컨테이너_실행_테스트() throws Exception {
        assertEquals(elasticsearchContainer.isRunning(), true);
    }

    @Test
    public void 엘라스틱서치_테스트() throws Exception {
        /*given*/
        Restaurant res1 = restaurantRepository.save(createRestaurant("맘스터치"));
        restaurantDocumentRepository.save(RestaurantDocument.from(res1));

        Restaurant res2 = restaurantRepository.save(createRestaurant("맥도날드"));
        restaurantDocumentRepository.save(RestaurantDocument.from(res2));

        Restaurant res3 = restaurantRepository.save(createRestaurant("케이 에프 씨"));
        restaurantDocumentRepository.save(RestaurantDocument.from(res3));

        Restaurant res4 = restaurantRepository.save(createRestaurant("맘스킥"));
        restaurantDocumentRepository.save(RestaurantDocument.from(res4));

        /*when*/
        List<RestaurantDocument> moms = restaurantDocumentRepository.findByNameContaining("맘스");
        List<RestaurantDocument> kfc = restaurantDocumentRepository.findByNameContaining("에프");

        /*then*/
        assertEquals(2, moms.size());
        assertEquals(1, kfc.size());
    }


    private Restaurant createRestaurant(String name) {
        return new Restaurant().builder().name(name).latitude("위도").longitude("경도")
                .category(Category.KOREAN).build();
    }
}
