package com.viettelpost.fms.utm_integration.mapper;

import com.viettelpost.fms.common.mapper.AbstractMapper;
import com.viettelpost.fms.utm_integration.domain.Example;
import com.viettelpost.fms.utm_integration.dto.ExampleDto;
import org.springframework.stereotype.Component;

@Component
public class ExampleMapper extends AbstractMapper<Example, ExampleDto> {
    @Override
    public Class<ExampleDto> getDtoClass() {
        return ExampleDto.class;
    }
}
