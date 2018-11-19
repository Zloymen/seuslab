package ru.seuslab.service.fluxservice1.service;

import reactor.core.publisher.Flux;
import ru.seuslab.service.fluxservice1.dto.AnswerDto;
import ru.seuslab.service.fluxservice1.dto.DetailDto;
import ru.seuslab.service.fluxservice1.dto.InputDataDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface RestService {

    AnswerDto getData(String projectName);

    List<DetailDto> getDataWithService2(InputDataDto data) throws InterruptedException, ExecutionException, TimeoutException;

    CompletableFuture<AnswerDto> getAsyncData(String projectName);

    Flux<DetailDto> getAsyncData(String projectName, long millsecond);
}
