package ru.seuslab.service.service1.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import ru.seuslab.service.service1.dto.AnswerDto;
import ru.seuslab.service.service1.dto.DetailDto;
import ru.seuslab.service.service1.dto.InputDataDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface RestService {

    AnswerDto getData(String projectName);

    List<DetailDto> getDataWithService2(InputDataDto data) throws InterruptedException, ExecutionException, TimeoutException;

    CompletableFuture<AnswerDto> getAsyncData(String projectName);

    ListenableFuture<ResponseEntity<AnswerDto>> getOldAsyncData(String projectName);
}
