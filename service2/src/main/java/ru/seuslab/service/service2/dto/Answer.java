package ru.seuslab.service.service2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.seuslab.service.service2.entity.Detail;

import java.util.List;

@Data
@AllArgsConstructor
public class Answer {

    private List<Detail> items;
}
