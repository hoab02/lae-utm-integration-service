package com.viettelpost.fms.utm_integration.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.viettelpost.fms.common.dto.AuditorDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExampleDto extends AuditorDto {

    private String uuid;

    @NotBlank(message = "Name not blank")
    private String name;

    @NotBlank(message = "Code not blank")
    private String code;

    private String description;

    private Date date;
}
