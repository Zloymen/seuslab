package ru.seuslab.service.fluxservice1;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Flux;
import ru.seuslab.service.fluxservice1.dto.DetailDto;
import ru.seuslab.service.fluxservice1.dto.InputDataDto;
import ru.seuslab.service.fluxservice1.dto.SubDetailDto;
import ru.seuslab.service.fluxservice1.service.RestService;

import java.io.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@EnableAsync
@SpringBootApplication
@Slf4j
public class Application1 {

    @Autowired
    private RestService restService;

    @Bean
    RoutesBuilder myRouter() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("file:data/inbox?noop=true")
                        .threads(10)
                        .log("test")

                        .process(exchange -> {

                            Message message = exchange.getIn();
                            File fileToProcess = message.getBody(File.class);


                            try (FileInputStream fin = new FileInputStream(fileToProcess);
                                 Reader reader = new InputStreamReader(fin)) {

                                CsvToBean<InputDataDto> csvToBean = new CsvToBeanBuilder(reader)
                                        .withType(InputDataDto.class)
                                        .withSkipLines(0)
                                        .build();

                                List<InputDataDto> list = csvToBean.parse();

                                exchange.getIn().setBody(list);
                            }
                        })
                        .split(simple("${body}"))
                        .parallelProcessing(true)
                        .to("direct:record");

                from("direct:record").log("Processing run ${body}").doTry()

                        .process(exchange -> {
                            Message message = exchange.getIn();

                            InputDataDto record = message.getBody(InputDataDto.class);

                            log.info("run {}", record.getName());

                            Flux<DetailDto> detailDtos = restService.getAsyncData(record.getName(), record.getTimeout());

                            exchange.getIn().setBody(detailDtos.toStream().collect(Collectors.toList()));
                            exchange.getIn().setHeader("project_name", record.getName());

                        })
                        .log("Processing done ${body}").to("direct:splite_response")
                        .doCatch(TimeoutException.class)
                        .log("Processing timeout ${body}").to("direct:record");

                from("direct:splite_response")
                        .log("Splite processing ${body}")
                        .split(simple("${body}"))
                        .parallelProcessing(true)
                        .to("direct:create_file");

                from("direct:create_file")
                        .log("create file ${body}")
                        .process(exchange -> {
                            Message message = exchange.getIn();
                            String projectName = exchange.getIn().getHeader("project_name", String.class);
                            DetailDto record = message.getBody(DetailDto.class);


                            try (OutputStream fin = new ByteArrayOutputStream()) {
                                try (Writer writer = new OutputStreamWriter(fin)) {

                                    StatefulBeanToCsv<SubDetailDto> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                                            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                                            .build();

                                    beanToCsv.write(record.getSubDetails());
                                }

                                exchange.getOut().setBody(fin.toString());
                                exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
                                exchange.getOut().setHeader("CamelFileName",
                                        String.format("%s_%s_%s.csv",
                                                StringUtils.deleteWhitespace(projectName),
                                                StringUtils.deleteWhitespace(record.getName()),
                                                record.getId().toString())
                                );
                            }
                        })
                        .to("file:data/outbox");
            }
        };
    }


    @Bean
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application1.class, args);
    }
}
