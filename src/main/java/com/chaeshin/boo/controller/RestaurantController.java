package com.chaeshin.boo.controller;

import com.chaeshin.boo.domain.restaurant.RestaurantDocument;
import com.chaeshin.boo.service.RestaurantService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public Long addRestaurant(@RequestParam String name) {
        return restaurantService.save(name);
    }

    @GetMapping
    public List<DocumentDto> searchRestaurants(@RequestParam String keyword) {
         List<RestaurantDocument> restaurants = restaurantService.searchRestaurant(keyword);
         List<DocumentDto> result = restaurants.stream()
                 .map(o -> new DocumentDto(o)).collect(Collectors.toList());
         return result;
    }

    @Data
    static class DocumentDto {

        private Long id;
        private String name;

        public DocumentDto(RestaurantDocument restaurantDocument) {
            this.id = restaurantDocument.getId();
            this.name = restaurantDocument.getName();
        }
    }
}
