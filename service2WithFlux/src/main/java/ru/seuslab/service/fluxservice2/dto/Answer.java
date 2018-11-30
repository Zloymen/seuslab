package ru.seuslab.service.fluxservice2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.seuslab.service.fluxservice2.entity.Detail;

import java.util.List;

@Data
@AllArgsConstructor
public class Answer {

    private List<Detail> items;
}
