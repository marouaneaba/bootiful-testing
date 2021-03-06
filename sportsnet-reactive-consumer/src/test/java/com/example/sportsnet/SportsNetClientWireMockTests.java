package com.example.sportsnet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@RunWith(SpringRunner.class)
//@AutoConfigureWireMock(port = 8089)
//@AutoConfigureJsonTesters
public class SportsNetClientWireMockTests {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private SportsNetClient client;

    private final Team first = new Team("1", "REDS");
    private final Team second = new Team("2", "BLUES");

    //@Before
    public void setupWireMock() throws JsonProcessingException {
        String jsonBody = objectMapper.writeValueAsString(Arrays.asList(first, second));
        String favoritesJson = objectMapper.writeValueAsString(Arrays.asList(second));

        WireMock.stubFor(
                WireMock
                        .get("/teams/all")
                        .willReturn(
                                WireMock
                                        .aResponse()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(jsonBody)
                        )
        );

        WireMock.stubFor(
                WireMock
                        .get("/teams/favorites")
                        .willReturn(
                                WireMock
                                        .aResponse()
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                                        .withBody(favoritesJson)
                        )
        );

    }

    //@Test
    public void testShouldFetchTeams() {
        Flux<Team> customers = this.client.getAllTeams();
        StepVerifier
                .create(customers)
                .expectNext(new Team("1", "REDS"))
                .expectNext(new Team("2", "BLUES"))
                .verifyComplete();
    }

    //@Test
    public void testShouldFailToFetch() {
        Flux<Team> customers = this.client.getFavorites();
        StepVerifier
                .create(customers)
                .expectNext(new Team("2", "BLUES"))
                .verifyComplete();
    }
}