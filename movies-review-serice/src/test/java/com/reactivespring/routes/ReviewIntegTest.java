package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewIntegTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review("2", 1L, "Awesome Movie1", 9.0),
                new Review("3", 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {

        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assertNotEquals(null, savedReview);
                    assertNotEquals(null, savedReview.getReviewId());
                });
    }

    @Test
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });

    }

    @Test
    void updateMovieInfo() {
        //given
        var reviewId = "1";
        //when
        var updatedReview = new Review(null, 1L, "Not an Awesome Movie", 8.0);
        ;

        //then
        webTestClient
                .put()
                .uri("/v1/reviews/{id}", reviewId)
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var uReview = reviewResponse.getResponseBody();
                    assert uReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertNotNull(uReview.getReviewId());
                    assertEquals(8.0, uReview.getRating());
                    assertEquals("Not an Awesome Movie", uReview.getComment());
                });
    }

    @Test
    void deleteReview() {
        //given
        var reviewId = "1";

        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getReviewsByMovieInfoId() {
        //given
        var movieInfoId = "1";

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path("/v1/reviews")
                            .queryParam("movieInfoId", movieInfoId)
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    assertEquals(2, reviewList.size());
                });

    }
}
