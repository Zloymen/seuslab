package ru.seuslab.service.service2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "sub_detail")
public class SubDetail extends BaseEntity {


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="detail_id")
    private Detail detail;
}
