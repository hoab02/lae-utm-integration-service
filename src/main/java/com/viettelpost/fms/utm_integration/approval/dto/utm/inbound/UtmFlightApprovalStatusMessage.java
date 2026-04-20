package com.viettelpost.fms.utm_integration.approval.dto.utm.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UtmFlightApprovalStatusMessage {

    @JsonProperty("EventType")
    private String eventType;

    @JsonProperty("ObjectID")
    private String objectId;

    @JsonProperty("ObjectName")
    private String objectName;

    @JsonProperty("EventStatus")
    private String eventStatus;

    @JsonProperty("CreatedAt")
    private String createdAt;

    @JsonProperty("UpdatedAt")
    private String updatedAt;

    @JsonProperty("CreatedBy")
    private Object createdBy;

    @JsonProperty("UpdatedBy")
    private Object updatedBy;
}