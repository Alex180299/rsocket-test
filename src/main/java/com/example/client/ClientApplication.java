package com.example.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Log4j2
@SpringBootApplication
public class ClientApplication implements ApplicationRunner
{

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public RSocketRequester requester() {
        return RSocketRequester
                .builder()
                .connectTcp("localhost", 4000)
                .block();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        Mono<String> hello = Mono.just("Hello server");
        Flux<String> mainFlux = Flux.just(1,2,3).map(aLong -> "{\"companyId\": 8,\"type\": \"status\",\"different\": \"1\",\"newValue\": \"2\",\"conversationId\": \"605cee7d10c4e56598d946b5\"}");

        this.requester().route("/topic/notifications/{companyId}")
                .data(Flux.merge(hello, mainFlux))
                .retrieveFlux(String.class)
                .subscribe(log::info);
    }

}