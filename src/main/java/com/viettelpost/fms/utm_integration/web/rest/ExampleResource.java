package com.viettelpost.fms.utm_integration.web.rest;

import com.viettelpost.fms.common.dto.PaginationDto;
import com.viettelpost.fms.common.exception.I18nException;
import com.viettelpost.fms.utm_integration.dto.ExampleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Example Resource")
@RequestMapping(ExampleResource.EXAMPLE_RESOURCE)
public interface ExampleResource {

    String EXAMPLE_RESOURCE = "/example";

    @Operation(summary = "Search examples")
    @GetMapping
    ResponseEntity<PaginationDto<ExampleDto>> search(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "code", required = false) String code
    );

    @Operation(summary = "Get example by id")
    @GetMapping("/{uuid}")
    ResponseEntity<ExampleDto> get(@PathVariable String uuid) throws I18nException;

    @Operation(summary = "Create example")
    @PostMapping
    ResponseEntity<String> create(@Valid @RequestBody ExampleDto request) throws I18nException;

    @Operation(summary = "Update example by id")
    @PutMapping("/{uuid}")
    ResponseEntity<String> update(@PathVariable String uuid, @Valid @RequestBody ExampleDto request) throws I18nException;

    @Operation(summary = "Delete example by id")
    @DeleteMapping("/{uuid}")
    ResponseEntity<Boolean> delete(@PathVariable String uuid) throws I18nException;
}
