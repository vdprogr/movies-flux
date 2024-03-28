package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest()
@ActiveProfiles(value = "test")
class MovieInfoRepositoryIntegTest {

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
    void findAll() {
        //given

        //when
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        //given

        //when
        var moviesInfoMono = movieInfoRepository.findById("abc").log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        //given
        var movieInfo = new MovieInfo(null, "Raman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        //when
        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(mInfo -> {
                    assertNotNull(mInfo.getMovieInfoId());
                    assertEquals("Raman Begins", mInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        //given
        var movieInfo = movieInfoRepository.findById("abc").block();
        movieInfo.setYear(2024);
        //when
        var moviesInfoMono = movieInfoRepository.save(movieInfo).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .assertNext(mInfo -> {
                    assertNotNull(mInfo.getMovieInfoId());
                    assertEquals(2024, mInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {
        //given

        //when
        movieInfoRepository.deleteById("abc").block();
        var moviesInfoFlux = movieInfoRepository.findAll().log();

        //then
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByYear() {
        //given

        //when
        var moviesInfoMono = movieInfoRepository.findByYear(2005).log();

        //then
        StepVerifier.create(moviesInfoMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findByName() {

        var movieInfosMono = movieInfoRepository.findByName("Batman Begins").log();

        StepVerifier.create(movieInfosMono)
                .expectNextCount(1)
                .verifyComplete();


    }
}