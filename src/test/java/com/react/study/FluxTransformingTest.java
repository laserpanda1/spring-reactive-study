package com.react.study;

import lombok.Data;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class FluxTransformingTest {

    @Test
    public void skipAFew() {
        Flux<String> countFlux = Flux.just("one","two","skip a few", "ninety nine", "one hundred")
                .skip(3);
        StepVerifier.create(countFlux)
                .expectNext("ninety nine", "one hundred")
                .verifyComplete();
    }

    @Test
    public void skipAFewSeconds() {
        Flux<String> counts = Flux.just("one","two","skip a few","ninety nine","one hundred")
                .delayElements(Duration.ofSeconds(1))
                .skip(Duration.ofSeconds(4));
        StepVerifier.create(counts)
                .expectNext("ninety nine", "one hundred")
                .verifyComplete();
    }

    @Test
    public void takeAFew() {
        Flux<String> counts = Flux.just("one","two","skip a few","ninety nine","one hundred")
                .take(3);
        StepVerifier.create(counts)
                .expectNext("one","two","skip a few")
                .verifyComplete();
    }

    @Test
    public void filter() {
        Flux<String> nationalParkFlux = Flux.just("Yellowstone","Yosemite","Grand Canyon","Zion","Grand Teton")
                .filter(f->!f.contains(" "));
        StepVerifier.create(nationalParkFlux)
                .expectNext("Yellowstone","Yosemite","Zion")
                .verifyComplete();

    }

    @Test
    public void distinct() {
        Flux<String> animals = Flux.just("dog","cat","dog","cat","cat").distinct();
        StepVerifier.create(animals)
                .expectNext("dog","cat")
                .verifyComplete();
    }

    @Test
    public void MapFlux() {
        Flux<Player> PlayerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .map(n->{
                   String[] split = n.split("\\s");
                   return new Player(split[0],split[1]);
                });
        StepVerifier.create(PlayerFlux)
                .expectNext(new Player("Michael", "Jordan"))
                .expectNext(new Player("Scottie", "Pippen"))
                .expectNext(new Player("Steve", "Kerr"))
                .verifyComplete();
    }


    @Data
    private static class Player {
        private final String firstName;
        private final String lastName;
    }


    @Test
    public void flatMap() {
        Flux<Player> names = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n-> Mono.just(n)
                        .map(p-> {
                            String[] split = p.split("\\s");
                            return new Player(split[0], split[1]);
                        })
                        .subscribeOn(Schedulers.parallel()));

        List<Player> playerList = Arrays.asList(
                new Player("Michael", "Jordan"),
                new Player("Scottie", "Pippen"),
                new Player("Steve", "Kerr")
        );

        StepVerifier.create(names)
                .expectNextMatches(p->playerList.contains(p))
                .expectNextMatches(p->playerList.contains(p))
                .expectNextMatches(p->playerList.contains(p))
                .verifyComplete();
    }

}
