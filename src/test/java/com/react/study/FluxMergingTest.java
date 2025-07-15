package com.react.study;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

public class FluxMergingTest {

    @Test
    public void mergeFluxes() {
        Flux<String> names = Flux
                .just("Garfield","Kojak","Barbossa")
                .delayElements(Duration.ofMillis(500));
        Flux<String> foods = Flux
                .just("Lasagna","Lollipops","Apples")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = names.mergeWith(foods);
        StepVerifier.create(mergedFlux)
                .expectNext("Garfield")
                .expectNext("Lasagna")
                .expectNext("Kojak")
                .expectNext("Lollipops")
                .expectNext("Barbossa")
                .expectNext("Apple")
                .verifyComplete();
    }

    @Test
    public void zipFluxes() {
        Flux<String> names = Flux.just("Garfield","Kojak","Barbossa");
        Flux<String> foods = Flux.just("Lasagna","Lollipops","Apple");

        Flux<Tuple2<String,String>> Fluxes = Flux.zip(names,foods);

        StepVerifier.create(Fluxes)
                .expectNextMatches(p ->
                        p.getT1().equals("Garfield") &&
                        p.getT2().equals("Lasagna"))
                .expectNextMatches(p->
                        p.getT1().equals("Kojak") &&
                        p.getT2().equals("Lollipops"))
                .expectNextMatches(p->
                        p.getT1().equals("Barbossa") &&
                        p.getT2().equals("Apple"))
                .verifyComplete();
    }

    @Test
    public void firstWithSignalFlux() {
        Flux<String> slowFlux = Flux.just("tortoise","snail","sloth")
                .delaySubscription(Duration.ofMillis(100));
        Flux<String> fastFlux = Flux.just("hare","cheetah","squirrel");
        Flux<String> firstFlux = Flux.firstWithSignal(slowFlux,fastFlux);
        StepVerifier.create(firstFlux)
                .expectNext("hare")
                .expectNext("cheetah")
                .expectNext("squirrel")
                .verifyComplete();
    }
}
