package com.viettelpost.fms.utm_integration.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExampleStatus {

    INIT("Init"),

    PROCESSING("Processing"),

    COMPLETED("Completed");

    private final String description;
}
