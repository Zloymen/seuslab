package ru.seuslab.service.service2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.seuslab.service.service2.dao.DetailDao;
import ru.seuslab.service.service2.entity.Detail;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DetailDao detailDao;

    private static final String KEY = "project";

    @Override
    public List<Detail> getDetailByProjectName(String name){

        List<Long> redisIds = (List<Long>) redisTemplate.opsForHash().get(KEY, name);
        return detailDao.findByRedisIdIn(redisIds);
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
