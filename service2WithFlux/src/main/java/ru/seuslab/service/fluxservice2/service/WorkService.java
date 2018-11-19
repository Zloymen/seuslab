package ru.seuslab.service.fluxservice2.service;

import ru.seuslab.service.fluxservice2.entity.Detail;

import java.util.List;

public interface WorkService {

    List<Detail> getDetailByProjectName(String name);
}
