package com.viettelpost.fms.utm_integration.airspace.mqtt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CorridorMqttMessage {

    private String eventId;
    private String eventType;
    private String timestamp;

    @Builder.Default
    private Map<String, String> metadata = Map.of();

    private String createdBy;

    @Builder.Default
    private List<CorridorItem> payload = List.of();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CorridorItem {

        @JsonProperty("_id")
        private String corridorId;

        private String type;

        @Builder.Default
        private List<JsonNode> waypoint = List.of();
    }
}
