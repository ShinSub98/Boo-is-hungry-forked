package com.chaeshin.boo.controller;

import com.chaeshin.boo.exception.CustomException;
import com.chaeshin.boo.exception.Exceptions;
import com.chaeshin.boo.service.restaurant.dto.RestaurantSimpleDto;
import com.chaeshin.boo.service.review.ReviewService;
import com.chaeshin.boo.service.review.dto.*;
import com.chaeshin.boo.utils.ResponseDto;
import com.chaeshin.boo.utils.jwt.JwtProvider;
import com.chaeshin.boo.utils.translator.ServiceTranslatorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtProvider jwtProvider;

    @GetMapping("/myreview/")
    public ResponseEntity<ResponseDto<List<ReviewDto>>> getMyReview(HttpServletRequest request) {
        jwtProvider.validateToken(request);
        Long memberId = jwtProvider.getMemberId(request);

        return new ResponseEntity<>(reviewService.getMyReviews(memberId), HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurantId}/")
    public ResponseEntity<ResponseDto<List<ReviewDto>>> getRestaurantByReview(
            HttpServletRequest request, @PathVariable Long restaurantId) {
        jwtProvider.validateToken(request);

        return new ResponseEntity<>(reviewService
                .getReviewByRestaurantId(restaurantId), HttpStatus.OK);
    }

    @GetMapping("/restaurant/simpleinfo/{reviewId}/")
    public ResponseEntity<ResponseDto<RestaurantSimpleDto>> getRestaurantSimpleInfo(
            HttpServletRequest request, @PathVariable Long reviewId) {
        jwtProvider.validateToken(request);

        return new ResponseEntity<>(reviewService
                .getRestaurantSimpleInfoByReviewId(reviewId), HttpStatus.OK);
    }

    @GetMapping("/user/{memberId}/")
    public ResponseEntity<ResponseDto<List<ReviewDto>>> getMemberReviews
            (HttpServletRequest request, @PathVariable Long memberId) {
        jwtProvider.validateToken(request);

        return new ResponseEntity<>(reviewService
                .getReviewsByMemberId(memberId), HttpStatus.OK);
    }

    @PostMapping("/create/{restaurantId}/")
    public ResponseEntity<ResponseDto<ReviewDto>> createReview(
            HttpServletRequest request, @PathVariable Long restaurantId,
            @RequestBody ReviewRequestDto reviewRequestDto) {
        jwtProvider.validateToken(request);
        Long memberId = jwtProvider.getMemberId(request);

        return new ResponseEntity<>(reviewService.createReview(memberId,
                restaurantId, reviewRequestDto), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/update/{reviewId}/", method = {RequestMethod.DELETE, RequestMethod.PUT})
    public ResponseEntity<ResponseDto> updateDeleteReview(
            HttpServletRequest request, @PathVariable Long reviewId,
            @RequestBody(required = false) ReviewRequestDto reviewRequestDto) {
        jwtProvider.validateToken(request);
        Long memberId = jwtProvider.getMemberId(request);

        if ("PUT".equals(request.getMethod())) {
            ResponseDto result = reviewService.updateReview(memberId, reviewId, reviewRequestDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if ("DELETE".equals(request.getMethod())) {
            ResponseDto result = reviewService.deleteReview(memberId, reviewId);
            return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
        }
        throw new CustomException(Exceptions.METHOD_NOT_ALLOWED);
    }

    @GetMapping("/detail/{reviewId}/")
    public ResponseEntity<ResponseDto<ReviewDetailDto>> getReviewDetail(
            HttpServletRequest request, @PathVariable Long reviewId) {
        jwtProvider.validateToken(request);

        ResponseDto<ReviewDetailDto> result = reviewService.getReviewDetail(reviewId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/image/original/{imageId}/")
    public ResponseEntity<ResponseDto<OriginalImageDto>> getOriginalImage(@PathVariable Long imageId) {
        return new ResponseEntity<>(reviewService.getOriginalImageUrl(imageId),
                HttpStatus.OK);
    }

    @GetMapping("/trans/{reviewId}/")
    public ResponseEntity<ResponseDto<ServiceTranslatorDto>> translateReview(
            HttpServletRequest request, @PathVariable Long reviewId) {
        jwtProvider.validateToken(request);
        Long memberId = jwtProvider.getMemberId(request);

        return new ResponseEntity<>(reviewService.
                translateReview(memberId, reviewId), HttpStatus.OK);
    }

    @PostMapping("/image/upload/{reviewId}/")
    public ResponseEntity<ResponseDto<ImageUploadDto>> uploadImage (@PathVariable Long reviewId,
                @RequestParam(name = "original_image") MultipartFile image) throws IOException {
        return new ResponseEntity<>(reviewService.
                uploadReviewImage(reviewId, image), HttpStatus.CREATED);
    }
}

