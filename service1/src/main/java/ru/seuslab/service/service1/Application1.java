package ru.seuslab.service.service1;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import ru.seuslab.service.service1.dto.DetailDto;
import ru.seuslab.service.service1.dto.InputDataDto;
import ru.seuslab.service.service1.dto.SubDetailDto;
import ru.seuslab.service.service1.service.RestService;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@EnableAsync
@SpringBootApplication
@Slf4j
public class Application1 {

    @Autowired
    private RestService restService;

    @Value("${pathData}")
    private String path;

    @Bean
    RoutesBuilder myRouter() {
        return new RouteBuilder() {
            @Override
            public void configure()  {
                from("file:" + path + "/inbox?noop=true")
                    .log("test")

                    .process(exchange -> {

                        Message message = exchange.getIn();
                        File fileToProcess = message.getBody(File.class);


                        try(FileInputStream fin = new FileInputStream(fileToProcess);
                            Reader reader = new InputStreamReader(fin)){

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
                from("direct:record")
                        .log("Processing done ${body}")

                        .process(exchange -> {
                        Message message = exchange.getIn();

                        InputDataDto record = message.getBody(InputDataDto.class);

                        log.info("run {}", record.getName());

                        List<DetailDto> detailDtos = restService.getOldAsyncData(record.getName())
                                    .get(record.getTimeout(), TimeUnit.MILLISECONDS).getBody().getItems();

                        exchange.getIn().setBody(detailDtos);
                        exchange.getIn().setHeader("project_name", record.getName());

                    })
                        .log("Processing done ${body}")
                        .split(simple("${body}"))
                        .parallelProcessing(true)
                        .to("direct:create_file");

                from("direct:create_file")
                        .process(exchange -> {
                            Message message = exchange.getIn();
                            String projectName = exchange.getIn().getHeader("project_name", String.class);
                            DetailDto record = message.getBody(DetailDto.class);



                            try( OutputStream fin = new ByteArrayOutputStream()){
                                try(Writer writer = new OutputStreamWriter(fin)) {

                                    StatefulBeanToCsv<SubDetailDto> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                                            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                                            .build();

                                    beanToCsv.write(record.getSubDetails());
                                }

                                exchange.getOut().setBody( fin.toString());
                                exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
                                exchange.getOut().setHeader("CamelFileName",
                                        String.format("%s_%s_%s.csv",
                                                StringUtils.deleteWhitespace(projectName),
                                                StringUtils.deleteWhitespace(record.getName()),
                                                record.getId().toString() )
                                );
                            }
                        })
                        .to("file:" + path + "/outbox");
            }
        };
    }


    //todo  replace Webclient
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean
    RestTemplate restTemplate(){

        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    @Bean
    public Executor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application1.class, args);
    }
}
