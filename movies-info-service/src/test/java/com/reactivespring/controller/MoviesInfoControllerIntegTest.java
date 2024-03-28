package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegTest {

    @Autowired
    WebTestClient webTestClient;
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        movieInfoRepository.deleteAll();
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {

        var movieInfo = new MovieInfo("3", "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri("/v1/movieinfos")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                          var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                          assertNotEquals( null, savedMovieInfo);
                          assertNotEquals(null, savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void getAllMovieInfo() {

        webTestClient
                .get()
                .uri("/v1/movieinfos")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {

        var movieInfoId = "abc";

        webTestClient
                .get()
                .uri("/v1/movieinfos/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                //.expectBody()
                //.jsonPath("$.name").isEqualTo("Dark Knight Rises")
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotEquals(null, savedMovieInfo);
                    assertEquals(movieInfoId, savedMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void getMovieInfoByIdNotFound() {

        var movieInfoId = "abcdef";

        webTestClient
                .get()
                .uri("/v1/movieinfos/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo() {

        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Lotman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
                .put()
                .uri("/v1/movieinfos/{id}", movieInfoId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotEquals(null, updatedMovieInfo);
                    assertEquals( "Lotman Begins", updatedMovieInfo.getName());
                });

    }

    @Test
    void delleteMovieInfo() {

        var movieInfoId = "abc";

        webTestClient
                .delete()
                .uri("/v1/movieinfos/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }
}
