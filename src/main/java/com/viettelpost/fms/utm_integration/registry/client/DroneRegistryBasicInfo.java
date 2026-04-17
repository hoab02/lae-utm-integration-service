package com.viettelpost.fms.utm_integration.registry.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DroneRegistryBasicInfo(
        String manufacturer,
        String model,
        @JsonProperty("serial_number") String serialNumber,
        @JsonProperty("registration_id") String registrationId,
        String type,
        String standard,
        @JsonProperty("factory_number") String factoryNumber,
        @JsonProperty("registration_issued_at") Long registrationIssuedAt,
        @JsonProperty("engine_type") String engineType,
        @JsonProperty("radio_work_info") String radioWorkInfo,
        @JsonProperty("control_method") String controlMethod,
        @JsonProperty("propose_usage") String proposeUsage
) {
}
