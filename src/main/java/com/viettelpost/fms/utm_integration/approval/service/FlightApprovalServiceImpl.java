package com.viettelpost.fms.utm_integration.approval.service;

import com.viettelpost.fms.utm_integration.approval.client.UtmFlightApprovalClient;
import com.viettelpost.fms.utm_integration.approval.domain.ApprovalStatus;
import com.viettelpost.fms.utm_integration.approval.domain.FlightApprovalEntity;
import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalStatusResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.FlightApprovalSubmitResponse;
import com.viettelpost.fms.utm_integration.approval.dto.api.UtmFlightApprovalRequest;
import com.viettelpost.fms.utm_integration.approval.dto.kafka.FlightApprovalStatusEvent;
import com.viettelpost.fms.utm_integration.approval.dto.utm.inbound.UtmFlightApprovalStatusMessage;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitRequest;
import com.viettelpost.fms.utm_integration.approval.dto.utm.outbound.UtmFlightApprovalSubmitResponse;
import com.viettelpost.fms.utm_integration.approval.kafka.FlightApprovalStatusKafkaPublisher;
import com.viettelpost.fms.utm_integration.approval.mapper.FlightApprovalUtmMapper;
import com.viettelpost.fms.utm_integration.approval.repository.FlightApprovalRepository;
import com.viettelpost.fms.utm_integration.approval.support.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightApprovalServiceImpl implements FlightApprovalService {

    private final FlightApprovalRepository repository;
    private final FlightApprovalUtmMapper mapper;
    private final ApprovalTokenProvider approvalTokenProvider;
    private final UtmFlightApprovalClient utmFlightApprovalClient;
    private final FlightApprovalStatusKafkaPublisher kafkaPublisher;
    private final JsonUtils jsonUtils;

    @Override
    @Transactional
    public FlightApprovalSubmitResponse submit(UtmFlightApprovalRequest request) {
        String planId = extractPlanId(request);

        if (repository.existsByPlanId(planId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Flight approval already exists for planId=" + planId
            );
        }

        UtmFlightApprovalSubmitRequest utmRequest = mapper.toUtmSubmitRequest(request);
        String authorizationHeaderValue = approvalTokenProvider.getAuthorizationHeaderValue();

        UtmFlightApprovalSubmitResponse utmResponse =
                utmFlightApprovalClient.submit(utmRequest, authorizationHeaderValue);

        Instant now = Instant.now();

        FlightApprovalEntity entity = FlightApprovalEntity.builder()
                .id(UUID.randomUUID().toString())
                .planId(planId)
                .missionId(null)
                .utmApplicationId(utmResponse != null ? utmResponse.getId() : null)
                .utmRequestId(utmResponse != null ? utmResponse.getRequestId() : null)
                .status(ApprovalStatus.PENDING)
                .submittedAt(now)
                .utmRequestPayload(jsonUtils.toJson(utmRequest))
                .build();

        repository.save(entity);
        kafkaPublisher.publish(toKafkaEvent(entity));

        log.info("approval_submit_success planId={} utmApplicationId={} utmRequestId={}",
                entity.getPlanId(), entity.getUtmApplicationId(), entity.getUtmRequestId());

        return FlightApprovalSubmitResponse.builder()
                .id(entity.getId())
                .planId(entity.getPlanId())
                .missionId(entity.getMissionId())
                .utmApplicationId(entity.getUtmApplicationId())
                .utmRequestId(entity.getUtmRequestId())
                .status(entity.getStatus())
                .submittedAt(entity.getSubmittedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FlightApprovalStatusResponse getByPlanId(String planId) {
        FlightApprovalEntity entity = repository.findByPlanId(planId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Flight approval not found for planId=" + planId
                ));

        return toStatusResponse(entity);
    }

    @Override
    @Transactional
    public void handleUtmStatus(UtmFlightApprovalStatusMessage message) {
        FlightApprovalEntity entity = findTargetEntity(message)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Flight approval not found for inbound status message"
                ));

        ApprovalStatus newStatus = mapInboundStatus(message.getEventStatus());
        validateTransition(entity.getStatus(), newStatus);

        entity.setUtmLastStatusPayload(jsonUtils.toJson(message));

        if (message.getObjectId() != null && entity.getUtmApplicationId() == null) {
            entity.setUtmApplicationId(message.getObjectId());
        }

        applyStatus(entity, newStatus, null);
        repository.save(entity);
        kafkaPublisher.publish(toKafkaEvent(entity));

        log.info("approval_status_updated planId={} status={} utmApplicationId={} utmRequestId={}",
                entity.getPlanId(), entity.getStatus(), entity.getUtmApplicationId(), entity.getUtmRequestId());
    }

    private String extractPlanId(UtmFlightApprovalRequest request) {
        if (request == null
                || request.getApplication() == null
                || request.getApplication().getId() == null
                || request.getApplication().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "application.id is required");
        }
        return request.getApplication().getId();
    }

    private Optional<FlightApprovalEntity> findTargetEntity(UtmFlightApprovalStatusMessage message) {
        if (message.getObjectId() != null && !message.getObjectId().isBlank()) {
            Optional<FlightApprovalEntity> byApplicationId =
                    repository.findByUtmApplicationId(message.getObjectId());
            if (byApplicationId.isPresent()) {
                return byApplicationId;
            }
        }
        return Optional.empty();
    }

    private ApprovalStatus mapInboundStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inbound status is blank");
        }

        String normalized = rawStatus.trim().toUpperCase();

        return switch (normalized) {
            case "SUBMITTED", "PENDING" -> ApprovalStatus.PENDING;
            case "APPROVED", "ACCEPTED" -> ApprovalStatus.APPROVED;
            case "REJECTED", "DENIED" -> ApprovalStatus.REJECTED;
            case "CANCELLED", "CANCELED" -> ApprovalStatus.CANCELLED;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported inbound status: " + rawStatus
            );
        };
    }

    private void validateTransition(ApprovalStatus current, ApprovalStatus target) {
        if (current == ApprovalStatus.PENDING
                && (target == ApprovalStatus.PENDING
                || target == ApprovalStatus.APPROVED
                || target == ApprovalStatus.REJECTED
                || target == ApprovalStatus.CANCELLED)) {
            return;
        }

        if ((current == ApprovalStatus.REJECTED || current == ApprovalStatus.CANCELLED)
                && target == ApprovalStatus.PENDING) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Invalid approval status transition from " + current + " to " + target
        );
    }

    private void applyStatus(FlightApprovalEntity entity, ApprovalStatus status, String reason) {
        entity.setStatus(status);

        if (status == ApprovalStatus.PENDING) {
            if (entity.getSubmittedAt() == null) {
                entity.setSubmittedAt(Instant.now());
            }
            entity.setRejectReason(null);
            return;
        }

        if (status == ApprovalStatus.APPROVED) {
            entity.setApprovedAt(Instant.now());
            entity.setRejectedAt(null);
            entity.setCancelledAt(null);
            entity.setRejectReason(null);
            return;
        }

        if (status == ApprovalStatus.REJECTED) {
            entity.setRejectedAt(Instant.now());
            entity.setApprovedAt(null);
            entity.setCancelledAt(null);
            entity.setRejectReason(reason);
            return;
        }

        if (status == ApprovalStatus.CANCELLED) {
            entity.setCancelledAt(Instant.now());
            entity.setApprovedAt(null);
            entity.setRejectedAt(null);
            entity.setRejectReason(reason);
        }
    }

    private FlightApprovalStatusResponse toStatusResponse(FlightApprovalEntity entity) {
        return FlightApprovalStatusResponse.builder()
                .id(entity.getId())
                .planId(entity.getPlanId())
                .missionId(entity.getMissionId())
                .utmApplicationId(entity.getUtmApplicationId())
                .utmRequestId(entity.getUtmRequestId())
                .status(entity.getStatus())
                .rejectReason(entity.getRejectReason())
                .submittedAt(entity.getSubmittedAt())
                .approvedAt(entity.getApprovedAt())
                .rejectedAt(entity.getRejectedAt())
                .cancelledAt(entity.getCancelledAt())
                .build();
    }

    private FlightApprovalStatusEvent toKafkaEvent(FlightApprovalEntity entity) {
        return FlightApprovalStatusEvent.builder()
                .flightTripCode(entity.getPlanId())
                .missionId(entity.getMissionId())
                .utmApplicationId(entity.getUtmApplicationId())
                .utmRequestId(entity.getUtmRequestId())
                .status(entity.getStatus())
                .reason(entity.getRejectReason())
                .submittedAt(entity.getSubmittedAt())
                .approvedAt(entity.getApprovedAt())
                .rejectedAt(entity.getRejectedAt())
                .cancelledAt(entity.getCancelledAt())
                .eventTime(Instant.now())
                .build();
    }
}