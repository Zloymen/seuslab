package ru.seuslab.service.fluxservice1.service;

import reactor.core.publisher.Flux;
import ru.seuslab.service.fluxservice1.dto.DetailDto;

public interface RestService {

    Flux<DetailDto> getAsyncData(String projectName, long millsecond);
}
