package ru.seuslab.service.service2.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.seuslab.service.service2.entity.Detail;

import java.util.List;

public interface DetailDao extends JpaRepository<Detail, Long> {

    List<Detail> findByRedisIdIn(List<Long> redisIds);

    @Query("select d from Detail d where d.redisId in :redisIds")
    List<Detail> getByRedisIdIn(@Param("redisIds") List<Long> redisIds);
}
