package com.viettelpost.fms.utm_integration.registry.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UtmRegistryResponseEnvelope<T> {
    private Integer code;
    private String message;
    private T data;
}