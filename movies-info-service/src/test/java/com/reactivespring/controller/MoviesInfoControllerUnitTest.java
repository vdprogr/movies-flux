package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
class MoviesInfoControllerUnitTest {
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    private MoviesInfoService movieInfoServiceMock;

    @Test
    void getAllMovieInfos() {

        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));

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

        var movieInfo = new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoServiceMock.getMovieInfoById(isA(String.class))).thenReturn(Mono.just(movieInfo));

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
    void addMovieInfo() {

        var movieInfo = new MovieInfo("3", "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

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
    void addMovieInfoValidation() {

        var movieInfo = new MovieInfo("3", "",
                -2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));

        webTestClient
                .post()
                .uri("/v1/movieinfos")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void updateMovieInfo() {

        var movieInfoId = "abc";
        var movieInfo = new MovieInfo(null, "Lotman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).thenReturn(Mono.just(
                new MovieInfo("mockid", "Lotman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))
        ));
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
                    assertEquals( "mockid", updatedMovieInfo.getMovieInfoId());
                });

    }

    @Test
    void delleteMovieInfo() {

        var movieInfoId = "abc";

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/v1/movieinfos/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
    }
}