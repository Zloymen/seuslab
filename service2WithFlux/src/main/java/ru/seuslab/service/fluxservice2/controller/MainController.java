package ru.seuslab.service.fluxservice2.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.seuslab.service.fluxservice2.dto.Answer;
import ru.seuslab.service.fluxservice2.service.WorkService;

import javax.validation.constraints.NotEmpty;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
@Slf4j
public class MainController  {

    private final WorkService workService;

    private Random random = new Random(System.currentTimeMillis());

    @GetMapping(value = "/")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Answer approveConsultant(@RequestParam @NotEmpty String name) throws InterruptedException {

        int rnr = random.nextInt(15);
        log.info("sleep time:" + rnr);
        Thread.sleep(rnr * 1000L);

        return new Answer(workService.getDetailByProjectName(name));
    }
}
