package com.chaeshin.boo.repository;

import com.chaeshin.boo.domain.Member;
import com.chaeshin.boo.domain.restaurant.Category;
import com.chaeshin.boo.domain.restaurant.Menu;
import com.chaeshin.boo.domain.restaurant.Restaurant;
import com.chaeshin.boo.domain.review.Review;
import com.chaeshin.boo.domain.review.ReviewImage;
import com.chaeshin.boo.repository.restaurant.RestaurantRepository;
import com.chaeshin.boo.repository.review.ReviewImageRepository;
import com.chaeshin.boo.repository.review.ReviewRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReviewImageRepository imageRepository;
    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Test
    public void 리뷰_작성() throws Exception {
        /*given*/
        Restaurant restaurant = restaurantRepository.save(createRestaurant());
        Member member = memberRepository.save(createMember("kim"));

        /*when*/
        Review review = createReview(member, restaurant);
        Review savedReview = reviewRepository.save(review);

        /*then*/
        assertEquals(review, savedReview);
    }


    @Test
    public void 유저_리뷰_리스트_조회() throws Exception {
        /*given*/
        Restaurant restaurant = restaurantRepository.save(createRestaurant());
        Member member = memberRepository.save(createMember("kim"));

        /*when*/
        for (int i = 0; i < 20; i++) {
            Review review = reviewRepository.save(createReview(member, restaurant));
            member.getReviews().add(review);
        }

        /*then*/
        // 유저의 리스트에서 확인
        assertEquals(20, member.getReviews().size());

        // 레포지토리에서 확인
        List<Review> result = reviewRepository.findAllByMemberIdWithReviewImages(member.getId());
        assertEquals(20, result.size());
    }

    @Test
    public void 식당_리뷰_리스트_조회() throws Exception {
        /*given*/
        Restaurant restaurant = restaurantRepository.save(createRestaurant());
        Member member = memberRepository.save(createMember("kim"));

        /*when*/
        for (int i = 0; i < 20; i++) {
            Review review = reviewRepository.save(createReview(member, restaurant));
            restaurant.getReviews().add(review);
        }

        /*then*/
        // 식당의 리스트에서 확인
        assertEquals(20, restaurant.getReviews().size());

        // 레포지토리에서 확인
        assertEquals(20, reviewRepository.findAllByRestaurantIdWithReviewImages(restaurant.getId()).size());
    }

    @Test
    public void 리뷰_이미지_업로드() throws Exception {
        /*given*/
        Restaurant restaurant = restaurantRepository.save(createRestaurant());
        Member member = memberRepository.save(createMember("kim"));
        Review review = reviewRepository.save(createReview(member, restaurant));
        member.getReviews().add(review);
        restaurant.getReviews().add(review);

        /*when*/
        ReviewImage image = createReviewImage(review);
        ReviewImage savedImage = imageRepository.save(image);

        /*then*/
        assertEquals(image, savedImage);
    }

    @Test
    public void 리뷰_이미지_조회() throws Exception {
        /*given*/
        Restaurant restaurant = restaurantRepository.save(createRestaurant());
        Member member = memberRepository.save(createMember("kim"));
        Review review = reviewRepository.save(createReview(member, restaurant));
        member.getReviews().add(review);
        restaurant.getReviews().add(review);

        /*when*/
        for (int i = 0; i < 20; i++) {
            ReviewImage image = imageRepository.save(createReviewImage(review));
            review.getReviewImages().add(image);
        }

        /*then*/
        // 리뷰의 리스트에서 확인
        assertEquals(20, review.getReviewImages().size());

        // 레포지토리에서 확인
        assertEquals(20, reviewRepository.findById(review.getId()).get().getReviewImages().size());
    }

    @Test
    public void 리뷰_이미지_테스트() throws Exception {
        /*given*/
        restaurantRepository.save(createRestaurant());
        Restaurant restaurant = restaurantRepository.findById(1L).get();
        Member member = memberRepository.save(createMember("kim"));

        for (int i = 0; i < 3; i++) {
            Review review = reviewRepository.save(createReview(member, restaurant));
            restaurant.getReviews().add(review);
            for (int k = 0; k < 3; k++) {
                ReviewImage reviewImage = imageRepository.save(createReviewImage(review));
                review.getReviewImages().add(reviewImage);
            }
        }

        /*when*/
        List<Review> reviews = restaurant.getReviews();
        List<ReviewImage> images = reviews.get(0).getReviewImages();

        /*then*/
        assertEquals(3, reviews.size());
        assertEquals(3, images.size());
    }


    private Restaurant createRestaurant() {
        return new Restaurant().builder().name("맥도날드").latitude("위도").longitude("경도")
                .category(Category.KOREAN).build();
    }

    private Menu createMenu(Restaurant restaurant) {
        return new Menu().builder().restaurant(restaurant).name("햄버거").build();
    }

    private Review createReview(Member member, Restaurant restaurant) {
        return new Review().builder().restaurant(restaurant).member(member).build();
    }

    private ReviewImage createReviewImage(Review review) {
        return new ReviewImage().builder().review(review).build();
    }

    private Member createMember(String nickname) {
        return new Member().builder().nickname(nickname).build();
    }
}
