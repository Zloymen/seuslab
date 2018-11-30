package ru.seuslab.service.fluxservice1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.seuslab.service.fluxservice1.dto.AnswerDto;
import ru.seuslab.service.fluxservice1.dto.DetailDto;

import javax.annotation.PostConstruct;
import java.time.Duration;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestServiceImpl implements RestService {

    @Value("${service2.url}")
    private String url;

    private WebClient client;

    @PostConstruct
    private void post(){
        client = WebClient.create();
    }


    @Override
    public Flux<DetailDto> getAsyncData(String projectName, long millsecond){
        return client.get().uri(builder ->
                        fromHttpUrl(url).queryParam("name", projectName).build().toUri())
                .exchange()
                //.take(Duration.ofMillis(millsecond))
                .timeout(Duration.ofMillis(millsecond))
                .flatMap(item -> item.bodyToMono(AnswerDto.class))
                .flatMap(item -> Mono.just(item.getItems()))
                .flatMapMany(Flux::fromIterable);
    }


}
