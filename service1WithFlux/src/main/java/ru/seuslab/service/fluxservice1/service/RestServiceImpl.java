package ru.seuslab.service.fluxservice1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.seuslab.service.fluxservice1.dto.AnswerDto;
import ru.seuslab.service.fluxservice1.dto.DetailDto;
import ru.seuslab.service.fluxservice1.dto.InputDataDto;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestServiceImpl implements RestService {

    private final RestTemplate restTemplate;


    @Value("${service2.url}")
    private String url;

    private WebClient client;

    @PostConstruct
    private void post(){
        client = WebClient.create(url);
    }


    @Override
    public AnswerDto getData(String projectName){

        log.info("run async {}", projectName);
        UriComponentsBuilder builder = fromHttpUrl(url).queryParam("name", projectName);

        return restTemplate.getForObject(builder.toUriString(), AnswerDto.class);
    }

    @Override
    public Flux<DetailDto> getAsyncData(String projectName, long millsecond){
        return client.get().exchange().take(Duration.ofMillis(millsecond)).flatMap(item -> item.bodyToMono(AnswerDto.class))
                .flatMap(item -> Mono.just(item.getItems()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public CompletableFuture<AnswerDto> getAsyncData(String projectName) {
        return CompletableFuture.completedFuture(getData(projectName));
    }

    @Override
    public List<DetailDto> getDataWithService2(InputDataDto data) throws InterruptedException, ExecutionException, TimeoutException {
        return getAsyncData(data.getName()).get(data.getTimeout(), TimeUnit.MILLISECONDS).getItems();
    }

}
