package ru.seuslab.service.service1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.seuslab.service.service1.dto.AnswerDto;
import ru.seuslab.service.service1.dto.DetailDto;
import ru.seuslab.service.service1.dto.InputDataDto;

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

    private final AsyncRestTemplate asyncRestTemplate;


    @Value("${service2.url}")
    private String url;

    @Override
    public AnswerDto getData(String projectName){

        log.info("run async {} from {}", projectName, url);
        UriComponentsBuilder builder = fromHttpUrl(url).queryParam("name", projectName);

        return restTemplate.getForObject(builder.toUriString(), AnswerDto.class);
    }

    @Override
    public ListenableFuture<ResponseEntity<AnswerDto>> getOldAsyncData(String projectName){
        UriComponentsBuilder builder = fromHttpUrl(url).queryParam("name", projectName);
        log.debug(builder.toUriString());
        return asyncRestTemplate.getForEntity(builder.toUriString(), AnswerDto.class);
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
