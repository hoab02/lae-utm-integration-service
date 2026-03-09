package com.viettelpost.fms.utm_integration.web.rest;

import com.viettelpost.fms.common.dto.PaginationDto;
import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.dto.ExampleDto;
import com.viettelpost.fms.utm_integration.dto.ExampleSearchRequest;
import com.viettelpost.fms.utm_integration.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ExampleController implements ExampleResource {

    private final ExampleService exampleService;

    @Override
    public ResponseEntity<PaginationDto<ExampleDto>> search(Integer page,
                                                            Integer pageSize,
                                                            String name,
                                                            String code) {
        ExampleSearchRequest request = ExampleSearchRequest.builder()
                .name(name)
                .code(code)
                .build();
        return ResponseEntity.ok(exampleService.search(request, page, pageSize));
    }

    @Override
    public ResponseEntity<ExampleDto> get(String uuid) throws I18nException {
        return ResponseEntity.ok(exampleService.get(uuid));
    }

    @Override
    public ResponseEntity<String> create(ExampleDto request) throws I18nException {
        return ResponseEntity.ok(exampleService.create(request));
    }

    @Override
    public ResponseEntity<String> update(String uuid, ExampleDto request) throws I18nException {
        return ResponseEntity.ok(exampleService.update(uuid, request));
    }

    @Override
    public ResponseEntity<Boolean> delete(String uuid) throws I18nException {
        return ResponseEntity.ok(exampleService.delete(uuid));
    }
}
