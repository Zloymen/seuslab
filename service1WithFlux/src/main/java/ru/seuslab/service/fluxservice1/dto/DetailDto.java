package ru.seuslab.service.fluxservice1.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetailDto extends BaseDto {

    private Long redisId;

    private List<SubDetailDto> subDetails;
}
