package ru.seuslab.service.service1.dto;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class BaseDto {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private String name;

}
