package ru.seuslab.service.service2.service;

import ru.seuslab.service.service2.entity.Detail;

import java.util.List;

public interface WorkService {

    List<Detail> getDetailByProjectName(String name);
}
