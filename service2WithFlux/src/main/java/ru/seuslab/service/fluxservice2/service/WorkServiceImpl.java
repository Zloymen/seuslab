package ru.seuslab.service.fluxservice2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.seuslab.service.fluxservice2.dao.DetailDao;
import ru.seuslab.service.fluxservice2.entity.Detail;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    @Autowired
    private ReactiveRedisOperations<String, List<Long>> redisTemplate;
    private final DetailDao detailDao;

    private static final String KEY = "project";

    @Override
    public List<Detail> getDetailByProjectName(String name){

        Flux<Long> redisIds = redisTemplate.opsForValue().get(name).flatMapMany(Flux::fromIterable);
        return detailDao.findByRedisIdIn(redisIds.toStream().collect(Collectors.toList()));
    }

    @PostConstruct
    public void init(){
        //redisTemplate.opsForHash().
        redisTemplate.opsForHash().put(KEY,"test_project_1", Arrays.asList(1L, 2L, 3L, 4L, 5L));
        redisTemplate.opsForHash().put(KEY,"test_project_2", Arrays.asList(2L, 3L, 4L, 5L, 6L));
        redisTemplate.opsForHash().put(KEY,"test_project_3", Arrays.asList(3L, 4L, 5L, 6L, 7L));
        redisTemplate.opsForHash().put(KEY,"test_project_4", Arrays.asList(4L, 5L, 6L, 7L, 8L));
        redisTemplate.opsForHash().put(KEY,"test_project_5", Arrays.asList(5L, 6L, 7L, 8L, 9L));
    }
}
