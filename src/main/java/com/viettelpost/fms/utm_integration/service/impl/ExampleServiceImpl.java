package com.viettelpost.fms.utm_integration.service.impl;

import com.viettelpost.fms.common.dto.PaginationDto;
import com.viettelpost.fms.common.event.BaseEvent;
import com.viettelpost.fms.common.util.PaginationUtil;
import com.viettelpost.fms.utm_integration.domain.Example;
import com.viettelpost.fms.utm_integration.dto.ExampleDto;
import com.viettelpost.fms.utm_integration.dto.ExampleSearchRequest;
import com.viettelpost.fms.utm_integration.enumeration.ErrorCode;
import com.viettelpost.fms.utm_integration.exception.InternalException;
import com.viettelpost.fms.utm_integration.kafka.event.ExampleEvent;
import com.viettelpost.fms.utm_integration.mapper.ExampleMapper;
import com.viettelpost.fms.utm_integration.repository.ExampleRepository;
import com.viettelpost.fms.utm_integration.repository.predicate.ExampleQueryCriteria;
import com.viettelpost.fms.utm_integration.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    private final ExampleRepository exampleRepository;
    private final ExampleMapper exampleMapper;
    private final StreamBridge streamBridge;

    @Override
    public PaginationDto<ExampleDto> search(ExampleSearchRequest request,
                                            Integer page, Integer pageSize) {
        ExampleQueryCriteria predicate = new ExampleQueryCriteria()
                .withCode(request.getCode())
                .withName(request.getName());
        Page<Example> examplePage = exampleRepository.findAll(predicate.getPredicate(), PaginationUtil.init(page, pageSize));

        List<ExampleDto> ret = exampleMapper.toDtoList(examplePage.getContent());
        return new PaginationDto<>(ret, examplePage.getTotalElements(), examplePage.getTotalPages());
    }

    @Override
    public ExampleDto get(String uuid) throws InternalException {
        Optional<Example> exampleOpt = exampleRepository.findById(uuid);
        if (exampleOpt.isEmpty()) {
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }
        return exampleMapper.toDto(exampleOpt.get());
    }

    @Override
    @Transactional
    public String create(ExampleDto exampleRequest) throws InternalException {
        Example example = Example.builder()
                .name(exampleRequest.getName())
                .code(exampleRequest.getCode())
                .description(exampleRequest.getDescription())
                .build();
        example = exampleRepository.save(example);

        BaseEvent<ExampleEvent> event = new BaseEvent<>();
        event.setEventType("EXAMPLE_CREATE");
        event.setPayload(ExampleEvent.builder().itemTotal(5).build());
        streamBridge.send("exampleCreated-out-0", event);
        return example.getUuid();
    }

    @Override
    @Transactional
    public String update(String uuid, ExampleDto exampleDto) throws InternalException {
        Optional<Example> exampleOpt = exampleRepository.findById(uuid);
        if (exampleOpt.isEmpty()) {
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }

        Example example = exampleOpt.get();
        example.setName(exampleDto.getName());
        example.setCode(exampleDto.getCode());
        exampleRepository.save(example);
        return example.getUuid();
    }

    @Override
    @Transactional
    public boolean delete(String uuid) throws InternalException {
        Optional<Example> exampleOpt = exampleRepository.findById(uuid);
        if (exampleOpt.isEmpty()) {
            throw new InternalException(ErrorCode.ERROR_REQUEST_INVALID);
        }

        Example example = exampleOpt.get();
        exampleRepository.delete(example);
        return true;
    }
}
