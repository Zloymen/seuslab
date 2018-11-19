package ru.seuslab.service.fluxservice2.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Detail extends BaseEntity {

    @Column(name = "redis_id")
    private Long redisId;

    @OneToMany(mappedBy = "detail")
    private List<SubDetail> subDetails;
}
