package com.viettelpost.fms.utm_integration.service;

import com.viettelpost.fms.common.dto.PaginationDto;
import com.viettelpost.fms.utm_integration.dto.ExampleDto;
import com.viettelpost.fms.utm_integration.dto.ExampleSearchRequest;
import com.viettelpost.fms.utm_integration.exception.InternalException;

public interface ExampleService {

    PaginationDto<ExampleDto> search(ExampleSearchRequest request, Integer page, Integer pageSize);

    ExampleDto get(String uuid) throws InternalException;

    String create(ExampleDto example) throws InternalException;

    String update(String uuid, ExampleDto exampleDto) throws InternalException;

    boolean delete(String uuid) throws InternalException;
}
